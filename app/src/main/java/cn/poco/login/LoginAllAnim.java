package cn.poco.login;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.poco.home.home4.Home4Page;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;

public class LoginAllAnim {

    private static int m_animtime = 400;

    private static int m_lastHeight = 0;

    private static boolean m_loginPageStarted = false;

    private static boolean m_willStartLoginBtnAnim = true;

    public static void loginPageAnim(FrameLayout view1,FrameLayout view2,FrameLayout view3)
    {
//        SetBK(view2);
        if(view1 == null || view2 == null || view3 == null)
        {
            return;
        }
        if(!m_loginPageStarted || m_lastHeight == 0)
        {
            alphaAnim(view1);
            translateAnimUp2Down(view1);
//            translateAnimDown2Up(view2);
            m_lastHeight = view1.getHeight();
            m_loginPageStarted = true;
        }
        else
        {
            alphaAnim(view3);
            transitionAnim(view1,view2,m_lastHeight,view1.getHeight());
            m_lastHeight = view1.getHeight();
        }
    }

    public static void getVCodePageAnim(FrameLayout view1,FrameLayout view2,FrameLayout view3)
    {
//        SetBK(view2);
        if(view1 == null || view2 == null || view3 == null)
        {
            return;
        }
        if(m_loginPageStarted && m_lastHeight != 0)
        {
            alphaAnim(view3);
            transitionAnim(view1,view2,m_lastHeight,view1.getHeight());
            m_lastHeight = view1.getHeight();
        }
        else
        {
            m_loginPageStarted = true;
            m_lastHeight = view1.getHeight();
        }
    }


    public static void registerInfoPageAnim(FrameLayout view1,FrameLayout view2,FrameLayout view3)
    {
//        SetBK(view2);
        if(view1 == null || view2 == null || view3 == null)
        {
            return;
        }
        if(m_loginPageStarted && m_lastHeight != 0)
        {
            alphaAnim(view3);
            transitionAnim(view1,view2,m_lastHeight,view1.getHeight());
            m_lastHeight = view1.getHeight();
        }
    }

    public static void reSetPSWPageAnim(FrameLayout view1,FrameLayout view2,FrameLayout view3)
    {
//        SetBK(view2);
        if(view1 == null || view2 == null || view3 == null)
        {
            return;
        }
        if(m_loginPageStarted && m_lastHeight != 0)
        {
            alphaAnim(view1);
            transitionAnim(view1,view2,m_lastHeight,view1.getHeight());
            m_lastHeight = view1.getHeight();
        }
    }


    public static void translateAnimUp2Down(View view1)
    {
       if(view1 != null)
       {
           m_lastHeight = 0;
           int viewHeight = view1.getHeight();
           int realPosH = getViewTopMagin(view1);
           int startY = -viewHeight;
           int endY = realPosH;
           ObjectAnimator animator = ObjectAnimator.ofFloat(view1,"translationY",startY,endY);
           animator.setDuration(m_animtime);
           animator.start();
       }
    }


    public static void loginpageResetAnim(View view1)
    {
        if(view1 != null)
        {
            m_lastHeight = 0;
            int viewHeight = view1.getHeight();
            int realPosH = getViewTopMagin(view1);
            int startY = -viewHeight;
            int endY = realPosH;
            ObjectAnimator animator = ObjectAnimator.ofFloat(view1,"translationY",endY,startY);
            animator.setDuration(300);
            animator.start();
        }
    }


    public static void translateAnimDown2UpLoginBtnReset(final LinearLayout view,final int endBottomY)
    {
            if(view != null && view.getHeight() > 0)
            {
                m_willStartLoginBtnAnim = false;
                int startY = -view.getHeight();
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(endBottomY,startY);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) view.getLayoutParams();
//                        float value = (float) valueAnimator.getAnimatedValue();
                        fl.bottomMargin = (int) valueAnimator.getAnimatedValue();
                        view.setLayoutParams(fl);
                    }
                });
                valueAnimator.setDuration(300);
                valueAnimator.start();
            }
    }


    public static void translateAnimDown2Up(FrameLayout view)
    {
        int viewHeight = view.getHeight();
        int realPosH = getViewTopMagin(view);
        int startY = realPosH + viewHeight;
        int endY = realPosH;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,View.Y,startY,endY);
        animator.setDuration(m_animtime);
        animator.start();
    }


    public static void translateAnimDown2UpLoginBtn(FrameLayout view,int startY,int endY)
    {
        if(m_willStartLoginBtnAnim) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,View.Y,startY,endY);
            animator.setDuration(m_animtime);
            animator.start();
        }
        else
        {
            alphaAnim(view);
        }
    }

    public static void translateAnimDown2UpLoginBtn(final LinearLayout view,final int endBottomY)
    {
//        if(m_willStartLoginBtnAnim) {
            if(view != null && view.getHeight() > 0)
            {
                m_willStartLoginBtnAnim = false;
                int startY = -view.getHeight() - endBottomY;
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(startY,endBottomY);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                        float value = (float) valueAnimator.getAnimatedValue();
                        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) view.getLayoutParams();
                            fl.bottomMargin = (int) valueAnimator.getAnimatedValue();
                            view.setLayoutParams(fl);
                    }
                });
                valueAnimator.setDuration(m_animtime);
                valueAnimator.start();
            }
//        }
//        else
//        {
//            if(view != null)
//            {
//                FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) view.getLayoutParams();
//                fl.bottomMargin = endBottomY;
//                view.setLayoutParams(fl);
//            }
//        }
    }

    public static void alphaAnim(View view)
    {
        if(view != null)
        {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,"alpha",0.0f,1f);
            animator.setDuration(m_animtime + 200);
            animator.start();
        }
    }

    public static int getViewTopMagin(View view)
    {
        int out = 0;
       if(view != null)
       {
           if(view.getLayoutParams() instanceof FrameLayout.LayoutParams)
           {
               FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) view.getLayoutParams();
               out = fl.topMargin;
           }
           else if(view.getLayoutParams() instanceof LinearLayout.LayoutParams)
           {
               LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) view.getLayoutParams();
               out = ll.topMargin;
           }
           else if(view.getLayoutParams() instanceof RelativeLayout.LayoutParams)
           {
               RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) view.getLayoutParams();
               out = rl.topMargin;
           }
       }
        return out;
    }

    public static void transitionAnim(final View view1, final View view2, final int startHeight, final int endHeight)
    {
        if(view1 == null || view2 == null)
        {
            return;
        }
        final int dis = startHeight - endHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) view1.getLayoutParams();
                float value = (float) animation.getAnimatedValue();
                int tempHeight = (int) (startHeight - (dis * value));
                fl.height = tempHeight;
                view1.setLayoutParams(fl);

                fl = (FrameLayout.LayoutParams) view2.getLayoutParams();
                fl.topMargin = tempHeight;
                fl.height = ShareData.m_screenHeight - tempHeight;
                view2.setLayoutParams(fl);
            }
        });
        valueAnimator.start();
    }

    public static void SetBK(View view)
    {
        Bitmap bk = null;
        if(view != null && Home4Page.s_maskBmpPath != null && Home4Page.s_maskBmpPath.length() > 0)
        {
            bk =  cn.poco.imagecore.Utils.DecodeFile((String) Home4Page.s_maskBmpPath,null);
            bk = MakeBmpV2.CreateFixBitmapV2(bk,0,MakeBmpV2.FLIP_NONE,0,ShareData.m_screenWidth,ShareData.m_screenHeight - view.getHeight(), Bitmap.Config.ARGB_8888);
            if(bk != null)
            {
                view.setBackgroundDrawable(new BitmapDrawable(bk));
            }
            else
            {
                view.setBackgroundColor(Color.WHITE);
            }
        }
        else
        {
            if(view != null)
            {
                view.setBackgroundColor(Color.WHITE);
            }
        }
    }

    //重置动画相关的数据
    public static void ReSetLoginAnimData()
    {
        m_loginPageStarted = false;
        m_lastHeight = 0;
        m_willStartLoginBtnAnim = true;
    }

}
