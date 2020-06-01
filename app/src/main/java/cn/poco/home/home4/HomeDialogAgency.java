package cn.poco.home.home4;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Date;

import cn.poco.banner.BannerCore3;
import cn.poco.framework.MyFramework2App;
import cn.poco.home.home4.widget.HomeTipDialog;
import cn.poco.home.home4.widget.LoginTipDialog;
import cn.poco.image.PocoDetector;
import cn.poco.login.UserMgr;
import cn.poco.prompt.PopupMgr;
import cn.poco.resource.BannerRes;
import cn.poco.resource.BannerResMgr2;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MemoryTipDialog;
import cn.poco.utils.Utils;
import cn.poco.web.info.UpdateInfo;
import cn.poco.web.ui.ImformUpdateView;
import cn.poco.widget.AlertDialogV1;
import cn.poco.widget.SdkOutDatedDialog;

/**
 * Created by lgd on 2017/12/15.
 *开机进入主页的对话框弹出逻辑这里判断
 *
 *
 */
public class HomeDialogAgency
{
    private Home4Page home4Page;
    private Context context;
    private FrameLayout m_updateDlg;
    private PopupMgr m_popupView;
    private UpdateInfo.UpdateType mUpdateType;
    public HomeDialogAgency(Home4Page home4Page)
    {
        super();
        this.home4Page = home4Page;
        context = home4Page.getContext();
    }

    /**
     * 主页做完动画和毛玻璃后调用
     * @param isFirstInstall
     */
    protected void checkAndShowDialog(boolean isFirstInstall)
    {
        if (isFirstInstall)
        {
            if(!isShowLoginTipDialog())
            {
                home4Page.setUiEnabled(false);
                final HomeTipDialog tipDialog = new HomeTipDialog((Activity) context);
                tipDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        home4Page.setUiEnabled(true);
                    }
                });
                tipDialog.setCallback(new HomeTipDialog.Callback()
                {
                    @Override
                    public void onOk()
                    {
                        home4Page.setUiEnabled(true);
                        tipDialog.dismiss();
                        if (!isShowMemoryTipDialog())
                        {
                            if (!shouldShowSdkOutDatedDialog())
                            {
                                showPopup();
                            }
                        }
                    }
                });
                tipDialog.show();
            }
        } else
        {
            if(!isShowLoginTipDialog())
            {
                if (!isShowAppUpdateDialog())
                {
                    if (!isShowMemoryTipDialog())
                    {
                        if (!shouldShowSdkOutDatedDialog())
                        {
                            showPopup();
                        }
                    }
                }
            }
        }
    }

    private void showPopup()
    {
        if (m_popupView != null && m_popupView.IsRecycle())
        {
            m_popupView = null;
        }
        if (m_popupView == null)
        {
            boolean canJumpApp = false;
            if (!MyFramework2App.getInstance().IsFirstRun())
            {
                canJumpApp = true;
            }
            m_popupView = new PopupMgr(context, BannerResMgr2.B20, canJumpApp, new PopupMgr.Callback()
            {
                @Override
                public void OnCloseBtn()
                {
                }

                @Override
                public void OnClose()
                {
                    home4Page.setGestureEnable(true);
                }

                @Override
                public void OnBtn()
                {
                }

                @Override
                public void OnJump(PopupMgr view, BannerRes res)
                {
                    if (home4Page.mUiEnabled && res != null)
                    {
                        home4Page.setGestureEnable(true);
                        BannerCore3.ExecuteCommand(context, res.m_cmdStr, home4Page.mSite.m_cmdProc);
                    }
                }
            });
            if (m_popupView.CanShow())
            {
                home4Page.setGestureEnable(false);
                m_popupView.Create();

                //InitGlassBk();
                m_popupView.SetBk(home4Page.m_maskBmp);
                m_popupView.Show(home4Page);
            } else
            {
                m_popupView.ClearAll();
                m_popupView = null;
            }
        }
    }

    /**
     * 登陆账号必须绑定手机
     * @return
     */
    protected boolean isShowLoginTipDialog()
    {
        boolean isShow = false;
        if(UserMgr.IsLogin(context,null)){
            SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
            String phone = info.GetPoco2Phone();
            if (phone == null || phone.length() <= 5)
            {
                home4Page.setUiEnabled(false);
                UserMgr.ExitLogin(context);
                isShow = true;
                final LoginTipDialog loginTipDialog = new LoginTipDialog((Activity) context);
                loginTipDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        home4Page.setUiEnabled(true);
                    }
                });
                loginTipDialog.setCallBack(new LoginTipDialog.LoginTipCallBack()
                {
                    @Override
                    public void onLogin()
                    {
                        home4Page.setUiEnabled(true);
                        loginTipDialog.dismiss();
                        home4Page.mSite.OnLogin(context,Home4Page.s_maskBmpPath);
                    }

                    @Override
                    public void onNextTime()
                    {
                        home4Page.setUiEnabled(true);
                        loginTipDialog.dismiss();
                    }
                });
                loginTipDialog.show();
            }
        }
        return isShow;
    }

    // 检查判断一下是否需要显示更新应用的弹框
    private boolean isShowAppUpdateDialog()
    {
        boolean isShow = false;
        //判断是否要弹
        String updateInfo = TagMgr.GetTagValue(context, Tags.APP_UPDATE_INFO);
        if (updateInfo != null && updateInfo.length() > 0)
        {
            UpdateInfo info = new UpdateInfo();
            if (info.DecodeData(updateInfo))
            {
                if (!info.getVersion().val.equals(CommonUtils.GetAppVer(context)))
                {
                    int ver = info.getPopVersion();
                    UpdateInfo.UpdateType updateType = info.getUpdateType();
                    String oldVer = TagMgr.GetTagValue(context, Tags.APP_UPDATE_VER);
                    boolean showCondition = false;
                    // 强制更新的情况
                    if (updateType == UpdateInfo.UpdateType.mandatoryUpdate)
                    {
                        showCondition = true;
                    } else
                    {
                        if ((oldVer == null || !oldVer.equals(Integer.toString(ver))))
                        {
                            showCondition = true;
                        }
                    }
                    if (showCondition)
                    {
                        if (info != null && info.getUpdateType() != null && info.getUpdateType() != UpdateInfo.UpdateType.unnecessary)
                        {
                            isShow = true;
                            showUpdateAppVersionPopup(info);
                        }
                    }
                }
            }
        }
        return isShow;
    }
    // 弹出更新应用的弹框
    private void showUpdateAppVersionPopup(final UpdateInfo info)
    {
        if (info != null)
        {
            mUpdateType = info.getUpdateType();
            home4Page.setGestureEnable(false);
            //显示弹框
            m_updateDlg = new FrameLayout(context);
            m_updateDlg.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });
            m_updateDlg.setBackgroundColor(0xB3000000);
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            home4Page.addView(m_updateDlg, fl);
            {
                ImformUpdateView updateView = new ImformUpdateView(context, info);
                fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(582), FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                m_updateDlg.addView(updateView, fl);
                updateView.setOnUpdateDialogClickListener(new ImformUpdateView.OnUpdateDialogClickListener()
                {
                    @Override
                    public void onClickIgnoreUpdate()
                    {
                        ClearUpdateDlg();
                        if (!isShowMemoryTipDialog())
                        {
                            showPopup();
                        }
                        TagMgr.SetTagValue(context, Tags.APP_UPDATE_VER, Integer.toString(info.getPopVersion()));
                    }

                    @Override
                    public void onClickUpdateNow(String url)
                    {
//						TagMgr.SetTagValue(context, Tags.APP_UPDATE_VER, Integer.toString(info.getPopVersion()));
                        home4Page.mSite.OnUpdateNow(context, url);
                        ClearUpdateDlg();
                    }

                    @Override
                    public void onClickCheckDetail(String url)
                    {
                        home4Page.mSite.OnUpdateDetail(context, url);
                    }
                });
            }
        }
    }

    /**
     * 是否显示人脸检测sdkg过期的弹窗
     *
     * @return true为显示，false为不显示
     */
    private boolean shouldShowSdkOutDatedDialog()
    {
        boolean sdkIsValid = PocoDetector.detectFaceSdkIsValid(new Date());
        if (!sdkIsValid)
        {
            showSdkOutDatedDialog();
        }
        return !sdkIsValid;
    }

    private SdkOutDatedDialog mSdkOutDateDialog;

    /**
     * 弹出人脸检测过期的弹窗
     */
    private void showSdkOutDatedDialog()
    {
        if (mSdkOutDateDialog != null && !mSdkOutDateDialog.isShowingDialog())
        {
            mSdkOutDateDialog.show();
        } else
        {
            mSdkOutDateDialog = new SdkOutDatedDialog((Activity) this.context);
            mSdkOutDateDialog.setCallback(new SdkOutDatedDialog.SdkDialogCallback()
            {
                @Override
                public void updateNow()
                {
                    CommonUtils.OpenBrowser(context, "market://details?id=my.beautyCamera");
                }

                @Override
                public void updateLater()
                {

                }
            });
            mSdkOutDateDialog.show();
        }
    }

    /**
     * 内存空间检查
     * @return
     */
    private boolean isShowMemoryTipDialog()
    {
        boolean isShow;
        final MemoryTipDialog dialog = MemoryTipDialog.shouldShowMemoryDialog(context, Utils.SDCARD_ERROR | Utils.SDCARD_OUT_OF_SPACE);
        if (dialog != null)
        {
            home4Page.mHasNoMemory = true;
            isShow = true;
            int type = Utils.CheckSDCard(context);
            if (type == Utils.SDCARD_OUT_OF_SPACE)
            {
                dialog.setPositiveClickListener(new MemoryTipDialog.OnDialogClick()
                {
                    @Override
                    public void onClick(AlertDialogV1 dialogV1)
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
            }
            dialog.show();
        } else
        {
            isShow = false;
        }
        return isShow;
    }

    private void ClearUpdateDlg()
    {
        if (m_updateDlg != null)
        {
            ViewGroup vg = (ViewGroup) m_updateDlg.getParent();
            if (vg != null)
            {
                vg.removeView(m_updateDlg);
            }
            m_updateDlg = null;
        }
        home4Page.setGestureEnable(true);
    }


    protected boolean OnBack()
    {
        boolean b = false;
        if (m_updateDlg != null)
        {
            ClearUpdateDlg();
            if (mUpdateType == UpdateInfo.UpdateType.mandatoryUpdate)
            {
                // 强制更新
                CommonUtils.OpenBrowser(context, "market://details?id=my.beautyCamera");
            } else
            {
                if (!isShowMemoryTipDialog())
                {
                    showPopup();
                }
            }
            b = true;
        }
        if (m_popupView != null && m_popupView.IsShow())
        {
            m_popupView.OnCancel(true);
            b = true;
        }
        return b;
    }

    protected void clearAll()
    {
        ClearUpdateDlg();
        if (m_popupView != null)
        {
            m_popupView.ClearAll();
            m_popupView = null;
        }
        home4Page = null;
    }

}
