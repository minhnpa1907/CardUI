package com.minhnguyen.cardui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.model.User;
import com.minhnguyen.cardui.utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 2/22/17.
 */

public class WriteTagsFragment extends Fragment implements View.OnClickListener {

    private DialogWriteTagsFragment dialogWriteTagsFragment;

    @BindView(R.id.tilFullName)
    TextInputLayout tilFullName;
    @BindView(R.id.tilPosition)
    TextInputLayout tilPosition;
    @BindView(R.id.tilOrganization)
    TextInputLayout tilOrganization;
    @BindView(R.id.tilBirthday)
    TextInputLayout tilBirthday;
    @BindView(R.id.tilIDNumber)
    TextInputLayout tilIDNumber;

    // EditText
    @BindView(R.id.etFullName)
    EditText etFullName;
    @BindView(R.id.etPosition)
    EditText etPosition;
    @BindView(R.id.etOrganization)
    EditText etOrganization;
    @BindView(R.id.etBirthday)
    EditText etBirthday;
    @BindView(R.id.etIDNumber)
    EditText etIDNumber;

    @BindView(R.id.tvView)
    TextView tvView;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnRefresh)
    Button btnRefresh;

    public static WriteTagsFragment newInstance() {

        Bundle args = new Bundle();

        WriteTagsFragment fragment = new WriteTagsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnStart.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onNFCDetected(Intent intent) {
        byte[] data = getData();
        dialogWriteTagsFragment.onNFCDetected(intent, data);
    }

    private void showDialogWriteTagsFragment() {
        dialogWriteTagsFragment = (DialogWriteTagsFragment) getFragmentManager()
                .findFragmentByTag(DialogWriteTagsFragment.TAG);

        if (null == dialogWriteTagsFragment) {
            dialogWriteTagsFragment = DialogWriteTagsFragment.newInstance();
        }
        dialogWriteTagsFragment.show(getFragmentManager(), DialogWriteTagsFragment.TAG);
    }

    // Tao chuoi string data roi doi qua chuoi byte de luu xuong the
    public byte[] getData() {
        User user = new User();

        user.setUserName("Lương Thị Ngọc Mai");
        user.setUserGender("Nữ");
        user.setUserPosition("Chuyên viên");
        user.setUserOrganization("Văn phòng Sở");
        user.setUserNumber("11100000848");
        user.setUserBirthday("10/04/1988");
        user.setUserIDNumber("024758294");
        user.setUserPhoneNumber("098576xxxx");
        user.setUserEmail("mai.luong@email.com");

        // Convert to string json
        Gson gson = new Gson();
        String jsonData = gson.toJson(user);

        String strData = countData(jsonData) + jsonData;

        return Utilities.getInstance().convertFromStringToByteArray(strData);
    }

    // Method dem do dai chuoi data
    private String countData(String str) {
        byte[] bytes = Utilities.getInstance().convertFromStringToByteArray(str);
        int length = bytes.length;

        if (length < 10) {
            return String.valueOf(length)
                    + symbolCountSeparate()
                    + symbolCountSeparate()
                    + symbolCountSeparate()
                    + symbolCountSeparate();
        } else if (length >= 10 && length < 100) {
            return String.valueOf(length)
                    + symbolCountSeparate()
                    + symbolCountSeparate()
                    + symbolCountSeparate();
        } else if (length >= 100 && length < 1000) {
            return String.valueOf(length)
                    + symbolCountSeparate()
                    + symbolCountSeparate();
        } else {
            return String.valueOf(length)
                    + symbolCountSeparate();
        }
    }

    private void clearData() {
        etFullName.setText(null);
        etPosition.setText(null);
        etOrganization.setText(null);
        etBirthday.setText(null);
        etIDNumber.setText(null);
        etFullName.requestFocus();
    }

    private String separateSymbol() {
        return StringValue.DATA_SEPARATE;
    }

    private String symbolCountSeparate() {
        return StringValue.DATA_COUNT_SEPARATE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                showDialogWriteTagsFragment();
                break;
            case R.id.btnRefresh:
                clearData();
                break;
            default:
                break;
        }
    }
}
