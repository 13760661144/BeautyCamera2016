package cn.poco.camera3.beauty.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.utils.OnAnimationClickListener;

/**
 * @author lmx
 *         Created by lmx on 2017-12-08.
 */

public class ShapeSubAdapter extends RecyclerView.Adapter
{
    protected ArrayList<ShapeExAdapter.ShapeSubInfo> mList;
    protected OnSubItemClickListener mCB;
    protected int mPosition = -1;

    protected RecyclerView mParent;
    protected ShapeExAdapterConfig mConfig;

    public ShapeSubAdapter(ShapeExAdapterConfig config, ArrayList<ShapeExAdapter.ShapeSubInfo> list)
    {
        mConfig = config;
        mList = list;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        mParent = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setSubClickCB(OnSubItemClickListener listener)
    {
        this.mCB = listener;
    }


    public void setSelectPosition(int position)
    {
        mPosition = position;
        notifyDataSetChanged();
        if (mPosition != -1 && mParent != null)
        {
            mParent.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mParent != null)
                    {
                        mParent.smoothScrollToPosition(mPosition);

                        mParent.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mParent != null)
                                {
                                    scrollByCenter(mParent.getLayoutManager().findViewByPosition(mPosition));
                                }
                            }
                        }, 150);
                    }
                }
            }, 100);
        }
    }

    public void setSelectInfo(@ShapeDataType int type)
    {
        if (mList != null)
        {
            for (int i = 0, size = mList.size(); i < size; i++)
            {
                ShapeExAdapter.ShapeSubInfo info = mList.get(i);
                if (info != null && info.m_type == type)
                {
                    mPosition = i;
                    break;
                }
            }
        }
        setSelectPosition(mPosition);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        SubItemView view = new SubItemView(parent.getContext());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                mConfig.def_sub_item_w, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (mList == null) return;
        SubItemView itemView = (SubItemView) holder.itemView;
        itemView.setTag(position);
        ShapeExAdapter.ShapeSubInfo info = mList.get(position);
        if (info != null)
        {
            itemView.setLogo(info.m_sub_logo);
            itemView.setTitle(info.m_sub_name);
            itemView.onSelect(position == mPosition);
        }
        itemView.setOnTouchListener(mOnAnimationClickListener);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        if (holder != null && holder.itemView != null)
        {
            ((SubItemView) holder.itemView).clearLogo();
        }
        super.onViewRecycled(holder);
    }

    private void scrollByCenter(final View view)
    {
        if (view != null && mParent != null)
        {
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            //当前view移去中间的距离
            float viewCenter = dst[0] + view.getWidth() / 2f;
            float center = mConfig.def_parent_center_x + (mConfig.def_open_sub_parent_offset_left + mConfig.def_item_w + mConfig.def_open_sub_parent_left_margin) / 2f;
            float offset = viewCenter - center;
            mParent.smoothScrollBy((int) offset, 0);
        }
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            int position = mPosition;
            mPosition = (int) v.getTag();
            if (position != mPosition)
            {
                if (position != -1)
                {
                    notifyItemChanged(position);
                }
                notifyItemChanged(mPosition);
            }

            scrollByCenter(v);

            if (ShapeSubAdapter.this.mCB != null)
            {
                ShapeSubAdapter.this.mCB.onSubClick(mPosition, mList.get(mPosition));
            }
        }
    };

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    public void clear()
    {
        mConfig = null;
        mList = null;
        mPosition = -1;
        mCB = null;
        mOnAnimationClickListener = null;
    }

    public interface OnSubItemClickListener
    {
        void onSubClick(int position, ShapeExAdapter.ShapeSubInfo info);
    }

    public static final class SubItemViewHolder extends RecyclerView.ViewHolder
    {
        public SubItemViewHolder(View itemView)
        {
            super(itemView);
        }
    }

}
