package cn.poco.camera3.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 *
 * Created by Gxx on 2017/9/19.
 */

public class LanguageUtil
{
    public static boolean checkSystemLanguageIsChinese(Context context)
    {
        // 检查系统语言
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        }
        else
        {
            locale = context.getResources().getConfiguration().locale;
        }

        return locale.getLanguage().endsWith("zh");
    }


    public static void SetDefaultLocaleLanguage(Context context)
    {
        if (context == null) return;
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(Locale.getDefault());
        resources.updateConfiguration(configuration, displayMetrics);
    }

    public static void SetLanguage(Locale language, Context context)
    {
        if (context == null) return;
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        if (language == null) language = Locale.getDefault();
        configuration.setLocale(language);
        resources.updateConfiguration(configuration, displayMetrics);
    }

}
