package cn.poco.makeup.makeup2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseGroup;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;


public class Makeup2Group extends BaseGroup {
    private ImageView mImageView;
    private TextView mTextView;
    private ImageView mFlag;
    private MaskColorView m_maskColorView;

    public int def_title_color = Color.WHITE; //title文字的颜色

    public int def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
    public int def_bk_out_color = 0x66ffffff;
    public float def_title_size = 9f;
    public int def_download_complete_res = R.drawable.sticker_new;
    private RecommendExAdapter.ItemInfo itemInfo;
    private AbsExConfig m_config;
    public Makeup2Group(@NonNull Context context,AbsExConfig config) {
        super(context);
        m_config = config;
        init();
    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        itemInfo = (RecommendExAdapter.ItemInfo)info;

        Glide.with(getContext()).load(itemInfo.m_logos[0]).into(mImageView);
        mTextView.setText(itemInfo.m_names[0]);
    }

    private void init()
    {
        setBackgroundColor(def_bk_out_color);
        LayoutParams params;
        mImageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_config.def_item_w);
        addView(mImageView, params);

        mTextView = new TextView(getContext());
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(def_title_color);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, def_title_size);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_config.def_item_h - m_config.def_item_w);
        params.gravity = Gravity.BOTTOM;
        addView(mTextView, params);

        mFlag = new ImageView(getContext());
        mFlag.setVisibility(View.GONE);
        mFlag.setImageResource(def_download_complete_res);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        addView(mFlag, params);

        m_maskColorView = new MaskColorView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (m_config.def_item_h - ShareData.PxToDpi_xhdpi(46)*0.91f));
        addView(m_maskColorView,params);
        m_maskColorView.setAlpha(0.5f);
        m_maskColorView.setVisibility(GONE);
    }

    @Override
    public void onSelected() {
        setBackgroundColor(def_bk_over_color);
        if(itemInfo != null && itemInfo.m_ex instanceof MakeupSPage.Makeup2ResGroup)
        {
            MakeupSPage.Makeup2ResGroup temp = (MakeupSPage.Makeup2ResGroup) itemInfo.m_ex;
            m_maskColorView.setMaskColor(temp.m_maskColor);
            m_maskColorView.setVisibility(VISIBLE);
            mTextView.setBackgroundColor(temp.m_maskColor);
        }
    }

    @Override
    public void onUnSelected() {
        setBackgroundColor(def_bk_out_color);
        m_maskColorView.setVisibility(GONE);
        if(itemInfo != null && itemInfo.m_ex instanceof MakeupSPage.Makeup2ResGroup)
        {
            MakeupSPage.Makeup2ResGroup temp = (MakeupSPage.Makeup2ResGroup) itemInfo.m_ex;
            mTextView.setBackgroundColor(temp.m_maskColor);
        }
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    private class MaskColorView extends View
    {
        private int m_maskColor;
        public MaskColorView(Context context) {
            super(context);
        }

        public void setMaskColor(int color)
        {
            m_maskColor = color;
            invalidate();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(m_maskColor);
        }
    }
}
