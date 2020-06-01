package cn.poco.camera;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessUtils extends ContentObserver
{
    private static BrightnessUtils sBrightnessUtils;

    private Context mContext;

    private static final float MAX_BRIGHTNESS = 205;//255.0f;//默认模式;
    private int defaultMode;//默认模式;
    private int defaultBrightness;//默认亮度1-255;
    private boolean initSuccess;//是否获取默认成功;
    private boolean changed;//是否已经改变;
    private float defaultScreenBrightness;//是负值

    public static BrightnessUtils getInstance()
    {
        if (sBrightnessUtils == null)
        {
            synchronized (BrightnessUtils.class)
            {
                if (sBrightnessUtils == null)
                {
                    sBrightnessUtils = new BrightnessUtils();
                }
            }
        }
        return sBrightnessUtils;
    }

    public BrightnessUtils()
    {
        super(new Handler(Looper.getMainLooper()));
    }

    public BrightnessUtils setContext(@NonNull Context context)
    {
        this.mContext = context;
        return sBrightnessUtils;
    }

    /**
     * 初始化, init 之前需要{@link #setContext(Context)}
     */
    public void init()
    {
        if (mContext == null || initSuccess) return;

        initSuccess = false;
        try
        {
            //判断是否是自动模式;
            //记录默认值;
            defaultMode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            defaultBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            if (mContext != null)
            {
                Window window = ((Activity) mContext).getWindow();
                WindowManager.LayoutParams wmParams = window.getAttributes();
                if (wmParams != null)
                {
                    defaultScreenBrightness = wmParams.screenBrightness;
                }
            }

            initSuccess = true;
        }
        catch (SettingNotFoundException e)
        {
            e.printStackTrace();
            initSuccess = false;
        }
    }

    /**
     * 时刻监听系统亮度改变事件
     */
    @Override
    public void onChange(boolean selfChange)
    {
        super.onChange(selfChange);
        if (mContext == null) return;
        int mode = -1;
        int brightness = 0;
        try
        {
            mode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            brightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL && mContext != null)
        {
            Window window = ((Activity) mContext).getWindow();
            WindowManager.LayoutParams wmParams = window.getAttributes();
            wmParams.screenBrightness = brightness / 255f;
            window.setAttributes(wmParams);
        }
    }

    public void registerBrightnessObserver()
    {
        if (mContext == null) return;
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, this);
    }

    public void unregisterBrightnessObserver()
    {
        if (mContext == null) return;
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    /**
     * 设置到最大;
     */
    public void setBrightnessToMax()
    {
        setBrightnessValue(MAX_BRIGHTNESS);
    }

    /**
     * @param value 0.0f ~ 255.0f
     */
    public void setBrightnessValue(float value)
    {
        if (initSuccess && mContext != null)
        {
            if (value <= defaultBrightness)
            {
                return;
            }
            try
            {
//                Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

                Window window = ((Activity) mContext).getWindow();
                WindowManager.LayoutParams wmParams = window.getAttributes();
                wmParams.screenBrightness = value / 255f;
                window.setAttributes(wmParams);
                changed = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 还原到默认，会销毁当前引用
     */
    public void resetToDefault()
    {
        if (initSuccess && changed && mContext != null)
        {
//            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, defaultBrightness);
//            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, defaultMode);

            Window window = ((Activity) mContext).getWindow();
            WindowManager.LayoutParams wmParams = window.getAttributes();
            wmParams.screenBrightness = defaultScreenBrightness;
            ((Activity) mContext).getWindow().setAttributes(wmParams);
            changed = false;
            clearAll();
        }
    }

    public void destroyContext()
    {
        mContext = null;
    }

    public void clearAll()
    {
        destroyContext();
        initSuccess = false;
        sBrightnessUtils = null;
    }
}
