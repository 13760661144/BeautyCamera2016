package cn.poco.Theme;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


/**
 * Created by lgd on 2016/12/9.
 */

public class ThemeItem extends FrameLayout
{
	private TextView textView;
	private ImageView hook;
	private FrameLayout parent;
	public ThemeItem(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		LayoutParams  params;
//		params = new LayoutParams(ShareData.m_screenWidth/3, ShareData.PxToDpi_xhdpi(227));   //204  24
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PercentUtil.WidthPxToPercent(227));   //204  24
//		params.bottomMargin = ShareData.PxToDpi_xhdpi(24);
		this.setLayoutParams(params);

//		params = new LayoutParams(ShareData.PxToDpi_xhdpi(203), ShareData.PxToDpi_xhdpi(203));
		params = new LayoutParams(PercentUtil.WidthPxToPercent(203), PercentUtil.WidthPxToPercent(203));
//		params.leftMargin = ShareData.m_screenWidth/3-PercentUtil.WidthPxToPercent(203)+PercentUtil.WidthPxToPercent(24);
		params.gravity = Gravity.CENTER;
		 parent = new FrameLayout(getContext());
		parent.setId(R.id.theme_color_parent);
		addView(parent,params);

		textView = new TextView(getContext());
		textView.setId(R.id.theme_color_text);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
		textView.setTextColor(Color.WHITE);
		 params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		parent.addView(textView, params);

		hook = new ImageView(getContext());
		hook.setImageResource(R.drawable.theme_item_select_hook);
		hook.setId(R.id.theme_color_hook);
//		hook.setAlpha(0f);
		hook.setVisibility(View.GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24);
		parent.addView(hook, params);
	}

	public void setText(String text)
	{
		textView.setText(text);
	}

}
