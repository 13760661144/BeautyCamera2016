package cn.poco.album;

import android.content.Context;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import cn.poco.album.frame.BaseFrame;
import cn.poco.album.frame.CloudFrame;
import cn.poco.album.frame.RepeatFrame;
import cn.poco.album.frame.SingleFrame;
import cn.poco.album.site.AlbumSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/21
 */
public class AlbumPage extends IPage {

	/**
	 * 单选
	 */
	public static final int SINGLE = 0x1;

	/**
	 * 重复选择
	 */
	public static final int REPEAT = 0x2;

	/**
	 * 云相册
	 */
	public static final int CLOUD = 0x3;

	private Context mContext;
	private AlbumSite mSite;

	private int mMin = 1;
	private int mMax = 20;

	private int mMode = SINGLE;

	private BaseFrame mFrame;

	/**
	 * 单选使用
	 */
	private boolean mRestore = false;
	private boolean mFromCamera = false;
	private int mColorFilterId = 0;


	/**
	 * 重复选使用
	 */
	private List<String> mSelectedList;
	private boolean mRepeatTip = false;

	/**
	 * 云相册使用
	 */
	private long mFreeVolume;
	private String mAlbumName;

	public AlbumPage(Context context, BaseSite site) {
		super(context, site);
		TongJiUtils.onPageStart(context, R.string.选图);
		MyBeautyStat.onPageStartByRes(R.string.选相册_选相册_主页面);

		mContext = context;
		mSite = (AlbumSite)site;

		init();
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(mContext, R.string.选图);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(mContext, R.string.选图);
	}

	private void init() {
		PhotoStore.getInstance(mContext).clearCache();
	}

	/**
	 * 设置数据
	 * @param params 传入参数
	 *               mode: int 模式，默认BEAUTIFY
	 *				 min: int 最少选图的张数
	 * 	    		 max: int 最多选图的张数
	 * 				 默认是单选
	 * 				 restore: boolean 是否恢复到上一下浏览位置
	 * 				 from_camera: boolean 是否从镜头跳转过来或者隐藏右上角相机按钮
	 * 				 album_name: String 文件夹名字，云相册选图
	 * 				 repeat_tip: boolean 重复选择时是否提示，默认false(不提示)
	 * 				 imgInfo: List<String> 已经选中的图片
	 * 				 free_volume: long 云相册剩余空间
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void SetData(HashMap<String, Object> params) {

		if (params != null) {
			int min = -1, max = -1;

			Object o = params.get("min");
			if (o != null && o instanceof Integer) {
				min = (Integer) o;
				if (min < 0) {
					min = 0;
				}
			}

			o = params.get("max");
			if (o != null && o instanceof Integer) {
				max = (Integer) o;
				if (max < 0) {
					max = 20;
				}
			}

			o = params.get("mode");
			if (o != null && o instanceof Integer) {
				mMode = (Integer) o;
			}

			o = params.get("restore");
			if (o != null && o instanceof Boolean) {
				mRestore = (Boolean) o;
			} else {
				mRestore = false;
			}

			o = params.get("from_camera");
			if (o != null && o instanceof Boolean) {
				mFromCamera = (Boolean) o;
			} else {
				mFromCamera = false;
			}

			if(mFromCamera) {
				o = params.get(DataKey.COLOR_FILTER_ID);
				if(o != null && o instanceof Integer) {
					mColorFilterId = (int) o;
				} else {
					mColorFilterId = 0;
				}
			}else{
				o = params.get("hide_camera_entry");
				if (o != null && o instanceof Boolean) {
					mFromCamera = (Boolean) o;
				}
			}


			o = params.get("album_name");
			if (o != null && o instanceof String) {
				mAlbumName = (String) o;
			}

			o = params.get("repeat_tip");
			if (o != null && o instanceof Boolean) {
				mRepeatTip = (Boolean) o;
			}

			o = params.get("imgInfo");
			if (o != null && o instanceof List) {
				try {
					mSelectedList = (List<String>) o;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			o = params.get("free_volume");
			if (o != null && o instanceof Long) {
				mFreeVolume = (Long) o;
			}

			mMin = min;
			mMax = max;
			if (mMin == -1) {
				mMin = 1;
			}
			if (mMax == -1) {
				mMax = 20;
			}
		} else {
			mRestore = false;
			mFromCamera = false;
		}

		initFrame();
	}

	private void initFrame() {
		switch (mMode) {
			case SINGLE:
				mFrame = new SingleFrame(mContext, mSite, mRestore, mFromCamera);
				((SingleFrame)mFrame).SetColorFilterId(mColorFilterId);
				break;
			case REPEAT:
				mFrame = new RepeatFrame(mContext, mSite, mMin, mMax, mRepeatTip);
				((RepeatFrame)mFrame).setSelectedList(mSelectedList);
				break;
			case CLOUD:
				mFrame = new CloudFrame(mContext, mSite, mAlbumName, mFreeVolume, mMin, mMax);
				break;
		}

		removeAllViews();

		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
										ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mFrame, params);
	}

	@Override
	public void onBack() {
		if (mFrame != null)
		{
			mFrame.onBack();
		}
	}

	public boolean onPressBack() {
		return mFrame.onBack();
	}

	@Override
	public void onClose() {
		// 清除内存缓存
		Glide.get(mContext).clearMemory();
		Glide.get(mContext).getBitmapPool().clearMemory();

		if (mFrame != null) {
			mFrame.onClose();
		}

		TongJiUtils.onPageEnd(mContext, R.string.选图);
		MyBeautyStat.onPageEndByRes(R.string.选相册_选相册_主页面);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		mFrame.onPageResult(siteID, params);
	}

	public boolean isScrollToTop()
	{
		return mFrame.isScrollToTop();
	}

	public void changeSkin() {
		mFrame.changeSkin();
	}

	public void notifyUpdate() {
		mFrame.notifyUpdate(PhotoStore.sLastFolderIndex);
	}
}
