package cn.poco.arWish.site;

import android.content.Context;

import com.amap.api.services.core.PoiItem;

import java.util.HashMap;

import cn.poco.arWish.SearchNearPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by admin on 2018/1/18.
 */

public class SearchNearSite extends BaseSite {
    /**
     * 派生类必须实现一个XXXSite()的构造函数
     *
     * @param id
     */
    private Context m_context;

    public SearchNearSite()
    {
        super(SiteID.SEARCH_NEAR);
    }

    @Override
    public IPage MakePage(Context context)
    {
        m_context = context;
        return new SearchNearPage(context,this);
    }

    public void onBack()
    {
        MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void onChooseLocation(PoiItem poiItem){
        if (poiItem != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("address", poiItem.getTitle());
            params.put("latlon", poiItem.getLatLonPoint());
            MyFramework.SITE_Back(m_context, params, Framework2.ANIM_TRANSLATION_LEFT);
        }
    }

    public void goToKeySearch(String cityCode) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("cityCode",cityCode);
        MyFramework.SITE_Popup(m_context, SearchKeyWordSite.class, map, Framework2.ANIM_NONE);
    }
}
