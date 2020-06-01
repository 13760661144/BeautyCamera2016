package cn.poco.campaignCenter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.lang.reflect.Field;

/**
 * Created by admin on 2016/10/19.
 */

public class AndroidUtil {

    public static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must call this method on the main thread");
        }
    }

    public static void assertBackgroundThread() {
        if (!isOnBackgroundThread()) {
            throw new IllegalArgumentException("You must call this method on the background thread");
        }
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isOnBackgroundThread() {
       return !isOnMainThread();
    }

    // 通过反射设置动画的时效
    public static void setProgressBarAnimationDuration(ProgressBar progressBar, int duration) {
        if (progressBar == null) {
            return;
        }

        try {
            Field mCursorDrawableRes = ProgressBar.class.getDeclaredField("mDuration");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.setInt(progressBar, duration);
        } catch (Throwable e) {

        }
    }

    public static void openFileChooser(Activity activity, int requestCode) {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        activity.startActivityForResult(chooserIntent, requestCode);
    }




    public static int convertDpToPixel(Context context, float dp) {
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
        return pixel;
    }











}
