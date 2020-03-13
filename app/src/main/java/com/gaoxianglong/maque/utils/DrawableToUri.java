package com.gaoxianglong.maque.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

/**
 * 将drawable转换为URI
 */
public class DrawableToUri {
    public static Uri toUri(Context context,int drawable) {
        Resources resources = context.getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + resources.getResourcePackageName(drawable) +
                "/" + resources.getResourceTypeName(drawable) +
                "/" + resources.getResourceEntryName(drawable));
        return uri;
    }
}
