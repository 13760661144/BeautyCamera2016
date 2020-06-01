package cn.poco.camera3.beauty;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-15.
 */

public class TabUIConfig
{
    /**
     * 显示tab 类型，可多个
     */
    @IntDef({TAB_TYPE.UNSET, TAB_TYPE.TAB_BEAUTY, TAB_TYPE.TAB_SHAPE, TAB_TYPE.TAB_FILTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_TYPE
    {
        public int UNSET = 0;
        public int TAB_BEAUTY = 1;
        public int TAB_SHAPE = 1 << 1;
        public int TAB_FILTER = 1 << 2;
    }

    /**
     * 页面加载类型（镜头社区、直播助手）
     */
    @IntDef({PAGE_TYPE.UNSET, PAGE_TYPE.PAGE_CAMERA, PAGE_TYPE.PAGE_LIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PAGE_TYPE
    {
        public int UNSET = 0;
        public int PAGE_CAMERA = 1;
        public int PAGE_LIVE = 1 << 1;
    }

    /**
     * view 加载类型
     */
    @IntDef({VIEW_TYPE.UNSET, VIEW_TYPE.VIEW_PHOTO, VIEW_TYPE.VIEW_STICKER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VIEW_TYPE
    {
        public int UNSET = 0;
        public int VIEW_PHOTO = 1;
        public int VIEW_STICKER = 1 << 1;
    }

    public static final int TAB_TYPE_ALL = TAB_TYPE.TAB_BEAUTY | TAB_TYPE.TAB_SHAPE | TAB_TYPE.TAB_FILTER;
    public static final int VIEW_TYPE_ALL = VIEW_TYPE.VIEW_PHOTO | VIEW_TYPE.VIEW_STICKER;

    public Builder mBuilder;

    private TabUIConfig()
    {
    }

    private TabUIConfig create(Builder builder)
    {
        this.mBuilder = builder;
        return this;
    }

    public ArrayList<TabUIConfig.TabUI> getTabUIs()
    {
        if (this.mBuilder != null)
        {
            return this.mBuilder.m_tab_type_s;
        }
        return null;
    }

    public int getTabUIType(@TAB_TYPE int type)
    {
        int index = -1;
        ArrayList<TabUI> tabUIs = getTabUIs();
        if (type != TAB_TYPE.UNSET && tabUIs != null && tabUIs.size() > 0)
        {
            for (int i = 0, size = tabUIs.size(); i < size; i++)
            {
                TabUI tabUI1 = tabUIs.get(i);
                if (tabUI1.m_type == type)
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public TabUI getTabUI(int position)
    {
        ArrayList<TabUI> tabUIs = getTabUIs();
        if (tabUIs != null && tabUIs.size() > position)
        {
            return tabUIs.get(position);
        }
        return null;
    }

    public boolean hasTabUI(@TAB_TYPE int tab_type)
    {
        ArrayList<TabUI> tabUIs = getTabUIs();
        if (tabUIs != null)
        {
            for (TabUI tabUI : tabUIs)
            {
                if (tabUI != null
                        && tabUI.m_type == tab_type
                        && tab_type != TAB_TYPE.UNSET)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public int getCurrentTabType()
    {
        return mBuilder != null ? mBuilder.currentTabType : TAB_TYPE.UNSET;
    }

    public int getTabCount()
    {
        return getTabUIs() != null ? getTabUIs().size() : 0;
    }

    @PAGE_TYPE
    public int getPageType()
    {
        if (mBuilder == null) return PAGE_TYPE.UNSET;
        return mBuilder.m_page_type;
    }

    @VIEW_TYPE
    public int getViewType()
    {
        if (mBuilder == null) return VIEW_TYPE.UNSET;
        return mBuilder.m_view_type;
    }


    public static class Builder
    {
        @TAB_TYPE
        int m_tab_type = TAB_TYPE.UNSET;
        @PAGE_TYPE
        int m_page_type = PAGE_TYPE.UNSET;
        @VIEW_TYPE
        int m_view_type = VIEW_TYPE.UNSET;

        @TAB_TYPE
        int currentTabType = TAB_TYPE.UNSET;

        public ArrayList<TabUI> m_tab_type_s = new ArrayList<>();

        public Builder addTabType(int m_tab_type)
        {
            this.m_tab_type |= m_tab_type;
            return this;
        }

        public void setCurrentTabType(@TAB_TYPE int currentTabType)
        {
            this.currentTabType = currentTabType;
        }

        public boolean isTabType(@TAB_TYPE int m_tab_type)
        {
            return (this.m_tab_type & m_tab_type) != 0;
        }

        public Builder setPageType(@PAGE_TYPE int m_page_type)
        {
            this.m_page_type = m_page_type;
            return this;
        }

        public Builder setViewType(@VIEW_TYPE int m_view_type)
        {
            this.m_view_type = m_view_type;
            return this;
        }

        public TabUIConfig build(@NonNull Context context)
        {
            //tab type
            if (isTabType(TAB_TYPE.TAB_BEAUTY))
            {
                TabUI tabUI = new TabUI();
                tabUI.m_type = TAB_TYPE.TAB_BEAUTY;
                tabUI.m_title = context.getString(R.string.beauty_selector_view_tab_beauty);
                tabUI.m_icon_select = R.drawable.ic_shape_beauty;
                tabUI.m_icon_un_select = R.drawable.ic_shape_beauty_grey;
                m_tab_type_s.add(tabUI);
            }

            if (isTabType(TAB_TYPE.TAB_SHAPE))
            {
                TabUI tabUI = new TabUI();
                tabUI.m_type = TAB_TYPE.TAB_SHAPE;
                tabUI.m_title = context.getString(R.string.beauty_selector_view_tab_shape);
                tabUI.m_icon_select = R.drawable.ic_shape_shape;
                tabUI.m_icon_un_select = R.drawable.ic_shape_shape_grey;
                m_tab_type_s.add(tabUI);
            }

            if (isTabType(TAB_TYPE.TAB_FILTER))
            {
                TabUI tabUI = new TabUI();
                tabUI.m_type = TAB_TYPE.TAB_FILTER;
                tabUI.m_title = context.getString(R.string.beauty_selector_view_tab_filter);
                tabUI.m_icon_select = R.drawable.ic_shape_filter;
                tabUI.m_icon_un_select = R.drawable.ic_shape_filter_grey;
                m_tab_type_s.add(tabUI);
            }

            return new TabUIConfig().create(this);
        }
    }

    public static class TabUI
    {
        @TAB_TYPE
        public int m_type = TAB_TYPE.UNSET;

        public String m_title = "";

        @DrawableRes
        public int m_icon_select;
        @DrawableRes
        public int m_icon_un_select;
    }

}
