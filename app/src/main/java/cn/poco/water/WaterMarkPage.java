package cn.poco.water;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.framework.IPage;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.water.site.WaterMarkPageSite;
import my.beautyCamera.R;

/**
 * 水印设置
 * Created by Gxx on 2017/12/13.
 */

public class WaterMarkPage extends IPage
{
	private WaterMarkPageSite mSite;

	private int mTopBarHeight;
	private int mSpanCount;

	private ImageView mBackBtn;
	private ImageView mSaveBtn;
	private RecyclerView mWaterMarkLayout;
	private WatermarkAdapter mAdapter;

	private OnAnimationClickListener onAnimationClickListener;

	public WaterMarkPage(Context context, WaterMarkPageSite site)
	{
		super(context, site);
		mSite = site;
		mSpanCount = 2;
		mTopBarHeight = CameraPercentUtil.WidthPxToPercent(90);
		initCB();
		initView(context);
	}

	private void initCB()
	{
		onAnimationClickListener = new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(v == mBackBtn)
				{
					onBack();
				}
				else if(v == mSaveBtn)
				{
					int id = mAdapter.getSelectedID();
					SettingInfoMgr.GetSettingInfo(v.getContext()).SetPhotoWatermarkId(id);
					SettingInfoMgr.Save(v.getContext());
					onBack();
				}
			}
		};
	}

	private void initView(Context context)
	{
		FrameLayout topLayout = new FrameLayout(context);
		topLayout.setBackgroundColor(Color.WHITE);
		FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(topLayout, params);
		{
			mBackBtn = new ImageView(context);
			mBackBtn.setOnTouchListener(onAnimationClickListener);
			mBackBtn.setImageResource(R.drawable.framework_back_btn);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			topLayout.addView(mBackBtn, params);
			ImageUtils.AddSkin(context, mBackBtn);

			TextView title = new TextView(context);
			title.setText(getContext().getString(R.string.setting_topbar_title));
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			title.setTextColor(0xff333333);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			topLayout.addView(title, params);

			mSaveBtn = new ImageView(context);
			mSaveBtn.setOnTouchListener(onAnimationClickListener);
			mSaveBtn.setImageResource(R.drawable.watermark_page_save_logo);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
			params.rightMargin = CameraPercentUtil.WidthPxToPercent(10);
			topLayout.addView(mSaveBtn, params);
			ImageUtils.AddSkin(context, mSaveBtn);
		}

		mWaterMarkLayout = new RecyclerView(context);
		mWaterMarkLayout.setBackgroundColor(0xFF262626);
		((SimpleItemAnimator)mWaterMarkLayout.getItemAnimator()).setSupportsChangeAnimations(false);
		mWaterMarkLayout.getItemAnimator().setChangeDuration(0);
		mWaterMarkLayout.addItemDecoration(new WatermarkDecoration());
		mWaterMarkLayout.setLayoutManager(new GridLayoutManager(context, mSpanCount));
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - mTopBarHeight);
		params.topMargin = mTopBarHeight;
		addView(mWaterMarkLayout, params);

		initData(context);
	}

	private void initData(Context context)
	{
		mAdapter = new WatermarkAdapter(context, mSpanCount);
		mWaterMarkLayout.setAdapter(mAdapter);
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{

	}

	@Override
	public void onBack()
	{
		mSite.onBack(getContext());
	}

	@Override
	public void onClose()
	{
		super.onClose();

		if(mWaterMarkLayout != null)
		{
			int size = mWaterMarkLayout.getChildCount();
			for(int index = 0; index < size;index++)
			{
				View view = mWaterMarkLayout.getChildAt(index);
				if(view != null && view instanceof WatermarkItemView)
				{
					((WatermarkItemView)view).clearAll();
					((WatermarkItemView)view).removeAllViews();
				}
				mWaterMarkLayout.removeAllViews();
			}
		}
	}
}
