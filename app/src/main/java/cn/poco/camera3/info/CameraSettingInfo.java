package cn.poco.camera3.info;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/21.
 */

public class CameraSettingInfo
{
    private int mLogo;
    private Object mTag;
    private String mText;
    private boolean mIsSelected;

    public Object getTag()
    {
        return mTag;
    }

    /**
     * 记录 info 额外信息
     * @param tag {@link CameraSettingInfo.FlashIndex#ON} or
     *             {@link CameraSettingInfo.TimingIndex#ONE_SEC}
     */
    public void setTag(Object tag)
    {
        this.mTag = tag;
    }

    public int getLogo()
    {
        return mLogo;
    }

    public void setLogo(int logo)
    {
        this.mLogo = logo;
    }

    public String getText()
    {
        return mText;
    }

    public void setText(String text)
    {
        this.mText = text;
    }

    public boolean isSelected()
    {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected)
    {
        this.mIsSelected = isSelected;
    }

    // index
    public interface FlashIndex
    {
        int ON = 0;
        int AUTO = 1;
        int OFF = 2;
        int TORCH = 3; // 手电筒
    }

    // index
    public interface TimingIndex
    {
        int OFF = 0;
        int ONE_SEC = 1;
        int TWO_SEC = 2;
        int TEN_SEC = 3;
    }
}
