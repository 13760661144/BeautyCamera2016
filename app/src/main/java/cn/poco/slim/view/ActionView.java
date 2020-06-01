package cn.poco.slim.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/24
 */
public class ActionView extends FrameLayout {

	public ImageView image;
	public TextView text;
	public ImageView indicator;

	public ActionView(Context context) {
		super(context);

		initViews();
	}

	private void initViews() {

		LayoutParams params;
		FrameLayout centerLayout = new FrameLayout(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(centerLayout, params);
		{
			image = new ImageView(getContext());
			ImageUtils.AddSkin(getContext(), image);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			centerLayout.addView(image, params);

			text = new TextView(getContext());
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			text.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = ShareData.PxToDpi_xhdpi(55);
			params.gravity = Gravity.CENTER_VERTICAL;
			centerLayout.addView(text, params);
		}

		indicator = new ImageView(getContext());
		ImageUtils.AddSkin(getContext(), indicator);
		indicator.setImageResource(R.drawable.beauty_slim_switching_line);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		addView(indicator, params);
	}
}
