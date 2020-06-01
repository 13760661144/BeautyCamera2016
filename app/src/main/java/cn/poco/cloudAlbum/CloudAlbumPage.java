package cn.poco.cloudAlbum;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.cloudAlbum.frame.CloudAlbumBigImgFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumCategoryFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumFolderFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumListFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumMoveFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumSettingFrame;
import cn.poco.cloudAlbum.frame.CloudAlbumTransportFrame;
import cn.poco.cloudAlbum.frame.CreateCloudAlbumFrame;
import cn.poco.cloudAlbum.site.CloudAlbumPageSite;
import cn.poco.cloudalbumlibs.AbsAlbumListFrame;
import cn.poco.cloudalbumlibs.BaseCreateAlbumFrame;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.cloudalbumlibs.controller.CloudAlbumController;
import cn.poco.cloudalbumlibs.controller.NotificationCenter;
import cn.poco.cloudalbumlibs.model.CloudStorageItem;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.cloudalbumlibs.utils.Tags;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.storage.StorageService;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.NetworkMonitor;

/**
 * Created by: fwc
 * Date: 2016/9/13
 */
public class CloudAlbumPage extends IPage implements IAlbumPage {

	private String mUserId;

	private String mAccessToken;

	private IFrame mIFrame;

	private CloudAlbumPageSite mSite;

	private Context mContext;

	private AppInterface mAppInterface;

	/**
	 * 选择本地图片后的回调
	 */
	public UploadCallBack mUploadCallBack;

	private List<FolderInfo> mFolderInfos;

	private TransportInfoReceiver mTransportInfoReceiver;

	private OnMoveCallback mOnMoveCallback;

	private OnCreateAlbumCallback mOnCreateAlbumCallback;

	public CloudStorageItem mCloudStorageItem;


	public CloudAlbumPage(Context context, BaseSite site) {
		super(context, site);

		mContext = context;
		mSite = (CloudAlbumPageSite) site;

		mAppInterface = AppInterface.GetInstance(mContext);
		ShareData.InitData(context);
		NetworkMonitor.getInstance().startMonitor(context);
		TransportImgs.getInstance(getContext()).onListener();
		TransportImgs.getInstance(getContext()).addOnUploadCompleteListener(mUploadComleteListener);

		mTransportInfoReceiver = new TransportInfoReceiver();
		IntentFilter intentFilter = new IntentFilter(StorageService.TRANSPORT_ACTION);
		mContext.registerReceiver(mTransportInfoReceiver, intentFilter);

		String temp = TagMgr.GetTagValue(mContext, Tags.CLOUDALBUM_ISWIFITRANSPORTIMGS);
		boolean isWifi = temp == null || temp.equals("true");
		StorageService.SetOnlyWifi(mContext, isWifi);
	}

	@Override
	public void SetData(HashMap<String, Object> params) {
		mUserId = (String) params.get("id");
		mAccessToken = (String) params.get("token");

		if (TransportImgs.sLoadTransportInfo) {
			TransportImgs.sLoadTransportInfo = false;
			StorageService.GetTransportInfos(mContext);
		}

		TransportImgs.init(mUserId, mAccessToken, Tags.CLOUDALBUM_ISWIFITRANSPORTIMGS);
		openCloudAlbumFolderFrame();
	}

	@Override
	public void onBack() {
		if (null != mIFrame && mIFrame.onBackPress()) {
			//nothing
		} else {
			mSite.OnBack(getContext());
		}
	}

	@Override
	public String getUserId() {
		return mUserId;
	}

	@Override
	public String getAccessToken() {
		return mAccessToken;
	}

	@Override
	public void onFrameBack(View frame) {
		if (frame instanceof IFrame) {
			IFrame currentFrame = (IFrame) frame;
			currentFrame.onClose();
		}
		this.removeView(frame);
		mIFrame = (IFrame) this.getChildAt(this.getChildCount() - 1);
        if (mIFrame instanceof CloudAlbumFolderFrame) {
            CloudAlbumFolderFrame folderFrame = (CloudAlbumFolderFrame)mIFrame;
            folderFrame.hideTipBar();
        }
	}

	@Override
	public void onResume() {
		if (mIFrame != null) {
			mIFrame.onResume();
		}
	}

	@Override
	public void onPause() {
		if (mIFrame != null) {
			mIFrame.onResume();
		}
	}



	@Override
	public void openCloudAlbumListFrame(FolderInfo folderInfo, boolean openSelPhoto) {
		CloudAlbumListFrame frame = new CloudAlbumListFrame(this, folderInfo, mAppInterface);
		mIFrame = frame;
		addView(frame);
		if (openSelPhoto) {
			frame.onUpload();
		}
	}

	@Override
	public void openCloudAlbumEditFrame(FolderInfo folderInfo) {
        CreateCloudAlbumFrame createCloudAlbumFrame = new CreateCloudAlbumFrame(this.getContext(), BaseCreateAlbumFrame.Route.EDIT_ALBUM, folderInfo, this, mAppInterface);
        mIFrame = createCloudAlbumFrame;
        this.addView(createCloudAlbumFrame);
	}

	@Override
	public void openCloudAlbumBigPhotoFrame(AbsAlbumListFrame listFrame, List<PhotoInfo> photoInfos, int position, String albumName) {
		CloudAlbumBigImgFrame frame = new CloudAlbumBigImgFrame(this, listFrame, photoInfos, position, albumName, mAppInterface);
		mIFrame = frame;
		addView(frame);
	}

	@Override
	public void openCloudAlbumMovePhotoFrame(AbsAlbumListFrame listFrame, String folderId, String photoIds, List<String> urls) {
		CloudAlbumMoveFrame frame = new CloudAlbumMoveFrame(this, listFrame, folderId, photoIds, urls);
		mIFrame = frame;
		addView(frame);
	}

	@Override
	public void openSelectAlbumFrame() {
		CloudAlbumMoveFrame frame = new CloudAlbumMoveFrame(this);
		mIFrame = frame;
		addView(frame);
	}

	@Override
	public void openCloudAlbumTransportFrame(boolean download) {
		CloudAlbumTransportFrame frame = new CloudAlbumTransportFrame(this, download);
		mIFrame = frame;
		addView(frame);
	}


	@Override
	public IAlbum getIAlbum() {
		return mAppInterface;
	}

	@Override
	public Context getPageContext() {
		return mContext;
	}

	@Override
	public CloudAlbumPageSite getSite() {
		return mSite;
	}


    // 打开云相册首页
	public void openCloudAlbumFolderFrame() {
        CloudAlbumFolderFrame cloudAlbumFolderFrame = new CloudAlbumFolderFrame(this.getContext(), this, mAppInterface);
        mIFrame = cloudAlbumFolderFrame;
        this.addView(cloudAlbumFolderFrame);
	}

    //  打开创建相册的页面
	@Override
	public void openCreateAlbumFrame(BaseCreateAlbumFrame.Route route) {
        CreateCloudAlbumFrame createCloudAlbumFrame = new CreateCloudAlbumFrame(this.getContext(), route, this, mAppInterface);
        mIFrame = createCloudAlbumFrame;
        this.addView(createCloudAlbumFrame);
	}


    // 打开相册分类页面
	@Override
	public void openAlbumCategoryFrame(FolderInfo folderInfo, BaseCreateAlbumFrame.Route mode) {
        CloudAlbumCategoryFrame cloudAlbumCategoryFrame = new CloudAlbumCategoryFrame(this.getContext(), mode, folderInfo, this, mAppInterface);
        mIFrame = cloudAlbumCategoryFrame;
        this.addView(cloudAlbumCategoryFrame);
	}

    // 打开相册设置页面
    @Override
    public void openCloudAlbumSettingFrame() {
        CloudAlbumSettingFrame cloudAlbumSettingFrame = new CloudAlbumSettingFrame(this.getContext(), this, mAppInterface);
        mIFrame = cloudAlbumSettingFrame;
        this.addView(cloudAlbumSettingFrame);
    }

	@Override
	public void switchToAlbumInnerFrame() {
        for (int a = getChildCount()-1; a >=0; a--) {
            View v = getChildAt(a);
            if (v instanceof CloudAlbumListFrame) {
                break;
            }else{
                ((IFrame)v).onBackPress();
            }
        }
	}

	@Override
	public void updateFolderFrameAfterDeleteAlbum(String folderId) {
		if (mIFrame instanceof CloudAlbumFolderFrame) {
			((CloudAlbumFolderFrame)mIFrame).updateLayoutAfterDeleteAlbum(folderId);
		}
	}


	@Override
	public void updateFolderFrameAfterRenameAlbum(FolderInfo folderInfo) {
        View v = recurseViewsToCheckCoverLayout(this);
        if (v != null) {
            ((CloudAlbumFolderFrame)v).updateAlbumNameAfterRenameAlbum(folderInfo);
        }
	}

	@Override
	public void updateFolderFrameAfterCreateAlbum(FolderInfo folderInfo) {
        View v = recurseViewsToCheckCoverLayout(this);
        if (v != null) {
            ((CloudAlbumFolderFrame)v).updateAlbumFolderFrameAfterCreateAlbum(folderInfo);
        }
	}

	@Override
	public void updateFolderFrame(String folderId) {
        View v = recurseViewsToCheckCoverLayout(this);
        if (v != null) {
            ((CloudAlbumFolderFrame)v).updateView();
        }
	}

	@Override
	public void clearSiteFrame() {
        int childCount = this.getChildCount();
        ArrayList<View> frameViewList = new ArrayList<>();
        for (int a = 0; a < childCount; a++) {
            if (a != 0) {
                frameViewList.add(this.getChildAt(a));
            }
        }
        for (View v : frameViewList) {
            ((IFrame)v).onBackPress();
        }
        mIFrame = (IFrame) this.getChildAt(this.getChildCount()-1);
	}

	// 清除所有云相册创建相册流程的所有frame
	@Override
	public void clearCreateFolderSiteFrame() {
		int childCount = this.getChildCount();
		ArrayList<View> frameViewList = new ArrayList<>();
		for (int a = 0; a < childCount; a++) {
			View currentView = this.getChildAt(a);
			if ((currentView instanceof CreateCloudAlbumFrame) || (currentView instanceof CloudAlbumCategoryFrame)) {
				frameViewList.add(currentView);
			}
		}
		for (View view : frameViewList) {
			((IFrame)view).onBackPress();
		}
		mIFrame = (IFrame) this.getChildAt(this.getChildCount()-1);

	}

	private View recurseViewsToCheckCoverLayout(ViewGroup viewGroup) {
        for (int a = 0; a < viewGroup.getChildCount(); a++) {
            View v = viewGroup.getChildAt(a);
            if (v instanceof CloudAlbumFolderFrame) {
                return v;
            }
        }
        return null;
    }

    public void setUploadCallback(UploadCallBack uploadCallback) {
		mUploadCallBack = uploadCallback;
	}

	@Override
	public void setFolderInfos(List<FolderInfo> infos) {
		mFolderInfos = infos;
	}

	@Override
	public List<FolderInfo> getFolderInfos() {
		return mFolderInfos;
	}

	@Override
	public void updateInfoAfterEdit(FolderInfo folderInfo) {
		View view;
		for (int i = 0; i < getChildCount(); i++) {
			view = getChildAt(i);
			if (view instanceof CloudAlbumListFrame) {
				((CloudAlbumListFrame)view).updateInfoAfterEdit(folderInfo);
				break;
			}
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		// 在这里清除资源
		TransportImgs.getInstance(getContext()).stopListener();
		TransportImgs.getInstance(getContext()).removeOnUploadCompleteListener(mUploadComleteListener);
		NetworkMonitor.getInstance().stopMonitor();
        CloudAlbumController.getInstacne().release();
        NotificationCenter.getInstance().release();

		mContext.unregisterReceiver(mTransportInfoReceiver);

		StorageService.Stop(mContext);
	}

	public interface IFrame {
		boolean onBackPress();
		void onResume();
		void onPause();
		void onClose();
	}

	public interface UploadCallBack {
		void upload(String[] imgs);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		if (null != params && params.size() > 0) {

			Object o = params.get("size");
			if (o instanceof Long) {
				mCloudStorageItem.setFreeVolume(mCloudStorageItem.getFreeVolume() - (Long)o);
			}

			Object imgs = params.get("imgs");

			if (null != mUploadCallBack) {
//				RotationImg2[] img2s = (RotationImg2[]) imgs;
//				String[] Imgs = new String[img2s.length];
//				for (int i = 0; i < img2s.length; i++) {
//					Imgs[i] = img2s[i].m_orgPath;
//				}
				mUploadCallBack.upload((String[]) imgs);
			}
		}
	}


	private TransportImgs.OnUploadCompleteListener mUploadComleteListener = new TransportImgs.OnUploadCompleteListener() {

		@Override
		public void onComplete(String folderId) {
			if (TransportImgs.getInstance(mContext).isUploadCompleted(folderId)) {
				updateFolderFrame(folderId);
			}
		}
	};

	@Override
	public void setMoveCallback(OnMoveCallback callback) {
		mOnMoveCallback = callback;
	}

	@Override
	public void notifyMoveCompleted(boolean success) {
		if (mOnMoveCallback != null) {
			mOnMoveCallback.onBack(success);
			mOnMoveCallback = null;
		}
	}

	@Override
	public void setOnCreateAlbumCallback(OnCreateAlbumCallback callback) {
		mOnCreateAlbumCallback = callback;
	}

	@Override
	public void notifyCreateAlbumCompleted(String folderId) {
		if (mOnCreateAlbumCallback != null) {
			mOnCreateAlbumCallback.onCreateCompleted(folderId);
			mOnCreateAlbumCallback = null;
		}
	}

	@Override
	public long getFreeVolume() {
		if (mCloudStorageItem == null) {
			return 0;
		}
		return mCloudStorageItem.getFreeVolume();
	}

	public interface OnMoveCallback {
		void onBack(boolean success);
	}

	public interface OnCreateAlbumCallback {
		void onCreateCompleted(String folderId);
	}
}
