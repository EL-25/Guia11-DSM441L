package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlumnoAdapter
    private lateinit var api: AlumnoApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fab_agregar: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_agregar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Obtiene la instancia de Retrofit con el cliente OkHttpClient
        api = RetrofitClient.instance
        cargarDatos(api)
        // Cuando el usuario quiere agregar un nuevo registro
        fab_agregar.setOnClickListener(View.OnClickListener {
            val i = Intent(getBaseContext(), CrearAlumnoActivity::class.java)
            startActivity(i)
        })
    }
    override fun onResume() {
        super.onResume()
        cargarDatos(api)
    }
    private fun cargarDatos(api: AlumnoApi) {
        val call = api.obtenerAlumnos()
        call.enqueue(object : Callback<List<Alumno>> {
            override fun onResponse(call: Call<List<Alumno>>, response: Response<List<Alumno>>) {
                if (response.isSuccessful) {
                    val alumnos = response.body()
                    if (alumnos != null) {
                        adapter = AlumnoAdapter(alumnos)
                        recyclerView.adapter = adapter
                        // Establecemos el escuchador de clics en el adaptador
                        adapter.setOnItemClickListener(object : AlumnoAdapter.OnItemClickListener {
                            override fun onItemClick(alumno: Alumno) {
                                val opciones = arrayOf("Modificar Alumno", "Eliminar Alumno")
                                AlertDialog.Builder(this@MainActivity)
                                    .setTitle(alumno.nombre)
                                    .setItems(opciones) { dialog, index ->
                                        when (index) {
                                            0 -> Modificar(alumno)
                                            1 -> eliminarAlumno(alumno, api)
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener los alumnos: $error")
                    Toast.makeText(
                        this@MainActivity,
                        "Error al obtener los alumnos 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<List<Alumno>>, t: Throwable) {
                Log.e("API", "Error al obtener los alumnos: ${t.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Error al obtener los alumnos 2",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun Modificar(alumno: Alumno) {
        // Creamos un intent para ir a la actividad de actualización de alumnos
        val i = Intent(getBaseContext(), ActualizarAlumnoActivity::class.java)
        // Pasamos el ID del alumno seleccionado a la actividad de actualización
        i.putExtra("alumno_id", alumno.id)
        i.putExtra("nombre", alumno.nombre)
        i.putExtra("apellido", alumno.apellido)
        i.putExtra("edad", alumno.edad)
        // Iniciamos la actividad de actualización de alumnos
        startActivity(i)
    }
    private fun eliminarAlumno(alumno: Alumno, api: AlumnoApi) {
        Log.e("API", "id : $alumno")
        val llamada = api.eliminarAlumno(alumno.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Alumno eliminado",
                        Toast.LENGTH_SHORT).show()
                    cargarDatos(api)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar alumno : $error")
                    Toast.makeText(this@MainActivity, "Error al eliminar alumno 1",
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar alumno : $t")
                Toast.makeText(this@MainActivity, "Error al eliminar alumno 2",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}
