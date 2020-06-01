package cn.poco.resource;

import java.util.ArrayList;

/**
 * Created by zwq on 2017/06/12 11:11.<br/><br/>
 */

public class RealTimeMakeUpRes {

    public int mId;
    private ArrayList<RealTimeMakeUpSubRes> mSubResList;

    private int mSubResIndex = -1;
    private int[] mMakeUpFrameArr;

    public RealTimeMakeUpRes() {

    }

    public void setSubResList(ArrayList<RealTimeMakeUpSubRes> subResList) {
        mSubResList = subResList;
    }

    public ArrayList<RealTimeMakeUpSubRes> getSubResList() {
        return mSubResList;
    }

    /**
     * 彩妆出现的帧范围
     * @param makeUpFrameArr eg:[0, 10) 第1帧开显示，第10帧时消失
     */
    public void setMakeUpFrameArr(int[] makeUpFrameArr) {
        mSubResIndex = -1;
        mMakeUpFrameArr = makeUpFrameArr;
    }

    public int getSubResIndex() {
        return mSubResIndex;
    }

    public void resetAll() {
        mSubResIndex = -1;
        if (mSubResList != null) {
            for (RealTimeMakeUpSubRes subRes : mSubResList) {
                if (subRes != null) {
                    subRes.resetTextureIds();
                }
            }
        }
    }

    public RealTimeMakeUpSubRes getSubRes(int frameIndex) {
        mSubResIndex = -1;
        if (mSubResList != null) {
            if (mMakeUpFrameArr != null) {
                for (int i = 0; i < mMakeUpFrameArr.length / 2; i++) {// 0 10 20   [0, 10),[10, 20)
                    if (frameIndex >= mMakeUpFrameArr[i * 2] && frameIndex < mMakeUpFrameArr[i * 2 + 1]) {
                        mSubResIndex = i;
                        break;
                    }
                }
            }
            if (mSubResIndex > -1 && mSubResIndex < mSubResList.size()) {
                RealTimeMakeUpSubRes subRes = mSubResList.get(mSubResIndex);
                if (subRes != null) {
                    subRes.mResIndex = mSubResIndex;
                }
                return subRes;
            }
        }
        return null;
    }
}
