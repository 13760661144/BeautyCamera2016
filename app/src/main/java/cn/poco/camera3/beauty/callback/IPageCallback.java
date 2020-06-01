package cn.poco.camera3.beauty.callback;

import android.view.View;

import cn.poco.camera3.beauty.TabUIConfig;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public interface IPageCallback
{
    void setShowSelectorView(boolean show);

    void onShowTopBar(boolean show);

    Object getFramePagerData(int position, String frameTag, boolean isUpdate);

    void onLogin();

    void onBindPhone();

    void onPageSelected(int position, TabUIConfig.TabUI tabUI);

    void onSeekBarSlide(View seek, int progress, boolean isStop);
}
