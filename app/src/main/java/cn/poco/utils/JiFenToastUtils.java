package cn.poco.utils;

import android.content.Context;

import cn.poco.widget.CustomerPointToast;

/**
 * 积分提示工具类
 */
public class JiFenToastUtils {

    /**
     * Toast提示如下: "text1 + text2"
     * @param context
     * @param text1
     * @param text2
     */
    public static void show(Context context, String text1, String text2) {
        CustomerPointToast toast = new CustomerPointToast(context);
        toast.setText(text1, text2);
        toast.show();
    }
}
