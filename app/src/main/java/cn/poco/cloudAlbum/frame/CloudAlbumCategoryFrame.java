package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudalbumlibs.AbsAlbumCategoryFrame;
import cn.poco.cloudalbumlibs.controller.CloudAlbumController;
import cn.poco.cloudalbumlibs.controller.NotificationCenter;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.view.ActionBar;
import cn.poco.cloudalbumlibs.view.CategorySelectLayout;
import cn.poco.cloudalbumlibs.view.widget.NotificationDialog;
import cn.poco.credits.Credit;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.cloudalbumlibs.AbsAlbumListFrame.CLICK_INTERVAL;
import static cn.poco.cloudalbumlibs.BaseCreateAlbumFrame.Route.EDIT_ALBUM;

/**
 * Created by admin on 2016/9/14.
 */
public class CloudAlbumCategoryFrame extends AbsAlbumCategoryFrame implements NotificationCenter.NotificationDelegate, cn.poco.cloudAlbum.CloudAlbumPage.IFrame{
    private Context mContext;
    private IAlbumPage mIAlbumPage;
    private AppInterface mAppInterface;

    private CategorySelectLayout.CategoryItemCell mCurrentSelectedCell;
    private FolderInfo mFolderInfo;
    private Route mMode;

    private long mLastClickTime = 0;
    private boolean createButtonPressed;

    private WaitAnimDialog mProgressDialog;


    public CloudAlbumCategoryFrame(Context context, Route state, FolderInfo folderInfo, IAlbumPage iAlbum, AppInterface appInterface) {
        super(context, state);
        this.mContext = context;
        this.mIAlbumPage = iAlbum;
        this.mFolderInfo = folderInfo;
        this.mAppInterface = appInterface;
        this.mMode = state;
        initialize();
        StatisticHelper.countPageEnter(mContext, mContext.getString(R.string.云相册_相册分类));
    }

    @Override
    public void onResume() {
        StatisticHelper.countPageResume(mContext, mContext.getString(R.string.云相册_相册分类));
    }

    @Override
    public void onPause() {
        StatisticHelper.countPagePause(mContext, mContext.getString(R.string.云相册_相册分类));
    }

    @Override
    public void onClose() {
        StatisticHelper.countPageLeave(mContext, mContext.getString(R.string.云相册_相册分类));
    }

    @Override
    protected void initialize() {
        super.initialize();
        createView(getContext());
    }

    @Override
    public void createView(Context context) {
        super.createView(context);
        if (mMode == EDIT_ALBUM && !TextUtils.isEmpty(mFolderInfo.getCategoryId())) {
            setSelectCategoryCell(mFolderInfo.getCategoryId());
        }
        this.setAbsAlbumCategoryFrameDelegate(new AbsAlbumCategoryFrameDelegate() {
            @Override
            public void onCategoryLayoutSelected(CategorySelectLayout.CategoryItemCell cell) {

            }
        });
        addNotification();
        addSkin();
    }

    @Override
    protected ActionBar initActionbar(ActionBar titleActionbar) {
        ActionBar actionBar = super.initActionbar(titleActionbar);
        actionBar.setOnActionbarMenuItemClick(new ActionBar.onActionbarMenuItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == ActionBar.LEFT_MENU_ITEM_CLICK) {
                    clearView();
                } else if (id == ActionBar.RIGHT_MENU_ITEM_CLICK) {
                    if (createButtonPressed) {
                        return;
                    }
                    createButtonPressed = true;
                    mAppInterface.createNewAlbum(mContext);
                    mCurrentSelectedCell = mAlbumCategoryLayout.getCurrentSelectCategoryCell();
                    if(mCurrentSelectedCell == null) {
                        Toast.makeText(mContext, getResources().getString(R.string.not_select_category_yet), Toast.LENGTH_SHORT).show();
                    } else {
                        switch (mMode) {
                            case CREATE_NEW_ALBUM_UPLOAD_PHOTO:
                            case CREATE_NEW_ALBUM_UPLOAD_PHOTO_INNER:
                            case CREATE_NEW_ALBUM: {
                                mProgressDialog.show();
                                boolean exist = checkExistSameAlbum(mFolderInfo.getName(), mCurrentSelectedCell.getCorrespondingCategoryId());
                                if (!exist) {
                                    createCloudAlbumOnServer(mCurrentSelectedCell.getCorrespondingCategoryId());
                                } else {
                                    mProgressDialog.dismiss();
                                    createButtonPressed = false;
                                    Toast.makeText(mContext, getResources().getString(R.string.same_name_album_exist), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case EDIT_ALBUM: {
                                mProgressDialog.show();
                                boolean exist = checkExistSameAlbum(mFolderInfo.getName(), mCurrentSelectedCell.getCorrespondingCategoryId());
                                if (!exist) {
                                    updateAlbumFolderOnServer(mCurrentSelectedCell.getCorrespondingCategoryId());
                                } else {
                                    mProgressDialog.dismiss();
                                    createButtonPressed = false;
                                    Toast.makeText(mContext, getResources().getString(R.string.album_exist), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            default:
                        }
                    }
                }
            }
        });
        return actionBar;
    }


    @Override
    public void updateView() {
        super.updateView();
    }

    @Override
    protected void clearView() {
        super.clearView();
        removeNotification();
        mIAlbumPage.onFrameBack(this);
        if (notificationDialogView != null && notificationDialogView.isShowing()) {
            notificationDialogView.dismiss();
        }
    }

    @Override
    public boolean onBackPress() {
        clearView();
        return true;
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        mHandler.post(new Runnable()
        {
            @Override
            public void run() {
                createButtonPressed = false;
                mProgressDialog.dismiss();
                if (id == NotificationCenter.CREATE_ALBUM_SUCCESS) {
                    if (args.length > 1) {
                        final FolderInfo album = (FolderInfo) args[0];
                        int from = (int)args[1];
                        if (from == CloudAlbumController.CREATE_ALBUM_CATEGORY) {
                            creditIncome();
                            if (album != null) {
                                mIAlbumPage.updateFolderFrameAfterCreateAlbum(album);
                                onCreateAlbumSuccessfully(mMode, album);
                            }
                        }
                    }
                } else if (id == NotificationCenter.CREATE_ALBUM_FAULURE) {
                    if (args.length > 0) {
                        int from = (int)args[0];
                        if (from == CloudAlbumController.CREATE_ALBUM_CATEGORY) {
                            Toast.makeText(mContext, getResources().getString(R.string.fali_to_craete_album), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (id == NotificationCenter.UPDATE_ALBUM_SUCCESS) {
                    if (args.length > 0) {
                        FolderInfo album = (FolderInfo) args[0];
                        if (album != null) {
                            mIAlbumPage.switchToAlbumInnerFrame();
                            mIAlbumPage.updateFolderFrameAfterRenameAlbum(album);
                            mIAlbumPage.updateInfoAfterEdit(album);
                            Toast.makeText(mContext, getResources().getString(R.string.succeed_save), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else if (id == NotificationCenter.UPDATE_ALBUM_FAILURE) {
                    Toast.makeText(mContext, getResources().getString(R.string.fail_to_save), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void initDialog() {
        mProgressDialog = new WaitAnimDialog((Activity) getContext());
        mProgressDialog.setCancelable(false);
    }

    public void updateAlbumFolderOnServer(final String categoryId)
    {
        mFolderInfo.setCategoryId(categoryId);
        CloudAlbumController.getInstacne().updateCloudAlbum(mUserId, mAccessToken, mFolderInfo, mAppInterface);
    }

    public void createCloudAlbumOnServer(final String categoryId)
    {
        FolderInfo album = new FolderInfo();
        album.setName(mFolderInfo.getName());
        album.setCategoryId(categoryId);
        album.setPhotoCount(String.valueOf(0));
        album.setCoverImgUrl("");
        CloudAlbumController.getInstacne().createCloudAlbum(mUserId, mAccessToken, album, mAppInterface, CloudAlbumController.CREATE_ALBUM_CATEGORY);
    }


    private void addNotification() {
        if(mMode != EDIT_ALBUM){
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.CREATE_ALBUM_SUCCESS);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.CREATE_ALBUM_FAULURE);
        }else if (mMode == EDIT_ALBUM){
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.UPDATE_ALBUM_SUCCESS);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.UPDATE_ALBUM_FAILURE);
        }
    }

    private void removeNotification()
    {
        if(mMode != EDIT_ALBUM) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.CREATE_ALBUM_SUCCESS);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.CREATE_ALBUM_FAULURE);
        }else if (mMode == EDIT_ALBUM){
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.UPDATE_ALBUM_SUCCESS);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.UPDATE_ALBUM_FAILURE);
        }
    }

    private void setSelectCategoryCell(final String id)
    {
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CloudAlbumCategoryFrame.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mAlbumCategoryLayout.setCurrentSelectCategoryCell(id);
            }
        });
    }

    public void creditIncome()
    {
        if(null == TagMgr.GetTagValue(getContext(), "云相册首次新建文件夹"))
        {
            Credit.CreditIncome(getContext(), String.valueOf(getContext().getResources().getInteger(R.integer.积分_云相册新建文件夹)));

            TagMgr.SetTagValue(getContext(), "云相册首次新建文件夹", "yes");
        }
    }

    public void onCreateAlbumSuccessfully(Route state, FolderInfo album) {
        switch (state) {
            case CREATE_NEW_ALBUM: {
                showChooseDialog(album);
                break;
            }

            case CREATE_NEW_ALBUM_UPLOAD_PHOTO:
                mIAlbumPage.clearSiteFrame();
                mIAlbumPage.openCloudAlbumListFrame(album, true);
                break;
            case CREATE_NEW_ALBUM_UPLOAD_PHOTO_INNER: {
                mIAlbumPage.clearCreateFolderSiteFrame();
                mIAlbumPage.notifyCreateAlbumCompleted(album.folderId);
                break;
            }

            default:
        }
    }


    private void showChooseDialog(final FolderInfo album) {
        String content = getResources().getString(R.string.succeed_creating_album) + System.getProperty("line.separator") + getResources().getString(R.string.upload_photoes_rightnow);
        String cancelText = getResources().getString(R.string.go_back_home_page);
        String confirmText = getResources().getString(R.string.good);

        OnAnimationClickListener animationClickListener = new OnAnimationClickListener() {
            @Override
            public void onAnimationClick(View v) {
                if (v == notificationDialogView.cancelBtn) {
                    notificationDialogView.dismiss();
                    mIAlbumPage.clearSiteFrame();
                } else if (v == notificationDialogView.confirmBtnCell) {
                    if (mLastClickTime == 0 || mLastClickTime + CLICK_INTERVAL < System.currentTimeMillis()) {
                        mLastClickTime = System.currentTimeMillis();
                        notificationDialogView.dismiss();
                        mIAlbumPage.clearSiteFrame();
                        mIAlbumPage.openCloudAlbumListFrame(album, true);
                    }
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
        showNotificationDialog(configuration);
        notificationDialogView.confirmBtnCell.addSkin(ImageUtils.GetSkinColor(Color.RED));
    }




    @Override
    protected String getUserId() {
        return mIAlbumPage.getUserId();
    }

    @Override
    protected String getAccessToken() {
        return mIAlbumPage.getAccessToken();
    }

    private boolean checkExistSameAlbum(String name, String categoryId) {
        for (FolderInfo info : mIAlbumPage.getFolderInfos()) {
            if (mMode != EDIT_ALBUM && info.getName().equals(name)) {
                return true;
            } else if (mMode == EDIT_ALBUM && info.getName().equals(name) && info.getCategoryId().equals(categoryId)){
                return true;
            }
        }
        return false;
    }

    private void addSkin() {
        ImageUtils.AddSkin(mContext, actionBar.getLeftImageBtn());
        actionBar.getRightTextBtn().setTextColor(ImageUtils.GetSkinColor(actionBar.getRightTextBtn().getCurrentTextColor()));
        for (int i = 0; i < mAlbumCategoryLayout.getChildCount(); i++) {
            CategorySelectLayout.CategoryItemCell currentCell = (CategorySelectLayout.CategoryItemCell)mAlbumCategoryLayout.getChildAt(i);
            ImageUtils.AddSkin(mContext, currentCell.getLeftImageIcon());
            ImageUtils.AddSkin(mContext, currentCell.getRightImageIcon());
        }
    }
}
