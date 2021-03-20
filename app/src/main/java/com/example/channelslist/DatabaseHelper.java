package com.example.channelslist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "CHANNELS_LIST.DB";

    // database version
    static final int DB_VERSION = 1;

    // Initial Channels Table Name
    public static final String TABLE_NAME = "CHANNELS_0";
    public static final String VERBOSE_TABLE_NAME = "Kitchen TV";

    // List of tables table name
    public static final String TABLES_LIST_TABLE_NAME = "TABLES_LIST";

    // Id column used in both tables
    public static final String _ID = "_id";

    // Tables list table columns
    public static final String COL_TABLE_NAME = "table_name";
    public static final String COL_VERBOSE_TABLE_NAME = "verbose_table_name";

    // Create tables list table query
    private static final String CREATE_TABLES_LIST_TABLE =
            "CREATE TABLE " + TABLES_LIST_TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TABLE_NAME + " TEXT NOT NULL ,"
            + COL_VERBOSE_TABLE_NAME + " TEXT NOT NULL"
            + ");";

    // Table columns
    public static final String COL_NUMBER = "number";
    public static final String COL_NAME = "name";
    public static final String COL_SEARCH = "search";

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NUMBER + " INTEGER NOT NULL, " + COL_NAME + " TEXT," + COL_SEARCH + " TEXT);";

    private static final String REGISTER_INITIAL_TABLE_QUERY = "INSERT INTO " + TABLES_LIST_TABLE_NAME
            + "(" + COL_TABLE_NAME + ", "
            + COL_VERBOSE_TABLE_NAME + ")"
            + " VALUES (" + "'" + TABLE_NAME + "'" + ","
            + "'" + VERBOSE_TABLE_NAME + "'" + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES_LIST_TABLE);
        db.execSQL(CREATE_TABLE);
        db.execSQL(REGISTER_INITIAL_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
