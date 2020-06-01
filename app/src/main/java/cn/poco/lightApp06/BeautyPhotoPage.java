package cn.poco.lightApp06;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.ctrls.SharedTipsView;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.advanced.ImageUtils;
import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.site.CameraPageSite300;
import cn.poco.camera3.ui.PreviewBackMsgToast;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.filter4.WatermarkAdapter;
import cn.poco.filter4.WatermarkItem;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.filter;
import cn.poco.lightApp06.site.BeautyPhotoPageSite;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.share.ShareFrame;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.share.SimpleSharePage;
import cn.poco.share.SinaRequestActivity;
import cn.poco.statisticlibs.PhotoStat;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.PhotoMark;
import cn.poco.utils.Utils;
import cn.poco.view.PictureView;
import cn.poco.view.material.VerFilterViewEx;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

import static cn.poco.setting.SettingInfoMgr.GetSettingInfo;

/**
 * Created by pocouser on 2017/12/13.
 */

public class BeautyPhotoPage extends IPage implements VerFilterViewEx.ControlCallback
{
	private static final int TAG = R.string.动态贴纸_分享;
	private BeautyPhotoPageSite mPageSite;

	private PictureView mView;
	//private ImageView mImage;
	private ImageView mSaveView;
	private FrameLayout mShare;
	private ImageView mShareLogo;
	private TextView mShareText;
	private FrameLayout mBack;
	private ImageView mBackLogo;
	private TextView mBackText;

	private SimpleSharePage mSharePage;

	private Bitmap mShareBmp;
	private String mSavePath;
	private SinaBlog mSina;
	private QzoneBlog2 mQzone;
	private WeiXinBlog mWeiXin;
	private FacebookBlog mFacebook;
	private TwitterBlog mTwitter;
	private InstagramBlog mInstagram;
	private boolean m_bmpSaved;
	private Bitmap mScreenshots;

	//是否保存过
	private boolean isDoSaved;

	//是否社区调用
	private boolean isFromCommunity;

	private int mOrientation;

	private int resId;
	private String resTjId;


	//水印recycler view
	private boolean isShowWaterMarkView;
	private boolean isDoingWVAnim;
	private int mWaterMarkId;
	private MyStatusButton mWCenterBtn;
	private FrameLayout mWatermarkFr;
	private ArrayList<WatermarkItem> mWatermarkResArr;
	private RecyclerView mWatermarkRecyclerView;
	private WatermarkAdapter mWatermarkAdapter;
	private boolean isDoWaterAlphaAnim = true;    //水印标识alpha动画

	//虚拟键隐藏与显示
	private int mOriginVisibility = -1; //还原系统设置
	private int mSystemUiVisibility = -1;//当前设置系统

	public BeautyPhotoPage(Context context, BaseSite site)
	{
		super(context, site);
		mWaterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
		mPageSite = (BeautyPhotoPageSite)site;
		MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照预览页_主页面);
	}

	/**
	 * @param params bmp:传过来的水印图片
	 *               res_id:使用的贴纸资源id
	 *            	 resTjId:使用的贴纸统计id
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		Bitmap bmp = null;
		if (params.containsKey("bmp"))
		{
			bmp = (Bitmap) params.get("bmp");
		}
		if(params.containsKey("res_id"))
		{
			resId = (Integer)params.get("res_id");
		}
		if(params.containsKey("res_tj_id"))
		{
			resTjId = (String)params.get("res_tj_id");
		}
		if (params.containsKey("orientation"))
		{
			mOrientation = (Integer)params.get("orientation");
		}
		if (params.containsKey("community"))
		{
			isFromCommunity = (Boolean) params.get("community");
		}
		init();
		setPicture(bmp);
	}

	private void init()
	{
		setBackgroundColor(Color.WHITE);

		LayoutParams params;

		mView = new PictureView(getContext());
		mView.setVerFilterCB(this);
		mView.setBackColor(Color.WHITE);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(mView, params);

		/*mImage = new ImageView(getContext());
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.START | Gravity.TOP;
		addView(mImage, params);*/

		mBack = new FrameLayout(getContext());
		mBack.setOnTouchListener(mOnAnimationClickListener);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.LEFT;
		params.leftMargin = CameraPercentUtil.WidthPxToPercent(106);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(140);
		addView(mBack, params);
		{
			mBackLogo = new ImageView(getContext());
			mBackLogo.setImageResource(R.drawable.camera_pre_back_gray);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mBack.addView(mBackLogo, params);

			mBackText = new TextView(getContext());
			mBackText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
			mBackText.setTypeface(Typeface.DEFAULT_BOLD);
			mBackText.setTextColor(0xff999999);
			mBackText.setText(getResources().getString(R.string.back));
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.WidthPxToPercent(68);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mBack.addView(mBackText, params);
		}

		mSaveView = new ImageView(getContext());
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera_photo_pre_bg);
		bmp = ImageUtils.AddSkin(getContext(), bmp);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mSaveView.setBackground(new BitmapDrawable(getResources(), bmp));
		} else {
			mSaveView.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
		}
		mSaveView.setImageResource(R.drawable.camera_photo_pre_save_logo);
		mSaveView.setOnTouchListener(mOnAnimationClickListener);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(170), CameraPercentUtil.WidthPxToPercent(170));
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(106);
		addView(mSaveView, params);

		mShare = new FrameLayout(getContext());
        mShare.setVisibility(!isFromCommunity ? VISIBLE : GONE);
        mShare.setOnTouchListener(mOnAnimationClickListener);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(106);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(140);
		addView(mShare, params);
		{
			mShareLogo = new ImageView(getContext());
			mShareLogo.setImageResource(R.drawable.camera_pre_share);
			ImageUtils.AddSkin(getContext(), mShareLogo);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mShare.addView(mShareLogo, params);

			mShareText = new TextView(getContext());
			mShareText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
			mShareText.setTypeface(Typeface.DEFAULT_BOLD);
			int color = ImageUtils.GetSkinColor();
			if(color != 0) mShareText.setTextColor(color);
			else mShareText.setTextColor(0xffe75988);
			mShareText.setText(getResources().getString(R.string.share));
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.WidthPxToPercent(68);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mShare.addView(mShareText, params);
		}
	}

	private void setPicture(Bitmap bitmap)
	{
		if(bitmap != null && !bitmap.isRecycled())
		{
			mShareBmp = bitmap;
			float bmp_scale = mShareBmp.getWidth() * 1f / mShareBmp.getHeight();

			int ratioTopMargin;
			float ratio;

			//横屏处理
			if (mOrientation % 180 > 0 && (bmp_scale == 4f/3 || bmp_scale >= 2f)) {
				ratio = bmp_scale;
				float vH = (int) (ShareData.m_screenRealWidth * 4f / 3);
				float scale_x = ShareData.m_screenRealWidth * 1f / mShareBmp.getWidth();
				float scale_y = vH / mShareBmp.getHeight();
				float scale = Math.min(scale_x, scale_y);
				ratioTopMargin = (int) ((vH - mShareBmp.getHeight() * scale) / 2f);
				mView.setRatioTopMargin(ratioTopMargin);
			} else {
				ratio = mShareBmp.getHeight() * 1f / mShareBmp.getWidth();
				ratioTopMargin = RatioBgUtils.GetTopHeightByRatio(ratio);
				mView.setRatioTopMargin(ratioTopMargin);
			}

			//全屏(除4：3)
			if (ratio > CameraConfig.PreviewRatio.Ratio_4_3)
			{
				//string.format 不要使用Local.getDefault()，会出现小数点变成“，”，应该使用China，English
				//http://blog.csdn.net/ttdevs/article/details/69664669
				int ratioBottomMargin = RatioBgUtils.GetBottomHeightByRation(ratio);
				if (Float.valueOf(String.format(Locale.CHINA, "%.2f", ratio).trim())
						>= Float.valueOf(String.format(Locale.CHINA, "%.2f", 16f/9).trim()) && ratioBottomMargin > 0) {
					mView.setDoRatioTopMarginAnim(true);

                    //btn上移
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) mBack.getLayoutParams();
                    layoutParams.bottomMargin += CameraPercentUtil.HeightPxToPercent(100);
                    mBack.requestLayout();

                    layoutParams = (LayoutParams) mSaveView.getLayoutParams();
                    layoutParams.bottomMargin += CameraPercentUtil.HeightPxToPercent(100);
                    mSaveView.requestLayout();

                    layoutParams = (LayoutParams) mShare.getLayoutParams();
					layoutParams.bottomMargin += CameraPercentUtil.HeightPxToPercent(100);
                    mShare.requestLayout();
                }
				mView.updateHeight(ShareData.m_screenRealHeight, ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(320), true);
			}
			mView.requestLayout();
			mView.setOrgImage(mShareBmp);
            mShare.setVisibility(!isFromCommunity ? VISIBLE : GONE);
            if(!(bmp_scale >= 3f / 4) && bmp_scale != 1f)
			{
				mBackLogo.setImageResource(R.drawable.camera_pre_back);
				mBackText.setTextColor(Color.WHITE);
				mBackText.setShadowLayer(CameraPercentUtil.HeightPxToPercent(5), 0, CameraPercentUtil.HeightPxToPercent(2), ImageUtils.GetColorAlpha(Color.BLACK, 0.5f));
				ImageUtils.RemoveSkin(getContext(), mShareLogo);
				mShareLogo.setImageResource(R.drawable.camera_pre_share_white);
				mShareText.setTextColor(Color.WHITE);
				mShareText.setShadowLayer(CameraPercentUtil.HeightPxToPercent(5), 0, CameraPercentUtil.HeightPxToPercent(2), ImageUtils.GetColorAlpha(Color.BLACK, 0.5f));
			}

			//水印设置
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					setWaterMark();
				}
			}, 150);

			//照片统计
			SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
			if (settingInfo != null)
			{
				PhotoStat.Stat(getContext(), mShareBmp, CommonUtils.GetAppVer(getContext()), settingInfo.GetPoco2Id(true));
			}
		}
		else
		{
			Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_pic), Toast.LENGTH_SHORT).show();
		}
	}

	private Bitmap getScreenshots()
	{
		if(mScreenshots == null || mScreenshots.isRecycled()) mScreenshots = filter.fakeGlassBeauty(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight), 0xdcf5f5f5);
		return mScreenshots;
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus)
		{
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					resetSystemUiVisibility();
					changeSystemUiVisibility(View.GONE);
				}
			}, 200);
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility)
	{
		super.onWindowVisibilityChanged(visibility);
		changeSystemUiVisibility(visibility == View.GONE ?
				View.VISIBLE : View.GONE);
	}

	/**
	 * @param visibility {@link View#VISIBLE}：显示导航栏（非沉浸式），{@link View#GONE}：沉浸式导航栏处理
	 */
	public void changeSystemUiVisibility(int visibility)
	{
		if (visibility != mSystemUiVisibility)
		{
			int vis = ShareData.showOrHideStatusAndNavigation(getContext(), visibility == View.VISIBLE, mOriginVisibility, visibility == View.VISIBLE);
			if (mOriginVisibility == -1 && visibility == View.GONE)
			{
				mOriginVisibility = vis;
			}
			mSystemUiVisibility = visibility;
		}
	}

	public void resetSystemUiVisibility()
	{
		mSystemUiVisibility = -1;//重置当前设置系统
	}

	private void setWaterMark()
	{
		if (mView != null)
		{
			WatermarkItem item = WatermarkResMgr2.getInstance().GetWaterMarkById(mWaterMarkId);
			if (item != null && mView != null)
			{
				mView.setDrawWaterMark(true);
				mWaterMarkId = item.mID;
				if (isDoWaterAlphaAnim)
				{
					mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(),
							item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
				}
				else
				{
					mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(),
							item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
				}
				isDoWaterAlphaAnim = false;
			}
		}
	}

	private void initWaterMarkUI()
	{
		if (mWatermarkRecyclerView == null || mWatermarkFr == null || mWatermarkAdapter == null)
		{
			mWatermarkFr = new FrameLayout(getContext());
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(320));
			params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			this.addView(mWatermarkFr, params);
			{
				FrameLayout topBar = new FrameLayout(getContext());
				topBar.setBackgroundColor(Color.WHITE);
				params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				mWatermarkFr.addView(topBar, params);

				mWCenterBtn = new MyStatusButton(getContext());
				mWCenterBtn.setData(R.drawable.filterbeautify_watermark_icon, getContext().getString(R.string.filterpage_watermark));
				mWCenterBtn.setBtnStatus(true, !isShowWaterMarkView);
				mWCenterBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						boolean show = !isShowWaterMarkView;
						boolean isAnim = showWaterMarkView(show);
						if (!isAnim) {
							mView.doAnim(show);
						}
					}
				});
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.CENTER;
				topBar.addView(mWCenterBtn, params);
			}

			mWatermarkRecyclerView = new RecyclerView(getContext());
			((SimpleItemAnimator) mWatermarkRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
			mWatermarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
			mWatermarkRecyclerView.setHasFixedSize(true);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
			params.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
			mWatermarkFr.addView(mWatermarkRecyclerView, params);

			if (mWatermarkResArr == null)
			{
				mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
			}
			mWatermarkAdapter = new WatermarkAdapter(getContext());
			mWatermarkAdapter.SetData(mWatermarkResArr);//水印数据集
			mWatermarkAdapter.SetSelectedId(mWaterMarkId);
			mWatermarkAdapter.setListener(new WatermarkAdapter.OnItemClickListener()
			{
				@Override
				public void onItemClick(int position, WatermarkItem item)
				{
					//切换滤镜重新保存图片
					m_bmpSaved = false;
					mView.setDrawWaterMark(true);
					mWaterMarkId = item.mID;
					SettingInfoMgr.GetSettingInfo(getContext()).SetPhotoWatermarkId(mWaterMarkId);
					if (isDoWaterAlphaAnim)
					{
						mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
					}
					else
					{
						mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
					}
					isDoWaterAlphaAnim = false;
					scroll2Center(position);
				}
			});
			mWatermarkRecyclerView.setAdapter(mWatermarkAdapter);

			if (mWatermarkRecyclerView.getBackground() == null)
			{
				//水印素材区域毛玻璃处理
				Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenRealWidth, ShareData.m_screenRealHeight);
				Bitmap out = MakeBmp.CreateFixBitmap(temp, temp.getWidth(), CameraPercentUtil.HeightPxToPercent(320), MakeBmp.POS_END, 0, Bitmap.Config.ARGB_8888);
				Bitmap mask = filter.fakeGlassBeauty(out, 0x99000000);//60%黑色
				mWatermarkRecyclerView.setBackgroundDrawable(new BitmapDrawable(getResources(), mask));
			}

		}
	}

	private void scroll2Center(int position)
	{
		if (mWatermarkRecyclerView != null)
		{
			View view = mWatermarkRecyclerView.getLayoutManager().findViewByPosition(position);
			if (view != null)
			{
				float center = mWatermarkRecyclerView.getWidth() / 2f;
				float viewCenter = view.getX() + view.getWidth() / 2f;
				mWatermarkRecyclerView.smoothScrollBy((int) (viewCenter - center), 0);
			}
		}
	}

	/**
	 * 收展水印
	 * @param show
	 * @return true 表示正在进行
	 */
	private boolean showWaterMarkView(final boolean show)
	{
		if (isShowWaterMarkView == show || isDoingWVAnim) return true;
		this.isShowWaterMarkView = show;
		if (mWCenterBtn != null)
		{
			mWCenterBtn.setBtnStatus(true, !show);
		}
		if (show)
		{
			initWaterMarkUI();
			this.post(new Runnable()
			{
				@Override
				public void run()
				{
					mWatermarkRecyclerView.smoothScrollToPosition(mWatermarkAdapter.GetPosition());
				}
			});
		}

		float start = show ? CameraPercentUtil.HeightPxToPercent(320) : 0;
		float end = show ? 0 : CameraPercentUtil.HeightPxToPercent(320);
        float alphaS = show ? 1 : 0;
        float alphaE = show ? 0 : 1;
        ObjectAnimator object = ObjectAnimator.ofFloat(mWatermarkFr, "translationY", start, end);
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(mBack, "alpha", alphaS, alphaE);
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mSaveView, "alpha", alphaS, alphaE);
		ObjectAnimator alpha3 = ObjectAnimator.ofFloat(mShare, "alpha", alphaS, alphaE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(object, alpha1, alpha2, alpha3);
        animatorSet.setDuration(300);
        animatorSet.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isDoingWVAnim = true;
				if (show)
				{
					mWatermarkFr.setVisibility(VISIBLE);
				}
				else
                {
                    mBack.setVisibility(View.VISIBLE);
                    mSaveView.setVisibility(View.VISIBLE);
                    mShare.setVisibility(isFromCommunity ? View.GONE : View.VISIBLE);
                }
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				isDoingWVAnim = false;
				if (!show)
				{
					mWatermarkFr.setVisibility(GONE);
				}
				else
				{
					scroll2Center(mWatermarkAdapter.GetPosition());
                    mBack.setVisibility(View.GONE);
                    mSaveView.setVisibility(View.GONE);
                    mShare.setVisibility(View.GONE);
				}
			}
		});
        animatorSet.start();
        return false;
	}

	private void showSharePage()
	{
		closeSharePage();
		MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照分享页_主页面);
		mSharePage = new SimpleSharePage(getContext());
		mSharePage.needAnime();
		mSharePage.setScreenshots(getScreenshots(), ShareData.PxToDpi_xhdpi(550));
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.START | Gravity.TOP;
		addView(mSharePage, fl);
		ArrayList<SimpleSharePage.ShareType> shareTypeArrayList = new ArrayList<>();
		shareTypeArrayList.add(SimpleSharePage.ShareType.wechat);
		shareTypeArrayList.add(SimpleSharePage.ShareType.wechat_friends_circle);
		shareTypeArrayList.add(SimpleSharePage.ShareType.qq);
		shareTypeArrayList.add(SimpleSharePage.ShareType.qzone);
		shareTypeArrayList.add(SimpleSharePage.ShareType.sina);
		shareTypeArrayList.add(SimpleSharePage.ShareType.facebook);
		shareTypeArrayList.add(SimpleSharePage.ShareType.instagram);
		shareTypeArrayList.add(SimpleSharePage.ShareType.twitter);
		mSharePage.init(shareTypeArrayList, new SimpleSharePage.SimpleSharePageClickListener()
		{
			@Override
			public void onClick(SimpleSharePage.ShareType type)
			{
				if(type == null)
				{
					closeSharePage();
					return;
				}
				saveFile();
				switch(type)
				{
					case wechat:
						sendPicToWeiXin(mSavePath, true);
						break;

					case wechat_friends_circle:
						sendPicToWeiXin(mSavePath, false);
						break;

					case sina:
						sendPicToSina(mSavePath);
						break;

					case qq:
						sendPicToQQ(mSavePath);
						break;

					case qzone:
						sendPicToQzone(mSavePath);
						break;

					case facebook:
						sendPicToFacebook(mSavePath);
						break;

					case twitter:
						sendPicToTwitter(mSavePath, null);
						break;

					case instagram:
						sendPicToInstagram(mSavePath);
						break;

					case community:
						shareToCommunity();
						break;
				}
			}
		});
	}

	private void closeSharePage()
	{
		if(mSharePage != null)
		{
			MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照分享页_主页面);
			removeView(mSharePage);
			mSharePage.close();
			mSharePage = null;
			System.gc();
		}
	}

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mBack)
			{
				onBack();
			}
			else if(v == mSaveView)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_萌妆照预览页_主页面_保存);
				boolean saved = saveFile();
				if (saved) {
					showToast(R.string.succeed_save2);
					if (isFromCommunity) {
						if (mPageSite != null) mPageSite.onSaveToCommunity(getContext(), mSavePath, CameraPageSite300.makeCircleExtra(resId, resTjId));
						return;
					}
				} else {
					m_bmpSaved = false;
					showToast(R.string.saving_picture_failed);
					return;
				}
				if(mPageSite != null) mPageSite.onBack(getContext());
			}
			else if(v == mShare)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_萌妆照预览页_主页面_分享);
				showSharePage();
			}
		}
	};

	private void showToast(@StringRes int res) {
		PreviewBackMsgToast toast = new PreviewBackMsgToast();
		toast.setMsg(getResources().getString(res)).show(getContext());
//		Toast toast = Toast.makeText(getContext(), res, Toast.LENGTH_SHORT);
//		int topPaddingHeight = RatioBgUtils.getTopPaddingHeight(1f);
//		topPaddingHeight += ShareData.m_screenRealWidth * 1f / 1f + CameraPercentUtil.HeightPxToPercent(60);
//		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, topPaddingHeight);
//		toast.show();
	}

	private boolean saveFile()
	{
		if(mShareBmp != null && !mShareBmp.isRecycled())
		{
			if(m_bmpSaved && mSavePath != null && new File(mSavePath).exists()) return true;
			mSavePath = null;

			boolean addDate = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
			Bitmap saveBitmap = null;

			WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(mWaterMarkId);
			if (watermarkItem != null && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
			{
				saveBitmap = mShareBmp.copy(Bitmap.Config.ARGB_8888, true);

				PhotoMark.drawWaterMarkLeft(saveBitmap,
						MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), addDate);
			}

			if (addDate)
			{
				if (saveBitmap == null)
				{
					saveBitmap = mShareBmp.copy(Bitmap.Config.ARGB_8888, true);
				}
				PhotoMark.drawDataLeft(saveBitmap);
			}

			if (saveBitmap == null || saveBitmap.isRecycled()) {
				saveBitmap = mShareBmp;
			}

			mSavePath = Utils.SaveImg(getContext(), saveBitmap, Utils.MakeSavePhotoPath(getContext(), (float) saveBitmap.getWidth() / saveBitmap.getHeight()), 100, true);
			if(mSavePath == null) return false;
			m_bmpSaved = true;
			isDoSaved = true;
			return true;
		}
		return false;
	}

	private void sendPicToSina(String pic)
	{
		if (pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if (mSina == null) {
			mSina = new SinaBlog(getContext());
		}
		if (!mSina.checkSinaClientInstall()) {
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if (send_success)
				{
					switch (response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到新浪, getResources().getString(TAG));
							ShareTools.addIntegral(getContext());
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_萌妆照分享页_主页面);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
				}
			}
		});

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
		intent.putExtra("pic", pic);
		intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
		((Activity) getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	private void sendPicToQzone(String pic) {
		if (mQzone == null) {
			mQzone = new QzoneBlog2(getContext());
		}
		if (!mQzone.checkQQClientInstall()) {
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
			@Override
			public void sendComplete(int result) {
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
					TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到QQ空间, getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_萌妆照分享页_主页面);
				}
				else if (result == QzoneBlog2.SEND_CANCEL) {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToPublicQzone(1, pic))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
		}
	}

	private void sendPicToQQ(String pic) {
		if (mQzone == null) {
			mQzone = new QzoneBlog2(getContext());
		}
		if (!mQzone.checkQQClientInstall()) {
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
			@Override
			public void sendComplete(int result) {
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
//                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer., getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_萌妆照分享页_主页面);
				}
				else if (result == QzoneBlog2.SEND_CANCEL) {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToQQ(pic))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
		}
	}

	private void sendPicToWeiXin(String pic, final boolean WXSceneSession) {
		if (pic == null || pic.length() <= 0) return;

		if (mWeiXin == null) {
			mWeiXin = new WeiXinBlog(getContext());
		}
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
		if (mWeiXin.sendToWeiXin(pic, thumb, WXSceneSession)) {
			SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener() {
				@Override
				public void onCallFinish(int result) {
					switch (result) {
						case BaseResp.ErrCode.ERR_OK:
							if(WXSceneSession)
							{
								TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到微信好友, getResources().getString(TAG));
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_萌妆照分享页_主页面);
							}
							else
							{
								TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到朋友圈, getResources().getString(TAG));
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_萌妆照分享页_主页面);
							}
							ShareTools.addIntegral(getContext());
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
							break;
						case BaseResp.ErrCode.ERR_USER_CANCEL:
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
							break;
						case BaseResp.ErrCode.ERR_AUTH_DENIED:
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
							break;
						default:
							break;
					}
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(listener);
		} else {
			SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
		}
	}

	/**
	 * 发送图片到Facebook
	 * @param pic 图片路径(图片不能大于12mb)
	 */
	private void sendPicToFacebook(String pic)
	{
		if(mFacebook == null) mFacebook = new FacebookBlog(getContext());
		Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, -1, -1);
		final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.share_facebook_client_call));
		mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mFacebookProgressDialog.setCancelable(true);
		boolean send_success = mFacebook.sendPhotoToFacebookBySDK(bmp, new FacebookBlog.FaceBookSendCompleteCallback()
		{
			@Override
			public void sendComplete(int result, String error_info)
			{
				if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
				else return;
				if(result == FacebookBlog.RESULT_SUCCESS)
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					ShareTools.addIntegral(getContext());
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_萌妆照分享页_主页面);
				}
				else
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});

		if(!send_success)
		{
			mFacebookProgressDialog.dismiss();
			String message;
			switch(mFacebook.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 发送图片和文字到Twitter，两者至少有一种
	 * @param pic 图片路径
	 * @param content 文字内容
	 */
	private void sendPicToTwitter(String pic, String content)
	{
		if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
		if(!mTwitter.sendToTwitter(pic, content))
		{
			String message;
			switch(mTwitter.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_context_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
		MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_萌妆照分享页_主页面);
	}

	/**
	 * 发送图片到Instagram
	 * @param pic 图片
	 */
	private void sendPicToInstagram(String pic)
	{
		if(mInstagram == null) mInstagram = new InstagramBlog(getContext());
		if(!mInstagram.sendToInstagram(pic))
		{
			String message;
			switch(mInstagram.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
	}

	private void shareToCommunity()
	{
		if(mPageSite == null) return;
		if(!UserMgr.IsLogin(getContext(), null))
		{
			//未登录去登录
			mPageSite.OnLogin(getContext());
			return;
		}
		UserInfo userInfo = UserMgr.ReadCache(getContext());
		if(userInfo != null && TextUtils.isEmpty(userInfo.mMobile))
		{
			//未完善资料去完善资料
			mPageSite.onBindPhone(getContext());
			return;
		}
		if(mSavePath == null || TextUtils.isEmpty(mSavePath) || !FileUtil.isFileExists(mSavePath))
		{
			Toast.makeText(getContext(), R.string.share_error_image_is_null, Toast.LENGTH_SHORT).show();
			return;
		}
		mPageSite.onCommunity(getContext(), mSavePath, 1);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		switch(siteID)
		{
			case SiteID.PUBLISH_OPUS_PAGE:
				if(params != null && params.containsKey("isSuccess")){
					boolean b = (boolean)params.get("isSuccess");
					if(b) showSuccessDialog();
				}
				break;

			case SiteID.LOGIN:
			case SiteID.REGISTER_DETAIL:
			case SiteID.RESETPSW:
				if(!UserMgr.IsLogin(getContext(), null)) return;
				UserInfo userInfo = UserMgr.ReadCache(getContext());
				if(userInfo == null) return;
				if(TextUtils.isEmpty(userInfo.mMobile)) return ;
				shareToCommunity();
				break;
		}
		super.onPageResult(siteID, params);
	}

	private void showSuccessDialog()
	{
		SharedTipsView view = new SharedTipsView(getContext());

		final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
		view.setJump2AppClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mPageSite.OnCommunityHome(getContext());
				dialog.dismiss();
			}
		});
		view.setStayClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
		dialog.setContentView(view);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mSina != null) {
			mSina.onActivityResult(requestCode, resultCode, data, -1);
		}
		if (mQzone != null) {
			mQzone.onActivityResult(requestCode, resultCode, data);
		}
		if(mFacebook != null) {
			mFacebook.onActivityResult(requestCode, resultCode, data, 10086);
		}
		return super.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    public boolean onActivityKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
            case KeyEvent.KEYCODE_UNKNOWN:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_ENTER:
            {
                if (mSaveView != null && mOnAnimationClickListener != null) {
                    mOnAnimationClickListener.onAnimationClick(mSaveView);
                }
                return true;
            }
        }
        return super.onActivityKeyDown(keyCode, event);
    }

    @Override
	public void onBack()
	{
		if(mSharePage != null)
		{
			mSharePage.onBack();
			return;
		}

		//已经保存过不提示
		if (!isDoSaved)
		{
			showToast(R.string.cancel_save);
		}
        MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照预览页_主页面_返回);
		if(mPageSite != null) mPageSite.onBack(getContext());
	}

	@Override
	public void onClose()
	{
		if(mShareBmp != null && !mShareBmp.isRecycled())
		{
			mShareBmp.recycle();
			mShareBmp = null;
		}
		if(mScreenshots != null && !mScreenshots.isRecycled())
		{
			mScreenshots.recycle();
			mScreenshots = null;
		}
		if (mSina != null) {
			mSina.clear();
			mSina = null;
		}
		if (mQzone != null) {
			mQzone.clear();
			mQzone = null;
		}
		mWeiXin = null;
		mInstagram = null;
		if(mFacebook != null)
		{
			mFacebook.clear();
			mFacebook = null;
		}
		if(mTwitter != null)
		{
			mTwitter.clear();
			mTwitter = null;
		}
		if (mView != null) {
			mView.ReleaseMem();
			mView = null;
		}
		changeSystemUiVisibility(VISIBLE);
		SettingInfoMgr.Save(getContext());
		System.gc();
		super.onClose();
	}

	@Override
	public void OnFingerDown(int fingerCount)
	{
		if (isShowWaterMarkView) {
			boolean isAnim = showWaterMarkView(false);
			if (!isAnim)
			{
				mView.doAnim(false);
			}
		}
	}

	@Override
	public void OnFingerUp(int fingerCount)
	{

	}

	@Override
	public void OnClickWaterMask()
	{
		boolean show = !isShowWaterMarkView;
		boolean isAnim = showWaterMarkView(show);
		if (!isAnim) {
			mView.doAnim(show);
		}
	}

	@Override
	public void OnSelFaceIndex(int index)
	{

	}

	@Override
	public void OnAnimFinish()
	{

	}
}
