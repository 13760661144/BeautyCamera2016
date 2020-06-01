package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudalbumlibs.AbsAlbumListFrame;
import cn.poco.cloudalbumlibs.AbsAlbumMoveFrame;
import cn.poco.cloudalbumlibs.BaseCreateAlbumFrame;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.statistics.TongJiUtils;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/9/13
 */
public class CloudAlbumMoveFrame extends AbsAlbumMoveFrame implements CloudAlbumPage.IFrame {

	private IAlbumPage mIAlbumPage;

	private List<String> mUrls;

	private String mFolderId;

	private WaitAnimDialog mWaitAnimDialog;

	public CloudAlbumMoveFrame(IAlbumPage iAlbumPage, AbsAlbumListFrame listFrame, String folderId, String photoIds, List<String> urls) {
		super(iAlbumPage.getPageContext(), listFrame, folderId, photoIds);
		TongJiUtils.onPageStart(getContext(), R.string.云相册_移动_选择相册);

		mIAlbumPage = iAlbumPage;
		mUrls = urls;

		mFolderId = folderId;

		initUserIdAndToken();
		initViews();
	}

	public CloudAlbumMoveFrame(IAlbumPage iAlbumPage) {
		super(iAlbumPage.getPageContext());

		TongJiUtils.onPageStart(getContext(), R.string.云相册_上传_选择相册);
		mIAlbumPage = iAlbumPage;
		initViews();
	}

	@Override
	protected void init() {
		super.init();
		mWaitAnimDialog = new WaitAnimDialog((Activity)mContext);
	}

	@Override
	protected void movePhotoSuccess(String oldFolderId, String newFolderId, int moveCount) {
		super.movePhotoSuccess(oldFolderId, newFolderId, moveCount);

		// 通知首页改变
		mIAlbumPage.updateFolderFrame(mFolderId);

		mIAlbumPage.notifyMoveCompleted(true);

		back();
	}

	@Override
	protected void uploadImage(Context context, FolderInfo folderInfo) {
		// 先清除本页面，然后跳到相应的相册内部进行选图上传
		back();
		mIAlbumPage.openCloudAlbumListFrame(folderInfo, true);
	}

	@Override
	protected void createAlbum(Context context, int type) {
		// 进入新建相册页面
		if (type == MOVE) {
			mIAlbumPage.setOnCreateAlbumCallback(new CloudAlbumPage.OnCreateAlbumCallback() {
				@Override
				public void onCreateCompleted(String folderId) {
					movePhoto(mFolderId, folderId, mPhotoIds);
				}
			});
			mIAlbumPage.openCreateAlbumFrame(BaseCreateAlbumFrame.Route.CREATE_NEW_ALBUM_UPLOAD_PHOTO_INNER);
		} else if (type == UPLOAD) {
			mIAlbumPage.openCreateAlbumFrame(BaseCreateAlbumFrame.Route.CREATE_NEW_ALBUM_UPLOAD_PHOTO);
		}
	}

	@Override
	protected List<FolderInfo> getFolderInfos() {
		return mIAlbumPage.getFolderInfos();
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
	protected void onBack() {
		mIAlbumPage.setOnCreateAlbumCallback(null);
		super.onBack();
		mIAlbumPage.onFrameBack(this);
	}

	@Override
	protected void initAppBar(RelativeLayout appBar, ImageView backView, TextView titleView) {
		super.initAppBar(appBar, backView, titleView);
		ImageUtils.AddSkin(mContext, backView);
	}

	@Override
	protected void addSkin(ImageView imageView) {
		super.addSkin(imageView);
		ImageUtils.AddSkin(mContext, imageView);
	}

	@Override
	protected void changeColor(TextView textView) {
		textView.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
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
		if (mType == MOVE) {
			TongJiUtils.onPageResume(getContext(), R.string.云相册_移动_选择相册);
		} else if (mType == UPLOAD) {
			TongJiUtils.onPageResume(getContext(), R.string.云相册_上传_选择相册);
		}
	}

	@Override
	public void onPause() {
		if (mType == MOVE) {
			TongJiUtils.onPagePause(getContext(), R.string.云相册_移动_选择相册);
		} else if (mType == UPLOAD) {
			TongJiUtils.onPagePause(getContext(), R.string.云相册_上传_选择相册);
		}
	}

	@Override
	public void onClose() {
		if (mType == MOVE) {
			TongJiUtils.onPageEnd(getContext(), R.string.云相册_移动_选择相册);
		} else if (mType == UPLOAD) {
			TongJiUtils.onPageEnd(getContext(), R.string.云相册_上传_选择相册);
		}
	}
}
