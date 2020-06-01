package cn.poco.lightApp06.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

import cn.poco.beautify4.site.Beautify4PageSite4;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite2;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.lightApp06.BeautyVideoPage;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.LoginPageSite10;
import cn.poco.preview.site.PreviewImgPageSite;
import cn.poco.utils.Utils;

/**
 * @author lmx
 *         Created by lmx on 2017-12-21.
 */

public class BeautyVideoPageSite extends BaseSite
{
    public HomePageSite.CmdProc m_cmdProc;


    public BeautyVideoPageSite()
    {
        super(SiteID.DYNAMIC_STICKER_VIDEO_PREVIEW);

        m_cmdProc = new HomePageSite.CmdProc();
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new BeautyVideoPage(context, this);
    }


    /**
     * 打开图片、视频预览
     *
     * @param path    文件本地路径
     * @param isVideo 是否为视频文件
     */
    public void OnPreview(Context context, String path, boolean isVideo)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("img", path);
        params.put("isVideo", isVideo);
        MyFramework.SITE_Popup(context, PreviewImgPageSite.class, params, isVideo ? Framework2.ANIM_NONE : Framework2.ANIM_TRANSITION);
    }

    /**
     * 首页
     *
     * @param context
     */
    public void OnHome(Context context)
    {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

    /**
     * 继续拍摄
     */
    public void OnCamera(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        Class<? extends BaseSite> siteClass = CameraPageSite2.class;
        BaseSite baseSite = MyFramework.GetLinkSite(context, CameraPageSite.class);
        if (baseSite != null) {
            params = (HashMap<String, Object>) baseSite.m_inParams.clone();
        } else {
            CameraConfig.getInstance().initAll(context);
            params.put(CameraSetDataKey.KEY_START_MODE, CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.LastCameraId));
            CameraConfig.getInstance().clearAll();
        }
        MyFramework.SITE_Open(context, true, siteClass, params, Framework2.ANIM_NONE);
    }

    /**
     * 从分享跳到美颜美化界面
     *
     * @param params img:RotationImg2
     */
    public void OnBeautyFace(Context context, HashMap<String, Object> params)
    {
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgs", params.get("img"));
        temp.put("only_one_pic", true);
        MyFramework.SITE_Open(context, Beautify4PageSite4.class, temp, Framework2.ANIM_NONE);
    }


    /**
     * 分享到社区
     *
     * @param path 路径
     * @param type 类型 1:图片 2:视频 3: gif
     */
    public void OnCommunity(Context context, String path, String content, int type, String extra)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("type", type);
        params.put("content", content);
        params.put("extra", extra);
        MyFramework.SITE_Popup(context, PublishOpusSite.class, params, Framework2.ANIM_NONE);
    }

    public void OnLogin(Context context)
    {
        MyFramework.SITE_Popup(context, LoginPageSite10.class, null, Framework2.ANIM_NONE);
    }

    public void OnBindPhone(Context context)
    {
        MyFramework.SITE_Popup(context, BindPhonePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void OnCommunityHome(Context context)
    {
        HashMap<String, Object> param = new HashMap<>();
        param.put(Home4Page.KEY_CUR_MODE, Home4Page.CAMPAIGN);

        HashMap<String, Object> data = new HashMap<>();
        data.put("openFriendPage", true);

        param.put(Home4Page.KEY_TOP_DATA, data);

        MyFramework.SITE_Open(context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
    }

    /**
     * 合成视频后直接分享跳转到社区
     *
     * @param context
     * @param videoPath
     * @param circleExtras {@link cn.poco.camera.site.CameraPageSite300#makeCircleExtra(int, String)}
     */
    public void onSaveToCommunity(Context context, String videoPath, String circleExtras)
    {
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("videoPath", videoPath);
        temp.put(DataKey.COMMUNITY_SEND_CIRCLE_EXTRA, circleExtras);
        MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_NONE);
    }

    /**
     * 第三方调用
     */
    public void onThirdPartyBack(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(CameraSetDataKey.GetExternalTakeVideo());
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
    }

    /**
     * 第三方调用
     *
     * @param path
     */
    public void onThirdPartySave(Context context, String path)
    {
        int resultCode = Activity.RESULT_CANCELED;

        Intent data = new Intent();
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            if (file.exists())
            {
                Uri uri = Utils.InsertVideoToSys(context, path);
                if (uri == null)
                {
                    uri = Uri.fromFile(file);
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                data.setData(uri);
                resultCode = Activity.RESULT_OK;
            }
        }
        MyFramework.SITE_Finish(context, resultCode, data);
    }


    public void onBack(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
    }

}
