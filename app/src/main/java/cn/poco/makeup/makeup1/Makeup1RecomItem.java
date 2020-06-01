package cn.poco.makeup.makeup1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import my.beautyCamera.R;


public class Makeup1RecomItem extends Makeup1Group {
    private ImageView m_flag;
    private Makeup1ExAdapter.RecommendItemInfo m_itemInfo;
    public Makeup1RecomItem(@NonNull Context context, AbsConfig config) {
        super(context,config);
        addInit();
    }

    private void addInit()
    {
        m_flag = new ImageView(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.RIGHT;
        m_flag.setLayoutParams(fl);
        this.addView(m_flag);
        m_flag.setImageResource(R.drawable.sticker_recom);
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnSelected() {

    }

    @Override
    public void onClick() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        m_itemInfo = (Makeup1ExAdapter.RecommendItemInfo)info;
        Glide.with(getContext()).load(m_itemInfo.m_logos[0]).into(mImageView);
        mTextView.setText(m_itemInfo.m_names[0]);
        mTextView.setBackgroundColor(m_itemInfo.m_bkColor);
    }
}
