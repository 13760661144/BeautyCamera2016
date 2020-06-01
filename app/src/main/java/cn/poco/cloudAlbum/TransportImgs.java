package cn.poco.cloudAlbum;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.cloudAlbum.model.TransportInfo;
import cn.poco.cloudAlbum.utils.FileUtils;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.cloudalbumlibs.model.TransportImgInfo;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.storage.StorageReceiver;
import cn.poco.storage.StorageService;
import cn.poco.storagesystemlibs.AbsStorageService;
import cn.poco.storagesystemlibs.CloudListener;
import cn.poco.storagesystemlibs.ServiceStruct;
import cn.poco.system.TagMgr;
import cn.poco.utils.NetworkMonitor;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/8/29
 */
public class TransportImgs {

	public static final int UPLOAD_WAITING_WIFI = 0x2101;
	public static final int UPLOAD_ALL_COMPLETE = 0x2102;
	public static final int DOWNLOAD_WAITING_WIFI = 0x210001;
	public static final int DOWNLOAD_ALL_COMPLETE = 0x210002;

	/**
	 * 标记是否需要加载传输记录信息
	 */
	public static boolean sLoadTransportInfo = false;

	private static final int TRANSPORT_LIMIT = 100;

	private Context mContext;

	private static TransportImgs sTransportImgs;

	public static boolean sCloseTip = false;

	public static boolean sAllowCellular = false;

	private int mUploadCompleted = 0;

	public List<TransportImgInfo> mUploadingList = new ArrayList<>();
	public List<TransportImgInfo> mUploadErrorList = new ArrayList<>();
	public Map<String, List<TransportImgInfo>> mUploadWaittingMap = new HashMap<>();

	public List<TransportImgInfo> mDownloadingList = new ArrayList<>();
	public List<TransportImgInfo> mDownloadErrorList = new ArrayList<>();
	public List<TransportImgInfo> mDownloadWaittingList = new ArrayList<>();

	private List<IDataChange> mListeners = new ArrayList<>();

	/**
	 * 标记是否退出云相册页面
	 */
	private boolean mIsExit;

	private BarInfo mBarInfo = new BarInfo();

	private static String sUserId;
	private static String sAccessToken;
	private static String sWifiTag;

	private TransportImgs(Context context) {
		mContext = context;
		mIsExit = false;
		StorageReceiver.AddCloudListener(mCloudListener);
		NetworkMonitor.getInstance().addNetworkListener(mNetworkListener);
	}

	/**
	 * 初始化
	 *
	 * @param userId      用户id
	 * @param accessToken access_token
	 */
	public static void init(String userId, String accessToken, String wifiTag) {
		sUserId = userId;
		sAccessToken = accessToken;
		sWifiTag = wifiTag;
	}

	public static TransportImgs getInstance(Context context) {
		if (sTransportImgs == null) {
			sTransportImgs = new TransportImgs(context);
		}
		return sTransportImgs;
	}

	/**
	 * 继续监听
	 */
	public void onListener() {
		if (mIsExit) {
			StorageReceiver.AddCloudListener(mCloudListener);
			NetworkMonitor.getInstance().addNetworkListener(mNetworkListener);

			mIsExit = false;
		}
	}

	public void addListener(IDataChange listener) {
		mListeners.add(listener);
	}

	public void removeListener(IDataChange listener) {
		mListeners.remove(listener);
	}

	private void notifyDataChange(int type, boolean download) {
		for (IDataChange listener : mListeners) {
			listener.onDataChange(type, download);
		}
	}

	/**
	 * 上传图片操作
	 *
	 * @param imgs      图片路径
	 * @param albumId   相册id
	 * @param albumName 相册名称
	 */
	public void uploadImgs(String[] imgs, String albumId, String albumName) {

		if (!validateInit()) {
			throw new RuntimeException("必须先调用TransportImgs的init()方法");
		}

		int totalSize = mUploadingList.size() + getUploadWaittingSize();
		if (totalSize >= TRANSPORT_LIMIT) {
			String text = mContext.getResources().getString(R.string.uploading_number_limit, TRANSPORT_LIMIT);
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			return;
		} else if (totalSize < TRANSPORT_LIMIT && totalSize + imgs.length > TRANSPORT_LIMIT) {
			int left = TRANSPORT_LIMIT - totalSize;
			imgs = Arrays.copyOfRange(imgs, 0, left);
			String text = mContext.getResources().getString(R.string.uploading_number_limit, TRANSPORT_LIMIT);
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}

		List<TransportImgInfo> imgInfos = new ArrayList<>();
		File file;
		TransportImgInfo uploadImgInfo;

		for (int i = 0; i < imgs.length; i++) {
			uploadImgInfo = new TransportImgInfo();
			file = new File(imgs[i]);
			if (!file.exists()) {
				continue;
			}
			uploadImgInfo.setImgName(file.getName());
			uploadImgInfo.setImgVolume(String.valueOf(file.length()));
			uploadImgInfo.setImgPath(imgs[i]);
			uploadImgInfo.setToAlbumName(albumName);
			uploadImgInfo.setFolderId(albumId);

			imgInfos.add(uploadImgInfo);
		}

		boolean isWifi;

		String temp = TagMgr.GetTagValue(mContext, sWifiTag);
		isWifi = temp == null || temp.equals("true");

		if (!sAllowCellular && isWifi && !NetWorkUtils.isWifiContected(mContext)) {
			for (TransportImgInfo info : imgInfos) {
				info.setAcid(0);
			}
			List<TransportImgInfo> transportImgInfos = mUploadWaittingMap.get(albumId);
			if (transportImgInfos == null) {
				transportImgInfos = imgInfos;
			} else {
				transportImgInfos.addAll(0, imgInfos);
			}

			mUploadWaittingMap.put(albumId, transportImgInfos);

			transportState(UPLOAD_WAITING_WIFI);
			notifyDataChange(WAITTING, false);

		} else {
			mUploadingList.addAll(imgInfos);

			ServiceStruct struct;
			TransportInfo transportInfo;
			int acid;
			for (TransportImgInfo info : imgInfos) {
				struct = new ServiceStruct();

				transportInfo = new TransportInfo();
				transportInfo.type = TransportInfo.UPLOAD;
				transportInfo.path = info.getImgPath();
				transportInfo.folderId = albumId;
				transportInfo.folderName = albumName;

				struct.mEx = transportInfo;

				struct.mPath = info.getImgPath();
				struct.mIsAlbum = true;
				struct.mUserId = sUserId;
				struct.mAccessToken = sAccessToken;
				struct.mFolderId = Integer.valueOf(albumId);
				acid = StorageService.PushUploadTask(mContext, struct);
				info.setAcid(acid);
			}

			transportState(AbsStorageService.UPLOAD);
			notifyDataChange(PROGRESS, false);
		}
	}

	/**
	 * 编辑后重新上传调用
	 *
	 * @param imgInfos 传输列表信息
	 */
	public void uploadImgs(List<TransportImgInfo> imgInfos) {
		ServiceStruct struct;
		TransportInfo transportInfo;
		int acid;
		for (TransportImgInfo info : imgInfos) {
			struct = new ServiceStruct();

			transportInfo = new TransportInfo();
			transportInfo.type = TransportInfo.UPLOAD;
			transportInfo.path = info.getImgPath();
			transportInfo.folderId = info.getFolderId();
			transportInfo.folderName = info.getToAlbumName();

			struct.mEx = transportInfo;

			struct.mPath = info.getImgPath();
			struct.mIsAlbum = true;
			struct.mUserId = sUserId;
			struct.mAccessToken = sAccessToken;
			struct.mFolderId = Integer.valueOf(info.getFolderId());
			acid = StorageService.PushUploadTask(mContext, struct);
			info.setAcid(acid);
		}
	}

	/**
	 * 验证是否初始化
	 */
	private boolean validateInit() {
		return !TextUtils.isEmpty(sUserId) && !TextUtils.isEmpty(sAccessToken) && !TextUtils.isEmpty(sWifiTag);
	}

	/**
	 * 下载图片
	 *
	 * @param photoInfos 图片信息
	 * @param albumName  相册名
	 */
	public void downloadImgs(List<PhotoInfo> photoInfos, String albumName) {

		if (!validateInit()) {
			throw new RuntimeException("必须先调用TransportImgs的init()方法");
		}

		int totalSize = mDownloadingList.size() + mDownloadWaittingList.size();
		if (totalSize >= TRANSPORT_LIMIT) {
			String text = mContext.getResources().getString(R.string.downloading_number_limit, TRANSPORT_LIMIT);
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			return;
		} else if (totalSize < TRANSPORT_LIMIT && totalSize + photoInfos.size() > TRANSPORT_LIMIT) {
			int left = TRANSPORT_LIMIT - totalSize;
			photoInfos = photoInfos.subList(0, left);
			String text = mContext.getResources().getString(R.string.downloading_number_limit, TRANSPORT_LIMIT);
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}

		List<TransportImgInfo> imgInfos = new ArrayList<>();

		TransportImgInfo downloadImgInfo;
		PhotoInfo photoInfo;
		String photoName, url;

		for (int i = 0; i < photoInfos.size(); i++) {
			photoInfo = photoInfos.get(i);

			downloadImgInfo = new TransportImgInfo();
			url = photoInfo.getUrl();
			photoName = url.substring(url.lastIndexOf("/") + 1);
			downloadImgInfo.setImgName(photoName);
			downloadImgInfo.setImgVolume(photoInfo.getVolume());
			downloadImgInfo.setImgPath(photoInfo.getCoverUrl());
			downloadImgInfo.setToAlbumName(albumName);
			downloadImgInfo.setUrl(url);

			imgInfos.add(downloadImgInfo);
		}

		boolean isWifi;
		String temp = TagMgr.GetTagValue(mContext, sWifiTag);
		isWifi = temp == null || temp.equals("true");

		if (!sAllowCellular && isWifi && !NetWorkUtils.isWifiContected(mContext)) {
			for (TransportImgInfo info : imgInfos) {
				info.setAcid(0);
			}
			mDownloadWaittingList.addAll(imgInfos);
			transportState(DOWNLOAD_WAITING_WIFI);
			notifyDataChange(WAITTING, true);
		} else {
			mDownloadingList.addAll(imgInfos);

			ServiceStruct struct;
			TransportInfo transportInfo;
			int acid;
			for (TransportImgInfo info : imgInfos) {
				struct = new ServiceStruct();

				transportInfo = new TransportInfo();
				transportInfo.type = TransportInfo.DOWNLOAD;
				transportInfo.path = info.getUrl();
				transportInfo.size = info.getImgVolume();
				transportInfo.folderName = albumName;

				struct.mEx = transportInfo;

				struct.mUserId = sUserId;
				struct.mAccessToken = sAccessToken;
				struct.mMyUrl = info.getUrl();
				acid = StorageService.PushDownloadTask(mContext, struct);
				info.setAcid(acid);
			}

			transportState(AbsStorageService.DOWNLOAD);
			notifyDataChange(PROGRESS, true);
		}

	}

	/**
	 * 编辑后重新下载调用
	 *
	 * @param imgInfos 传输列表信息
	 */
	public void downloadImgs(List<TransportImgInfo> imgInfos) {
		ServiceStruct struct;
		TransportInfo transportInfo;
		int acid;
		for (TransportImgInfo info : imgInfos) {
			struct = new ServiceStruct();

			transportInfo = new TransportInfo();
			transportInfo.type = TransportInfo.DOWNLOAD;
			transportInfo.path = info.getUrl();
			transportInfo.size = info.getImgVolume();
			transportInfo.folderName = info.getToAlbumName();

			struct.mEx = transportInfo;

			struct.mUserId = sUserId;
			struct.mAccessToken = sAccessToken;
			struct.mMyUrl = info.getUrl();
			acid = StorageService.PushDownloadTask(mContext, struct);
			info.setAcid(acid);
		}
	}

	private NetworkMonitor.NetworkListener mNetworkListener = new NetworkMonitor.NetworkListener() {
		@Override
		public void onNetworkChanged(int networkType) {
			// wifi
			if (networkType == ConnectivityManager.TYPE_WIFI) {
				if (getUploadWaittingSize() != 0) {
					processWaittingUpload();
				}

				if (!mDownloadWaittingList.isEmpty()) {
					processWaittingDownload();
				}
			}
		}
	};

	/**
	 * 处理等待上传的图片
	 */
	private void processWaittingUpload() {

		Map<String, List<TransportImgInfo>> temp = new HashMap<>();
		temp.putAll(mUploadWaittingMap);
		mUploadWaittingMap.clear();

		List<TransportImgInfo> waittingImgInfos;
		String[] imgs;
		for (String albumId : temp.keySet()) {
			waittingImgInfos = temp.get(albumId);
			imgs = new String[waittingImgInfos.size()];
			for (int i = 0; i < waittingImgInfos.size(); i++) {
				imgs[i] = waittingImgInfos.get(i).getImgPath();
			}
			uploadImgs(imgs, albumId, waittingImgInfos.get(0).getToAlbumName());
		}
	}

	/**
	 * 处理等待下载的图片
	 */
	private void processWaittingDownload() {
		// folderName -> imgs
		Map<String, ArrayList<PhotoInfo>> map = new HashMap<>();

		List<TransportImgInfo> temp = new ArrayList<>();
		temp.addAll(mDownloadWaittingList);
		mDownloadWaittingList.clear();

		ArrayList<PhotoInfo> photoInfos;
		PhotoInfo photoInfo;
		for (TransportImgInfo info : temp) {

			photoInfos = map.get(info.getToAlbumName());
			if (photoInfos == null) {
				photoInfos = new ArrayList<>();
			}

			photoInfo = new PhotoInfo();
			photoInfo.setUrl(info.getUrl());
			photoInfo.setVolume(info.getImgVolume());
			photoInfo.setCoverUrl(info.getImgPath());
			photoInfos.add(photoInfo);
			map.put(info.getToAlbumName(), photoInfos);
		}

		for (String albumName : map.keySet()) {
			downloadImgs(map.get(albumName), albumName);
		}
	}

	/**
	 * 获取等待上传的照片的数目
	 */
	public int getUploadWaittingSize() {
		int count = 0;
		for (String albumId : mUploadWaittingMap.keySet()) {
			count += mUploadWaittingMap.get(albumId).size();
		}

		return count;
	}

	/**
	 * 获取等待上传的照片列表
	 */
	public List<TransportImgInfo> getUploadWaittingList() {
		List<TransportImgInfo> uploadWaittingList = new ArrayList<>();
		for (String albumId : mUploadWaittingMap.keySet()) {
			uploadWaittingList.addAll(mUploadWaittingMap.get(albumId));
		}
		return uploadWaittingList;
	}

	public static final int TYPE_UPLOAD_PROGRESS = 1;
	public static final int TYPE_UPLOAD_WAIT = 2;
	public static final int TYPE_UPLOAD_COMPLETED = 4;
	public static final int TYPE_UPLOAD_ERROR = 8;

	public static final int TYPE_DOWNLOAD_PROGRESS = 16;
	public static final int TYPE_DOWNLOAD_WAIT = 32;
	public static final int TYPE_DOWNLOAD_COMPLETED = 64;
	public static final int TYPE_DOWNLOAD_ERROR = 128;

	/**
	 * 传输状态改变
	 *
	 * @param type 传输类型
	 */
	private void transportState(int type) {
		int tempType = 0;
		String tip = "";
		if (mUploadingList.size() > 0 && mDownloadingList.size() > 0) {
			tip = mContext.getResources().getString(R.string.uploading_and_downloading_tip, mUploadingList.size(), mDownloadingList.size());
			tempType = TYPE_UPLOAD_PROGRESS + TYPE_DOWNLOAD_PROGRESS;
		} else if (mUploadingList.size() > 0) {
			tip = mContext.getResources().getString(R.string.uploading_tip, mUploadingList.size());
			tempType = TYPE_UPLOAD_PROGRESS;
		} else if (mDownloadingList.size() > 0) {
			tip = mContext.getResources().getString(R.string.downloading_tip, mDownloadingList.size());
			tempType = TYPE_DOWNLOAD_PROGRESS;
		}

		if (tip.isEmpty()) {
			if (getUploadWaittingSize() > 0 && mDownloadWaittingList.size() > 0) {
				tip = mContext.getResources().getString(R.string.waiting_wifi_to_transport, getUploadWaittingSize() + mDownloadWaittingList.size());
				tempType = TYPE_UPLOAD_WAIT + TYPE_DOWNLOAD_WAIT;
			} else if (getUploadWaittingSize() > 0) {
				tip = mContext.getResources().getString(R.string.waiting_wifi_to_upload, getUploadWaittingSize());
				tempType = TYPE_UPLOAD_WAIT;
			} else if (mDownloadWaittingList.size() > 0) {
				tip = mContext.getResources().getString(R.string.waiting_wifi_to_download, mDownloadWaittingList.size());
				tempType = TYPE_DOWNLOAD_WAIT;
			}
		}

		if (tip.isEmpty() && mUploadingList.size() == 0 && mDownloadingList.size() == 0) {
			if (type == AbsStorageService.UPLOAD_SINGLE_COMPLETE) {
				if (mUploadErrorList.size() == 0 && mDownloadErrorList.size() == 0) {
					tip = mContext.getResources().getString(R.string.all_photo_upload_success);
					tempType = TYPE_UPLOAD_COMPLETED;
				}
			} else if (type == AbsStorageService.DOWNLOAD_SINGLE_COMPLETE) {
				if (mUploadErrorList.size() == 0 && mDownloadErrorList.size() == 0) {
					tip = mContext.getResources().getString(R.string.all_photo_download_success);
					tempType = TYPE_DOWNLOAD_COMPLETED;
				}
			}
		}

		if (tip.isEmpty()) {
			if (mUploadErrorList.size() > 0) {
				tip = mContext.getResources().getString(R.string.upload_error, mUploadErrorList.size());
				tempType = TYPE_UPLOAD_ERROR;
			} else if (mDownloadErrorList.size() > 0) {
				tip = mContext.getResources().getString(R.string.download_error, mDownloadErrorList.size());
				tempType = TYPE_DOWNLOAD_ERROR;
			}
		}

		mBarInfo.message = tip;
		mBarInfo.type = tempType;

		for (OnStateChangeListener listener : mOnStateChangeListeners) {
			listener.onStateChange(tempType, tip);
		}
	}

	/**
	 * 在取消传输任务时调用
	 */
	public void transportState() {
		transportState(0);
	}

	public boolean isShowBar() {
		return !(mBarInfo.type == 0 || TextUtils.isEmpty(mBarInfo.message) || sCloseTip);
	}

	public BarInfo getBarInfo() {
		return mBarInfo;
	}

	private List<OnStateChangeListener> mOnStateChangeListeners = new ArrayList<>();

	public void addOnStateChangeListener(OnStateChangeListener listener) {
		mOnStateChangeListeners.add(listener);
	}

	public void removeOnStateChangeListener(OnStateChangeListener listener) {
		mOnStateChangeListeners.remove(listener);
	}

	public interface OnStateChangeListener {
		void onStateChange(int type, String tip);
	}

	public CloudListener mCloudListener = new CloudListener() {

		@Override
		public void OnProgress(int type, ServiceStruct str, int progress) {
			TransportImgInfo info = findItem(type, str.mAcId);
			if (info != null) {
				if (info.isCanEdit() && info.isStop()) {
					info.setProgress(0);
					info.updateProgress();
					if (type == AbsStorageService.UPLOAD_SINGLE_PROGRESS) {
						StorageService.CancelUploadTask(mContext, info.getAcid());
					} else if (type == AbsStorageService.DOWNLOAD_SINGLE_PROGRESS) {
						StorageService.CancelDownloadTask(mContext, info.getAcid());
					}
				} else {
//					info.setStop(false);
					info.setCanEdit(false);
					info.setProgress(progress);
					info.updateProgress();
				}
			}
		}

		@Override
		public void OnComplete(int type, ServiceStruct str) {
			str.mEx = null;
			TransportImgInfo info = findItem(type, str.mAcId);
			if (info != null) {
				info.setProgress(100);
				info.updateProgress();
				// 避免内存泄漏
				info.setProgressListener(null);
				info.setCanEdit(false);
			}
			if (type == AbsStorageService.UPLOAD_SINGLE_COMPLETE) {
				mUploadingList.remove(info);
				mUploadCompleted++;
//				if (info != null && isUploadCompleted(info.getFolderId())) {
//					sendUploadCompleteEvent(info.getFolderId());
//				}
				if (info != null) {
					sendUploadCompleteEvent(info.getFolderId());
				}
				notifyDataChange(COMPLETE, false);
				transportState(AbsStorageService.UPLOAD_SINGLE_COMPLETE);
			} else if (type == AbsStorageService.DOWNLOAD_SINGLE_COMPLETE) {
				mDownloadingList.remove(info);
				final String path = str.mPath;
				if (path != null && path.length() > 0 && mContext != null) {
					File img = new File(path);
					String imageName = img.getName();
					File newImg = FileUtils.createNewFile(imageName, img.length(), true);
					if (newImg != null) {
						FileUtils.fileChannelCopy(img, newImg);
						Utils.FileScan(mContext, newImg.getAbsolutePath());
					}
				}
				notifyDataChange(COMPLETE, true);
				transportState(AbsStorageService.DOWNLOAD_SINGLE_COMPLETE);
			}
		}

		@Override
		public void OnFail(int type, ServiceStruct str) {
			str.mEx = null;
			TransportImgInfo info = findItem(type, str.mAcId);

			if (info != null) {
				info.setProgress(0);
				info.updateProgress();

				if (info.isStop()) {
					return;
				} else {
					// 避免内存泄漏
					info.setProgressListener(null);
					info.setCanEdit(true);
				}
			}

			if (type == AbsStorageService.UPLOAD_SINGLE_FAIL) {
				if (info != null) {
					mUploadingList.remove(info);
					mUploadErrorList.add(info);

					notifyDataChange(ERROR, false);
					transportState(AbsStorageService.UPLOAD_SINGLE_FAIL);
				}
			} else if (type == AbsStorageService.DOWNLOAD_SINGLE_FAIL) {
				if (info != null) {
					mDownloadingList.remove(info);
					mDownloadErrorList.add(info);

					notifyDataChange(ERROR, true);
					transportState(AbsStorageService.DOWNLOAD_SINGLE_FAIL);
				}
			}
		}

		@Override
		public void OnError(int type, ServiceStruct str) {
			str.mEx = null;
			if (type == AbsStorageService.UPLOAD_ERROR) {
				for (TransportImgInfo info : mUploadingList) {
					info.setProgress(0);
					info.updateProgress();
					// 避免内存泄漏
					info.setProgressListener(null);
					info.setCanEdit(true);
				}
				mUploadErrorList.addAll(mUploadingList);
				mUploadingList.clear();
				notifyDataChange(ERROR, false);
				transportState(AbsStorageService.UPLOAD_ERROR);
			} else if (type == AbsStorageService.DOWNLOAD_ERROR) {
				for (TransportImgInfo info : mDownloadingList) {
					info.setProgress(0);
					info.updateProgress();
					info.setProgressListener(null);
					info.setCanEdit(true);
				}
				mDownloadErrorList.addAll(mDownloadingList);
				mDownloadingList.clear();
				notifyDataChange(ERROR, true);
				transportState(AbsStorageService.DOWNLOAD_ERROR);
			}
		}
	};

	/**
	 * 清除，在注销登录时调用
	 */
	public void clear() {
		stopListener();

		mUploadingList.clear();
		mUploadErrorList.clear();
		mUploadWaittingMap.clear();
		mUploadCompleted = 0;

		mDownloadingList.clear();
		mDownloadErrorList.clear();
		mDownloadWaittingList.clear();

		mBarInfo.clear();

		StorageService.ClearTransportInfos(mContext);

		sTransportImgs = null;
	}

	private TransportImgInfo findItem(int type, int acid) {
		if (type >= AbsStorageService.UPLOAD && type <= AbsStorageService.UPLOAD_ERROR) {
			for (TransportImgInfo info : mUploadingList) {
				if (info.getAcid() == acid) {
					return info;
				}
			}

		} else if (type >= AbsStorageService.DOWNLOAD && type <= AbsStorageService.DOWNLOAD_ERROR) {
			for (TransportImgInfo info : mDownloadingList) {
				if (info.getAcid() == acid) {
					return info;
				}
			}
		}

		return null;
	}

	public static final int PROGRESS = 0;
	public static final int COMPLETE = 1;
	public static final int WAITTING = 2;
	public static final int ERROR = 3;

	public interface IDataChange {
		void onDataChange(int type, boolean download);
	}

	private List<OnUploadCompleteListener> mOnUploadCompleteListeners = new ArrayList<>();

	public void addOnUploadCompleteListener(OnUploadCompleteListener listener) {
		mOnUploadCompleteListeners.add(listener);
	}

	public void removeOnUploadCompleteListener(OnUploadCompleteListener listener) {
		mOnUploadCompleteListeners.remove(listener);
	}

	private void sendUploadCompleteEvent(String folderId) {
		for (OnUploadCompleteListener listener : mOnUploadCompleteListeners) {
			listener.onComplete(folderId);
		}
	}

	public interface OnUploadCompleteListener {
		void onComplete(String folderId);
	}

	/**
	 * 判断指定文件夹内的图片是否上传完成
	 *
	 * @param folderId 文件夹id
	 * @return true: 图片上传完成
	 */
	public boolean isUploadCompleted(String folderId) {

		for (TransportImgInfo info : mUploadingList) {
			if (info.getFolderId().equals(folderId)) {
				return false;
			}
		}

		return true;
	}

	public void stopListener() {

		StorageReceiver.RemoveCloudListener(mCloudListener);
		NetworkMonitor.getInstance().removeNetworkListener(mNetworkListener);
		mIsExit = true;
		mUploadCompleted = 0;
//		sCloseTip = false;
	}

	public int getUploadCompleted() {
		int temp = mUploadCompleted;
		mUploadCompleted = 0;
		return temp;
	}

	public static class BarInfo {

		public int type;
		public String message;

		BarInfo() {
			type = 0;
			message = null;
		}

		void clear() {
			type = 0;
			message = null;
		}
	}
}
