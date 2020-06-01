package cn.poco.dynamicSticker.v2;

import android.graphics.Rect;

/**
 * Created by zwq on 2017/04/05 10:48.<br/><br/>
 */
public class StickerSpriteFrame {

    private String mFileName;
    private Rect mFrame;//[x, y, width, height]
    private float mDuration = 0.1f;

    public StickerSpriteFrame() {
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public Rect getFrame() {
        return mFrame;
    }

    public void setFrame(Rect frame) {
        mFrame = frame;
    }

    public void setDuration(float duration) {
        if (duration > 0.0f) {
            mDuration = duration;
        }
    }

    public float getDuration() {
        return mDuration;
    }

    public int getFrameWidth() {
        if (mFrame != null) {
            return mFrame.right;
        }
        return 0;
    }

    public int getFrameHeight() {
        if (mFrame != null) {
            return mFrame.bottom;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "StickerSpriteFrame{" +
                "mFileName='" + mFileName + '\'' +
                ", mFrame=" + mFrame +
                ", mDuration=" + mDuration +
                '}';
    }
}
