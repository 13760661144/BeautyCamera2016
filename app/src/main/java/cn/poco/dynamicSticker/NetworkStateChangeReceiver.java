package cn.poco.dynamicSticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zwq on 2017/02/09 12:10.<br/><br/>
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

    public interface NetworkStateChangeListener {
        void onNetworkDisconnect();
    }

    private NetworkStateChangeListener mNetworkStateChangeListener;

    public void setNetworkStateChangeListener(NetworkStateChangeListener listener) {
        mNetworkStateChangeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobNetInfo != null && wifiNetInfo != null
                && !mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//            Log.i("bbb", "网络不可用");
            if (mNetworkStateChangeListener != null) {
                mNetworkStateChangeListener.onNetworkDisconnect();
            }
        } else {
//            Log.i("bbb", "网络可用");
        }
    }
}
