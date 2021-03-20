package com.example.channelslist;

public class TvRequest {
    public static final int ADD_TV = 1;
    public static final int RENAME_TV = 2;
    private int mRequestType;
    private String mTitle;
    TvRequest(String tvTitle, int requestType) {
        mRequestType = requestType;
        mTitle = tvTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getRequestType() {
        return mRequestType;
    }
}
