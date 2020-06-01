package cn.poco.campaignCenter.widget.share;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.poco.image.filter;
import cn.poco.share.ShareTools;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2016/12/23.
 */

public class ShareActionSheet extends Dialog{
    private Context mContext;
    private FrameLayout mViewContainer;
    private View mBackground;
    private ShareLayout mShareLayout;
    private boolean mWasLayout;
    private boolean mDefaultStyle;


    public ShareActionSheet(Context context, int theme, boolean defaultLayout) {
        super(context, theme);
        this.mContext = context;
        this.mDefaultStyle = defaultLayout;
        init(context);
        initView(context);
    }

    protected void init(Context context) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        ShareData.InitData(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mViewContainer);
    }

    private void initView(Context context) {
        mViewContainer = new FrameLayout(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_UP) {
                    dismiss();
                }
                return super.onTouchEvent(event);
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (!mWasLayout) {
                    mWasLayout = true;
                    if (mShareLayout != null) {
                        int leftPosition = 0;
                        int topPosition = this.getMeasuredHeight();
                        int rightPosition = this.getMeasuredWidth();
                        int bottomPosition = topPosition + mShareLayout.getMeasuredHeight();
                        mShareLayout.layout(leftPosition, topPosition, rightPosition, bottomPosition);
                        mWasLayout = false;
                    }
                }
            }
        };
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.setLayoutParams(params);
        mViewContainer.setClickable(true);

        mBackground = new View(context);
        mBackground.setBackgroundColor(Color.BLACK);
        mBackground.setAlpha(0);
        FrameLayout.LayoutParams paramsBg = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mBackground.setLayoutParams(paramsBg);
        mViewContainer.addView(mBackground);

        mShareLayout = new ShareLayout(context, mDefaultStyle);
        mShareLayout.setBackgroundColor(0xffffffff);
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(548));
        mShareLayout.setLayoutParams(layoutParams1);
        mViewContainer.addView(mShareLayout);
        mShareLayout.setDelegate(new ShareLayout.ShareLayoutDelegate() {
            @Override
            public void onImageItemClick(int index) {

            }

            @Override
            public void onCancelBtnClick() {
                dismiss();
            }
        });
    }

    public void setContentPadding(int leftPadding, int rightPadding) {
        mShareLayout.setLayoutData(leftPadding, rightPadding);
    }


    public void close() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    public void clear() {
        mShareLayout.setDelegate(null);
        mShareLayout.clear();
    }


    public ShareTools getShareTools() {
        return mShareLayout.getShareTools();
    }

    public void setUpShareContentInfo(String title, String content, String link, String img, String imgType2, String downLoadImgPathNormal, String downloadImgTwitter) {
        mShareLayout.setUpShareInfo(title, content, link, img, imgType2, downLoadImgPathNormal, downloadImgTwitter);
    }

    public void setShareIconView(ShareIconView shareIconView) {
        mShareLayout.setShareIconView(shareIconView);
    }

    @Override
    public void show() {
        super.show();
        if (mShareLayout.getMeasuredHeight() == 0) {
            mShareLayout.measure(View.MeasureSpec.makeMeasureSpec(ShareData.m_screenWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(ShareData.PxToDpi_xhdpi(548), View.MeasureSpec.EXACTLY));
        }

        Bitmap bmp = CommonUtils.GetScreenBmp((Activity) mContext, ShareData.m_screenWidth, ShareData.m_screenHeight);
        Bitmap cropBitmap = null;
        if(bmp != null)
        {
            bmp = filter.fakeGlassBeauty(bmp, 0);
            cropBitmap = Bitmap.createBitmap(bmp, 0, ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(548), ShareData.m_screenWidth, ShareData.PxToDpi_xhdpi(548));
        }

        if(cropBitmap != null) {
            //bmp set到背景
            mShareLayout.mBg.setBackgroundDrawable(new BitmapDrawable(cropBitmap));
        } else {
            mShareLayout.mBg.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(mBackground, "alpha", 0, 0.3f);
        ObjectAnimator translateObjectAnimator = ObjectAnimator.ofFloat(mShareLayout, "translationY", -mShareLayout.getMeasuredHeight());

        AnimatorSet animatorSet = new AnimatorSet();
        if (alphaObjectAnimator != null) {
            animatorSet.playTogether(alphaObjectAnimator, translateObjectAnimator);
        } else {
            animatorSet.playTogether(translateObjectAnimator);
        }
        animatorSet.setDuration(300);
        animatorSet.start();

    }

    @Override
    public void dismiss() {
        Window window = this.getWindow();
        if (window != null) {
            window.setDimAmount(0);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(mBackground, "alpha", 0.3f, 0);
        ObjectAnimator translateObjectAnimator = ObjectAnimator.ofFloat(mShareLayout, "translationY", mShareLayout.getMeasuredHeight());

        AnimatorSet animatorSet = new AnimatorSet();
        if (alphaObjectAnimator != null) {
            animatorSet.playTogether(alphaObjectAnimator, translateObjectAnimator);
        } else {
            animatorSet.play(translateObjectAnimator);
        }
        animatorSet.setDuration(300);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ShareActionSheet.super.dismiss();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ShareActionSheet.super.dismiss();
            }
        });
    }
}
