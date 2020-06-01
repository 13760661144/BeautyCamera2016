package cn.poco.campaignCenter.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Shine on 2017/1/14.
 */

public class ToastUtil {
   private static Toast mToast;

   public static void showToast(Context context, CharSequence text) {
       if (mToast == null) {
           mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
       } else {
           mToast.setText(text);
       }
       mToast.show();
   }

    public static void clear() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
