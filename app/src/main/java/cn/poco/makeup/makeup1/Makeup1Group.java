package cn.poco.makeup.makeup1;

import android.content.Context;
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
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseGroup;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;


public class Makeup1Group extends BaseGroup {
    protected Makeup1ListConfig m_config;
    protected ImageView mImageView;
    protected TextView mTextView;
    protected ImageView mFlag;
    protected ImageView m_maskImg;

    public int def_title_color = Color.WHITE; //title文字的颜色

    public int def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
    public int def_bk_out_color = 0x66ffffff;
    public float def_title_size = 9f;
    public int def_download_complete_res = R.drawable.sticker_new;
    public Makeup1ExAdapter.ItemInfo m_itemInfo;
    public Makeup1Group(@NonNull Context context, AbsConfig config) {
        super(context);
        m_config = (Makeup1ListConfig) config;
        init();
    }

    private void init()
    {
        LayoutParams params;
        mImageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_config.def_item_w);
        addView(mImageView, params);

        mFlag = new ImageView(getContext());
        mFlag.setVisibility(View.GONE);
        mFlag.setImageResource(def_download_complete_res);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        addView(mFlag, params);

        m_maskImg = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(m_maskImg, params);
        m_maskImg.setVisibility(GONE);


        mTextView = new TextView(getContext());
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(def_title_color);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, def_title_size);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_config.def_item_h - m_config.def_item_w);
        params.gravity = Gravity.BOTTOM;
        addView(mTextView, params);
    }

    @Override
    public void onSelected() {
        m_maskImg.setVisibility(VISIBLE);
    }

    @Override
    public void onUnSelected() {
        m_maskImg.setVisibility(GONE);
    }

    @Override
    public void onClick() {
        mFlag.setVisibility(GONE);
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        m_itemInfo = (Makeup1ExAdapter.ItemInfo)info;
        Glide.with(getContext()).load(m_itemInfo.m_logos[0]).into(mImageView);
        mTextView.setText(m_itemInfo.m_names[0]);
        mTextView.setBackgroundColor(m_itemInfo.m_maskColor);
        m_maskImg.setBackgroundColor((m_itemInfo.m_maskColor & 0x00FFFFFF) | 0xCC000000);
        if(m_itemInfo.m_style == RecommendExAdapter.ItemInfo.Style.NEW)
        {
            mFlag.setVisibility(VISIBLE);
        }
        else
        {
            mFlag.setVisibility(GONE);
        }
    }
}
