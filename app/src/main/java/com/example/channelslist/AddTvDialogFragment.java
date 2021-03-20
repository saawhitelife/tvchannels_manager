package com.example.channelslist;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class AddTvDialogFragment extends DialogFragment implements View.OnClickListener {
    private ItemViewModel viewModel;
    private EditText mEditText;
    Button confirm;
    private final String LOG_TAG = "AddTvDialogFragment";
    private final String CANCEL = "Cancel";

    public AddTvDialogFragment() {
    }

    public static AddTvDialogFragment newInstance(String tvTitle, int tvRequestType) {
        AddTvDialogFragment frag = new AddTvDialogFragment();
        Bundle args = new Bundle();
        args.putInt("tvRequestType", tvRequestType);
        args.putString("tvTitle", tvTitle);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(requireActivity()).get(ItemViewModel.class);
        getDialog().setTitle(getString(R.string.action_add_tv));
        View v = inflater.inflate(R.layout.add_tv_dialog, null);
        confirm = (Button) v.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        mEditText = v.findViewById(R.id.tv_title);
        if (!getArguments().getString("tvTitle").isEmpty()) {
            getDialog().setTitle(String.format(getString(R.string.action_rename_tv_title), getArguments().getString("tvTitle")));
            confirm.setText(R.string.rename_tv_confirm);
        }
        mEditText.setText(getArguments().getString("tvTitle"));
        return v;
    }

    public void onClick(View v) {
        if (((Button) v).getText().toString().equals(CANCEL)) {
            dismiss();
        } else {
            String newTvName = mEditText.getText().toString();
            if (newTvName.isEmpty()) {
                Toast.makeText(getContext(), getResources().getString(R.string.add_empty_tv), Toast.LENGTH_LONG).show();
            } else if (newTvName.length() < StringUtil.MINIMUM_TV_NAME_LENGTH) {
                Toast.makeText(getContext(), getResources().getString(R.string.add_tv_too_short_name), Toast.LENGTH_LONG).show();
            } else {
                if (!StringUtil.detectUnwantedSymbols(newTvName)) {
                    TvRequest tvRequest = new TvRequest(newTvName, getArguments().getInt("tvRequestType"));
                    viewModel.selectItem(tvRequest);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.add_tv_prohibited_symbols), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static String TAG = "AddTVConfirmationDialog";
}