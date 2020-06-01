package cn.poco.album.model;

/**
 * Created by: fwc
 * Date: 2016/8/22
 */
public class PhotoInfo implements Cloneable {

	private int mId;

	private String mImagePath;

	private int mRotation;

	private long mSize;

	private String mFolderName;

	private long mLastModified;

	private boolean mSelected;

	private boolean mShowEdit;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getImagePath() {
		return mImagePath;
	}

	public void setImagePath(String imagePath) {
		mImagePath = imagePath;
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

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	public boolean isShowEdit() {
		return mShowEdit;
	}

	public void setShowEdit(boolean showEdit) {
		mShowEdit = showEdit;
	}

	public PhotoInfo Clone() {
		PhotoInfo out = null;
		try {
			out = (PhotoInfo)clone();
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return out;
	}
}
