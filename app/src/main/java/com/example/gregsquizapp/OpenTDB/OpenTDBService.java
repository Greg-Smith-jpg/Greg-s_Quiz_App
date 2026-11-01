package com.example.gregsquizapp.OpenTDB;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTDBService {
    // calls the OpenTDB api for 10 questions from the selected category with the selected difficulty
    @GET("api.php?amount=10")
    Call<OpenTDBResponse> getQuestions(
            @Query("category") int category,
            @Query("difficulty") String difficulty
    );
}