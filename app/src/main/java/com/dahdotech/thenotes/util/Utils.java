package com.dahdotech.thenotes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyLocalizedPattern("hh:mm aaa, MMM dd, yyyy");

        return simpleDateFormat.format(date);
    }
}
