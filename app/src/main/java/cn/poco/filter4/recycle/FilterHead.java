package cn.poco.filter4.recycle;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterHead extends BaseItem
{
	public ImageView mBlur;
	public ImageView mDark;
	private FilterAdapter.HeadItemInfo itemInfo;

	public FilterHead(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		LayoutParams params;
		mBlur = new ImageView(getContext());
		mBlur.setImageResource(R.drawable.beautify_blur_btn_out);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//		params.topMargin = ShareData.PxToDpi_xhdpi(30);
		params.topMargin = ShareData.PxToDpi_xhdpi(20);
		addView(mBlur, params);

		mDark = new ImageView(getContext());
		mDark.setImageResource(R.drawable.beautify_dark_btn_out);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//		params.bottomMargin = ShareData.PxToDpi_xhdpi(30);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(20);
		addView(mDark, params);
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof FilterAdapter.HeadItemInfo)
		{
			itemInfo = (FilterAdapter.HeadItemInfo)info;
			if(itemInfo.isSelectBlur)
			{
				mBlur.setImageBitmap(ImageUtils.AddSkin(getContext(),
						BitmapFactory.decodeResource(getResources(), R.drawable.beautify_blur_btn_over)));
			}
			else
			{
				mBlur.setImageResource(R.drawable.beautify_blur_btn_out);
			}
			if(itemInfo.isSelectDark)
			{
				mDark.setImageBitmap(ImageUtils.AddSkin(getContext(),
						BitmapFactory.decodeResource(getResources(), R.drawable.beautify_dark_btn_over)));
			}
			else
			{
				mDark.setImageResource(R.drawable.beautify_dark_btn_out);
			}
		}
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
		if(itemInfo.isSelectBlur)
		{
			mBlur.setImageResource(R.drawable.beautify_blur_btn_over);
		}
		else
		{
			mBlur.setImageResource(R.drawable.beautify_blur_btn_out);
		}
		if(itemInfo.isSelectDark)
		{
			mDark.setImageResource(R.drawable.beautify_dark_btn_over);
		}
		else
		{
			mDark.setImageResource(R.drawable.beautify_dark_btn_out);
		}
	}
}
