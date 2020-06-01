package cn.poco.arWish.site;

import android.content.Context;

import com.amap.api.services.core.PoiItem;

import cn.poco.arWish.SearchKeyWordPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by admin on 2018/1/18.
 */

public class SearchKeyWordSite extends BaseSite {
    /**
     * 派生类必须实现一个XXXSite()的构造函数
     *
     * @param id
     */
    private Context m_context;

    public SearchKeyWordSite()
    {
        super(SiteID.SEARCH_KEY_WORD);
    }

    @Override
    public IPage MakePage(Context context)
    {
        m_context = context;
        return new SearchKeyWordPage(context,this);
    }

    public void onBack()
    {
        MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSITION);
    }

    public void onChooseLocation(PoiItem poiItem){
        MyFramework.SITE_ClosePopup(m_context,null,Framework2.ANIM_TRANSITION);
    }
}
