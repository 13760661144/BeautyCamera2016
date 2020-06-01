package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.camera3.config.StickerImageViewConfig;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import my.beautyCamera.R;

/**
 * @author Created by Gxx on 2017/10/31.
 */

public class StickerLocalItemView extends FrameLayout
{
    private ImageView mSelectedView;
    private ImageView mContentView;

    private CropCircleTransformation mCropCircleTransformation;

    private int mItemType = StickerImageViewConfig.ItemType.NORMAL_STICKER;

    public StickerLocalItemView(@NonNull Context context)
    {
        this(context, StickerImageViewConfig.ItemType.NORMAL_STICKER);
    }

    public StickerLocalItemView(@NonNull Context context, int type)
    {
        super(context);
        mItemType = type;
        mCropCircleTransformation = new CropCircleTransformation(context);
        initView(context, type);
    }

    public void ClearAll()
    {
        if (mContentView != null)
        {
            Glide.clear(mContentView);
        }
    }

    private void initView(Context context, int type)
    {
        mContentView = new ImageView(context);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContentView, params);

        mSelectedView = new ImageView(context);
        mSelectedView.setVisibility(GONE);
        mSelectedView.setImageResource(R.drawable.sticker_manager_item_select);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSelectedView, params);
    }

    public void setThumb(Object thumb)
    {
//        if (thumb != null && mContentView != null)
//        {
//            if (thumb instanceof Integer)
//            {
//                Glide.with(getContext()).load((Integer) thumb)
//                        .into(mContentView);
//            }
//            else if (thumb instanceof String)
//            {
//                Glide.with(getContext()).load((String) thumb)
//                        .into(mContentView);
//            }
//        }

        if (thumb != null && mContentView != null)
        {
            if (thumb instanceof Integer)
            {
                if (mItemType == StickerImageViewConfig.ItemType.NORMAL_STICKER)
                {
                    Glide.with(getContext()).load((Integer) thumb).into(mContentView);
                }
                else
                {
                    Glide.with(getContext()).load((Integer) thumb).bitmapTransform(mCropCircleTransformation).into(mContentView);
                }
            }
            else if (thumb instanceof String)
            {
                if (mItemType == StickerImageViewConfig.ItemType.NORMAL_STICKER)
                {
                    Glide.with(getContext()).load((String) thumb).into(mContentView);
                }
                else
                {
                    Glide.with(getContext()).load((String) thumb).bitmapTransform(mCropCircleTransformation).into(mContentView);
                }
            }
        }
    }

    public void setSelected(boolean selected)
    {
        if (mSelectedView != null)
        {
            mSelectedView.setVisibility(selected ? VISIBLE : GONE);
        }
    }
}
