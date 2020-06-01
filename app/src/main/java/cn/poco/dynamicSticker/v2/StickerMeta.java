package cn.poco.dynamicSticker.v2;

/**
 * Created by zwq on 2017/04/05 10:57.<br/><br/>
 */
public class StickerMeta {

    public String mImgName;//图片(类型)名称

    public Object mImage;
    public int mImgWidth;
    public int mImgHeight;

    public boolean mHasDecodeImg;//是否decode了img
    public int mStartIndex;
    public int mFrameCount;//帧数

    public StickerMeta() {
    }

    @Override
    public String toString() {
        return "StickerMeta{" +
                ", mImgName='" + mImgName + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mImgWidth=" + mImgWidth +
                ", mImgHeight=" + mImgHeight +
                '}';
    }
}
