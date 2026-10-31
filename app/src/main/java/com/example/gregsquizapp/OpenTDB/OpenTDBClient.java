package com.example.gregsquizapp.OpenTDB;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenTDBClient {
    private static final String BASE_URL = "https://opentdb.com/";
    private static Retrofit retrofit;
    private static OpenTDBService service;

    public static OpenTDBService getService() {
        if (service == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(OpenTDBService.class);
        }

        return service;
    }
}