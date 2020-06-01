package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;

/**
 * @author lmx
 *         Created by lmx on 2017-12-14.
 */
public class SubItemView extends BaseItem
{
    public ImageView mIVLogo;
    public TextView mTVTitle;

    public SubItemView(@NonNull Context context)
    {
        super(context);
        init(context);
    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index)
    {

    }

    @Override
    public boolean performClick()
    {
        return super.performClick();
    }

    private void init(Context context)
    {
        boolean isChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        mIVLogo = new ImageView(getContext());
        mIVLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(68), CameraPercentUtil.HeightPxToPercent(68));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mIVLogo, params);

        mTVTitle = new TextView(getContext());
        mTVTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, isChinese ? 12 : 10f);
        mTVTitle.setTextColor(0xff717171);
        mTVTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        mTVTitle.setSingleLine();
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(68);
        this.addView(mTVTitle, params);
    }

    public void onSelect(boolean isSelect)
    {
        if (isSelect)
        {
            ImageUtils.AddSkin(getContext(), mIVLogo);
            mTVTitle.setTextColor(ImageUtils.GetSkinColor());
        }
        else
        {
            mIVLogo.clearColorFilter();
            mTVTitle.setTextColor(0xff717171);
        }
    }

    public void setLogo(Object logo)
    {
        Glide.with(getContext()).load(logo).into(mIVLogo);
    }

    public void setTitle(String title)
    {
        mTVTitle.setText(title);
    }

    public void clearLogo()
    {
        Glide.clear(mIVLogo);
    }

    @Override
    public void onSelected()
    {

    }

    @Override
    public void onUnSelected()
    {

    }

    @Override
    public void onClick()
    {

    }
}