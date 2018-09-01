package com.influx.fb.webservice

import com.influx.fb.model.Response.FnBResponse
import retrofit2.Call
import retrofit2.http.GET

interface APIInterface{

    @GET("5b700cff2e00005c009365cf")
    fun getFoodList() : Call<FnBResponse>
}