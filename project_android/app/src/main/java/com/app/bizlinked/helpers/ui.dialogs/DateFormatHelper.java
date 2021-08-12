package com.app.bizlinked.helpers.ui.dialogs;

import android.text.format.DateFormat;

import com.app.bizlinked.helpers.common.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatHelper {

    public static String formatDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(newDate);
    }

    public static String ConfirmPaymentformatDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MM-yy");
        return format.format(newDate);
    }
/*
    public static String formatDate(String date)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
		Date newDate = null;
		try {
			newDate = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		format = new SimpleDateFormat("dd MMM, YYYY");
		return format.format(newDate);
	}
*/


    public static String formatTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        format = new SimpleDateFormat("hh:mm");
        return format.format(newDate);
    }

    public static String format_AM_PM_Time(String time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        format = new SimpleDateFormat("hh:mm a");
        return format.format(newDate);
    }


    public static String changeServerFormatDate(String date) {
        if (!Utils.isEmptyOrNull(date)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date newDate = null;
                newDate = format.parse(date);
                format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                return format.format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return date;
    }


    public static String changeServerToOurFormatDate(String date) {

        if (!Utils.isEmptyOrNull(date)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date newDate = null;
                newDate = format.parse(date);
                format = new SimpleDateFormat("dd MMM yyyy");
                return format.format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return date;

    }



    public static String convertServerToOurFormatDate(String date) {

        if (!Utils.isEmptyOrNull(date)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date newDate = format.parse(date);
                return new SimpleDateFormat("dd MMM yyyy",  Locale.getDefault()).format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return date;

    }

    public static String convertDateToFormattedDate(String date) {

        if (!Utils.isEmptyOrNull(date)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date newDate = format.parse(date);
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  Locale.getDefault()).format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return date;

    }

    public static String convertDateToFormattedDate(String date, String formatDate, String convertedDateFormat) {

        if (!Utils.isEmptyOrNull(date)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(formatDate, Locale.getDefault());
                Date newDate = format.parse(date);
                return new SimpleDateFormat(convertedDateFormat,  Locale.getDefault()).format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return date;

    }


    public static String getFormattedDate(long time, String format) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
//        String date = DateFormat.format("dd MMM yyyy HH:mm:ss a", cal).toString();
        String date = DateFormat.format(format, cal).toString();
        return date;
    }



    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yyyy HH:mm:ss a", cal).toString();
        return date;
    }

    public static String convert24HoursTo12HoursTime(String time) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.US);
            final Date dateObj = sdf.parse(time);
            //System.out.println(dateObj);
            return new SimpleDateFormat("K:mm a", Locale.US).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static Date convertStringToServerFormattedDate(String dateInString) {
        if (!Utils.isEmptyOrNull(dateInString)) {

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                Date newDate = format.parse(dateInString);
                return newDate;
//                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  Locale.getDefault()).format(newDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return null;

    }

    public static String convertDateToServerFormattedString(Date dateObj) {

        if (dateObj != null) {

            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",  Locale.getDefault()).format(dateObj);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return null;

    }
}
