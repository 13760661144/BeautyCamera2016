package cn.poco.camera3.info;

/**
 * 比例信息
 * Created by Gxx on 2017/12/6.
 */

public class CameraRatioInfo
{
    private int mThumb;
    private int mIndex;
    private float mValue;
    private String mText;
    private boolean isSelected;

    public int getThumb()
    {
        return mThumb;
    }

    public void setThumb(int thumb)
    {
        this.mThumb = thumb;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void setIndex(int index)
    {
        this.mIndex = index;
    }

    public float getValue()
    {
        return mValue;
    }

    public void setValue(float value)
    {
        this.mValue = value;
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
        return isSelected;
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }
}
