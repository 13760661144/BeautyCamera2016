package cn.poco.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.poco.share.ImageButton;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class TipsDialog extends RelativeLayout {

	private Context mContext;
	private Bitmap mScreenBmp;
	private String mStrTitle;
	private String mStrCancel;
	private String mStrOk;
	private Listener mListener;
	
	
	private int mWidth = ShareData.PxToDpi_xhdpi(520);
	private int mHeight = ShareData.PxToDpi_xhdpi(404);

	private LayoutParams rlParams = null;
	private RelativeLayout mContainer = null;
	private ImageButton mCancelBtn = null;
	private ImageButton mOkBtn = null;
	private boolean CornerRoundOne = false;

	private FullScreenDlg dialog;
	
	public TipsDialog(Context context, Bitmap screenBmp, String title, String cancel, String ok, Listener listener) {
		super(context);
		mContext = context;
		mScreenBmp = screenBmp;
		mStrTitle = title;
		mStrCancel = cancel;
		mStrOk = ok;
		mListener = listener;
		
		initUI();

	}
	
	@SuppressLint("NewApi")
	private void initUI() {
		Drawable drawable = null;
		rlParams = new LayoutParams(mWidth, LayoutParams.WRAP_CONTENT);// 408
		mContainer = new RelativeLayout(mContext);
		mContainer.setVisibility(View.VISIBLE);
		rlParams.addRule(CENTER_IN_PARENT);// 居中
		rlParams.addRule(CENTER_HORIZONTAL);
		this.addView(mContainer, rlParams);
		{
			int height = ShareData.PxToDpi_xhdpi(120);
			int lineCount = (mStrTitle.length()/13);
			if( lineCount > 0)
			{
				height = ShareData.PxToDpi_xhdpi(110) + ShareData.PxToDpi_xhdpi(65)*lineCount;
			}
			
			Bitmap temp = LoginOtherUtil.createBitmapByColor(Color.WHITE, mWidth, height);
			Bitmap conerBg = LoginOtherUtil.MakeDiffCornerRoundBmp(temp, ShareData.PxToDpi_xhdpi(20),ShareData.PxToDpi_xhdpi(20), 0, 0);
			if(temp != null){
				temp.recycle();
				temp = null;
			}
			rlParams = new LayoutParams(mWidth, LayoutParams.WRAP_CONTENT);
			TextView titleText = new TextView(mContext);
			titleText.setPadding(ShareData.PxToDpi_xhdpi(30),ShareData.PxToDpi_xhdpi(30),ShareData.PxToDpi_xhdpi(30),ShareData.PxToDpi_xhdpi(30));
			titleText.setId(R.id.login_tipsdialog_titletext);
			titleText.setText(mStrTitle);
			titleText.setTextSize(16f);
			titleText.setLineSpacing(16.0f,1);
			titleText.setGravity(Gravity.CENTER);
			titleText.setTextColor(0xff000000);
			titleText.setBackgroundDrawable(new BitmapDrawable(conerBg));
//			titleText.setBackgroundResource(R.drawable.choosepreviewsharealertviewtextbg);
			mContainer.addView(titleText, rlParams);
			
			ImageView line = new ImageView(getContext());
			rlParams = new LayoutParams(mWidth, 1);
			rlParams.addRule(BELOW, R.id.login_tipsdialog_titletext);
			rlParams.addRule(CENTER_HORIZONTAL);
			line.setId(R.id.login_tipsdialog_line);
			line.setImageResource(R.drawable.beauty_login_line);
			mContainer.addView(line,rlParams);

			rlParams = new LayoutParams(mWidth, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(BELOW, R.id.login_tipsdialog_line);
			rlParams.addRule(CENTER_HORIZONTAL);
			LinearLayout btns = new LinearLayout(mContext);
			mContainer.addView(btns, rlParams);
			{
				int mHeigh = ShareData.PxToDpi_xhdpi(188 / 2);
				LinearLayout.LayoutParams llParams = null;
				int btnWidth = mWidth;
				if(mStrCancel != null && mStrOk != null)
				{
					CornerRoundOne = true;
				}
				if (mStrCancel != null) {
					btnWidth = btnWidth / 2;
					llParams = new LinearLayout.LayoutParams(btnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
					llParams.weight = 1;
					RelativeLayout calcleView = new RelativeLayout(mContext);
					btns.addView(calcleView, llParams);
					{
						mCancelBtn = new ImageButton(mContext);
						Bitmap temp1 = LoginOtherUtil.createBitmapByColor(Color.WHITE, btnWidth, mHeigh);
						Bitmap bmpNormal = null;
						if(CornerRoundOne)
						{
							bmpNormal = LoginOtherUtil.MakeDiffCornerRoundBmp(temp1, 0, 0, ShareData.PxToDpi_xhdpi(20), 0);
						}
						else
						{
							bmpNormal = LoginOtherUtil.MakeDiffCornerRoundBmp(temp1, 0, 0, ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20));
						}
						if(temp1 != null){
							temp1.recycle();
							temp1 = null;
						}
						mCancelBtn.setButtonImage(bmpNormal, bmpNormal);
						mCancelBtn.setOnClickListener(mOnClickListener);
//						mCancelBtn.setButtonImage(R.drawable.choosepreviewsharealertviewcancelbg, R.drawable.choosepreviewsharealertviewcancelbghover);
						mCancelBtn.setScaleType(ScaleType.FIT_XY);
						rlParams = new LayoutParams(btnWidth, mHeigh);
						calcleView.addView(mCancelBtn, rlParams);

						TextView cancleText = new TextView(mContext);
						rlParams = new LayoutParams(btnWidth, mHeigh);
						cancleText.setText(mStrCancel);
						cancleText.setTextSize(14.5f);
						cancleText.setTextColor(0xff0982ff);
						cancleText.setGravity(Gravity.CENTER);
						calcleView.addView(cancleText, rlParams);
					}
				}
				
				ImageView line1 = new ImageView(getContext());
				rlParams = new LayoutParams(1, mHeigh);
				rlParams.addRule(BELOW, R.id.login_tipsdialog_titletext);
				rlParams.addRule(CENTER_HORIZONTAL);
				line.setImageResource(R.drawable.beauty_login_verificationcode_line);
				btns.addView(line1,rlParams);

				llParams = new LinearLayout.LayoutParams(btnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
				llParams.weight = 1;
				RelativeLayout okView = new RelativeLayout(mContext);
				btns.addView(okView, llParams);
				{
					mOkBtn = new ImageButton(mContext);
					Bitmap temp1 = LoginOtherUtil.createBitmapByColor(Color.WHITE, btnWidth, mHeigh);
					Bitmap bmpNormal = null;
					if(CornerRoundOne)
					{
						bmpNormal = LoginOtherUtil.MakeDiffCornerRoundBmp(temp1, 0, 0, 0, ShareData.PxToDpi_xhdpi(20));
					}
					else
					{
						bmpNormal = LoginOtherUtil.MakeDiffCornerRoundBmp(temp1, 0, 0, ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20));
					}
					if(temp1 != null){
						temp1.recycle();
						temp1 = null;
					}
					mOkBtn.setButtonImage(bmpNormal, bmpNormal);
					mOkBtn.setScaleType(ScaleType.FIT_XY);
					mOkBtn.setOnClickListener(mOnClickListener);
					rlParams = new LayoutParams(btnWidth, mHeigh);
					okView.addView(mOkBtn, rlParams);

					TextView okText = new TextView(mContext);
					rlParams = new LayoutParams(btnWidth, mHeigh);
					okText.setText(mStrOk);
					okText.setTextSize(14.5f);
					okText.setTextColor(0xff0982ff);
					okText.setGravity(Gravity.CENTER);
					okView.addView(okText, rlParams);
				}
			}
		}
		mContainer.setVisibility(VISIBLE);
		
		mContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                	mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    mHeight = mContainer.getMeasuredHeight();
                    return false;
                }
            });
		
		Handler mHandler = new Handler();
	}



	private boolean isClickCancel = true;
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mCancelBtn) {
				isClickCancel = true;
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if(mListener != null)
				{
					mListener.cancel();
				}
			} else if (v == mOkBtn) {
				isClickCancel = false;
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if(mListener != null)
				{
					mListener.ok();
				}

			}
		}
	};
	
	@SuppressLint("NewApi")
	private void release(){
		if(mScreenBmp!=null&&!mScreenBmp.isRecycled()){
			mScreenBmp.recycle();
			mScreenBmp=null;
		}
		this.setBackground(null);
	}
	
	public static int findStr(String srcText, String keyword) {  
        int count = 0;  
        Pattern p = Pattern.compile(keyword);  
        Matcher m = p.matcher(srcText);  
        while (m.find()) {  
            count++;  
        }  
        return count;  
    }  

     public interface Listener {
		 void cancel();
		 void ok();
	}


	public void showDialog() {
		if (dialog == null) {
			dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
		}
		if (dialog != null) {
			dialog.m_fr.removeAllViews();
			dialog.m_fr.addView(this);
			dialog.show();
		}
	}

	public void dissmissDialog()
	{
		if(dialog != null)
		{
			dialog.dismiss();
			dialog = null;
		}
	}
}