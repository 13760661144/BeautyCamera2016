package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.GlassRes;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.MosaicRes;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendItem extends BaseItem
{
	private ImageView mImageView;
	private TextView mTextView;
	private ImageView mFlag;
	public int def_title_color_out = 0xff737373; //title文字的颜色
	public int def_title_color_over = 0xffffffff; //title文字的颜色
	public int def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
	public int def_bk_out_color = Color.WHITE;
	private RecommendAdapter.ItemInfo itemInfo;

	public RecommendItem(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
//		setBackgroundColor(def_bk_out_color);
		LayoutParams params;
		mImageView = new ImageView(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(146));
		addView(mImageView, params);

		mTextView = new TextView(getContext());
		mTextView.setBackgroundColor(def_bk_out_color);
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(def_title_color_out);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
		params.gravity = Gravity.BOTTOM;
		addView(mTextView, params);

		mFlag = new ImageView(getContext());
		mFlag.setVisibility(View.GONE);
		mFlag.setImageResource(R.drawable.sticker_new);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mFlag, params);
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		itemInfo = (RecommendAdapter.ItemInfo)info;
		if(itemInfo.m_logo instanceof String)
		{
			mImageView.setImageBitmap(BitmapFactory.decodeFile((String)itemInfo.m_logo));
		}
		else if(itemInfo.m_logo instanceof Integer)
		{
			mImageView.setImageResource((Integer)itemInfo.m_logo);
		}
		mTextView.setText(itemInfo.m_name);
		if(itemInfo.m_style == RecommendAdapter.ItemInfo.Style.NEW)
		{
			mFlag.setVisibility(View.VISIBLE);
		}
		else
		{
			mFlag.setVisibility(View.GONE);
		}
	}

	@Override
	public void onSelected()
	{
		//		setBackgroundColor(def_bk_over_color);
		mTextView.setBackgroundColor(def_bk_over_color);
		mTextView.setTextColor(def_title_color_over);

		//去除new状态
		if(itemInfo.m_style == RecommendAdapter.ItemInfo.Style.NEW)
		{
			BaseRes res = (BaseRes)itemInfo.m_ex;
			int themeId = res.m_id;
			if(res instanceof FrameExRes)
			{
				//相框
				FrameExResMgr2.getInstance().DeleteNewFlag(getContext(), themeId);
			}else if(res instanceof GlassRes){
				//毛玻璃
				GlassResMgr2.getInstance().DeleteNewFlag(getContext(), themeId);
			}else if(res instanceof MosaicRes){
				//马赛克
				MosaicResMgr2.getInstance().DeleteNewFlag(getContext(), themeId);
			}
			itemInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
			mFlag.setVisibility(View.GONE);
		}

	}

	@Override
	public void onUnSelected()
	{
		//		setBackgroundColor(def_bk_out_color);
		mTextView.setBackgroundColor(def_bk_out_color);
		mTextView.setTextColor(def_title_color_out);
	}

	@Override
	public void onClick()
	{
//		//去除new状态
//		if(itemInfo.m_style == RecommendAdapter.ItemInfo.Style.NEW)
//		{
//			BaseRes res = (BaseRes)itemInfo.m_ex;
//			int themeId = res.m_id;
//			if(res instanceof FrameExRes)
//			{
//				FrameExResMgr.DeleteSimpleFrameNewFlag(getContext(), themeId);
//			}
//			itemInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
//			mFlag.setVisibility(View.GONE);
//		}
	}
}
