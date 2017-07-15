package com.minhnguyen.cardui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.database.StatisticalDatabaseHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 1/16/17.
 */

public class WorkContactActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.snOrganization)
    Spinner snOrganization;
    @BindView(R.id.edtReason)
    EditText edtReason;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_contact);
        setupActionBar();
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initializeSpinner();

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirm:
                break;
            case R.id.btnCancel:
                break;
            default:
                break;
        }
    }

    private void initializeSpinner() {
        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(this);
        List<String> organizationList = databaseHelper.getAllOrg();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, organizationList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        snOrganization.setAdapter(spinnerArrayAdapter);
    }
}
