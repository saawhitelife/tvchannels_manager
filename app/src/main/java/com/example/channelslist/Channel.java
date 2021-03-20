package com.example.channelslist;

public class Channel {
    private String mTitle;
    private String mSearch;
    private int mNumber;
    Channel(String title, String search, int number) {
        mTitle = title;
        mSearch = search;
        mNumber = number;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSearch() {
        return mSearch;
    }
}
