package com.gaoxianglong.maque.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;

public class ToDate {
    /**
     * 将时间戳转换为时间
     * <p>
     * s就是时间戳
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String stampToDate(String date) {
        if (date.length() == 10) {
            date = date + "000";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //如果它本来就是long类型的,则不用写这一步
        long lt = new Long(date);
        res = simpleDateFormat.format(new Date(lt));
        return res;
    }
}
