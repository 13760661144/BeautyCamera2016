package cn.poco.camera2;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/11/08 17:10.<br/><br/>
 * 贴纸功能使用指引提示
 */
public class StickerTipsForUse extends RelativeLayout {

    private RelativeLayout shutterTip;
    private ImageView moreStickerTip;


    public StickerTipsForUse(Context context) {
        super(context);

        initView();
    }

    private void initView() {
        this.setClickable(true);
        int virtualKeyHeight = ShareData.getCurrentVirtualKeyHeight((Activity)getContext());

        RelativeLayout.LayoutParams rParams;

        shutterTip = new RelativeLayout(getContext());
//        shutterTip.setBackgroundResource(R.drawable.camera_shutter_shuoming2_pop);
        rParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.bottomMargin = ShareData.getRealPixel_720P(230);
        addView(shutterTip, rParams);
        {
//            ImageView bgView = new ImageView(getContext());
//            bgView.setImageResource(R.drawable.camera_shutter_tips);
//            rParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//            shutterTip.addView(bgView, rParams);
//            ImageUtils.AddSkin(getContext(), bgView);

            TextView tipsTxt = new TextView(getContext());
            tipsTxt.setBackgroundResource(R.drawable.camera_shutter_tips);
            tipsTxt.setText(getContext().getString(R.string.camerapage_record_tips));
            tipsTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tipsTxt.setTextColor(0xffffffff);//ImageUtils.GetSkinColor(0xffff7fa8));
//            tipsTxt.setLineSpacing(12, 0.5f);
            tipsTxt.setGravity(Gravity.CENTER);
//            tipsTxt.setPadding(0, ShareData.getRealPixel_720P(8), 0, 0);
            rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            shutterTip.addView(tipsTxt, rParams);
        }

//        moreStickerTip = new ImageView(getContext());
//        moreStickerTip.setImageResource(R.drawable.more_sticker_green);//R.drawable.more_sticker
//        rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        rParams.leftMargin = ShareData.getRealPixel_720P(180);
//        rParams.bottomMargin = ShareData.getRealPixel_720P(170) - virtualKeyHeight;
//        addView(moreStickerTip, rParams);
    }
}
