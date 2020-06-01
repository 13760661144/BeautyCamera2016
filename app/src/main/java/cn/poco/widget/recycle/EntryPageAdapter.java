package cn.poco.widget.recycle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.ArrayList;
import java.util.List;

import cn.poco.album.utils.RoundCornerTransformation;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

/**
 * Created by admin on 2017/12/15.
 */

public class EntryPageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<CampaignInfo> mInfos = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private final int itemW = ShareData.PxToDpi_xhdpi(403);

    public EntryPageAdapter(Context context) {
        this.context = context;

    }

    public void setDates(List<CampaignInfo> info) {
        if(info != null)
        {
            mInfos.clear();
            mInfos.addAll(info);
//            mInfos.add(info.get(0));
            notifyDataSetChanged();
        }
    }

    public int getItemsW()
    {
        return mInfos.size() * itemW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemView itemView = new ItemView(parent.getContext());
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.imageView = itemView.mLogo;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Glide.with(context)
                .load(mInfos.get(position).getCoverUrl()).bitmapTransform(new CenterCrop(context),
                new RoundCornerTransformation(context, ShareData.PxToDpi_xhdpi(18), 0))
                .into(viewHolder.imageView);

        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnTouchListener(mOnAnimationClickListener);
    }

    protected OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if(mOnItemClickListener != null && v.getTag() != null){
                int position = (int) v.getTag();
                mOnItemClickListener.onClick(position, mInfos.get(position),((ItemView)v).hasDrawable());
            }
        }
    };

    @Override
    public int getItemCount() {
        return mInfos == null ? 0 : mInfos.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onClick(int position, CampaignInfo info,boolean isImageLoadComplete);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

        }
    }

    class ItemView extends FrameLayout
    {
        protected ImageView mLogo;
        public ItemView(@NonNull Context context)
        {
            super(context);
            mLogo = new ImageView(getContext());
            mLogo = new ImageView(context);
            mLogo.setLayoutParams(new ViewGroup.LayoutParams(itemW, ShareData.PxToDpi_xhdpi(230)));
            mLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(mLogo);
        }
        public boolean hasDrawable()
        {
            return mLogo.getDrawable() != null;
        }
    }
}
