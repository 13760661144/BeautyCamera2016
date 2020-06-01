package cn.poco.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 自定义Toast(自定义显示时长)
 * Created by MarkChan on 16/6/13.
 */
public class CustomToast extends Toast {

    private static final String TAG = CustomToast.class.getName();

    // Toast.LENGTH_LONG（3.5秒）和 Toast.LENGTH_SHORT（2秒）
    private int mDuration = 2000;
    private boolean isStart;
    private CountDownTimer countDownTimer;

    private int defaultAnimationId = android.R.style.Animation_Toast;//default
    private int animResId = defaultAnimationId;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}. <br/>
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomToast(Context context) {
        super(context);
    }

    /**
     * Custom toast animation
     *
     * @param context
     * @param animResId @style/Custom.Toast.Animation <br/>
     *                  <style name="Custom.Toast.Animation"> <br/>
     *                  <item name="android:windowEnterAnimation">@anim/custom_toast_enter</item> <br/>
     *                  <item name="android:windowExitAnimation">@anim/custom_toast_exit</item> <br/>
     *                  </style> <br/>
     */
    public CustomToast(Context context, int animResId) {
        this(context);
        this.animResId = animResId;
    }

    private int getAnimation() {
        return animResId;
    }

    /**
     * 反射字段
     *
     * @param clazz     要反射的类
     * @param fieldName 要反射的字段名称
     * @param object    要反射的对象
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static Object getField(Class<?> clazz, String fieldName, Object object) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }

    @Override
    public final void setDuration(int duration) {
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            super.setDuration(duration);
        } else {
            super.setDuration(Toast.LENGTH_SHORT);
            if (duration > 0) {
                mDuration = duration;
            }
            isStart = true;

            if (animResId != defaultAnimationId) {
                try {
                    Object mTN = getField(this.getClass().getSuperclass(), "mTN", this);
                    if (mTN != null) {
                        Object mParams = getField(mTN.getClass(), "mParams", mTN);
                        if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                            params.windowAnimations = getAnimation();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public final void show() {
        super.show();
        if (isStart) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (countDownTimer == null) {
                countDownTimer = new CountDownTimer(mDuration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        show();
                    }

                    @Override
                    public void onFinish() {
                        hide();
                    }
                };
                countDownTimer.start();
            }
            isStart = false;
        }
    }

    private final void hide() {
        cancel();
        isStart = true;
    }

    @Override
    public final void cancel() {
        super.cancel();
    }
}
