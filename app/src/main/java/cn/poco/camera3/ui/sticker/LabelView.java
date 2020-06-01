package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.graphics.Color;
import android.os.MessageQueue;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

import static android.os.Looper.myQueue;

/**
 * 贴纸素材标签
 * Created by Gxx on 2017/10/12.
 */

public class LabelView extends LinearLayout implements View.OnClickListener
{
	private FrameLayout mNonLayout;
	private RecyclerView mContentView;
	private LabelAdapter mLabelAdapter;

	private LinearLayoutManager mLayoutManager;
	private ImageView mNonIcon;
	private TextView mNonText;
	private StickerInnerListener mUIListener;

	public LabelView(Context context)
	{
		super(context);
		setOrientation(HORIZONTAL);
		initView(context);
	}

	private void initView(Context context)
	{
		mNonLayout = new FrameLayout(getContext());
		mNonLayout.setOnClickListener(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(132), CameraPercentUtil.WidthPxToPercent(80));
		addView(mNonLayout, params);
		{
			// 空标签
			mNonIcon = new ImageView(getContext());
			FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fp.gravity = Gravity.CENTER_VERTICAL;
			fp.leftMargin = CameraPercentUtil.WidthPxToPercent(32);
			mNonLayout.addView(mNonIcon, fp);

			mNonText = new TextView(getContext());
			mNonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			mNonText.setText("无");
			fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fp.gravity = Gravity.CENTER_VERTICAL;
			fp.leftMargin = CameraPercentUtil.WidthPxToPercent(32 + 30 + 12);
			mNonLayout.addView(mNonText, fp);
		}

		mContentView = new RecyclerView(context);
		mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
		((SimpleItemAnimator)mContentView.getItemAnimator()).setSupportsChangeAnimations(false);
		mContentView.getItemAnimator().setChangeDuration(0);
		mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
		mContentView.setLayoutManager(mLayoutManager);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(80));
		params.gravity = Gravity.CENTER_VERTICAL;
		addView(mContentView, params);

		initData();
	}

	private void initData()
	{
		mLabelAdapter = new LabelAdapter();
		mContentView.setAdapter(mLabelAdapter);
	}

	public void updateData()
	{
		if(mLabelAdapter != null)
		{
			StickerResMgr mgr = StickerResMgr.getInstance();
			boolean showMgr = !mgr.isBusiness() && !mgr.isSpecific() && !mgr.isSpecificLabel();
			ArrayList<LabelInfo> data = StickerResMgr.getInstance().getLabelInfoArr(getContext(), showMgr);

			if(data != null)
			{
				mLabelAdapter.setData(data);
			}
		}
	}

	public void updateSelectedStatus()
	{
		int index = StickerResMgr.getInstance().getSelectedLabelIndex();
		if(mLabelAdapter != null)
		{
			mLabelAdapter.notifyItemChanged(index);
		}
	}

	public void notifyLabelDataChange(int index)
	{
		if(mLabelAdapter != null)
		{
			if(index != -1)
			{
				mLabelAdapter.notifyItemChanged(index);
			}
			else
			{
				mLabelAdapter.notifyDataSetChanged();
			}
		}
	}

	public void setStickerDataHelper(StickerInnerListener helper)
	{
		mUIListener = helper;
		if(mLabelAdapter != null)
		{
			mLabelAdapter.setStickerDataHelper(helper);
		}
	}

	public void scrollToCenter(final int position)
	{
		if(mLayoutManager != null && mContentView != null)
		{
//			StickerResMgr mgr = StickerResMgr.getInstance();
//			boolean showMgr = !mgr.isBusiness() && !mgr.isSpecific() && !mgr.isSpecificLabel();
//			if (mLabelAdapter != null && mLabelAdapter.getItemCount() > 0 && position > -1 && position < (showMgr ? mLabelAdapter.getItemCount() -1 : mLabelAdapter.getItemCount()))
//			{
//
//			}
			View view = mLayoutManager.findViewByPosition(position);
			float center = mContentView.getWidth() / 2f;
			if(view != null)
			{
				float viewCenter = view.getX() + view.getWidth() / 2f;
				mContentView.smoothScrollBy((int)(viewCenter - center), 0);
			}
			else
			{
				mContentView.smoothScrollToPosition(position);
				myQueue().addIdleHandler(new MessageQueue.IdleHandler()
				{
					@Override
					public boolean queueIdle()
					{
						myQueue().removeIdleHandler(this);
						scrollToCenter(position);
						return false;
					}
				});
			}
		}
	}

	public void showBlackNonDrawable(boolean show)
	{
		if(mLabelAdapter != null)
		{
			mLabelAdapter.showWhiteTextAndMgrLogo(!show);
		}
		if(mNonIcon != null)
		{
			mNonIcon.setImageResource(show ? R.drawable.sticker_non_black : R.drawable.sticker_non_white);
		}
		if(mNonText != null)
		{
			mNonText.setTextColor(show ? Color.BLACK : Color.WHITE);
		}
	}

	public void ClearAll()
	{
		mUIListener = null;

		if (mContentView != null)
		{
			mContentView.setLayoutFrozen(true);
			mContentView = null;
		}

		if(mLabelAdapter != null)
		{
			mLabelAdapter.ClearAll();
			mLabelAdapter = null;
		}

		removeAllViews();
	}

	@Override
	public void setOrientation(int orientation)
	{
		orientation = HORIZONTAL;
		super.setOrientation(orientation);
	}

	@Override
	public void onClick(View v)
	{
		if(v == mNonLayout)
		{
			StickerResMgr.getInstance().clearAllSelectedInfo();
			if(mUIListener != null)
			{
				mUIListener.onSelectedSticker(null);
			}
		}
	}
}
