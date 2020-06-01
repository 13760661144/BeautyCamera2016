package cn.poco.brush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.acne.view.UndoPanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.brush.site.BrushPageSite;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.credits.Credit;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.graffiti.GraffitiViewV2;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushRes;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.GroupRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.UndoRedoDataMgr;
import cn.poco.transitions.TweenLite;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.widget.recycle.RecommendDragContainer;
import cn.poco.widget.recycle.RecommendExAdapter;
import cn.poco.widget.recycle.RecommendExConfig;
import my.beautyCamera.R;

/**
 * 指尖魔法
 *
 * @since v4.0
 */
public class BrushPage extends IPage
{
	private static final String TAG = "指尖魔法";

	private BrushPageSite mSite;
	private boolean mUiEnabled;
	private boolean mQuit;

	private RotationImg2 mImgs;
	private Bitmap mOrgBmp;
	private Bitmap mBkBmp;

	private int mBrushUri;
	private BrushRes mBrushRes;

	private int mFrW;
	private int mFrH;
	private int mBottomLayoutHeight;
	private int mBottomBarHeight;

	private BrushViewV2 mView;

	private FrameLayout mBottomFr;
    private MyStatusButton mCenterBtn;

	private ImageView mBrushClearBtn;
	private ImageView mCancelBtn;
	private ImageView mOkBtn;

	private RecommendDragContainer mRecycleViewFr;
	private RecommendExConfig mListConfig;
	private RecommendExAdapter mRecommendAdapter;
	private ArrayList<RecommendExAdapter.ItemInfo> mListInfoArr;

	private RecomDisplayMgr mRecomView;
	private UndoRedoDataMgr mBrushData;

	private final static int MSG_SAVE_CACHE = 0x011;
	private HandlerThread mHandlerThread;
	private Handler mUIHandler;
	private Handler mImageHandler;
	private UndoPanel mUndoCtrl;
	private WaitAnimDialog m_waitDlg;

	private int DEF_IMG_SIZE;

	private boolean isFold = false;
	private boolean mChange = false;

	//ui anim
	private float m_currImgH = 0f;
	private int m_imgH = 0;
	private int m_viewH = 0;
	private int m_viewTopMargin;
	private static final int SHOW_VIEW_ANIM = 300;

	private ArrayList<Integer> mBrushUseTongjiId;

	public BrushPage(Context context, BaseSite site)
	{
		super(context, site);

		mSite = (BrushPageSite)site;
		InitData();
		InitUI();

		MyBeautyStat.onPageStartByRes(R.string.美颜美图_指尖魔法页面_主页面);
		TongJiUtils.onPageStart(getContext(), R.string.指尖魔法);
	}

	protected void InitData()
	{
		mBrushUseTongjiId = new ArrayList<>();
		mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);
		mBottomBarHeight = ShareData.PxToDpi_xhdpi(88);
		mFrW = ShareData.m_screenWidth;
		mFrH = ShareData.m_screenHeight - mBottomBarHeight - mBottomLayoutHeight;

		// 获取图片的最大边长
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mListConfig = new RecommendExConfig();

		mUiEnabled = false;

		mBrushData = new UndoRedoDataMgr(10, true, new UndoRedoDataMgr.Callback()
		{
			@Override
			public void DeleteData(Object data)
			{
				File file = new File((String)data);
				if(file != null && file.exists())
				{
					file.delete();
				}
			}
		});

		if(DownloadMgr.getInstance() != null)
		{
			DownloadMgr.getInstance().AddDownloadListener(mDownloadLst);
		}

		mHandlerThread = new HandlerThread("brush_handler_thread");
		mHandlerThread.start();
		mImageHandler = new Handler(mHandlerThread.getLooper())
		{
			@Override
			public void dispatchMessage(Message msg)
			{
				switch(msg.what)
				{
					case MSG_SAVE_CACHE:
					{

						if(msg.obj != null && msg.obj instanceof Bitmap)
						{
							String path = FileCacheMgr.GetLinePath();
							if(Utils.SaveTempImg((Bitmap)msg.obj, path))
							{
								mBrushData.AddData(path);
							}
						}

						if(mUIHandler != null)
						{
							Message uimsg = mUIHandler.obtainMessage();
							uimsg.what = MSG_SAVE_CACHE;
							mUIHandler.sendMessage(uimsg);
						}
						break;
					}
					default:
						break;
				}
			}
		};
		mUIHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case MSG_SAVE_CACHE:
					{
						mUiEnabled = true;
						if(mView != null)
						{
							mView.SetUIEnabled(true);
							updateUndoRedoBtnState();
						}
						break;
					}
					default:
						break;
				}
			}
		};
	}

	protected void InitUI()
	{
		LayoutParams fl;
		mView = new BrushViewV2(getContext(), mFrW + 2, mFrH + 2, mCtrlInterface4);
		fl = new LayoutParams(mFrW + 2, mFrH + 2);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		this.addView(mView, 0, fl);

		mBottomFr = new FrameLayout(getContext());
		fl = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.addView(mBottomFr, fl);
		{
			//撤销 重做 清空
            FrameLayout mEditBtnLayout = new FrameLayout(getContext());
			mEditBtnLayout.setBackgroundColor(Color.TRANSPARENT);
			mEditBtnLayout.setPadding(ShareData.PxToDpi_xhdpi(24), 0, ShareData.PxToDpi_xhdpi(24), ShareData.PxToDpi_xhdpi(24));
			fl = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			fl.bottomMargin = mBottomBarHeight + mBottomLayoutHeight;
			mEditBtnLayout.setLayoutParams(fl);
			mBottomFr.addView(mEditBtnLayout);
			{
				mUndoCtrl = new UndoPanel(getContext());
				mUndoCtrl.setCallback(mUndoCB);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				mUndoCtrl.setLayoutParams(fl);
				mUndoCtrl.setVisibility(INVISIBLE);
				mEditBtnLayout.addView(mUndoCtrl);

				mBrushClearBtn = new ImageView(getContext());
				mBrushClearBtn.setBackgroundResource(R.drawable.beautify_white_circle_bg);
				mBrushClearBtn.setImageResource(R.drawable.advanced_beautify_brush_clear);
				mBrushClearBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				mBrushClearBtn.setVisibility(INVISIBLE);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
				mBrushClearBtn.setLayoutParams(fl);
				mEditBtnLayout.addView(mBrushClearBtn);
				mBrushClearBtn.setOnTouchListener(new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						if(mUiEnabled)
						{
							MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_清空按钮);
							TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法_清空按钮);
							showClearDialog();
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
				});
			}

            FrameLayout mBottomBar = new FrameLayout(getContext());
			mBottomBar.setBackgroundColor(0xe6ffffff);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBottomBarHeight); //88px
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			fl.bottomMargin = mBottomLayoutHeight;
			mBottomBar.setLayoutParams(fl);
			mBottomFr.addView(mBottomBar);
			{
				mCancelBtn = new ImageView(getContext());
				mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				mCancelBtn.setImageResource(R.drawable.beautify_cancel);
				mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				mCancelBtn.setLayoutParams(fl);
				mBottomBar.addView(mCancelBtn);
				mCancelBtn.setOnTouchListener(mOnAnimationClickListener);

				mOkBtn = new ImageView(getContext());
				mOkBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				mOkBtn.setImageResource(R.drawable.beautify_ok);
				mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				ImageUtils.AddSkin(getContext(), mOkBtn);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
				mOkBtn.setLayoutParams(fl);
				mBottomBar.addView(mOkBtn);
				mOkBtn.setOnTouchListener(mOnAnimationClickListener);

				mCenterBtn = new MyStatusButton(getContext());
				mCenterBtn.setData(R.drawable.brush_icon, getContext().getString(R.string.brushpage_brush));
				mCenterBtn.setBtnStatus(true, false);
				mCenterBtn.setLineWidth(ShareData.PxToDpi_xhdpi(188));
				mCenterBtn.setOnClickListener(mOnClickListener);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.CENTER;
				mCenterBtn.setLayoutParams(fl);
				mBottomBar.addView(mCenterBtn);
			}

			//素材区域
			mListInfoArr = BrushResMgr.GetBrushRes(getContext());
			mRecommendAdapter = new RecommendExAdapter(mListConfig);
			mRecommendAdapter.SetData(mListInfoArr);
			mRecommendAdapter.setOnItemClickListener(m_onItemClickListener);
			mRecommendAdapter.setOnDragCallBack(mOnDragCallBack);
			mRecycleViewFr = new RecommendDragContainer(getContext(), mRecommendAdapter);

			fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			mBottomFr.addView(mRecycleViewFr, fl);
		}
		m_waitDlg = new WaitAnimDialog((Activity)getContext());

		mRecomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback()
		{
			@Override
			public void UnlockSuccess(BaseRes res)
			{

			}

			@Override
			public void OnCloseBtn()
			{

			}

			@Override
			public void OnBtn(int state)
			{

			}

			@Override
			public void OnClose()
			{

			}

			@Override
			public void OnLogin()
			{
				if(mSite != null)
				{
					mSite.OnLogin(getContext());
				}
			}
		});
		mRecomView.Create(this);
	}


	/**
	 * 没用的参数不必传进来，使用时先判断参数是否为null
	 *
	 * @param params imgs : RotationImg[] / Bitmap
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		boolean hasSelect = false;
		if(params != null)
		{
			Object o;

			o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
			if(o != null && o instanceof Integer)
			{
				m_imgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if(o != null && o instanceof Integer)
			{
				m_viewH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
			if(o != null && o instanceof Integer)
			{
				m_viewTopMargin = (int)o;
			}

			if(params.get("imgs") != null)
			{
				setImgs(params.get("imgs"));
			}


			o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
			if(o != null && o instanceof Integer)
			{
				mBrushUri = (int)params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
				if(mBrushUri != 0)
				{
					int tempUri = mBrushUri;
					mBrushUri = 0;
					setSelUri(tempUri);
					hasSelect = true;
				}
			}
		}
		if(!hasSelect)
		{
			mRecommendAdapter.SetSelectByUri(BrushResMgr.GetFirstUri(mListInfoArr), false, false,true);
		}
	}

	private void setSelUri(final int uri)
	{
		if(uri != RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && uri != RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
		{
			mRecommendAdapter.SetSelectByUri(uri);
		}
	}

	private void setImgs(Object params)
	{
		SetWaitUI(true, getContext().getString(R.string.brushpage_loading_img));

		Bitmap orgBmp = null;
		if(params instanceof RotationImg2[])
		{
			mImgs = ((RotationImg2[])params)[0];
			orgBmp = ImageUtils.MakeBmp(getContext(), params, DEF_IMG_SIZE, DEF_IMG_SIZE);
		}
		else if(params instanceof Bitmap)
		{
			orgBmp = (Bitmap)params;
		}

		if(orgBmp != null)
		{
			Canvas canvas = new Canvas(orgBmp);
			canvas.drawColor(0xFFFFFFFF, PorterDuff.Mode.DST_ATOP);
		}

		if(orgBmp != null && !orgBmp.isRecycled())
		{
			SetBmp(orgBmp);
		}
	}

	protected OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(mUiEnabled)
			{
				if(v == mCenterBtn)
				{
					isFold = !isFold;
					SetViewState(isFold);
					SetBottomFrState(isFold);
					mCenterBtn.setBtnStatus(true, isFold);
					MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_指尖魔法页面_主页面_收回bar : R.string.美颜美图_指尖魔法页面_主页面_展开bar);

				}
			}
		}
	};

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(mUiEnabled)
			{
				if(v == mCancelBtn)
				{
					if(mRecomView != null && mRecomView.IsShow())
					{
						mRecomView.OnCancel(true);
						return;
					}
					MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_取消);
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法_取消);
					if(mChange)
					{
						showExitDialog();
					}
					else
					{
						cancel();
					}
				}
				else if(v == mOkBtn)
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_确认);
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法_确认);
					String param = Credit.APP_ID + Credit.BRUSH + mBrushUri;
					Credit.CreditIncome(param, getContext(), R.integer.积分_首次使用新素材);
					if (mBrushUseTongjiId != null && mBrushUseTongjiId.size() > 0)
					{
						int size = mBrushUseTongjiId.size();
						String[] tongjis = new String[size];
						for (int i = 0; i < size; i++)
						{
							tongjis[i] = String.valueOf(mBrushUseTongjiId.get(i));
						}
						MyBeautyStat.onUseBrush(tongjis);
					}
					if(mSite != null)
					{
						mUiEnabled = false;

						if(mView != null)
						{
							HashMap<String, Object> params = new HashMap<>();
							params.put("img", mView.m_img.m_bmp);
							params.putAll(getBackAnimParam());
							mSite.OnSave(getContext(), params);
						}
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


	private void cancel()
	{
		mUiEnabled = false;
		//清理
		if(mView != null)
		{
			BrushPage.this.removeView(mView);
			if(mView.m_img != null && !mView.m_img.m_bmp.isRecycled())
			{
				mView.m_img.m_bmp.recycle();
				mView.m_img.m_bmp = null;
			}
		}
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mOrgBmp);
		params.putAll(getBackAnimParam());
		mSite.OnBack(getContext(), params);
	}

	private HashMap<String, Object> getBackAnimParam()
	{
		HashMap<String, Object> params = new HashMap<>();
		float imgHeight = mView.m_img.m_h * mView.m_img.m_scaleY;
		float viewHeight = ShareData.m_screenHeight - mBottomBarHeight - mBottomLayoutHeight;
		float topMargin = (mView.getHeight() - viewHeight) / 2f;
		params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, topMargin);
		params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, imgHeight);
		return params;
	}


	private CloudAlbumDialog mClearHintDialog;

	private void showClearDialog()
	{
		if(mClearHintDialog == null)
		{
			mClearHintDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mClearHintDialog.getOkButtonBg());
			mClearHintDialog.setCancelButtonText(R.string.no).setOkButtonText(R.string.yes).setMessage(R.string.is_it_emptied).setListener(new CloudAlbumDialog.OnButtonClickListener()
			{
				@Override
				public void onOkButtonClick()
				{
					mClearHintDialog.dismiss();
					if(mOrgBmp != null && mView != null && mView.m_img != null && mView.m_img.m_bmp != null)
					{
						mCtrlInterface4.DrawStart(mView.m_img.m_bmp);
						Canvas canvas = new Canvas(mView.m_img.m_bmp);
						canvas.drawColor(0, PorterDuff.Mode.CLEAR);
						canvas.drawBitmap(mOrgBmp, new Matrix(), null);
						mView.invalidate();

						if(mImageHandler != null)
						{
							Message msg = mImageHandler.obtainMessage();
							msg.what = MSG_SAVE_CACHE;
							msg.obj = mOrgBmp;
							mImageHandler.sendMessage(msg);
						}
					}
				}

				@Override
				public void onCancelButtonClick()
				{
					mClearHintDialog.dismiss();
				}
			});
		}
		mClearHintDialog.show();
	}

	private void clearHideDialog()
	{
		if (mClearHintDialog != null)
		{
			mClearHintDialog.dismiss();
			mClearHintDialog.setListener(null);
			mClearHintDialog = null;
		}
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
					cancel();
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

	protected void SetBottomFrState(boolean isFold)
	{
		mBottomFr.clearAnimation();
		int start;
		int end;
		start = isFold ? 0 : mBottomLayoutHeight;
		end = isFold ? mBottomLayoutHeight : 0;
		ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
		object.setDuration(300);
		object.setInterpolator(new AccelerateDecelerateInterpolator());
		object.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				mUiEnabled = false;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				mUiEnabled = true;
			}
		});
		object.start();
	}

	protected TweenLite m_tween = new TweenLite();

	private void SetViewState(boolean isFold)
	{
		int mFrH = ShareData.m_screenHeight - mBottomBarHeight;
		int mFrH2 = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
		int start;
		int end;
		if(isFold)
		{
			start = mFrH2;
			end = mFrH;
		}
		else
		{
			start = mFrH;
			end = mFrH2;
		}
		m_tween.Init(start, end, 300);
		m_tween.M1Start(TweenLite.EASING_LINEAR | TweenLite.EASE_OUT);
		UpdateViewHeight();
	}

	protected void UpdateViewHeight()
	{
		if(mView != null && m_tween != null)
		{
			LayoutParams fl = new LayoutParams(mFrW + 2, (int)m_tween.M1GetPos());
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
			mView.setLayoutParams(fl);
			if(!m_tween.M1IsFinish())
			{
				this.postDelayed(mViewAnimRunnable, 1);
			}
		}
	}

	private Runnable mViewAnimRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			UpdateViewHeight();
		}
	};

	protected UndoPanel.Callback mUndoCB = new UndoPanel.Callback()
	{
		@Override
		public void onUndo()
		{
			if(mUiEnabled && mUndoCtrl.isCanUndo())
			{
				MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_撤销);
				Object obj = mBrushData.Undo();
				if(obj != null && mView != null)
				{
					if(mView.m_img.m_bmp != null)
					{
						mView.m_img.m_bmp.recycle();
						mView.m_img.m_bmp = null;
					}
					mView.UpdateImg(cn.poco.imagecore.Utils.DecodeImage(getContext(), obj, 0, -1, -1, -1));
					mView.invalidate();
					updateUndoRedoBtnState();
				}
			}
		}

		@Override
		public void onRedo()
		{
			if(mUiEnabled && mUndoCtrl.isCanRedo())
			{
				MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_重做);
				Object obj = mBrushData.Redo();
				if(obj != null && mView != null)
				{
					if(mView.m_img.m_bmp != null)
					{
						mView.m_img.m_bmp.recycle();
						mView.m_img.m_bmp = null;
					}
					mView.UpdateImg(cn.poco.imagecore.Utils.DecodeImage(getContext(), obj, 0, -1, -1, -1));
					mView.invalidate();
					updateUndoRedoBtnState();
				}
			}
		}
	};

	private void updateUndoRedoBtnState()
	{
		mUndoCtrl.setCanUndo(mBrushData.CanUndo());
		mUndoCtrl.setCanRedo(mBrushData.CanRedo());
	}

	//protected Bitmap temp_bmp;
	protected GraffitiViewV2.Callback mCtrlInterface4 = new GraffitiViewV2.Callback()
	{

		@Override
		public void DrawStart(Bitmap bmp)
		{
			showEditLayoutAnim(false);
			mChange = true;
		}

		@Override
		public void DrawFinish(Bitmap bmp)
		{
			if(mBrushRes != null)
			{
				TongJi2.AddCountById(mBrushRes.m_tjId + "");
			}
			showEditLayoutAnim(true);
			if(bmp != null)
			{
				mUiEnabled = false;
				mView.SetUIEnabled(false);

				if(mImageHandler != null)
				{
					Message msg = mImageHandler.obtainMessage();
					msg.what = MSG_SAVE_CACHE;
					msg.obj = bmp;
					mImageHandler.sendMessage(msg);
				}
			}
		}
	};

	private void showEditLayoutAnim(boolean isShow)
	{
		if(isShow)
		{
			if(mUndoCtrl != null) mUndoCtrl.show();
			showBrushClearAnim(true);
		}
		else
		{
			if(mUndoCtrl != null) mUndoCtrl.hide();
			showBrushClearAnim(false);
		}
	}

	private void showBrushClearAnim(boolean show)
	{
		mBrushClearBtn.animate().cancel();

		if(mBrushClearBtn.getVisibility() == INVISIBLE && show)
		{
			mBrushClearBtn.setScaleX(0);
			mBrushClearBtn.setScaleY(0);
			mBrushClearBtn.setVisibility(VISIBLE);
			mBrushClearBtn.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
		}
		else
		{
			mBrushClearBtn.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					mBrushClearBtn.setVisibility(INVISIBLE);
				}
			});
		}
	}

	private AbsDragAdapter.OnDragCallBack mOnDragCallBack = new AbsDragAdapter.OnDragCallBack() {
		@Override
		public void onItemDelete(AbsDragAdapter.ItemInfo info, int position) {
			RecommendExAdapter.ItemInfo itemInfo = (RecommendExAdapter.ItemInfo) info;
			ThemeRes themeRes = ThemeResMgr2.getInstance().GetRes(itemInfo.m_uri);
			GroupRes res = new GroupRes();
			res.m_themeRes = themeRes;
			res.m_ress = new ArrayList<>();
			for (int i = 1; i < itemInfo.m_uris.length; i++) {
				res.m_ress.add(cn.poco.resource.BrushResMgr2.getInstance().GetRes(itemInfo.m_uris[i]));
			}
			cn.poco.resource.BrushResMgr2.getInstance().DeleteGroupRes(getContext(),res);

			((RecommendExAdapter.DownloadItemInfo)mListInfoArr.get(0)).setNum(cn.poco.resource.BrushResMgr2.getInstance().GetNoDownloadCount(getContext()));
			mRecommendAdapter.notifyItemChanged(0);
			RecommendExAdapter.RecommendItemInfo recommendInfo = BrushResMgr.getRecommendInfo(getContext());
			int recommendIndex = mRecommendAdapter.GetIndex((RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI));
			if(recommendInfo != null && recommendIndex >= 0) {
				mListInfoArr.set(recommendIndex, recommendInfo);
				mRecommendAdapter.notifyItemChanged(recommendIndex);
			}
		}

		@Override
		public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition) {
			AbsAdapter.ItemInfo itemInfo = mListInfoArr.get(fromPosition);
			int fromPos = fromPosition;
			int toPos = toPosition;
			if (itemInfo != null) {
				fromPos = ResourceUtils.HasId(BrushResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			itemInfo = mListInfoArr.get(toPosition);
			if (itemInfo != null) {
				toPos = ResourceUtils.HasId(BrushResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			if(fromPos >=0 && toPos >=0){
				ResourceUtils.ChangeOrderPosition(BrushResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
				BrushResMgr2.getInstance().SaveOrderArr();
			}
		}

		@Override
		public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position) {
			int out = ResourceUtils.HasItem(ThemeResMgr2.getInstance().sync_GetLocalRes(getContext(), null),itemInfo.m_uri);
			if(out >= 0){
				mRecycleViewFr.setCanDelete(false);
			}else{
				mRecycleViewFr.setCanDelete(true);
			}
		}

		@Override
		public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position) {

		}

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position) {

        }
    };

	private RecommendExAdapter.OnItemClickListener m_onItemClickListener = new RecommendExAdapter.OnItemClickListener()
	{
		@Override
		public void OnItemDown(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemUp(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemClick(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemDown(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
		}

		@Override
		public void OnItemUp(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
		}

		@Override
		public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
			switch(info.m_uris[0])
			{
				case RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法_下载更多);
					openDownloadMorePage(ResType.BRUSH);
					break;
				case RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法_推荐位);
					OpenRecommendView((ArrayList<RecommendRes>)((RecommendExAdapter.ItemInfo)info).m_ex, ResType.BRUSH.GetValue());
					break;
				default:
					if(subIndex > 0)
					{
						int selUri = ((RecommendExAdapter.ItemInfo)info).m_uris[subIndex];
//				Log.i(TAG, "OnItemClick: "+selUri);
						if(mBrushUri != selUri)
						{
							if(selUri == 0)
							{
							}
							else
							{
								BrushRes brushRes = cn.poco.resource.BrushResMgr2.getInstance().GetRes(selUri);
								if(brushRes != null && brushRes.m_res != null && mOrgBmp != null)
								{
									int len = brushRes.m_res.length;
									Object[] res = brushRes.m_res;
									Bitmap[] temp = new Bitmap[len];
									for(int i = 0; i < len; i++)
									{
										temp[i] = cn.poco.imagecore.Utils.DecodeImage(getContext(), res[i], 0, -1, -1, -1);
									}
									float resScale = (mOrgBmp.getWidth() > mOrgBmp.getHeight() ? mOrgBmp.getWidth() : mOrgBmp.getHeight()) / 1024f;
									if(mView != null)
									{
										mView.SetMasks(temp, resScale);
										mView.SetRotation(brushRes.m_ra, brushRes.m_rb);
										mView.SetScale(brushRes.m_sa, brushRes.m_sb);
										mView.SetDensity(((brushRes.m_db - brushRes.m_da) * 0.8f + brushRes.m_da) * resScale);
									}
									mBrushRes = brushRes;
								}
							}
							mBrushUri = selUri;
							if (mBrushUseTongjiId != null)
							{
								mBrushUseTongjiId.add(mBrushRes != null ? mBrushRes.m_tjId : 0);
							}
						}
					}
					else
					{
						Toast.makeText(getContext(), "需要下载", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	};

	protected void OpenRecommendView(ArrayList<RecommendRes> ress, int resType)
	{
		MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_指尖魔法推荐位);
		//推荐位
		RecommendRes recommendRes = null;
		if(ress != null && ress.size() > 0)
		{
			recommendRes = ress.get(0);
		}
		if(mRecomView != null && recommendRes != null)
		{
			mRecomView.SetBk(CommonUtils.GetScreenBmp((Activity)getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
			mRecomView.SetDatas(recommendRes, resType);
			mRecomView.Show();
		}
	}

	private void openDownloadMorePage(ResType resType)
	{
		MyBeautyStat.onClickByRes(R.string.美颜美图_指尖魔法页面_主页面_下载更多);
		if (mSite != null) mSite.OpenDownloadMore(getContext(), resType);
	}

	public DownloadMgr.DownloadListener mDownloadLst = new AbsDownloadMgr.DownloadListener()
	{
		@Override
		public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
		{
			if(resArr != null && ((BaseRes)resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
			{
				if(resType == ResType.BRUSH.GetValue())
				{
					ArrayList<RecommendExAdapter.ItemInfo> dst = BrushResMgr.GetBrushRes(getContext());
					if(dst.size() > mListInfoArr.size())
					{
						mRecommendAdapter.notifyItemDownLoad(dst.size() - mListInfoArr.size());
					}
					mListInfoArr = dst;
					mRecommendAdapter.SetData(mListInfoArr);
					mRecommendAdapter.notifyDataSetChanged();
				}
			}
		}
	};


	@Override
	public void onClose()
	{
		mUiEnabled = false;
		mQuit = true;

		if(mHandlerThread != null)
		{
			mHandlerThread.quit();
			mHandlerThread = null;
		}

		if(mImageHandler != null)
		{
			mImageHandler.removeMessages(MSG_SAVE_CACHE);
			mImageHandler = null;
		}

		if(mUIHandler != null)
		{
			mUIHandler.removeMessages(MSG_SAVE_CACHE);
			mUIHandler = null;
		}

		if(mView != null)
		{
			this.removeView(mView);
			mView.ClearAll();
			mView = null;
		}

		clearHideDialog();
		clearExitDialog();

		if(DownloadMgr.getInstance() != null)
		{
			DownloadMgr.getInstance().RemoveDownloadListener(mDownloadLst);
		}

		if(mBkBmp != null && !mBkBmp.isRecycled())
		{
			mBkBmp.recycle();
		}

		if (mBrushUseTongjiId != null)
		{
			mBrushUseTongjiId.clear();
			mBrushUseTongjiId = null;
		}

		MyBeautyStat.onPageEndByRes(R.string.美颜美图_指尖魔法页面_主页面);
		TongJiUtils.onPageEnd(getContext(), R.string.指尖魔法);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.指尖魔法);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.指尖魔法);
		super.onResume();
	}

	protected void SetWaitUI(boolean flag, String str)
	{
		if(flag)
		{
			if(str == null)
			{
				str = "";
			}

			if(m_waitDlg != null)
			{
				m_waitDlg.show();
			}
		}
		else
		{
			if(m_waitDlg != null)
			{
				m_waitDlg.hide();
			}
		}
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(!mUiEnabled || mQuit)
		{
			return true;
		}
		else
		{
			return super.onInterceptTouchEvent(ev);
		}
	}

	public void SetBmp(Bitmap bmp)
	{
		mOrgBmp = bmp;

		mView.SetImg(mOrgBmp.copy(Bitmap.Config.ARGB_8888, true));

		ShowStartAnim();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mBrushData.ClearAll();
				String path = FileCacheMgr.GetLinePath();
				if(Utils.SaveTempImg(mOrgBmp, path))
				{
					mBrushData.AddData(path);
				}
			}
		}).start();

		SetWaitUI(false, "");

		mUiEnabled = true;
	}

	private void ShowStartAnim()
	{
		if(m_viewH > 0 && m_imgH > 0)
		{
			int tempStartY = (int)(ShareData.PxToDpi_xhdpi(90) + (m_viewH - mFrH) / 2f);
			float scaleX = (float)mFrW / (float)mOrgBmp.getWidth();
			float scaleY = (float)mFrH / (float)mOrgBmp.getHeight();
			m_currImgH = mOrgBmp.getHeight() * Math.min(scaleX, scaleY);
			float scaleH = m_imgH / m_currImgH;
			ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
		}
	}

	private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
	{
		if(view != null)
		{
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
			ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
			ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
			ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
			animatorSet.setDuration(duration);
			animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
			animatorSet.playTogether(object1, object2, object3, object4);
			animatorSet.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					mUiEnabled = true;
					view.clearAnimation();
					mBottomFr.clearAnimation();
				}
			});
			animatorSet.start();
		}
	}


	@Override
	public void onBack()
	{
		if(mOnAnimationClickListener != null)
		{
			mOnAnimationClickListener.onAnimationClick(mCancelBtn);
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		if(siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO)
		{
			if(params != null)
			{
				boolean isDelete = false;
				if(params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL) != null)
				{
					isDelete = (boolean)params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
					if(isDelete)
					{
						mListInfoArr = BrushResMgr.GetBrushRes((Activity)getContext());
						mRecommendAdapter.CancelSelect();
						mRecommendAdapter.SetData(mListInfoArr);
						mRecommendAdapter.notifyDataSetChanged();
						mView.SetMasks(null, 0);
					}
				}

				if(params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null)
				{
					int uri = (int)params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
					if(isDelete && uri == 0)
					{
                        //删除素材并且没有指定素材后，沿用原素材id
                        uri = mBrushUri;
                        mBrushUri = 0;
					}

					if(uri == 0)
					{
						uri = mBrushUri;
						mBrushUri = 0;
					}
					mRecommendAdapter.SetSelectByUri(uri);
				}
			}
		}
		else if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL)
		{
			if(mRecomView != null)
			{
				mRecomView.UpdateCredit();
			}
		}
	}
}
