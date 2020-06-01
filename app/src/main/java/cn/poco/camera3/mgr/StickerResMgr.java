package cn.poco.camera3.mgr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.animation.DecelerateInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import cn.poco.camera.StickerSPConfig;
import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.cb.sticker.StickerPagerViewHelper;
import cn.poco.camera3.config.CameraStickerConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.InfoBuilder;
import cn.poco.camera3.info.StickerDownloadAnim;
import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.dynamicSticker.ShowType;
import cn.poco.framework.MyFramework2App;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.VideoStickerGroupRes;
import cn.poco.resource.VideoStickerGroupResMgr2;
import cn.poco.resource.VideoStickerRes;
import cn.poco.resource.VideoStickerResMgr2;
import my.beautyCamera.R;

/**
 * 贴纸 + 标签 管理
 * Created by Gxx on 2017/10/25.
 */

public class StickerResMgr
{
	private boolean mIsLoadBuildIn;
	private volatile boolean mHasFindGoToLabelID;

	public boolean isLoadBuildIn()
	{
		return mIsLoadBuildIn;
	}

	@Retention(RetentionPolicy.SOURCE)
	public @interface SelectedInfoKey
	{
		int STICKER = 1 << 2; //
		int AUTO_DOWN_LOAD = 1 << 3;
		int LABEL = 1 << 4;
	}

	@Retention(RetentionPolicy.SOURCE)
	@interface Msg
	{
		int LOAD_DATA_SUCCEED = 1 << 5;
		int START_LOAD_DATA = 1 << 6;
	}

	private static StickerResMgr sInstance;
	private Handler mMainHandler;
	private HandlerThread mThread;
	private Handler mThreadHandler;

	private AbsDownloadMgr.Callback m_download_cb;
	private StickerInnerListener mDataHelper;
	private SparseArray<StickerPagerViewHelper> mStickerPagerViewHelperArr;
	private SparseIntArray mDownloadIDArr;
	private ArrayList<StickerInfo> mDownloadInfoArr;

	private volatile boolean mIsBusiness;
	private volatile boolean mCancelLoadData;
	private volatile boolean mLoadDataSucceed;
	private volatile boolean mCancelDownload;
	private boolean mShowGrayProgressBK;
	private boolean mIsShowStickerSelector;
	private boolean mRememberUseStickerID;
	private volatile int mJustGoToLabelID;

	// key --> value: "sticker" --> id; "shape" --> id; "label" --> id
	private volatile SparseIntArray mSelectedArr;

	private ArrayList<LabelInfo> mAllLabelsArr; // 全部标签数据
	private ArrayList<StickerInfo> mAllStickersArr; // 全部贴纸数据
	private ArrayList<StickerInfo> mAllBuildInStickersArr; //全部内置贴纸数据

	private int mCurrentType;
	private float mPreviewRatio;
	private int[] mSpecificLabelArr;

	private static boolean mInitConfig;

	private StickerResMgr()
	{
	}

	public static boolean sInstanceIsNull()
	{
		return sInstance == null;
	}

	public static boolean isInitConfig()
	{
		return mInitConfig;
	}

	public synchronized static StickerResMgr getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new StickerResMgr();
		}
		return sInstance;
	}

	public void unregisterStickerResMgrCB(StickerPagerViewHelper helper)
	{
		if(mStickerPagerViewHelperArr == null || helper == null) return;
		mStickerPagerViewHelperArr.delete(helper.getIndex());
	}

	public void registerStickerResMgrCB(StickerPagerViewHelper helper)
	{
		if(mStickerPagerViewHelperArr == null || helper == null) return;
		mStickerPagerViewHelperArr.put(helper.getIndex(), helper);
	}

	public void setInitDataCB(StickerInnerListener cb)
	{
		mDataHelper = cb;
	}

	public void init(final Context context)
	{
		initArr();

		initBuildInRes(context);

		mMainHandler = new Handler(Looper.getMainLooper())
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case Msg.LOAD_DATA_SUCCEED:
					{
						if (msg.obj != null)
						{
							SparseArray params = (SparseArray) msg.obj;
							ArrayList<StickerInfo> sticker = (ArrayList<StickerInfo>) params.get(0);
							ArrayList<LabelInfo> label = (ArrayList<LabelInfo>) params.get(1);

							if (!sInstanceIsNull())
							{
								mAllStickersArr = sticker;
								mAllLabelsArr = label;
								mLoadDataSucceed = true;
								notifyLoadDataSucceed();
							}
						}
						if (mThread != null)
						{
							mThread.quit();
							mThread = null;
						}
						if (mThreadHandler != null)
						{
							mThreadHandler = null;
						}
						break;
					}
				}
			}
		};

		mThread = new HandlerThread("load_data_thread");
		mThread.start();

		mThreadHandler = new Handler(mThread.getLooper(), new Handler.Callback()
		{
			@Override
			public boolean handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case Msg.START_LOAD_DATA:
					{
						if (msg.obj != null && msg.obj instanceof HashMap)
						{
							HashMap<String, Object> params = (HashMap<String, Object>) msg.obj;
							if (params.containsKey("label"))
							{
								ArrayList<LabelInfo> label_list = (ArrayList<LabelInfo>) params.get("label");
								initAllSticker(context, label_list);
							}
						}
						break;
					}
				}
				return true;
			}
		});

		m_download_cb = new AbsDownloadMgr.Callback()
		{
			@Override
			public void OnProgress(int downloadId, IDownload res, int progress)
			{
				if(res != null && res instanceof VideoStickerRes)
				{
					VideoStickerRes stickerRes = (VideoStickerRes)res;
					StickerInfo info = getStickerInfoByID(stickerRes.m_id);
					if(info != null && info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
					{
						info.mDownloadStatus = StatusMgr.DownloadStatus.ING;
						info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.START;
						startDownloadAnim(info, 0, 96, 15000);
					}
				}
			}

			@Override
			public void OnComplete(int downloadId, IDownload res)
			{
				if(mDownloadIDArr != null)
				{
					mDownloadIDArr.delete(downloadId);
				}

				if(res != null && res instanceof VideoStickerRes)
				{
					VideoStickerRes stickerRes = (VideoStickerRes)res;
					StickerInfo info = getStickerInfoByID(stickerRes.m_id);
					if(info != null)
					{
						if (mDownloadInfoArr != null)
						{
							mDownloadInfoArr.remove(info);
						}
						info.mDownloadStatus = StatusMgr.DownloadStatus.SUCCEED;
						if(info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.END || info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
						{
							info.mProgress = -1;
							info.mStatus = StatusMgr.Type.LOCAL;
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
							if(isNeedNotify(info))
							{
								for(int index : info.mLabelIndexList)
								{
									StickerPagerViewHelper helper = getStickerPagerViewHelper(index);
									if(helper != null)
									{
										helper.OnComplete(info.id);
									}
								}
							}
						}
					}
				}
			}

			@Override
			public void OnFail(int downloadId, IDownload res)
			{
				if(mDownloadIDArr != null)
				{
					mDownloadIDArr.delete(downloadId);
				}

				if(res != null && res instanceof VideoStickerRes)
				{
					VideoStickerRes stickerRes = (VideoStickerRes)res;
					StickerInfo info = getStickerInfoByID(stickerRes.m_id);
					if(info != null)
					{
						if (mDownloadInfoArr != null)
						{
							mDownloadInfoArr.remove(info);
						}
						info.mDownloadStatus = StatusMgr.DownloadStatus.FAILED;
						if(info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.END  || info.mDownloadAnimStatus == StatusMgr.DownloadAnimStatus.IN_IDLE)
						{
							info.mProgress = -1;
							info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
							if(isNeedNotify(info))
							{
								for(int index : info.mLabelIndexList)
								{
									StickerPagerViewHelper helper = getStickerPagerViewHelper(index);
									if(helper != null)
									{
										helper.OnFail(info.id);
									}
								}
							}
						}
					}
				}
			}
		};
	}

	private boolean isNeedNotify(StickerInfo info)
	{
		return (info.mShowType.equals(ShowType.BOTH)) || (info.mShowType.equals(ShowType.GIF) && isGIF()) || (info.mShowType.equals(ShowType.STICKER) && !isGIF());
	}

	private ValueAnimator.AnimatorUpdateListener mDownLoadAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener()
	{
		@Override
		public void onAnimationUpdate(ValueAnimator animation)
		{
			if(animation instanceof StickerDownloadAnim)
			{
				StickerInfo info = (StickerInfo)((StickerDownloadAnim)animation).getRes();

				if(mCancelDownload && info != null)
				{
					info.mAnim.setRes(null);
					info.mAnim.removeAllListeners();
					info.mAnim.removeAllUpdateListeners();
					info.mAnim.cancel();
					info.mAnim = null;
					return;
				}

				if(info != null)
				{
					float value = (float)animation.getAnimatedValue();

					switch(info.mDownloadStatus)
					{
						case StatusMgr.DownloadStatus.SUCCEED:
						{
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mAnim.setRes(null);
							info.mAnim.removeAllListeners();
							info.mAnim.removeAllUpdateListeners();
							info.mAnim.cancel();
							info.mAnim = null;
							startDownloadAnim(info, value, 100, (long)((100 - value) * (1500f / 100)));
							return;
						}

						case StatusMgr.DownloadStatus.FAILED:
						{
							info.mProgress = -1;
							info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;

							info.mAnim.setRes(null);
							info.mAnim.removeAllListeners();
							info.mAnim.removeAllUpdateListeners();
							animation.cancel();
							info.mAnim = null;
							break;
						}

						default:
						{
							info.mProgress = value;
							break;
						}
					}

					if(isNeedNotify(info))
					{
						for(int index : info.mLabelIndexList)
						{
							StickerPagerViewHelper helper = getStickerPagerViewHelper(index);
							if(helper != null)
							{
								helper.OnProgress(info.id);
							}
						}
					}
				}
			}
		}
	};

	private AnimatorListenerAdapter mDownLoadAnimListener = new AnimatorListenerAdapter()
	{
		@Override
		public void onAnimationEnd(Animator animation)
		{
			if(animation != null && animation instanceof StickerDownloadAnim)
			{
				StickerInfo info = (StickerInfo)((StickerDownloadAnim)animation).getRes();

				if(mCancelDownload && info != null)
				{
					info.mAnim.setRes(null);
					info.mAnim.removeAllUpdateListeners();
					info.mAnim.removeAllListeners();
					info.mAnim.cancel();
					info.mAnim = null;
					return;
				}

				if(info != null)
				{
					info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.END;
					info.mAnim.removeAllUpdateListeners();
					info.mAnim.removeAllListeners();
					info.mAnim.setRes(null);
					info.mAnim = null;

					switch(info.mDownloadStatus)
					{
						case StatusMgr.DownloadStatus.IN_IDLE:
						case StatusMgr.DownloadStatus.SUCCEED:
						{
							info.mProgress = -1;
							info.mStatus = StatusMgr.Type.LOCAL;
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
							break;
						}

						case StatusMgr.DownloadStatus.FAILED:
						{
							info.mProgress = -1;
							info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
							info.mDownloadStatus = StatusMgr.DownloadStatus.IN_IDLE;
							info.mDownloadAnimStatus = StatusMgr.DownloadAnimStatus.IN_IDLE;
							break;
						}
					}

					if(isNeedNotify(info))
					{
						for(int index : info.mLabelIndexList)
						{
							StickerPagerViewHelper helper = getStickerPagerViewHelper(index);
							if(helper != null)
							{
								helper.OnComplete(info.id);
							}
						}
					}
				}
			}
		}
	};

	private void startDownloadAnim(StickerInfo info, float start, float end, long duration)
	{
		if(info != null)
		{
			info.mAnim = new StickerDownloadAnim();
			info.mAnim.setRes(info);
			info.mAnim.setDuration(duration);
			info.mAnim.setFloatValues(start, end);
			info.mAnim.setInterpolator(new DecelerateInterpolator());

			info.mAnim.addListener(mDownLoadAnimListener);
			info.mAnim.addUpdateListener(mDownLoadAnimUpdateListener);
			info.mAnim.start();
		}
	}

	public void DownloadRes(StickerInfo info)
	{
		if(info != null)
		{
			int download_id = DownloadMgr.getInstance().DownloadRes((IDownload)info.mRes, getDownLoadCB());
			if(mDownloadIDArr != null)
			{
				mDownloadIDArr.put(download_id, download_id);
			}
			if(mDownloadInfoArr != null)
			{
				mDownloadInfoArr.add(info);
			}
		}
	}

	public void CancelDownload()
	{
		if(mDownloadInfoArr != null)
		{
			Iterator<StickerInfo> iterator = mDownloadInfoArr.iterator();
			while (iterator.hasNext())
			{
				StickerInfo info = iterator.next();
				if (info != null)
				{
					info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
					info.mProgress = -1;
					notifyPagerViewDataChange(info);
					iterator.remove();
				}
			}
		}

		if(mDownloadIDArr != null)
		{
			int size = mDownloadIDArr.size();
			for(int index = 0; index < size; index++)
			{
				DownloadMgr.getInstance().CancelDownload(mDownloadIDArr.valueAt(index));
			}
		}
	}

	private AbsDownloadMgr.Callback getDownLoadCB()
	{
		return m_download_cb;
	}

	private StickerPagerViewHelper getStickerPagerViewHelper(int label_index)
	{
		if(mStickerPagerViewHelperArr != null)
		{
			return mStickerPagerViewHelperArr.get(label_index);
		}
		return null;
	}

	private void initArr()
	{
		if(mDownloadInfoArr == null)
		{
			mDownloadInfoArr = new ArrayList<>();
		}

		if(mSelectedArr == null)
		{
			mSelectedArr = new SparseIntArray();
		}

		if(mStickerPagerViewHelperArr == null)
		{
			mStickerPagerViewHelperArr = new SparseArray<>();
		}

		if(mAllLabelsArr == null)
		{
			mAllLabelsArr = new ArrayList<>();
		}

		if(mAllStickersArr == null)
		{
			mAllStickersArr = new ArrayList<>();
		}

		if(mDownloadIDArr == null)
		{
			mDownloadIDArr = new SparseIntArray();
		}
	}

	public void notifyReflashAllData()
	{
		if(mDataHelper != null)
		{
			mDataHelper.onRefreshAllData();
		}
	}

	public void notifyCanLoadData()
	{
		if(mDataHelper != null)
		{
			mDataHelper.onCanLoadRes();
		}
	}

	private void notifyLoadDataSucceed()
	{
		if(mDataHelper != null)
		{
			mDataHelper.onLoadStickerDataSucceed();
		}
	}

	private void notifyLabelScrollToSelected()
	{
		if(mDataHelper != null)
		{
			mDataHelper.onLabelScrollToSelected(getSelectedLabelIndex());
		}
	}

	/**
	 * 修改数据之后，一定要调用通知邻近的 StickerPagerView 刷新 ui
	 */
	public void notifyPagerViewDataChange(StickerInfo... info_list)
	{
		if(info_list != null)
		{
			for(StickerInfo info : info_list)
			{
				if(info != null)
				{
					for(int index : info.mLabelIndexList)
					{
						StickerPagerViewHelper helper = getStickerPagerViewHelper(index);
						if(helper != null)
						{
							helper.onDataChange(info.id);
						}
					}
				}
			}
		}
	}

	public void notifyShutterTabChange()
	{
		resetStickerSelectedStatus();

		if(mDataHelper != null)
		{
			mDataHelper.onShutterTabChange();
		}
	}

	public boolean isHadSelected(int new_info_id)
	{
		int last_sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);

		return new_info_id == last_sticker_selected_id;
	}

	/**
	 * 修改之前 素材 的选中状态
	 *
	 * @return true 修改成功
	 */
	public boolean modifyPreviousSelected(StickerInfo new_info)
	{
		int last_sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);

		StickerInfo last_sticker_selected_info = getStickerInfoByID(last_sticker_selected_id);

		if(last_sticker_selected_info != null)
		{
			last_sticker_selected_info.mIsSelected = false;
			notifyPagerViewDataChange(last_sticker_selected_info);
		}

		notifyLabelScrollToSelected();
		updateSelectedInfo(SelectedInfoKey.STICKER, new_info.id);// 更新选中的素材 id

		new_info.mIsSelected = true;
		notifyPagerViewDataChange(new_info);
		return true;
	}

	private void resetStickerSelectedStatus()
	{
		if(mSelectedArr == null) return;

		StickerInfo sticker_info = getStickerInfoByID(mSelectedArr.get(SelectedInfoKey.STICKER));
		LabelInfo last_label_info = getLabelInfoByID(mSelectedArr.get(SelectedInfoKey.LABEL));

		if(sticker_info != null)
		{
			int label_index = -1;

			if(!sticker_info.mShowType.equals(ShowType.BOTH)) //找不到对应的素材
			{
				sticker_info.mIsSelected = false;
				updateSelectedInfo(SelectedInfoKey.STICKER, -1);
			}

			if(sticker_info.mLabelIndexList != null && sticker_info.mLabelIndexList.size() > 0)
			{
				label_index = sticker_info.mLabelIndexList.get(0);
			}
			// 标签
			resetLabelSelectedStatus(last_label_info, label_index == -1 ? 0 : label_index);
		}
	}

	private void resetLabelSelectedStatus(LabelInfo last_label_info, int need_selected_index)
	{
		if(last_label_info != null && last_label_info.mIndex != need_selected_index)
		{
			last_label_info.isSelected = false;

			LabelInfo label_info = getLabelInfoByIndex(need_selected_index);
			if(label_info != null)
			{
				label_info.isSelected = true;
				updateSelectedInfo(SelectedInfoKey.LABEL, label_info.ID);
			}
		}
	}

	public void setStickerConfig(CameraStickerConfig config)
	{
		if(config != null)
		{
			boolean isTailorModeSetting = config.isTailorMadeSetting();
			mIsBusiness = config.isBusiness();
			mSpecificLabelArr = config.getSpecificLabelArr();
			mCurrentType = config.getShutterType();
			mPreviewRatio = config.getPreviewRatio();
			mIsShowStickerSelector = config.isShowStickerSelector();
			mRememberUseStickerID = config.isAutoRememberUseStickerID();
			mJustGoToLabelID = config.getJustGoToLabelID();

			if(mSelectedArr != null)
			{
				mSelectedArr.put(SelectedInfoKey.STICKER, isTailorModeSetting ? -1 : config.getSelectedStickerID());
			}
			mInitConfig = true;
		}
		notifyCanLoadData();
	}

	public void setShutterType(int type)
	{
		mCurrentType = type;
	}

	public int getShutterType()
	{
		return mCurrentType;
	}

	public void setPreviewRatio(float ratio)
	{
		mPreviewRatio = ratio;
		if(mDataHelper != null)
		{
			mDataHelper.onUpdateUIByRatio(mPreviewRatio);
		}
	}

	public void showGrayProgressBK(boolean show)
	{
		mShowGrayProgressBK = show;
	}

	public boolean isShowGrayProgressBK()
	{
		return mShowGrayProgressBK;
	}

	public boolean isGIF()
	{
		return mCurrentType == ShutterConfig.TabType.GIF;
	}

	public boolean isInInitDataJustGotoLabel()
	{
		return mJustGoToLabelID > -1;
	}

	/**
	 * 初步判断是否有指定标签、素材(一般内部判断)
	 */
	public boolean isSpecificLabel()
	{
		return mSpecificLabelArr != null && mSpecificLabelArr.length > 0;
	}

	/**
	 * 通过 load 标签、素材后，真正判断是否有指定标签、素材(一般外部调用)
	 */
	public boolean isSpecific()
	{
		return isSpecificLabel() && mSpecificLabelArr.length == getLabelArrValidSize();
	}

	public boolean isShowStickerSelector()
	{
		return mIsShowStickerSelector;
	}

	public boolean isBusiness()
	{
		return mIsBusiness;
	}

	public int getLabelArrValidSize()
	{
		return mAllLabelsArr != null ? mAllLabelsArr.size() : 0;
	}

	public LabelInfo getLabelInfoByIndex(int label_index)
	{
		if(mAllLabelsArr != null && mAllLabelsArr.size() > 0 && label_index >= 0 && label_index < mAllLabelsArr.size())
		{
			return mAllLabelsArr.get(label_index);
		}
		return null;
	}

	private LabelInfo getLabelInfoByID(int label_id)
	{
		if(mAllLabelsArr != null)
		{
			for(LabelInfo info : mAllLabelsArr)
			{
				if(info != null && info.ID == label_id)
				{
					return info;
				}
			}
		}
		return null;
	}

	public int getStickerInfoIndexInPagerView(int sticker_ID, int label_index)
	{
		LabelInfo label_info = getLabelInfoByIndex(label_index);
		if(label_info != null)
		{
			return getStickerInfoIndexInPagerView(sticker_ID, isSpecificLabel() ? label_info.mSpareStickerArr : isGIF() ? label_info.mGIFStickerArr : label_info.mStickerArr);
		}
		return -1;
	}

	private int getStickerInfoIndexInPagerView(int sticker_ID, ArrayList<StickerInfo> list)
	{
		if(list != null)
		{
			int size = list.size();
			for(int index = 0; index < size; index++)
			{
				StickerInfo info = list.get(index);
				if(info != null && info.id == sticker_ID)
				{
					return index;
				}
			}
		}
		return -1;
	}

	public StickerInfo getStickerInfoByID(int sticker_id)
	{
		if(mAllStickersArr != null)
		{
			for(StickerInfo info : mAllStickersArr)
			{
				if(info.id == sticker_id)
				{
					return info;
				}
			}
		}
		return null;
	}

	public ArrayList<StickerInfo> getStickerInfoArr(int label_index, int... ids)
	{
		return isBusiness() ? getBusinessStickerArrByIDs(ids) : getStickerInfoArrByIndex(label_index);
	}

	/**
	 * 通过指定id get 商业贴纸素材
	 *
	 * @param ids 指定的贴纸素材id
	 * @return 商业贴纸素材
	 */
	private ArrayList<StickerInfo> getBusinessStickerArrByIDs(int... ids)
	{
		if(mLoadDataSucceed)
		{
			if(mAllStickersArr != null)
			{
				if(ids == null || ids.length == 0) return mAllStickersArr;

				for(StickerInfo info : mAllStickersArr)
				{
					if(info != null)
					{
						boolean isFit = false;
						for(int id : ids)
						{
							if(info.id == id) isFit = true;
						}
						if(!isFit) mAllStickersArr.remove(info);
					}
				}
				return mAllStickersArr;
			}
		}

		return InfoBuilder.BuildEmptyStickerInfoList(ids);
	}

	private ArrayList<StickerInfo> getStickerInfoArrByIndex(int label_index)
	{
		if(mLoadDataSucceed)
		{
			LabelInfo label_info = getLabelInfoByIndex(label_index);

			if(label_info != null)
			{
				return isSpecific() ? label_info.mSpareStickerArr : isGIF() ? label_info.mGIFStickerArr : label_info.mStickerArr;
			}
		}

		LabelInfo info = getLabelInfoByIndex(label_index);
		return InfoBuilder.BuildEmptyStickerInfoList(info);
	}

	private void initBuildInRes(Context context)
	{
		if(mAllBuildInStickersArr == null)
		{
			mAllBuildInStickersArr = new ArrayList<>();
		}

		ArrayList<VideoStickerRes> local_list = VideoStickerResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplicationContext(), null);

		for(VideoStickerRes res : local_list)
		{
			StickerInfo local_info = InfoBuilder.BuildStickerInfo(context, res, true);
			mAllBuildInStickersArr.add(local_info);
		}
	}

	private StickerInfo isBuildInRes(int res_id)
	{
		if(mAllBuildInStickersArr != null)
		{
			for(StickerInfo info : mAllBuildInStickersArr)
			{
				if(info.id == res_id)
				{
					return info;
				}
			}
		}
		return null;
	}

	/**
	 * @return 贴纸素材标签
	 */
	public ArrayList<LabelInfo> getLabelInfoArr(final Context context, boolean hasMgrLogo)
	{
		if(mAllLabelsArr != null && mAllLabelsArr.size() > 0)
		{
			ArrayList<LabelInfo> out = new ArrayList<>(mAllLabelsArr);

			if(hasMgrLogo)
			{
				LabelInfo info = new LabelInfo();
				info.mIndex = mAllLabelsArr.size();
				info.mType = TypeMgr.StickerLabelType.MANAGER;//管理标签
				info.mLabelName = context.getString(R.string.sticker_pager_manager);

				out.add(info);
			}

			return out;
		}


		// 商业通道
		if(isBusiness())
		{
			LabelInfo info = new LabelInfo();
			info.mIndex = 0;
			info.mLabelName = "商业";
			info.isSelected = true;
			mAllLabelsArr.add(info);

			// 初始化商业贴纸数据
			sendMsgToLoadData();

			return mAllLabelsArr;
		}

		// 非商业
		ArrayList<VideoStickerGroupRes> source = VideoStickerGroupResMgr2.getInstance().getCloudDownloadRes(context, false, mSpecificLabelArr);

		if(source != null && source.size() > 0)
		{
			initAllLabelInfo(context, source);

			// 初始化非商业贴纸数据
			sendMsgToLoadData();
		}
		else
		{
			// 内置
			initLocalLabel(context);
		}

		ArrayList<LabelInfo> out = new ArrayList<>(mAllLabelsArr);

		if(hasMgrLogo)
		{
			LabelInfo info = new LabelInfo();
			info.mIndex = mAllLabelsArr.size();
			info.mType = TypeMgr.StickerLabelType.MANAGER;//管理标签
			info.mLabelName = context.getString(R.string.sticker_pager_manager);

			out.add(info);
		}

		if(mAllBuildInStickersArr != null)
		{
			mAllBuildInStickersArr.clear();
			mAllBuildInStickersArr = null;
		}

		return out;
	}

	private void sendMsgLoadSucceed(ArrayList<StickerInfo> sticker_arr, ArrayList<LabelInfo> label_arr)
	{
		if (mMainHandler != null)
		{
			Message msg = new Message();
			SparseArray<Object> params = new SparseArray<>();
			params.put(0, sticker_arr);
			params.put(1, label_arr);
			msg.what = Msg.LOAD_DATA_SUCCEED;
			msg.obj = params;
			mMainHandler.sendMessage(msg);
		}
	}

	private void sendMsgToLoadData()
	{
		if (mThreadHandler != null)
		{
			if (mAllLabelsArr != null)
			{
				ArrayList<LabelInfo> arr = new ArrayList<>();
				for (LabelInfo info : mAllLabelsArr)
				{
					if (info != null)
					{
						LabelInfo newInfo = new LabelInfo();
						newInfo.set(info);
						arr.add(newInfo);
					}
				}
				HashMap<String, Object> params = new HashMap<>();
				params.put("label", arr);
				Message msg = mThreadHandler.obtainMessage();
				msg.what = Msg.START_LOAD_DATA;
				msg.obj = params;
				mThreadHandler.sendMessage(msg);
			}
		}
	}

	private void initAllLabelInfo(Context context, ArrayList<VideoStickerGroupRes> source)
	{
		int size = source.size();
		boolean is_specific_label = isSpecificLabel() && size == mSpecificLabelArr.length;

		for(int index = 0; index < size; index++)
		{
			VideoStickerGroupRes res = source.get(index);
			if(res != null)
			{
				LabelInfo label_info = InfoBuilder.BuildLabelInfo(context, res);
				label_info.mIndex = index;
				if(is_specific_label)
				{
					label_info.mSpareStickerArr = new ArrayList<>();
				}
				else
				{
					label_info.mStickerArr = new ArrayList<>();
					label_info.mGIFStickerArr = new ArrayList<>();
				}
				mAllLabelsArr.add(label_info);
			}
		}

		if(is_specific_label)
		{
			int id = mSpecificLabelArr[0];
			if(mAllLabelsArr != null)
			{
				for(LabelInfo info : mAllLabelsArr)
				{
					if(info != null && info.ID == id)
					{
						info.isSelected = true;
						updateSelectedInfo(SelectedInfoKey.LABEL, info.ID);
						return;
					}
				}
			}
			return;
		}
		else if(mSpecificLabelArr != null)
		{
			updateSelectedInfo(SelectedInfoKey.STICKER, -1);
		}

		int sticker_selected_id = mSelectedArr.get(SelectedInfoKey.STICKER);

		for(LabelInfo label_info : mAllLabelsArr)
		{
			if(sticker_selected_id == -2)//首次安装
			{
				if(label_info.mStickerIDArr != null)
				{
					for(int id : label_info.mStickerIDArr)
					{
						StickerInfo sticker_info = isBuildInRes(id);
						if(sticker_info != null)
						{
							label_info.isSelected = true;
							updateSelectedInfo(SelectedInfoKey.LABEL, label_info.ID);
							updateSelectedInfo(SelectedInfoKey.STICKER, id);
							return;
						}
					}
				}
			}
			else if(sticker_selected_id == -1) // 没有任何一款素材被选中
			{
				if (mJustGoToLabelID != -1)
				{
					if (label_info.ID == mJustGoToLabelID)
					{
						mHasFindGoToLabelID = true;
						label_info.isSelected = true;
						updateSelectedInfo(SelectedInfoKey.LABEL, label_info.ID);
						return;
					}
				}
				else if (label_info.mIndex == 0)
				{
					label_info.isSelected = true;
					updateSelectedInfo(SelectedInfoKey.LABEL, label_info.ID);
					return;
				}
			}
			else // 有素材被选中
			{
				if(label_info.mStickerIDArr != null)
				{
					for(int id : label_info.mStickerIDArr)
					{
						if(sticker_selected_id == id)
						{
							label_info.isSelected = true;
							updateSelectedInfo(SelectedInfoKey.LABEL, label_info.ID);
							return;
						}
					}
				}
			}
		}

		// 如果都没匹配到，就默认选中第一个 lab
		if(!mHasFindGoToLabelID && mAllLabelsArr != null && mAllLabelsArr.size() > 0)
		{
			LabelInfo info = mAllLabelsArr.get(0);
			if(info != null)
			{
				info.isSelected = true;
				updateSelectedInfo(SelectedInfoKey.LABEL, info.ID);
			}
		}
	}

	private void initLocalLabel(Context context)
	{
		mIsLoadBuildIn = true;
		LabelInfo label_info = new LabelInfo();
		label_info.mType = TypeMgr.StickerLabelType.HOT;
		label_info.ID = LabelInfo.BUILT_IN_LABEL_HOT_ID;
		label_info.isSelected = true;
		label_info.mLabelName = "HOT";
		label_info.mIndex = 0;
		label_info.mStickerArr = new ArrayList<>();
		label_info.mGIFStickerArr = new ArrayList<>();

		mSelectedArr.put(SelectedInfoKey.LABEL, LabelInfo.BUILT_IN_LABEL_HOT_ID);

		mAllLabelsArr.add(label_info);

		ArrayList<VideoStickerRes> list = VideoStickerResMgr2.getInstance().GetAllLocalRes(isBusiness());

		for(VideoStickerRes res : list)
		{
			if(res != null)
			{
				StickerInfo build_in_info = InfoBuilder.BuildStickerInfo(context, res, true);
				build_in_info.mLabelIndexList.add(label_info.mIndex);

				mAllStickersArr.add(build_in_info);

				int size = mAllLabelsArr.size();

				for(int index = 0; index < size; index++)
				{
					label_info = mAllLabelsArr.get(index);

					switch(build_in_info.mShowType)
					{
						case ShowType.GIF:
						{
							label_info.mGIFStickerArr.add(build_in_info);
							break;
						}

						case ShowType.STICKER:
						{
							label_info.mStickerArr.add(build_in_info);
							break;
						}

						default:
						case ShowType.BOTH:
						{
							label_info.mStickerArr.add(build_in_info);
							label_info.mGIFStickerArr.add(build_in_info);
							break;
						}
					}
				}
			}
		}

		int sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);
		// 初始化素材选中信息
		if(sticker_selected_id == -2) // 首次安装
		{
			for(StickerInfo info : mAllStickersArr)
			{
				if(info != null)
				{
					if(info.mStatus == StatusMgr.Type.LOCAL || info.mStatus == StatusMgr.Type.BUILT_IN)
					{
						info.mIsSelected = true;
						updateSelectedInfo(SelectedInfoKey.STICKER, info.id);
						break;
					}
				}
			}
		}
		else if(sticker_selected_id > -1) // 有素材被选中
		{
			for(StickerInfo info : mAllStickersArr)
			{
				if(info != null)
				{
					if(info.id == sticker_selected_id)
					{
						info.mIsSelected = true;
						break;
					}
				}
			}
		}

		mLoadDataSucceed = true;
	}

	private void initAllSticker(Context context, ArrayList<LabelInfo> labelArr)
	{
		if(checkQuitThread() || labelArr == null || labelArr.size() < 1) return;

		ArrayList<StickerInfo> out = new ArrayList<>();

		ArrayList<VideoStickerRes> resList = VideoStickerResMgr2.getInstance().GetResArr(context, mIsBusiness);

		if(resList != null)
		{
			for(VideoStickerRes res : resList)
			{
				if(checkQuitThread()) return;

				StickerInfo info = InfoBuilder.BuildStickerInfo(context, res, false);

				if(info != null)
				{
					out.add(info);
				}
			}

			if(mIsBusiness)
			{
				sendMsgLoadSucceed(out, labelArr);
				return;
			}
		}

		boolean is_specific_label = isSpecificLabel() && getLabelArrValidSize() == mSpecificLabelArr.length;

		int label_size = labelArr.size();

		if(!is_specific_label)
		{
			int sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);
			// 初始化素材选中信息
			if(sticker_selected_id == -2) // 首次安装
			{
				if(checkQuitThread()) return;

				int size = out.size();

				for(int i = 0; i < size; i++)
				{
					if(checkQuitThread()) return;

					StickerInfo info = out.get(i);

					if(info != null && (info.mStatus == StatusMgr.Type.LOCAL || info.mStatus == StatusMgr.Type.BUILT_IN))
					{
						info.mIsSelected = true;
						updateSelectedInfo(SelectedInfoKey.STICKER, info.id);
						break;
					}
				}
			}
			else if(sticker_selected_id > -1) // 有素材被选中
			{
				if(checkQuitThread()) return;

				int size = out.size();

				boolean hasFindOut = false;

				for(int i = 0; i < size; i++)
				{
					if(checkQuitThread()) return;
					StickerInfo info = out.get(i);
					if(info != null)
					{
						if(info.id == sticker_selected_id)
						{
							if(info.mStatus == StatusMgr.Type.LOCAL || info.mStatus == StatusMgr.Type.BUILT_IN)
							{
								hasFindOut = true;
								info.mIsSelected = true;
								break;
							}
						}
					}
				}

				if(!hasFindOut)
				{
					updateSelectedInfo(SelectedInfoKey.STICKER, -1);
					LabelInfo label = getLabelInfoByID(getSelectedInfo(SelectedInfoKey.LABEL));
					if(label != null)
					{
						label.isSelected = false;
					}

					label = labelArr.get(0);

					if(label != null)
					{
						label.isSelected = true;
						updateSelectedInfo(SelectedInfoKey.LABEL, label.ID);
					}
				}
			}

			for(int index = 0; index < label_size; index++)// 遍历标签
			{
				if(checkQuitThread()) return; // 检查是否不需要继续

				LabelInfo labelInfo = labelArr.get(index);

				if(labelInfo != null)
				{
					int[] ids = labelInfo.mStickerIDArr;
					if(ids != null)
					{
						for(int id : ids)// 遍历每个标签的 id 数组
						{
							if(checkQuitThread()) return;// 检查是否不需要继续

							for(StickerInfo info : out) // 遍历全部贴纸素材
							{
								if(checkQuitThread()) return;

								if(info != null && info.id == id)// 根据贴纸 id 进行匹配
								{
									switch(info.mShowType)
									{
										case ShowType.GIF:
										{
											labelInfo.mGIFStickerArr.add(info);
											break;
										}

										case ShowType.STICKER:
										{
											labelInfo.mStickerArr.add(info);
											break;
										}

										default:
										case ShowType.BOTH:
										{
											labelInfo.mStickerArr.add(info);
											labelInfo.mGIFStickerArr.add(info);
											break;
										}
									}
									info.mLabelIndexList.add(index);
									break;
								}
							}
						}
					}
				}
			}
		}
		else
		{
			// 如果是指定标签 + 素材,先筛选贴纸
			for(int index = 0; index < label_size; index++)// 遍历标签
			{
				if(checkQuitThread()) return; // 检查是否不需要继续

				LabelInfo labelInfo = labelArr.get(index);
				if(labelInfo != null)
				{
					int[] ids = labelInfo.mStickerIDArr;
					if(ids != null)
					{
						for(int id : ids)// 遍历每个标签的 id 数组
						{
							if(checkQuitThread()) return;// 检查是否不需要继续

							for(StickerInfo info : out) // 遍历全部贴纸素材
							{
								if(info != null && info.id == id)// 根据贴纸 id 进行匹配
								{
									info.mLabelIndexList.add(index);
									labelInfo.mSpareStickerArr.add(info);
									break;
								}
							}
						}
					}
				}
			}

			// 根据指定的贴纸id，找到info
			int sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);

			StickerInfo selected_sticker_info = getStickerInfoByID(sticker_selected_id);

			if(selected_sticker_info != null)
			{
				switch(selected_sticker_info.mStatus)
				{
					case StatusMgr.Type.LIMIT:
					case StatusMgr.Type.LOCK:
					case StatusMgr.Type.NEW:
					case StatusMgr.Type.NEED_DOWN_LOAD:
					{
						// 如果info 需要解锁
						if(selected_sticker_info.mHasLock)
						{
							LockRes lockRes = selected_sticker_info.mLockRes;

							if(lockRes != null)
							{
								if(selected_sticker_info.mLabelIndexList != null && selected_sticker_info.mLabelIndexList.size() > 0)
								{
									int index = selected_sticker_info.mLabelIndexList.get(0);
									LabelInfo label_info = getLabelInfoByIndex(index);
									if(label_info != null && label_info.mSpareStickerArr != null)
									{
										// 先找一次 是否有可用素材
										boolean available = false;
										for(StickerInfo info : label_info.mSpareStickerArr)
										{
											if(info != null && (info.mStatus == StatusMgr.Type.LOCAL || info.mStatus == StatusMgr.Type.BUILT_IN))
											{
												info.mIsSelected = true;
												updateSelectedInfo(SelectedInfoKey.STICKER, info.id);
												available = true;
												break;
											}
										}

										// 第二次 就找可以下载的
										boolean need_down_load = false;
										if(!available)
										{
											for(StickerInfo info : label_info.mSpareStickerArr)
											{
												if(info != null)
												{
													if(!info.mHasLock)
													{
														updateSelectedInfo(SelectedInfoKey.STICKER, -1);
														updateSelectedInfo(SelectedInfoKey.AUTO_DOWN_LOAD, info.id);
														need_down_load = true;
														break;
													}
												}
											}
										}

										// 没可用、没可下载，就无
										if(!available && !need_down_load)
										{
											updateSelectedInfo(SelectedInfoKey.STICKER, -1);
										}
									}
								}
							}
						}
						else
						{
							updateSelectedInfo(SelectedInfoKey.STICKER, -1);
							updateSelectedInfo(SelectedInfoKey.AUTO_DOWN_LOAD, selected_sticker_info.id);
						}
						break;
					}

					case StatusMgr.Type.BUILT_IN:
					case StatusMgr.Type.LOCAL:
					{
						selected_sticker_info.mIsSelected = true;
						break;
					}
				}
			}
			else
			{
				LabelInfo info = getLabelInfoByID(getSelectedInfo(SelectedInfoKey.LABEL));

				if(info == null && labelArr.size() > 0)
				{
					LabelInfo temp = labelArr.get(0);
					if(temp != null)
					{
						info = temp;
						updateSelectedInfo(SelectedInfoKey.LABEL, temp.ID);
					}
				}

				if(info != null && info.mSpareStickerArr != null)
				{
					// 先找一次 是否有可用素材
					boolean available = false;
					for(StickerInfo sticker : info.mSpareStickerArr)
					{
						if(sticker != null && (sticker.mStatus == StatusMgr.Type.LOCAL || sticker.mStatus == StatusMgr.Type.BUILT_IN))
						{
							sticker.mIsSelected = true;
							updateSelectedInfo(SelectedInfoKey.STICKER, sticker.id);
							available = true;
							break;
						}
					}

					// 第二次 就找可以下载的
					boolean need_down_load = false;
					if(!available)
					{
						for(StickerInfo sticker : info.mSpareStickerArr)
						{
							if(sticker != null)
							{
								if(!sticker.mHasLock)
								{
									updateSelectedInfo(SelectedInfoKey.STICKER, -1);
									updateSelectedInfo(SelectedInfoKey.AUTO_DOWN_LOAD, sticker.id);
									need_down_load = true;
									break;
								}
							}
						}
					}

					// 没可用、没可下载，就无
					if(!available && !need_down_load)
					{
						updateSelectedInfo(SelectedInfoKey.STICKER, -1);
					}
				}
			}
		}

		sendMsgLoadSucceed(out, labelArr);
	}

	public void clearAllSelectedInfo()
	{
		if (mSelectedArr == null) return;

		int sticker_id = mSelectedArr.get(SelectedInfoKey.STICKER);

		if(sticker_id == -1)
		{
			return;
		}

		StickerInfo sticker_info = getStickerInfoByID(sticker_id);

		if(sticker_info != null)
		{
			sticker_info.mIsSelected = false;
		}

		updateSelectedInfo(SelectedInfoKey.STICKER, -1);

		notifyPagerViewDataChange(sticker_info);
	}

	public void saveStickerSelectedInfo()
	{
		if(!isSpecificLabel() && mRememberUseStickerID)
		{
			StickerSPConfig.getInstance().setStickerId(getSelectedInfo(SelectedInfoKey.STICKER));
		}
	}

	/**
	 * 更新被选中的信息 <br/>
	 * key --> value: "sticker" --> id; "shape" --> id; "label" --> id
	 */
	public void updateSelectedInfo(@SelectedInfoKey int key, int value)
	{
		if(mSelectedArr != null)
		{
			mSelectedArr.put(key, value);
		}
	}

	/**
	 * 获取被选中的信息
	 *
	 * @return key --> value: "sticker" --> id; "shape" --> id; "label" --> id
	 */
	public int getSelectedInfo(@SelectedInfoKey int key)
	{
		return mSelectedArr != null ? mSelectedArr.get(key) : 0;
	}

	public boolean isRememberUseStickerID()
	{
		return mRememberUseStickerID;
	}

	public StickerInfo getSelectedStickerInfo()
	{
		int sticker_selected_id = getSelectedInfo(SelectedInfoKey.STICKER);

		if(sticker_selected_id == -1)
		{
			return null;
		}
		return getStickerInfoByID(sticker_selected_id);
	}

	public int getSelectedLabelIndex()
	{
		int label_selected_id = getSelectedInfo(SelectedInfoKey.LABEL);
		LabelInfo info = getLabelInfoByID(label_selected_id);
		if(info != null)
		{
			return info.mIndex;
		}
		return -1;
	}

	private void cancelLoadData()
	{
		mCancelLoadData = true;
		mCancelDownload = true;

		if (mThread != null)
		{
			mThread.quit();
			mThread = null;
		}
		if (mThreadHandler != null)
		{
			mThreadHandler.removeMessages(Msg.START_LOAD_DATA);
			mThreadHandler = null;
		}
	}

	private boolean checkQuitThread()
	{
		return mCancelLoadData;
	}

	public void unregisterAllStickerResMgrCB()
	{
		mDataHelper = null;

		if(mStickerPagerViewHelperArr != null)
		{
			mStickerPagerViewHelperArr.clear();
		}
	}

	public void clearMemory()
	{
		m_download_cb = null;

		unregisterAllStickerResMgrCB();
		saveStickerSelectedInfo();
		cancelLoadData();
		CancelDownload();
		clearAllData();

		if(mMainHandler != null)
		{
			mMainHandler.removeMessages(Msg.LOAD_DATA_SUCCEED);
			mMainHandler = null;
		}

		mCancelLoadData = false;

		mLoadDataSucceed = false;

		mInitConfig = false;

		mIsShowStickerSelector = false;

		mIsLoadBuildIn = false;

		mRememberUseStickerID = true;

		sInstance = null;
	}

	/**
	 * 取消下载后才会调用
	 */
	private void clearAllData()
	{
		// selected
		if(mSelectedArr != null)
		{
			mSelectedArr.clear();
			mSelectedArr = null;
		}

		// label
		if(mAllLabelsArr != null)
		{
			ListIterator<LabelInfo> iterator = mAllLabelsArr.listIterator();
			while(iterator.hasNext())
			{
				LabelInfo info = iterator.next();

				if(info != null)
				{
					if(info.mStickerArr != null)
					{
						info.mStickerArr.clear();
						info.mStickerArr = null;
					}

					if(info.mGIFStickerArr != null)
					{
						info.mGIFStickerArr.clear();
						info.mGIFStickerArr = null;
					}

					if(info.mSpareStickerArr != null)
					{
						info.mSpareStickerArr.clear();
						info.mSpareStickerArr = null;
					}
				}
				iterator.remove();
			}
			mAllLabelsArr = null;
		}

		// sticker
		if(mAllStickersArr != null)
		{
			mAllStickersArr.clear();
			mAllStickersArr = null;
		}

		mSpecificLabelArr = null;

		if(mDownloadInfoArr != null)
		{
			mDownloadInfoArr.clear();
			mDownloadInfoArr = null;
		}

		if(mDownloadIDArr != null)
		{
			mDownloadIDArr.clear();
			mDownloadIDArr = null;
		}
	}
}
