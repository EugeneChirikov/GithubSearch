package ru.farpost.githubsearch.ui;

import java.util.ArrayList;
import java.util.List;

import ru.farpost.githubsearch.network.model.SearchResults;

/**
 * Created by eugene on 12/1/16.
 */

public class SearchResultsMapper {
    public static List<Repository> toRepositories(SearchResults searchResults) {
        if (searchResults == null || searchResults.items == null) {
            return new ArrayList<>(0);
        } else {
            List<Repository> repositories = new ArrayList<>(searchResults.items.size());
            for (SearchResults.Item item : searchResults.items) {
                Repository repository = new Repository();
                repository.fullName = item.fullName;
                repository.description = item.description;
                repository.ownerAvatarUrl = item.owner.avatarUrl;
                repositories.add(repository);
            }
            return repositories;
        }
    }
}
