package cn.poco.camera3.ui.customization;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.TailorMadeConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import my.beautyCamera.R;
public class TailorMadeViewV2 extends FrameLayout
{
    private OnTailorMadeStateListener mStateListener;

    public interface OnTailorMadeStateListener
    {
        void onShowGuide(); // 指引动画结束后回调

        void onStartDismissGuide(); // "以后再说" 回调，动画前

        void onShowTailorMade(); // 显示 美形定制 调整窗口 动画前

        void onDismissTailor(); // 隐藏 美形定制 调整窗口 动画后
    }

    public void SetOnStateListener(OnTailorMadeStateListener listener)
    {
        mStateListener = listener;
    }

    private FrameLayout mBottomLayout;

    private AdjustView mAdjustView;

    private FrameLayout mTipsLayout;

    private BesselView mBesselView;

    private FrameLayout mSettingView;

    private TextView mSkipView;

    private View mClickLayer;

    private TweenLite mTweenLite;

    private boolean mDoingAnim = false;

    private float mDismissEndLoc[];

    private float mTipsDismissEndLoc[];

    private int mTipsTranslationY; // 提示窗口 初始位置

    private int mAdjustTranslationY; // 数值调整窗口 初始位置

    private boolean mShowPatchBtnB4Anim = false; // 动画前 是否显示校正btn

    private boolean mIsChinese = false;

    private Toast mToast;

    public TailorMadeViewV2(@NonNull Context context)
    {
        super(context);
        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);

        mAdjustTranslationY = CameraPercentUtil.HeightPxToPercent(440);
        if (mIsChinese)
        {
            mTipsTranslationY = -ShareData.m_screenRealHeight;
        }
        else
        {
            mTipsTranslationY = -ShareData.m_screenRealHeight;
        }

        mDismissEndLoc = new float[2];

        mTipsDismissEndLoc = new float[2];

        setBackgroundColor(Color.BLACK);
        getBackground().setAlpha(0);

        initView();
    }

    public void showCloseSwitchToast()
    {
        initToast(getContext().getString(R.string.tailor_close_switch_text));
        mToast.show();
    }

    public void showOpenSwitchToast()
    {
        initToast(getContext().getString(R.string.tailor_open_switch_text));
        mToast.show();
    }

    private void initToast(String text)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            TextView tv = new TextView(getContext());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setText(text);
            tv.setTextColor(0xe6333333);
            tv.setGravity(Gravity.CENTER);
            Paint paint = tv.getPaint();
            paint.setFakeBoldText(false);
            tv.setBackgroundResource(R.drawable.new_tailor_bk);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(526), CameraPercentUtil.HeightPxToPercent(79));
            tv.setLayoutParams(params);

            mToast.setView(tv);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        else
        {
            mToast.cancel();
            mToast = null;
            initToast(text);
        }
    }

    public void CancelToast()
    {
        if (mToast != null)
        {
            mToast.cancel();
        }
    }

    private void initView()
    {
        mClickLayer = new View(getContext());
        mClickLayer.setOnClickListener(mClickListener);
        mClickLayer.setLongClickable(true);
        mClickLayer.setVisibility(GONE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mClickLayer, params);

        mBottomLayout = new FrameLayout(getContext());
        mBottomLayout.setClickable(true);
        mBottomLayout.setLongClickable(true);
        mBottomLayout.setBackgroundColor(Color.WHITE);
        mBottomLayout.setTranslationY(mAdjustTranslationY);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(mBottomLayout, params);
        {
            // 背景图 下边距 16px 、内容 393px、 上边距大概是 23px = 432
//            View bk = new View(getContext());
//            bk.setBackgroundResource(R.drawable.tailor_made_bg);
//            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(432));
//            mBottomLayout.addView(bk, params);

            // 调整页面
            mAdjustView = new AdjustView(getContext());
//            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(688), CameraPercentUtil.HeightPxToPercent(393));
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(393));
            params.gravity = Gravity.BOTTOM;
//            params.leftMargin = CameraPercentUtil.WidthPxToPercent(16);
//            params.bottomMargin = CameraPercentUtil.HeightPxToPercent(16);
            mBottomLayout.addView(mAdjustView, params);
        }

        // 提示窗口
        mTipsLayout = new FrameLayout(getContext());
        mTipsLayout.setClickable(true);
        mTipsLayout.setTranslationY(mTipsTranslationY);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.addView(mTipsLayout, params);
        {
            mBesselView = new BesselView(getContext());
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(568), mIsChinese ? CameraPercentUtil.HeightPxToPercent(758) : CameraPercentUtil.HeightPxToPercent(824));
            mTipsLayout.addView(mBesselView, params);

            // 文本区域
            RelativeLayout rl = new RelativeLayout(getContext());
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(528), mIsChinese ? CameraPercentUtil.HeightPxToPercent(400) : CameraPercentUtil.HeightPxToPercent(466));
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            mTipsLayout.addView(rl, params);
            {
                TextView tv = new TextView(getContext());
                tv.setId(R.id.ding_zhi_first_tv);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                tv.setTextColor(0xff333333);
                Paint paint = tv.getPaint();
                paint.setFakeBoldText(true);
                tv.setText(R.string.ding_zhi_second_text);
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlParams.topMargin = CameraPercentUtil.HeightPxToPercent(30);
                rl.addView(tv, rlParams);

//                tv = new TextView(getContext());
//                tv.setId(R.id.ding_zhi_second_tv);
//                tv.setGravity(Gravity.CENTER);
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//                tv.setTextColor(0xff000000);
//                tv.setText(R.string.ding_zhi_second_text);
//                rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                rlParams.addRule(RelativeLayout.BELOW, R.id.ding_zhi_first_tv);
//                rlParams.topMargin = CameraPercentUtil.HeightPxToPercent(10);
//                rl.addView(tv, rlParams);

                tv = new TextView(getContext());
                tv.setId(R.id.ding_zhi_third_tv);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
                tv.setTextColor(0xff666666);
                tv.setText(R.string.ding_zhi_third_text);
                rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlParams.addRule(RelativeLayout.BELOW, R.id.ding_zhi_first_tv);
                rlParams.topMargin = CameraPercentUtil.HeightPxToPercent(18);
                rl.addView(tv, rlParams);

                tv = new TextView(getContext());
                tv.setId(R.id.ding_zhi_forth_tv);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
                tv.setTextColor(0xff666666);
                tv.setText(R.string.ding_zhi_forth_text);
                rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlParams.addRule(RelativeLayout.BELOW, R.id.ding_zhi_third_tv);
                rlParams.topMargin = CameraPercentUtil.HeightPxToPercent(8);
                rl.addView(tv, rlParams);

                mSettingView = new FrameLayout(getContext())
                {
                    @Override
                    public boolean onTouchEvent(MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            this.setAlpha(0.5f);
                        }
                        else if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            this.setAlpha(1f);
                        }
                        return super.onTouchEvent(event);
                    }
                };
                mSettingView.setId(R.id.ding_zhi_setting);
                mSettingView.setOnClickListener(mClickListener);
                rlParams = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(450), CameraPercentUtil.HeightPxToPercent(78));
                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlParams.addRule(RelativeLayout.BELOW, R.id.ding_zhi_forth_tv);
                rlParams.topMargin = CameraPercentUtil.HeightPxToPercent(30);
                rl.addView(mSettingView, rlParams);
                {
                    ImageView bk = new ImageView(getContext());
                    bk.setScaleType(ImageView.ScaleType.FIT_XY);
                    bk.setImageResource(R.drawable.new_material4_downloadall);
                    ImageUtils.AddSkin(getContext(), bk);
                    FrameLayout.LayoutParams fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mSettingView.addView(bk, fl);

                    tv = new TextView(getContext());
                    tv.setTextColor(Color.WHITE);
                    paint = tv.getPaint();
                    paint.setFakeBoldText(true);
                    tv.setText(R.string.ding_zhi_bottom_text);
                    fl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.CENTER;
                    mSettingView.addView(tv, fl);
                }

                mSkipView = new TextView(getContext());
                mSkipView.setTextColor(0xffa0a0a0);
                mSkipView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                mSkipView.setText(R.string.ding_zhi_skip_text);
                mSkipView.setGravity(Gravity.CENTER);
                mSkipView.getPaint().setFakeBoldText(true);
                mSkipView.setOnClickListener(mClickListener);
                mSkipView.setMinWidth(CameraPercentUtil.WidthPxToPercent(200));
                rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CameraPercentUtil.HeightPxToPercent(120));
                rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rlParams.addRule(RelativeLayout.BELOW, R.id.ding_zhi_setting);
                rl.addView(mSkipView, rlParams);
            }
        }
    }

    /**
     * @param isShow 动画前校正是否显示
     */
    public void SetPathStateB4Anim(boolean isShow)
    {
        mShowPatchBtnB4Anim = isShow;
    }

    public boolean GetPatchStateB4Anim()
    {
        return mShowPatchBtnB4Anim;
    }

    /**
     * @return 是否在美形定制过程
     */
    public boolean isTailorMadeAlive()
    {
        return (mTipsLayout.getVisibility() == VISIBLE && mTipsLayout.getTranslationY() == 0) ||
                (mBottomLayout.getVisibility() == VISIBLE && mBottomLayout.getTranslationY() == 0);
    }

    public void SetDismissEndLoc(float x, float y)
    {
        mDismissEndLoc[0] = x;
        mDismissEndLoc[1] = y;
    }

    public void SetTipsDismissEndLoc(float x, float y)
    {
        mTipsDismissEndLoc[0] = x;
        mTipsDismissEndLoc[1] = y;
    }

    public void SetAdjustUIEnable(boolean enable)
    {
        if (mAdjustView != null)
        {
            mAdjustView.SetSwitchState(enable);
            mAdjustView.SetControlUIEnable(enable);
        }
    }

    public void UpdateSeekerBarProgress()
    {
        if (mAdjustView == null) return;
        mAdjustView.InitSeekBerProgress();
    }

    public void ResetSelIndex()
    {
        if (mAdjustView == null) return;
        mAdjustView.ResetSelState();
    }

    public void DismissAdjustArea()
    {
        MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制);
        removeAllAnim();
        initAnim(TweenLite.EASE_OUT | TweenLite.EASING_LINEAR);
        post(mDismissAnim);
    }

    /**
     * 动画 show 出调整窗口
     *
     * @param withAnim 是否动画显示
     */
    public void ShowAdjustArea(boolean withAnim)
    {
        MyBeautyStat.onPageStartByRes(R.string.拍照_拍照_美形定制);
        removeAllAnim();
        mClickLayer.setVisibility(GONE);
        if (withAnim)
        {
            initAnim(TweenLite.EASE_OUT | TweenLite.EASING_LINEAR);
            if (this.getMeasuredHeight() <= 0 || this.getMeasuredWidth() <= 0)
            {
                this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        post(mShowTailorAnimV2);
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
            else
            {
                post(mShowTailorAnimV2);
            }
        }
        else
        {
            if(mStateListener != null)
            {
                mStateListener.onShowTailorMade();
            }
            if (mBottomLayout != null)
            {
                mBottomLayout.setTranslationY(0);
            }
        }
    }

    /**
     * 弹出指引
     */
    public void ShowGuide()
    {
        MyBeautyStat.onPageStartByRes(R.string.拍照_拍照_美形定制首次弹窗);
        removeAllAnim();
        initAnim(TweenLite.EASE_OUT | TweenLite.EASING_BACK);
        if (mTipsLayout != null)
        {
            mClickLayer.setVisibility(VISIBLE);

            if (this.getMeasuredHeight() <= 0 || this.getMeasuredWidth() <= 0)
            {
                this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        post(mShowTipsAnim);
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
            else
            {
                post(mShowTipsAnim);
            }
        }
    }

    /**
     * 隐藏提示, 显示调整窗口
     */
    private void HideTips()
    {
        MyBeautyStat.onPageStartByRes(R.string.拍照_拍照_美形定制);
        removeAllAnim();
        if (mStateListener != null)
        {
            mStateListener.onShowTailorMade();
        }
        initAnim(TweenLite.EASE_OUT | TweenLite.EASING_LINEAR);
        post(mShowAdjustAreaAnim);
    }

    /**
     * 隐藏提示，动画结束 GONE 掉 TailorMadeViewV2
     */
    private void HideTipsV2()
    {
        removeAllAnim();
        if (mStateListener != null)
        {
            mStateListener.onStartDismissGuide();
        }
        initAnim(TweenLite.EASE_OUT | TweenLite.EASING_LINEAR);
        post(mHideTipsAnim);
    }

    private OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mSettingView)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗_去设置);
                MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制首次弹窗_去设置);
                MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制首次弹窗);
                HideTips();
            }
            else if (v == mSkipView)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗_以后再说);
                MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制首次弹窗_以后再说);
                MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制首次弹窗);
                HideTipsV2();
            }
            else if (v == mClickLayer)
            {
                if (mDoingAnim) return;

                if (mTipsLayout.getVisibility() == VISIBLE)
                {
                    Drawable drawable = getBackground();
                    if (drawable instanceof ColorDrawable)
                    {
                        int alpha = ((ColorDrawable) drawable).getAlpha();
                        if (alpha != 0)
                        {
                            TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗_以后再说);
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制首次弹窗_以后再说);
                            MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制首次弹窗);
                            HideTipsV2();
                        }
                    }
                }
            }
        }
    };

    public void SetAdjustParamsListener(AdjustView.OnParamsListener listener)
    {
        if (mAdjustView != null)
        {
            mAdjustView.SetOnParamsListener(listener);
        }
    }

    public void SetTailorConfig(TailorMadeConfig config)
    {
        if (mAdjustView != null)
        {
            mAdjustView.SetTailorConfig(config);
        }
    }

    // **************************** 动画 *******************************//

    private void removeAllAnim()
    {
        removeCallbacks(mDismissAnim);
        removeCallbacks(mShowAdjustAreaAnim);
        removeCallbacks(mShowTipsAnim);
        removeCallbacks(mHideTipsAnim);
    }

    private void clearAllAnim()
    {
        mDismissAnim = null;
        mShowAdjustAreaAnim = null;
        mShowTipsAnim = null;
        mHideTipsAnim = null;
    }

    private void initAnim(int animType)
    {
        if (mTweenLite == null)
        {
            mTweenLite = new TweenLite();
        }
        mTweenLite.M1End();

        mDoingAnim = true;
        mTweenLite.Init(0, 1, 300);
        mTweenLite.M1Start(animType);
    }

    private void SetAnimState(boolean isDoAnim)
    {
        mDoingAnim = isDoAnim;
    }

    public boolean isDoingAnim()
    {
        return mDoingAnim;
    }

    private Runnable mDismissAnim = new Runnable()
    {
        @Override
        public void run()
        {
            float startY = 0;
            float endY = -(ShareData.m_screenRealHeight - mDismissEndLoc[1] - CameraPercentUtil.HeightPxToPercent(432) / 2f);

            float startX = 0;
            float endX = mDismissEndLoc[0] - ShareData.m_screenRealWidth / 2f;

            float startScale = 1f;
            float endScale = 0.01f;

            float startAlpha = 1f;
            float endAlpha = 0.01f;

            float dy = mTweenLite.M1GetPos();
            mBottomLayout.setTranslationY(startY + (endY - startY) * dy);
            mBottomLayout.setTranslationX(startX + (endX - startX) * dy);
            mBottomLayout.setScaleX(startScale + (endScale - startScale) * dy);
            mBottomLayout.setScaleY(startScale + (endScale - startScale) * dy);
            mBottomLayout.setAlpha(startAlpha + (endAlpha - startAlpha) * dy);

            if (!mTweenLite.M1IsFinish())
            {
                post(this);
            }
            else
            {
                mBottomLayout.setTranslationX(0);
                mBottomLayout.setTranslationY(mAdjustTranslationY);
                mBottomLayout.setScaleX(1f);
                mBottomLayout.setScaleY(1f);
                mBottomLayout.setAlpha(1);

                mTipsLayout.setVisibility(GONE);
                mTipsLayout.setTranslationY(mTipsTranslationY);

                setVisibility(GONE);

                SetAnimState(false);

                if (mStateListener != null)
                {
                    mStateListener.onDismissTailor();
                }
            }
        }
    };

    private Runnable mShowTipsAnim = new Runnable()
    {
        @Override
        public void run()
        {
            int startY = mTipsTranslationY;
            int endY = 0;

            float startAlpha = 0;
            float endAlpha = 0.6f;

            float dy = mTweenLite.M1GetPos();
            mTipsLayout.setTranslationY(startY + (endY - startY) * dy);
            dy = dy >= 1 ? 1 : dy;
            getBackground().setAlpha((int) (255 * (endAlpha - startAlpha) * dy));

            if (!mTweenLite.M1IsFinish())
            {
                post(this);
            }
            else
            {
                SetAnimState(false);
                if (mStateListener != null)
                {
                    mStateListener.onShowGuide();
                }
            }
        }
    };

    private Runnable mShowAdjustAreaAnim = new Runnable()
    {
        @Override
        public void run()
        {
            float startY = 0;
            float endY = -(ShareData.m_screenRealHeight - mTipsDismissEndLoc[1] - ShareData.m_screenHeight / 2f);

            float startX = 0;
            float endX = mTipsDismissEndLoc[0] - ShareData.m_screenRealWidth / 2f;

            if (endX <= 1)
            {
                endX = 0;
            }
            float startAlpha = 0.6f;
            float endAlpha = 0;

            float startScale = 1f;
            float endScale = 0f;

            int adjustStartY = 0;
            int adjustEndY = mAdjustTranslationY;

            float dy = mTweenLite.M1GetPos();
            mTipsLayout.setTranslationY(startY + (endY - startY) * dy);
            mTipsLayout.setTranslationX(startX + (endX - startX) * dy);
            mTipsLayout.setScaleX(startScale + (endScale - startScale) * dy);
            mTipsLayout.setScaleY(startScale + (endScale - startScale) * dy);
            mTipsLayout.setAlpha(1 + (endAlpha - 1) * dy);

            getBackground().setAlpha((int) (255 * (startAlpha + (endAlpha - startAlpha) * dy)));

            mBottomLayout.setTranslationY(adjustEndY - (adjustEndY - adjustStartY) * dy);

            if (!mTweenLite.M1IsFinish())
            {
                post(this);
            }
            else
            {
                mTipsLayout.setVisibility(GONE);
                mTipsLayout.setTranslationY(mTipsTranslationY);
                mClickLayer.setVisibility(GONE);

                SetAnimState(false);
            }
        }
    };

    private Runnable mShowTailorAnimV2 = new Runnable()
    {
        @Override
        public void run()
        {
            int startY = 0;
            int endY = mAdjustTranslationY;

            float dy = mTweenLite.M1GetPos();
            mBottomLayout.setTranslationY(endY - (endY - startY) * dy);

            if (!mTweenLite.M1IsFinish())
            {
                post(this);
            }
            else
            {
                SetAnimState(false);
            }
        }
    };

    private Runnable mHideTipsAnim = new Runnable()
    {
        @Override
        public void run()
        {
            float startY = 0;
            float endY = -(ShareData.m_screenRealHeight - mDismissEndLoc[1] - ShareData.m_screenHeight / 2f);

            float startX = 0;
            float endX = mDismissEndLoc[0] - ShareData.m_screenRealWidth / 2f;

            float startAlpha = 0.6f;
            float endAlpha = 0;

            float startScale = 1f;
            float endScale = 0f;

            float dy = mTweenLite.M1GetPos();
            mTipsLayout.setTranslationY(startY + (endY - startY) * dy);
            mTipsLayout.setTranslationX(startX + (endX - startX) * dy);
            mTipsLayout.setScaleX(startScale + (endScale - startScale) * dy);
            mTipsLayout.setScaleY(startScale + (endScale - startScale) * dy);

            getBackground().setAlpha((int) (255 * (startAlpha + (endAlpha - startAlpha) * dy)));
            mTipsLayout.setAlpha(1 + (endAlpha - 1) * dy);

            if (!mTweenLite.M1IsFinish())
            {
                post(this);
            }
            else
            {
                mTipsLayout.setVisibility(GONE);
                mTipsLayout.setTranslationY(mTipsTranslationY);
                mClickLayer.setVisibility(GONE);

                setVisibility(GONE);
                SetAnimState(false);

                if (mStateListener != null)
                {
                    mStateListener.onDismissTailor();
                }
            }
        }
    };

    public void ClearMemory()
    {
        if (mTweenLite != null)
        {
            mTweenLite.M1IsFinish();
        }

        removeAllAnim();

        clearAllAnim();

        mClickListener = null;
        mStateListener = null;

        mSettingView.setOnClickListener(null);
        mSkipView.setOnClickListener(null);
        mAdjustView.SetOnParamsListener(null);
        mClickLayer.setOnClickListener(null);
        mBesselView.ClearMemory();
        mAdjustView.ClearMemory();

        if (mToast != null)
        {
            mToast.cancel();
            mToast = null;
        }
    }
}