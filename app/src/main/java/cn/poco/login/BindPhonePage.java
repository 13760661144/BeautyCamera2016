package cn.poco.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.VerifyInfo;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 绑定手机页面
 */
public class BindPhonePage extends IPage {
	//private static final String TAG = "绑手机";

	private CommonView m_commonView;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private LoginInfo loginInfo;
	private String mVerityCode;
	private Bitmap bkBmp;
	private boolean m_isHideTitle = false;

	private boolean m_isShowTips = true;

	private BindPhonePageSite mSite;

	private LoginStyle.LoginBaseInfo m_reLoginInfo;

	public BindPhonePage(Context context, BaseSite site)
	{
		super(context,site);
		mSite = (BindPhonePageSite) site;
		initUI();

		TongJiUtils.onPageStart(getContext(), R.string.绑定手机);
	}

	/**
	 * @param params
	 * img:背景图片的路径；
	 * loginInfo:登录返回的对象
	 * isHide：是否隐藏标题 true：隐藏标题 ，false：显示
	 * isShowTips:输入已绑定的手机是否弹出提示框，默认和true 弹出，false 不弹出
     */
	@Override
	public void SetData(HashMap<String, Object> params) {
		if(params != null)
		{
//			if(params.get("img") != null)
//			{
//				SetBackground(Utils.DecodeFile((String) params.get("img"),null));
//			}
//			else
//			{
//				SetBackground(null);
//			}
			LoginInfo loginInfo = (LoginInfo) params.get("loginInfo");
			if(loginInfo != null)
			{
				setLoginInfo(loginInfo);
			}
			if(params.get("isHide") != null)
			{
				if ((boolean)params.get("isHide") == true) {
					setIsHideTitle((boolean) params.get("isHide"));
				}
			}

			if(params.get("isShowTips") != null)
			{
				m_isShowTips = (boolean)params.get("isShowTips");
			}

			if(params.get("relogininfo") != null)
			{
				m_reLoginInfo = (LoginStyle.LoginBaseInfo) params.get("relogininfo");
			}

		}
	}
	
	public void initUI(){
		m_commonView = new CommonView(this.getContext());
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		m_commonView.setLayoutParams(rl);
		m_commonView.setState(CommonView.STATE_BINDPHONE);
		m_commonView.SetCallback(cb);
		this.addView(m_commonView);
	}
	public void setIsHideTitle(boolean isHide)
	{
		if(m_commonView != null)
		{
			m_commonView.setIsHideTitle(isHide);
		}
	}
	
	private CommonView.CallBack cb = new CommonView.CallBack() {
		
		@Override
		public void onClickGetVerifyCode(final String zoneNum, final String phone) {
			m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.bindphonepage_getting));
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
////					final VerifyInfo info = LoginUtils.getVerifyCode(zoneNum, phone, LoginUtils.VerifyCodeType.bind_mobile, AppInterface.GetInstance(getContext()));
//					final VerifyInfo info = LoginUtils.getVerifyCodeForBindPhone(zoneNum, phone, getUserId(), LoginUtils.VerifyCodeType.bind_mobile, AppInterface.GetInstance(getContext()));
//					mHandler.post(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							if(info != null)
//							{
//								if(info.mCode == 0)
//								{
//									LoginOtherUtil.showToast(getContext().getResources().getString(R.string.bindphonepage_sendsuccess));
////									m_commonView.SetVCodeText(info.mVerifyCode);
//									if(m_commonView.mtimer != null)
//									{
//										m_commonView.mtimer.start();
//									}
//
//									if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE)
//									{
//										m_commonView.m_tipsFr.setVisibility(INVISIBLE);
//									}
//								}
//								else if(info.mCode == 10102)
//								{
////									LoginOtherUtil.showToast("请选择国际区号");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_chooseareanum));
//								}
//								else if(info.mCode == 10001)
//								{
////									LoginOtherUtil.showToast("请输入正确的手机号");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_writerightnum));
//								}
//								else if(info.mCode == 10002)
//								{
////									LoginOtherUtil.showToast("操作过于频繁，请稍后再试");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_operafrequently));
//								}
//								else if(info.mCode == 10005)
//								{
//									if(info.mMsg != null)
//									{
//										m_commonView.showErrorTips(info.mMsg);
//									}
//								}
//								else if(info.mCode > 10005 && info.mCode < 10010)
//								{
////									LoginOtherUtil.showToast("发送失败，请稍后再试");
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_senderror));
//								}
//								else if(info.mCode == 10100)
//								{
//									final TipsPage tipsPage = new TipsPage(getContext());
//									tipsPage.SetText(getContext().getResources().getString(R.string.bindphonepage_reigstertips),getContext().getResources().getString(R.string.bindphonepage_logintips),getContext().getResources().getString(R.string.bindphonepage_cancel),ShareData.PxToDpi_xhdpi(225));
//									Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), (int)(ShareData.m_screenWidth/2f),(int)(ShareData.m_screenHeight/2f));
//									tipsPage.SetBackgroundBk(LoginOtherUtil.getScreenBmpPath(temp));
//									tipsPage.showTips(BindPhonePage.this, new TipsPage.TipsonClickListener() {
//										@Override
//										public void onclickOk() {
//											UserMgr.ExitLogin(getContext());
//											EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
//											mSite.toLoginPage(new HashMap<String, Object>(),getContext());
//										}
//
//										@Override
//										public void onclickCancel() {
//
//										}
//									});
//									m_commonView.isTimerDone = true;
//								}
//								else
//								{
//									m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_othererror));
//								}
//
//								if(info.mCode != 0)
//								{
//									m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.bindphonepage_reget));
//									m_commonView.isTimerDone = true;
//								}
//							}
//							else
//							{
//								LoginOtherUtil.showToast(getContext().getResources().getString(R.string.bindphonepage_networkerror));
//								m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.bindphonepage_reget));
//								m_commonView.isTimerDone = true;
//							}
//						}
//					});
//				}
//			}).start();

			LoginUtils2.getVerifyCode(zoneNum, phone, getUserId(), new HttpResponseCallback()
			{
				@Override
				public void response(Object object)
				{
					if(object == null)
					{
						LoginOtherUtil.showToast(getContext().getResources().getString(R.string.bindphonepage_networkerror));
						m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.bindphonepage_reget));
						m_commonView.isTimerDone = true;
						return;
					}
					final VerifyInfo info = (VerifyInfo)object;
					if(info.mCode == 0)
					{
						LoginOtherUtil.showToast(getContext().getResources().getString(R.string.bindphonepage_sendsuccess));
						if(m_commonView.mtimer != null) m_commonView.mtimer.start();
						if(m_commonView.m_tipsFr != null && m_commonView.m_tipsFr.getVisibility() == VISIBLE) m_commonView.m_tipsFr.setVisibility(INVISIBLE);
					}
					else if(info.mCode == 55033) //手机已注册
					{
						final TipsPage tipsPage = new TipsPage(getContext());
						tipsPage.SetText(getContext().getResources().getString(R.string.bindphonepage_reigstertips),getContext().getResources().getString(R.string.bindphonepage_logintips),getContext().getResources().getString(R.string.bindphonepage_cancel),ShareData.PxToDpi_xhdpi(225));
						Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), (int)(ShareData.m_screenWidth/2f),(int)(ShareData.m_screenHeight/2f));
						tipsPage.SetBackgroundBk(LoginOtherUtil.getScreenBmpPath(temp));
						tipsPage.showTips(BindPhonePage.this, new TipsPage.TipsonClickListener() {
							@Override
							public void onclickOk() {
								UserMgr.ExitLogin(getContext());
								EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
								mSite.toLoginPage(new HashMap<String, Object>(),getContext());
							}

							@Override
							public void onclickCancel() {

							}
						});
						m_commonView.isTimerDone = true;
					}
					else
					{
						if(info.mMsg != null && info.mMsg.length() > 0) m_commonView.showErrorTips(info.mMsg);
						else m_commonView.showErrorTips(getContext().getResources().getString(R.string.bindphonepage_othererror));
					}

					if(info.mCode != 0)
					{
						m_commonView.centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.bindphonepage_reget));
						m_commonView.isTimerDone = true;
					}
				}
			});
		}

		@Override
		public void onComfirmBtn(final String phoneNum, final String verityCode, final String areaCodeNum) {
			m_commonView.reSetData();
			if(mSite != null)
			{
				HashMap<String ,Object> params = new HashMap<>();
				params.put("mode", ResetPswPage.STATE_BINDPHONE);
				LoginPageInfo loginNeedInfo = new LoginPageInfo();
				loginNeedInfo.m_info = loginInfo;
				loginNeedInfo.m_phoneNum = phoneNum;
				loginNeedInfo.m_verityCode = verityCode;
				loginNeedInfo.m_areaCodeNum = areaCodeNum;
				params.put("info",loginNeedInfo);
				params.put("relogininfo",m_reLoginInfo);
				mSite.bindSuccess(params,getContext());
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
			mSite.onBackToLastPage(getContext());
		}

		@Override
		public void toAgreeWebView() {
			if(CommonView.AGREE_URL != null && CommonView.AGREE_URL.length() > 0)
			{
				mSite.OpenWebView(CommonView.AGREE_URL,getContext());
			}
		}

		@Override
		public void onOkCallBackForSendTongJi() {

		}
	};

	private String getUserId()
	{
		String out = null;
		if(loginInfo != null)
		{
			out = loginInfo.mUserId;
		}
		return out;
	}

	protected void setLoginInfo(LoginInfo info)
	{
		this.loginInfo = info;
	}
	
	protected void SetBackground(Bitmap bk)
	{
		if(m_commonView != null)
		{
			if(bk != null)
			{
				bkBmp = bk;
				m_commonView.setBackgroundDrawable(new BitmapDrawable(bkBmp));
			}
			else
			{
				m_commonView.setBackgroundColor(Color.WHITE);
			}
		}
	}

	@Override
	public void onBack() {
		mSite.onBack(getContext());
	}

	@Override
	public void onClose() {
		TongJiUtils.onPageEnd(getContext(), R.string.绑定手机);
	}

	@Override
	public void onPause() {
		super.onPause();
		TongJiUtils.onPagePause(getContext(), R.string.绑定手机);
	}

	@Override
	public void onResume() {
		super.onResume();
		TongJiUtils.onPageResume(getContext(), R.string.绑定手机);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		super.onPageResult(siteID, params);
		if (siteID == SiteID.CHOOSE_COUNTRY)
		{
			if(params != null)
			{
				LoginPageInfo info = (LoginPageInfo) params.get("info");
				m_commonView.setCountryAndCodeNum(info.m_country,info.m_areaCodeNum);
			}
		}
	}
}
