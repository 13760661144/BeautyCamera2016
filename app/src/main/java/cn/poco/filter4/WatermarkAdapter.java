package cn.poco.filter4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.filter4.view.WaterMarkItemView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

/**
 * @author lmx
 *         Created by lmx on 2017/3/28.
 */

public class WatermarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_CUS = 0x00000000;
    public static final int TYPE_VIEW = 0x00000001;

    private Context mContext;
    private ArrayList<WatermarkItem> mLists;
    private int mPosition = -1;
    private OnItemClickListener mListener;


    public WatermarkAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void SetData(ArrayList<WatermarkItem> lists) {
        this.mLists = lists;
    }

    public void clear() {
        this.mLists = null;
        this.mListener = null;
    }

    public int GetPosition() {
        return mPosition;
    }

    public void SetSelectedId(int id) {
        if (mLists != null) {
            for (int i = 0, size = mLists.size(); i < size; i++) {
                WatermarkItem item = mLists.get(i);
                if (item != null && item.mID == id) {
                    this.mPosition = i;
                    break;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT);
        View view = null;
        if (viewType == TYPE_CUS) {
            view = new WaterMarkItemView(parent.getContext(), 0);
        } else {
            view = new WaterMarkItemView(parent.getContext(), 1);
        }
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final WatermarkItem item = mLists.get(position);
        WaterMarkItemView view = (WaterMarkItemView) holder.itemView;
        view.setTag(position);
        view.SetSelected(mPosition == position);
        if (item.thumb instanceof Bitmap) {
            view.mImg.setImageBitmap((Bitmap) item.thumb);
        } else if (item.thumb instanceof Integer) {
            view.mImg.setImageResource((Integer) item.thumb);
        }
        view.setOnTouchListener(mOnAnimationClickListener);
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            int position = (Integer) v.getTag();

            if (position != mPosition)
            {
                if (mListener != null) {
                    mListener.onItemClick(position, mLists.get(position));
                }
                int lastP = mPosition;
                mPosition = position;

                if (lastP != -1)
                {
                    notifyItemChanged(lastP);
                }
                notifyItemChanged(mPosition);

            }
        }
    };

    @Override
    public int getItemCount() {
        return mLists != null ? mLists.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_CUS : TYPE_VIEW;
    }


    public void setListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, WatermarkItem item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = 0;
            outRect.bottom = 0;

            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = ShareData.PxToDpi_xhdpi(0);
                outRect.right = outRect.left;
            } else {
                outRect.left = ShareData.PxToDpi_xhdpi(10);
                outRect.right = outRect.left;
            }
        }
    }
}
