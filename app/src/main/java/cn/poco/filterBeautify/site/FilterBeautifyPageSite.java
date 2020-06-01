package cn.poco.filterBeautify.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.beautify.EffectType;
import cn.poco.beautify4.site.Beautify4PageSite3;
import cn.poco.camera.RotationImg2;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.filterBeautify.FilterBeautifyPageV2;
import cn.poco.filterManage.site.FilterMoreSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.login.site.LoginPageSite10;
import cn.poco.resource.ResType;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * 正常流程拍照预览页
 */
public class FilterBeautifyPageSite extends BaseSite
{
    public FilterBeautifyPageSite()
    {
        super(SiteID.FILTER);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new FilterBeautifyPageV2(context, this);
    }

    public void OnBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void OnBeauty(Context context, HashMap<String, Object> params, RotationImg2 img, int filterUri, int filterAlpha, boolean hasWaterMark, int waterMarkId)
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        params.put("imgs", new RotationImg2[]{img});
        params.put(DataKey.COLOR_FILTER_ID, filterUri);
        params.put("filter_alpha", filterAlpha);
        params.put("do_not_del_filter_cache", true);
        params.put("do_not_reset_data", true);
        params.put("def_color_sel_uri", EffectType.EFFECT_DEFAULT);
        params.put("only_one_pic", true);
        params.put("has_water_mark", hasWaterMark);
        params.put("water_mark_id", waterMarkId);
        SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
        if (settingInfo.GetAddDateState())
        {
            //添加拍照日期
            params.put("add_date", true);
        }
        MyFramework.SITE_Open(context, Beautify4PageSite3.class, params, Framework2.ANIM_NONE);
    }

    /**
     * 保存成功，返回镜头
     *
     * @param context
     * @param params
     */
    public void OnSave(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);

        /*if (params != null) params.put("from_camera", true);
        MyFramework.SITE_Open(context, false, SharePageSite3.class, params, new AnimatorHolder()
        {
            @Override
            public void doAnimation(View oldView, View newView, final AnimatorListener lst)
            {
                lst.OnAnimationStart();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        lst.OnAnimationEnd();
                    }
                }, 500);
            }
        });*/
    }


    public void OnLogin(Context context)
    {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }

    public void onLoginCommunity(Context context)
    {
        MyFramework.SITE_Popup(context, LoginPageSite10.class, null, Framework2.ANIM_NONE);
    }


    public void onBindPhone(Context context)
    {
        MyFramework.SITE_Popup(context, BindPhonePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void onHome(Context context)
    {
        HashMap<String, Object> param = new HashMap<>();
        param.put(Home4Page.KEY_CUR_MODE, Home4Page.CAMPAIGN);

        HashMap<String, Object> data = new HashMap<>();
        data.put("openFriendPage", true);
        param.put(Home4Page.KEY_TOP_DATA, data);
        MyFramework.SITE_Open(context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
    }

    /**
     * 推荐位下载更多
     *
     * @param resType
     */
    public void OpenDownloadMore(Context context, ResType resType)
    {
        MyFramework.SITE_Popup(context, FilterMoreSite.class, null, Framework2.ANIM_NONE);
    }

    /**
     * 分享到社区
     *
     * @param path 路径
     * @param type 类型 1:图片 2:视频 3: gif
     */
    public void onCommunity(Context context, String path, int type)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("type", type);
        params.put("content", "");
        MyFramework.SITE_Popup(context, PublishOpusSite.class, params, Framework2.ANIM_NONE);
    }
}
