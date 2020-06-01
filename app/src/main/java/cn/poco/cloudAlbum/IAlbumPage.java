package cn.poco.cloudAlbum;

import android.content.Context;
import android.view.View;

import java.util.List;

import cn.poco.cloudAlbum.site.CloudAlbumPageSite;
import cn.poco.cloudalbumlibs.AbsAlbumListFrame;
import cn.poco.cloudalbumlibs.BaseCreateAlbumFrame;
import cn.poco.cloudalbumlibs.api.IAlbum;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.model.PhotoInfo;

/**
 * Created by: fwc
 * Date: 2016/9/13
 */
public interface IAlbumPage {

	String getUserId();

	String getAccessToken();

	IAlbum getIAlbum();

	Context getPageContext();

	CloudAlbumPageSite getSite();

	void setUploadCallback(CloudAlbumPage.UploadCallBack uploadCallback);

	void setFolderInfos(List<FolderInfo> infos);

	List<FolderInfo> getFolderInfos();

	void updateInfoAfterEdit(FolderInfo folderInfo);

	void onFrameBack(View frame);

	void openCloudAlbumListFrame(FolderInfo folderInfo, boolean openSelPhoto);

	void openCloudAlbumEditFrame(FolderInfo info);

	void openCloudAlbumBigPhotoFrame(AbsAlbumListFrame listFrame, List<PhotoInfo> photoInfos, int position, String albumName);

	void openCloudAlbumMovePhotoFrame(AbsAlbumListFrame listFrame, String folderId, String photoIds, List<String> urls);

	void openSelectAlbumFrame();

	void openCloudAlbumTransportFrame(boolean download);


	void openCloudAlbumFolderFrame();


	void openCreateAlbumFrame(BaseCreateAlbumFrame.Route route);

	void openAlbumCategoryFrame(FolderInfo folderInfo, BaseCreateAlbumFrame.Route mode);

	void switchToAlbumInnerFrame();

	void openCloudAlbumSettingFrame();

	void updateFolderFrameAfterDeleteAlbum(String folderId);

	void updateFolderFrameAfterRenameAlbum(FolderInfo folderInfo);

	void updateFolderFrameAfterCreateAlbum(FolderInfo folderInfo);

	void updateFolderFrame(String folderId);

	/**
	 * 清除CLoudAlbumFolderFrame以外的页面
	 */
	void clearSiteFrame();

	void clearCreateFolderSiteFrame();

	void setMoveCallback(CloudAlbumPage.OnMoveCallback callback);

	void notifyMoveCompleted(boolean success);

	void setOnCreateAlbumCallback(CloudAlbumPage.OnCreateAlbumCallback callback);

	void notifyCreateAlbumCompleted(String folderId);

	long getFreeVolume();
}
