package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/10/14.
 */

public class CampaignCell extends FrameLayout {

    private Context mContext;

    public CampaignBgView mBackground;

    protected ImageView mVideoPlayBtn;

    private FrameLayout mCampaignThemeContainer;
    protected TextView mCampaignName;
    protected ImageView mCheckMoreBtn;

    private CampaignInfo mCampaingInfo;
    private static TextPaint mPaint;


    public CampaignCell(Context context) {
        super(context);
        mContext = context;
        if (mPaint == null) {
            mPaint = new TextPaint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(16);
        }
        initView(context);
    }

    private void initView(Context context) {
        mBackground = new CampaignBgView(context);
        mBackground.setScaleType(ImageView.ScaleType.MATRIX);
        mBackground.setBackgroundColor(Color.WHITE);

        ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBackground.setLayoutParams(params1);
        this.addView(mBackground);

        mVideoPlayBtn = new ImageView(mContext);
        mVideoPlayBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mVideoPlayBtn.setLayoutParams(params3);
        mVideoPlayBtn.setImageResource(R.drawable.campaigncenter_video_play);
        mVideoPlayBtn.setVisibility(View.GONE);

        mCampaignThemeContainer = new FrameLayout(mContext);
        mCampaignThemeContainer.setBackgroundColor(Color.WHITE);
        mCampaignThemeContainer.setAlpha(0.96f);
        float rate = (76 * 1.0f / 720);
        int viewHeight = (int) (ShareData.m_screenWidth * rate);
        FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight, Gravity.BOTTOM);
        mCampaignThemeContainer.setLayoutParams(params4);
        this.addView(mCampaignThemeContainer);
        {
            mCampaignName = new TextView(mContext);
            mCampaignName.setLines(1);
            mCampaignName.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            mCampaignName.setMaxLines(1);
            mCampaignName.setSingleLine(true);
            mCampaignName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            mCampaignName.setTextColor(0xB3000000);
            mCampaignName.setEllipsize(TextUtils.TruncateAt.END);
            FrameLayout.LayoutParams params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT);
//          params5.leftMargin = ShareData.PxToDpi_xhdpi(28);
            params5.rightMargin = ShareData.PxToDpi_xhdpi(49);
            mCampaignName.setLayoutParams(params5);
            mCampaignThemeContainer.addView(mCampaignName);

            mCheckMoreBtn = new ImageView(mContext);
            mCheckMoreBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mCheckMoreBtn.setImageResource(R.drawable.campaigncenter_right_arrow);
            FrameLayout.LayoutParams params6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            params6.rightMargin = ShareData.PxToDpi_xhdpi(20);
            mCheckMoreBtn.setLayoutParams(params6);
            mCampaignThemeContainer.addView(mCheckMoreBtn);
        }
    }

    public void setData(CampaignInfo campaingnInfo, int type) {
        this.mCampaingInfo = campaingnInfo;
        mBackground.setType(type);
        mCampaignName.setText(mCampaingInfo.getTitle());
        updateLayout();
        ImageUtils.AddSkin(mContext, mCheckMoreBtn);
    }

    private void updateLayout() {
        CampaignInfo.CampaignType campaignType = mCampaingInfo.getCampaignType();
        if (campaignType == CampaignInfo.CampaignType.Vedio) {
            mVideoPlayBtn.setVisibility(View.VISIBLE);
        } else {
            mVideoPlayBtn.setVisibility(View.GONE);
        }
        ImageLoaderUtil.displayImage(mContext, mCampaingInfo.getCoverUrl(), mBackground, false, new ImageLoaderUtil.ImageLoaderCallback() {
            @Override
            public void loadImageSuccessfully(Object object) {
                if (object instanceof GlideDrawable) {
                    GlideDrawable glideDrawable = (GlideDrawable) object;
                    CampaignCell.this.mBackground.setUpImage(glideDrawable);
                }
            }

            @Override
            public void failToLoadImage() {

            }
        });
    }

    public CampaignInfo getData() {
        return mCampaingInfo;
    }


@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    public void setOnNavigationItemTouchListener(OnAnimationClickListener listener) {
        mCheckMoreBtn.setOnTouchListener(listener);
    }

    public void clear() {

    }



}
