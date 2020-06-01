package cn.poco.dynamicSticker.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cn.poco.dynamicSticker.StickerSoundRes;
import cn.poco.resource.FilterRes;

/**
 * Created by zwq on 2017/04/28 16:58.<br/><br/>
 */
public class StickerRes {

    public String mId;
    public String mName;
    public int mSWidth;//标准分辨率宽
    public int mSHeight;
    public ArrayList<Map.Entry<String, StickerSubRes>> mOrderStickerRes;//已排序的素材

    public String mAction;//动作提示文案

    public boolean mIs3DRes;

    public StickerSoundRes mStickerSoundRes;

    public FilterRes mFilterRes;//滤镜素材

    //动画最大时长（单位毫秒）
    public int mMaxFrameDurations;

    public StickerSplitScreen mStickerSplitScreen;//分屏数据  各部分帧数需保持一致

    public StickerRes() {

    }

    public void setStickerSoundRes(StickerSoundRes mStickerSoundRes) {
        this.mStickerSoundRes = mStickerSoundRes;
    }

    public void setMaxFrameDurations(int mMaxFrameDurations) {
        this.mMaxFrameDurations = mMaxFrameDurations;
    }

    /**
     * 添加贴纸数据并处理排序
     *
     * @param stickerSubRes
     */
    public void setStickerSubRes(HashMap<String, StickerSubRes> stickerSubRes) {
        if (stickerSubRes == null || stickerSubRes.isEmpty()) {
            mOrderStickerRes = null;
            return;
        }

        mOrderStickerRes = new ArrayList<>(stickerSubRes.entrySet());

        //按层级排序
        Collections.sort(mOrderStickerRes, new Comparator<Map.Entry<String, StickerSubRes>>() {
            @Override
            public int compare(Map.Entry<String, StickerSubRes> lhs, Map.Entry<String, StickerSubRes> rhs) {
                StickerSubRes entity1 = lhs.getValue();
                StickerSubRes entity2 = rhs.getValue();
                if (entity1.getTier() < entity2.getTier()) {
                    return -1;
                } else if (entity1.getTier() == entity2.getTier()) {
                    if (entity1.getLayer() < entity2.getLayer()) {
                        return -1;
                    } else if (entity1.getLayer() == entity2.getLayer()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }
            }
        });
    }

    /**
     *
     * @return sec 秒
     */
    public float calculateMaxDuration() {
        float maxDuration = 0.0f;
        if (mOrderStickerRes == null || mOrderStickerRes.isEmpty()) {
            return maxDuration;
        }
        for (Map.Entry<String, StickerSubRes> orderStickerRe : mOrderStickerRes) {
            if (orderStickerRe == null) {
                continue;
            }
            StickerSubRes stickerSubRes = orderStickerRe.getValue();
            if (stickerSubRes != null && stickerSubRes.mFrames != null) {
                float duration = 0;
                for (StickerSpriteFrame frame : stickerSubRes.mFrames) {
                    if (frame != null) {
                        duration += frame.getDuration();
                    }
                }
                if (duration >= maxDuration) {
                    maxDuration = duration;
                }
            }
        }
        return maxDuration;
    }

}
