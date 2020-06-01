package cn.poco.camera3.info;

import cn.poco.resource.IDownload;

public class PreviewBgmInfo
{
    private int mId;
    private String mName;
    private int mTjid;
    private int mProducId; // 作品id
    private Object mThumb; // 缩略图路径
    private Object mRes; // 资源路径
    private String mResName;
    private int mProgress; //下载进度
    private int mState; // 下载状态
    private boolean mIsSel = false;
    private float mThumbDegree = 0; // 旋转角度
    private IDownload mEx; // res 对象 --> PreviewBgmRes --> IDownload
    private int mDownloadID = -1;
    private boolean mIsClip = false;//是否编辑过改音乐
    private long mDuration;

    public int getId()
    {
        return mId;
    }

    public void setId(int mId)
    {
        this.mId = mId;
    }

    public boolean isClip()
    {
        return mIsClip;
    }

    public void setIsClip(boolean mIsClip)
    {
        this.mIsClip = mIsClip;
    }

    public int getProducId()
    {
        return mProducId;
    }

    public void setProducId(int producId)
    {
        this.mProducId = producId;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public int getTjid()
    {
        return mTjid;
    }

    public void setTjid(int tjid)
    {
        this.mTjid = tjid;
    }

    public Object getThumb()
    {
        return mThumb;
    }

    public void setThumb(Object thumb)
    {
        this.mThumb = thumb;
    }

    public Object getRes()
    {
        return mRes;
    }

    public String getResName()
    {
        return mResName;
    }

    public void setResName(String mResName)
    {
        this.mResName = mResName;
    }

    public void setRes(Object res)
    {
        this.mRes = res;
    }

    public int getProgress()
    {
        return mProgress;
    }

    public void setProgress(int progress)
    {
        this.mProgress = progress;
    }

    public int getState()
    {
        return mState;
    }

    public void setState(int state)
    {
        this.mState = state;
    }

    public boolean isIsSel()
    {
        return mIsSel;
    }

    public void setIsSel(boolean isSel)
    {
        this.mIsSel = isSel;
    }

    public float getThumbDegree()
    {
        return mThumbDegree;
    }

    public void setThumbDegree(float degree)
    {
        this.mThumbDegree = degree;
    }

    public IDownload getEx()
    {
        return mEx;
    }

    public void setEx(IDownload ex)
    {
        this.mEx = ex;
    }

    public int getDownloadID()
    {
        return mDownloadID;
    }

    public void setDownloadID(int downloadID)
    {
        this.mDownloadID = downloadID;
    }

    public long getDuration()
    {
        return mDuration;
    }

    public void setDuration(long duration)
    {
        this.mDuration = duration;
    }
}
