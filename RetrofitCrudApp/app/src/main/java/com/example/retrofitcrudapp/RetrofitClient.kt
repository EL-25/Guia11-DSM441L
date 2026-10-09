package com.example.retrofitcrudapp

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://66240d4504457d4aaf9b8530.mockapi.io/api/"

    // Deserializador seguro para números enteros (convierte cadenas como "edad 1" o "30" a enteros válidos sin fallar)
    private val safeIntDeserializer = JsonDeserializer { json, _, _ ->
        try {
            json.asInt
        } catch (e: Exception) {
            val digits = json.asString.filter { it.isDigit() }
            digits.toIntOrNull() ?: 0
        }
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(Int::class.java, safeIntDeserializer)
        .registerTypeAdapter(java.lang.Integer::class.java, safeIntDeserializer)
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val instance: AlumnoApi by lazy {
        retrofit.create(AlumnoApi::class.java)
    }

    val profesorApi: ProfesorApi by lazy {
        retrofit.create(ProfesorApi::class.java)
    }
}
