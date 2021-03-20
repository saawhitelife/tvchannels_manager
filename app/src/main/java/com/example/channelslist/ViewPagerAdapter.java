package com.example.channelslist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>{

    private LayoutInflater mInflater;

    private DBManager dbManager;

    private final String LOG_TAG = "ViewPagerAdapter";

    private Context mContext;

    private Activity mActivity;

    private ArrayList<TabTableCursor> mTabsTablesCursors;

    private SimpleCursorAdapter adapter;

    final String[] from = new String[]{DatabaseHelper._ID,
            DatabaseHelper.COL_NUMBER, DatabaseHelper.COL_NAME};

    final int[] to = new int[]{R.id.id, R.id.number, R.id.name};

    ViewPagerAdapter(Context context, ArrayList<TabTableCursor> tabsTablesCursors, Activity activity) {
        mActivity = activity;
        mTabsTablesCursors = tabsTablesCursors;
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pagePosition = position;
        Cursor cursor = mTabsTablesCursors.get(position).getCursor();
        adapter = new SimpleCursorAdapter(mContext, R.layout.activity_view_record, cursor, from, to, 0);
        holder.listView.setEmptyView(holder.emptyView);
        holder.listView.setAdapter(adapter);

        holder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = view.findViewById(R.id.id);
                TextView numberTextView = view.findViewById(R.id.number);
                TextView descTextView = view.findViewById(R.id.name);
                cursor.moveToPosition(position);
                String search = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_SEARCH));

                String id = idTextView.getText().toString();
                String number = numberTextView.getText().toString();
                String name = descTextView.getText().toString();
                Intent modifyIntent = new Intent(mContext, ModifyChannelActivity.class);
                modifyIntent.putExtra("number", number);
                modifyIntent.putExtra("name", name);
                modifyIntent.putExtra("id", id);
                modifyIntent.putExtra("search", search);
                modifyIntent.putExtra("tableName", mTabsTablesCursors.get(pagePosition).getTableName());
                modifyIntent.putExtra("tabIndex", pagePosition);
                mActivity.startActivityForResult(modifyIntent, ModifyChannelActivity.REQUEST_CODE);

            }
        });

    }

    @Override
    public int getItemCount() {
        dbManager = new DBManager(mContext);
        dbManager.open();
        int count = dbManager.getTablesCount();
        dbManager.close();
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListView listView;
        TextView emptyView;

        ViewHolder(View itemView) {
            super(itemView);
            listView = itemView.findViewById(R.id.list_view);
            emptyView = itemView.findViewById(R.id.empty);
//            button = itemView.findViewById(R.id.btnToggle);
//
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    if(viewPager2.getOrientation() == ViewPager2.ORIENTATION_VERTICAL)
//                        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
//                    else{
//                        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
//                    }
//                }
//            });
        }
    }

}