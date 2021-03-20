package com.example.channelslist;

import android.database.Cursor;

import androidx.annotation.NonNull;

public class TabTableCursor {
    private String mTabName;
    private String mTableName;
    private Cursor mCursor;

    TabTableCursor(String tabName, String tableName, Cursor cursor) {
        mTabName = tabName;
        mTableName = tableName;
        mCursor = cursor;
    }

    public String getTableName() {
        return mTableName;
    }

    public String getTabName() {
        return mTabName;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }

    public void setTabName(String tabName) {
        mTabName = tabName;
    }

    @NonNull
    @Override
    public String toString() {
        String representation =
                String.format(
                        "Tab name : %s \nTable name: %s \nCursor: %s",
                        mTabName, mTableName, mCursor.toString()
                );
        return representation;
    }
}
