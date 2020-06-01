package cn.poco.filter4.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterDownMore extends BaseItem
{
	private ImageView mLogo;
	private TextView mNum;
	private TextView mTitle;

	public FilterDownMore(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		setBackgroundColor(0xB3ffffff);
		LayoutParams params;
		mLogo = new ImageView(getContext());
//		mLogo.setImageResource(R.drawable.filter_recommend_default);
		Glide.with(getContext()).load(R.drawable.filter_recommend_default).into(mLogo);
		mLogo.setScaleType(ImageView.ScaleType.FIT_START);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP;
		addView(mLogo, params);

		FrameLayout bottom = new FrameLayout(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(41));
		params.gravity = Gravity.BOTTOM;
		addView(bottom,params);
		{
			FrameLayout downPage = new FrameLayout(getContext());
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(36), ViewGroup.LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.BOTTOM | Gravity.LEFT;
			params.leftMargin = ShareData.PxToDpi_xhdpi(16);
			bottom.addView(downPage, params);
			{
				ImageView downLogo = new ImageView(getContext());
				downLogo.setImageResource(R.drawable.photofactory_download_logo2);
				ImageUtils.AddSkin(getContext(), downLogo);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				downPage.addView(downLogo, params);

				mNum = new TextView(getContext());
				mNum.setGravity(Gravity.CENTER);
				mNum.setBackgroundResource(R.drawable.photofactory_download_num_bk);
				mNum.setTextColor(Color.WHITE);
				mNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 6f);
                mNum.setIncludeFontPadding(false);
				params = new LayoutParams(ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(15));
				params.gravity = Gravity.TOP | Gravity.RIGHT;
				mNum.setText(String.valueOf(0));
				downPage.addView(mNum, params);
			}
			mTitle = new TextView(getContext());
			mTitle.setText(R.string.recommend_download_more);
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
			mTitle.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
			params.leftMargin = ShareData.PxToDpi_xhdpi(56);
			bottom.addView(mTitle, params);
		}
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof FilterAdapter.DownloadItemInfo)
		{
			mNum.setText(String.valueOf(((FilterAdapter.DownloadItemInfo)info).num));
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

	}
}
