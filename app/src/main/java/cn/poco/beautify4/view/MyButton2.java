package cn.poco.beautify4.view;

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
 * Date: 2017/2/8
 */
public class MyButton2 extends FrameLayout {

	public ImageView image;
	public TextView text;

	private ImageView mNewIcon;

	public MyButton2(Context context) {
		super(context);

		init();
	}

	private void init() {
		setClipToPadding(false);
		setPadding(ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20), 0);

		LayoutParams params;

		image = new ImageView(getContext());
		ImageUtils.AddSkin(getContext(), image);
		params = new LayoutParams(ShareData.PxToDpi_xhdpi(82), ShareData.PxToDpi_xhdpi(82));
		params.gravity = Gravity.CENTER_HORIZONTAL;
		addView(image, params);

		text = new TextView(getContext());
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		text.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		text.setIncludeFontPadding(false);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(96);
		addView(text, params);
	}

	public void setNew(boolean flag) {

		if (mNewIcon != null) {
			this.removeView(mNewIcon);
			mNewIcon = null;
		}
		if (flag) {
			mNewIcon = new ImageView(getContext());
			mNewIcon.setImageResource(R.drawable.beautify4page_button_new);
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.END | Gravity.TOP;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(-1);
			fl.topMargin = ShareData.PxToDpi_xhdpi(3);
			this.addView(mNewIcon, fl);
		}
	}
}
