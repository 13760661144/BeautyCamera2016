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

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterOriginal extends BaseItem
{

	private View mBackColor;
	private ImageView mLogo;
	private TextView mBottomText;
	private int textBkColor;
	private int def_bkCover = -1;
	private int bkCover;

	public FilterOriginal(@NonNull Context context)
	{
		super(context);
		init();
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{

	}

	private void init()
	{
		LayoutParams params;
		bkCover = ImageUtils.GetSkinColor(0xffe75988, 0.94f);
		textBkColor = ImageUtils.GetSkinColor(0xffe75988, 0.7f);

		mBackColor = new View(getContext());
		mBackColor.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.TOP;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(42);
		addView(mBackColor, params);

		mLogo = new ImageView(getContext());
		mLogo.setImageResource(R.drawable.filter_res_non_icon);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(20);
		addView(mLogo, params);

		mBottomText = new TextView(getContext());
		mBottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
		mBottomText.setGravity(Gravity.CENTER);
		mBottomText.setTextColor(Color.WHITE);
		mBottomText.setText(R.string.filter_type_none);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
		params.gravity = Gravity.BOTTOM;
		addView(mBottomText, params);


	}

	@Override
	public void onSelected()
	{
		mLogo.setImageResource(R.drawable.filter_selected_tips_icon_none);
//		setBackgroundColor(bkCover);
		//mBackColor.setAlpha(0.5f);
		mBackColor.setBackgroundColor(bkCover);
		mBottomText.setBackgroundColor(textBkColor);
		mBottomText.setTextColor(Color.WHITE);
	}

	@Override
	public void onUnSelected()
	{
		mLogo.setImageResource(R.drawable.filter_res_non_icon);
//		setBackgroundColor(Color.WHITE);
		//mBackColor.setAlpha(1f);
		mBackColor.setBackgroundColor(def_bkCover != -1 ? def_bkCover : Color.WHITE);
		mBottomText.setBackgroundColor(def_bkCover != -1 ? def_bkCover : Color.WHITE);
		mBottomText.setTextColor(Color.BLACK);
	}

	@Override
	public void onClick()
	{

	}

	public void setDefBKCoverColor(int color)
	{
		def_bkCover = color;
	}
}
