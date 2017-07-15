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

import com.minhnguyen.cardui.IDialogMonthSelectListener;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.adapter.StatisticsItemAdapter;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.database.StatisticalDatabaseHelper;
import com.minhnguyen.cardui.model.StatisticItem;
import com.minhnguyen.cardui.utilities.Utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 1/16/17.
 */

public class MonthlyStatisticsFragment extends Fragment implements IDialogMonthSelectListener {

    @BindView(R.id.snOrganization)
    Spinner snOrganization;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.rvMonthlyStatistics)
    RecyclerView rvMonthlyStatistics;
    List<StatisticItem> statisticItems;

    Date dateStart;
    Date dateEnd;

    public static MonthlyStatisticsFragment newInstance() {
        Bundle args = new Bundle();
        MonthlyStatisticsFragment fragment = new MonthlyStatisticsFragment();

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
        return inflater.inflate(R.layout.fragment_monthly_statistics, container, false);
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

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        calendarStart.set(Utilities.getInstance().getCurrentYear(),
                Utilities.getInstance().getCurrentMonth(),
                1);
        calendarEnd.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH),
                calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH));

        dateStart = calendarStart.getTime();
        dateEnd = calendarEnd.getTime();
        tvDate.setText(Utilities.getInstance().convertDateToStringShortMonthFormat(dateStart));
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Date date = Utilities.getInstance().convertStringToDateShortMonthFormat(tvDate.getText().toString());

                calendar.setTime(date);
                showDialogMonthPickFragment(calendar);
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

    @Override
    public void onFinishSelectMonthDialog(int month, int year) {
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        calendarStart.set(year, month, 1);
        calendarEnd.set(year, month, calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        dateStart = calendarStart.getTime();
        dateEnd = calendarEnd.getTime();

        tvDate.setText(Utilities.getInstance().convertDateToStringShortMonthFormat(dateStart));
    }

    private void syncData() {
        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(getContext());
        String strOrganization;

        if (snOrganization.getSelectedItem().toString().equals(StringValue.LIST_ALL)) {
            strOrganization = null;
        } else {
            strOrganization = snOrganization.getSelectedItem().toString();
        }

        statisticItems = databaseHelper.getStatistical(strOrganization, dateStart, dateEnd);
        initializeAdapter();
    }

    private void initializeSpinner() {
        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(getContext());
        List<String> organizationList = databaseHelper.getAllOrg();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_text, organizationList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        snOrganization.setAdapter(spinnerArrayAdapter);
    }

    private void initializeAdapter() {
        StatisticsItemAdapter adapter = new StatisticsItemAdapter(getContext(), statisticItems);
        rvMonthlyStatistics.setAdapter(adapter);
        rvMonthlyStatistics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }


    private void showDialogMonthPickFragment(Calendar calendar) {
        DialogMonthSelectFragment dialogMonthPickFragment = (DialogMonthSelectFragment) getFragmentManager()
                .findFragmentByTag(DialogMonthSelectFragment.TAG);

        if (null == dialogMonthPickFragment) {
            dialogMonthPickFragment = DialogMonthSelectFragment.newInstance(calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.YEAR));
        }
        dialogMonthPickFragment.setTargetFragment(MonthlyStatisticsFragment.this, 9001);
        dialogMonthPickFragment.show(getFragmentManager(), DialogMonthSelectFragment.TAG);
    }
}
