package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.cloudalbumlibs.AbsAlbumBigImgFrame;
import cn.poco.cloudalbumlibs.AbsAlbumListFrame;
import cn.poco.cloudalbumlibs.ITongJi;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.utils.WaitAnimDialog;

/**
 * Created by: fwc
 * Date: 2016/9/13
 */
public class CloudAlbumBigImgFrame extends AbsAlbumBigImgFrame implements CloudAlbumPage.IFrame {

	private IAlbumPage mIAlbumPage;

	private String mAlbumName;

	private AbsAlbumListFrame mListFrame;

	private String mFolderId;

	private WaitAnimDialog mWaitAnimDialog;

	public CloudAlbumBigImgFrame(IAlbumPage iAlbumPage, AbsAlbumListFrame listFrame,
								 List<PhotoInfo> infos, int currentPosition, String albumName, ITongJi iTongJi) {
		super(iAlbumPage.getPageContext(), listFrame, infos, currentPosition, iTongJi);

		mAlbumName = albumName;

		mIAlbumPage = iAlbumPage;

		mListFrame = listFrame;

		mFolderId = infos.get(0).getFolderId();

		initUserIdAndToken();
	}

	@Override
	protected void init() {
		super.init();
		mWaitAnimDialog = new WaitAnimDialog((Activity)mContext);
	}

	@Override
	protected void onDownload(PhotoInfo photoInfo) {
		TransportImgs.sCloseTip = false;
		T.showShort(mContext, cn.poco.cloudalbumlibs.R.string.cloud_album_start_download);
		List<PhotoInfo> infos = new ArrayList<>();
		infos.add(photoInfo);
		TransportImgs.getInstance(mContext).downloadImgs(infos, mAlbumName);
	}

	@Override
	protected void openCloudAlbumMovePhoto(String folderId, String photoId) {

		mIAlbumPage.setMoveCallback(new CloudAlbumPage.OnMoveCallback() {
			@Override
			public void onBack(boolean success) {
				if (success) {
					onMovePhotoSuccess();
				}
			}
		});

		ArrayList<String> urls = new ArrayList<>();
		String url = null;
		for (PhotoInfo info : mItems) {
			if (info.getId().equals(photoId)) {
				url = info.getUrl();
				break;
			}
		}

		urls.add(url);
		mIAlbumPage.openCloudAlbumMovePhotoFrame(mListFrame, folderId, photoId, urls);
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
		back();
		return true;
	}

	@Override
	public void onBack() {
		mIAlbumPage.setMoveCallback(null);
		super.onBack();
		mIAlbumPage.onFrameBack(this);
	}

	@Override
	protected void deletePhotoSuccess(int position) {
		super.deletePhotoSuccess(position);
		// 通知首页改变
		mIAlbumPage.updateFolderFrame(mFolderId);

		if (mItems.isEmpty()) {
			back();
		}
	}

	@Override
	protected void initAppBar(RelativeLayout appBar, ImageView backView, TextView titleView) {
		super.initAppBar(appBar, backView, titleView);
		ImageUtils.AddSkin(mContext, backView);
	}

	@Override
	protected void initBottomBar(RelativeLayout bottomBar, ImageView deleteView, TextView moveView, ImageView downloadView) {
		super.initBottomBar(bottomBar, deleteView, moveView, downloadView);
		moveView.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		ImageUtils.AddSkin(mContext, deleteView);
		ImageUtils.AddSkin(mContext, downloadView);
	}

	@Override
	protected void addSkin(ImageView imageView) {
		super.addSkin(imageView);
		ImageUtils.AddSkin(mContext, imageView);
	}

	@Override
	protected void showProgressDialog() {
		mWaitAnimDialog.show();
	}

	@Override
	protected void hideProgressDialog() {
		mWaitAnimDialog.dismiss();
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
