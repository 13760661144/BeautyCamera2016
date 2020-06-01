package cn.poco.camera3.beauty.page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.camera3.beauty.callback.IPageCallback;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.viewpagerIndicator.IconPagerAdapter;

/**
 * @author lmx
 *         Created by lmx on 2018-01-15.
 */

public class BeautyFramePagerAdapter extends PagerAdapter
        implements IconPagerAdapter
{
    protected TabUIConfig mTabUIConfig;
    protected Context mContext;

    private ArrayList<TabUIConfig.TabUI> mTabUIs;

    protected IPageCallback mCallback;

    public BeautyFramePagerAdapter(@NonNull Context context,
                                   @NonNull IPageCallback callback)
    {
        mContext = context;
        mCallback = callback;
    }

    public void setCallback(IPageCallback mCallback)
    {
        this.mCallback = mCallback;
    }

    public void setTabUIConfig(TabUIConfig mTabUIConfig)
    {
        this.mTabUIConfig = mTabUIConfig;
        if (mTabUIConfig != null)
        {
            this.mTabUIs = mTabUIConfig.getTabUIs();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mTabUIs == null) return null;
        TabUIConfig.TabUI tabUI = mTabUIs.get(position);
        switch (tabUI.m_type)
        {
            case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
            {
                //美颜
                BeautyFramePager framePager = new BeautyFramePager(mContext, getTabConfig());
                framePager.setTag(framePager.getFramePagerTAG());
                framePager.setCallback(this.mCallback);
                addToContainer(container, framePager);
                HashMap<String, Object> params = new HashMap<>();
                if (this.mCallback != null)
                {
                    params.put(BeautyFramePager.BUNDLE_KEY_DATA_BEAUTY_LIST, this.mCallback.getFramePagerData(position, framePager.getFramePagerTAG(), false));
                }
                params.put(BaseFramePager.BUNDLE_KEY_POSITION, position);
                framePager.setData(params);
                return framePager;
            }
            case TabUIConfig.TAB_TYPE.TAB_SHAPE:
            {
                //脸型
                ShapeFramePager framePager = new ShapeFramePager(mContext, getTabConfig());
                framePager.setTag(framePager.getFramePagerTAG());
                addToContainer(container, framePager);
                framePager.setCallback(this.mCallback);
                HashMap<String, Object> params = new HashMap<>();
                if (this.mCallback != null)
                {
                    params.put(ShapeFramePager.BUNDLE_KEY_DATA_SHAPE_LIST, this.mCallback.getFramePagerData(position, framePager.getFramePagerTAG(), false));
                }
                params.put(BaseFramePager.BUNDLE_KEY_POSITION, position);
                framePager.setData(params);
                return framePager;
            }
            case TabUIConfig.TAB_TYPE.TAB_FILTER:
            {
                //滤镜
                FilterFramePager framePager = new FilterFramePager(mContext, getTabConfig());
                addToContainer(container, framePager);
                framePager.setTag(framePager.getFramePagerTAG());
                framePager.setCallback(this.mCallback);
                HashMap<String, Object> params = new HashMap<>();
                if (this.mCallback != null)
                {
                    params.put(FilterFramePager.BUNDLE_KEY_DATA_FILTER_LIST, this.mCallback.getFramePagerData(position, framePager.getFramePagerTAG(), false));
                }
                params.put(BaseFramePager.BUNDLE_KEY_POSITION, position);
                framePager.setData(params);
                return framePager;
            }
            case TabUIConfig.TAB_TYPE.UNSET:
            default:
                return null;
        }
    }

    private void addToContainer(ViewGroup container, BaseFramePager baseFramePager)
    {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        baseFramePager.setLayoutParams(params);
        container.addView(baseFramePager);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object instanceof BaseFramePager)
        {
            ((BaseFramePager) object).onDestroyView();
        }
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        if (mTabUIs == null) return "";
        return mTabUIs.get(position).m_title;
    }

    @Override
    public int[] getIconResId(int index)
    {
        int[] ids = new int[2];
        if (mTabUIs == null) return ids;
        TabUIConfig.TabUI tabUI = mTabUIs.get(index);
        ids[0] = tabUI.m_icon_select;
        ids[1] = tabUI.m_icon_un_select;
        return ids;
    }

    @Override
    public int getCount()
    {
        return mTabUIs != null ? mTabUIs.size() : 0;
    }

    private TabUIConfig getTabConfig()
    {
        return mTabUIConfig;
    }
}
