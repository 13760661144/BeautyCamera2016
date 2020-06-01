package cn.poco.live.dui;

import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/9.
 */

public class DUICtrl {
    public static final String COMMAND_DELETE = "delete";
    public static final String COMMAND_INSERT = "insert";
    public static final String COMMAND_MODIFY = "modify";
    private static int sId = 1;
    private int mParentId;
    private int mId;
    private String mName;
    private String mClass;
    private ArrayList<DUICtrl> mChilds = new ArrayList<DUICtrl>();
    private OnCtrlEventListener mOnCtrlEventListener = null;

    public static final String EVENT_CLICK = "click";
    public static final String EVENT_DOUBLECLICK = "doubleclick";
    public static final String EVENT_SLIDER = "slider";
    public static final String EVENT_CUSTOM = "custom";

    interface OnCtrlEventListener {
        void onEvent(DUICtrl ctrl, String event, Bundle attr);
    }

    public static int createCtrlId() {
        sId++;
        return sId;
    }

    public static void toXml(DUICtrl ctrl, StringBuffer xml) {
        ArrayList<Pair<String, String>> attrs = ctrl.getAttributes();
        xml.append("<ctrl");
        for (int i = 0; i < attrs.size(); i++) {
            Pair<String, String> pair = attrs.get(i);
            xml.append(" " + pair.first + "=\"" + pair.second + "\"");
        }
        xml.append(">");
        int count = ctrl.getChildCount();
        for (int i = 0; i < count; i++) {
            DUICtrl child = ctrl.getChildAt(i);
            if (child != null) {
                toXml(child, xml);
            }
        }
        xml.append("</ctrl>");
    }

    public DUICtrl(String cls) {
        mClass = cls;
        mId = createCtrlId();
    }

    public int getId() {
        return mId;
    }

    public int getParentId() {
        return mParentId;
    }

    public void setName(String name) {
        mName = name;
    }

    public void clearChild()
    {
        if (mChilds != null)
        {
            mChilds.clear();
        }
    }

    public void addChild(DUICtrl ctrl) {
        addChild(ctrl, getChildCount());
    }

    public void addChild(DUICtrl ctrl, int index)
    {
        if (ctrl.mParentId != 0)
        {
            throw new RuntimeException("控件已经被添加到布局里了，请先移除");
        }
        if (index > mChilds.size())
        {
            index = mChilds.size();
        }
        if (index < 0)
        {
            index = 0;
        }
        ctrl.mParentId = mId;
        mChilds.add(index, ctrl);
    }

    public void removeChild(DUICtrl ctrl)
    {
        if (mChilds != null && ctrl != null && mChilds.contains(ctrl))
        {
            ctrl.mParentId = 0;
            mChilds.remove(ctrl);
        }
    }

    public void removeChild(int index) {
        if (index >= 0 && index < mChilds.size()) {
            mChilds.remove(index).mParentId = 0;
        }
    }

    public int getChildCount() {
        return mChilds.size();
    }

    public DUICtrl getChildAt(int i) {
        if (i >= 0 && i < mChilds.size()) {
            return mChilds.get(i);
        }
        return null;
    }

    public void setOnCtrlEventListener(OnCtrlEventListener l) {
        mOnCtrlEventListener = l;
    }

    public void fireEvent(String event, Bundle attr) {
        if (mOnCtrlEventListener != null) {
            mOnCtrlEventListener.onEvent(this, event, attr);
        }
    }

    public ArrayList<Pair<String, String>> getAttributes() {
        ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        list.add(new Pair<String, String>("name", mName));
        list.add(new Pair<String, String>("id", "" + mId));
        list.add(new Pair<String, String>("class", mClass));
        return list;
    }

    public String toXml() {
        StringBuffer xml = new StringBuffer();
        toXml(this, xml);
        return xml.toString();
    }
}
