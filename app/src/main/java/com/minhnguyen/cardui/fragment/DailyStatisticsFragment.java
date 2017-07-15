package com.minhnguyen.cardui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.adapter.StatisticsItemAdapter;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.database.StatisticalDatabaseHelper;
import com.minhnguyen.cardui.model.StatisticItem;
import com.minhnguyen.cardui.utilities.Utilities;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 1/16/17.
 */

public class DailyStatisticsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.snOrganization)
    Spinner snOrganization;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.rvDailyStatistics)
    RecyclerView rvDailyStatistics;
    List<StatisticItem> statisticItems;

    Date dateStart;
    Date dateEnd;

    public static DailyStatisticsFragment newInstance() {
        Bundle args = new Bundle();
        DailyStatisticsFragment fragment = new DailyStatisticsFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeSpinner();

        // Tao ngay moi va gan vao TextView
        dateStart = dateEnd = new Date();
        tvDate.setText(Utilities.getInstance().convertDateToStringShortFormat(dateStart));
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Date date = Utilities.getInstance().convertStringToDateShortFormat(tvDate.getText().toString());

                calendar.setTime(date);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DailyStatisticsFragment.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_sync, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tbSync:
                syncData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeSpinner() {
        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(getContext());
        List<String> organizationList = databaseHelper.getAllOrg();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_text, organizationList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        snOrganization.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, monthOfYear, dayOfMonth);
        dateStart = dateEnd = calendar.getTime();
        tvDate.setText(Utilities.getInstance().convertDateToStringShortFormat(dateStart));
    }

    // Lay data tu database
    private void syncData() {
        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(getContext());
        String strOrganization;

        if (snOrganization.getSelectedItem().toString().equals(StringValue.LIST_ALL)) {
            strOrganization = null;
        } else {
            strOrganization = snOrganization.getSelectedItem().toString();
        }

        statisticItems = databaseHelper.getStatistical(strOrganization, dateStart, dateEnd);
        // Co data roi thi bo vao RecyclerView
        initializeAdapter();
    }

    private void initializeAdapter() {
        StatisticsItemAdapter adapter = new StatisticsItemAdapter(getContext(), statisticItems);
        rvDailyStatistics.setAdapter(adapter);
        rvDailyStatistics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}
