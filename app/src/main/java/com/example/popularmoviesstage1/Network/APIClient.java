package com.example.popularmoviesstage1.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//class to help build url
public class APIClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
