package com.nanioi.youtube.service

import com.nanioi.youtube.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/7c7a9c65-17eb-4cc9-bd79-71d05a63a245")
    fun listVideos():Call<VideoDto>
}