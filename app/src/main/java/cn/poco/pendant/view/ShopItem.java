package cn.poco.pendant.view;

import android.content.Context;
import android.graphics.Color;
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
 * Date: 2016/12/5
 */
public class ShopItem extends FrameLayout {

	private Context mContext;

	private ImageView mImageView;
	private TextView mTextView;

	public ShopItem(Context context) {
		super(context);
		mContext = context;

		initViews();
	}

	private void initViews() {

		LayoutParams params;
		mImageView = new ImageView(mContext);
		ImageUtils.AddSkin(getContext(), mImageView);
		mImageView.setImageResource(R.drawable.pendant_material);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(mImageView, params);

		mTextView = new TextView(mContext);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setBackgroundResource(R.drawable.pendantpage_res_not_download_tip);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);
		mTextView.setPadding(ShareData.PxToDpi_xhdpi(1), 0, ShareData.PxToDpi_xhdpi(1), 0);
		mTextView.setGravity(Gravity.CENTER);
		params = new LayoutParams(ShareData.PxToDpi_xxhdpi(50), ShareData.PxToDpi_xxhdpi(36));
		params.topMargin = ShareData.PxToDpi_xhdpi(18);
		params.rightMargin = ShareData.PxToDpi_xhdpi(8);
		params.gravity = Gravity.END;
		addView(mTextView, params);
	}

	public void setNumber(int number) {
		if (number >= 0) {
			String text = String.valueOf(number);
			if (number > 99) {
				text = "99+";
			}
			mTextView.setText(text);
		}
	}
}
