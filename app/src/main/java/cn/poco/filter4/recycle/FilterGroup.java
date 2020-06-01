package cn.poco.filter4.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseGroup;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FilterGroupRes;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterGroup extends BaseGroup
{
	private ImageView mLogo;
	private TextView mBottomText;
	private View mBkColor;
	private ImageView mBackLogo;
	private TextView mCenterText;
	private TextView mSelectedText;
	private ImageView mFlag;
	private boolean isSelected = false;
	private boolean isHasOpen = false;
	private FilterAdapter.ItemInfo itemInfo;

	public FilterGroup(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		LayoutParams params;
		mLogo = new ImageView(getContext());
		mLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);  //默认样式的有黑边
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.TOP;
		addView(mLogo, params);

		mBkColor = new View(getContext());
		mBkColor.setVisibility(View.GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.TOP;
		addView(mBkColor, params);


		mBottomText = new TextView(getContext());
		mBottomText.setGravity(Gravity.CENTER);
		mBottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
//		mBottomText.setTextColor(ImageUtils.GetSkinColor(Color.WHITE));
		mBottomText.setTextColor(0xe6ffffff);  //90白色
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
		params.gravity = Gravity.BOTTOM;
		addView(mBottomText, params);

		mCenterText = new TextView(getContext());
		mCenterText.setVisibility(View.GONE);
		mCenterText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
		mCenterText.getPaint().setFakeBoldText(true);
		mCenterText.setTextColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(30);
		params.topMargin = ShareData.PxToDpi_xhdpi(40);
		addView(mCenterText, params);

		mBackLogo = new ImageView(getContext());
		mBackLogo.setImageResource(R.drawable.filter_scroll_back_icon);
		mBackLogo.setVisibility(View.GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(20);
		addView(mBackLogo, params);

		mFlag = new ImageView(getContext());
		mFlag.setVisibility(View.GONE);
		mFlag.setImageResource(R.drawable.sticker_new);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mFlag, params);

		mSelectedText = new TextView(getContext());
		mSelectedText.setVisibility(View.GONE);
		mSelectedText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f);
//		mSelectedText.setTextColor(Color.WHITE);
		mSelectedText.getPaint().setFakeBoldText(true);
		mSelectedText.setTextColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(mSelectedText, params);
	}
	public void reStore()
	{
		isSelected = false;
		isHasOpen = false;
		mSelectedText.setVisibility(View.GONE);
		mFlag.setVisibility(View.GONE);
		mBackLogo.setVisibility(View.GONE);
		mCenterText.setVisibility(View.GONE);
		mBkColor.setVisibility(View.GONE);
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof FilterAdapter.ItemInfo)
		{
			reStore();

			itemInfo = (FilterAdapter.ItemInfo)info;
			mCenterText.setText(itemInfo.m_names[0]);
			mBottomText.setText(itemInfo.m_names[0]);
			mSelectedText.setText(itemInfo.m_names[0]);

			mBottomText.setBackgroundColor(itemInfo.m_bkColor);
			mBottomText.getBackground().setAlpha(240);   //94
			mBkColor.setBackgroundColor(itemInfo.m_bkColor);
			mBkColor.getBackground().setAlpha(204);   //80
//			if(itemInfo.m_logos[0] instanceof String)
//			{
//				mLogo.setImageBitmap(BitmapFactory.decodeFile((String)itemInfo.m_logos[0]));
//			}
//			else if(itemInfo.m_logos[0] instanceof Integer)
//			{
//				mLogo.setImageResource((Integer)itemInfo.m_logos[0]);
//				Glide.with(getContext()).load((Integer)itemInfo.m_logos[0]).into(mLogo);
//			}
			Glide.with(getContext()).load(itemInfo.m_logos[0]).into(mLogo);

			if(itemInfo.m_style == FilterAdapter.ItemInfo.Style.NEW)
			{
				mFlag.setVisibility(View.VISIBLE);
			}
			else
			{
				mFlag.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onSelected()
	{
		isSelected = true;
		if(isHasOpen)
		{
			mBackLogo.setVisibility(View.VISIBLE);
			mCenterText.setVisibility(View.VISIBLE);
			mSelectedText.setVisibility(View.GONE);
		}
		else
		{
			mBackLogo.setVisibility(View.GONE);
			mCenterText.setVisibility(View.GONE);
			mSelectedText.setVisibility(View.VISIBLE);
		}
		mBkColor.setVisibility(View.VISIBLE);
		mBottomText.setVisibility(View.GONE);
	}

	@Override
	public void onUnSelected()
	{
		isSelected = false;
		if(isHasOpen)
		{
			mBkColor.setVisibility(View.VISIBLE);
			mBackLogo.setVisibility(View.VISIBLE);
			mCenterText.setVisibility(View.VISIBLE);
			mBottomText.setVisibility(View.GONE);
		}
		else
		{
			mBkColor.setVisibility(View.GONE);
			mBackLogo.setVisibility(View.GONE);
			mCenterText.setVisibility(View.GONE);
			mBottomText.setVisibility(View.VISIBLE);
		}
		mSelectedText.setVisibility(View.GONE);
	}

	@Override
	public void onClick()
	{
//		//去除new状态
//		if(itemInfo.m_style == FilterAdapter.ItemInfo.Style.NEW)
//		{
//			BaseRes res = (BaseRes)itemInfo.m_ex;
//			int themeId = res.m_id;
//			if(res instanceof FilterGroupRes)
//			{
//				cn.poco.resource.FilterResMgr.DeleteFilterGroupNewFlag(getContext(), themeId);
//			}
//			itemInfo.m_style = FilterAdapter.ItemInfo.Style.NORMAL;
//			mFlag.setVisibility(View.GONE);
//		}
		//TODO 阿玛尼商业201708

		if (itemInfo != null && itemInfo.m_uri == 1362)
		{
			Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0065502457/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
		}
	}

	@Override
	public void onOpen()
	{

		//去除new状态
		if(itemInfo.m_style == FilterAdapter.ItemInfo.Style.NEW)
		{
			BaseRes res = (BaseRes)itemInfo.m_ex;
			int themeId = res.m_id;
			if(res instanceof FilterGroupRes)
			{
				cn.poco.resource.FilterResMgr2.getInstance().DeleteGroupNewFlag(getContext(), themeId);
			}
			itemInfo.m_style = FilterAdapter.ItemInfo.Style.NORMAL;
			mFlag.setVisibility(View.GONE);
		}

		isHasOpen = true;
		mBkColor.setVisibility(View.VISIBLE);
		mBottomText.setVisibility(View.GONE);
		mCenterText.setVisibility(View.VISIBLE);
		mBackLogo.setVisibility(View.VISIBLE);
		mSelectedText.setVisibility(View.GONE);

	}

	@Override
	public void onClose()
	{
		isHasOpen = false;
		if(isSelected)
		{
			mBkColor.setVisibility(View.VISIBLE);
			mSelectedText.setVisibility(View.VISIBLE);
			mBottomText.setVisibility(View.GONE);
		}
		else
		{
			mBkColor.setVisibility(View.GONE);
			mSelectedText.setVisibility(View.GONE);
			mBottomText.setVisibility(View.VISIBLE);
		}
		mBackLogo.setVisibility(View.GONE);
		mCenterText.setVisibility(View.GONE);
	}
}
