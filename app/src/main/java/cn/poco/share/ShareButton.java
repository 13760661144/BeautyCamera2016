package cn.poco.share;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2016/12/15.
 */

public class ShareButton extends FrameLayout
{
	public ShareButton(Context context)
	{
		super(context);
	}

	public void init(int icon_resId, String text, OnAnimationClickListener listener)
	{
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		ImageView bg = new ImageView(getContext());
		bg.setImageResource(R.drawable.share_button_bg);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		addView(bg, fl);
		ImageUtils.AddSkin(getContext(), bg);
		bg.setOnTouchListener(listener);

		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		fl.leftMargin = ShareData.PxToDpi_xhdpi(54);
		addView(linearLayout, fl);
		{
			ImageView icon = new ImageView(getContext());
			icon.setImageResource(icon_resId);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			linearLayout.addView(icon, ll);

			TextView text_view = new TextView(getContext());
			text_view.setText(text);
			text_view.setTextColor(0xffffffff);
			text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(17);
			linearLayout.addView(text_view, ll);
		}
	}
}
