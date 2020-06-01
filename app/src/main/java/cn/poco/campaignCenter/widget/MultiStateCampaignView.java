package cn.poco.campaignCenter.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.poco.campaignCenter.widget.view.EmptyHolderView;
import cn.poco.campaignCenter.widget.view.LoadingView;

/**
 * Created by Shine on 2016/12/14.
 */

public class MultiStateCampaignView extends FrameLayout implements CampaignDataState{
    private EmptyHolderView mNetWorkErrorView;
    private LoadingView mLoadingView;
    private LinearLayout mContentLayout;
    private View mCurrentShowingView;

    private static final int BLACK_DEFAULT_ALPHA = 25;
    private static final int WHITE_DEFAULT_ALPHA = 80;

    private Paint mGradientPaint;

    // 5%的黑色不透明 到 50%的白色透明
    private Paint mBlackPaint, mWhitePaint;
    private Shader mShader;
    int mBlackLayerAlpha, mWhiteLayerAlpha;

    private int mColorTop;
    private int mColorBottom;
    private int mType;



    public MultiStateCampaignView(Context context) {
        super(context);
        initData();
        initView(context);
        CampaignListMonitor.getInstance().addObservers(this);
        setWillNotDraw(false);
    }

    private void initData() {
        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mBlackPaint = new Paint();
        mBlackPaint.setAntiAlias(true);
        mBlackPaint.setStyle(Paint.Style.FILL);

        // 5%黑色不透明
        mBlackPaint.setColor(0x000000);
        mBlackPaint.setAlpha(mBlackLayerAlpha);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(0xffffff);
        mWhitePaint.setAlpha(mWhiteLayerAlpha);
    }


    private void initView(Context context) {
        mNetWorkErrorView = new EmptyHolderView(context);
        mNetWorkErrorView.setBackgroundColor(Color.WHITE);
        mNetWorkErrorView.setVisibility(View.GONE);
        mNetWorkErrorView.setAlpha(0);

        mNetWorkErrorView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(mNetWorkErrorView);

        mContentLayout = new LinearLayout(this.getContext());
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.setGravity(Gravity.CENTER);
        mContentLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContentLayout.setVisibility(View.GONE);
        this.addView(mContentLayout);

        mLoadingView = new LoadingView(context);
        mLoadingView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mLoadingView.setVisibility(View.VISIBLE);
        this.addView(mLoadingView);

    }


    public void addDataView(View v) {
        mContentLayout.addView(v);
    }

    public void setOnClickNetWorkError(OnClickListener onClickNetWorkError) {
        mNetWorkErrorView.setOnClickListener(onClickNetWorkError);
    }


    private Shader initShader() {
        LinearGradient shader;
        if(mType == 0)
        {
            shader = new LinearGradient(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), new int[]{mColorTop, mColorBottom}, null, Shader.TileMode.CLAMP);
        }
        else if(mType == 2)
        {
            shader = new LinearGradient(this.getTop(), this.getLeft(), this.getBottom(), this.getRight(), new int[]{mColorTop, mColorBottom}, null, Shader.TileMode.CLAMP);
        }
        else
        {
            shader = new LinearGradient((this.getRight() - this.getLeft())/2, this.getTop(), (this.getRight() - this.getLeft()) / 2, this.getBottom(), new int[]{mColorTop, mColorBottom}, null, Shader.TileMode.CLAMP);
        }
        return shader;
    }

    public void setThemeSkin(int topColor, int bottomColor,int type) {
        this.mColorTop = topColor;
        this.mColorBottom = bottomColor;
        this.mType = type;
        this.invalidate();
    }


    // 正在加载数据的状态
    @Override
    public void loadingData(double pregress) {
        mCurrentShowingView = mLoadingView;
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.setClickable(false);
        mContentLayout.setVisibility(View.GONE);

        final int unit = 255;
        mWhiteLayerAlpha = (int)(pregress * unit);
        mBlackLayerAlpha = (int)((1 - pregress) * unit);

        int finalWhiteAlpha = mWhiteLayerAlpha > WHITE_DEFAULT_ALPHA ? WHITE_DEFAULT_ALPHA : mWhiteLayerAlpha;
        int finalBlackAlpha = mBlackLayerAlpha > BLACK_DEFAULT_ALPHA ? BLACK_DEFAULT_ALPHA : mBlackLayerAlpha;

        mWhitePaint.setAlpha(finalWhiteAlpha);
        mBlackPaint.setAlpha(finalBlackAlpha);
        mGradientPaint.setAlpha(255);
        invalidate();
    }


    // 无法获取数据的状态
    @Override
    public void failToLoadData(double progress, int style) {
        mCurrentShowingView = mNetWorkErrorView;
        mLoadingView.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.GONE);

        final int unit = 255;
        mWhiteLayerAlpha = (int)(progress * unit);
        mBlackLayerAlpha = (int)((1 - progress) * unit);

        int finalWhiteAlpha = mWhiteLayerAlpha > WHITE_DEFAULT_ALPHA ? WHITE_DEFAULT_ALPHA : mWhiteLayerAlpha;
        int finalBlackAlpha = mBlackLayerAlpha > BLACK_DEFAULT_ALPHA ? BLACK_DEFAULT_ALPHA : mBlackLayerAlpha;

        mWhitePaint.setAlpha(finalWhiteAlpha);
        mBlackPaint.setAlpha(finalBlackAlpha);
        switch (style) {
            case CampaignDataState.BEGIN_WITH_THEME_SKIN : {
                mGradientPaint.setAlpha((int)((1 - progress) * 255));
                mNetWorkErrorView.setVisibility(View.VISIBLE);
                mNetWorkErrorView.setAlpha((float)progress);
                break;
            }

            case CampaignDataState.BEGIN_WITH_OTHER : {
                mGradientPaint.setAlpha(0);
                mNetWorkErrorView.setVisibility(View.VISIBLE);
                mNetWorkErrorView.setAlpha(1);
                break;
            }
            default: {
                break;
            }
        }
        invalidate();
    }


    // 成功获取数据
    @Override
    public void succedLoadData(double progress) {
        mCurrentShowingView = mContentLayout;
        mLoadingView.setVisibility(View.GONE);
        mNetWorkErrorView.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mContentLayout.setClickable(true);

        mGradientPaint.setAlpha(255);
        mWhitePaint.setAlpha(WHITE_DEFAULT_ALPHA);
        mBlackPaint.setAlpha(0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mShader = initShader();
        mGradientPaint.setShader(mShader);
        canvas.drawRect(this.getLeft(), 0, this.getRight(), this.getBottom(), mGradientPaint);
        canvas.drawRect(this.getLeft(), 0, this.getRight(), this.getBottom(), mWhitePaint);
        canvas.drawRect(this.getLeft(), 0, this.getRight(), this.getBottom(), mBlackPaint);
    }

    public boolean isNetWorkErrorViewShowing() {
       return mCurrentShowingView == mNetWorkErrorView; 
    }

}
