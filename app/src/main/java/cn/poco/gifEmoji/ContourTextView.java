package cn.poco.gifEmoji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.widget.TextView;

import java.lang.reflect.Field;

import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2017/5/27.
 */

public class ContourTextView extends AppCompatTextView
{
    private Paint mPaint;
    private String mText;
    private int mInnerColor, mOuterColor;

    public ContourTextView(Context context)
    {
        super(context);
        mPaint = getPaint();
    }

    public void setColor(int outerColor, int innerColor)
    {
        mOuterColor = outerColor;
        mInnerColor = innerColor;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mText = getText().toString();
        if(mText != null && !mText.equals(""))
        {
            setTextColorUseReflection(mOuterColor);
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(3)); // 描边宽度
            mPaint.setStyle(Paint.Style.STROKE); // 描边种类
            super.onDraw(canvas);

            setTextColorUseReflection(mInnerColor);
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL);
        }
        super.onDraw(canvas);
    }

    /**
     * 使用反射的方法进行字体颜色的设置
     * @param color
     */
    private void setTextColorUseReflection(int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mPaint.setColor(color);
    }
}
