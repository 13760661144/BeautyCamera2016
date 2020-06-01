package cn.poco.dynamicSticker.v2;

import java.util.ArrayList;

/**
 * Created by zwq on 2018/01/16 10:45.<br/><br/>
 * 贴纸分屏数据及切换逻辑
 */
public class StickerSplitScreen {

    public static class MaskData {
        public String pic;//色块图片
        //public int[] area;//指定区域
        public int compositeMode = 0;//混合模式
        public float opaqueness = 1.0f;//混合模式不透明度

        public int picTextureId;
        public float picRatio;// width/height

        public void reset() {
            picTextureId = -1;
            picRatio = 0.0f;
        }

    }

    public static class SplitData {

        public int s;//分屏数（平均分屏）
        public int from;//从第1帧开始（当type=0时，此参数无效）
        public int count;//到第10帧结束（当type=0时，此参数无效）
        public MaskData[] maskData;//色块数据
        public int[] maskIndex;//指定区域色块索引

        public int rows = 1;//横向分屏数
        public int columns = 1;//纵向分屏数 （rows=1且columns=1时，表示不分屏）

        public void calculateRowColumn() {
            if (s == 4) {//4分屏
                rows = 2;
                columns = 2;
            } else if (s == 9) {//9分屏
                rows = 3;
                columns = 3;
            } else if (s == 16) {//16分屏
                rows = 4;
                columns = 4;
            }
        }
    }

    private int type;//0:固定分屏，1:动作触发分屏，2:动画触发分屏
    private String action;//脸部动作触发类型 （当type=1时，此参数才有效）
    private ArrayList<SplitData> mSplitDatas;//type=0时只读取数组中的第一个

    private boolean mHasReset;
    private int mDataIndex = -1;
    private boolean mHasAction;
    private boolean mAnimStart;
    private int mAnimFrameCount;
    private int mAnimLastFrameIndex = -1;
    private int mAnimCount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<SplitData> getSplitDatas() {
        return mSplitDatas;
    }

    public void setSplitDatas(ArrayList<SplitData> splitDatas) {
        mSplitDatas = splitDatas;
    }

    public boolean isHasReset() {
        return mHasReset;
    }

    public void setHasReset(boolean hasReset) {
        mHasReset = hasReset;
    }

    public void reset() {
        if (mSplitDatas != null) {
            for (SplitData splitData : mSplitDatas) {
                if (splitData != null && splitData.maskData != null) {
                    for (MaskData maskData : splitData.maskData) {
                        if (maskData != null) {
                            maskData.reset();
                        }
                    }
                }
            }
        }
        mHasReset = true;
        mDataIndex = -1;

        mHasAction = false;
        mAnimStart = false;
        mAnimFrameCount = 0;
        mAnimLastFrameIndex = -1;
        mAnimCount = 0;
    }

    public void setHasAction(boolean hasAction) {
        mHasAction = hasAction;
    }

    /**
     *
     * @param allFrameCount
     * @param frameIndex
     * @return  -2:不切换(保持不变), -1:切换为null
     */
    public int getSplitDataIndex(int allFrameCount, int frameIndex) {
        if (mSplitDatas == null) {
            return -2;
        }

        int index = -2;
        if (type == 1) {
            if (mHasAction && !mAnimStart) {
                mHasAction = false;
                mAnimStart = true;
            }
            //Log.i("vvv", "getSplitDataIndex: " + mAnimStart + ", " + frameIndex + ", " + mDataIndex + ", " + mAnimLastFrameIndex + ", " + mAnimCount + ", " + mAnimFrameCount);
            if (mAnimStart) {
                if (mAnimLastFrameIndex != -1 && mAnimFrameCount > 0 && mAnimCount <= mAnimFrameCount) {
                    if (frameIndex == mAnimLastFrameIndex) {
                        return -2;
                    } else {
                        if (frameIndex > mAnimLastFrameIndex) {
                            mAnimCount += (frameIndex - mAnimLastFrameIndex);
                        } else {
                            mAnimCount += (((allFrameCount - 1) - mAnimLastFrameIndex) + (frameIndex + 1));
                        }
                        mAnimLastFrameIndex = frameIndex;
                        if (mAnimCount <= mAnimFrameCount) {
                            return -2;
                        }
                    }
                }
                index = (mDataIndex + 1) % mSplitDatas.size();
                if (index < mDataIndex) {//结束 重新计算
                    mDataIndex = -1;
                    mHasAction = false;
                    mAnimStart = false;
                    mAnimFrameCount = 0;
                    mAnimLastFrameIndex = -1;
                    mAnimCount = 0;
                    return -1;
                }
                if (index != mDataIndex) {
                    SplitData splitData = mSplitDatas.get(index);
                    if (splitData != null) {
                        mAnimFrameCount = splitData.count;
                        mAnimLastFrameIndex = frameIndex;
                        //mAnimCount = 1;
                    } else {
                        index = -2;
                    }
                    mAnimCount = 1;
                    splitData = null;
                }
            } else {
                return -2;
            }

        } else if (type == 2) {
            SplitData splitData = null;
            for (int i = 0; i < mSplitDatas.size(); i++) {// 0 10 20
                splitData = mSplitDatas.get(i);
                //frameIndex从0开始
                if (splitData != null && frameIndex >= splitData.from - 1 && frameIndex < (splitData.from - 1) + splitData.count) {
                    index = i;
                    break;
                }
            }
            splitData = null;

            if (index == -2 && mDataIndex > -1) {//中间部分没分屏或者分屏结束
                mDataIndex = -1;
                return -1;
            }
        } else { //type = 0;
            index = 0;
        }
        if (index > -1 && index != mDataIndex) {
            mDataIndex = index;
            return mDataIndex;
        }
        return -2;
    }

    public SplitData getSplitData(int index) {
        if (mSplitDatas == null || index < 0 || index >= mSplitDatas.size()) {
            return null;
        }
        return mSplitDatas.get(index);
    }
}
