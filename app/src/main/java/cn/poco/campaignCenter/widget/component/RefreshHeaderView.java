package cn.poco.campaignCenter.widget.component;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.IRecyclerView;
import cn.poco.utils.WaitAnimDialog;

/**
 * Created by Shine on 2017/1/17.
 */

public class RefreshHeaderView extends FrameLayout implements IRecyclerView.RefreshTrigger{
    private WaitAnimDialog.WaitAnimView mLoadingView;

    public RefreshHeaderView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mLoadingView = new WaitAnimDialog.WaitAnimView(context);
        setSkin();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mLoadingView.setLayoutParams(params);
        this.addView(mLoadingView);
        mLoadingView.stop();
        mLoadingView.invalidate();
    }

    public void setSkin() {
        mLoadingView.setColor(ImageUtils.GetSkinColor1(ImageUtils.GetSkinColor()),ImageUtils.GetSkinColor2(ImageUtils.GetSkinColor()));
    }


    @Override
    public void onStart(boolean automatic, int headerHeight, int finalHeight) {

    }

    @Override
    public void onMove(boolean finished, boolean automatic, int moved) {

    }

    @Override
    public void onRefresh() {
        mLoadingView.start();
        mLoadingView.invalidate();
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        restoreToDefault();
    }

    @Override
    public void onReset() {
        restoreToDefault();
    }

    private void restoreToDefault() {
        if (mLoadingView != null) {
            mLoadingView.stop();
            mLoadingView.invalidate();
        }
    }

}
