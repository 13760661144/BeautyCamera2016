package cn.poco.beautifyEyes.page;

import android.content.Context;

import cn.poco.beautifyEyes.site.BeautyBaseSite;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.framework.BaseSite;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/2.
 */

public class BrightEyesPage extends BeautifyEyesBasePage {
    private String mPageName;

    public BrightEyesPage(Context context, BaseSite baseSite) {
        super(context, (BeautyBaseSite) baseSite);
        mPageName = getResources().getString(R.string.beautify4page_liangyan_btn);
        StatisticHelper.countPageEnter(context, mPageName);
        StatisticHelper.ShenCe_countPageEnter(R.string.美颜美图_亮眼页面_主页面);
    }

    @Override
    protected int implementData() {
        return 2;
    }

    @Override
    protected void cancel() {
        super.cancel();
    }

    @Override
    protected void saveBitmap() {
        super.saveBitmap();
    }

    @Override
    protected void switchToPinFacePoint() {
        super.switchToPinFacePoint();
    }


    @Override
    public void onPause() {
        super.onPause();
        StatisticHelper.countPagePause(getContext(), mPageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatisticHelper.countPageResume(getContext(), mPageName);
    }

    @Override
    public void onClose() {
        super.onClose();
        StatisticHelper.countPageLeave(getContext(), mPageName);
        StatisticHelper.ShenCe_countPageLeave(R.string.美颜美图_亮眼页面_主页面);
    }
}
