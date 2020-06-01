package cn.poco.campaignCenter.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shine on 2016/12/24.
 */

public class ClickEffectUtil {

    public static void addTextViewClickEffect(TextView textView, int colorNormal, int colorPressed) {
        ColorStateList colorStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        colorPressed,
                        colorNormal});
        textView.setTextColor(colorStates);
    }

    public static void addImageViewClickEffect(ImageView imageView, int normalDrawalbeId, int pressedDrawableId) {
        Resources resources = imageView.getResources();
        StateListDrawable stateListDrawable = new StateListDrawable();
        Bitmap normalBitmap = BitmapFactory.decodeResource(resources, normalDrawalbeId);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(resources, pressedDrawableId);

        stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, new BitmapDrawable(resources,pressedBitmap));
        stateListDrawable.addState(new int[] {android.R.attr.state_checkable}, new BitmapDrawable(resources,pressedBitmap));
        stateListDrawable.addState(new int[] {android.R.attr.state_focused}, new BitmapDrawable(resources,pressedBitmap));
        stateListDrawable.addState(new int[] {}, new BitmapDrawable(resources, normalBitmap));
        imageView.setImageDrawable(stateListDrawable);
    }

    public static void addViewClickEffect(View view, int colorNormal, int colorPressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {android.R.attr.state_checkable}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {android.R.attr.state_focused}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {}, new ColorDrawable(colorNormal));
        view.setBackgroundDrawable(stateListDrawable);
    }

    public static StateListDrawable makeStateListDrawable(int colorNormal, int colorPressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {android.R.attr.state_checkable}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {android.R.attr.state_focused}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[] {}, new ColorDrawable(colorNormal));
        return stateListDrawable;
    }












}
