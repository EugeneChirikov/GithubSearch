package ru.farpost.githubsearch.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eugene on 12/1/16.
 */

public class Repository implements Parcelable {
    public String ownerAvatarUrl;
    public String fullName;
    public String description;

    public Repository() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(ownerAvatarUrl);
        parcel.writeString(fullName);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<Repository> CREATOR = new Parcelable.Creator<Repository>() {
        public Repository createFromParcel(Parcel in) {
            return new Repository(in);
        }

        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };

    private Repository(Parcel parcel) {
        description = parcel.readString();
        fullName = parcel.readString();
        ownerAvatarUrl = parcel.readString();
    }
}
