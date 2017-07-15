package com.minhnguyen.cardui.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import com.minhnguyen.cardui.constant.StringValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by minhnguyen on 2/23/17.
 */

public class Utilities {
    private static Utilities Instance;

    public static Utilities getInstance() {
        if (null == Instance) {
            Instance = new Utilities();
        }

        return Instance;
    }

    private SimpleDateFormat shortDateFormatForDatabase = new SimpleDateFormat(StringValue.SHORT_DATE_FORMAT_DATABASE, Locale.ENGLISH);
    private SimpleDateFormat longDateFormatForDatabase = new SimpleDateFormat(StringValue.LONG_DATE_FORMAT_DATABASE, Locale.ENGLISH);
    private SimpleDateFormat shortDateFormatForView = new SimpleDateFormat(StringValue.SHORT_DATE_FORMAT_VIEW, Locale.ENGLISH);
    private SimpleDateFormat longDateFormatForView = new SimpleDateFormat(StringValue.LONG_DATE_FORMAT_VIEW, Locale.ENGLISH);
    private SimpleDateFormat shortMonthFormatForView = new SimpleDateFormat(StringValue.SHORT_MONTH_FORMAT_VIEW, Locale.ENGLISH);

    public String convertFromByteArrayToString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] convertFromStringToByteArray(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String[] splitString(String strSplit, String strSign) {
        return strSplit.split(strSign);
    }

    public void toastMessage(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public void setDeviceVibrate(Activity activity, int intensity) {
        if (null != activity) {
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(intensity);
        }
    }

    public int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public String convertDateToStringShortDatabaseFormat(Date date) {
        return shortDateFormatForDatabase.format(date);
    }

    public Date convertStringToDateShortDatabaseFormat(String string) {
        try {
            return shortDateFormatForDatabase.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String convertDateToStringLongDatabaseFormat(Date date) {
        return longDateFormatForDatabase.format(date);
    }

    public Date convertStringToDateLongDatabaseFormat(String string) {
        try {
            return longDateFormatForDatabase.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String convertDateToStringShortFormat(Date date) {
        return shortDateFormatForView.format(date);
    }

    public String convertDateToStringLongFormat(Date date) {
        return longDateFormatForView.format(date);
    }

    public Date convertStringToDateShortFormat(String string) {
        try {
            return shortDateFormatForView.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Date convertStringToDateLongFormat(String string) {
        try {
            return longDateFormatForView.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String convertDateToStringShortMonthFormat(Date date) {
        return shortMonthFormatForView.format(date);
    }

    public Date convertStringToDateShortMonthFormat(String string) {
        try {
            return shortMonthFormatForView.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
