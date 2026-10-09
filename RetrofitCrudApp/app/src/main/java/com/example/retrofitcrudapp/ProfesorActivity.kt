package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfesorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfesorAdapter
    private lateinit var api: ProfesorApi
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var tvCantidadProfesores: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar: ImageButton = findViewById(R.id.btnRegresar)
        val fabAgregar: ExtendedFloatingActionButton = findViewById(R.id.fab_agregar_profesor)
        recyclerView = findViewById(R.id.recyclerViewProfesores)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvCantidadProfesores = findViewById(R.id.tvCantidadProfesores)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Instancia de ProfesorApi desde RetrofitClient
        api = RetrofitClient.profesorApi

        btnRegresar.setOnClickListener {
            finish()
        }

        fabAgregar.setOnClickListener {
            val intent = Intent(this, CrearProfesorActivity::class.java)
            startActivity(intent)
        }

        cargarDatos()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE

        val call = api.obtenerProfesores()
        call.enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(
                call: Call<List<Profesor>>,
                response: Response<List<Profesor>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val profesores = response.body() ?: emptyList()
                    tvCantidadProfesores.text = "${profesores.size} registrados"

                    if (profesores.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        recyclerView.adapter = null
                    } else {
                        tvEmptyState.visibility = View.GONE
                        adapter = ProfesorAdapter(profesores)
                        recyclerView.adapter = adapter

                        adapter.setOnItemClickListener(object : ProfesorAdapter.OnItemClickListener {
                            override fun onItemClick(profesor: Profesor) {
                                mostrarOpcionesDialog(profesor)
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API_PROFESOR", "Error al obtener profesores: $error")
                    Toast.makeText(
                        this@ProfesorActivity,
                        "Error al cargar profesores (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("API_PROFESOR", "Fallo al conectar: ${t.message}")
                Toast.makeText(
                    this@ProfesorActivity,
                    "Error de conexión con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun mostrarOpcionesDialog(profesor: Profesor) {
        val opciones = arrayOf("Modificar Profesor", "Eliminar Profesor")
        AlertDialog.Builder(this)
            .setTitle("${profesor.nombre} ${profesor.apellido}")
            .setItems(opciones) { _, index ->
                when (index) {
                    0 -> modificarProfesor(profesor)
                    1 -> confirmarEliminarProfesor(profesor)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun modificarProfesor(profesor: Profesor) {
        val intent = Intent(this, ActualizarProfesorActivity::class.java).apply {
            putExtra("profesor_id", profesor.id)
            putExtra("nombre", profesor.nombre)
            putExtra("apellido", profesor.apellido)
            putExtra("edad", profesor.edad)
        }
        startActivity(intent)
    }

    private fun confirmarEliminarProfesor(profesor: Profesor) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar al profesor ${profesor.nombre} ${profesor.apellido}?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProfesor(profesor)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProfesor(profesor: Profesor) {
        progressBar.visibility = View.VISIBLE
        val llamada = api.eliminarProfesor(profesor.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ProfesorActivity,
                        "Profesor eliminado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarDatos()
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API_PROFESOR", "Error al eliminar profesor: $error")
                    Toast.makeText(
                        this@ProfesorActivity,
                        "No se pudo eliminar el profesor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("API_PROFESOR", "Fallo al eliminar: ${t.message}")
                Toast.makeText(
                    this@ProfesorActivity,
                    "Error de conexión al eliminar profesor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
