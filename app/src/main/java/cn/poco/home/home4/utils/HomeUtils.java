package cn.poco.home.home4.utils;

import android.content.Context;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.IAdSkin;

import java.lang.reflect.Method;
import java.util.Calendar;

import cn.poco.adMaster.HomeAd;

/**
 * Created by lgd on 2017/6/2.
 */

public class HomeUtils
{
    private static final String TAG = "HomeUtils";

    private static boolean sIsEMUISystem = false;

    static public String getSkinPath(Context context, String adResId)
    {
        String path = null;
        if(!TextUtils.isEmpty(adResId))
        {
            AbsAdRes res = HomeAd.GetOneHomeRes(context, adResId);
            if (res != null && isInPeriodOfValidity(res.mBeginTime, res.mEndTime))
            {
                if (res instanceof IAdSkin)
                {
                    path = ((IAdSkin) res).getBg();
                }
            }
        }
        return path;
    }

    static protected boolean isInPeriodOfValidity(long beginTime, long endTime)
    {
        boolean b = false;
        long time = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(beginTime);
        Calendar to = Calendar.getInstance();
        to.setTimeInMillis(endTime);
//            Log.i(TAG, "getYslResId mCalendar: "+mCalendar.get(Calendar.YEAR)+" "+mCalendar.get(Calendar.MONTH)+" "+mCalendar.get(Calendar.DAY_OF_MONTH)+" "+mCalendar.get(Calendar.HOUR_OF_DAY)+" "+mCalendar.get(Calendar.MINUTE));
//            Log.i(TAG, "getYslResId from : "+from.get(Calendar.YEAR)+" "+from.get(Calendar.MONTH)+from.get(Calendar.DAY_OF_MONTH)+" "+from.get(Calendar.HOUR_OF_DAY)+" "+from.get(Calendar.MINUTE));
//            Log.i(TAG, "getYslResId to : "+to.get(Calendar.YEAR)+" "+to.get(Calendar.MONTH)+" "+to.get(Calendar.DAY_OF_MONTH)+" "+to.get(Calendar.HOUR_OF_DAY)+" "+from.get(Calendar.MINUTE));

        if (calendar.after(from) && calendar.before(to))
        {
            b = true;
        }
        return b;
    }

    static public final int TOP_TO_BOTTOM = 1;
    static public final int LEFT_TO_RIGHT = 2;
    static public final int RIGHT_TO_LEFT = 3;
    static public final int BOTTOM_TO_TOP = 4;
    static public final int FADE_OUT_IN = 5;

    static public Animation getAnimationType(@IntRange(from = 1, to = 5) int direction)
    {
        int distance = PercentUtil.WidthPxToPercent(40);
        int fadeOutDuration = 1500;
        int fadeInTranslateDuration = 1200;
        final AnimationSet animationSet = new AnimationSet(true);
        Animation animation2 = null;
        switch (direction)
        {
            case TOP_TO_BOTTOM:
                animation2 = new TranslateAnimation(0, 0, 0, distance);
                break;
            case LEFT_TO_RIGHT:
                animation2 = new TranslateAnimation(0, distance, 0, 0);
                break;
            case RIGHT_TO_LEFT:
                animation2 = new TranslateAnimation(0, -distance, 0, 0);
                break;
            case BOTTOM_TO_TOP:
                animation2 = new TranslateAnimation(0, 0, 0, -distance);
                break;
            case FADE_OUT_IN:
                animation2 = new  AlphaAnimation(1, 0);
                break;
            default:
                animation2 = null;
                break;
        }
        Animation animation1 = new AlphaAnimation(0, 1);
        animation1.setDuration(fadeOutDuration);
        animationSet.addAnimation(animation1);
        if (animation2 != null)
        {
            animation2.setDuration(fadeInTranslateDuration);
            animation2.setStartOffset(fadeOutDuration);
            animationSet.addAnimation(animation2);

            animation1 = new AlphaAnimation(1, 0);
            animation1.setDuration(fadeInTranslateDuration);
            animation1.setStartOffset(fadeOutDuration);
            animationSet.addAnimation(animation1);
        }
        return animationSet;
    }

    private static boolean sHasCheck = false;
    static public boolean isEMUISystem()
    {
        if(!sHasCheck){
            sHasCheck = true;
            if(OSUtils.getRomType() == OSUtils.ROM_TYPE.EMUI)
            {
                sIsEMUISystem = true;
//                Log.i(TAG, "isEMUISystem: "+android.os.Build.MODEL);
//                Log.i(TAG, "isEMUISystem: " + getEMUI());
            }
        }
        return sIsEMUISystem;
    }

    static public boolean isShowAlpha()
    {
        boolean b = false;
        String emui = getEMUI();
        if (!TextUtils.isEmpty(emui))
        {
            String[] texts = emui.split(".");
            if (texts.length > 1)
            {
                int i1 = Integer.parseInt(texts[0]);
                int i2 = Integer.parseInt(texts[2]);
                if (i1 == 5 && (i2 == 0 || i2 == 1)){
                    b = true;
                }
            }
        }
        return b;
    }


    private static String getEMUI() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

}
