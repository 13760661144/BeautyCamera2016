package cn.poco.cloudAlbum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.poco.cloudAlbum.model.TransportInfo;
import cn.poco.cloudalbumlibs.model.PhotoInfo;
import cn.poco.storage.StorageService;

/**
 * Created by: fwc
 * Date: 2016/11/4
 */
public class TransportInfoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction().equals(StorageService.TRANSPORT_ACTION)) {
			List<TransportInfo> infos = intent.getParcelableArrayListExtra("infos");

			if (infos != null) {
				TransportImgs transportImgs = TransportImgs.getInstance(context);
				transportImgs.mUploadErrorList.clear();
				transportImgs.mDownloadErrorList.clear();

				ArrayMap<String, List<TransportInfo>> uploadMap = new ArrayMap<>();
				ArrayMap<String, List<TransportInfo>> downloadMap = new ArrayMap<>();

				List<TransportInfo> temp;
				for (TransportInfo info : infos) {
					if (info.type.equals(TransportInfo.UPLOAD)) {
						temp = uploadMap.get(info.folderName);
						if (temp == null) {
							temp = new ArrayList<>();
							uploadMap.put(info.folderName, temp);
						}
						temp.add(info);

					} else if (info.type.equals(TransportInfo.DOWNLOAD)) {
						temp = downloadMap.get(info.folderName);
						if (temp == null) {
							temp = new ArrayList<>();
							downloadMap.put(info.folderName, temp);
						}
						temp.add(info);
					}
				}

				String[] paths;
				for (Map.Entry<String, List<TransportInfo>> entry : uploadMap.entrySet()) {
					temp = entry.getValue();
					if (temp != null && !temp.isEmpty()) {
						paths = new String[temp.size()];
						for (int i = 0; i < paths.length; i++) {
							paths[i] = temp.get(i).path;
						}
						transportImgs.uploadImgs(paths, temp.get(0).folderId, entry.getKey());
					}
				}

				List<PhotoInfo> photoInfos;
				PhotoInfo photoInfo;
				for (Map.Entry<String, List<TransportInfo>> entry : downloadMap.entrySet()) {
					temp = entry.getValue();
					if (temp != null && !temp.isEmpty()) {
						photoInfos = new ArrayList<>();
						for (TransportInfo transportInfo : temp) {
							photoInfo = new PhotoInfo();
							photoInfo.setUrl(transportInfo.path);
							photoInfo.setVolume(transportInfo.size);
							photoInfo.setCoverUrl(transportInfo.path);
							photoInfos.add(photoInfo);
						}
						transportImgs.downloadImgs(photoInfos, entry.getKey());
					}
				}
			}
		}
	}
}
