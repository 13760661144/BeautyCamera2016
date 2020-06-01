package cn.poco.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.widget.Toast;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.login.site.ResetLoginPswPageSite;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.VerifyInfo;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.FullScreenDlg;
import my.beautyCamera.R;

/**
 * 忘记密码填写验证码页面
 */
public class ResetLoginPswPage extends IPage {
	//private static final String TAG = "重置密码前验证";

	private CommonView m_commonView;
	private Handler mHandler;
	private FullScreenDlg dialog;
	private Bitmap mBmp;
	private ResetLoginPswPageSite mSite;

	/**
	 * @param params
	 * img:背景图片路径;
	 * info:页面需要的信息的对象
     */
	@Override
	public void SetData(HashMap<String, Object> params) {
		if(params != null)
		{
//			if(params.get("img") != null)
//			{
//				SetBackgroundBmp(Utils.DecodeFile((String) params.get("img"),null));
//			}
//			else
//			{
//				SetBackgroundBmp(null);
//			}
			if(params.get("info") != null)
			{
				setCountryNumAndCode(((LoginPageInfo)params.get("info")).m_country,((LoginPageInfo)params.get("info")).m_areaCodeNum);
			}
		}

	}

	public ResetLoginPswPage(Context context, BaseSite site)
	{
		super(context,site);
		mSite = (ResetLoginPswPageSite) site;
		initUI();
		MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_忘记密码_步骤一);
		TongJiUtils.onPageStart(getContext(), R.string.发送验证码);
	}
	
	public void initUI(){
		mHandler = new Handler();
		dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
		m_commonView = new CommonView(getContext());
		LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		m_commonView.setLayoutParams(fl);
		this.addView(m_commonView);
		m_commonView.setState(CommonView.STATE_RESETPSW);
		m_commonView.SetCallback(cb);
	}
	
	protected void setCountryNumAndCode(String country,String code){
		if(m_commonView != null)
		{
			m_commonView.setCountryAndCodeNum(country, code);
		}
	}
	
	protected void SetBackgroundBmp(Bitmap bmp){
		if(m_commonView != null)
		{
			if(bmp != null)
			{
				mBmp = bmp;
				m_commonView.setBackgroundDrawable(new BitmapDrawable(bmp));
			}
			else
			{
				m_commonView.setBackgroundColor(Color.WHITE);
			}
		}
	}
	
	private CommonView.CallBack cb = new CommonView.CallBack() {
		
		@Override
		public void onClickGetVerifyCode(final String zoneNum, final String phoneNum) {
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤一_获取验证码);
			m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.resetloginpage_getting));
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					final VerifyInfo info = LoginUtils.getVerifyCode(zoneNum, phoneNum, LoginUtils.VerifyCodeType.find, AppInterface.GetInstance(getContext()));
//					mHandler.post(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							if(info != null)
//							{
//								if(info.mCode == 10101)
//								{
////									TipsDialog tips = new TipsDialog(getContext(), null, "手机号未注册！", null, "确定", listener);
////									showDialog(tips);
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_notregister));
//								}
//								else if (info.mCode == 0)
//								{
//									Toast.makeText(getContext(), getContext().getResources().getString(R.string.resetloginpage_sendsuccess), Toast.LENGTH_SHORT).show();
////									m_commonView.SetVCodeText(info.mVerifyCode);
//									if(m_commonView.mtimer != null)
//									{
//										m_commonView.mtimer.start();
//									}
//									if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE)
//									{
//										m_commonView.m_tipsFr.setVisibility(INVISIBLE);
//									}
//								}
//								else if(info.mCode == 10102)
//								{
////									LoginOtherUtil.showToast("请选择国际区号");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_chooseareanum));
//								}
//								else if(info.mCode == 10001)
//								{
////									LoginOtherUtil.showToast("请输入正确的手机号");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_writephonenum));
//								}
//								else if(info.mCode == 10002)
//								{
////									LoginOtherUtil.showToast("操作过于频繁，请稍后再试");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_oparefrequent));
//								}
//								else if(info.mCode == 10004)
//								{
////									LoginOtherUtil.showToast(info.mMsg);
//									final String tempString = info.mMsg;
//									m_commonView.showErrorTips(tempString);
//									ResetLoginPswPage.this.postDelayed(new Runnable() {
//										@Override
//										public void run() {
//											if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE && m_commonView.m_tipText.getText().equals(tempString))
//											{
//												m_commonView.m_tipsFr.setVisibility(INVISIBLE);
//											}
//										}
//									},2000);
//								}
//								else if(info.mCode > 10005 && info.mCode < 10010)
//								{
////									LoginOtherUtil.showToast("发送失败，请稍后再试");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_sendfail));
//								}
//								else
//								{
////									LoginOtherUtil.showToast("获取验证码失败!");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_getvcodefail));
//								}
//
//								if(info.mCode != 0)
//								{
//									m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.resetloginpage_reget));
//									m_commonView.isTimerDone = true;
//								}
//
//							}
//							else
//							{
////								LoginOtherUtil.showToast("网络异常,获取验证码失败!");
//								m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_networkerror));
//								m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.resetloginpage_reget));
//								m_commonView.isTimerDone = true;
//							}
//						}
//					});
//				}
//			}).start();

			LoginUtils2.getVerifyCode(zoneNum, phoneNum, LoginUtils.VerifyCodeType.find, new HttpResponseCallback()
			{

				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_networkerror));
						m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.resetloginpage_reget));
						m_commonView.isTimerDone = true;
						return;
					}
					final VerifyInfo info = (VerifyInfo)object;
					if(info.mCode == 0)
					{
						Toast.makeText(getContext(), getContext().getResources().getString(R.string.resetloginpage_sendsuccess), Toast.LENGTH_SHORT).show();
						if(m_commonView.mtimer != null) m_commonView.mtimer.start();
						if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE) m_commonView.m_tipsFr.setVisibility(INVISIBLE);
					}
					else if(info.mCode == 55504)
					{
						final String tempString = info.mMsg;
						m_commonView.showErrorTips(tempString);
						ResetLoginPswPage.this.postDelayed(new Runnable() {
							@Override
							public void run() {
								if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE && m_commonView.m_tipText.getText().equals(tempString))
								{
									m_commonView.m_tipsFr.setVisibility(INVISIBLE);
								}
							}
						},2000);
					}
					else
					{
						if(info.mMsg != null && info.mMsg.length() > 0) m_commonView.showErrorTips(info.mMsg);
						else m_commonView.showErrorTips(getContext().getResources().getString(R.string.resetloginpage_getvcodefail));
					}

					if(info.mCode != 0)
					{
						m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.resetloginpage_reget));
						m_commonView.isTimerDone = true;
					}
				}
			});
		}

		@Override
		public void onComfirmBtn(String phoneNum, String verityCode, String areaCodeNum) {
			if(mSite != null)
			{
				HashMap<String,Object> params = new HashMap<>();
				params.put("img",mBmp);
				LoginPageInfo info = new LoginPageInfo();
				info.m_phoneNum = phoneNum;
				info.m_verityCode = verityCode;
				info.m_areaCodeNum = areaCodeNum;
				params.put("info",info);
				params.put("mode",ResetPswPage.STATE_RESETPSW);
				mSite.pre_reset(params,getContext());
			}
		}

		@Override
		public void onChooseCountry() {
			if(mSite != null)
			{
				mSite.chooseCountry(getContext());
			}
		}

		@Override
		public void onBack() {
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤一_返回);
			mSite.backToLastPage(getContext());
		}

		@Override
		public void toAgreeWebView() {

		}

		@Override
		public void onOkCallBackForSendTongJi() {
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤一_确定);
		}
	};
	

	@Override
	public void onBack() {
		mSite.onBack(getContext());
	}

	@Override
	public void onClose() {
		if(dialog != null)
		{
			dialog.dismiss();
			dialog =null;
		}
		if(m_commonView != null)
		{
			m_commonView.onClose();
		}
		MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_忘记密码_步骤一);
		TongJiUtils.onPageEnd(getContext(), R.string.发送验证码);
	}

	@Override
	public void onResume() {
		super.onResume();
		TongJiUtils.onPageResume(getContext(), R.string.发送验证码);
	}

	@Override
	public void onPause() {
		super.onPause();
		TongJiUtils.onPagePause(getContext(), R.string.发送验证码);
	}

	/*private TipsDialog.Listener listener = new TipsDialog.Listener() {
		
		@Override
		public void ok() {
			if(dialog != null)
			{
				dialog.dismiss();
			}
		}
		
		@Override
		public void cancel() {
		}
	};
    
    private void showDialog(View view)
	{
		if(dialog == null)
		{
			dialog = new FullScreenDlg(PocoCamera.main,R.style.dialog);
		}
		if(dialog != null)
		{
			dialog.m_fr.removeAllViews();
			dialog.m_fr.addView(view);
			dialog.show();
		}
	}*/

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		super.onPageResult(siteID, params);

		if (siteID == SiteID.CHOOSE_COUNTRY)
		{
			if(params != null)
			{
				if((LoginPageInfo) params.get("info") != null)
				{

					m_commonView.setCountryAndCodeNum(((LoginPageInfo) params.get("info")).m_country,((LoginPageInfo) params.get("info")).m_areaCodeNum);
				}
			}
		}
	}
}
