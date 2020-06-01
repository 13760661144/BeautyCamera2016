package cn.poco.pendant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.credits.Credit;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.pendant.site.PendantSite;
import cn.poco.pendant.view.GarbageView;
import cn.poco.pendant.view.PageView;
import cn.poco.pendant.view.SelectPanel;
import cn.poco.pendant.view.TitleItem;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DecorateGroupRes;
import cn.poco.resource.DecorateRes;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.GroupRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.PendantRecommendResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.BaseView;
import cn.poco.view.material.PendantViewEx;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/18
 */
public class PendantPage extends IPage {

	private int DEF_IMG_SIZE;

	/**
	 * 每页显示素材的个数
	 */
	public static int PAGE_COUNT = 24;

	private Context mContext;
	private PendantSite mSite;

	private SelectPanel mSelectPanel;

	private PendantViewEx mPendantView;

	private ImageView mPullUpView;

	private int mFrameWidth;
	private int mFrameHeight;
	private int mSelectPanelHeight;
	private int mSelectPanelHeight2;
	private int mDistance;
	private int mDeleteLayoutWidth;
	private int mDeleteLayoutHeight;

	private Bitmap mBitmap;
	private Bitmap mSelectPanelBgBmp;

	private ArrayList<DecorateGroupRes> mGroupRes = new ArrayList<>();
	private DecorateGroupRes mRecommendDecorate;
	private boolean mDownloadRecommend = false;
	private boolean mRealDownloadRecomend = false;

	private Bitmap mOutputBitmap;

	private boolean mIsExpand = true;
	private boolean mDoingAnimation = false;

	protected WaitAnimDialog mWaitDialog;
	private boolean mInit = false;

	private int mPendantLast = 1;

	private boolean mChange = false;

	private boolean mShowDialog = false;
	private boolean mOnce = false;

	private int mDefSelUri = 0;

	private WaitAnimDialog mDownloadDialog;

	private int mNotDownloadNum;

	private boolean mPullUp = false;
	private float mLastY = 0;

	private float mDownX = 0;
	private float mDownY = 0;

	private int mTouchSlop;

	private BaseRes mRecommendRes;
	private RecomDisplayMgr mRecomView;

//	private CloudAlbumDialog mBackHintDialog;

	private boolean mIsDelete = false;
	private FrameLayout mDeleteLayout;
	private TextView mDeleteTitle;
	private View mDeleteView;
	private int mDeletePosition;

	private Bitmap mDeleteBgBitmap;
	private FrameLayout mDeleteBgLayout;
	private GarbageView mDeleteButton;

	private AnimatorSet mDeleteCancelAnimator;
	private AnimatorSet mDeletedAnimator;

	private int mStartWidth;
	private int mStartHeight;
	private float mHeightDistance;

	private int mImgH;
	private int mViewH;
	private int mViewTopMargin;
	private static final int SHOW_VIEW_ANIM_TIME = 300;

	private int mStartY;
	private float mStartScale;

	private boolean mDown = false;

	public PendantPage(Context context, BaseSite site) {
		super(context, site);

		TongJiUtils.onPageStart(context, R.string.贴图);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_贴图页面_主页面);

		mContext = context;
		mSite = (PendantSite)site;

		initDatas();
		initViews();
	}

	/**
	 * 设置数据
	 *
	 * @param params 传入参数
	 *               imgs: RotationImg2[]/Bitmap
	 */
	@Override
	public void SetData(HashMap<String, Object> params) {

		if (params != null) {

			Object o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
			if (o != null && o instanceof Integer) {
				mDefSelUri = (Integer)o;
			}

			Object imgs = params.get("imgs");
			if (imgs != null && imgs instanceof RotationImg2[]) {
				mBitmap = ImageUtils.MakeBmp(getContext(), imgs, DEF_IMG_SIZE, DEF_IMG_SIZE);
			}

			if (imgs != null && imgs instanceof Bitmap) {
				mBitmap = (Bitmap)imgs;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
			if (o instanceof Integer) {
				mImgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if (o instanceof Integer) {
				mViewH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
			if (o instanceof Integer) {
				mViewTopMargin = (int)o;
			}

			o = params.get("bgimg");
			if (o instanceof Bitmap) {
				generateSelectPanelBgBmp((Bitmap)o);
			}

			mPendantView.setImage(mBitmap);
		}

		setDatas();
	}

	/**
	 * 初始化数据
	 */
	private void initDatas() {
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mDistance = ShareData.PxToDpi_xhdpi(212);
		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

		mSelectPanelHeight2 = ShareData.PxToDpi_xhdpi(532);
		mSelectPanelHeight = mSelectPanelHeight2 - mDistance;

		mFrameWidth = ShareData.m_screenWidth;
		mFrameWidth -= mFrameWidth % 2;
		mFrameWidth += 2;

		mFrameHeight = ShareData.m_screenHeight - mSelectPanelHeight;
		mFrameHeight -= mFrameHeight % 2;
		mFrameHeight += 2;

		mDeleteLayoutWidth = ShareData.PxToDpi_xhdpi(195);
		mDeleteLayoutHeight = ShareData.PxToDpi_xhdpi(150);

//		mPendantLast = TagMgr.GetTagIntValue(mContext, Tags.PENDANT_LAST, 1);

		if (DownloadMgr.getInstance() != null) {
			DownloadMgr.getInstance().AddDownloadListener(mDownloadListener);
		}
	}

	/**
	 * 初始化View
	 */
	private void initViews() {

		FrameLayout.LayoutParams params;


		mPendantView = new PendantViewEx(mContext, mCallback);
		params = new FrameLayout.LayoutParams(mFrameWidth, mFrameHeight);
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		addView(mPendantView, params);

		mPullUpView = new ImageView(mContext);
		mPullUpView.setImageResource(R.drawable.pendant_pull_up_tip);
		mPullUpView.setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(10));
		params = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(40));
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = mSelectPanelHeight2;
		addView(mPullUpView, params);
//		mPullUpView.setVisibility(INVISIBLE);

		mDeleteBgLayout = new FrameLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mDeleteBgLayout, params);
		mDeleteBgLayout.setVisibility(INVISIBLE);

		mSelectPanel = new SelectPanel(mContext, mSite);
		params = new FrameLayout.LayoutParams(mFrameWidth, mSelectPanelHeight2);
		params.gravity = Gravity.BOTTOM | Gravity.START;
		addView(mSelectPanel, params);

		mWaitDialog = new WaitAnimDialog((Activity)mContext);

		mDownloadDialog = new WaitAnimDialog((Activity)mContext);
		mDownloadDialog.setCancelable(true);

		mRecomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback() {
			@Override
			public void UnlockSuccess(BaseRes res) {
				mShowDialog = false;
				Toast.makeText(getContext(), R.string.pendant_unlock_success, Toast.LENGTH_SHORT).show();
				if (mRecommendDecorate != null) {
					TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + mRecommendDecorate.m_id);
				}

//				realDownloadRecommendRes();
			}

			@Override
			public void OnCloseBtn() {
			}

			@Override
			public void OnBtn(int state) {

			}

			@Override
			public void OnClose() {
				mShowDialog = false;
			}

			@Override
			public void OnLogin() {
				mSite.onLogin(getContext());
			}
		});
		mRecomView.Create(this);

		mDeleteButton = new GarbageView(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(mDeleteButton, params);
		mDeleteButton.setVisibility(INVISIBLE);

		mDeleteLayout = new FrameLayout(mContext);
		mDeleteLayout.setBackgroundResource(R.drawable.pendant_delete_item_bg);
		mDeleteLayout.setAlpha(0.9f);
		params = new LayoutParams(mDeleteLayoutWidth, mDeleteLayoutHeight);
		addView(mDeleteLayout, params);
		{
			mDeleteTitle = new TextView(mContext);
			mDeleteTitle.setTextColor(0xb3000000);
			mDeleteTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mDeleteLayout.addView(mDeleteTitle, params);
		}
		mDeleteLayout.setVisibility(INVISIBLE);

		initListeners();
	}

	private void setDatas() {

		if (mInit) {
			return;
		}
		mInit = true;

		mGroupRes.clear();
		mGroupRes.addAll(DecorateResMgr2.getInstance().GetGroupResArr());
		mSelectPanel.setDecorateGroupRes(mGroupRes, mOnClickRes, mOnItemLongClickListener, mTitleItemClickListener, true);
		addRecommend();
		//设置new状态
		int len = mGroupRes.size();
		for (int i = 0; i < len; i++) {
			if (DecorateResMgr2.getInstance().IsNewGroup(mGroupRes.get(i).m_id)) {
				mSelectPanel.setNewByIndex(i, true);
			}
		}

		if (mRecommendDecorate == null) {
			mPendantLast = 0;
			showSelectPanel();
		}

		mNotDownloadNum = DecorateResMgr2.getInstance().GetNoDownloadedGroupResArr(getContext()).size();
		mSelectPanel.setNotDownloadNumber(mNotDownloadNum);

		if (mImgH > 0 && mViewH > 0) {
			mStartY = (int)(mViewTopMargin + (mViewH - mFrameHeight) / 2f);
			float scaleX = (mFrameWidth-2) * 1f / mBitmap.getWidth();
			float scaleY = (mFrameHeight-2) * 1f / mBitmap.getHeight();
			mStartScale = mImgH / (mBitmap.getHeight() * Math.min(scaleX, scaleY));
			showViewAnim();
		}
	}

	private void showViewAnim() {

		mDoingAnimation = true;
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator object1 = ObjectAnimator.ofFloat(mPendantView, "scaleX", mStartScale, 1);
		ObjectAnimator object2 = ObjectAnimator.ofFloat(mPendantView, "scaleY", mStartScale, 1);
		ObjectAnimator object3 = ObjectAnimator.ofFloat(mPendantView, "translationY", mStartY, 0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mSelectPanel, "translationY", mSelectPanelHeight2 + ShareData.PxToDpi_xhdpi(50), 0);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mPullUpView, "translationY", mSelectPanelHeight2 + ShareData.PxToDpi_xhdpi(50), 0);
		animatorSet.setDuration(SHOW_VIEW_ANIM_TIME);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(object1).with(object2).with(object3).with(yAnimator).with(yAnimator2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;

				mDeleteBgLayout.post(new Runnable() {
					@Override
					public void run() {
						Bitmap tempBmp = CommonUtils.GetScreenBmp((Activity)mContext, ShareData.m_screenWidth, ShareData.m_screenHeight);
						mDeleteBgBitmap = BeautifyResMgr2.MakeBkBmp(tempBmp, ShareData.m_screenWidth, ShareData.m_screenHeight);
						mDeleteBgLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), mDeleteBgBitmap));
						tempBmp.recycle();
					}
				});
			}
		});
		animatorSet.start();

	}

	private void showSelectPanel() {
		int position = 0;
		if (mDefSelUri != 0) {
			for (DecorateGroupRes res : mGroupRes) {
				if (res.m_id == mDefSelUri) {
					mSelectPanel.scrollTo(position);
					break;
				} else {
					position++;
				}
			}
		} else {
			mSelectPanel.scrollTo(mPendantLast);
		}
		mDefSelUri = 0;
		mPendantLast = 1;
	}

	/**
	 * 初始化监听器
	 */
	private void initListeners() {
		mSelectPanel.setOnCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPendantView != null && mPendantView.getCurPendantNum() != 0)
				{
					showExitDialog();
				}
				else
				{
					onCancel();
				}
			}
		});

		mSelectPanel.setOnOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCompleted();
			}
		});
	}

	/**
	 * 折叠
	 */
	private void collapse() {

		if (mDoingAnimation) {
			return;
		}

		mIsExpand = false;

		mDoingAnimation = true;

		ValueAnimator animator = ValueAnimator.ofInt(0, ShareData.PxToDpi_xhdpi(212));
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer)animation.getAnimatedValue();
				mSelectPanel.setTranslationY(value);
				mPullUpView.setTranslationY(value);
			}
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mSelectPanel.setTranslationY(0);
				LayoutParams params = new LayoutParams(mFrameWidth, mSelectPanelHeight);
				params.gravity = Gravity.BOTTOM | Gravity.START;
				mSelectPanel.setLayoutParams(params);

				PAGE_COUNT = 12;
				mSelectPanel.setDecorateGroupRes(mGroupRes, mOnClickRes, mOnItemLongClickListener, mTitleItemClickListener, false);

				mDoingAnimation = false;
			}
		});
		animator.setDuration(350);
		animator.start();
	}

	/**
	 * 添加推荐资源
	 */
	private void addRecommend() {
		ArrayList<ThemeRes> orgThemeRes = ThemeResMgr2.getInstance().GetAllResArr();
		ArrayList<RecommendRes> pendantRecommendRes = PendantRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(getContext(), null);
		DecorateGroupRes pendantGroupRecommendRes = null;
		RecommendRes res = null;
		mRecommendDecorate = null;
		mRecommendRes = null;

		if (orgThemeRes != null && pendantRecommendRes != null) {
			PENDANT_RECOM_OK:
			for (RecommendRes recommendRes : pendantRecommendRes) {
				for (ThemeRes themeRes : orgThemeRes) {
					if (themeRes.m_id == recommendRes.m_id && TagMgr.CheckTag(getContext(), Tags.ADV_RECO_PENDANT_DOWNLOAD_ID + recommendRes.m_id) && themeRes.m_decorateIDArr != null && themeRes.m_decorateIDArr.length > 0) {
						ArrayList<DecorateRes> tempArr = DecorateResMgr2.getInstance().GetResArr2(themeRes.m_decorateIDArr, false);
						for (int i = 0; i < tempArr.size(); i++) {
							if (tempArr.size() == themeRes.m_decorateIDArr.length && tempArr.get(i).m_type == BaseRes.TYPE_NETWORK_URL) {
								//mPendantGroupRecommendRes = new ArrayList<DecorateGroupRes>();
								DecorateGroupRes group = new DecorateGroupRes();
								group.m_id = themeRes.m_id;
								group.m_name = themeRes.m_name;
								group.m_titleThumb = themeRes.m_decorateThumb;
								group.m_group = tempArr;
								pendantGroupRecommendRes = group;
								res = recommendRes;
								break PENDANT_RECOM_OK;
							}
						}
					}
				}
			}
		}

		if (pendantRecommendRes != null && pendantRecommendRes.size() > 0 && pendantGroupRecommendRes != null) {
			mRecommendDecorate = pendantGroupRecommendRes;
			mRecommendRes = res;
			downloadRecommend();
		}
	}

	/**
	 * 下载推荐素材
	 */
	private void downloadRecommend() {

		int recommendSize = mRecommendDecorate.m_group.size();
		//下载装饰缩略图
		BaseRes[] ress = new BaseRes[recommendSize];
		for (int i = 0; i < recommendSize; i++) {
			ress[i] = mRecommendDecorate.m_group.get(i);
		}

		mDownloadRecommend = true;

		DownloadMgr.getInstance().DownloadRes(ress, true, new DownloadMgr.Callback2() {

			@Override
			public void OnProgress(int downloadId, IDownload res, int progress) {
			}

			@Override
			public void OnComplete(int downloadId, IDownload res) {
			}

			@Override
			public void OnFail(int downloadId, IDownload res) {
			}

			@Override
			public void OnGroupComplete(int downloadId, IDownload[] resArr) {
				if (resArr == null || mRecommendDecorate == null) {
					return;
				}

				if (mSelectPanel == null) {
					mGroupRes.add(0, mRecommendDecorate);
				} else {
					boolean isLock = MgrUtils.unLockTheme(mRecommendRes.m_id) != null;
					mSelectPanel.addRecommend(mRecommendDecorate, mOnce, isLock);
					if (!mOnce) {
						mOnce = true;
						showSelectPanel();
					} else {
						mSelectPanel.scrollTo(1);
					}
				}

				Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
					@Override
					public boolean queueIdle() {
						mDownloadRecommend = false;
						return false;
					}
				});
			}

			@Override
			public void OnGroupFail(int downloadId, IDownload[] resArr) {
			}

			@Override
			public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress) {
			}

		});
	}

	private void showRecommendDetailDialog() {

		if (mShowDialog) {
			return;
		}

		TongJi2.AddCountByRes(mContext, R.integer.修图_素材美化_贴图_推荐位);
		MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_贴图推荐位);

		mShowDialog = true;
		mRecomView.SetBk(CommonUtils.GetScreenBmp((Activity)getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
		mRecomView.SetDatas(mRecommendRes, ResType.DECORATE.GetValue());
		mRecomView.Show();
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		if (siteID == SiteID.DOWNLOAD_MORE && mSelectPanel != null) {
			mSelectPanel.onPageBack();
			if (params != null) {
				boolean del = false;
				Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
				if (obj instanceof Boolean) {
					del = (Boolean)obj;
				}

				if (del) {
					reloadData();
				}
			}
		}

		if ((siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO) && params != null) {
			Object o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
			if (o != null && o instanceof Integer) {
				mDefSelUri = (Integer)o;
			}
			int position = 0;
			if (mDefSelUri != 0) {
				for (DecorateGroupRes res : mGroupRes) {
					if (res.m_id == mDefSelUri) {
						mSelectPanel.scrollTo(position);
						break;
					} else {
						position++;
					}
				}
			}
			mDefSelUri = 0;
		}

		if (siteID == SiteID.LOGIN) {
			mRecomView.UpdateCredit();
		}
	}

	/**
	 * 单击完成按钮时调用
	 */
	private void onCompleted() {

		if (!mChange) {
			onCancel();
			return;
		}

		if (mPendantView.getPendantArray() != null) {
			int len = mPendantView.getPendantArray().size();

			if (len > 0) {
				String[] tongjiIds = new String[len];

				String param = "";
				Object obj;
				String idStr;
				for (int i = 0; i < len; i++) {
					obj = mPendantView.getPendantArray().get(i).m_ex;
					if (obj instanceof BaseRes) {
						idStr = String.valueOf(((BaseRes)obj).m_tjId);
						tongjiIds[i] = idStr;
						TongJi2.AddCountById(idStr);
						param += Credit.APP_ID + Credit.DECORATE + ((BaseRes)obj).m_id + ",";
					}
				}

				MyBeautyStat.onUseDecorate(tongjiIds);

				if (param.length() > 0) {
					final String incomeInfo = param.substring(0, param.length() - 1);
					Credit.CreditIncome(incomeInfo, getContext(), R.integer.积分_首次使用新素材);
				}
			}
		}

//		TagMgr.SetTagValue(getContext(), Tags.PENDANT_LAST, Integer.toString(mSelectPanel.getGroupIndex()));

		mOutputBitmap = mPendantView.getOutPutBmp();

		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mOutputBitmap);
		params.putAll(getBackAnimParam());
		mSite.onSave(getContext(), params);
	}

	@Override
	public void onBack() {

		if (mShowDialog) {
			mShowDialog = false;
			mRecomView.OnCancel(true);
		} else {
			if (mPendantView != null && mPendantView.getCurPendantNum() != 0) {
				showExitDialog();
			} else {
				onCancel();
			}
		}
	}

	public void onCancel() {
//		if (mChange) {
//			if (mBackHintDialog == null) {
//				mBackHintDialog = new CloudAlbumDialog(mContext,
//													   ViewGroup.LayoutParams.WRAP_CONTENT,
//													   ViewGroup.LayoutParams.WRAP_CONTENT);
//				ImageUtils.AddSkin(mContext, mBackHintDialog.getOkButtonBg());
//				mBackHintDialog.setCancelButtonText(R.string.cancel)
//						.setOkButtonText(R.string.ensure)
//						.setMessage(R.string.confirm_back)
//						.setListener(new CloudAlbumDialog.OnButtonClickListener() {
//							@Override
//							public void onOkButtonClick() {
//								mBackHintDialog.dismiss();
//								cancel();
//							}
//
//							@Override
//							public void onCancelButtonClick() {
//								mBackHintDialog.dismiss();
//							}
//						});
//			}
//			mBackHintDialog.show();
//		} else {
//			cancel();
//		}
		cancel();
	}

	private void cancel() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mBitmap);
		params.putAll(getBackAnimParam());
		mSite.onBack(getContext(), params);
	}

	private HashMap<String, Object> getBackAnimParam() {
		HashMap<String, Object> params = new HashMap<>();
		if (mDown) {
			params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (float)ShareData.PxToDpi_xhdpi(232) / 2);
			params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, mPendantView.getImgHeight());
		} else {
			params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, 0f);
			params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, (float) mImgH);
		}
		return params;
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(mContext, R.string.贴图);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(mContext, R.string.贴图);
	}

	@Override
	public void onClose() {
		// 清理资源
		if (DownloadMgr.getInstance() != null) {
			DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
		}

		clearExitDialog();
		if (mRecomView != null) {
			mRecomView.ClearAllaa();
			mRecomView = null;
		}

		if (mDeleteCancelAnimator != null && mDeleteCancelAnimator.isRunning()) {
			mDeleteCancelAnimator.cancel();
			mDeleteCancelAnimator = null;
		}

		mWaitDialog.dismiss();
		mWaitDialog = null;

		PAGE_COUNT = 24;

		TongJiUtils.onPageEnd(mContext, R.string.贴图);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_贴图页面_主页面);
	}

	/**
	 * 页面资源单击回调
	 */
	private PageView.OnClickRes mOnClickRes = new PageView.OnClickRes() {
		@Override
		public void onClick(final int groupIndex, final int position, final DecorateRes res) {

			if (mDoingAnimation) {
				return;
			}

			if (mRecommendDecorate != null && mGroupRes.get(groupIndex) == mRecommendDecorate && mRecommendRes != null) {

				showRecommendDetailDialog();
				return;
			}

			if (groupIndex >= 0 && groupIndex < mGroupRes.size()) {
				mSelectPanel.setNewByIndex(groupIndex, false);
				DecorateResMgr2.getInstance().DeleteGroupNewFlag(getContext(), mGroupRes.get(groupIndex).m_id);
			}

			int index = mPendantView.addPendant(res, null);
			if (index < 0) {
				Toast.makeText(getContext(), R.string.pendant_number_limit, Toast.LENGTH_SHORT).show();
			}

			mChange = true;

			if (mIsExpand) {
				collapse();
			}
		}
	};


	private SelectPanel.OnTitleItemClickListener mTitleItemClickListener = new SelectPanel.OnTitleItemClickListener() {
		@Override
		public void onItemClick(View view, int position, int lastPosition) {
			if (view instanceof TitleItem && (position == lastPosition || mDown) && !mDoingAnimation) {
				mSelectPanel.setUiEnable(false);
				final TitleItem item = (TitleItem)view;
				int height = mIsExpand ? mSelectPanelHeight2 : mSelectPanelHeight;
				height -= ShareData.PxToDpi_xhdpi(88);
				if (mDown) {

					MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_展开bar);
					mDoingAnimation = true;
					AnimatorSet set = new AnimatorSet();
					ObjectAnimator animator = ObjectAnimator.ofFloat(mSelectPanel, "translationY", height, 0);
					ValueAnimator animator1 = ValueAnimator.ofInt(mFrameHeight + ShareData.PxToDpi_xhdpi(232), mFrameHeight);
					animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							int value = (int)animation.getAnimatedValue();
							LayoutParams params = (LayoutParams) mPendantView.getLayoutParams();
							params.height = value;
							mPendantView.requestLayout();
						}
					});
					set.play(animator).with(item.getUpAnimator()).with(animator1);
					set.setDuration(300);
					set.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mDoingAnimation = false;
							mDown = false;
							mSelectPanel.setDown(false);
							mPullUpView.setVisibility(VISIBLE);
							item.showLine(true);
							mSelectPanel.setUiEnable(true);
						}

						@Override
						public void onAnimationStart(Animator animation) {
							item.showLine(true);
						}
					});
					set.start();

				} else {
					MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_收回bar);
					mDoingAnimation = true;
					AnimatorSet set = new AnimatorSet();
					ObjectAnimator animator = ObjectAnimator.ofFloat(mSelectPanel, "translationY", 0, height);
					ValueAnimator animator1 = ValueAnimator.ofInt(mFrameHeight, mFrameHeight + ShareData.PxToDpi_xhdpi(232));
					animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							int value = (int)animation.getAnimatedValue();
							LayoutParams params = (LayoutParams) mPendantView.getLayoutParams();
							params.height = value;
							mPendantView.requestLayout();
						}
					});
					set.play(animator).with(item.getDownAnimator()).with(animator1);
					set.setDuration(300);
					set.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mDoingAnimation = false;
							mDown = true;
							item.showLine(false);
							mSelectPanel.setDown(true);
							mSelectPanel.setUiEnable(true);
						}

						@Override
						public void onAnimationStart(Animator animation) {
							mPullUpView.setVisibility(INVISIBLE);
						}
					});
					set.start();
				}
			}
		}
	};

	private BaseView.ControlCallback mCallback = new BaseView.ControlCallback() {
		@Override
		public Bitmap MakeShowImg(Object info, int frW, int frH) {
			return null;
		}

		@Override
		public Bitmap MakeOutputImg(Object info, int outW, int outH) {
			return null;
		}

		@Override
		public Bitmap MakeShowFrame(Object info, int frW, int frH) {
			return null;
		}

		@Override
		public Bitmap MakeOutputFrame(Object info, int outW, int outH) {
			return null;
		}

		@Override
		public Bitmap MakeShowBK(Object info, int frW, int frH) {
			return null;
		}

		@Override
		public Bitmap MakeOutputBK(Object info, int outW, int outH) {
			return null;
		}

		@Override
		public Bitmap MakeShowPendant(Object info, int frW, int frH) {
			return cn.poco.imagecore.Utils.DecodeImage(getContext(), ((DecorateRes)info).m_res, 0, -1, frW * 2 / 3, frH * 2 / 3);

		}

		@Override
		public Bitmap MakeOutputPendant(Object info, int outW, int outH) {
			return null;
		}
	};

	private void generateSelectPanelBgBmp(Bitmap bgBitmap) {
		if (bgBitmap != null) {
			int height = (int)((mSelectPanelHeight2 * 1f / ShareData.m_screenHeight) * bgBitmap.getHeight());
			mSelectPanelBgBmp = Bitmap.createBitmap(bgBitmap, 0, bgBitmap.getHeight() - height, bgBitmap.getWidth(), height);
			mSelectPanel.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), mSelectPanelBgBmp));
		}
	}

	public DownloadMgr.DownloadListener mDownloadListener = new DownloadMgr.DownloadListener() {
		@Override
		public void OnDataChange(int resType, int downloadId, IDownload[] resArr) {
			if (resArr != null && ((BaseRes)resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH && !mDownloadRecommend) {
				if (resType == ResType.DECORATE.GetValue() && mSelectPanel != null) {
					reloadData();
				}
			}
		}
	};

	private void reloadData() {
		// 下载素材完成会回调（如推荐素材或素材中心）
		// 下载推荐素材也会回调
		mGroupRes.clear();
		mGroupRes.addAll(DecorateResMgr2.getInstance().GetGroupResArr());
		mSelectPanel.setDecorateGroupRes(mGroupRes, mOnClickRes, mOnItemLongClickListener, mTitleItemClickListener, true);

		addRecommend();

		//设置new状态
		int len = mGroupRes.size();
		for (int i = 0; i < len; i++) {
			if (DecorateResMgr2.getInstance().IsNewGroup(mGroupRes.get(i).m_id)) {
				mSelectPanel.setNewByIndex(i, true);
			}
		}

		if (mRealDownloadRecomend) {
			mNotDownloadNum--;
			mSelectPanel.setNotDownloadNumber(mNotDownloadNum);
		} else {
			mSelectPanel.setNotDownloadNumber(DecorateResMgr2.getInstance().GetNoDownloadedGroupResArr(getContext()).size());
		}

		mRealDownloadRecomend = false;
		mShowDialog = false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				mDownY = event.getY();
				if (findChild(event) == mPullUpView && !mDoingAnimation) {
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				// 这里要处理ACTION_UP事件, 因为这里拦截ACTION_UP事件后，
				// ACTION_UP事件会转换成ACTION_CANCEL事件传给子View，
				// 会丢失ACTION_UP事件的处理
				if (mIsDelete) {
					onDeleteCancel();
				}
		}

		return mIsDelete || super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		float x = event.getX();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				if (findChild(event) == mPullUpView && !mDoingAnimation) {
					mPullUp = true;

					if (!mIsExpand) {
						PAGE_COUNT = 24;
						mSelectPanel.setDecorateGroupRes(mGroupRes, mOnClickRes, mOnItemLongClickListener, mTitleItemClickListener, false);
						LayoutParams params = new LayoutParams(mFrameWidth, mSelectPanelHeight2);
						params.gravity = Gravity.BOTTOM | Gravity.START;
						mSelectPanel.setLayoutParams(params);
						mSelectPanel.setTranslationY(mDistance);
					}
					mLastY = y;
				} else {
					mPullUp = false;
					mLastY = -1;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mPullUp) {

					if (!mIsExpand) {
						float distance = y - mLastY;
						if (distance >= -mDistance && distance <= 0) {
							mSelectPanel.setTranslationY(mDistance + distance);
							mPullUpView.setTranslationY(mDistance + distance);
						}
					} else {
						float distance = y - mLastY;
						if (distance <= mDistance && distance >= 0) {
							mSelectPanel.setTranslationY(distance);
							mPullUpView.setTranslationY(distance);
						}
					}
				} else if (mIsDelete) {
					float deltaX = x - mDownX;
					float deltaY = y - mDownY;

					float progress = Math.abs(deltaY) / mHeightDistance * 100;
					if (progress > 100) {
						progress = 100f;
					}

					if (deltaY <= 0) {
						mDeleteButton.setProgress(progress);
					} else {
						mDeleteButton.setProgress(0);
					}
					mDeleteLayout.setTranslationX(deltaX);
					mDeleteLayout.setTranslationY(deltaY);

				}
				break;
			case MotionEvent.ACTION_UP:
				if (mPullUp) {
					mPullUp = false;
					mDoingAnimation = true;

					float start = mSelectPanel.getTranslationY();

					boolean click = Math.abs(y - mLastY) <= mTouchSlop;

					if ((click && !mIsExpand) || (!click && start < mDistance / 2f)) {
						mIsExpand = true;
						ValueAnimator animator = ValueAnimator.ofInt((int)start, 0);
						animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
							@Override
							public void onAnimationUpdate(ValueAnimator animation) {
								int value = (Integer)animation.getAnimatedValue();
								mSelectPanel.setTranslationY(value);
								mPullUpView.setTranslationY(value);
							}
						});
						animator.addListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								mDoingAnimation = false;
							}
						});

						animator.setDuration((long)(start / mDistance * 350));
						animator.start();
					} else {
						mIsExpand = false;
						ValueAnimator animator = ValueAnimator.ofInt((int)start, mDistance);
						animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
							@Override
							public void onAnimationUpdate(ValueAnimator animation) {
								int value = (Integer)animation.getAnimatedValue();
								mSelectPanel.setTranslationY(value);
								mPullUpView.setTranslationY(value);
							}
						});
						animator.addListener(new AnimatorListenerAdapter() {

							@Override
							public void onAnimationEnd(Animator animation) {

								mSelectPanel.setTranslationY(0);
								LayoutParams params = new LayoutParams(mFrameWidth, mSelectPanelHeight);
								params.gravity = Gravity.BOTTOM | Gravity.START;
								mSelectPanel.setLayoutParams(params);

								PAGE_COUNT = 12;
								mSelectPanel.setDecorateGroupRes(mGroupRes, mOnClickRes, mOnItemLongClickListener, mTitleItemClickListener, false);

								mDoingAnimation = false;
							}
						});
						animator.setDuration((long)((mDistance - start) / mDistance * 350));
						animator.start();
					}
				} else if (mIsDelete) {

					RectF rect = new RectF(mDeleteButton.getLeft(), mDeleteButton.getTop(), mDeleteButton.getRight(), mDeleteButton.getBottom());
					if (rect.contains(x, y)) {
						onDelete();
					} else {
						onDeleteCancel();
					}
				}

				mLastY = -1;
				break;
		}

		return mPullUp || mIsDelete || super.onTouchEvent(event);
	}

	private View findChild(MotionEvent e) {
		final int x = (int)e.getX();
		final int y = (int)e.getY();
		final int cCount = getChildCount();
		for (int i = cCount - 1; i >= 0; i--) {
			View v = getChildAt(i);
			if (v.getVisibility() != View.VISIBLE) continue;
			Rect outRect = new Rect();
			v.getHitRect(outRect);
			if (outRect.contains(x, y)) {
				return v;
			}
		}
		return null;
	}

	private SelectPanel.OnItemLongClickListener mOnItemLongClickListener = new SelectPanel.OnItemLongClickListener() {
		@Override
		public void onItemLongClick(View view, int position, String title, boolean isRecomment) {
			if (isRecomment) {
				return;
			}
			mIsDelete = true;
			mDeleteView = view;
			mDeletePosition = position;

			int[] loc = new int[2];
			view.getLocationOnScreen(loc);
			mStartWidth = view.getWidth();
			mStartHeight = view.getHeight();
			int deltaX = (mDeleteLayoutWidth - mStartWidth) / 2;
			int deltaY = (mDeleteLayoutHeight - mStartHeight) / 2;

			mDeleteTitle.setText(title);
			LayoutParams params = new LayoutParams(mDeleteLayoutWidth, mDeleteLayoutHeight);
			params.leftMargin = loc[0] - deltaX;
			params.topMargin = loc[1] - deltaY;
			mDeleteLayout.setLayoutParams(params);
			mDeleteLayout.setVisibility(VISIBLE);

			mDeleteBgLayout.setVisibility(VISIBLE);
			mDeleteBgLayout.setAlpha(0);
			mDeleteBgLayout.animate().alpha(1).setDuration(100);

			int h;
			if (mIsExpand) {
				h = ShareData.m_screenHeight - mSelectPanelHeight2;
			} else {
				h = ShareData.m_screenHeight - mSelectPanelHeight;
			}
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = (int)((h - mDeleteButton.getHeight()) / 2f);
			mDeleteButton.setLayoutParams(params);

			mDeleteButton.setVisibility(VISIBLE);
			mDeleteButton.setAlpha(0);
			mDeleteButton.animate().alpha(1).setDuration(100);

			mHeightDistance = (loc[1] - deltaY) - ((h - mDeleteButton.getHeight()) / 2f + ShareData.PxToDpi_xhdpi(190));

			view.setVisibility(INVISIBLE);
		}
	};

	private void onDelete() {
		mIsDelete = false;
		DecorateGroupRes decorateGroupRes = mGroupRes.get(mDeletePosition);
		ThemeRes themeRes = ThemeResMgr2.getInstance().GetRes(decorateGroupRes.m_id);
		SparseArray<DecorateRes> sdcardArr = DecorateResMgr2.getInstance().sync_GetSdcardRes(getContext(), null);
		if(sdcardArr != null)
		{
			sdcardArr = sdcardArr.clone();
		}
		if (ResourceUtils.DeleteItems(sdcardArr, themeRes.m_decorateIDArr).isEmpty()) {
			T.showShort(mContext, R.string.decorate_can_not_delete);
		} else {
			mSelectPanel.deleteItem(mDeletePosition);
			GroupRes groupRes = new GroupRes();
			groupRes.m_themeRes = themeRes;
			groupRes.m_ress = new ArrayList<>();
			groupRes.m_ress.addAll(decorateGroupRes.m_group);

			DecorateResMgr2.getInstance().DeleteGroupRes(mContext, groupRes);
		}

		if (mDoingAnimation) {
			return;
		}
		mDoingAnimation = true;
		mDeletedAnimator = new AnimatorSet();
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(mDeleteBgLayout, "alpha", 1, 0);
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mDeleteButton, "alpha", 1, 0);
		mDeletedAnimator.play(alpha1).with(alpha2);
		mDeletedAnimator.setDuration(300);
		mDeletedAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
				mDeleteBgLayout.setVisibility(INVISIBLE);
				mDeleteButton.setVisibility(INVISIBLE);
				if (mDeleteView != null) {
					mDeleteView.setVisibility(VISIBLE);
					mDeleteView = null;
				}
			}
		});

		mDeleteLayout.setVisibility(INVISIBLE);
		mDeleteLayout.setTranslationX(0);
		mDeleteLayout.setTranslationY(0);

		mDeleteButton.setProgress(0);

		mDeletedAnimator.start();
	}

	private void onDeleteCancel() {
		mIsDelete = false;

		if (mDoingAnimation) {
			return;
		}
		mDoingAnimation = true;
		initAnimations();
		mDeleteCancelAnimator.start();
	}

	private void initAnimations() {

		AnimatorSet set1 = new AnimatorSet();
		AnimatorSet set2 = new AnimatorSet();
		mDeleteCancelAnimator = new AnimatorSet();

		ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(mDeleteButton, "alpha", 1, 0);
		ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mDeleteBgLayout, "alpha", 1, 0);
		ObjectAnimator translationX = ObjectAnimator.ofFloat(mDeleteLayout, "translationX", mDeleteLayout.getTranslationX(), 0);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(mDeleteLayout, "translationY", mDeleteLayout.getTranslationY(), 0);

		long duration = (long)((Math.abs(mDeleteLayout.getTranslationY()) / mHeightDistance) * 300);
		if (duration > 300) {
			duration = 300;
		}
		set1.play(alphaAnimator1).with(alphaAnimator2);
		set1.setDuration(100);
		set2.play(translationX).with(translationY);
		set2.setDuration(duration);

		mDeleteCancelAnimator.play(set2).with(mDeleteButton.getCloseAnimator()).before(set1);
		mDeleteCancelAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
				mDeleteLayout.setVisibility(INVISIBLE);
				mDeleteView.setVisibility(VISIBLE);
				mDeleteView = null;
				mDeleteBgLayout.setVisibility(INVISIBLE);
				mDeleteButton.setVisibility(INVISIBLE);
			}
		});
	}

	private CloudAlbumDialog mExitDialog;

	private void showExitDialog()
	{
		if (mExitDialog == null)
		{
			mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
			mExitDialog.setCancelable(true).setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener()
			{
				@Override
				public void onOkButtonClick()
				{
					if (mExitDialog != null)
					{
						mExitDialog.dismiss();
					}
					onCancel();
				}

				@Override
				public void onCancelButtonClick()
				{
					if (mExitDialog != null)
					{
						mExitDialog.dismiss();
					}
				}
			});
		}
		mExitDialog.show();
	}

	private void clearExitDialog()
	{
		if (mExitDialog != null)
		{
			mExitDialog.dismiss();
			mExitDialog.setListener(null);
			mExitDialog = null;
		}
	}
}
