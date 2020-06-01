package cn.poco.featuremenu.cell;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import cn.poco.adMaster.ShareAdBanner;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2017/9/7.
 */

public class AdCell extends LinearLayout{
    public interface AdCellCallback {
        void onClickAdItem(int adPosition, String adUrl);
    }

    public static class AdItem {
        public String mCoverImage;
        public String mAdUrl;
        public String[] mClickTjs;
        public int mTjId;
    }

    private TextView mTitle;

    public AdCell(Context context) {
        super(context);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        initView();
    }

    private AdCellCallback mCallback;
    public void setAdCellCallback(AdCellCallback callback) {
        this.mCallback = callback;
    }

    private void initView() {
        mTitle = new TextView(getContext());
        mTitle.setTextColor(0xff999999);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTitle.setPadding(ShareData.PxToDpi_xhdpi(28), 0, 0, 0);
        mTitle.setTypeface(mTitle.getTypeface(), Typeface.BOLD);
        mTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(60));
        mTitle.setLayoutParams(params);
        this.addView(mTitle);
        mTitle.setVisibility(View.GONE);
    }

    public void setAdInfo(final AdItem[] adItemArray) {
        this.removeAllViews();
        this.addView(mTitle);
        mTitle.setVisibility(View.VISIBLE);
        if (adItemArray != null && adItemArray.length > 0) {
            for (int i = 0; i < adItemArray.length; i++) {
                final int index = i;
                final AdItem curItem = adItemArray[i];
                final ScaleAndCenterView view = new ScaleAndCenterView(getContext());
                view.setScaleType(ImageView.ScaleType.MATRIX);
                LinearLayout.LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(660), ShareData.PxToDpi_xhdpi(294));
                if(i > 0) {
                    params.topMargin = ShareData.PxToDpi_xhdpi(20);
                }
                view.setLayoutParams(params);
                this.addView(view);
                ImageLoaderUtil.displayImage(getContext(), curItem.mCoverImage , view, false, new ImageLoaderUtil.ImageLoaderCallback() {
                    @Override
                    public void loadImageSuccessfully(Object object) {
                        if (object instanceof GlideDrawable) {
                            GlideDrawable glideDrawable = (GlideDrawable) object;
                            view.properScaleImage(glideDrawable, ShareData.PxToDpi_xhdpi(660), ShareData.PxToDpi_xhdpi(294));
                        }
                    }

                    @Override
                    public void failToLoadImage() {

                    }
                });
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            // 相应统计
                            MyBeautyStat.onBanner(String.valueOf(curItem.mTjId));
                            ShareAdBanner.SendTj(getContext(), curItem.mClickTjs);
                            mCallback.onClickAdItem(index, curItem.mAdUrl);
                        }
                    }
                });
            }
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

}
