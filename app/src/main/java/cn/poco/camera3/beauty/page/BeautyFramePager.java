package cn.poco.camera3.beauty.page;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.beauty.BeautySeekBar;
import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.callback.IBeautyPageCallback;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.BeautyInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-15.
 */

public class BeautyFramePager extends BaseFramePager implements BeautySeekBar.OnSeekBarChangeListener
{
    public static final String BEAUTY_FRAME_PAGER_TAG = "BeautyFramePager";

    public static final String BUNDLE_KEY_DATA_BEAUTY_LIST = "bundle_key_data_beauty_list";

    protected BeautyInfo mBeautyInfo;
    protected BeautySeekBar skinBeauty;
    protected BeautySeekBar whitenTeeth;
    protected BeautySeekBar skinType;
    protected BeautySeekBar.OnSeekBarChangeListener listener;

    public BeautyFramePager(@NonNull Context context, TabUIConfig mTabUIConfig)
    {
        super(context, mTabUIConfig);
    }

    @Override
    public void initData()
    {

    }


    @Override
    public void setData(HashMap<String, Object> data)
    {
        super.setData(data);
        if (data != null)
        {
            if (data.containsKey(BUNDLE_KEY_DATA_BEAUTY_LIST))
            {
                mBeautyInfo = (BeautyInfo) data.get(BUNDLE_KEY_DATA_BEAUTY_LIST);
            }
        }

        if (mBeautyInfo != null)
        {
            updateColorSeekBar(skinBeauty, (int) (mBeautyInfo.getData().getSkinBeauty()));//底层效果60，ui显示55\
            updateColorSeekBar(whitenTeeth, (int) (mBeautyInfo.getData().getWhitenTeeth()));
            updateColorSeekBar(skinType, (int) (mBeautyInfo.getData().getSkinType()));
        }
    }


    @Override
    public void onPageSelected(int position, Object pageTag)
    {
    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onClose()
    {
        super.onClose();
        if (skinBeauty != null)
        {
            skinBeauty.setOnSeekBarChangeListener(null);
        }
        if (whitenTeeth != null)
        {
            whitenTeeth.setOnSeekBarChangeListener(null);
        }
        if (skinType != null)
        {
            skinType.setOnSeekBarChangeListener(null);
        }
        mBeautyInfo = null;
        listener = null;
    }

    @Override
    public void notifyDataChanged()
    {
        if (mCallback != null)
        {

        }
    }

    @Override
    public void onCreateContainerView(@NonNull FrameLayout parentLayout)
    {
        RelativeLayout layout = new RelativeLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                CameraPercentUtil.HeightPxToPercent(272));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(88);
        parentLayout.addView(layout, params);

        float a, b, c;
        TextView sbtxt = new TextView(getContext());
        sbtxt.setGravity(Gravity.CENTER);
        sbtxt.setTextColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.9f));
        sbtxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        sbtxt.setText(getResources().getString(R.string.shape_cus_beauty_skin_beauty));
        a = sbtxt.getPaint().measureText(getResources().getString(R.string.shape_cus_beauty_skin_beauty));

        TextView wttxt = new TextView(getContext());
        wttxt.setGravity(Gravity.CENTER);
        wttxt.setTextColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.9f));
        wttxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        wttxt.setText(getResources().getString(R.string.shape_cus_beauty_whiten_teeth));
        b = wttxt.getPaint().measureText(getResources().getString(R.string.shape_cus_beauty_whiten_teeth));

        TextView sctxt = new TextView(getContext());
        sctxt.setGravity(Gravity.CENTER);
        sctxt.setTextColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.9f));
        sctxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        sctxt.setText(getResources().getString(R.string.shape_cus_beauty_skin_color));
        c = sctxt.getPaint().measureText(getResources().getString(R.string.shape_cus_beauty_skin_color));

        skinBeauty = getColorSeekBar(0);
        skinBeauty.setTag(STag.BeautyTag.SKINBEAUTY);
        whitenTeeth = getColorSeekBar(0);
        whitenTeeth.setTag(STag.BeautyTag.WHITENTEETH);
        skinType = getColorSeekBar(0);
        skinType.setTag(STag.BeautyTag.SKINTYPE);

        //美肤
        FrameLayout sbFr = new FrameLayout(getContext());
        sbFr.setId(R.id.shape_selector_beauty_seek_bar_fr_sb);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(77));
        rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rp.topMargin = CameraPercentUtil.HeightPxToPercent(16);
        layout.addView(sbFr, rp);
        {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(sbtxt, params);

            params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(458), CameraPercentUtil.HeightPxToPercent(77));
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(skinBeauty, params);
        }

        //美牙
        sbFr = new FrameLayout(getContext());
        sbFr.setId(R.id.shape_selector_beauty_seek_bar_fr_wh);
        rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(77));
        rp.addRule(RelativeLayout.BELOW, R.id.shape_selector_beauty_seek_bar_fr_sb);
        rp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rp.topMargin = CameraPercentUtil.HeightPxToPercent(4);
        layout.addView(sbFr, rp);
        {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(wttxt, params);

            params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(458), CameraPercentUtil.HeightPxToPercent(77));
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(whitenTeeth, params);
        }

        //肤色
        sbFr = new FrameLayout(getContext());
        sbFr.setId(R.id.shape_selector_beauty_seek_bar_fr_sc);
        rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(77));
        rp.addRule(RelativeLayout.BELOW, R.id.shape_selector_beauty_seek_bar_fr_wh);
        rp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rp.topMargin = CameraPercentUtil.HeightPxToPercent(8);
        layout.addView(sbFr, rp);
        {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(sctxt, params);

            params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(458), CameraPercentUtil.HeightPxToPercent(77));
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(85);
            sbFr.addView(skinType, params);
        }
    }


    @Override
    public String getFramePagerTAG()
    {
        return BEAUTY_FRAME_PAGER_TAG;
    }

    BeautySeekBar getColorSeekBar(int progress)
    {
        BeautySeekBar colorSeekBar = new BeautySeekBar(getContext());
        colorSeekBar.setInnCircleColor(Color.WHITE);
        colorSeekBar.setSeekLineBkColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.24f));
        colorSeekBar.setProgress(progress);
        colorSeekBar.setOnSeekBarChangeListener(this);
        return colorSeekBar;
    }

    void updateColorSeekBar(BeautySeekBar seekBar, int progress)
    {
        if (seekBar != null)
        {
            seekBar.setProgress(progress);
        }
    }

    public BeautyData updateBeautyData(@STag.BeautyTag int type, int uiProgress, boolean isUpdateBeautyData)
    {
        if (type == STag.BeautyTag.SKINBEAUTY) {
            updateColorSeekBar(skinBeauty, uiProgress);
            if (isUpdateBeautyData && mBeautyInfo != null) {
                mBeautyInfo.data.setSkinBeauty(uiProgress);
            }
        } else if (type == STag.BeautyTag.WHITENTEETH) {
            updateColorSeekBar(whitenTeeth, uiProgress);
            if (isUpdateBeautyData && mBeautyInfo != null) {
                mBeautyInfo.data.setWhitenTeeth(uiProgress);
            }
        } else if (type == STag.BeautyTag.SKINTYPE) {
            updateColorSeekBar(skinType, uiProgress);
            if (isUpdateBeautyData && mBeautyInfo != null) {
                mBeautyInfo.data.setSkinType(uiProgress);
            }
        }
        if (mBeautyInfo != null)
        {
            return mBeautyInfo.data;
        }
        return null;
    }

    @Override
    public void onProgressChanged(BeautySeekBar seekBar, int progress)
    {
        onProgressChanged(seekBar, seekBar.getProgress(), false);
    }

    @Override
    public void onStartTrackingTouch(BeautySeekBar seekBar)
    {
        onProgressChanged(seekBar, seekBar.getProgress(), false);
        int pageType = getPageType();
        if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE) {
            int resId = -1;
            int tag = (int) seekBar.getTag();
            if (tag == STag.BeautyTag.SKINBEAUTY) {
                resId = R.string.直播助手_美颜页_美颜调整_美肤滑竿;
            } else if (tag == STag.BeautyTag.WHITENTEETH) {
                resId = R.string.直播助手_美颜页_美颜调整_美牙滑竿;
            } else if (tag == STag.BeautyTag.SKINTYPE) {
                resId = R.string.直播助手_美颜页_美颜调整_肤色滑竿;
            }
            if (resId != -1) MyBeautyStat.onClickByRes(resId);
        } else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA) {

        }
    }

    @Override
    public void onStopTrackingTouch(BeautySeekBar seekBar)
    {
        onProgressChanged(seekBar, seekBar.getProgress(), true);
    }

    private void onProgressChanged(BeautySeekBar seekBar, int progress, boolean isStop)
    {
        // 美肤、美牙、肤质 seek bar回调
        if (mBeautyInfo == null) return;

        int tag = (int) seekBar.getTag();
        if (tag == STag.BeautyTag.SKINBEAUTY)
        {
            mBeautyInfo.data.setSkinBeauty(progress);
        }
        else if (tag == STag.BeautyTag.WHITENTEETH)
        {
            mBeautyInfo.data.setWhitenTeeth(progress);
        }
        else if (tag == STag.BeautyTag.SKINTYPE)
        {
            mBeautyInfo.data.setSkinType(progress);
        }

        if (mCallback != null)
        {
            mCallback.onSeekBarSlide(seekBar, progress, isStop);

            if (mCallback instanceof IBeautyPageCallback)
            {
                ((IBeautyPageCallback) mCallback).onBeautyUpdate(tag, mBeautyInfo.data);
            }
        }
    }
}
