package com.dahdotech.thenotes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public String longDateFormat(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyLocalizedPattern("hh:mm aaa, MMM dd, yyyy");

        return simpleDateFormat.format(date);
    }

    public String shortDateFormat(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyLocalizedPattern("hh:mm aaa");

        return simpleDateFormat.format(date);
    }
}
