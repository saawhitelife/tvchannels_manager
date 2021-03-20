package com.example.channelslist;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    MenuItem voice;
    MenuItem removeTv;
    MenuItem search;
    MenuItem addChannel;
    MenuItem renameTv;
    MenuItem importXLS;
    MenuItem settings;

    private final String LOG_TAG = "MainActivity";

    private TabLayout tabLayout;

    private boolean didSearch = false;

    private static final int SPEECH_REQUEST_CODE = 0;

    private static final int FILE_READ_REQUEST_CODE = 125;

    private ItemViewModel viewModel;

    ArrayList<TabTableCursor> tabsTablesCursors;

    private ViewPagerAdapter adapter;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_emp_list);

        viewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        viewPager2 = findViewById(R.id.pager);
        tabsTablesCursors = getTabsTablesCursors();
        adapter = new ViewPagerAdapter(this, tabsTablesCursors, this);
        viewPager2.setAdapter(adapter);
        viewModel.getSelectedItem().observe(this, tvRequest -> {
            switch (tvRequest.getRequestType()) {
                case TvRequest.ADD_TV:
                    try {
                        dbManager.open();
                        dbManager.addTv(tvRequest.getTitle());
                    } finally {
                        dbManager.close();
                    }
                    toggleMenuItemsVisibility(true);
                    tabsTablesCursors.clear();
                    tabsTablesCursors.addAll(getTabsTablesCursors());
                    Log.v(LOG_TAG, "observation done");
                    adapter.notifyDataSetChanged();
                    TabLayout.Tab tab = tabLayout.getTabAt(tabsTablesCursors.size() - 1);
                    tab.select();
                case TvRequest.RENAME_TV:
                    try {
                        dbManager.open();
                        dbManager.renameTv(tvRequest.getTitle(), tabsTablesCursors.get(tabLayout.getSelectedTabPosition()).getTableName());
                    } finally {
                        dbManager.close();
                    }
                    tabsTablesCursors.get(tabLayout.getSelectedTabPosition()).setTabName(tvRequest.getTitle());
                    tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).setText(tvRequest.getTitle());


            }

        });

        Log.v(LOG_TAG, "onCreateActivity Invoked");
        tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText(tabsTablesCursors.get(position).getTabName());
                }
        ).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Log.v(LOG_TAG, String.format("Table name for the selected tab position %s", tabsTablesCursors.get(tab.getPosition()).getTableName()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void toggleMenuItemsVisibility(boolean toggle) {
        voice.setVisible(toggle);
        importXLS.setVisible(toggle);
        addChannel.setVisible(toggle);
        search.setVisible(toggle);
        removeTv.setVisible(toggle);
        renameTv.setVisible(toggle);
        invalidateOptionsMenu();
    }

    ArrayList<TabTableCursor> getTabsTablesCursors() {
        ArrayList<TabTableCursor> tabsTablesCursors = new ArrayList<>();
        dbManager = new DBManager(getApplicationContext());
        dbManager.open();
        Cursor tabTablesCursor = dbManager.getTables();
        if (tabTablesCursor.getCount() > 0) {
            try {
                String tableName = tabTablesCursor.getString(tabTablesCursor.getColumnIndex(DatabaseHelper.COL_TABLE_NAME));
                tabsTablesCursors.add(new TabTableCursor(
                        tabTablesCursor.getString(tabTablesCursor.getColumnIndex(DatabaseHelper.COL_VERBOSE_TABLE_NAME)),
                        tableName,
                        dbManager.fetch(tableName)
                ));
                while (tabTablesCursor.moveToNext()) {
                    tableName = tabTablesCursor.getString(tabTablesCursor.getColumnIndex(DatabaseHelper.COL_TABLE_NAME));
                    tabsTablesCursors.add(new TabTableCursor(
                            tabTablesCursor.getString(tabTablesCursor.getColumnIndex(DatabaseHelper.COL_VERBOSE_TABLE_NAME)),
                            tableName,
                            dbManager.fetch(tableName)
                    ));
                }
            } finally {
                dbManager.close();
                tabTablesCursor.close();
            }
            for (TabTableCursor t : tabsTablesCursors) {
                Log.v(LOG_TAG, t.toString());
            }
        }
        return tabsTablesCursors;
    }

    Cursor getSearchCursor(String tableName, String searchTerm) {
        Cursor searchCursor;
        dbManager.open();
        try {
            searchCursor = dbManager.getSearchMatches(searchTerm, null, tableName);
        } finally {
            dbManager.close();
        }
        return searchCursor;
    }

    private void showTvAlertDialog(int tvRequestType) {
        String tvTitle = "";
        FragmentManager fm = getSupportFragmentManager();
        if (tabsTablesCursors.size() > 0) {
            tvTitle = (tvRequestType == TvRequest.ADD_TV) ? "" : tabsTablesCursors.get(tabLayout.getSelectedTabPosition()).getTabName();
        }
        AddTvDialogFragment alertDialog = AddTvDialogFragment.newInstance(tvTitle, tvRequestType);
        alertDialog.show(fm, AddTvDialogFragment.TAG);
    }

    public void showRemoveTvAlertDialog() {
        int index = tabLayout.getSelectedTabPosition();
        String tvTitle = tabsTablesCursors.get(index).getTabName();
        String tableName = tabsTablesCursors.get(index).getTableName();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setTitle(String.format(getString(R.string.remove_tv_dialog_title), tvTitle))
                .setPositiveButton(getString(R.string.delete_tv), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dbManager.open();
                            dbManager.removeTv(tableName);
                            tabsTablesCursors.remove(index);
                            if (tabsTablesCursors.size() == 0)
                                toggleMenuItemsVisibility(false);
                            adapter.notifyItemRemoved(index);
                        } finally {
                            dbManager.close();
                        }
                    }
                })
                .setNegativeButton(R.string.dont_delete_tv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        voice = menu.findItem(R.id.action_speech);
        importXLS = menu.findItem(R.id.action_import_xls);
        addChannel = menu.findItem(R.id.add_channel);
        search = menu.findItem(R.id.action_search);
        removeTv = menu.findItem(R.id.action_remove_tv);
        renameTv = menu.findItem(R.id.action_rename_tv);
        settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
        if (tabsTablesCursors.size() == 0)
            toggleMenuItemsVisibility(false);
        // Implement search
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v(LOG_TAG,
                        String.format("Typed: %s", newText)
                );
                int index = tabLayout.getSelectedTabPosition();
                TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
                String tableName = tabTableCursor.getTableName();
                Cursor searchCursor = getSearchCursor(tableName, newText);
                tabTableCursor.setCursor(searchCursor);
                tabsTablesCursors.set(index, tabTableCursor);
                adapter.notifyItemChanged(index);
                didSearch = true;
                return false;
            }
        });
        return true;
    }

    public void openFileBrowser() {
        // Use ACTION_GET_CONTENT if you want your app to simply read or import data.
        // With this approach, the app imports a copy of the data, such as an image file.
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        fileIntent.setType("*/*");
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(fileIntent, FILE_READ_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }

    public ArrayList<Channel> readXlsFile(Uri uri) {
        ArrayList<Channel> channels = new ArrayList<>();
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int rows = s.getRows();
            int cols = s.getColumns();
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < cols; column++) {
                    Cell cell = s.getCell(column, row);
                    String contents = cell.getContents();
                    int holder = 0;
                    try {
                        holder = Integer.parseInt(contents);
                    } catch (Exception e) {
                    }
                    if (holder != 0) {
                        Cell titleCell = s.getCell(column + 1, row);
                        String title = titleCell.getContents();
                        Channel channel = new Channel(title, title, holder);
                        channels.add(channel);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, String.format("exception occurred: %s", e.toString()));
        }
//        for (Channel ch: channels) {
//            Log.e(
//                    LOG_TAG,
//                    String.format("Channel #%s - %s", ch.getNumber(), ch.getTitle())
//            );
//        }
        return channels;
    }

    @Override
    public void onBackPressed() {
        if (didSearch) {
            int index = tabLayout.getSelectedTabPosition();
            dbManager.open();
            try {
                Cursor cursor = (dbManager.fetch(tabsTablesCursors.get(index).getTableName()));
                TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
                tabTableCursor.setCursor(cursor);
                tabsTablesCursors.set(index, tabTableCursor);
                adapter.notifyItemChanged(index);
            } finally {
                dbManager.close();
                didSearch = false;
            }
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.add_channel:
                    Intent add_mem = new Intent(this, AddChannelActivity.class);
                    String tableName = tabsTablesCursors.get(tabLayout.getSelectedTabPosition()).getTableName();
                    add_mem.putExtra("tableName", tableName);
                    add_mem.putExtra("tabIndex", tabLayout.getSelectedTabPosition());
                    startActivityForResult(add_mem, AddChannelActivity.REQUEST_CODE);
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_speech:
                displaySpeechRecognizer();
                return true;
            case R.id.action_add_tv:
                showTvAlertDialog(TvRequest.ADD_TV);
                return true;
            case R.id.action_rename_tv:
                showTvAlertDialog(TvRequest.RENAME_TV);
                return true;
            case R.id.action_remove_tv:
                showRemoveTvAlertDialog();
                return true;
            case R.id.action_import_xls:
                openFileBrowser();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            int index = tabLayout.getSelectedTabPosition();
            TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
            String tableName = tabTableCursor.getTableName();
            Cursor searchCursor = getSearchCursor(tableName, spokenText);
            tabTableCursor.setCursor(searchCursor);
            tabsTablesCursors.set(index, tabTableCursor);
            adapter.notifyItemChanged(index);
            didSearch = true;
        }
        if (requestCode == AddChannelActivity.REQUEST_CODE) {
            if (data.hasExtra("tabIndex")) {
                int index = data.getIntExtra("tabIndex", 0);
                dbManager.open();
                try {
                    Cursor cursor = (dbManager.fetch(tabsTablesCursors.get(index).getTableName()));
                    TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
                    tabTableCursor.setCursor(cursor);
                    tabsTablesCursors.set(index, tabTableCursor);
                    adapter.notifyItemChanged(index);
                } finally {
                    dbManager.close();
                }
                Toast.makeText(
                        this,
                        String.format("switch to tab %s", data.getIntExtra("tabIndex", 0)),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        if (requestCode == ModifyChannelActivity.REQUEST_CODE) {
            if (data.hasExtra("tabIndex")) {
                int index = data.getIntExtra("tabIndex", 0);
                dbManager.open();
                try {
                    Cursor cursor = (dbManager.fetch(tabsTablesCursors.get(index).getTableName()));
                    TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
                    tabTableCursor.setCursor(cursor);
                    tabsTablesCursors.set(index, tabTableCursor);
                    adapter.notifyItemChanged(index);
                } finally {
                    dbManager.close();
                }
                Toast.makeText(
                        this,
                        String.format("switch to tab %s", data.getIntExtra("tabIndex", 0)),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        if (requestCode == FILE_READ_REQUEST_CODE) {
            if (!data.getData().getPath().isEmpty()) {
                int index = tabLayout.getSelectedTabPosition();
                String tableName = tabsTablesCursors.get(index).getTableName();
                ArrayList<Channel> channels = readXlsFile(data.getData());
                try {
                    dbManager.open();
                    dbManager.clearTableByName(tableName);
                    dbManager.putChannelsListIntoTable(tableName, channels);
                    Cursor cursor = (dbManager.fetch(tabsTablesCursors.get(index).getTableName()));
                    TabTableCursor tabTableCursor = tabsTablesCursors.get(index);
                    tabTableCursor.setCursor(cursor);
                    tabsTablesCursors.set(index, tabTableCursor);
                    adapter.notifyItemChanged(index);
                } finally {
                    dbManager.close();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
