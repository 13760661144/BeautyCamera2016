package cn.poco.arWish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.taotie.circle.PLog;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.FindIntroPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.qrcodescan.DecodeHandler;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.AlertDialog;
import my.beautyCamera.R;

/**
 * Created by Anson on 2018/1/22.
 */

public class FindIntroPage extends IPage
{
	public FindIntroPage(Context context, BaseSite site)
	{
		super(context, site);
		mSite = (FindIntroPageSite) site;
		init(context);
	}

	private DecodeHandler mDecodeHandler = new DecodeHandler(null);
	private Button mNearBtn;
	private Button mAlbumBtn;
	private FindIntroPageSite mSite;
	private ImageView mBackBtn;

	private void init(Context context)
	{
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ImageView bgImage = new ImageView(getContext());
		bgImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		bgImage.setImageResource(R.drawable.__fil__21092017041711300295095560);
		addView(bgImage, params);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = ShareData.PxToDpi_xhdpi(28);
		params.leftMargin = ShareData.PxToDpi_xhdpi(28);
		mBackBtn = new ImageView(getContext());
		mBackBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mBackBtn.setImageResource(R.drawable.ar_top_bar_back_btn);
		addView(mBackBtn, params);
//		ImageUtils.AddSkin(getContext(), mBackBtn);
		mBackBtn.setOnTouchListener(mOnAnimationClickListener);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		LinearLayout msgLayout = new LinearLayout(getContext());
		msgLayout.setOrientation(LinearLayout.VERTICAL);
		msgLayout.setGravity(Gravity.CENTER);
		addView(msgLayout, params);

		GradientDrawable btnBg = new GradientDrawable();
		btnBg.setColor(ImageUtils.GetSkinColor());
		btnBg.setCornerRadius(ShareData.PxToDpi_xhdpi(55));

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(358), ShareData.PxToDpi_xhdpi(110));
		mNearBtn = new Button(getContext());
		mNearBtn.setText(getResources().getString(R.string.ar_find_intro_near_btn));
		mNearBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		mNearBtn.setTextColor(Color.WHITE);
		mNearBtn.setOnTouchListener(mOnAnimationClickListener);
		mNearBtn.setBackground(btnBg);
		msgLayout.addView(mNearBtn, layoutParams);

		layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(358), ShareData.PxToDpi_xhdpi(110));
		layoutParams.topMargin = ShareData.PxToDpi_xhdpi(60);
		layoutParams.bottomMargin = ShareData.PxToDpi_xhdpi(90);
		mAlbumBtn = new Button(getContext());
		mAlbumBtn.setText(getResources().getString(R.string.ar_find_intro_album_btn));
		mAlbumBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		mAlbumBtn.setTextColor(Color.WHITE);
		mAlbumBtn.setOnTouchListener(mOnAnimationClickListener);
		mAlbumBtn.setBackground(btnBg);
		msgLayout.addView(mAlbumBtn, layoutParams);



		mDecodeHandler.setOnDecodeCompleteListener(mOnDecodeCompleteListener);
	}

	//扫码结束
	private DecodeHandler.OnDecodeCompleteListener mOnDecodeCompleteListener = new DecodeHandler.OnDecodeCompleteListener() {

		@Override
		public void onComplete(Result result) {
			String resultString = result.getText();
			if(resultString.equals("yue_err_404")){
				final AlertDialog alert = new AlertDialog(getContext());
				alert.setMessage("未在图中发现二维码");
				alert.setNegativeButton("确定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						alert.dismiss();
					}
				});
				alert.show();

//				Toast.makeText(getContext(), "未发现二维码", Toast.LENGTH_SHORT).show();
			}else{
				if (resultString.equals("")) {
					Toast.makeText(getContext(), "Scan failed!", Toast.LENGTH_SHORT).show();
				} else {
					PLog.out("anson", "二维码内容：" + resultString);
				}
			}
		}
	};

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		if(params != null)
		{
			String[] imgs = (String[])params.get("imgs");
			if(imgs != null)
			{
				mDecodeHandler.decodeImg(imgs[0]);
			}
			PLog.out("anson", "图片路径：" + imgs[0]);
		}
		super.onPageResult(siteID, params);
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{

	}

	@Override
	public void onBack()
	{
		mSite.onBack();
	}

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {
			if(v == mAlbumBtn){
				mSite.onAlbum();
			}else if(v == mNearBtn){
				mSite.showNearList();
			}else if(v == mBackBtn){
				mSite.onBack();
			}
		}
	};
}
