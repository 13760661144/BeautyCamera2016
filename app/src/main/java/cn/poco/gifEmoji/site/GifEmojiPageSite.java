package cn.poco.gifEmoji.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite2;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.gifEmoji.GifEmojiPage;
import cn.poco.gifEmoji.GifEmojiSharePreviewPage;
import cn.poco.gifEmoji.GifEmojiSharePreviewPageSite;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.LoginPageSite10;

/**
 * Created by zwq on 2017/05/27 10:16.<br/><br/>
 */
public class GifEmojiPageSite extends BaseSite {

    public HomePageSite.CmdProc m_cmdProc;

    public GifEmojiPageSite() {
        super(SiteID.GIF_EMOJI_PREVIEW_EDIT);

        MakeCmdProc();
    }

    /**
     * 注意构造函数调用
     */
    protected void MakeCmdProc()
    {
        m_cmdProc = new HomePageSite.CmdProc();
    }

    @Override
    public IPage MakePage(Context context) {
        return new GifEmojiPage(context, this);
    }

    public void onBack(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
    }

    public void OnHome(Context context)
    {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

//    /**
//     * 打开图片、视频预览
//     *
//     * @param path 文件本地路径
//     * @param isVideo 是否为视频文件
//     */
//    public void OnPreview(Context context, String path, boolean isVideo)
//    {
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("img", path);
//        params.put("isVideo", isVideo);
//        MyFramework.SITE_Popup(context, PreviewImgPageSite.class, params, Framework2.ANIM_TRANSITION);
//    }

    public void OnPreview(Context context, GifEmojiSharePreviewPage.PreviewData previewData)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(GifEmojiSharePreviewPage.KEY_SET_DATA_PREVIEW_DATA, previewData);
        MyFramework.SITE_Popup(context, GifEmojiSharePreviewPageSite.class, params, Framework2.ANIM_NONE);
    }

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

    public void OnLogin(Context context)
    {
        MyFramework.SITE_Popup(context, LoginPageSite10.class, null, Framework2.ANIM_NONE);
    }

    public void OnBindPhone(Context context)
    {
        MyFramework.SITE_Popup(context, BindPhonePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void onCommunity(Context context, String path)
    {
        HashMap<String,Object> params = new HashMap<>();
        params.put("path", path);
        params.put("type", 3);
        params.put("content", "");
        MyFramework.SITE_Popup(context, PublishOpusSite.class, params, Framework2.ANIM_NONE);
    }

    public void OnHomeCommunity(Context context)
    {
        HashMap<String ,Object> param=new HashMap<>();
        param.put(Home4Page.KEY_CUR_MODE,Home4Page.CAMPAIGN);

        HashMap<String ,Object> data = new HashMap<>();
        data.put("openFriendPage",true);
		param.put(Home4Page.KEY_TOP_DATA, data);

        MyFramework.SITE_Open(context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
    }
}
