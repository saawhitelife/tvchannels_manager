package com.example.channelslist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    private final String LOG_TAG = "DB_MANAGER";

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addTv(String tvTitle) {
        String newTableName = UUID.randomUUID().toString().replaceAll("[0-9-]", "");
        String create_table_query = "create table " + newTableName
                + "(" + DatabaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseHelper.COL_NUMBER + " INTEGER NOT NULL, "
                + DatabaseHelper.COL_NAME + " TEXT,"
                + DatabaseHelper.COL_SEARCH + " TEXT);";
        database.execSQL(create_table_query);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_TABLE_NAME, newTableName);
        contentValues.put(DatabaseHelper.COL_VERBOSE_TABLE_NAME, tvTitle);
        // returns id of the inserted row or -1 in case of error
        long id = database.insert(DatabaseHelper.TABLES_LIST_TABLE_NAME, null, contentValues);
        return id != -1;
    }

    public void clearTableByName(String tableName) {
        String clear_table_query = "DELETE FROM " + tableName;
        database.execSQL(clear_table_query);
    }

    public void putChannelsListIntoTable(String tableName, ArrayList<Channel> channels) {
        for (Channel ch: channels) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COL_NAME, ch.getTitle());
            contentValues.put(DatabaseHelper.COL_NUMBER, ch.getNumber());
            contentValues.put(DatabaseHelper.COL_SEARCH, ch.getTitle());
            database.insert(tableName, null, contentValues);
        }
    }

    public int renameTv(String tvName, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_VERBOSE_TABLE_NAME, tvName);
        int i = database.update(DatabaseHelper.TABLES_LIST_TABLE_NAME, contentValues, DatabaseHelper.COL_TABLE_NAME + " = " + "'" + tableName + "'", null);
        return i;
    }

    public void removeTv(String tableName) {
        database.delete(DatabaseHelper.TABLES_LIST_TABLE_NAME, DatabaseHelper.COL_TABLE_NAME + "=" + "'" + tableName + "'", null);
        database.execSQL("DROP TABLE IF EXISTS " + "'" + tableName + "'");
    }

    public void insert(int number, String name, String search, String table) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_NUMBER, number);
        contentValues.put(DatabaseHelper.COL_NAME, name);
        contentValues.put(DatabaseHelper.COL_SEARCH, search);
        database.insert(table, null, contentValues);
    }

    public Cursor getTables() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.COL_TABLE_NAME, DatabaseHelper.COL_VERBOSE_TABLE_NAME};
        Cursor cursor = database.query(DatabaseHelper.TABLES_LIST_TABLE_NAME, columns, null, null, null, null, DatabaseHelper._ID + " ASC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Log.v(LOG_TAG, "invoked get tables");
        return cursor;
    }

    public int getTablesCount() {
        Cursor cursor = getTables();
        return cursor.getCount();
    }

    public Cursor getSearchMatches(String query, String[] columns, String table) {
        String selection = DatabaseHelper.COL_SEARCH + " LIKE ?";
        String[] selectionArgs = new String[] {"%" + query+ "%"};

        return query(selection, selectionArgs, columns, table);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns, String table) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(table);

        Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor fetch(String table) {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.COL_NUMBER, DatabaseHelper.COL_NAME, DatabaseHelper.COL_SEARCH};
        Cursor cursor = database.query(table, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, int number, String desc, String search, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_NUMBER, number);
        contentValues.put(DatabaseHelper.COL_NAME, desc);
        contentValues.put(DatabaseHelper.COL_SEARCH, search);
        int i = database.update(tableName, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id, String tableName) {
        database.delete(tableName, DatabaseHelper._ID + "=" + _id, null);
    }

}
