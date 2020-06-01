package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.cloudAlbum.adapter.CloudAlbumFolderAdapter;
import cn.poco.cloudAlbum.site.CloudAlbumPageSite;
import cn.poco.cloudalbumlibs.AbsAlbumFolderFrame;
import cn.poco.cloudalbumlibs.BaseCreateAlbumFrame;
import cn.poco.cloudalbumlibs.controller.CloudAlbumController;
import cn.poco.cloudalbumlibs.controller.NotificationCenter;
import cn.poco.cloudalbumlibs.model.CloudStorageItem;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import cn.poco.cloudalbumlibs.view.ActionBar;
import cn.poco.cloudalbumlibs.view.widget.NotificationDialog;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.advanced.ImageUtils.AddSkin;

/**
 * Created by admin on 2016/9/13.
 */
public class CloudAlbumFolderFrame extends AbsAlbumFolderFrame implements NotificationCenter.NotificationDelegate, CloudAlbumPage.IFrame{
    private Context mContext;
    private IAlbumPage mIAlbumPage;
    private AppInterface mAppInterface;

    private int mTipType;

    private ArrayList<FolderInfo> mCoverList = new ArrayList<>();
    private CloudAlbumFolderAdapter mAdapter;

    private TransportImgs transportImgs;

    private WaitAnimDialog mProgressDialog;

    public CloudAlbumFolderFrame(Context context, IAlbumPage iAlbum, AppInterface appInterface) {
        super(context);
        this.mContext = context;
        this.mIAlbumPage = iAlbum;
        this.mAppInterface = appInterface;
        initialize();
        StatisticHelper.countPageEnter(context, context.getString(R.string.云相册));
    }

    @Override
    public void onResume() {
        StatisticHelper.countPageResume(mContext, mContext.getString(R.string.云相册));
    }

    @Override
    public void onPause() {
        StatisticHelper.countPagePause(mContext, mContext.getString(R.string.云相册));
    }

    @Override
    public void onClose() {
        StatisticHelper.countPageLeave(mContext, mContext.getString(R.string.云相册));
    }

    @Override
    protected void initialize() {
        super.initialize();
        mAdapter = new CloudAlbumFolderAdapter(mContext, mCoverList);
        createView(getContext());
    }

    @Override
    public void createView(final Context context) {
        super.createView(context);
        String keyWord = getResources().getString(R.string.succeed_do_something);
        transportImgs = TransportImgs.getInstance(context);
        TransportImgs.BarInfo barInfo = transportImgs.getBarInfo();
        if (TransportImgs.getInstance(context).isShowBar()) {
            mTipType = barInfo.type;
            mProgressBarContainer.setVisibility(View.VISIBLE);
            setUpProgressBar(barInfo.type, barInfo.message);
            if (barInfo.message.contains(keyWord)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBarContainer.setVisibility(View.GONE);
                        TransportImgs.sCloseTip = true;
                        mSwipeRefreshLayout.requestLayout();
                    }
                }, 5000);
            }
        }

        mGridView.setAdapter(mAdapter);
        actionBar.setOnActionbarMenuItemClick(new ActionBar.onActionbarMenuItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == ActionBar.LEFT_MENU_ITEM_CLICK) {
                    if (mIAlbumPage instanceof CloudAlbumPage) {
                        CloudAlbumPage cloudAlbumPage = (CloudAlbumPage) mIAlbumPage;
                        cloudAlbumPage.onBack();
                    }
                } else if (id == ActionBar.RIGHT_MENU_ITEM_CLICK) {
                    mAppInterface.goToCloudSettingFrame(context);
                    mIAlbumPage.openCloudAlbumSettingFrame();
                }
            }
        });

        this.setAlbusAlbumFolderFrameDelegate(new AbsAlbumFolderFrameDelegate() {
            @Override
            public void onCreateFirstAlbumClick() {
                mAppInterface.createAlbum(context);
                mIAlbumPage.openCreateAlbumFrame(BaseCreateAlbumFrame.Route.CREATE_NEW_ALBUM);
            }

            @Override
            public void onProgressbarClick() {
                boolean download = false;
                if (mTipType > TransportImgs.TYPE_UPLOAD_ERROR) {
                    download = true;
                }
                if ((mTipType & 0x00000022) != 0) {
                    // 等待状态
                    mAppInterface.transportWaitBar(mContext);
                } else if ((mTipType & 0x00000088) != 0) {
                    // 失败状态
                    mAppInterface.transportErrorBar(mContext);
                } else if (mTipType == TransportImgs.TYPE_UPLOAD_PROGRESS) {
                    // 正在上传
                    mAppInterface.uploadingBar(mContext);
                }
                mIAlbumPage.openCloudAlbumTransportFrame(download);
            }

            @Override
            public void onProgressbarCloseBtnClick(RelativeLayout progressBarContainer, SwipeRefreshLayout swipeRefreshLayout) {
                TransportImgs.sCloseTip = true;
                mProgressBarContainer.setVisibility(View.GONE);
                mSwipeRefreshLayout.requestLayout();
            }

            @Override
            public void onFolderItemClick(int position) {
                synchronizeAlbumData();
                if (position == 0) {
                    mIAlbumPage.openCreateAlbumFrame(BaseCreateAlbumFrame.Route.CREATE_NEW_ALBUM);
                } else {
                    mIAlbumPage.openCloudAlbumListFrame(mIAlbumPage.getFolderInfos().get(position - 1), false);
                }
            }

            @Override
            public void onFolderFreshListener() {
                CloudAlbumController.getInstacne().getAlbumFromServer(mUserId, mAccessToken, mAppInterface, true);
            }

            @Override
            public void onDialogCancelListener() {
            }

            @Override
            public void onUpLoadPhotoClickListener() {
                synchronizeAlbumData();
                mIAlbumPage.openSelectAlbumFrame();
            }
        });

        // 修正closebtn的位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCloseBtn.getLayoutParams();
        params.rightMargin += ShareData.PxToDpi_xhdpi(10);

        addNotificationListener();
        CloudAlbumController.getInstacne().getAlbumFromServer(mUserId, mAccessToken, mAppInterface, false);
        final String unit = "GB";
        CloudAlbumController.getInstacne().getCloudStorageInfo(mUserId, mAccessToken, unit, mAppInterface);
        TransportImgs.getInstance(mContext).addOnStateChangeListener(mOnStateChangeListener);
        //设置皮肤颜色
        addSkin();
    }

    @Override
    protected void showProgressDialog() {
        mProgressDialog = new WaitAnimDialog((Activity) this.getContext());
        mProgressDialog.show();
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mIAlbumPage instanceof CloudAlbumPage) {
                    CloudAlbumPage cloudAlbumPage = (CloudAlbumPage) mIAlbumPage;
                    cloudAlbumPage.onBack();
                }
            }
        });

    }

    /**
     * 页面发生变化之后，刷新页面。
     */
    @Override
    public void updateView() {
        super.updateView();
        CloudAlbumController.getInstacne().refreshCloudAlbumFrame(mUserId, mAccessToken, mAppInterface);
    }

    /**
     * 在重命名相册之后，更新首页相应相册信息。
     */
    public void updateAlbumNameAfterRenameAlbum(FolderInfo folderInfo)
    {
        for(FolderInfo item : mCoverList)
        {
            if(item.getFolderId() != null && item.getFolderId().equals(folderInfo.getFolderId()))
            {
                item.setName(folderInfo.getName());
                item.setCategoryId(folderInfo.getCategoryId());
                item.setUpdatedTime(folderInfo.getUpdatedTime());
            }
        }
        mAdapter.updateAlbumList(mCoverList);
    }

    /**
     *在内页删除相册之后，在首页更新页面
     * @param folderInfo
     */
    public void updateAlbumFolderFrameAfterCreateAlbum(FolderInfo folderInfo)
    {
        mCoverList.add(1, folderInfo);
        mAdapter.updateAlbumList(mCoverList);
        synchronizeAlbumData();

        if(mCoverList.size() > 1)
        {
            mEmptyAlbumLayout.setVisibility(View.GONE);
            mContentLayer.setVisibility(View.VISIBLE);
            mUploadBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 在内页删除相册之后, 在首页刷新页面
     * @param folderId 相册文件夹id
     */
    public void updateLayoutAfterDeleteAlbum(String folderId)
    {
        ArrayList<FolderInfo> toRemove = new ArrayList<>();
        for(FolderInfo item : mCoverList)
        {
            if(item.getFolderId() != null && item.getFolderId().equals(folderId))
            {
                toRemove.add(item);
            }
        }
        mCoverList.removeAll(toRemove);
        mAdapter.updateAlbumList(mCoverList);

        // 保持各个页面之间相册信息的一致性。
        synchronizeAlbumData();

        // 根据情况判断是否展示空相册页面。
        if(!(mCoverList.size() > 1)) {
            mContentLayer.setVisibility(View.GONE);
            mUploadBtn.setVisibility(View.GONE);
            mEmptyAlbumLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 清理资源
     */
    @Override
    protected void clearView() {
        super.clearView();
        removeNotificationListener();
        TransportImgs.getInstance(mContext).removeOnStateChangeListener(mOnStateChangeListener);
        mIAlbumPage.onFrameBack(this);
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 用户点击了手机上的返回按钮，离开当前界面，同时清理资源
     * @return
     */
    @Override
    public boolean onBackPress() {
        clearView();
        return false;
    }

    /**
     * 页面接收消息的唯一入口
     * @param id 定义的事件id
     * @param args   分发的参数
     */
    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Object object = null;
                if(args != null && args.length > 0)
                {
                    object = args[0];
                }
                //  进入首页成功获取服务器数据；
                if(id == NotificationCenter.GET_ALBUM_FROM_SERVER_SUCCESSFUL) {
                    addCreteFolderIntoList();
                    if(object != null) {
                        ArrayList<FolderInfo> list = (ArrayList<FolderInfo>)object;
                        mCoverList.addAll(list);
                        if (mCoverList.size() > 1) {
                            mUploadBtn.setVisibility(View.VISIBLE);
                        }
                    }
                    updateUiByPullDownRefresh(false);
                }

                // 进入首页获取服务器数据失败;
                else if(id == NotificationCenter.GET_ALBUM_FROM_SERVER_FAILURE)
                {
                    final Object tag = object;
                    mProgressDialog.dismiss();

                    if(tag != null && ((String)tag).equals("multiUserLogin"))
                    {
                        showReloginDialog();
                    }
                    else
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.unable_to_get_info), Toast.LENGTH_SHORT).show();
                    }
                }

                // 在首页通过刷新，成功获取服务器数据;
                else if(id == NotificationCenter.REFRESH_ALBUM_FROM_SERVER_SUCCESSFUL)
                {
                    addCreteFolderIntoList();
                    if(object != null)
                    {
                        ArrayList<FolderInfo> list = (ArrayList<FolderInfo>)object;
                        mCoverList.addAll(list);
                    }
                    updateUiByPullDownRefresh(true);
                }

                // 在首页通过刷新，获取服务器数据失败;
                else if(id == NotificationCenter.REFRESH_ALBUM_FROM_SERVER_FAILURE)
                {
                    final Object tag = object;
                    mSwipeRefreshLayout.setRefreshing(false);
                    if(tag != null && ((String)tag).equals("multiUserLogin"))
                    {
                        showReloginDialog();
                    }
                    else
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.fail_to_update_info), Toast.LENGTH_SHORT).show();
                    }
                }

                // 在内页进行相应操作后，刷新相册首页;
                else if(id == NotificationCenter.REFRESH_ALBUM_FRAME) {
                    if(object != null)
                    {
                        addCreteFolderIntoList();
                        if(args.length > 0)
                        {
                                List<FolderInfo> folderInfos = (ArrayList<FolderInfo>)object;
                                if(folderInfos != null)
                                {
                                    mCoverList.addAll(folderInfos);
                                }
                            mAdapter.updateAlbumList(mCoverList);
                            synchronizeAlbumData();
                        }
                    }
                } else if(id == NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL) {
                    if (args.length > 0) {
                        CloudStorageItem storageItem = (CloudStorageItem) args[0];
                        ((CloudAlbumPage)mIAlbumPage).mCloudStorageItem = storageItem;
                    }
                }
            }
        });
    }


    /**
     * 在用户重新登陆之后刷新页面，初始化并重新获取数据
     */
    public void refreshFrame()
    {
        initialize();
        this.removeAllViews();
        createView(mContext);
        CloudAlbumController.getInstacne().getAlbumFromServer(mUserId, mAccessToken, mAppInterface, false);
    }

    /**
     * 将创建相册的Item加进首页
     */
    private void addCreteFolderIntoList() {
        mCoverList.clear();
        FolderInfo createFolderSymbol = new FolderInfo();
        final String urlPrefix = "drawable://";
        createFolderSymbol.setCoverImgUrl(urlPrefix + R.drawable.beauty_cloudalbum_create_album);
        mCoverList.add(createFolderSymbol);
    }

    /**
     * 展示出让用户重新登录的提示窗口。
     */
    private void showReloginDialog()
    {
        final String notificationText = getResources().getString(R.string.account_login_other_device) + System.getProperty("line.separator") + getResources().getString(R.string.relogin);
        String btnText = getResources().getString(R.string.ok_text);
        OnAnimationClickListener animationClickListener = new OnAnimationClickListener() {
            @Override
            public void onAnimationClick(View v) {
                notificationDialogView.dismiss();
                CloudAlbumPageSite currentSide = new CloudAlbumPageSite();
                currentSide.OpenLogin(getContext());
            }

            @Override
            public void onTouch(View v) {

            }

            @Override
            public void onRelease(View v) {

            }
        };

        NotificationDialog.DialogConfiguration configuration = new NotificationDialog.DialogConfiguration();
        configuration.mConfirmText = btnText;
        configuration.mNotificationText = notificationText;
        configuration.mConfirmTouchListener = animationClickListener;
        notificationDialogView = new NotificationDialog(getContext(), NotificationDialog.DialogLayoutStyle.SINGLE_BTN_STYLE, configuration);
        notificationDialogView.confirmBtnCell.addSkin(ImageUtils.GetSkinColor(Color.RED));
        notificationDialogView.show();
    }

    /**
     * 更新界面。
     *
     * @param isRresh 是否通过下拉刷新的方式。
     */
    private void updateUiByPullDownRefresh(final boolean isRresh)
    {
        synchronizeAlbumData();
        mProgressDialog.dismiss();
        mAdapter.updateAlbumList(mCoverList);
        if(isRresh) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(CloudAlbumFolderFrame.this.getContext(), getResources().getString(R.string.refreshed_finish), Toast.LENGTH_SHORT).show();
        }
        else {
            if(!(mCoverList.size() > 1)) {
                mContentLayer.setVisibility(GONE);
                mEmptyAlbumLayout.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 添加相应的通知事件。
     */
    private void addNotificationListener() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.GET_ALBUM_FROM_SERVER_SUCCESSFUL);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.GET_ALBUM_FROM_SERVER_FAILURE);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.REFRESH_ALBUM_FROM_SERVER_SUCCESSFUL);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.REFRESH_ALBUM_FROM_SERVER_FAILURE);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.REFRESH_ALBUM_FRAME);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL);
    }

    /**
     * 移除对应的通知事件。
     */
    private void removeNotificationListener()
    {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.GET_ALBUM_FROM_SERVER_SUCCESSFUL);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.GET_ALBUM_FROM_SERVER_FAILURE);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.REFRESH_ALBUM_FROM_SERVER_SUCCESSFUL);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.REFRESH_ALBUM_FROM_SERVER_FAILURE);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.REFRESH_ALBUM_FRAME);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL);
    }

    /**
     * 同步IAlbumPage里面的FolderInfo信息
     */
    private void synchronizeAlbumData()
    {
        ArrayList<FolderInfo> temp = new ArrayList<>();
        temp.addAll(mCoverList.subList(1, mCoverList.size()));
        mIAlbumPage.setFolderInfos(temp);
    }

    /**
     * 隐藏顶部的进度条
     */
    public void hideTipBar()
    {
        if(!TransportImgs.getInstance(mContext).isShowBar() && mProgressBarContainer.getVisibility() != GONE)
        {
            mProgressBarContainer.setVisibility(View.GONE);
            mSwipeRefreshLayout.requestLayout();
        }
    }

    private TransportImgs.OnStateChangeListener mOnStateChangeListener = new TransportImgs.OnStateChangeListener()
    {
        @Override
        public void onStateChange(int type, String tip)
        {
            if(mProgressBarContainer.getVisibility() != VISIBLE && !TransportImgs.sCloseTip)
            {
                mProgressBarContainer.setVisibility(View.VISIBLE);
            }
            changeProgressBarTip(type, tip);
        }
    };


    private void changeProgressBarTip(int type, String tip) {
        setUpProgressBar(type, tip);
        mTipType = type;
        autodismissProgressBar(type);
    }

    private void setUpProgressBar(int type, String tip) {
        if((type & 0x00000088) == 0)
        {
            mProgressBarContainer.setBackgroundColor(getResources().getColor(R.color.cloudalbum_transports_state_bar_bg_green));
        }
        else
        {
            mProgressBarContainer.setBackgroundColor(getResources().getColor(R.color.cloudalbum_transports_state_bar_bg_red));
        }
        mProgressBar.setText(tip);
    }

    private void autodismissProgressBar(int type) {
        if ((type & 0x00000044) != 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransportImgs.sCloseTip = true;
                    mProgressBarContainer.setVisibility(View.GONE);
                    mSwipeRefreshLayout.requestLayout();
                }
            }, 5000);
        }
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
        ImageUtils.AddSkin(mContext, mUploadBtn.getViewBg());
        AddSkin(mContext, mCreateAlbum.getViewBg());
        mSwipeRefreshLayout.setColorSchemeColors(ImageUtils.GetSkinColor());
        AddSkin(mContext, actionBar.getLeftImageBtn());
        AddSkin(mContext, actionBar.getRigthImageBtn());
    }


}
