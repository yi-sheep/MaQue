package com.gaoxianglong.maque.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

import java.io.ByteArrayOutputStream;

/**
 * 将图片转换  drawable <--> byte[]
 */
public class ConvertPictures {

    /**
     * 获取到图片转化后的byte[]格式的数据  可以直接存储
     * @param context
     * @param imageId
     * @return
     */
    public ContentValues getByteImage(Context context,int imageId) {
        Drawable drawable =context.getResources().getDrawable(imageId);
        ContentValues cv = new ContentValues();
        cv.put("image",getPicture(drawable));
        return cv;
    }

    //将drawable转换成可以用来存储的byte[]类型
    private byte[] getPicture(Drawable drawable) {
        if(drawable == null) {
            return null;
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /**
     *
     * @param bytes
     * @return
     */
    public Drawable getDrawableImage(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        Drawable drawable = bitmapDrawable;
        return drawable;
    }
}
