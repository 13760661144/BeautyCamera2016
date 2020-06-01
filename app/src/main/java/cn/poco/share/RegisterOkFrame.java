package cn.poco.share;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class RegisterOkFrame extends RelativeLayout{

	public RegisterOkFrame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public RegisterOkFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public RegisterOkFrame(Context context) {
		super(context);
		initialize(context);
	}
	
	protected ImageButton mBtnLogin;
	protected ImageView	mOkView;
	protected ImageButton mBtnClose;
	protected TextView mTxtsucInfo;
	protected TextView	mTxAccout;
	protected TextView mTxHelp;
	protected String   mStrAccount;
	protected String   mStrPsw;
	protected String   mStrId;
	
	public RegisterOkDialog mDialog = null;

	protected void initialize(Context context)
	{
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_hdpi(420));
		//LoginDialogBackground bk = new LoginDialogBackground(context);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		ImageView mImgLoginBk=new ImageView(context);
		mImgLoginBk.setBackgroundResource(R.drawable.framework_dialog_bg);
		addView(mImgLoginBk, params);
		
//		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		mOkView = new ImageView(context);
//		addView(mOkView, params);
//		mOkView.setPadding(ShareData.PxToDpi_hdpi(30), ShareData.PxToDpi_hdpi(50), 0, ShareData.PxToDpi_hdpi(16));
//		mOkView.setId(4);
		//2011年6月27日加入登录/注册关闭按钮X
		params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.rightMargin=ShareData.PxToDpi_hdpi(10);
		params.topMargin=ShareData.PxToDpi_hdpi(-10);
		mBtnClose=new ImageButton(context);
//		Bitmap bmpNormal=BitmapFactory.decodeResource(getResources(), R.drawable.share_bindpoco_exit_out);
//		mBtnClose.setButtonImage(bmpNormal, bmpNormal);
		mBtnClose.setOnClickListener(mClickListener);
		addView(mBtnClose, params);
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin=ShareData.PxToDpi_hdpi(60);
		mTxtsucInfo = new TextView(context);
		addView(mTxtsucInfo, params);
		mTxtsucInfo.setTextColor(0xff48AF09);
		mTxtsucInfo.setTextSize(20.0f);
		mTxtsucInfo.setText("注册POCO成功 !");
		mTxtsucInfo.setId(R.id.registerokframe_success);
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.registerokframe_success);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin=ShareData.PxToDpi_hdpi(20);
        mTxAccout = new TextView(context);
		addView(mTxAccout, params);
		mTxAccout.setTextColor(0xff000000);
		//setAccountInfo("cppbbs@sina.com",null,null);
		mTxAccout.setTextSize(16.0f);
		mTxAccout.setId(R.id.registerokframe_account);
		
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.bottomMargin=ShareData.PxToDpi_hdpi(30);
		mTxHelp = new TextView(context);
		addView(mTxHelp, params);
		mTxHelp.setTextColor(0xff48AF09);
		mTxHelp.setTextSize(14.0f);
		mTxHelp.setText("同时也可以在www.poco.cn登录哦!");
		mTxHelp.setId(R.id.registerokframe_help);
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ABOVE, R.id.registerokframe_help);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin=ShareData.PxToDpi_hdpi(30);
		mBtnLogin = new ImageButton(context);
//		bmpNormal = BitmapFactory.decodeResource(getResources(), R.drawable.share_bindpoco_determine_out);
//		mBtnLogin.setButtonImage(bmpNormal, bmpNormal);
		mBtnLogin.setPadding(0, ShareData.PxToDpi_hdpi(14), ShareData.PxToDpi_hdpi(30), 0);
		mBtnLogin.setOnClickListener(mClickListener);
		addView(mBtnLogin, params);
	}
	
	public void setAccountInfo(String account, String psw, String id)
	{
		mStrAccount = account;
		mStrPsw = psw;
		mStrId = id;
		if(account != null)
		{
			//mTxAccout.setText("恭喜您!POCO帐号已经生效:\n"+);
			mTxAccout.setText("恭喜您!POCO帐号已经生效:\n"+"\n\t\t\t"+mStrAccount);
		}
	}
	
	protected OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			if(v == mBtnLogin)
			{
				if(mDialog != null)
				{
					mDialog.onLogin(mStrId, mStrPsw);
				}
			}
			else if(v==mBtnClose)
			{
				if(mDialog!=null)
				{
					mDialog.dismiss();
				}
			}
		}
	};
}
