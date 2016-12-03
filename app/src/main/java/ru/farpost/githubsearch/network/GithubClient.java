package ru.farpost.githubsearch.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eugene on 12/1/16.
 */

public class GithubClient {
    private static final String BASE_URL = "https://api.github.com/";
    private static GithubApi mGithubApi;

    private static void initApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client =  new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGithubApi = retrofit.create(GithubApi.class);
    }

    public static GithubApi getApi() {
        if (mGithubApi == null) {
            initApi();
        }
        return mGithubApi;
    }
}
