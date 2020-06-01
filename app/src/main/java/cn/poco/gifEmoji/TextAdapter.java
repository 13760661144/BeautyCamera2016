package cn.poco.gifEmoji;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by admin on 2017/6/22.
 */

public class TextAdapter extends RecyclerView.Adapter
{
    private ArrayList<GIFTextInfo> mData;
    private OnItemClickListener mItemListener;
    private OnAnimationClickListener mTouchListener;
    private int ITEM_TOP = 1;

    public TextAdapter()
    {
        mTouchListener = new OnAnimationClickListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if (ss_uiEnabled)
                {
                    if (ss_view != view && ss_isDown)
                    {
                        return false;
                    }
                    ss_view = view;
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            ss_view.setPressed(true);
                            ss_isDown = true;
                            ss_clickRect = new Rect(0, 0, view.getWidth(), view.getHeight());
                            onTouch(view);
                            ss_touchAnimator.start();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            ss_view.setPressed(false);
                            ss_isDown = false;
                            if (isInClickArea((int) event.getX(), (int) event.getY()))
                            {
                                onRelease(view);
                                ss_touchAnimator.cancel();
                                startClickAnimation(view);
                                break;
                            }
                        case MotionEvent.ACTION_CANCEL:
                            ss_view.setPressed(false);
                            ss_isDown = false;
                            ss_touchAnimator.cancel();
                            ss_restoreAnimator.setDuration((int) (ss_touchDuration * (1 - view.getScaleX()) / (1 - ss_scaleSmall)));
                            ss_restoreAnimator.setFloatValues(view.getScaleX(), 1);
                            ss_restoreAnimator.start();
                            break;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onAnimationClick(View v)
            {

            }

            @Override
            public void onTouch(View v)
            {

            }

            @Override
            public void onRelease(View v)
            {
                if (v instanceof TextView && mItemListener != null)
                {
                    TextView et = (TextView)v;
                    mItemListener.onItemClick(et.getText().toString());
                }
            }
        };
        mTouchListener.setAnimDuration(100);
    }

    public boolean SetData(ArrayList<GIFTextInfo> data)
    {
        if (data != null)
        {
            mData = data;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void ClearMemory()
    {
        mTouchListener = null;
        mItemListener = null;
    }

    public void SetOnItemClickListener(OnItemClickListener listener)
    {
        mItemListener = listener;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return ITEM_TOP;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        FrameLayout itemView = new FrameLayout(parent.getContext());
        itemView.setBackgroundColor(Color.TRANSPARENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        itemView.setLayoutParams(params);
        {
            if (viewType == ITEM_TOP)
            {
                View topLine = new View(parent.getContext());
                topLine.setBackgroundColor(0xFFDFDFDF);
                FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                itemView.addView(topLine, fl);
            }

            TextView tv = new TextView(parent.getContext());
            tv.setId(R.id.edit_page_list_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTextColor(0xFF666666);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
            itemView.addView(tv, fl);

            View bottomLine = new View(parent.getContext());
            bottomLine.setBackgroundColor(0xFFDFDFDF);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            fl.gravity = Gravity.BOTTOM;
            itemView.addView(bottomLine, fl);
        }
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof MyHolder)
        {
            if (mData != null && mData.size() > 0)
            {
                final GIFTextInfo itemInfo = mData.get(position);
                FrameLayout itemView = ((MyHolder) holder).getItemView();

                final TextView tv = ((MyHolder) holder).getViewById(R.id.edit_page_list_text);

                if (itemInfo.mName != null)
                {
                    tv.setText(itemInfo.mName);
                }

                tv.setOnTouchListener(mTouchListener);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mData == null ? 0 : mData.size();
    }

    public interface OnItemClickListener
    {
        void onItemClick(String content);
    }
}
