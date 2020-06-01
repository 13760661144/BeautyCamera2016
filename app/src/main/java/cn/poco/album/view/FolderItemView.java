package cn.poco.album.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/11/23
 */
public class FolderItemView extends LinearLayout {

	private Context mContext;

	public ImageView image;
	public TextView name;
	public TextView number;

	public FolderItemView(Context context) {
		super(context);
		mContext = context;
		setClickable(true);
		initViews();
	}

	private void initViews() {

		setPadding(ShareData.PxToDpi_xhdpi(24), 0, 0, 0);

		setOrientation(HORIZONTAL);

		LayoutParams params;
		image = new ImageView(mContext);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);

		params = new LayoutParams(ShareData.PxToDpi_xhdpi(155), ShareData.PxToDpi_xhdpi(155));
		addView(image, params);

		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(VERTICAL);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = ShareData.PxToDpi_xhdpi(24);
		addView(linearLayout, params);
		{
			LayoutParams params1;
			name = new TextView(mContext);
			name.setIncludeFontPadding(false);
			name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			name.setTextColor(Color.BLACK);
			params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			linearLayout.addView(name, params1);

			number = new TextView(mContext);
			number.setIncludeFontPadding(false);
			number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			number.setTextColor(Color.BLACK);
			params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.topMargin = ShareData.PxToDpi_xhdpi(24);
			linearLayout.addView(number, params1);
		}
	}
}
