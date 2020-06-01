package cn.poco.camera3.beauty;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.ui.customization.BesselView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-18.
 */

public class BeautyGuideView extends FrameLayout implements View.OnClickListener
{

    private onBeautyGuideStateListener mStateListener;

    public interface onBeautyGuideStateListener
    {
        void onShowGuide(); // 指引动画结束后回调

        void onDismissGuideStart(); // "以后再说" 回调，动画前

        void onDismissGuideEnd(boolean is2Show); // "以后再说" 回调，动画前
    }

    private FrameLayout mTipsLayout;

    private BesselView mBesselView;

    private FrameLayout mSettingView;

    private TextView mSkipView;

    private View mClickLayer;

    private TweenLite mTweenLite;

    private boolean mDoingAnim = false;

    private boolean is2ShowSetting = false;

    private float mDismissEndLoc[];

    private int mTipsTranslationY; // 提示窗口 初始位置

    private boolean mShowPatchBtnB4Anim = false; // 动画前 是否显示校正btn

    private boolean mIsChinese = false;

    private Toast mToast;

    // 是否显示指引 相当于第一次使用
    public static boolean IsShowGuide(Context context)
    {
        return TagMgr.CheckTag(context, Tags.CAMERA_TAILOR_MADE_GUIDE_FLAG);
    }

    public static void SetGuideTag(Context context, boolean save)
    {
        TagMgr.SetTag(context, Tags.CAMERA_TAILOR_MADE_GUIDE_FLAG);
        if (save)
        {
            TagMgr.Save(context);
        }
    }

    public static void UpdateTag(Context context)
    {
        boolean guideFlag = IsShowGuide(context);

        if (guideFlag)
        {
            SetGuideTag(context, false);
        }

        if (guideFlag)
        {
            TagMgr.Save(context);
        }
    }

    public BeautyGuideView(@NonNull Context context)
    {
        super(context);
        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);

        if (mIsChinese)
        {
            mTipsTranslationY = -ShareData.m_screenRealHeight;
        }
        else
        {
            mTipsTranslationY = -ShareData.m_screenRealHeight;
        }

        mDismissEndLoc = new float[2];

        setBackgroundColor(Color.BLACK);
        getBackground().setAlpha(0);

        initView();
    }

    private void initView()
    {
        mClickLayer = new View(getContext());
        mClickLayer.setOnClickListener(this);
        mClickLayer.setLongClickable(true);
        mClickLayer.setVisibility(GONE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mClickLayer, params);

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
                mSettingView.setOnClickListener(this);
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
                mSkipView.setOnClickListener(this);
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

    public boolean isDoingAnim()
    {
        return mDoingAnim;
    }


    public boolean isBeautyGuideAlive()
    {
        return (mTipsLayout.getVisibility() == VISIBLE && mTipsLayout.getTranslationY() == 0);
    }

    public void SetOnStateListener(onBeautyGuideStateListener listener)
    {
        mStateListener = listener;
    }

    private void removeAllAnim()
    {
        removeCallbacks(mShowTipsAnim);
        removeCallbacks(mHideTipsAnim);
    }

    private void clearAllAnim()
    {
        mShowTipsAnim = null;
        mHideTipsAnim = null;
    }

    public void SetDismissEndLoc(float x, float y)
    {
        mDismissEndLoc[0] = x;
        mDismissEndLoc[1] = y;
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

    private Runnable mHideTipsAnim = new Runnable()
    {
        @Override
        public void run()
        {
            float startY = 0;
            float endY = mDismissEndLoc[1] - ShareData.m_screenRealHeight / 2f;

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
                    mStateListener.onDismissGuideEnd(is2ShowSetting);
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

    private void SetAnimState(boolean isDoAnim)
    {
        mDoingAnim = isDoAnim;
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

    /**
     * 隐藏提示，动画结束 GONE 掉 BeautyGuideView
     */
    private void HideTips(boolean isToSetting)
    {
        is2ShowSetting = isToSetting;
        removeAllAnim();
        if (mStateListener != null)
        {
            mStateListener.onDismissGuideStart();
        }
        initAnim(TweenLite.EASE_OUT | TweenLite.EASING_LINEAR);
        post(mHideTipsAnim);
    }


    @Override
    public void onClick(View v)
    {
        if (v == mSettingView)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗_去设置);
            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制首次弹窗_去设置);
            MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制首次弹窗);
            HideTips(true);
        }
        else if (v == mSkipView)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗_以后再说);
            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制首次弹窗_以后再说);
            MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制首次弹窗);
            HideTips(false);
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
                        HideTips(false);
                    }
                }
            }
        }
    }

    public void clear()
    {
        if (mTweenLite != null)
        {
            mTweenLite.M1IsFinish();
        }
        removeAllAnim();
        clearAllAnim();
        mStateListener = null;
        mSettingView.setOnClickListener(null);
        mSkipView.setOnClickListener(null);
        mClickLayer.setOnClickListener(null);
        mBesselView.ClearMemory();
        if (mToast != null)
        {
            mToast.cancel();
            mToast = null;
        }
    }
}
