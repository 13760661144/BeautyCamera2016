package cn.poco.featuremenu.util;

import android.graphics.Bitmap;

import cn.poco.image.filter;

/**
 * Created by Simon Meng on 2017/10/13.
 * Guangzhou Beauty Information Technology Co.,Ltd
 */

public class BitmapUtils {


    public static Bitmap scaleAndFilterBitmap(Bitmap srcBitmap, int dstWidth, int dstHeight, int color) {
        Bitmap result = null;
        if (srcBitmap != null && !srcBitmap.isRecycled()) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
            Bitmap filteredBitmap = filter.fakeGlassBeauty(scaledBitmap, color);
            result = filteredBitmap;
        }
        return result;
    }



}
