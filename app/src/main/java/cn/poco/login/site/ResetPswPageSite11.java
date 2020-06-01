package cn.poco.login.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**保存脸型数据 登陆，忘记密码，到设置密码
 * @author lmx
 *         Created by lmx on 2018-01-29.
 */

public class ResetPswPageSite11 extends ResetPswPageSite
{
    /**
     * 设置密码成功
     */
    @Override
    public void resetPswSuccess(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        //ShapeSyncResMgr.getInstance().post2UpdateSyncData(context);
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }
}
