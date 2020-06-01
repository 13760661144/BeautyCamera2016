package cn.poco.filterPendant;

/**
 * @author lmx
 * Created by lmx on 2016/12/1.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class ColorChangeLayoutV2 extends LinearLayout {

    private ArrayList<ColorItemInfo> mList;
    private Context mContext;
    private int selectedPosition = -1;

    private RecyclerView mRecyclerView;
    private ColorChangeAdapter mAdapter;


    public interface OnColorItemClickListener {
        void onDownClick();

        void onColorItemClick(int position, Object result);
    }

    public OnColorItemClickListener mItemOnClickListener;

    public void setmItemOnClickListener(OnColorItemClickListener mItemOnClickListener) {
        this.mItemOnClickListener = mItemOnClickListener;
    }

    public ColorChangeLayoutV2(Context context) {
        super(context);
        this.mContext = context;

        initView();
    }

    private void initView() {
        LayoutParams lp;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setBackgroundColor(Color.WHITE);

        ImageView downImg = new ImageView(mContext);
        downImg.setImageResource(R.drawable.filterpendant_colorpalette_down_btn);
        downImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        downImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemOnClickListener != null) {
                    mItemOnClickListener.onDownClick();
                }
            }
        });
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        downImg.setLayoutParams(lp);
        this.addView(downImg);

        mRecyclerView = new RecyclerView(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(232));
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(mRecyclerView, lp);
    }


    public void SetData(ArrayList<ColorItemInfo> list) {
        this.mList = list;
        if (mAdapter == null) {
            mAdapter = new ColorChangeAdapter();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setSelectedPosition(int selectedPosition) {
        if (this.selectedPosition != selectedPosition) {
            this.selectedPosition = selectedPosition;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(selectedPosition);
            }
        }
    }

    public void setSelectedColor(int color) {
        boolean select = false;
        if (mList != null) {
            for (int i = 0, size = mList.size();i < size; i++)
            {
                ColorItemInfo colorItemInfo = mList.get(i);
                if (colorItemInfo.m_color == color) {
                    setSelectedPosition(colorItemInfo.m_position);
                    select = true;
                }
            }
        }
        if (!select) {
            setSelectedPosition(-1);
        }
    }

    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public class ColorChangeAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT);
            ColorChangeItem view = new ColorChangeItem(parent.getContext());
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ColorChangeItem item = (ColorChangeItem) holder.itemView;
            item.setTag(position);
            if (mList != null) {
                final ColorItemInfo info = mList.get(position);
                item.setBackgroundColor(info.m_pre_Color);
                item.SetSelected(info.m_position == ColorChangeLayoutV2.this.selectedPosition);
                item.setOnTouchListener(new OnAnimationClickListener() {
                    @Override
                    public void onAnimationClick(View v) {
                        if (ColorChangeLayoutV2.this.mItemOnClickListener != null) {
//                            Log.d("mmm", "onClick: " + info.m_id);
                            mItemOnClickListener.onColorItemClick(position, info);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    class ColorChangeItem extends FrameLayout {

        private boolean isSelected = false;
        private ImageView icon;

        public ColorChangeItem(Context context) {
            super(context);
            initView(context);
        }

        public void initView(Context context) {
            icon = new ImageView(context);
            icon.setVisibility(INVISIBLE);
            icon.setImageResource(R.drawable.advanced_beautify_frame_color_palette_selected_icon);
            LayoutParams fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.CENTER;
            fp.setMargins(ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20));
            icon.setLayoutParams(fp);
            this.addView(icon);
        }

        public void SetSelected(boolean selected) {
            if (this.isSelected != selected) {
                this.isSelected = selected;
                icon.setVisibility(selected ? VISIBLE : INVISIBLE);
            }
        }
    }
}
