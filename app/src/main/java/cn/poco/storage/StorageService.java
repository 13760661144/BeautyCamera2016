package cn.poco.storage;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import cn.poco.cloudAlbum.model.TransportInfo;
import cn.poco.cloudAlbum.utils.FileUtils;
import cn.poco.storagesystemlibs.AbsStorageService;
import cn.poco.storagesystemlibs.IStorage;
import cn.poco.storagesystemlibs.ServiceStruct;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import cn.poco.utils.NetworkMonitor;

public class StorageService extends AbsStorageService {

	private static final int APP_ID = 0x09000000;//主要用于区分不同APP的ID
	private static int AC_ID = 0;

	public static final int GET_TRANSPORT_INFO = 0x10000000;
	public static final int CLEAR_TRANSPORT_INFO = 0x20000000;
	public static final int SET_ONLI_WIFI = 0x40000000;
	public static final int STOP = 0x80000000;
	public static final String TRANSPORT_ACTION = "cn.poco.storage.transport";

	/**
	 * 保存传输记录相关
	 */
	public static final String SAVE_FILENAME = "transports.txt";
	public static final String SEPARATOR = "---";

	private boolean mOnlyWifi = true;
	private int mStartId = 0;

	@Override
	public void onCreate()
	{
		super.onCreate();

		SysConfig.Read(this);

		NetworkMonitor.getInstance().startMonitor(this);
		NetworkMonitor.getInstance().addNetworkListener(mNetworkListener);
	}

	private NetworkMonitor.NetworkListener mNetworkListener = new NetworkMonitor.NetworkListener() {
		@Override
		public void onNetworkChanged(int networkType) {
			if (networkType != ConnectivityManager.TYPE_WIFI && mOnlyWifi) {
				for (AbsStorageService.UploadTask uploadTask : mUploadArr) {
					if (uploadTask != null) {
						uploadTask.Cancel();
					}
				}
				mUploadArr.clear();

				for (AbsStorageService.DownloadTask downloadTask : mDownloadArr) {
					if (downloadTask != null) {
						downloadTask.Cancel();
					}
				}
				mDownloadArr.clear();
			}
		}
	};

	@Override
	public void onDestroy() {
		NetworkMonitor.getInstance().removeNetworkListener(mNetworkListener);
		NetworkMonitor.getInstance().stopMonitor();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			mStartId = startId;
			int type = intent.getIntExtra("type", 0);
			switch (type) {
				case GET_TRANSPORT_INFO:
					ArrayList<TransportInfo> infos = FileUtils.readTransportInfos(this, SAVE_FILENAME, SEPARATOR);
					if (infos != null && !infos.isEmpty()) {
						Intent it = new Intent(TRANSPORT_ACTION);
						it.putParcelableArrayListExtra("infos", infos);
						sendBroadcast(it);
					}
					break;
				case CLEAR_TRANSPORT_INFO:
					int acid = intent.getIntExtra("acid", -1);
					if (acid == -1) {
						FileUtils.clearTransportInfos(this, SAVE_FILENAME);
					} else {
						FileUtils.clearTransportInfo(this, SAVE_FILENAME, SEPARATOR, acid);
					}
					break;
				case SET_ONLI_WIFI:
					mOnlyWifi = intent.getBooleanExtra("wifi", true);
					break;
				case STOP :
					stop();
					break;
				case CANCEL_UPLOAD:
					acid = intent.getIntExtra("acid", 0);
					for (AbsStorageService.UploadTask uploadTask : mUploadArr) {
						if (uploadTask.mStr.mAcId == acid) {
							TransportInfo info = (TransportInfo) uploadTask.mStr.mEx;
							info.cancel = 1;
							break;
						}
					}
					break;
				case CANCEL_DOWNLOAD:
					acid = intent.getIntExtra("acid", 0);
					for (AbsStorageService.DownloadTask downloadTask : mDownloadArr) {
						if (downloadTask.mStr.mAcId == acid) {
							TransportInfo info = (TransportInfo) downloadTask.mStr.mEx;
							info.cancel = 1;
							break;
						}
					}
					break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static int GetAcId() {
		if (AC_ID == 0) {
			AC_ID = ((android.os.Process.myPid() << 16) & 0x00FFFFFF) | APP_ID;
		}
		AC_ID++;
		return AC_ID;
	}

	@Override
	public IStorage GetIStorage()
	{
		return AppInterface.GetInstance(this);
	}

	public static void GetTransportInfos(Context context) {
		Intent intent = new Intent(context, StorageService.class);
		intent.putExtra("type", GET_TRANSPORT_INFO);
		context.startService(intent);
	}

	public static void ClearTransportInfos(Context context) {
		Intent intent = new Intent(context, StorageService.class);
		intent.putExtra("type", CLEAR_TRANSPORT_INFO);
		context.startService(intent);
	}

	public static void ClearTransportInfo(Context context, int acid) {
		Intent intent = new Intent(context, StorageService.class);
		intent.putExtra("type", CLEAR_TRANSPORT_INFO);
		intent.putExtra("acid", acid);
		context.startService(intent);
	}

	public static void SetOnlyWifi(Context context, boolean onlyWifi) {
		Intent intent = new Intent(context, StorageService.class);
		intent.putExtra("type", SET_ONLI_WIFI);
		intent.putExtra("wifi", onlyWifi);
		context.startService(intent);
	}

	public static void Stop(Context context) {
		Intent intent = new Intent(context, StorageService.class);
		intent.putExtra("type", STOP);
		context.startService(intent);
	}

	public static int PushUploadTask(Context context, ServiceStruct str) {
		int out = 0;

		if (str != null) {
			out = GetAcId();
			str.mAcId = out;

			Intent it = new Intent(context, StorageService.class);
			it.putExtra("type", StorageService.UPLOAD);
			it.putExtra("str", str);

			context.startService(it);
		}

		return out;
	}

	public static int PushDownloadTask(Context context, ServiceStruct str) {
		int out = 0;

		if (str != null) {
			out = GetAcId();
			str.mAcId = out;

			Intent it = new Intent(context, StorageService.class);
			it.putExtra("type", StorageService.DOWNLOAD);
			it.putExtra("str", str);
			context.startService(it);
		}

		return out;
	}

	public static void CancelUploadTask(Context context, int acid) {
		Intent it = new Intent(context, StorageService.class);
		it.putExtra("type", StorageService.CANCEL_UPLOAD);
		it.putExtra("acid", acid);
		context.startService(it);
	}

	public static void CancelDownloadTask(Context context, int acid) {
		Intent it = new Intent(context, StorageService.class);
		it.putExtra("type", StorageService.CANCEL_DOWNLOAD);
		it.putExtra("acid", acid);
		context.startService(it);
	}

	@Override
	protected void OnSingleComplete(int type, ServiceStruct str) {
		super.OnSingleComplete(type, str);

		stop();
	}

	@Override
	protected void OnSingleFail(int type, ServiceStruct str) {
		super.OnSingleFail(type, str);

		TransportInfo info = (TransportInfo) str.mEx;
		if (info.cancel == 0) {
			info.acid = str.mAcId;
			FileUtils.writeTransportInfo(this, SAVE_FILENAME, SEPARATOR, info);
		}

		// 停止服务
		stop();
	}

	@Override
	protected void OnError(int type, ServiceStruct str) {
		super.OnError(type, str);

		// 停止服务
		stop();
	}

	private void stop() {
		if (mUploadArr.isEmpty() && mDownloadArr.isEmpty()) {
			stopSelf(mStartId);
		}
	}
}
