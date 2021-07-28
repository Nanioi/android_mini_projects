package com.nanioi.airbnbapplication

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {

    @GET("/v3/f5cb89b4-ee01-49dd-be4b-677ec92fcd01")
    fun getHouseList(): Call<HouseDto>
}