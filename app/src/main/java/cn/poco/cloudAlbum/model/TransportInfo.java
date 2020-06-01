package cn.poco.cloudAlbum.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by: fwc
 * Date: 2016/11/4
 */
public class TransportInfo implements Parcelable, Serializable {

	public static final String UPLOAD = "upload";
	public static final String DOWNLOAD = "download";

	/**
	 * 类型，上传或下载
	 */
	public String type;

	/**
	 * Acid
	 */
	public int acid;

	/**
	 * 文件夹id
	 */
	public String folderId = "";

	/**
	 * 文件夹名字
	 */
	public String folderName;

	/**
	 * 图片路径，上传为本地路径，下载为url
	 */
	public String path;

	/**
	 * 图片大小，下载用
	 */
	public String size = "";

	/**
	 * 标记是否自己取消传输任务而导致的失败
	 */
	public int cancel = 0;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (folderId == null) {
			folderId = "";
		}

		dest.writeString(type);
		dest.writeInt(acid);
		dest.writeString(folderId);
		dest.writeString(folderName);
		dest.writeString(path);
		dest.writeString(size);
		dest.writeInt(cancel);
	}

	public static final Creator<TransportInfo> CREATOR = new Creator<TransportInfo>() {

		public TransportInfo createFromParcel(Parcel in) {
			TransportInfo info = new TransportInfo();
			info.type = in.readString();
			info.acid = in.readInt();
			info.folderId = in.readString();
			info.folderName = in.readString();
			info.path = in.readString();
			info.size = in.readString();
			info.cancel = in.readInt();

			return info;
		}

		public TransportInfo[] newArray(int size) {
			return new TransportInfo[size];
		}
	};

}
