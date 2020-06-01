package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;

/**
 * Created by Shine on 2016/12/14.
 */

public class AutoDisplayCell extends FrameLayout{
    public ImageView mBackground;
    private CampaignInfo mCampaignInfo;

    public AutoDisplayCell(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mBackground = new ImageView(context);
        mBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.TOP);
        mBackground.setLayoutParams(params);
        this.addView(mBackground);
    }

    public void setData(CampaignInfo campaignInfo) {
        this.mCampaignInfo = campaignInfo;
        ImageLoaderUtil.displayImage(getContext(),mCampaignInfo.getCoverUrl(), mBackground, false, null);
    }

    public CampaignInfo getData() {
        return mCampaignInfo;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }
}
