package cn.poco.cloudAlbum.frame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import cn.poco.advanced.ImageUtils;
import static cn.poco.advanced.ImageUtils.AddSkin;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.cloudAlbum.IAlbumPage;
import cn.poco.cloudalbumlibs.AbsAlbumSettingFrame;
import cn.poco.cloudalbumlibs.controller.CloudAlbumController;
import cn.poco.cloudalbumlibs.controller.NotificationCenter;
import cn.poco.cloudalbumlibs.model.CloudStorageItem;
import cn.poco.cloudalbumlibs.view.ActionBar;
import cn.poco.cloudalbumlibs.view.SettingSliderBtn;
import cn.poco.cloudalbumlibs.view.cell.MenuItemCell;
import cn.poco.cloudalbumlibs.view.widget.NotificationDialog;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.storage.StorageService;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * Created by shine on 2016/9/14.
 */
public class CloudAlbumSettingFrame extends AbsAlbumSettingFrame implements NotificationCenter.NotificationDelegate, CloudAlbumPage.IFrame{
    private Context mContext;
    private IAlbumPage mIAlbumPage;
    private AppInterface mAppInterface;
    private WaitAnimDialog mProgressDialog;

    public CloudAlbumSettingFrame(Context context, IAlbumPage iAlbum, AppInterface appInterface) {
        super(context);
        this.mContext = context;
        this.mIAlbumPage = iAlbum;
        this.mAppInterface = appInterface;
        initialize();
        StatisticHelper.countPageEnter(mContext, context.getString(R.string.云相册_设置));
    }

    @Override
    public void onResume() {
        StatisticHelper.countPageResume(mContext, mContext.getString(R.string.云相册_设置));
    }

    @Override
    public void onPause() {
        StatisticHelper.countPagePause(mContext, mContext.getString(R.string.云相册_设置));
    }

    @Override
    public void onClose() {
        StatisticHelper.countPageLeave(mContext, mContext.getString(R.string.云相册_设置));
    }

    @Override
    protected void initialize() {
        super.initialize();
        createView(getContext());
    }

    @Override
    public void createView(final Context context) {
        super.createView(context);
        addNotificationListener();
        CloudAlbumController.getInstacne().getCloudStorageInfo(mUserId, mAccessToken, "GB", mAppInterface);
        this.setAbsAlbumSettingDelegate(new AbsAlbumSettingFrameDelegate() {
            @Override
            public void onMenuItemClick(int index) {
                mAppInterface.clickTransportListInSettingFrame(context);
                mIAlbumPage.openCloudAlbumTransportFrame(false);
            }

            @Override
            public void onToggleButtonStageChange(View v, boolean state) {
                mAppInterface.onClickWiFiTransportButton(context);
                final SettingSliderBtn sBtn = (SettingSliderBtn)v;
                if(!state) {
                    OnAnimationClickListener animationListener = new OnAnimationClickListener() {
                        @Override
                        public void onAnimationClick(View v) {
                            if (v == notificationDialogView.cancelBtn) {
                                notificationDialogView.dismiss();
                                sBtn.setSwitchStatus(true);
                            } else if (v == notificationDialogView.confirmBtnCell) {
                                TagMgr.SetTagValue(getContext(), Tags.CLOUDALBUM_ISWIFITRANSPORTIMGS, "false");
                                StorageService.SetOnlyWifi(mContext, false);
                                notificationDialogView.dismiss();
                            }
                        }

                        @Override
                        public void onTouch(View v) {

                        }

                        @Override
                        public void onRelease(View v) {
                        }
                    };
                    String message = getResources().getString(R.string.close_wifi_notify) + System.getProperty("line.separator") + getResources().getString(R.string.close_wifi_notify2);
                    String confirmText = getResources().getString(R.string.go_on);
                    String cancelText = getResources().getString(R.string.cancel);

                    NotificationDialog.DialogConfiguration configuration = new NotificationDialog.DialogConfiguration();
                    configuration.mWidth = ShareData.PxToDpi_xhdpi(568);
                    configuration.mHeight = ShareData.PxToDpi_xhdpi(403);
                    configuration.mConfirmText = confirmText;
                    configuration.mCancelText = cancelText;
                    configuration.mNotificationText = message;
                    configuration.mCancelTouchListener = animationListener;
                    configuration.mConfirmTouchListener = animationListener;
                    showNotificationDialog(configuration);
                    notificationDialogView.confirmBtnCell.addSkin(ImageUtils.GetSkinColor(Color.RED));
                } else {
                    TagMgr.SetTagValue(getContext(), Tags.CLOUDALBUM_ISWIFITRANSPORTIMGS, "true");
                    StorageService.SetOnlyWifi(mContext, true);
                }
            }

            @Override
            public void onDialogCancal() {
                clearView();
            }
        });
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
                }
            }
        });
        return actionBar;
    }

    @Override
    protected ImageView initUserAvatar(ImageView userAvatar) {
        final ImageView avatar = super.initUserAvatar(userAvatar);
        String loginedAvatarUrl = SettingInfoMgr.GetSettingInfo(mContext).GetPoco2HeadUrl();
        ImageLoaderUtil.getBitmapByUrl(mContext, loginedAvatarUrl, new ImageLoaderUtil.ImageLoaderCallback() {
            @Override
            public void loadImageSuccessfully(Object object) {
                if (object instanceof Bitmap) {
                    Bitmap temp = (Bitmap) object;
                    // 用户已经登录，成功获取头像照片
                    if (temp != null) {
                        Bitmap avatarBitmap = ImageUtils.MakeHeadBmp(temp, PxToDpi_xhdpi(164), 2);
                        avatar.setImageBitmap(avatarBitmap);
                    }
                }
            }
            @Override
            public void failToLoadImage() {


            }
        });
        return avatar;
    }

    @Override
    protected TextView initUserName(TextView userName) {
        TextView tv = super.initUserName(userName);
        final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
        if (settingInfo.GetPocoNick() != null) {
            tv.setText(settingInfo.GetPocoNick());
        }
        return tv;
    }


    @Override
    public void updateView() {
        super.updateView();
    }

    @Override
    protected void clearView() {
        super.clearView();
        removeNotificationListene();
        mIAlbumPage.onFrameBack(this);
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
            public void run()
            {
                mProgressDialog.dismiss();
                if(id == NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL)
                {
                    if (args.length > 0) {
                        CloudStorageItem storageItem = (CloudStorageItem) args[0];

                        final long freeStorage = storageItem.getFreeVolume();
                        final long totalStorage = storageItem.getMaxVolume();
                        final long formatTotalStorage = storageItem.getFormatMaxVolume();
                        final long currentStorage = totalStorage - freeStorage;

                        final long GBUnit = 1024 * 1024 * 1024;
                        final long MBUnit = 1024 * 1024;

                        final double resultDivisionWithGB = ((double)currentStorage / (double)GBUnit);
                        final double resultDivisionWithMB = ((double)currentStorage / (double)MBUnit);

                        final String GBVolume = "GB";
                        final String MBVolume = "MB";

                        double formatStorageGBText = Double.parseDouble(new DecimalFormat("##.#").format(resultDivisionWithGB));
                        double formatStorageMBText = Double.parseDouble(new DecimalFormat("##.#").format(resultDivisionWithMB));
                        mProgressView.setTotalStorageText(formatTotalStorage + GBVolume);

                        if (resultDivisionWithGB > 0.512) {
                            mProgressView.setCurrentStorageText(formatStorageGBText + GBVolume);
                        } else if (resultDivisionWithMB > 1) {
                            mProgressView.setCurrentStorageText(formatStorageMBText + MBVolume);
                        } else if (resultDivisionWithMB > 0 && resultDivisionWithMB <= 1) {
                            mProgressView.setCurrentStorageText(1 + MBVolume);
                        } else {
                            mProgressView.setCurrentStorageText(0 + MBVolume);
                        }
                        double divisionResult = (double)currentStorage / (double)totalStorage * 100;
                        int percentage;
                        if (divisionResult < 1) {
                            percentage = currentStorage > 0 ? 1 : 0;
                        } else {
                            percentage = (int)Math.round(divisionResult);
                        }
                        mProgressView.getmProgressBar().setProgress(percentage);
                    }
                }
                else if(id == NotificationCenter.GET_CLOUD_STORAGE_INFO_FAILURE)
                {
                    Toast.makeText(CloudAlbumSettingFrame.this.getContext(), getResources().getString(R.string.unable_to_get_info), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void showDialog() {
        mProgressDialog = new WaitAnimDialog((Activity) getContext());
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void addNotificationListener()
    {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_FAILURE);
    }

    private void removeNotificationListene()
    {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_SUCCESSFUL);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.GET_CLOUD_STORAGE_INFO_FAILURE);
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
        AddSkin(mContext, actionBar.getLeftImageBtn());
        MenuItemCell itemCell = (MenuItemCell) mMenuLayout.getChildAt(0);
        if (itemCell != null) {
            AddSkin(mContext, itemCell.getArrowBtn());
        }

        LayerDrawable layerDrawable =(LayerDrawable) mProgressView.getmProgressBar().getProgressDrawable();
        ClipDrawable clipDrawable = (ClipDrawable) layerDrawable.getDrawable(1);
        clipDrawable.setColorFilter(SysConfig.s_skinColor, PorterDuff.Mode.SRC_ATOP);

        MenuItemCell itemCell2 = (MenuItemCell) mMenuLayout.getChildAt(2);
		Bitmap mBmpBlueBack = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.setting_slidebtn_bluebk));
        if (itemCell2 != null && itemCell2.getToggleBtn() != null) {
            Bitmap bitmap = ImageUtils.AddSkin(mContext, mBmpBlueBack);
            itemCell2.getToggleBtn().setBlueBackBitmap(bitmap);
        }
    }
}
