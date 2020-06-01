package cn.poco.widget.recycle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseItem;

/**
 * Created by lgd on 217/5/10.
 */

public class DownMorePage extends BaseItem
{
    protected FrameLayout mFrameLayout;
    protected AbsConfig mConfig;

    public DownMorePage(@NonNull Context context, AbsConfig config)
    {
        super(context);
        this.mConfig = config;
        init();
    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index)
    {
        if (mFrameLayout != null)
        {
            mFrameLayout.removeAllViews();
        }

        int num = 0;
        Object[] logos = null;
        if (info instanceof RecommendExAdapter.DownloadItemInfo)
        {
            RecommendExAdapter.DownloadItemInfo itemInfo = (RecommendExAdapter.DownloadItemInfo) info;
            num = itemInfo.num;
            logos = itemInfo.m_logos;
        }
        else if (info instanceof RecommendAdapter.DownloadItemInfo)
        {
            RecommendAdapter.DownloadItemInfo itemInfo = (RecommendAdapter.DownloadItemInfo) info;
            num = itemInfo.num;
            logos = itemInfo.m_logos;
        }

        View view = null;
        if (logos != null && logos.length > 2 && num > 0)
        {
            if (mFrameLayout.getChildCount() > 0)
            {
                View childAt = mFrameLayout.getChildAt(0);
                if (childAt instanceof DownMoreView)
                {
                    DownMoreView downMoreView = (DownMoreView) childAt;
                    downMoreView.SetData(logos, num, index);
                }
            }
            else
            {
                mFrameLayout.removeAllViews();
                DownMoreView downMoreView = new DownMoreView(getContext(), mConfig, logos, num);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                mFrameLayout.addView(downMoreView, params);
            }
        }
        else
        {
            if (mFrameLayout.getChildCount() > 0)
            {
                View childAt = mFrameLayout.getChildAt(0);
                if (childAt instanceof DownMoreView2)
                {
                    DownMoreView2 downMoreView = (DownMoreView2) childAt;
                    downMoreView.SetData(logos, num, index);
                }
            }
            else
            {
                mFrameLayout.removeAllViews();
                DownMoreView2 downMoreView = new DownMoreView2(getContext(), mConfig, logos, num);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                mFrameLayout.addView(downMoreView, params);
            }
        }
    }

    private void init()
    {
        mFrameLayout = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mFrameLayout, params);
    }

    @Override
    public void onSelected()
    {

    }

    @Override
    public void onUnSelected()
    {

    }

    @Override
    public void onClick()
    {

    }
}
