package com.example.gregsquizapp.OpenTDB;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenTDBResponse {
    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<OpenTDBQuestion> results;

    public int getResponseCode() {
        return responseCode;
    }

    public List<OpenTDBQuestion> getResults() {
        return results;
    }
}