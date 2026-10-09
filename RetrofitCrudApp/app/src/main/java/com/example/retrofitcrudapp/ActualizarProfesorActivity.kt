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

class ActualizarProfesorActivity : AppCompatActivity() {

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilApellido: TextInputLayout
    private lateinit var tilEdad: TextInputLayout
    private lateinit var nombreEditText: TextInputEditText
    private lateinit var apellidoEditText: TextInputEditText
    private lateinit var edadEditText: TextInputEditText
    private lateinit var actualizarButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var profesorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizar_profesor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar: ImageButton = findViewById(R.id.btnRegresar)
        tilNombre = findViewById(R.id.tilNombre)
        tilApellido = findViewById(R.id.tilApellido)
        tilEdad = findViewById(R.id.tilEdad)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        edadEditText = findViewById(R.id.edadEditText)
        actualizarButton = findViewById(R.id.actualizarButton)
        progressBar = findViewById(R.id.progressBarActualizar)

        // Obtener datos del Intent
        profesorId = intent.getIntExtra("profesor_id", -1)
        val nombre = intent.getStringExtra("nombre").orEmpty()
        val apellido = intent.getStringExtra("apellido").orEmpty()
        val edad = intent.getIntExtra("edad", 0)

        nombreEditText.setText(nombre)
        apellidoEditText.setText(apellido)
        if (edad > 0) {
            edadEditText.setText(edad.toString())
        }

        btnRegresar.setOnClickListener {
            finish()
        }

        actualizarButton.setOnClickListener {
            actualizarProfesor()
        }
    }

    private fun actualizarProfesor() {
        val nombre = nombreEditText.text?.toString()?.trim().orEmpty()
        val apellido = apellidoEditText.text?.toString()?.trim().orEmpty()
        val edadStr = edadEditText.text?.toString()?.trim().orEmpty()

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
        val profesorActualizado = Profesor(
            id = profesorId,
            nombre = nombre,
            apellido = apellido,
            edad = edad
        )

        actualizarButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        val api = RetrofitClient.profesorApi
        api.actualizarProfesor(profesorId, profesorActualizado).enqueue(object : Callback<Profesor> {
            override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                actualizarButton.isEnabled = true
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ActualizarProfesorActivity,
                        "Profesor actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Regresa a la lista
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API_ACTUALIZAR_PROF", "Error: $error")
                    Toast.makeText(
                        this@ActualizarProfesorActivity,
                        "Error al actualizar el profesor (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Profesor>, t: Throwable) {
                actualizarButton.isEnabled = true
                progressBar.visibility = View.GONE
                Log.e("API_ACTUALIZAR_PROF", "Fallo: ${t.message}")
                Toast.makeText(
                    this@ActualizarProfesorActivity,
                    "Error de conexión con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
