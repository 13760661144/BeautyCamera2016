package cn.poco.storage;

import java.util.ArrayList;

import cn.poco.storagesystemlibs.AbsStorageReceiver;
import cn.poco.storagesystemlibs.CloudListener;
import cn.poco.storagesystemlibs.ServiceStruct;

public class StorageReceiver extends AbsStorageReceiver {
	@Override
	public void OnProgress(int type, ServiceStruct str, int progress) {
		int len = s_lst.size();
		for (int i = 0; i < len; i++) {
			s_lst.get(i).OnProgress(type, str, progress);
		}
	}

	@Override
	public void OnComplete(int type, ServiceStruct str) {

		int len = s_lst.size();
		for (int i = 0; i < len; i++) {
			s_lst.get(i).OnComplete(type, str);
		}
	}

	@Override
	public void OnFail(int type, ServiceStruct str) {
		int len = s_lst.size();
		for (int i = 0; i < len; i++) {
			s_lst.get(i).OnFail(type, str);
		}
	}

	@Override
	public void OnError(int type, ServiceStruct str) {
		int len = s_lst.size();
		for (int i = 0; i < len; i++) {
			s_lst.get(i).OnError(type, str);
		}
	}

	protected static ArrayList<CloudListener> s_lst = new ArrayList<>();

	public static void AddCloudListener(CloudListener lst) {
		RemoveCloudListener(lst);
		s_lst.add(lst);
	}

	public static void RemoveCloudListener(CloudListener lst) {
		s_lst.remove(lst);
	}
}
