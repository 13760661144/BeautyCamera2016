package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/7/10.
 */

public class DownMoreView2 extends BaseDownMoreRecommendView
{
    protected TextView mNumTextView;
    private TextView mNumView;

    public DownMoreView2(Context context, @NonNull AbsConfig mConfig, Object[] logos, int num)
    {
        super(context, mConfig, logos, num);
    }

    @Override
    public void initView()
    {
        super.initView();

        LayoutParams params;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.photofactory_download_logo);
        ImageUtils.AddSkin(getContext(), imageView);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mBaseFr.addView(imageView, params);

        mNumTextView = new TextView(getContext());
        mNumTextView.setText(R.string.recommend_download_more);
        mNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f);
        mNumTextView.setTextColor(ImageUtils.GetSkinColor(Color.RED));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = ShareData.PxToDpi_xhdpi(55);
        mBaseFr.addView(mNumTextView, params);

        mNumView = new TextView(getContext());
        mNumView.setGravity(Gravity.CENTER);
        mNumView.setBackgroundResource(R.drawable.photofactory_download_num_bk);
        mNumView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8.5f);
        mNumView.setTextColor(Color.WHITE);
        if (mNum > 0) {
            if (mNumView.getVisibility() != View.VISIBLE) {
                mNumView.setVisibility(View.VISIBLE);
            }
            mNumView.setText(String.valueOf(mNum));
        } else {
            mNumView.setVisibility(View.GONE);
        }
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(32);
        params.leftMargin = ShareData.PxToDpi_xhdpi(32);
        mBaseFr.addView(mNumView, params);
    }

    @Override
    public void SetData(Object[] logos, int num, int index)
    {
        super.SetData(logos, num, index);
        if (mNum > 0) {
            if (mNumView.getVisibility() != View.VISIBLE) {
                mNumView.setVisibility(View.VISIBLE);
            }
            mNumView.setText(String.valueOf(mNum));
        } else {
            mNumView.setVisibility(View.GONE);
        }
    }
}
