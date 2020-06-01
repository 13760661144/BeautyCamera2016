package cn.poco.beauty.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class ItemView extends FrameLayout {

	public ImageView image;
	public TextView text;
	public FrameLayout selected;
	public ImageView selectedIcon;
	public TextView selectedText;
	public ImageView selectTip;

	private int mWidth;
	private int mHeight;

	public ItemView(Context context) {
		super(context);

		mWidth = ShareData.PxToDpi_xhdpi(146);
		mHeight = ShareData.PxToDpi_xhdpi(187);

		initViews();
	}

	private void initViews() {

		LayoutParams params;

		image = new ImageView(getContext());
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		params = new LayoutParams(mWidth, mWidth);
		addView(image, params);

		text = new TextView(getContext());
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
		text.setTextColor(0xe6000000);
		text.setGravity(Gravity.CENTER);
		text.setBackgroundColor(0xb3ffffff);
		params = new LayoutParams(mWidth, mHeight - mWidth);
		params.gravity = Gravity.BOTTOM;
		addView(text, params);

		selected = new FrameLayout(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(selected, params);
		{
			View view1 = new View(getContext());
			view1.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988));
			view1.setAlpha(0.94f);
			params = new LayoutParams(mWidth, mWidth);
			selected.addView(view1, params);

			View view2 = new View(getContext());
			view2.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988));
			view2.setAlpha(0.7f);
			params = new LayoutParams(mWidth, mHeight - mWidth);
			params.gravity = Gravity.BOTTOM;
			selected.addView(view2, params);

			selectedText = new TextView(getContext());
			selectedText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
			selectedText.setGravity(Gravity.CENTER);
			selectedText.setTextColor(Color.WHITE);
			params = new LayoutParams(mWidth, mHeight - mWidth);
			params.gravity = Gravity.BOTTOM;
			selected.addView(selectedText, params);
		}
		selected.setVisibility(GONE);

		FrameLayout center = new FrameLayout(getContext());
		params = new LayoutParams(mWidth, mWidth);
		addView(center, params);
		{
			selectedIcon = new ImageView(getContext());
			selectedIcon.setImageResource(R.drawable.filter_selected_tips_icon);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			center.addView(selectedIcon, params);
			selectedIcon.setVisibility(View.GONE);

			selectTip = new ImageView(getContext());
			selectTip.setImageResource(R.drawable.filter_selected_tips_icon_none);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			center.addView(selectTip, params);
			selectTip.setVisibility(GONE);
		}
	}
}
