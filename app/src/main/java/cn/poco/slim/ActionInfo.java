package cn.poco.slim;

import java.util.ArrayList;

/**
 * Created by lgd on 2016/12/16.
 * 记录当前记录
 */

public class ActionInfo {
	enum ActionType {
		Manual,
		Tool
	}

	private int curIndex = -1;
	private ArrayList<ActionType> actionRecords = new ArrayList<>();

	//增加记录，移除后面的
	void addRecord(ActionType actionType) {
		for (int i = actionRecords.size() - 1; i > curIndex; i--) {
			if (i >= 0) {
				actionRecords.remove(i);
			}
		}
		curIndex++;
		actionRecords.add(actionType);
	}

	public void setCurIndex(int curIndex) {
		this.curIndex = curIndex;
	}

	public int getCurIndex() {
		return curIndex;
	}

	public int getSize() {
		return actionRecords.size();
	}

	public ActionType getLast() {
		ActionType type = null;
		int len = actionRecords.size();
		if (len > 0 && curIndex > -1 && curIndex <= actionRecords.size() - 1) {
			//取上一条记录，判断是drag还是tools
			type = actionRecords.get(curIndex--);
		} else {
			curIndex = -1;
		}
		return type;
	}

	public ActionType getNext() {
		ActionType type = null;
		int len = actionRecords.size();
		if (len > 0 && curIndex < actionRecords.size() - 1 && curIndex >= -1) {
			//取上一条记录，判断是drag还是tools
			type = actionRecords.get(++curIndex);
		} else {
			curIndex = actionRecords.size() - 1;
		}
		return type;
	}

}
