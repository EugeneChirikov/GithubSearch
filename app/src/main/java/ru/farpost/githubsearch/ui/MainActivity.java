package ru.farpost.githubsearch.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.farpost.githubsearch.R;
import ru.farpost.githubsearch.network.GithubApi;
import ru.farpost.githubsearch.network.GithubClient;
import ru.farpost.githubsearch.network.GithubLinkHeaderParser;
import ru.farpost.githubsearch.network.model.ForbiddenError;
import ru.farpost.githubsearch.network.model.SearchResults;
import ru.farpost.githubsearch.ui.util.BottomScrollIndicator;
import ru.farpost.githubsearch.ui.util.DefaultRecyclerViewArrayAdapter;

public class MainActivity extends AppCompatActivity implements BottomScrollIndicator.OnBottomReachedListener {
    static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_QUERY = "search_query";
    private static final String REPOSITORIES_LIST = "repositories_list";
    private static final String NEXT_PAGE_URL = "next_page_url";
    private GithubApi mGithubApi;
    private TextView mOverlayText;
    private RepositoriesAdapter mAdapter;
    private Call<SearchResults> mCallSearchRepositories;
    private String mNextPageUrl;
    private BottomScrollIndicator mBottomScrollIndicator;

    private SearchView mSearchView;
    private CharSequence mRestoredSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGithubApi = GithubClient.getApi();
        mOverlayText = (TextView) findViewById(R.id.overlayText);
        initRepositoriesList();
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    private void initRepositoriesList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.repositoriesRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RepositoriesAdapter(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new DefaultRecyclerViewArrayAdapter.OnItemClickListener<Repository>() {
            @Override
            public void onItemClicked(View v, Repository item) {
                Toast.makeText(MainActivity.this, item.fullName, Toast.LENGTH_SHORT).show();
            }
        });
        mBottomScrollIndicator = new BottomScrollIndicator(recyclerView, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelApiCall(mCallSearchRepositories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        tryRestoreSearchView(searchMenuItem);
        initSearchView();
        return true;
    }

    private void initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return onQueryTextChange(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                handleSearchQuery(query);
                return true;
            }
        });
    }

    private void handleSearchQuery(String query) {
        mNextPageUrl = null;
        cancelApiCall(mCallSearchRepositories);
        if (query.length() > 0) {
            requestSearchRepository(query);
        } else {
            showWelcome();
        }
    }

    private void tryRestoreSearchView(MenuItem searchMenuItem) {
        if (mRestoredSearchQuery != null) {
            searchMenuItem.expandActionView();
            mSearchView.setQuery(mRestoredSearchQuery, false);
            mSearchView.clearFocus();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveSearchViewState(savedInstanceState);
        saveRepositoriesList(savedInstanceState);
        saveNextPageUrl(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void saveSearchViewState(Bundle savedInstanceState) {
        if (mSearchView != null) {
            CharSequence query = mSearchView.getQuery();
            if (!TextUtils.isEmpty(query)) {
                savedInstanceState.putCharSequence(SEARCH_QUERY, query);
            }
        }
    }

    private void saveRepositoriesList(Bundle savedInstanceState) {
        List<Repository> repositories = mAdapter.getRepositories();
        savedInstanceState.putParcelableArrayList(REPOSITORIES_LIST,
                (ArrayList<? extends Parcelable>) repositories);
    }

    private void saveNextPageUrl(Bundle savedInstanceState) {
        savedInstanceState.putString(NEXT_PAGE_URL, mNextPageUrl);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        mRestoredSearchQuery = savedInstanceState.getCharSequence(SEARCH_QUERY);
        if (mRestoredSearchQuery != null) {
            List<Repository> repositories = savedInstanceState.getParcelableArrayList(REPOSITORIES_LIST);
            showRepositoriesListOrNoResults(repositories);
        }
        mNextPageUrl = savedInstanceState.getString(NEXT_PAGE_URL);
    }

    private void requestSearchRepository(String query) {
        mCallSearchRepositories = mGithubApi.getSearchRepositories(query);
        mCallSearchRepositories.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                int code = response.code();
                if (code == 200) {
                    cacheNextPageUrl(response.headers());
                    SearchResults searchResults = response.body();
                    showResults(searchResults);
                } else {
                    showNoResults();
                    if (code == 403) {
                        tryShowForbiddenError(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                if (!call.isCanceled()) {
                    showNoResults();
                    showRetrofitError(t);
                }
            }
        });
    }

    private void cacheNextPageUrl(Headers headers) {
        GithubLinkHeaderParser linkHeaderParser = new GithubLinkHeaderParser(headers);
        mNextPageUrl = linkHeaderParser.getNext();
    }

    private void requestSearchRepositoryNext(final String nextUrl) {
        mCallSearchRepositories = mGithubApi.getSearchRepositoriesWithDynamicUrl(nextUrl);
        mCallSearchRepositories.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                int code = response.code();
                if (code == 200) {
                    cacheNextPageUrl(response.headers());
                    SearchResults searchResults = response.body();
                    addResults(searchResults);
                } else {
                    mBottomScrollIndicator.reset();
                    if (code == 403) {
                        tryShowForbiddenError(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                showRetrofitError(t);
                mBottomScrollIndicator.reset();
            }
        });
    }

    private void tryShowForbiddenError(Response response) {
        try {
            showForbiddenError(response.errorBody().string());
        } catch (IOException ignored) {}
    }

    private void showForbiddenError(String errorBodyJson) {
        Gson gson = new GsonBuilder().create();
        ForbiddenError forbiddenError =  gson.fromJson(errorBodyJson, ForbiddenError.class);
        if (forbiddenError != null && forbiddenError.message != null) {
            showErrorMessage(forbiddenError.message);
        }
    }

    private void showRetrofitError(Throwable retrofitThrowable) {
        String message = retrofitThrowable.getMessage();
        showErrorMessage(message != null ? message : getString(R.string.network_error));
    }

    private void showErrorMessage(String message) {
        View parentView = findViewById(R.id.activity_main);
        Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show();
    }

    private void cancelApiCall(Call call) {
        if (call != null) {
            call.cancel();
        }
    }

    private void showResults(SearchResults searchResults) {
        List<Repository> repositories = SearchResultsMapper.toRepositories(searchResults);
        showRepositoriesListOrNoResults(repositories);
    }

    private void showRepositoriesListOrNoResults(List<Repository> repositories) {
        if (repositories.size() > 0) {
            mAdapter.setItems(repositories);
            mBottomScrollIndicator.reset();
            hideTextOverlay();
        } else {
            showNoResults();
        }
    }

    private void hideTextOverlay() {
        mOverlayText.setVisibility(View.INVISIBLE);
    }

    private void addResults(SearchResults searchResults) {
        List<Repository> repositories = SearchResultsMapper.toRepositories(searchResults);
        mAdapter.addItems(repositories);
        mBottomScrollIndicator.reset();
        hideTextOverlay();
    }

    private void showWelcome() {
        mOverlayText.setText(R.string.search_welcome);
        showOverlayText();
    }

    private void showNoResults() {
        mOverlayText.setText(R.string.search_no_results);
        showOverlayText();
    }

    private void showOverlayText() {
        mOverlayText.setVisibility(View.VISIBLE);
        mAdapter.setItems(null);
        mBottomScrollIndicator.reset();
    }

    @Override
    public void onBottomReached() {
        if (mNextPageUrl != null) {
            requestSearchRepositoryNext(mNextPageUrl);
        }
    }
}
