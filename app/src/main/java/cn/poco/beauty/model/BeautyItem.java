package cn.poco.beauty.model;

import android.graphics.Bitmap;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class BeautyItem {

	public int type;

	public String title;

	public Bitmap thumb;

	public boolean select = false;

	public BeautyItem(int type, String title) {
		this.type = type;
		this.title = title;
	}
}
