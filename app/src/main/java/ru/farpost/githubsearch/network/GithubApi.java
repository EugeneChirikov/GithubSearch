package ru.farpost.githubsearch.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import ru.farpost.githubsearch.network.model.SearchResults;

/**
 * Created by eugene on 12/1/16.
 */

public interface GithubApi {

    @GET("search/repositories")
    Call<SearchResults> getSearchRepositories(@Query("q") String query);

    @GET
    Call<SearchResults> getSearchRepositoriesWithDynamicUrl(@Url String url);
}
