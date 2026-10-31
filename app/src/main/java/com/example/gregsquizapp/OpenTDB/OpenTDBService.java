package com.example.gregsquizapp.OpenTDB;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTDBService {
    @GET("api.php")
    Call<OpenTDBResponse> getQuestions(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("difficulty") String difficulty
    );
}