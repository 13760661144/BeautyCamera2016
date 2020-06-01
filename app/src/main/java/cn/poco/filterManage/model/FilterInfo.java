package cn.poco.filterManage.model;

import java.util.ArrayList;
import java.util.List;

import cn.poco.MaterialMgr2.BaseItemInfo;
import cn.poco.filterManage.FilterMorePage;
import cn.poco.resource.FilterRes;
import cn.poco.resource.ThemeRes;

/**
 * Created by: fwc
 * Date: 2017/3/31
 */
public class FilterInfo {

	public ThemeRes themeRes;

	public String image;
	public String name;
	public String description;
	public boolean check = false;

	public int uri = BaseItemInfo.URI_NONE;
	public int state = BaseItemInfo.PREPARE;
	public List<FilterRes> ress = new ArrayList<>();
	public int progress = 0;
	public boolean lock = false;

	private float mDy = 0;
	private float mRatio = 0;

	public void addY(float dy) {
		mDy -= dy;
		if (mDy > FilterMorePage.sMaxDelta) {
			mDy = FilterMorePage.sMaxDelta;
		} else if (mDy < -FilterMorePage.sMaxDelta) {
			mDy = -FilterMorePage.sMaxDelta;
		}
	}

	public void resetY() {
		mDy = 0;
	}

	public float getDy() {
		return mDy;
	}

	public float getRatio() {
		return mRatio;
	}

	public void setRatio(float ratio) {
		mRatio = ratio;
	}
}
