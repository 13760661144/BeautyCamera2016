package cn.poco.web.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/11/8.
 */

public class MutipleLineTextLayout extends View {
    private TextPaint mPaint;
    private Drawable mBlackDot;
    private StaticLayout mTextLayout;

    private String mText;

    public MutipleLineTextLayout(Context context) {
        super(context);
        initData();
    }

    private void initData() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(convertDpToPixel(14));
        mPaint.setColor(0xff666666);
        mBlackDot = getResources().getDrawable(R.drawable.web_update_version_blackdot);
    }

    public void setUpText(String text) {
        mText = text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int textLayoutWidth = this.getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - ShareData.PxToDpi_xhdpi(8);
        mTextLayout = new StaticLayout(mText, mPaint, textLayoutWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f ,false);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mTextLayout.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int drawableTopPosition = (mTextLayout.getLineBottom(0) - mBlackDot.getIntrinsicHeight()) / 2;
        mBlackDot.setBounds(getPaddingLeft(), drawableTopPosition, getPaddingLeft() + mBlackDot.getIntrinsicWidth(), drawableTopPosition + mBlackDot.getIntrinsicHeight());
        mBlackDot.draw(canvas);

        canvas.save();
        int textX = getPaddingLeft() + mBlackDot.getIntrinsicWidth() + ShareData.PxToDpi_xhdpi(10);
        canvas.translate(textX, 0);
        mTextLayout.draw(canvas);
        canvas.restore();
    }

    private int convertDpToPixel(float dp) {
        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
        return pixel;
    }

}
