package cn.poco.home.home4.widget;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static android.icu.lang.UCharacter.JoiningGroup.HE;
import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.HeightPxxToPercent;

/**
 * Created by lgd on 2017/10/16.
 */

public class LoginTipDialog extends FullScreenDlg
{
    private ImageView mLogo;
    private LinearLayout mCenterParent;
    private TextView nextTime;
    private TextView loginIn;

    public LoginTipDialog(Activity activity)
    {
        super(activity, R.style.homeDialog);
        getWindow().setWindowAnimations(R.style.homeTipAnimation);
        initUi();
    }

    private void initUi()
    {
        FrameLayout.LayoutParams fl;
        m_fr.setBackgroundColor(Color.BLACK);
        m_fr.getBackground().setAlpha((int) (255 * 0.5f));
//        mParent = new FrameLayout(getContext());
//        fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER);
//        m_fr.addView(mParent,fl);
//        {
//            View mBk = new View(getContext());
//            mBk.setBackgroundDrawable(new RoundColorDrawable(Color.WHITE,HeightPxToPercent(30)));
////            mBk.setImageBitmap(cn.poco.tianutils.ImageUtils.MakeColorRoundBmp(Color.WHITE,HeightPxToPercent(568),HeightPxToPercent(455),HeightPxToPercent(30)));
//            mBk.setMinimumWidth(HeightPxToPercent(568));
//            mBk.setMinimumHeight(HeightPxToPercent(455));
////            mBk.setScaleType(ImageView.ScaleType.FIT_XY);
//            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            fl.topMargin = HeightPxToPercent(90);
//            fl.gravity = Gravity.CENTER;
//            mParent.addView(mBk,fl);

            mCenterParent = new LinearLayout(getContext());
            mCenterParent.setOrientation(LinearLayout.VERTICAL);
            mCenterParent.setMinimumWidth(HeightPxToPercent(568));
            mCenterParent.setMinimumHeight(HeightPxToPercent(455));
            RoundColorDrawable roundColorDrawable = new RoundColorDrawable(Color.WHITE,HeightPxToPercent(30));
            roundColorDrawable.setTopPadding(HeightPxToPercent(90));
            mCenterParent.setBackgroundDrawable(roundColorDrawable);
            mCenterParent.setGravity(Gravity.CENTER);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER_HORIZONTAL;
            fl.gravity = Gravity.CENTER;
            m_fr.addView(mCenterParent, fl);
            {
                LinearLayout.LayoutParams ll;
                mLogo = new ImageView(getContext());
                mLogo.setImageResource(R.drawable.home4_login_tip_logo);
                mLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ll = new LinearLayout.LayoutParams(HeightPxxToPercent(249),HeightPxxToPercent(269));
                mCenterParent.addView(mLogo,ll);

                TextView text;

                text = new TextView(getContext());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16f);
                text.setText("登录账号享好礼");
                text.setTextColor(0xff333333);
                text.getPaint().setFakeBoldText(true);
                ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.topMargin = HeightPxToPercent(26);
                mCenterParent.addView(text,ll);

                text = new TextView(getContext());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f);
                text.setText("登录账号，玩转积分！");
                text.setTextColor(0xff4c4c4c);
                ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.topMargin = HeightPxToPercent(26);
                mCenterParent.addView(text,ll);

                text = new TextView(getContext());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f);
                text.setText("福利社兑换大牌好礼呦～");
                text.setTextColor(0xff4c4c4c);
                ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.topMargin = HeightPxToPercent(14);
                mCenterParent.addView(text,ll);

                loginIn = new TextView(getContext());
                loginIn.setGravity(Gravity.CENTER);
                loginIn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14f);
                loginIn.setText("马上登录");
                loginIn.setBackgroundDrawable(new RoundColorDrawable(ImageUtils.GetSkinColor(0xffe75988),HeightPxToPercent(50)));
                loginIn.setTextColor(Color.WHITE);
                loginIn.getPaint().setFakeBoldText(true);
                loginIn.setMinWidth(HeightPxToPercent(450));
                loginIn.setMinHeight(HeightPxToPercent(100));
                loginIn.setOnTouchListener(mClickListener);
                ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.topMargin = HeightPxToPercent(44);
                mCenterParent.addView(loginIn,ll);

                nextTime = new TextView(getContext());
                nextTime.setGravity(Gravity.CENTER);
                nextTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12f);
                nextTime.setText("以后再说");
                nextTime.setTextColor(0xffa0a0a0);
                nextTime.getPaint().setFakeBoldText(true);
                nextTime.setMinHeight(HeightPxToPercent(90));
                nextTime.setMinWidth(HeightPxToPercent(100));
                nextTime.setOnTouchListener(mClickListener);
                ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mCenterParent.addView(nextTime,ll);
            }
//        }
    }
    private OnAnimationClickListener mClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if(v == loginIn){
                if(mCallBack != null){
                    mCallBack.onLogin();
                }
            }else if(v == nextTime){
                if(mCallBack != null){
                    mCallBack.onNextTime();
                }
            }
        }
    };

    class RoundColorDrawable extends Drawable{

        private Paint mPaint;
        private RectF mRect;
        private int mRoundRadius;
        private int topPadding = 0;
        public RoundColorDrawable(int color,int roundRadius)
        {
            super();
            this.mRoundRadius = roundRadius;
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(color);
            mRect = new RectF();
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom)
        {
            super.setBounds(left, top, right, bottom);
            mRect.set(left,top,right,bottom);
            if(bottom - top > topPadding){
                mRect.top = top+topPadding;
            }
        }

        @Override
        public void draw(@NonNull Canvas canvas)
        {
            canvas.drawRoundRect(mRect,mRoundRadius,mRoundRadius,mPaint);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha)
        {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter)
        {

        }

        @Override
        public int getOpacity()
        {
            return 0;
        }

        public void setTopPadding(int topPadding)
        {
            this.topPadding = topPadding;
        }
    }

    private LoginTipCallBack mCallBack;

    public void setCallBack(LoginTipCallBack callBack)
    {
        this.mCallBack = callBack;
    }

    public interface LoginTipCallBack{
        void onLogin();

        void onNextTime();
    }

}
