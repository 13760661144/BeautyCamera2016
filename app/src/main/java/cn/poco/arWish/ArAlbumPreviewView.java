package cn.poco.arWish;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2018/1/29.
 */

public class ArAlbumPreviewView extends FrameLayout
{
    private ImageView mItemView;

    public ArAlbumPreviewView(Context context)
    {
        super(context);
        initView(context);
    }

    private void initView(Context context)
    {
        mItemView = new ImageView(context);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mItemView, params);

        ImageView playIcon = new ImageView(context);
        playIcon.setImageResource(R.drawable.ar_preview_logo);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(120), CameraPercentUtil.WidthPxToPercent(120));
        params.gravity = Gravity.CENTER;
        addView(playIcon, params);
    }

    public void setThumb(Object thumb)
    {
        if (thumb != null)
        {
            if (thumb instanceof String)
            {
                Glide.with(getContext()).load(thumb).asBitmap().into(mItemView);
            }
        }
    }

    public void clearAll()
    {
        Glide.clear(mItemView);
    }
}
