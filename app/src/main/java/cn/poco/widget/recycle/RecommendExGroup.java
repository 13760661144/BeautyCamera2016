package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseGroup;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushGroupRes;
import cn.poco.resource.FrameGroupRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendExGroup extends BaseGroup
{
	private ImageView mLogo;
	private TextView mTextView;
	private ImageView mFlag;
	public int def_title_color_out = 0xff737373; //title文字的颜色
	public int def_title_color_over = 0xffffffff; //title文字的颜色
	public int def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
	public int def_bk_out_color = Color.WHITE;
	public float def_title_size = 9f;
	private RecommendExAdapter.ItemInfo itemInfo;

	public RecommendExGroup(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
//		setBackgroundColor(def_bk_out_color);
		LayoutParams params;
		mLogo = new ImageView(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(146));
		addView(mLogo, params);

		mTextView = new TextView(getContext());
//		mTextView.setBackgroundColor(def_bk_out_color);

		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(def_title_color_out);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, def_title_size);
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
		if(info instanceof RecommendExAdapter.ItemInfo)
		{
			itemInfo = (RecommendExAdapter.ItemInfo)info;
//			if(itemInfo.m_logos[0] instanceof String)
//			{
//				mLogo.setImageBitmap(BitmapFactory.decodeFile((String)itemInfo.m_logos[0]));
//			}
//			else if(itemInfo.m_logos[0] instanceof Integer)
//			{
//				mLogo.setImageResource((Integer)itemInfo.m_logos[0]);
//			}
			Glide.with(getContext()).load(itemInfo.m_logos[0]).placeholder(new ColorDrawable(Color.WHITE)).into(mLogo);
			mTextView.setText(itemInfo.m_names[0]);
			if(itemInfo.m_style == RecommendExAdapter.ItemInfo.Style.NEW)
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
//		setBackgroundColor(def_bk_over_color);
		mTextView.setBackgroundColor(def_bk_over_color);
		mTextView.setTextColor(def_title_color_over);
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
		//去除new状态
//		if(itemInfo.m_style == RecommendExAdapter.ItemInfo.Style.NEW)
//		{
//			BaseRes res = (BaseRes)itemInfo.m_ex;
//			int themeId = res.m_id;
//			if(res instanceof BrushGroupRes)
//			{
//				cn.poco.resource.BrushResMgr.DeleteBrushGroupNewFlag(getContext(), themeId);
//			}
//			else if(res instanceof FrameGroupRes)
//			{
//				FrameResMgr.DeleteFrameGroupNewFlag(getContext(), themeId);
//			}
//			itemInfo.m_style = RecommendExAdapter.ItemInfo.Style.NORMAL;
//			mFlag.setVisibility(View.GONE);
//		}
	}

	@Override
	public void onOpen()
	{
		//去除new状态
		if(itemInfo.m_style == RecommendExAdapter.ItemInfo.Style.NEW)
		{
			BaseRes res = (BaseRes)itemInfo.m_ex;
			int themeId = res.m_id;
			if(res instanceof BrushGroupRes)
			{
				cn.poco.resource.BrushResMgr2.getInstance().DeleteGroupNewFlag(getContext(), themeId);
			}
			else if(res instanceof FrameGroupRes)
			{
				FrameResMgr2.getInstance().DeleteGroupNewFlag(getContext(), themeId);
			}
			itemInfo.m_style = RecommendExAdapter.ItemInfo.Style.NORMAL;
			mFlag.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClose()
	{

	}
}
