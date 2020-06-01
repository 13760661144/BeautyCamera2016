package cn.poco.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.login.site.RegisterLoginPageSite;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.VerifyInfo;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 注册填写验证码页面
 */
public class RegisterLoginPage extends IPage
{
	//private static final String TAG = "注册填验证码";

	private CommonView m_comomView;
	private Handler mHandler = new Handler();
	private Bitmap mBmp;
	private RegisterLoginPageSite mSite;

	private TipsPage m_tipsPage;

	public RegisterLoginPage(Context context, BaseSite site)
	{
		super(context, site);
		mSite = (RegisterLoginPageSite)site;
		initUI();

		MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_创建账号_步骤一);
		TongJiUtils.onPageStart(getContext(), R.string.发送验证码);
	}

	/**
	 * @param params
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
//		if(params != null)
//		{
//			if(params.get("img") != null)
//			{
//				mBmp = Utils.DecodeFile((String)params.get("img"), null);
//				m_comomView.SetBackgroundBmp(Utils.DecodeFile((String)params.get("img"), null));
//			}
//			else
//			{
//				m_comomView.SetBackgroundBmp(null);
//			}
//		}
//		else
//		{
//			m_comomView.SetBackgroundBmp(null);
//		}
	}

	public void initUI()
	{
		m_comomView = new CommonView(getContext());
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		m_comomView.setLayoutParams(fl);
		this.addView(m_comomView);
		m_comomView.setState(CommonView.STATE_REGISTER);
		m_comomView.SetCallback(callBack);
	}

	CommonView.CallBack callBack = new CommonView.CallBack()
	{
		@Override
		public void onClickGetVerifyCode(final String zoneNum, final String phone)
		{
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤一_获取验证码);
			m_comomView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.registerloginpage_getting));
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					final VerifyInfo info = LoginUtils.getVerifyCode(zoneNum, phone, LoginUtils.VerifyCodeType.register, AppInterface.GetInstance(getContext()));
//					mHandler.post(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							if(info != null)
//							{
//								if(info.mCode == 0)
//								{
//									LoginOtherUtil.showToast(getContext().getResources().getString(R.string.registerloginpage_sendverificationcodesuccess));
////									m_comomView.SetVCodeText(info.mVerifyCode);
//									if(m_comomView.mtimer != null)
//									{
//										m_comomView.mtimer.start();
//									}
//								}
//								else if(info.mCode == 10102)
//								{
////									LoginOtherUtil.showToast("请选择国际区号");
//									m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_countrynumtips));
//								}
//								else if(info.mCode == 10001)
//								{
////									LoginOtherUtil.showToast("请输入正确的手机号");
//									m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_phonenumtips));
//								}
//								else if(info.mCode == 10002)
//								{
////									LoginOtherUtil.showToast("操作过于频繁，请稍后再试");
//									m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_operatfrequent));
//								}
//								else if(info.mCode > 10005 && info.mCode < 10010)
//								{
////									LoginOtherUtil.showToast("发送失败，请稍后再试");
//									m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_senderror));
//								}
//								else if(info.mCode == 10100)
//								{
//                                    m_tipsPage = new TipsPage(getContext());
//									m_tipsPage.SetText(getContext().getResources().getString(R.string.registerloginpage_registertips),getContext().getResources().getString(R.string.registerloginpage_gologin),getContext().getResources().getString(R.string.registerloginpage_cancel),-1);
//									Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), (int)(ShareData.m_screenWidth/2f),(int)(ShareData.m_screenHeight/2f));
//									m_tipsPage.SetBackgroundBk(LoginOtherUtil.getScreenBmpPath(temp));
//									m_tipsPage.showTips(RegisterLoginPage.this, new TipsPage.TipsonClickListener() {
//                                 @Override
//                                 public void onclickOk() {
//									 m_tipsPage = null;
//									 mSite.toLoginPage(getContext());
//                                 }
//
//                                 @Override
//                                 public void onclickCancel() {
//									 m_tipsPage = null;
//                                 }
//                                });
//									m_comomView.isTimerDone = true;
//								}
//								else if(info.mCode == 10004)
//								{
////									LoginOtherUtil.showToast(info.mMsg);
//									final String tempString = info.mMsg;
//									m_comomView.showErrorTips(tempString);
//									RegisterLoginPage.this.postDelayed(new Runnable() {
//										@Override
//										public void run() {
//											if(m_comomView.m_tipsFr != null && m_comomView.m_tipsFr.getVisibility() == VISIBLE && m_comomView.m_tipText.getText().equals(tempString))
//											{
//												m_comomView.m_tipsFr.setVisibility(INVISIBLE);
//											}
//										}
//									},2000);
//								}
//								else
//								{
////									LoginOtherUtil.showToast("获取验证码失败");
//									m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_getverificationcodefail));
//								}
//
//								if(info.mCode != 0)
//								{
//									m_comomView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.registerloginpage_reget));
//									m_comomView.isTimerDone = true;
//								}
//							}
//							else
//							{
////								LoginOtherUtil.showToast("网络异常,获取验证码失败!");
//								m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_networkerror));
//								m_comomView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.registerloginpage_reget));
//								m_comomView.isTimerDone = true;
//							}
//						}
//					});
//				}
//			}).start();
			LoginUtils2.getVerifyCode(zoneNum, phone, LoginUtils.VerifyCodeType.register, new HttpResponseCallback()
			{
				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_networkerror));
						m_comomView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.registerloginpage_reget));
						m_comomView.isTimerDone = true;
						return;
					}
					VerifyInfo info = (VerifyInfo)object;
					if(info.mCode == 0)
					{
						LoginOtherUtil.showToast(getContext().getResources().getString(R.string.registerloginpage_sendverificationcodesuccess));
						if(m_comomView.mtimer != null) m_comomView.mtimer.start();
					}
					else if(info.mCode == 55033) //手机已注册
					{
						m_tipsPage = new TipsPage(getContext());
						m_tipsPage.SetText(getContext().getResources().getString(R.string.registerloginpage_registertips),getContext().getResources().getString(R.string.registerloginpage_gologin),getContext().getResources().getString(R.string.registerloginpage_cancel),-1);
						Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), (int)(ShareData.m_screenWidth/2f),(int)(ShareData.m_screenHeight/2f));
						m_tipsPage.SetBackgroundBk(LoginOtherUtil.getScreenBmpPath(temp));
						m_tipsPage.showTips(RegisterLoginPage.this, new TipsPage.TipsonClickListener()
						{
							@Override
							public void onclickOk()
							{
								m_tipsPage = null;
								mSite.toLoginPage(getContext());
							}

							@Override
							public void onclickCancel() {
								m_tipsPage = null;
							}
						});
						m_comomView.isTimerDone = true;
					}
					else if(info.mCode == 55504) //一分钟内不能重复发送
					{
						final String tempString = info.mMsg;
						m_comomView.showErrorTips(tempString);
						RegisterLoginPage.this.postDelayed(new Runnable() {
							@Override
							public void run() {
								if(m_comomView.m_tipsFr != null && m_comomView.m_tipsFr.getVisibility() == VISIBLE && m_comomView.m_tipText.getText().equals(tempString))
								{
									m_comomView.m_tipsFr.setVisibility(INVISIBLE);
								}
							}
						},2000);
					}
					else
					{
						if(info.mMsg != null && info.mMsg.length() > 0) m_comomView.showErrorTips(info.mMsg);
						else m_comomView.showErrorTips(getContext().getResources().getString(R.string.registerloginpage_getverificationcodefail));
					}

					if(info.mCode != 0)
					{
						m_comomView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.registerloginpage_reget));
						m_comomView.isTimerDone = true;
					}
				}
			});
		}

		@Override
		public void onComfirmBtn(String phoneNum, String verityCode, String areaCodeNum)
		{
			if(m_comomView != null)
			{
				m_comomView.reSetData();
				m_comomView.SetLoginBtnByState(false);
			}
			if(mSite != null)
			{
				HashMap<String, Object> params = new HashMap<>();
				LoginPageInfo info = new LoginPageInfo();
				info.m_phoneNum = phoneNum;
				info.m_verityCode = verityCode;
				info.m_areaCodeNum = areaCodeNum;
				params.put("info", info);
				params.put("img", mBmp);
				mSite.verify_Code(params,getContext());
			}
		}

		@Override
		public void onChooseCountry()
		{
			if(mSite != null)
			{
				mSite.chooseCountry(getContext());
			}
		}

		@Override
		public void onBack()
		{
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤一_返回);
			mSite.onBackToLastPage(getContext());
		}

		@Override
		public void toAgreeWebView() {
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤一_点击查看协议);
			TongJi2.StartPage(getContext(),getResources().getString(R.string.登录注册_用户协议));
			if(CommonView.AGREE_URL != null && CommonView.AGREE_URL.length() > 0) {
				mSite.OpenWebView(CommonView.AGREE_URL, getContext());
			}
		}

		@Override
		public void onOkCallBackForSendTongJi() {
			MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤一_确定);
		}
	};

	@Override
	public void onBack()
	{
		if(m_tipsPage != null)
		{
			if(m_tipsPage.getVisibility() == VISIBLE)
			{
				m_tipsPage.dimissPage();
				return;
			}
		}

		mSite.onBack(getContext());
	}


	@Override
	public void onClose()
	{
		if(m_comomView != null)
		{
			m_comomView.onClose();
		}

		MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_创建账号_步骤一);
		TongJiUtils.onPageEnd(getContext(), R.string.发送验证码);
	}

	@Override
	public void onPause() {
		super.onPause();
		TongJiUtils.onPagePause(getContext(), R.string.发送验证码);
	}

	@Override
	public void onResume() {
		super.onResume();
		TongJiUtils.onPageResume(getContext(), R.string.发送验证码);
	}

	/*private TipsDialog.Listener listener = new TipsDialog.Listener()
	{
		@Override
		public void ok()
		{
		}

		@Override
		public void cancel()
		{
		}
	};*/


	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		if(siteID == SiteID.CHOOSE_COUNTRY)
		{
			if(params != null)
			{
				if((LoginPageInfo)params.get("info") != null)
				{
					m_comomView.setCountryAndCodeNum(((LoginPageInfo)params.get("info")).m_country, ((LoginPageInfo)params.get("info")).m_areaCodeNum);
				}
			}
		}
		else if(siteID == SiteID.WEBVIEW)
		{
			TongJi2.EndPage(getContext(),getResources().getString(R.string.登录注册_用户协议));
		}
	}
}
