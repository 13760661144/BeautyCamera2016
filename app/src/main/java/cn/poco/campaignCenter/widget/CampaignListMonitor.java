package cn.poco.campaignCenter.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shine on 2017/1/13.
 */

public class CampaignListMonitor{
    private List<CampaignDataState> observers = new ArrayList<>();
    private static CampaignListMonitor instance;
    public int mCurrentState;

    private CampaignListMonitor (){

    }

    public static CampaignListMonitor getInstance() {
        if (instance == null) {
            instance = new CampaignListMonitor();
        }
        return instance;
    }


    public void notifyObservers(int state, double progress, int style) {
        mCurrentState = state;
        for (CampaignDataState observer : observers) {
        if (state == CampaignDataState.STATE_LOADING) {
            observer.loadingData(progress);
        } else if (state == CampaignDataState.STATE_FINISH_LOADED) {
            observer.succedLoadData(progress);
        } else if (state == CampaignDataState.STATE_LOED_FAIL) {
            observer.failToLoadData(progress, style);
        }
        }
    }

    public void addObservers(CampaignDataState campaignDataState) {
        if (observers.indexOf(campaignDataState) == -1) {
            observers.add(campaignDataState);
        }
    }

    public void clear() {
        observers.clear();
        instance = null;
    }



}
