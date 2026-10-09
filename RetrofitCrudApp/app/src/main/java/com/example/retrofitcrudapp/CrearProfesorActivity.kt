package com.example.retrofitcrudapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearProfesorActivity : AppCompatActivity() {

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilApellido: TextInputLayout
    private lateinit var tilEdad: TextInputLayout
    private lateinit var editTextNombre: TextInputEditText
    private lateinit var editTextApellido: TextInputEditText
    private lateinit var editTextEdad: TextInputEditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_profesor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar: ImageButton = findViewById(R.id.btnRegresar)
        tilNombre = findViewById(R.id.tilNombre)
        tilApellido = findViewById(R.id.tilApellido)
        tilEdad = findViewById(R.id.tilEdad)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextEdad = findViewById(R.id.editTextEdad)
        btnGuardar = findViewById(R.id.btnGuardar)
        progressBar = findViewById(R.id.progressBarGuardar)

        btnRegresar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarProfesor()
        }
    }

    private fun guardarProfesor() {
        val nombre = editTextNombre.text?.toString()?.trim().orEmpty()
        val apellido = editTextApellido.text?.toString()?.trim().orEmpty()
        val edadStr = editTextEdad.text?.toString()?.trim().orEmpty()

        // Limpiar errores previos
        tilNombre.error = null
        tilApellido.error = null
        tilEdad.error = null

        var esValido = true

        if (nombre.isEmpty()) {
            tilNombre.error = "Por favor ingresa el nombre"
            esValido = false
        }

        if (apellido.isEmpty()) {
            tilApellido.error = "Por favor ingresa el apellido"
            esValido = false
        }

        if (edadStr.isEmpty()) {
            tilEdad.error = "Por favor ingresa la edad"
            esValido = false
        } else {
            val edad = edadStr.toIntOrNull()
            if (edad == null || edad < 18 || edad > 100) {
                tilEdad.error = "La edad debe ser un número entre 18 y 100"
                esValido = false
            }
        }

        if (!esValido) {
            return
        }

        val edad = edadStr.toInt()
        val nuevoProfesor = Profesor(
            id = 0,
            nombre = nombre,
            apellido = apellido,
            edad = edad
        )

        // Deshabilitar botón y mostrar carga
        btnGuardar.isEnabled = false
        progressBar.visibility = View.VISIBLE

        val api = RetrofitClient.profesorApi
        api.crearProfesor(nuevoProfesor).enqueue(object : Callback<Profesor> {
            override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                btnGuardar.isEnabled = true
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CrearProfesorActivity,
                        "Profesor registrado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Regresa a la lista de profesores
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API_CREAR_PROFESOR", "Error: $error")
                    Toast.makeText(
                        this@CrearProfesorActivity,
                        "Error al registrar el profesor (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Profesor>, t: Throwable) {
                btnGuardar.isEnabled = true
                progressBar.visibility = View.GONE
                Log.e("API_CREAR_PROFESOR", "Fallo: ${t.message}")
                Toast.makeText(
                    this@CrearProfesorActivity,
                    "Error de conexión con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
