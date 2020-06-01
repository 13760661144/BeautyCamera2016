package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudalbumlibs.AbsCreateAlbumFrame;
import cn.poco.cloudalbumlibs.controller.CloudAlbumController;
import cn.poco.cloudalbumlibs.controller.NotificationCenter;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.view.ActionBar;
import cn.poco.cloudalbumlibs.view.widget.NotificationDialog;
import cn.poco.credits.Credit;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.cloudalbumlibs.AbsAlbumListFrame.CLICK_INTERVAL;
import static cn.poco.cloudalbumlibs.BaseCreateAlbumFrame.Route.EDIT_ALBUM;

/**
 * Created by admin on 2016/9/13.
 */
public class CreateCloudAlbumFrame extends AbsCreateAlbumFrame implements NotificationCenter.NotificationDelegate, cn.poco.cloudAlbum.CloudAlbumPage.IFrame{
    private Context mContext;
    private IAlbumPage mIAlbumPage;
    private AppInterface mAppInterface;


    private String albumNameToCreate;
    private String originAlbumName;
    private List<String> mAlbumNameList = new ArrayList<>();
    private FolderInfo mCurrentFolderInfo;

    private WaitAnimDialog mDialog;

    private final String[] albumNameArray = new String[]{getResources().getString(R.string.shortcut_travel), getResources().getString(R.string.shortcut_delicous_food),
            getResources().getString(R.string.shortcut_selfish), getResources().getString(R.string.shortcut_family), getResources().getString(R.string.shortcut_baby), getResources().getString(R.string.shortcut_pets)};
    private long mLastClickTime = 0;

    private boolean nextStepPressed;
    private boolean mIsCreateNewAlbum = true;


    // 进入创建相册页
    public CreateCloudAlbumFrame(Context context, Route state, IAlbumPage iAlbum, AppInterface appInterface) {
        super(context, state);
        this.mContext = context;
        this.mIAlbumPage = iAlbum;
        this.mAppInterface = appInterface;
        this.mCurrentFolderInfo = new FolderInfo();
        this.mMode = state;
        fillExistAlbumNameList();
        initialize();
        StatisticHelper.countPageEnter(context, context.getString(R.string.云相册_新建相册));
    }

    // 通过相册内页的相册编辑进入
    public CreateCloudAlbumFrame(Context context, Route state, FolderInfo folderInfo, IAlbumPage iAlbum, AppInterface appInterface) {
        super(context, state, folderInfo);
        mIsCreateNewAlbum = false;
        this.mContext = context;
        this.mMode = state;
        this.mIAlbumPage = iAlbum;
        this.mAppInterface = appInterface;
        this.mCurrentFolderInfo = folderInfo.clone();
        this.originAlbumName = folderInfo.getName();
        fillExistAlbumNameList();
        initialize();
        StatisticHelper.countPageEnter(context, context.getString(R.string.云相册_编辑相册));
    }

    /**
     * 记录下首页已经占用的相册名字
     */
    private void fillExistAlbumNameList() {
        mAlbumNameList.clear();
        for (FolderInfo item : mIAlbumPage.getFolderInfos()) {
            if (!item.getName().equals(originAlbumName)) {
                mAlbumNameList.add(item.getName());
            }
        }
    }

    @Override
    public void onResume() {
        if (mIsCreateNewAlbum) {
            StatisticHelper.countPageResume(mContext, mContext.getString(R.string.云相册_新建相册));
        } else {
            StatisticHelper.countPageResume(mContext, mContext.getString(R.string.云相册_编辑相册));
        }

    }

    @Override
    public void onPause() {
        if (mIsCreateNewAlbum) {
            StatisticHelper.countPagePause(mContext, mContext.getString(R.string.云相册_新建相册));
        } else {
            StatisticHelper.countPagePause(mContext, mContext.getString(R.string.云相册_编辑相册));
        }
    }

    @Override
    public void onClose() {
        if (mIsCreateNewAlbum) {
            StatisticHelper.countPageLeave(mContext, mContext.getString(R.string.云相册_新建相册));
        } else {
            StatisticHelper.countPageLeave(mContext, mContext.getString(R.string.云相册_编辑相册));
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        setShortCutAlbumNameList(Arrays.asList(albumNameArray));
        createView(getContext());
    }

    @Override
    public void createView(final Context context) {
        super.createView(context);
        actionBar.setOnActionbarMenuItemClick(new ActionBar.onActionbarMenuItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == ActionBar.LEFT_MENU_ITEM_CLICK) {
                    mAppInterface.goBackFromCreateAlbum(context);
                    mIAlbumPage.setOnCreateAlbumCallback(null);
                    clearView();
                } else if (id == ActionBar.RIGHT_MENU_ITEM_CLICK) {
                    if(mMode != EDIT_ALBUM) {
                        mAppInterface.createAlbum(context);
                    }
                    fillExistAlbumNameList();
                    hideKeyboard();
                    albumNameToCreate = mCreateAlbumEditText.getText().toString().trim();
                    if (albumNameToCreate.length() > 0) {
                        if (mAlbumNameList.contains(albumNameToCreate)) {
                            Toast.makeText(mContext, getResources().getString(R.string.album_name_taken), Toast.LENGTH_SHORT).show();
                        } else if (Arrays.asList(albumNameArray).contains(albumNameToCreate)) {
                            String categoryId = getCatagoryId(albumNameToCreate);
                            if (mMode != EDIT_ALBUM) {
                                if (nextStepPressed) {
                                    return;
                                }
                                nextStepPressed = true;
                                mDialog.show();
                                createCloudAlbumOnServer(albumNameToCreate,categoryId);
                            } else if (mMode == EDIT_ALBUM) {
                                String currentAlbumName = mCreateAlbumEditText.getText().toString();
                                if (!currentAlbumName.equals(originAlbumName)) {
                                    FolderInfo folderInfoCopy = new FolderInfo();
                                    folderInfoCopy.setCategoryId(getCatagoryId(currentAlbumName));
                                    folderInfoCopy.setPhotoCount(mCurrentFolderInfo.getPhotoCount());
                                    folderInfoCopy.setName(currentAlbumName);
                                    folderInfoCopy.setCreatedTime(mCurrentFolderInfo.getCreatedTime());
                                    folderInfoCopy.setUpdatedTime(mCurrentFolderInfo.getUpdatedTime());
                                    folderInfoCopy.setFolderId(mCurrentFolderInfo.getFolderId());
                                    mIAlbumPage.openAlbumCategoryFrame(folderInfoCopy, EDIT_ALBUM);
                                } else {
                                    mCurrentFolderInfo.setName(albumNameToCreate);
                                    mIAlbumPage.openAlbumCategoryFrame(mCurrentFolderInfo, EDIT_ALBUM);
                                }
                            }
                        } else {
                            mCurrentFolderInfo.setName(albumNameToCreate);
                            mIAlbumPage.openAlbumCategoryFrame(mCurrentFolderInfo, mMode);
                        }
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.album_name_can_not_empty), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 调整actionbar右边按钮的位置
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBar.getRightTextBtn().getLayoutParams();
        layoutParams.rightMargin = layoutParams.rightMargin + ShareData.PxToDpi_xhdpi(4);

        // 调整删除相册名字按钮的位置
        layoutParams = (FrameLayout.LayoutParams) mClearAlbumNameBtn.getLayoutParams();
        layoutParams.rightMargin = layoutParams.rightMargin + ShareData.PxToDpi_xhdpi(4);

        this.setAbsCreateAlbumFrameDelegate(new AbsCreateAlbumFrameDelegate() {
            @Override
            public void onInputAlbumNameOutOfBound() {

            }

            @Override
            public void onAlbumNameShortCutClick() {
                mAppInterface.createNameByShortCut(context);
            }
        });

        addNotification();
        addSkin();
    }


    // 更新界面
    @Override
    public void updateView() {
        super.updateView();
    }

    /**
     * 释放资源
     */
    @Override
    protected void clearView() {
        super.clearView();
        removeNotification();
        mIAlbumPage.onFrameBack(this);
        if (notificationDialogView != null && notificationDialogView.isShowing()) {
            notificationDialogView.dismiss();
        }
    }

    /**
     * 这个页面接收消息通知的唯一入口
     * @param id 定义的事件id
     * @param args 分发回来的参数
     */
    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                nextStepPressed = false;
                if (id == NotificationCenter.CREATE_ALBUM_SUCCESS) {
                    if (args.length > 1) {
                        final FolderInfo folderInfoArg = (FolderInfo) args[0];
                        int from = (int)args[1];
                        if (from == CloudAlbumController.CREATE_ALBUM) {
                            mDialog.dismiss();
                            creditIncome();
                            mIAlbumPage.updateFolderFrameAfterCreateAlbum(folderInfoArg);
                            onResponseToCreateAlbumSuccessfully(folderInfoArg);
                        }
                    }
                } else if (id == NotificationCenter.CREATE_ALBUM_FAULURE) {
                    if (args.length > 0) {
                        final int from = (int)args[0];
                        if (from  == 1) {
                            Toast.makeText(CreateCloudAlbumFrame.this.getContext(), getResources().getString(R.string.fali_to_craete_album), Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    //添加消息通知
    private void addNotification() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.CREATE_ALBUM_SUCCESS);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.CREATE_ALBUM_FAULURE);
    }

    //移除消息通知
    private void removeNotification() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.CREATE_ALBUM_SUCCESS);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.CREATE_ALBUM_FAULURE);
    }

    private void onResponseToCreateAlbumSuccessfully(final FolderInfo folderInfo) {
        switch (mMode) {
            case CREATE_NEW_ALBUM: {
                String content = getResources().getString(R.string.succeed_creating_album) + System.getProperty("line.separator") + getResources().getString(R.string.upload_photoes_rightnow);
                String cancelText = getResources().getString(R.string.go_back_home_page);
                String confirmText = getResources().getString(R.string.good);

                OnAnimationClickListener animationClickListener = new OnAnimationClickListener() {
                    @Override
                    public void onAnimationClick(View v) {
                        if (v == notificationDialogView.cancelBtn) {
                            clearView();
                        } else if (v == notificationDialogView.confirmBtnCell) {
                            if (mLastClickTime == 0 || mLastClickTime + CLICK_INTERVAL < System.currentTimeMillis()) {
                                mLastClickTime = System.currentTimeMillis();
                                mIAlbumPage.openCloudAlbumListFrame(folderInfo, true);
                                clearView();
                            };
                        }
                    }

                    @Override
                    public void onTouch(View v) {

                    }

                    @Override
                    public void onRelease(View v) {
                    }
                };

                NotificationDialog.DialogConfiguration configuration = new NotificationDialog.DialogConfiguration();
                configuration.mNotificationText = content;
                configuration.mConfirmText = confirmText;
                configuration.mCancelText = cancelText;
                configuration.mConfirmTouchListener = animationClickListener;
                configuration.mCancelTouchListener = animationClickListener;
                configuration.mWidth = ShareData.PxToDpi_xhdpi(568);
                configuration.mHeight = ShareData.PxToDpi_xhdpi(358);
                showNotificationDialog(configuration);
                notificationDialogView.confirmBtnCell.addSkin(ImageUtils.GetSkinColor(Color.RED));
                break;
            }

            case CREATE_NEW_ALBUM_UPLOAD_PHOTO_INNER:
                mIAlbumPage.clearCreateFolderSiteFrame();
                mIAlbumPage.notifyCreateAlbumCompleted(folderInfo.folderId);
                break;
            case CREATE_NEW_ALBUM_UPLOAD_PHOTO: {
                mIAlbumPage.clearSiteFrame();
                mIAlbumPage.openCloudAlbumListFrame(folderInfo, true);
                break;
            }

            default: {

            }
        }
    }

    //在服务器上创建相册，用于创建相册模式时
    private void createCloudAlbumOnServer(String albumName, String catogoryId) {
        FolderInfo album = new FolderInfo();
        album.setName(albumName);
        album.setPhotoCount(0 + "");
        album.setCoverImgUrl("");
        album.setCategoryId(catogoryId);
        CloudAlbumController.getInstacne().createCloudAlbum(mUserId, mAccessToken, album, mAppInterface, CloudAlbumController.CREATE_ALBUM);
    }


    // 实现快速创建相册的名字和相册Id的绑定。
    private String getCatagoryId(String name) {
        String id = null;
        for (int i = 0; i < albumNameArray.length; i++) {
            if (albumNameArray[i].equals(name)) {
                if (name.equals(getResources().getString(R.string.shortcut_travel))) {
                    id = "1";
                } else if (name.equals(getResources().getString(R.string.shortcut_delicous_food))) {
                    id = "2";
                } else if (name.equals(getResources().getString(R.string.shortcut_selfish)) || name.equals(getResources().getString(R.string.shortcut_family))) {
                    id = "4";
                } else if (name.equals(getResources().getString(R.string.shortcut_baby))) {
                    id = "3";
                } else if (name.equals(getResources().getString(R.string.shortcut_pets))) {
                    id = "5";
                }
                break;
            }
        }
        return id;
    }

    public void creditIncome() {
        if (null == TagMgr.GetTagValue(getContext(), "云相册首次新建文件夹")) {
            Credit.CreditIncome(getContext(), String.valueOf(getContext().getResources().getInteger(R.integer.积分_云相册新建文件夹)));
            TagMgr.SetTagValue(getContext(), "云相册首次新建文件夹", "yes");
        }
    }

    @Override
    public boolean onBackPress() {
        clearView();
        return true;
    }

    @Override
    protected String getUserId() {
        return mIAlbumPage.getUserId();
    }

    @Override
    protected String getAccessToken() {
        return mIAlbumPage.getAccessToken();
    }

    private void addSkin() {
        ImageUtils.AddSkin(mContext, actionBar.getLeftImageBtn());
        actionBar.getRightTextBtn().setTextColor(ImageUtils.GetSkinColor(actionBar.getRightTextBtn().getCurrentTextColor()));
        setCursorDrawableColor(mCreateAlbumEditText, ImageUtils.GetSkinColor());
    }

    @Override
    protected void initDialog() {
        mDialog = new WaitAnimDialog((Activity) getContext());
        mDialog.setCancelable(false);
    }
}
