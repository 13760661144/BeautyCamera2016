package cn.poco.camera3.config;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;

/**
 * toast 配置信息
 * Created by Gxx on 2017/11/6.
 */

public class MsgToastConfig
{
    private int mKey;

    public @interface Key
    {
        int VIDEO_DURATION = 1; // 视频时长
        int TAB_TITLE = 1 << 2; // 标题
        int STICKER_ACTION = 1 << 3; // 贴纸素材动作
        int SETTING = 1 << 4; // 设置
        int PREVIEW_PAGE_SAVE_TOAST = 1 << 5; // 预览页保存提示
    }

    private int mGravity;
    private int mXOffset;
    private int mYOffset;
    private int mTextUnit;
    private int mTextSize;
    private int mTextColor;
    private Typeface mTextTypeFace;

    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;

    private int mTextBGResID;
    private float mTextBGAlpha;
    private Drawable mTextBGDrawable;

    private float mShadowRadius;
    private float mShadowDX;
    private float mShadowDY;
    private int mShadowColor;

    public MsgToastConfig(@Key int key)
    {
        mKey = key;
        setGravity(Gravity.START | Gravity.TOP, 0, 0);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setTextColor(Color.BLACK);
        setTextBGAlpha(1f);
    }

    public int getKey()
    {
        return mKey;
    }

    public void setGravity(int gravity, int xOffset, int yOffset)
    {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0)
        {
            gravity |= Gravity.START;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0)
        {
            gravity |= Gravity.TOP;
        }

        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
    }

    public void setTextSize(int unit, int size)
    {
        mTextUnit = unit;
        mTextSize = size;
    }

    public void setTextColor(int color)
    {
        mTextColor = color;
    }

    public void setTextPadding(int left, int top, int right, int bottom)
    {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
    }

    public void setShadow(float radius, float dx, float dy, int color)
    {
        mShadowRadius = radius;
        mShadowDX = dx;
        mShadowDY = dy;
        mShadowColor = color;
    }

    public void setTextBGDrawable(Drawable drawable)
    {
        mTextBGDrawable = drawable;
    }

    public void setTextBG(int resID)
    {
        mTextBGResID = resID;
    }

    public void setTextBGAlpha(float alpha)
    {
        mTextBGAlpha = alpha;
    }

    public void setTextTypeFace(Typeface typeFace)
    {
        this.mTextTypeFace = typeFace;
    }

    public int getGravity()
    {
        return mGravity;
    }

    public int getXOffset()
    {
        return mXOffset;
    }

    public int getYOffset()
    {
        return mYOffset;
    }

    public int getTextUnit()
    {
        return mTextUnit;
    }

    public int getTextSize()
    {
        return mTextSize;
    }

    public int getTextColor()
    {
        return mTextColor;
    }

    public int getPaddingLeft()
    {
        return mPaddingLeft;
    }

    public int getPaddingTop()
    {
        return mPaddingTop;
    }

    public int getPaddingRight()
    {
        return mPaddingRight;
    }

    public int getPaddingBottom()
    {
        return mPaddingBottom;
    }

    public Drawable getTextBGDrawable()
    {
        return mTextBGDrawable;
    }

    public int getTextBGResID()
    {
        return mTextBGResID;
    }

    public float getTextBGAlpha()
    {
        return mTextBGAlpha;
    }

    public Typeface getTextTypeFace()
    {
        return mTextTypeFace;
    }

    public float getShadowRadius()
    {
        return mShadowRadius;
    }

    public float getShadowDX()
    {
        return mShadowDX;
    }

    public float getShadowDY()
    {
        return mShadowDY;
    }

    public int getShadowColor()
    {
        return mShadowColor;
    }
}
