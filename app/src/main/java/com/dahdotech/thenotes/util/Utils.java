package com.dahdotech.thenotes.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dahdotech.thenotes.ui.MainActivity;

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

    public void collapseKeyboard(Activity activity){
        View view = activity.getCurrentFocus();

        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void undoCollapseKeyboard(Activity activity){
        View view = activity.getCurrentFocus();

        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
        }
    }

}
