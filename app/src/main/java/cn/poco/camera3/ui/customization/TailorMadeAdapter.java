package cn.poco.camera3.ui.customization;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.info.TailorMadeItemInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

public class TailorMadeAdapter extends RecyclerView.Adapter
{
    private final boolean mIsChinese;
    private ArrayList<TailorMadeItemInfo> mData;
    private int mSelIndex = 0;

    public TailorMadeAdapter(Context context, ArrayList<TailorMadeItemInfo> data)
    {
        mData = data;
        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        FrameLayout itemView = new FrameLayout(parent.getContext());
        RecyclerView.LayoutParams rl = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(rl);
        {
            PressedButton iv = new PressedButton(parent.getContext());
            iv.setId(R.id.ding_zhi_item_iv);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            itemView.addView(iv, params);

            TextView tv = new TextView(parent.getContext());
            tv.setId(R.id.ding_zhi_item_tv);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 12 : 10);
            tv.setGravity(Gravity.CENTER);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(73);
            itemView.addView(tv, params);
        }
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof MyHolder)
        {
            FrameLayout itemView = ((MyHolder) holder).getItemView();
            itemView.setTag(position);
            PressedButton iv = ((MyHolder) holder).getViewById(R.id.ding_zhi_item_iv);
            TextView tv = ((MyHolder) holder).getViewById(R.id.ding_zhi_item_tv);

            TailorMadeItemInfo itemInfo = mData.get(position);
            tv.setText(itemInfo.mText);

            if (itemInfo.mIsSelected)
            {
                tv.setTextColor(ImageUtils.GetSkinColor());
                iv.setButtonImage(itemInfo.mResId, itemInfo.mResId, ImageUtils.GetSkinColor());
            }
            else
            {
                tv.setTextColor(0xff717171);
                iv.setButtonImage(itemInfo.mResId, itemInfo.mResId);
            }

            itemView.setOnClickListener(mClickListener);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int index = (int) v.getTag();

            if (index == mSelIndex) return;

            mData.get(index).mIsSelected = true;
            mData.get(mSelIndex).mIsSelected = false;

            mSelIndex = index;
            notifyDataSetChanged();

            if (mItemClickListener != null)
            {
                mItemClickListener.onItemClick(v, mData.get(mSelIndex).mEx);
            }
        }
    };

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public void SetSelIndex(int index)
    {
        if (index < 0 || index >= getItemCount()) return;

        mData.get(mSelIndex).mIsSelected = false;
        notifyItemChanged(mSelIndex);

        mSelIndex = index;

        mData.get(mSelIndex).mIsSelected = true;
        notifyItemChanged(mSelIndex);
    }

    private OnItemClickListener mItemClickListener;

    public void SetOnItemClickListener(OnItemClickListener listener)
    {
        mItemClickListener = listener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int type);
    }
}
