package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite18;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite11;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.EditHeadIconImgPage;
import cn.poco.login.UserInfoPage;
import cn.poco.taskCenter.TaskCenterPage;
import cn.poco.taskCenter.site.TaskCenterPageSite;

/**
 * 侧边栏进入个人中心
 */
public class UserInfoPageSite extends BaseSite {
    @Override
    public IPage MakePage(Context context) {
        return new UserInfoPage(context, this);
    }

    public UserInfoPageSite() {
        super(SiteID.USERINFO_PAGE);
    }

    //个人信息页点击头像下的相机icon调用
    public void onCamera(String id, String token, String bgPath, Context context) {
        HashMap<String, Object> datas = new HashMap<String, Object>();
        datas.put("poco_id", id);
        datas.put("poco_token", token);
        datas.putAll(CameraSetDataKey.GetRegisterTakePicture(false));
        if (bgPath != null && bgPath.length() > 0) {
            datas.put(EditHeadIconImgPage.BGPATH, bgPath);
        }
        MyFramework.SITE_Popup(context, CameraPageSite11.class, datas, Framework2.ANIM_NONE);
    }

    //点击头像调用
    public void onChooseHeadBmp(String id, String tocken, String bgPath, Context context) {
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("userId", id);
        datas.put("tocken", tocken);
        if (bgPath != null && bgPath.length() > 0) {
            datas.put(EditHeadIconImgPage.BGPATH, bgPath);
        }
        datas.put("from_camera", true);
        MyFramework.SITE_Popup(context, AlbumSite18.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }

    //退出登录，返回首页
    public void onExit(Context context) {
        MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    //绑定手机调用
    public void onBindPhone(HashMap<String, Object> params, Context context) {
        MyFramework.SITE_Open(context, BindPhonePageSite1.class, params, Framework2.ANIM_NONE);
    }


    //返回首页
    public void onBack(Context context) {
        MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void onOpenCredit(String id, String token, String maskPath, Context context) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(TaskCenterPage.EXTRA_USER_ID, id);
        params.put(TaskCenterPage.EXTRA_ACCESS_TOKEN, token);
        params.put(TaskCenterPage.EXTRA_BITMAP, maskPath);
        MyFramework.SITE_Open(context, TaskCenterPageSite.class, params, Framework2.ANIM_NONE);
    }

    //登录信息过期，跳转到登陆页面调用
    public void toLoginPage(Context context) {
        MyFramework.SITE_Open(context, LoginPageSite.class, null, Framework2.ANIM_NONE);
    }

}
