package cn.poco.album.model;

/**
 * Created by: fwc
 * Date: 2016/8/22
 */
public class FolderInfo {

	private String mName;

	private int mCount = 0;

	private String mCover;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		mCount = count;
	}

	public void addCount() {
		mCount++;
	}

	public String getCover() {
		return mCover;
	}

	public void setCover(String cover) {
		mCover = cover;
	}

	@Override
	public String toString() {
		return "Name: " + mName + ", Count: " + mCount + ", Cover: " + mCover;
	}
}
