package cn.poco.campaignCenter.manager;

import android.os.Handler;
import android.os.Looper;

import cn.poco.campaignCenter.api.CampaignApi;
import cn.poco.campaignCenter.api.CampaignWebUtil;
import cn.poco.campaignCenter.api.ICampaign;
import cn.poco.tianutils.NetCore2;


/**
 * Created by Shine on 2016/11/29.
 */

public class ConnectionsManager {

    private static volatile ConnectionsManager Instance = null;
    private static final String DEFAULT_COUNT_PER_PAGE = "10";
    private Handler mHandler;

    // singleton
    public static ConnectionsManager getInstacne() {
        ConnectionsManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ConnectionsManager();
                }
            }
        }
        return localInstance;
    }

    public ConnectionsManager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void getCampaignInfo(final String groupPosition, final String pageIndex, final String pageCount, final ICampaign iCampaign, final RequestDelegate delegate) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                final CampaignApi campaignApi = CampaignWebUtil.getCampaignInfoFromServer(groupPosition, pageIndex, pageCount, iCampaign);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (campaignApi != null && campaignApi.mProtocolCode == 200 && campaignApi.mCode == 0) {
                            if (delegate != null) {
                                delegate.run(campaignApi, null);
                            }
                        } else {
                            NetWorkError error = new NetWorkError();
                            if (campaignApi != null) {
                                error.errorCode = campaignApi.mCode;
                                error.errorDescription = campaignApi.mProtocolMsg;
                                if (delegate != null) {
                                    delegate.run(null, error);
                                }
                            } else {
                                if (delegate != null) {
                                    delegate.run(null, error);
                                }
                            }
                        }
                    }
                });
            }
        };
        thread.start();
    }

    public void getCampaignInfo(String groupPosition, String pageIndex, ICampaign iCampaign, RequestDelegate delegate) {
        getCampaignInfo(groupPosition, pageIndex, DEFAULT_COUNT_PER_PAGE, iCampaign, delegate);
    }

    public void downloadImage(final String url, final String filePath, final RequestDelegate delegate) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                NetCore2 netCore2 = new NetCore2();
                final NetCore2.NetMsg msg = netCore2.HttpGet(url, null, filePath, null);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (delegate != null) {
                            if (msg != null && msg.m_stateCode == 200) {
                                delegate.run(filePath, null);
                            }else {
                                NetWorkError netWorkError = new NetWorkError();
                                if (msg != null) {
                                    netWorkError.errorCode = msg.m_stateCode;
                                }
                                delegate.run(null, netWorkError);
                            }
                        }
                    }
                });
            }
        };
        thread.start();
    }

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
    }


    public interface RequestDelegate{
        void run(Object response, NetWorkError error);
    }

    public static class NetWorkError{
        public int errorCode;
        public String errorDescription;
    }
}
