package cn.poco.filter4.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2016/11/30.
 */

public class FilterItemView extends FrameLayout {
    private Context mContext;
    public ImageView mImg;
    public ImageView mIcon;
    public TextView mTitle;
    public FrameLayout mMaskFr;
    public FrameLayout mTitleMaskFr;

    private boolean isSelected = false;

    public FilterItemView(Context context) {
        super(context);

        mContext = context;
        initView();
    }

    private void initView() {

        int item_w = ShareData.PxToDpi_xhdpi(140);
        int item_h = ShareData.PxToDpi_xhdpi(140);
        int title_h = ShareData.PxToDpi_xhdpi(40);

        LayoutParams fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setBackgroundColor(0x00000000);
        setLayoutParams(fp);

        mImg = new ImageView(mContext);
        fp = new LayoutParams(item_w, item_h);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        addView(mImg, fp);

        mMaskFr = new FrameLayout(mContext);
        mMaskFr.setBackgroundColor(0x00000000);
        fp = new LayoutParams(item_w, item_h);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        addView(mMaskFr, fp);
        {
            mIcon = new ImageView(mContext);
            mIcon.setVisibility(INVISIBLE);
            mIcon.setImageResource(R.drawable.filter_selected_tips_icon);
            mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.CENTER;
            mMaskFr.addView(mIcon, fp);
        }

        mTitleMaskFr = new FrameLayout(mContext);
        mTitleMaskFr.setBackgroundColor(0xffffffff);
        fp = new LayoutParams(item_w, title_h);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fp.topMargin = item_h;
        addView(mTitleMaskFr, fp);

        mTitle = new TextView(mContext);
        mTitle.setTextColor(0xcc000000);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.0f);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setBackgroundColor(Color.TRANSPARENT);
        fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fp.bottomMargin = ShareData.PxToDpi_xhdpi(8);
        addView(mTitle, fp);
    }


    public void SetSelected(boolean selected) {
        if (isSelected == selected) return;
        isSelected = selected;
        if (selected) {
            mIcon.setVisibility(VISIBLE);
            mMaskFr.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988, 0.94f));
//            mMaskFr.setAlpha(0.94f);
            mTitleMaskFr.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988, 0.7f));
//            mTitleMaskFr.setAlpha(0.7f);
            mTitle.setTextColor(Color.WHITE);
        } else {
            mIcon.setVisibility(INVISIBLE);
            mMaskFr.setBackgroundColor(0x00000000);
            mTitleMaskFr.setBackgroundColor(0xffffffff);
            mTitle.setTextColor(0xcc000000);
        }
    }
}
