package cn.poco.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import org.json.JSONObject;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.business.site.JoinPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.filter;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyTextButton;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * 马上参加页面
 */
public class JoinPage extends IPage
{
	//private static final String TAG = "马上参加";

	protected JoinPageSite m_site;

	protected ImageView mBtnCancel;
	protected TextView mTitle;
	protected AdIntroImgView mIntroImgView;
	protected MyTextButton mBtnOk;
	protected AbsChannelAdRes mChannelAdRes;
	protected AbsChannelAdRes.GatePageData mRes;
	protected JSONObject mPostStr;
	protected Bitmap mGlassBk;

	public JoinPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (JoinPageSite)site;

		Init();

		TongJiUtils.onPageStart(getContext(), R.string.商业_马上参加);
	}

	protected void Init()
	{
		TongJi2.AddCountByRes(getContext(), R.integer.商业_介绍页);
		this.setBackgroundColor(0xFFEDEDE9);
		FrameLayout.LayoutParams fl;

		FrameLayout topBar = new FrameLayout(getContext());
		/*BitmapDrawable bmpDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.business_top_bk);
		if(bmpDraw != null)
		{
			bmpDraw.setTileModeX(Shader.TileMode.REPEAT);
		}
		topBar.setBackgroundDrawable(bmpDraw);*/
		topBar.setBackgroundColor(0xf4ffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
		this.addView(topBar, fl);
		{
			mBtnCancel = new ImageView(getContext());
			mBtnCancel.setImageResource(R.drawable.framework_back_btn);
			ImageUtils.AddSkin(getContext(), mBtnCancel);
			mBtnCancel.setOnTouchListener(m_btnLst2);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			topBar.addView(mBtnCancel, fl);

			mTitle = new TextView(getContext());
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			mTitle.setTextColor(0xff333333);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			topBar.addView(mTitle, fl);
		}

		mIntroImgView = new AdIntroImgView(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(90);
		fl.bottomMargin = ShareData.PxToDpi_xhdpi(100);
		this.addView(mIntroImgView, fl);
		mIntroImgView.setOnClickListener(m_btnLst);

		FrameLayout bottomBar = new FrameLayout(getContext());
		/*bmpDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.business_bottom_bk);
		if(bmpDraw != null)
		{
			bmpDraw.setTileModeX(Shader.TileMode.REPEAT);
		}
		bottomBar.setBackgroundDrawable(bmpDraw);*/
		bottomBar.setBackgroundColor(0xffffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
		fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
		this.addView(bottomBar, fl);
		{
			mBtnOk = new MyTextButton(getContext());
			mBtnOk.setBk(R.drawable.business_signup_btn_bk);
			mBtnOk.setName(R.string.business_signup_btn_name, 14, 0xffffffff, false);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			bottomBar.addView(mBtnOk, fl);
			mBtnOk.setOnTouchListener(m_btnLst2);
		}
	}

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.商业_马上参加);

		super.onClose();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.商业_马上参加);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.商业_马上参加);
		super.onResume();
	}

	/**
	 * @param params {@link HomePageSite#BUSINESS_KEY}:BusinessRes
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		mChannelAdRes = (AbsChannelAdRes)params.get(HomePageSite.BUSINESS_KEY);
		if(mChannelAdRes != null)
		{
			mRes = (AbsChannelAdRes.GatePageData)mChannelAdRes.GetPageData(AbsChannelAdRes.GatePageData.class);
		}
		mPostStr = (JSONObject)params.get(HomePageSite.POST_STR_KEY);
		if(mPostStr == null)
		{
			mPostStr = new JSONObject();
		}

		if(mIntroImgView != null)
		{
			mIntroImgView.clear();
		}

		if(mRes != null)
		{
			if(mRes.mAdm != null && mRes.mAdm.length > 0)
			{
				Bitmap bmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity)getContext(), mRes.mAdm[0], 0, 3.0f / 4.0f, MakeBmpV2.FLIP_NONE);
				if(bmp != null)
				{
//					ViewGroup.LayoutParams layoutParams = mIntroImgView.getLayoutParams();
//					layoutParams.width = ShareData.m_screenWidth;
//					layoutParams.height = layoutParams.width * bmp.getHeight() / bmp.getWidth();
//					mIntroImgView.setLayoutParams(layoutParams);
					mIntroImgView.setImageBitmap(bmp);
				}
			}

			if(mRes.mTitle != null)
			{
				mTitle.setText(mRes.mTitle);
			}
		}
	}

	@Override
	public void onBack()
	{
		m_site.OnBack(getContext());
	}

	protected OnAnimationClickListener m_btnLst2 = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mBtnCancel)
			{
				m_site.OnBack(getContext());
			}
			else if(v == mBtnOk)
			{
				if(mRes != null)
				{
					Utils.UrlTrigger(getContext(), mRes.mJoinTj);
					if(mRes.mInputInfoArr.size() > 0)
					{
						if(mGlassBk == null)
						{
							mGlassBk = CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth / 2, ShareData.m_screenHeight / 2);
							if(mGlassBk != null)
							{
								mGlassBk = filter.fakeGlassBeauty(mGlassBk, 0);
							}
						}
						//R.style.dialog
						ActSignUpDialog dlg = new ActSignUpDialog(getContext(), R.style.MyTheme_Dialog_Transparent_Fullscreen_NO_ANIM);
						dlg.setBk(mGlassBk);
						dlg.show();
						dlg.setOkListener(new ActSignUpDialog.OkListener()
						{
							@Override
							public void onOk(JSONObject postStr)
							{
								Utils.UrlTrigger(getContext(), mRes.mSubmitTj);
								HomePageSite.postStrMerge(mPostStr, postStr);
								m_site.OnJoin(mChannelAdRes, mPostStr,getContext());
							}
						});
						dlg.setBusinessRes(mChannelAdRes);
					}
					else
					{
						m_site.OnJoin(mChannelAdRes, mPostStr,getContext());
					}
				}
			}
		}

		@Override
		public void onTouch(View v)
		{

		}

		@Override
		public void onRelease(View v)
		{

		}
	};

	protected View.OnClickListener m_btnLst = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == mIntroImgView)
			{
				String url = null;
				if(mRes != null && mRes.mAdmClick != null && mRes.mAdmClick.length > 0)
				{
					url = mRes.mAdmClick[0];
				}
				m_site.OnClickImg(getContext(), url);
			}
		}
	};
}
