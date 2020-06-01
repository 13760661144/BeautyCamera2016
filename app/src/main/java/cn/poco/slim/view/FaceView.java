package cn.poco.slim.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/12/24
 */
public class FaceView extends LinearLayout {

	public ImageView face;
	public TextView text;

	public FaceView(Context context) {
		super(context);

		initViews();
	}

	private void initViews() {
		setOrientation(VERTICAL);

		LayoutParams params;

		face = new ImageView(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		addView(face, params);

		text = new TextView(getContext());
		text.setIncludeFontPadding(false);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		text.setTextColor(0xb3000000);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(20);
		addView(text, params);
	}
}
