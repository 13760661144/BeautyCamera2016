package cn.poco.album.model;

/**
 * Created by: fwc
 * Date: 2016/8/22
 */
public class VideoInfo implements Cloneable {

	private int mId;

	private String mPath;

	private String mMimeType;

	private int mRotation;

	private long mDuration;

	private long mSize;

	private String mFolderName;

	private long mLastModified;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		mPath = path;
	}

	public String getMimeType() {
		return mMimeType;
	}

	public void setMimeType(String mimeType) {
		mMimeType = mimeType;
	}

	public int getRotation() {
		return mRotation;
	}

	public void setRotation(int rotation) {
		mRotation = rotation;
	}

	public long getSize() {
		return mSize;
	}

	public void setSize(long size) {
		mSize = size;
	}

	public String getFolderName() {
		return mFolderName;
	}

	public void setFolderName(String folderName) {
		mFolderName = folderName;
	}

	public long getLastModified() {
		return mLastModified;
	}

	public void setLastModified(long lastModified) {
		mLastModified = lastModified;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long duration) {
		mDuration = duration;
	}
}
