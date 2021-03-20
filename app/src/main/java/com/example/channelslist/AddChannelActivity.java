package com.example.channelslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddChannelActivity extends AppCompatActivity implements OnClickListener {
    private Button addButton;
    private Button cancelButton;
    private EditText numberEditText;
    private EditText searchEditText;
    private EditText nameEditText;
    public final static int REQUEST_CODE = 123;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_channel);

        numberEditText = (EditText) findViewById(R.id.number_edittext);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        searchEditText = (EditText) findViewById(R.id.search_edittext);

        addButton = (Button) findViewById(R.id.add_channel);
        cancelButton = (Button) findViewById(R.id.cancel_channel_add);

        dbManager = new DBManager(this);
        dbManager.open();
        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_channel:
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
                final String name = nameEditText.getText().toString();
                final String search = searchEditText.getText().toString();
                String tableName = getIntent().getStringExtra("tableName");

                dbManager.insert(number, name, search, tableName);
                finish();
                break;
            case R.id.cancel_channel_add:
                finish();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        intent.putExtra("tabIndex", tabIndex);
        // Activity finished ok, return the data
        setResult(RESULT_OK, intent);
        super.finish();
    }

    void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
