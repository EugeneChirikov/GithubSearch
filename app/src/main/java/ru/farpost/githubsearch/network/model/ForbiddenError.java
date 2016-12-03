package ru.farpost.githubsearch.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eugene on 12/3/16.
 */

public class ForbiddenError {

    public String message;

    @SerializedName("documentation_url")
    public String documentationUrl;
}
