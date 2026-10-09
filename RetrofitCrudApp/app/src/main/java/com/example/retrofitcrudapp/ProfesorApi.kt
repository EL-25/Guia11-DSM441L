package com.example.retrofitcrudapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfesorApi {
    @GET("escuela/profesor")
    fun obtenerProfesores(): Call<List<Profesor>>

    @GET("escuela/profesor/{id}")
    fun obtenerProfesorPorId(@Path("id") id: Int): Call<Profesor>

    @POST("escuela/profesor")
    fun crearProfesor(@Body profesor: Profesor): Call<Profesor>

    @PUT("escuela/profesor/{id}")
    fun actualizarProfesor(@Path("id") id: Int, @Body profesor: Profesor): Call<Profesor>

    @DELETE("escuela/profesor/{id}")
    fun eliminarProfesor(@Path("id") id: Int): Call<Void>
}
