package com.android.root.popularmovies.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * Created by root on 5/15/17.
 */

public class ImageUtility {

    public static byte[] getImageBytes(Drawable drawable){
        Bitmap bitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public static Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }
    private static Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width:1;
        int height = drawable.getIntrinsicHeight();
        height = height >0 ? height:1;

        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

