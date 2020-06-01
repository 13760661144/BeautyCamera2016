package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.cloudalbumlibs.AbsAlbumListFrame;
import cn.poco.cloudalbumlibs.ITongJi;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.cloudalbumlibs.model.TransportImgInfo;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.cloudalbumlibs.view.TabIndicator;
import cn.poco.credits.Credit;
import cn.poco.storage.StorageService;
import cn.poco.system.TagMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/9/13
 */
public class CloudAlbumListFrame extends AbsAlbumListFrame implements CloudAlbumPage.IFrame {

	private int mTipType;

	private static final int TIP_DURATION = 5000;

	private IAlbumPage mIAlbumPage;

	private MyHandler mHandler;

	private WaitAnimDialog mWaitAnimDialog1;

	private WaitAnimDialog mWaitAnimDialog2;

	public CloudAlbumListFrame(IAlbumPage iAlbumPage, FolderInfo folderInfo, ITongJi iTongJi) {
		super(iAlbumPage.getPageContext(), folderInfo, iTongJi);

		mIAlbumPage = iAlbumPage;


		mHandler = new MyHandler(this);

		TransportImgs.getInstance(mContext).addOnStateChangeListener(mOnStateChangeListener);
		TransportImgs.getInstance(mContext).addOnUploadCompleteListener(mUploadComleteListener);

		initUserIdAndToken();

		mWaitAnimDialog2.show();
		getFolderImg(1, PAGE_COUNT);

		showTipbar();
	}

	@Override
	protected void init() {
		super.init();
		mWaitAnimDialog1 = new WaitAnimDialog((Activity)mContext);
		mWaitAnimDialog2 = new WaitAnimDialog((Activity)mContext);
		mWaitAnimDialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					mWaitAnimDialog2.dismiss();
					back();
				}
				return true;
			}
		});
	}

	@Override
	protected void openCloudAlbumMovePhotoFrame(String folderId, String photoIds) {
		List<PhotoInfo> photoInfos = mAdapter1.getSelectImageInfos();
		if (mPosition == 1) {
			photoInfos = mAdapter2.getSelectImageInfos();
		}
		ArrayList<String> urls = new ArrayList<String>();
		for (PhotoInfo info : photoInfos) {
			urls.add(info.getUrl());
		}
		mIAlbumPage.openCloudAlbumMovePhotoFrame(this, folderId, photoIds, urls);
	}

	@Override
	protected void openCloudAlbumBigPhotoFrame(List<PhotoInfo> photoInfos, int position) {
		mIAlbumPage.openCloudAlbumBigPhotoFrame(this, photoInfos, position, mFolderInfo.getName());
	}

	@Override
	protected void openCloudAlbumTransportFrame() {
		boolean download = false;
		if ((mTipType & 0x0000000f) == 0) {
			download = true;
		}

		if ((mTipType & 0x00000022) != 0) {
			// 等待状态
			mITongJi.transportWaitBar(mContext);
		} else if ((mTipType & 0x00000088) != 0) {
			// 失败状态
			mITongJi.transportErrorBar(mContext);
		} else if (mTipType == TransportImgs.TYPE_UPLOAD_PROGRESS) {
			// 正在上传
			mITongJi.uploadingBar(mContext);
		}
		mIAlbumPage.openCloudAlbumTransportFrame(download);
	}

	@Override
	protected void haveSelected(ImageView deleteView, TextView moveView, ImageView downloadView, boolean selected) {
		if (selected) {
			Bitmap bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_delete));
			deleteView.setImageBitmap(bitmap);
			bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_save));
			downloadView.setImageBitmap(bitmap);
			moveView.setAlpha(1);
		} else {
			Bitmap bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_delete_default));
			deleteView.setImageBitmap(bitmap);
			bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_save_default));
			downloadView.setImageBitmap(bitmap);
			moveView.setAlpha(0.3f);
		}
	}

	@Override
	protected void onDownload(List<PhotoInfo> photoInfos) {
		TransportImgs.sCloseTip = false;
		T.showShort(mContext, R.string.cloud_album_start_download);
		TransportImgs.getInstance(mContext).downloadImgs(photoInfos, mFolderInfo.getName());
	}

	@Override
	public void onUpload() {

		mIAlbumPage.setUploadCallback(new CloudAlbumPage.UploadCallBack() {
			@Override
			public void upload(String[] imgs) {
				TransportImgs.sCloseTip = false;
				TransportImgs.getInstance(mContext).uploadImgs(imgs, mFolderInfo.getFolderId(), mFolderInfo.getName());
				creditIncome();
			}
		});
		Glide.get(mContext).clearMemory();
		mIAlbumPage.getSite().OpenPickPhoto(getContext(), mFolderInfo.getName(), mIAlbumPage.getFreeVolume());
	}

	@Override
	protected void onTipDelete() {
		TransportImgs.sCloseTip = true;
	}

	@Override
	protected void openCloudAlbumEditFrame(FolderInfo folderInfo) {
		mIAlbumPage.openCloudAlbumEditFrame(folderInfo);
	}

	@Override
	protected void deleteAlbumSuccess() {
		super.deleteAlbumSuccess();
		mIAlbumPage.updateFolderFrameAfterDeleteAlbum(mFolderInfo.getFolderId());
	}

	@Override
	protected void deleteAllTransportInfos() {
		TransportImgs transportImgs = TransportImgs.getInstance(mContext);
		List<TransportImgInfo> removeList = new ArrayList<>();

		String folderId = mFolderInfo.getFolderId();

		for (TransportImgInfo info : transportImgs.mUploadingList) {
			if (info.getFolderId().equals(folderId)) {
				StorageService.CancelUploadTask(mContext, info.getAcid());
				removeList.add(info);
			}
		}
		if (!removeList.isEmpty()) {
			transportImgs.mUploadingList.removeAll(removeList);
			removeList.clear();
		}

		for (TransportImgInfo info : transportImgs.mUploadErrorList) {
			if (info.getFolderId().equals(folderId)) {
				StorageService.ClearTransportInfo(mContext, info.getAcid());
				removeList.add(info);
			}
		}
		if (!removeList.isEmpty()) {
			transportImgs.mUploadErrorList.removeAll(removeList);
			removeList.clear();
		}

		if (transportImgs.mUploadWaittingMap.containsKey(folderId)) {
			transportImgs.mUploadWaittingMap.remove(folderId);
		}

		for (TransportImgInfo info : transportImgs.mDownloadingList) {
			if (info.getFolderId().equals(folderId)) {
				StorageService.CancelDownloadTask(mContext, info.getAcid());
				removeList.add(info);
			}
		}
		if (!removeList.isEmpty()) {
			transportImgs.mDownloadingList.removeAll(removeList);
			removeList.clear();
		}

		for (TransportImgInfo info : transportImgs.mDownloadErrorList) {
			if (info.getFolderId().equals(folderId)) {
				StorageService.ClearTransportInfo(mContext, info.getAcid());
				removeList.add(info);
			}
		}
		if (!removeList.isEmpty()) {
			transportImgs.mDownloadErrorList.removeAll(removeList);
			removeList.clear();
		}

		for (TransportImgInfo info : transportImgs.mDownloadWaittingList) {
			if (info.getFolderId().equals(folderId)) {
				removeList.add(info);
			}
		}
		if (!removeList.isEmpty()) {
			transportImgs.mDownloadWaittingList.removeAll(removeList);
			removeList.clear();
		}

		// 通知传输bar改变
		transportImgs.transportState();
	}

	@Override
	protected void deletePhotoSuccess(int deleteCount) {
		super.deletePhotoSuccess(deleteCount);
		mIAlbumPage.updateFolderFrame(mFolderInfo.getFolderId());
	}

	@Override
	protected void movePhotoSuccess(String oldFolderId, String newFolderId, int moveCount) {
		super.movePhotoSuccess(oldFolderId, newFolderId, moveCount);
		mIAlbumPage.updateFolderFrame(newFolderId);
		mIAlbumPage.setMoveCallback(null);
	}

	@Override
	protected String getUserId() {
		return mIAlbumPage.getUserId();
	}

	@Override
	protected String getAccessToken() {
		return mIAlbumPage.getAccessToken();
	}

	@Override
	protected IAlbum getIAlbum() {
		return mIAlbumPage.getIAlbum();
	}

	@Override
	public boolean onBackPress() {
		dispatchBack();
		return true;
	}

	@Override
	protected void onBack() {
		super.onBack();
		TransportImgs.getInstance(mContext).removeOnStateChangeListener(mOnStateChangeListener);
		TransportImgs.getInstance(mContext).removeOnUploadCompleteListener(mUploadComleteListener);

		mHandler.removeMessages(0x123);

		mIAlbumPage.setUploadCallback(null);
		mIAlbumPage.onFrameBack(this);
	}

	public void updateInfoAfterEdit(FolderInfo folderInfo) {
		this.mFolderInfo = folderInfo;
		mTitleView.setText(folderInfo.getName());
	}

	/**
	 * 监听传输状态改变
	 */
	private TransportImgs.OnStateChangeListener mOnStateChangeListener = new TransportImgs.OnStateChangeListener() {
		@Override
		public void onStateChange(int type, String tip) {

			if (TextUtils.isEmpty(tip)) {
				if (mTipBar.getVisibility() == View.VISIBLE) {
					hideTipBar();
				}
				return;
			}

			if (mTipBar.getVisibility() != View.VISIBLE && !TransportImgs.sCloseTip) {
				mTipBar.setVisibility(View.VISIBLE);
				mViewPager.setPadding(0, ShareData.PxToDpi_xhdpi(32), 0, 0);
			}
			changeTipbar(type, tip);
		}
	};

	private void showTipbar() {
		TransportImgs transportImgs = TransportImgs.getInstance(mContext);

		TransportImgs.BarInfo barInfo = transportImgs.getBarInfo();
		if (TextUtils.isEmpty(barInfo.message)) {
			if (mTipBar.getVisibility() == View.VISIBLE) {
				hideTipBar();
			}
			return;
		}

		if (transportImgs.isShowBar() && mTipBar.getVisibility() != View.VISIBLE) {
			mTipBar.setVisibility(View.VISIBLE);
			mViewPager.setPadding(0, ShareData.PxToDpi_xhdpi(32), 0, 0);

			changeTipbar(barInfo.type, barInfo.message);
		}
	}

	private void changeTipbar(int type, String tip) {
		if ((type & 0x00000088) == 0) {
			mTipBar.setBackgroundColor(Color.parseColor("#ff07c34e"));
		} else {
			mTipBar.setBackgroundColor(Color.parseColor("#ccfe4c4c"));
		}
		mTipText.setText(tip);
		mTipType = type;
		if ((type & 0x00000044) != 0) {
			// 上传或下载完成
			mHandler.sendEmptyMessageDelayed(0x123, TIP_DURATION);
		}
	}

	/**
	 * 避免内存泄漏
	 */
	private static class MyHandler extends Handler {

		private WeakReference<CloudAlbumListFrame> mWeakReference;

		private MyHandler(CloudAlbumListFrame listFrame) {
			mWeakReference = new WeakReference<>(listFrame);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123 && mWeakReference.get() != null) {
				mWeakReference.get().autoHideTipBar();
			}
		}
	}

	/**
	 * 自动隐藏TipBar
	 */
	private void autoHideTipBar() {
		if (mTipBar.getVisibility() != GONE && (mTipType & 0x00000044) != 0) {
			hideTipBar();
			onTipDelete();
		}
	}

	private void hideTipBar() {
		mTipBar.setVisibility(View.GONE);
		mViewPager.setPadding(0, 0, 0, 0);
	}

	private TransportImgs.OnUploadCompleteListener mUploadComleteListener = new TransportImgs.OnUploadCompleteListener() {

		@Override
		public void onComplete(String folderId) {
			if (folderId.equals(mFolderInfo.getFolderId())) {
				refresh(-1);
			}
		}
	};

	/**
	 * 触发积分行为
	 */
	private void creditIncome()
	{
		if(null == TagMgr.GetTagValue(getContext(), "云相册文件夹首次导入照片（默认+新建）") && null != TagMgr.GetTagValue(getContext(), "云相册首次新建文件夹"))
		{
			Credit.CreditIncome(getContext(), String.valueOf(getContext().getResources().getInteger(R.integer.积分_云相册首次上传图片)));
			TagMgr.SetTagValue(getContext(), "云相册文件夹首次导入照片（默认+新建）", "true");
		}
	}

	@Override
	protected void initListView(SwipeRefreshLayout refreshLayout1, ListView listView1, SwipeRefreshLayout refreshLayout2, ListView listView2) {
		super.initListView(refreshLayout1, listView1, refreshLayout2, listView2);
		refreshLayout1.setColorSchemeColors(ImageUtils.GetSkinColor(0xffe75988));
		refreshLayout2.setColorSchemeColors(ImageUtils.GetSkinColor(0xffe75988));
	}

	@Override
	protected void initSelectBar(RelativeLayout selectBar, TextView selectAll, TextView selectNumber, TextView selectCancel) {
		super.initSelectBar(selectBar, selectAll, selectNumber, selectCancel);
		selectAll.setVisibility(GONE);
		selectAll.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		selectCancel.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	@Override
	protected void initTabBar(View tabBar, View[] tabs, ImageView[] imageViews, TextView[] textViews, TabIndicator tabIndicator) {
		super.initTabBar(tabBar, tabs, imageViews, textViews, tabIndicator);
		tabIndicator.setColor(ImageUtils.GetSkinColor(0xffe75988));

		Bitmap bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_create_date_selected));
		imageViews[0].setImageBitmap(bitmap);
		textViews[0].setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		imageViews[1].setImageResource(R.drawable.cloudalbum_upload_normal);
		textViews[1].setTextColor(COLOR_GRAY);
	}

	@Override
	protected void initAppBar(RelativeLayout appBar, ImageView backView, TextView titleView, ImageView moreView) {
		super.initAppBar(appBar, backView, titleView, moreView);
		ImageUtils.AddSkin(mContext, backView);
		ImageUtils.AddSkin(mContext, moreView);
	}

	@Override
	protected void initUploadView(FrameLayout uploadView, ImageView uploadImage, TextView uploadText, ImageView uploadCircle) {
		super.initUploadView(uploadView, uploadImage, uploadText, uploadCircle);
		ImageUtils.AddSkin(mContext, uploadCircle);
	}

	@Override
	protected void onTabSelected(ImageView[] imageViews, TextView[] textViews, int position) {
		super.onTabSelected(imageViews, textViews, position);
		if (mPosition == 0) {
			imageViews[0].setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_create_date_normal);
			textViews[0].setTextColor(COLOR_GRAY);
			Bitmap bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_upload_selected));
			imageViews[1].setImageBitmap(bitmap);
			textViews[1].setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		} else {
			imageViews[1].setImageResource(cn.poco.cloudalbumlibs.R.drawable.cloudalbum_upload_normal);
			textViews[1].setTextColor(COLOR_GRAY);
			Bitmap bitmap = ImageUtils.AddSkin(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.cloudalbum_create_date_selected));
			imageViews[0].setImageBitmap(bitmap);
			textViews[0].setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		}
	}

	@Override
	protected void addSkin(ImageView imageView) {
		super.addSkin(imageView);
		ImageUtils.AddSkin(mContext, imageView);
	}

	@Override
	protected void initBottomBar(RelativeLayout bottomBar, ImageView deleteView, TextView moveView, ImageView downloadView) {
		super.initBottomBar(bottomBar, deleteView, moveView, downloadView);
		moveView.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	@Override
	protected void showProgressDialog1() {
		mWaitAnimDialog1.show();
	}

	@Override
	protected void hideProgressDialog1() {
		mWaitAnimDialog1.dismiss();
	}

	@Override
	protected void hideProgressDialog2() {
		mWaitAnimDialog2.dismiss();
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onClose() {

	}
}
