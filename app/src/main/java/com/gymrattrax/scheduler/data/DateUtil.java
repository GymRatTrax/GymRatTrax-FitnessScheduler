package com.gymrattrax.scheduler.data;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    /**
     * Convert a String representation of a date from the database or preferences to a Java Date
     * object.
     * @param date A {@link String} representation of a date.
     * @return A {@link java.util.Date} object corresponding to that date.
     */
    public static Date convertDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        return sdf.parse(date);
    }

    /**
     * Convert a Java Date object to a String representation that is formatted for use in the
     * database or preferences.
     * @param date A {@link java.util.Date} object.
     * @return A {@link String} representation of that date.
     */
    public static String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        return sdf.format(date);
    }

    /**
     * Displays a passed in Date value (date only) according to the preferred date format.
     * @param context The Context that will be passed into the getDefaultSharedPreferences routine.
     * @param date The Date that will be displayed.
     * @return A String value that represents the passed in date according to the preferred format.
     * @see DateUtil#displayDateTime(android.content.Context,
     * java.util.Date) displayDateTime returns a formatted string that includes the time value.
     */
    public static String displayDate(Context context, Date date) {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        String dateFormat = sharedPref.getString(SettingsActivity.PREF_DATE_FORMAT, "MM/dd/yyyy");
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
//        return sdf.format(date);
        return android.text.format.DateFormat.getDateFormat(context).format(date);
    }

    /**
     * Displays a passed in Date value (date with time) according to the preferred date format.
     * @param context The Context that will be passed into the getDefaultSharedPreferences routine.
     * @param date The Date that will be displayed.
     * @return A String value that represents the passed in date (Date with time) according to the
     * preferred format.
     * @see DateUtil#displayDate(android.content.Context, java.util.Date)
     * displayDate returns a formatted string that does not also include any time value.
     */
    public static String displayDateTime(Context context, Date date) {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        String dateFormat = sharedPref.getString(SettingsActivity.PREF_DATE_FORMAT, "MM/dd/yyyy");
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat + " hh:mm a", Locale.US);
//        return sdf.format(date);
        return android.text.format.DateFormat.getTimeFormat(context).format(date);
    }

    public static String now() {
        Calendar cal = new GregorianCalendar();
        Date dat = cal.getTime();
        return convertDate(dat);
    }

    /**
     * Create a Date object based on a year, month, and day.
     * @param year Integer value representing the year.
     * @param month Integer value between 1 and 12.
     * @param day Integer value between 1 and 31.
     * @return Date object of the corresponding date.
     */
    public static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
