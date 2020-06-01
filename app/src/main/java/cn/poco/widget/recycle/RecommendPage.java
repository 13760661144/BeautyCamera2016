package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendPage extends BaseItem
{
	private ImageView mLogo;
	private TextView mTextView;
	private ImageView mFlag;

	public RecommendPage(@NonNull Context context)
	{
		super(context);
		init();
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof RecommendExAdapter.ItemInfo)
		{
			RecommendExAdapter.ItemInfo itemInfo = (RecommendExAdapter.ItemInfo)info;
			Glide.with(getContext()).load(itemInfo.m_logos[0]).into(mLogo);
			mTextView.setText(itemInfo.m_names[0]);
		}
		else if(info instanceof RecommendAdapter.ItemInfo)
		{
			RecommendAdapter.ItemInfo itemInfo = (RecommendAdapter.ItemInfo)info;
			Glide.with(getContext()).load(itemInfo.m_logo).into(mLogo);
			mTextView.setText(itemInfo.m_name);
		}
	}

	private void init()
	{
		LayoutParams params;
		mLogo = new ImageView(getContext());
		mLogo.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(146));
		addView(mLogo, params);

		mTextView = new TextView(getContext());
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(ImageUtils.GetSkinColor(0xff737373));
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
		mTextView.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
		params.gravity = Gravity.BOTTOM;
		addView(mTextView, params);

		mFlag = new ImageView(getContext());
		mFlag.setImageResource(R.drawable.sticker_recom);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mFlag, params);
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
