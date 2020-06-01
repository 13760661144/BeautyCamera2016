package cn.poco.campaignCenter.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/11/23.
 */

public class EditUserInfoBtn extends View {
    private Paint mOuterPaint, mInnerPaint;
    private Drawable mDrawable;
//    private Path mCirclePath;
//    private Path mInnerPath;

    public EditUserInfoBtn(Context context) {
        super(context);
        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setStyle(Paint.Style.FILL);
        mOuterPaint.setColor(Color.parseColor("#ffffff"));

        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(Color.parseColor("#e75887"));

        mDrawable = getResources().getDrawable(R.drawable.homes_edit_userinfo_btn);

//        mCirclePath = new Path();
//        setBackgroundColor(Color.WHITE);
    }

    public void setDrawable(int resId) {
        mDrawable = getResources().getDrawable(resId);
    }

    public void setThemeColor(String color) {
        mInnerPaint.setColor(Color.parseColor(color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, ShareData.PxToDpi_xhdpi(19), mOuterPaint);
        canvas.drawCircle(getWidth() / 2, getHeight()/ 2, ShareData.PxToDpi_xhdpi(17), mInnerPaint);
        int leftPosition = (this.getWidth() - mDrawable.getIntrinsicWidth()) / 2;
        int topPosition = (this.getHeight() - mDrawable.getIntrinsicHeight()) / 2;
        int rightPosition = leftPosition + mDrawable.getIntrinsicWidth();
        int bottomPostion = topPosition + mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(leftPosition, topPosition, rightPosition, bottomPostion);
        mDrawable.draw(canvas);
//        mCirclePath.addCircle(getWidth() / 2, getHeight() / 2, );




    }
}
