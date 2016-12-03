package ru.farpost.githubsearch.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by eugene on 12/1/16.
 */

public class SearchResults {
    @SerializedName("total_count")
    public Integer totalCount;

    @SerializedName("incomplete_results")
    public Boolean incompleteResults;

    public static class Item {
        public static class Owner {
            public String login;
            public Integer id;

            @SerializedName("avatar_url")
            public String avatarUrl;

            public String gravatarId;
            public String url;

            @SerializedName("received_events_url")
            public String receivedEventsUrl;

            public String type;
        }

        public Integer id;
        public String name;

        @SerializedName("full_name")
        public String fullName;

        public Owner owner;

        @SerializedName("private")
        public Boolean isPrivate;

        @SerializedName("html_url")
        public String htmlUrl;

        public String description;
        public Boolean fork;
        public String url;

        @SerializedName("created_at")
        public String createdAt;

        @SerializedName("updated_at")
        public String updatedAt;

        @SerializedName("pushed_at")
        public String pushedAt;

        public String homepage;
        public Integer size;

        @SerializedName("stargazers_count")
        public Integer stargazersCount;

        @SerializedName("watchers_count")
        public Integer watchersCount;

        @SerializedName("language")
        public String languageName;

        @SerializedName("forks_count")
        public Integer forksCount;

        @SerializedName("open_issues_count")
        public Integer openIssuesCount;

        @SerializedName("master_branch")
        public String masterBranch;

        @SerializedName("default_branch")
        public String defaultBranch;

        public Float score;
    }

    public List<Item> items;
}
