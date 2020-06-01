package cn.poco.camera3.beauty.page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.poco.camera3.beauty.callback.IPageCallback;
import cn.poco.camera3.beauty.TabUIConfig;

/**
 * @author lmx
 *         Created by lmx on 2018-01-15.
 */

public abstract class BaseFramePager extends FrameLayout implements IFramePager
{
    public static final String BUNDLE_KEY_POSITION = "bundle_key_position";

    public int mPosition;
    public HashMap<String, Object> mParams;
    public IPageCallback mCallback;

    private TabUIConfig mTabUIConfig;

    public BaseFramePager(@NonNull Context context, TabUIConfig mTabUIConfig)
    {
        super(context);
        this.mTabUIConfig = mTabUIConfig;
        initData();
        onCreateContainerView(this);
    }

    @Override
    public void setData(HashMap<String, Object> data)
    {
        if (data != null)
        {
            if (data.containsKey(BaseFramePager.BUNDLE_KEY_POSITION))
            {
                mPosition = (int) data.get(BaseFramePager.BUNDLE_KEY_POSITION);
            }

            mParams = (HashMap<String, Object>) data.clone();
        }
    }

    @Override
    public void onDestroyView()
    {

    }

    @Override
    public void onClose()
    {
        mParams = null;
        mCallback = null;
        mTabUIConfig = null;
    }

    protected @TabUIConfig.PAGE_TYPE int getPageType()
    {
        return mTabUIConfig == null ? TabUIConfig.PAGE_TYPE.UNSET : mTabUIConfig.getPageType();
    }

    public void setCallback(IPageCallback mCallback)
    {
        this.mCallback = mCallback;
    }

    public TabUIConfig getTabUIConfig()
    {
        return mTabUIConfig;
    }

    public abstract void onCreateContainerView(@NonNull FrameLayout parentLayout);

    public abstract void initData();

    public abstract String getFramePagerTAG();
}
