package cn.poco.arWish;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.album.model.VideoInfo;

/**
 * Created by admin on 2018/1/29.
 */

public class ArAlbumPreviewAdapter extends PagerAdapter implements View.OnClickListener
{
    public void setSelectedListener(OnClickListener listener)
    {
        this.mListener = listener;
    }

    public interface OnClickListener
    {
        void onSelected(String path);
    }

    ArrayList<VideoInfo> mData;
    private OnClickListener mListener;

    public ArAlbumPreviewAdapter(ArrayList<VideoInfo> mData)
    {
        this.mData = mData;
    }

    @Override
    public int getCount()
    {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        ArAlbumPreviewView itemView = new ArAlbumPreviewView(container.getContext());
        itemView.setTag(position);
        itemView.setOnClickListener(this);
        itemView.setThumb(mData.get(position).getPath());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(itemView, params);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object instanceof ArAlbumPreviewView)
        {
            ((ArAlbumPreviewView) object).setOnClickListener(null);
            ((ArAlbumPreviewView) object).clearAll();
            container.removeView((View) object);
        }
    }

    public VideoInfo getInfoByIndex(int index)
    {
        return mData != null && index >= 0 && index < mData.size() ? mData.get(index) : null;
    }

    @Override
    public void onClick(View v)
    {
        int position = (int) v.getTag();
        if (mData != null && position >= 0 && position < mData.size())
        {
            VideoInfo info = getInfoByIndex(position);
            if (info != null && mListener != null)
            {
                mListener.onSelected(info.getPath());
//                Log.d("xxx", "ArAlbumPreviewAdapter --> onClick: video path == " + info.getPath());
            }
        }
    }
}
