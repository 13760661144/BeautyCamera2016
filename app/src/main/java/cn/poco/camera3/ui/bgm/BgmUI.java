package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.camera3.ui.decoration.BgmItemDecoration;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.video.AudioStore;
import my.beautyCamera.R;

public class BgmUI extends FrameLayout
{
    private OnBgmUIControlCallback mUIControlCB;
    private boolean mBtnClickable = true;

    public void SetOnBgmUIControlCB(OnBgmUIControlCallback cb)
    {
        mUIControlCB = cb;
        if (mAdapter != null)
        {
            mAdapter.SetOnItemClickListener(cb);
        }
    }

    public interface OnBgmUIControlCallback
    {
        void onClickCenterBtn(MyStatusButton centerView);

        void onBgmItemClick(PreviewBgmInfo info);

        void onOpenClipView(PreviewBgmInfo info);

        void onDownloadFailed();
    }

    private FrameLayout mTopLayout;
    private MyStatusButton mBmgCenterBtn;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutMgr;
    private BgmAdapter mAdapter;

    public BgmUI(@NonNull Context context)
    {
        super(context);
        initView(context);
    }

    private void initView(Context context)
    {
        mTopLayout = new FrameLayout(context);
        mTopLayout.setBackgroundColor(Color.WHITE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mTopLayout, params);
        {
            mBmgCenterBtn = new MyStatusButton(getContext());
            mBmgCenterBtn.setOnClickListener(mClickListener);
            mBmgCenterBtn.setData(R.drawable.bgm_center_icon, getContext().getString(R.string.bgm_center_name));
            mBmgCenterBtn.setBtnStatus(true, true);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mTopLayout.addView(mBmgCenterBtn, params);
        }

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setBackgroundColor(0xe6f0f0f0);
        mLayoutMgr = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutMgr);
        mRecyclerView.addItemDecoration(new BgmItemDecoration());
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(88);
        this.addView(mRecyclerView, params);

        initAdapter();
    }

    private void initAdapter()
    {
        mAdapter = new BgmAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setBtnClickable(boolean able)
    {
        mBtnClickable = able;
        if (mAdapter != null)
        {
            mAdapter.setItemClickable(able);
        }
    }

    public void updateAdapterInfo(AudioStore.AudioInfo audioInfo, int info_id)
    {
        if (mAdapter != null) mAdapter.updateAdapterInfo(audioInfo, info_id);
    }

    public void insertBgmLocalInfo(AudioStore.AudioInfo audioInfo)
    {
        if (mAdapter != null)
        {
            mAdapter.insertBgmLocalInfo(audioInfo);
        }
    }

    private OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!mBtnClickable) return;

            if (mUIControlCB != null)
            {
                if (v == mBmgCenterBtn)
                {
                    mUIControlCB.onClickCenterBtn(mBmgCenterBtn);
                }
            }
        }
    };

    public MyStatusButton getBmgCenterBtn()
    {
        return mBmgCenterBtn;
    }

    public void ClearMemory()
    {
        SetOnBgmUIControlCB(null);

        for (int i = 0; i < mRecyclerView.getChildCount(); i++)
        {
            View v = mRecyclerView.getChildAt(i);
            if (v != null && v instanceof FrameLayout)
            {
                BgmCircleView bcv = (BgmCircleView) v.findViewById(R.id.bgm_item_view);
                bcv.setOnTouchListener(null);
                bcv.setOnClickListener(null);
                bcv.setOnLongClickListener(null);
                bcv.ClearMemory();
            }
        }

        mAdapter.ClearMemory();
    }
}
