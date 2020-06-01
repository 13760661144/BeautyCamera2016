package cn.poco.campaignCenter.widget;

/**
 * Created by Shine on 2017/1/13.
 */

public interface CampaignDataState {
    int STATE_LOADING = 1 << 0;
    int STATE_FINISH_LOADED = 1 << 1;
    int STATE_LOED_FAIL = 1 << 2;

    int BEGIN_WITH_THEME_SKIN = 1;
    int BEGIN_WITH_OTHER = 2;


    void loadingData(double progress);
    void failToLoadData(double proress, int style);
    void succedLoadData(double progress);
}
