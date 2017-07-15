package com.minhnguyen.cardui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.minhnguyen.cardui.IDialogMonthSelectListener;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 2/27/17.
 */

public class DialogMonthSelectFragment extends DialogFragment {

    public static final String TAG = DialogWriteTagsFragment.class.getSimpleName();

    @BindView(R.id.snMonth)
    Spinner snMonth;
    @BindView(R.id.snYear)
    Spinner snYear;

    public static DialogMonthSelectFragment newInstance(int month, int year) {

        Bundle args = new Bundle();

        args.putInt("month", month);
        args.putInt("year", year);
        DialogMonthSelectFragment fragment = new DialogMonthSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Select month")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendBackResult();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_month_select, null);
        ButterKnife.bind(this, view);

        alertDialogBuilder.setView(view);
        return alertDialogBuilder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeSpinner();

        snMonth.setSelection((getArguments().getInt("month")));
        snYear.setSelection(getPositionByValue(snYear, getArguments().getInt("year")));
    }

    private void initializeSpinner() {
        // Tao list nam, 10 nam - 5 nam truoc va 5 nam sau tu nam hien tai
        List<Integer> years = new ArrayList<>();
        years.add(Utilities.getInstance().getCurrentYear() - 5);

        for (int i = 1; i <= 10; i++) {
            years.add(years.get(i - 1) + 1);
        }

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, years);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        snYear.setAdapter(spinnerArrayAdapter);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        IDialogMonthSelectListener listener = (IDialogMonthSelectListener) getTargetFragment();
        int month = snMonth.getSelectedItemPosition();
        int year = Integer.parseInt(snYear.getSelectedItem().toString());
        listener.onFinishSelectMonthDialog(month, year);
        dismiss();
    }

    //private method of your class
    private int getPositionByValue(Spinner spinner, int value) {
        int position = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(String.valueOf(value))) {
                position = i;
                break;
            }
        }
        return position;
    }
}
