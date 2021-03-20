package com.example.channelslist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ModifyChannelActivity extends AppCompatActivity implements OnClickListener {
    private final String LOG_TAG = "ModifyChannelActivity";
    public final static int REQUEST_CODE = 124;
    private EditText numberEditText;
    private Button updateBtn, deleteBtn;
    private EditText nameEditText;
    private EditText searchText;

    private long _id;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_record);

        dbManager = new DBManager(this);
        dbManager.open();

        numberEditText = (EditText) findViewById(R.id.number_edittext);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        searchText = (EditText) findViewById(R.id.search_edittext);

        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String number = intent.getStringExtra("number");
        String name = intent.getStringExtra("name");
        String search = intent.getStringExtra("search");
        _id = Long.parseLong(id);

        numberEditText.setText(number);
        nameEditText.setText(name);
        searchText.setText(search);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                int number = 0;
                String numberText = numberEditText.getText().toString();
                if (
                        !StringUtil.containsOnlyDigitsNoSpaces(numberText)
                                || numberText.length() > StringUtil.MAXIMUM_CHANNEL_NUMBER_LENGTH
                                || numberText.length() < StringUtil.MINIMUM_CHANNEL_NUMBER_LENGTH
                ) {
                    showLongToast("Minimum channel Number is 0. Maximum value for channel number is 9999. Only digits allowed");
                    break;
                } else {
                    number = Integer.parseInt(numberEditText.getText().toString());
                }
                String desc = nameEditText.getText().toString();
                String search = searchText.getText().toString();
                dbManager.update(_id, number, desc, search, getIntent().getStringExtra("tableName"));
                finish();
                break;

            case R.id.btn_delete:
                dbManager.delete(_id,  getIntent().getStringExtra("tableName"));
                finish();
                break;
        }
    }

    void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void finish() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        intent.putExtra("tabIndex", tabIndex);
        // Activity finished ok, return the data
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
