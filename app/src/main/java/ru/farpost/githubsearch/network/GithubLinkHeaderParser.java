package ru.farpost.githubsearch.network;

import android.support.annotation.Nullable;

import okhttp3.Headers;

/**
 * Created by eugene on 12/2/16.
 */

public class GithubLinkHeaderParser {
    private static final String DELIM_LINKS = ",";
    private static final String DELIM_LINK_PARAM = ";";

    private static final String HEADER_LINK = "Link";
    private static final String HEADER_NEXT = "X-Next";
    private static final String HEADER_LAST = "X-Last";

    private static final String META_REL = "rel";
    private static final String META_LAST = "last";
    private static final String META_NEXT = "next";
    private static final String META_FIRST = "first";
    private static final String META_PREV = "prev";

    private String mFirst;
    private String mLast;
    private String mNext;
    private String mPrev;


    public GithubLinkHeaderParser(Headers responseHeaders) {
        String linkHeader = responseHeaders.get(HEADER_LINK);
        if (linkHeader != null) {
            String[] linkItems = linkHeader.split(DELIM_LINKS);
            for (String linkItem : linkItems) {
                parseLinkItem(linkItem);
            }
        } else {
            mNext = responseHeaders.get(HEADER_NEXT);
            mLast = responseHeaders.get(HEADER_LAST);
        }
    }

    private void parseLinkItem(String linkItem) {
        String[] segments = linkItem.split(DELIM_LINK_PARAM);
        if (segments.length >= 2) {
            String link = segments[0].trim();
            if (link.startsWith("<") && link.endsWith(">")) {
                link = link.substring(1, link.length() - 1);
                for (int i = 1; i < segments.length; i++) {
                    parseLinkRelation(link, segments[i]);
                }
            }
        }
    }

    private void parseLinkRelation(String link, String relationData) {
        String[] rel = relationData.trim().split("=");
        if (rel.length >= 2 && META_REL.equals(rel[0])) {
            String relValue = rel[1];
            if (relValue.startsWith("\"") && relValue.endsWith("\"")) {
                relValue = relValue.substring(1, relValue.length() - 1);
            }
            switch (relValue) {
                case META_FIRST:
                    mFirst = link;
                    break;
                case META_LAST:
                    mLast = link;
                    break;
                case META_NEXT:
                    mNext = link;
                    break;
                case META_PREV:
                    mPrev = link;
                    break;
            }
        }
    }

    public @Nullable String getFirst() {
        return mFirst;
    }

    public @Nullable String getLast() {
        return mLast;
    }

    public @Nullable String getNext() {
        return mNext;
    }

    public @Nullable String getPrev() {
        return mPrev;
    }
}
