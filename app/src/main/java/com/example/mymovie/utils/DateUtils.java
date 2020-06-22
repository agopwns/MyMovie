package com.example.mymovie.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String dateFromLong(long time) {
        TimeZone tz;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd aaa hh:mm");
        tz = TimeZone.getTimeZone("Asia/Seoul");
        format.setTimeZone(tz);
        return format.format(new Date(time));
    }
}
