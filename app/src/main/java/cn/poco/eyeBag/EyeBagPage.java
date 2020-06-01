package cn.poco.eyeBag;

import android.content.Context;

import cn.poco.beautifyEyes.page.BeautifyEyesBasePage;
import cn.poco.beautifyEyes.site.BeautyBaseSite;
import cn.poco.beautifyEyes.util.StatisticHelper;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/2/9.
 */

public class EyeBagPage extends BeautifyEyesBasePage {
    private String mPageName;

    public EyeBagPage(Context context, BeautyBaseSite site) {
        super(context, site);
        mPageName = getResources().getString(R.string.beautify4page_quyandai_btn);
        StatisticHelper.countPageEnter(context, mPageName);
        StatisticHelper.ShenCe_countPageEnter(R.string.美颜美图_祛眼袋页面_主页面);
    }

    @Override
    protected int implementData() {
        return 1;
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
        StatisticHelper.ShenCe_countPageLeave(R.string.美颜美图_祛眼袋页面_主页面);
    }
}
