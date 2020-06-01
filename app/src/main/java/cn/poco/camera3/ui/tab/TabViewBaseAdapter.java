package cn.poco.camera3.ui.tab;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.info.TabInfo;
import cn.poco.camera3.config.TabItemConfig;

public abstract class TabViewBaseAdapter implements View.OnClickListener, View.OnTouchListener
{
    // 事件监听
    protected OnItemClickListener mOnItemClickListener;

    // 数据集 --> 一般通过 config 获得
    protected ArrayList<TabInfo> mData;

    protected CameraUIConfig mConfig;

    TabItemConfig mItemConfig169;

    TabItemConfig mItemConfig43;

    private View mView;

    TabViewBaseAdapter(CameraUIConfig config)
    {
        mConfig = config;
        mData = mConfig.GetTabData();
        mItemConfig169 = mConfig.GetTabItemConfig169();
        mItemConfig43 = mConfig.GetTabItemConfig43();
    }

    protected int getItemViewType(int position)
    {
        return 0;
    }

    public abstract int getItemCount();

    public abstract View onCreateView(ViewGroup parent, int viewType, int position);

    public abstract void onBindView(View view, int position);

    public void setView(View view)
    {
        if (view instanceof TabView)
        {
            mView = view;
        }
    }

    TabInfo getTabInfoByIndex(int index)
    {
        return (mData != null && index >= 0 && index < getItemCount()) ? mData.get(index) : null;
    }

    public void notifyDateChange()
    {
        if (mView != null && mView instanceof TabView)
        {
            TabView view = (TabView) mView;
            view.updateChildrenUI();
        }
    }

    public void ClearMemory()
    {
        mConfig.ClearMemory();
        mView = null;
        mOnItemClickListener = null;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;
    }

    public void setItemClickListener(OnItemClickListener listener)
    {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }
}
