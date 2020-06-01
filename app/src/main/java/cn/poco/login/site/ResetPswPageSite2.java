package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.cloudAlbum.site.CloudAlbumPageSite2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * 弹窗进入云相册没绑定手机，绑定手机填写密码
 */
public class ResetPswPageSite2 extends ResetPswPageSite {
    //填写密码成功之后跳转
    @Override
    public void successBind(HashMap<String, Object> datas,Context context) {
        LoginAllAnim.ReSetLoginAnimData();
        SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
        String id = info.GetPoco2Id(true);
        String token = info.GetPoco2Token(true);
        if(id != null && id.length() > 0 && token != null && token.length() > 0)
        {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("token", token);
            MyFramework.SITE_OpenAndClosePopup(context, false, CloudAlbumPageSite2.class, params, Framework2.ANIM_NONE);
        }
    }
}
