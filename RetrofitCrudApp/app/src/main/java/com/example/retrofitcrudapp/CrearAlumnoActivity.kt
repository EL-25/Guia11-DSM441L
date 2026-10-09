package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class CrearAlumnoActivity : AppCompatActivity() {
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var crearButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_alumno)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        edadEditText = findViewById(R.id.editTextEdad)
        crearButton = findViewById(R.id.btnGuardar)
        crearButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val edad = edadEditText.text.toString().toInt()
            val alumno = Alumno(0,nombre, apellido, edad)
            // Obtiene la instancia de Retrofit con el cliente OkHttpClient
            val api = RetrofitClient.instance
            api.crearAlumno(alumno).enqueue(object : Callback<Alumno> {
                override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearAlumnoActivity, "Alumno creado exitosamente", Toast.LENGTH_SHORT).show()
                        val i = Intent(getBaseContext(), MainActivity::class.java)
                        startActivity(i)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error crear alumno: $error")
                        Toast.makeText(this@CrearAlumnoActivity, "Error al crear el alumno", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Alumno>, t: Throwable) {
                    Toast.makeText(this@CrearAlumnoActivity, "Error al crear el alumno", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
