package com.example.gregsquizapp.OpenTDB;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// stores the response code and question list returned by the OpenTDB api
public class OpenTDBResponse {
    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<OpenTDBQuestion> results;

    // gets the response code from OpenTDB
    // 0 means success
    public int getResponseCode() {
        return responseCode;
    }

    // gets the list of questions returned by OpenTDB
    public List<OpenTDBQuestion> getResults() {
        return results;
    }
}