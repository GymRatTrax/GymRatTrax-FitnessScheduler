package com.gymrattrax.scheduler.service;


import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;

import com.gymrattrax.scheduler.activity.ViewScheduleActivity;

import java.util.Date;
import java.util.TimeZone;

public class CalendarService extends ViewScheduleActivity {
    private static long eventID;
    private static byte[] details;
    private Date currentDay;
    private double time;
    private long ID;
    private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";


    public CalendarService() {
    }

    public Date getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(Date currentDay) {
        this.currentDay = currentDay;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }


    public String name = "GymRatTrax Workout Schedule";

    public String accountName = Account.class.getName();

    // Projection array
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                         // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,       // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                // 3

    };

    // Indices for projection
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    // Insert GRT calendar
    public static Uri createNewCalendar(Context ctx, String name, String accountName) {
        Uri target = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "ACCOUNT_TYPE_LOCAL").build();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, "ACCOUNT_TYPE_LOCAL");
        values.put(CalendarContract.Calendars.NAME, name);
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0x00FF00);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_ROOT);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().toString());
        values.put(CalendarContract.Calendars.CAN_PARTIALLY_UPDATE, 1);
        values.put(CalendarContract.Calendars.ALLOWED_REMINDERS, CalendarContract.Reminders.METHOD_DEFAULT);
        values.put(CalendarContract.Calendars.CAL_SYNC8, System.currentTimeMillis());
        Uri uri = ctx.getContentResolver().insert(target, values);
        return uri;
    }

    public long id = getID();

    // insert GTR workout event
//    public static long addEvent(Context ctx, String accountName, String name, String data) {
//        Uri target = Uri.parse("content://com.android.calendar/events");
//// Uri target = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
//        target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
//                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
//                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "ACCOUNT_TYPE_LOCAL").build();
//
//        long startMillis = 0;
//        long endMillis = 0;
//
//        // get workout details
//        String[] divData = data.split("!", 3);
//        String details = divData[0];
//        String[] divDate = divData[1].split("/", 3);
//        String[] divTime = divData[2].split(":", 2);
//
//        int month = Integer.parseInt(divDate[0]);
//        int day = Integer.parseInt(divDate[1]);
//        int year = Integer.parseInt(divDate[2]);
//        int hour =  Integer.parseInt(divTime[0]);
//        int minute =  Integer.parseInt(divTime[1]);
//
//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(year, month, day, hour, minute);
//        startMillis = beginTime.getTimeInMillis();
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(year, month, day, hour, minute + 10);
//        endMillis = endTime.getTimeInMillis();
//
//        // set event details
//        ContentValues cv = new ContentValues();
//        cv.put(CalendarContract.Events.CALENDAR_ID, 1);
//        cv.put(CalendarContract.Events.DTSTART, startMillis);
//        cv.put(CalendarContract.Events.DURATION, "+P1H");
//        cv.put(CalendarContract.Events.TITLE, name);
//        cv.put(CalendarContract.Events.DESCRIPTION, details);
//        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
//
//        Uri newEvent = ctx.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, cv);
//        CalendarService.eventID = Long.parseLong(newEvent.getLastPathSegment());
//
//        return Long.parseLong(newEvent.getLastPathSegment());
//    }

//
//    public static boolean deleteEvent(Context ctx, String accountName, String name, long eventID) {
//
//        return true;
//    }


    public Uri.Builder getAllEvents() {
        Uri.Builder builder = Uri.parse(getCalendarUriBase() + "/instances/when").buildUpon();
        long now = new Date().getTime();
        ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS);
        ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS);
        return builder;
    }

    private String getCalendarUriBase() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{"_id", "displayname"}, null, null, null);
        cursor.moveToFirst();
        String[] CalNames = new String[cursor.getCount()];
        int[] CalIds = new int[cursor.getCount()];
        for (int i = 0; i < CalNames.length; i++) {
            CalIds[i] = cursor.getInt(0);
            CalNames[i] = cursor.getString(1);
            cursor.moveToNext();
        }
        cursor.close();

        return null;
    }

//    public static Intent viewEvent() {
//    public Intent viewEvent() {
//        long eventID = 208;
//        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
//        return new Intent(Intent.ACTION_VIEW)
//                .setData(uri);
//    }


    public long getID() {
        return ID;
    }

}