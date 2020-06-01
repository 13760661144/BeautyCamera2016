package cn.poco.dynamicSticker;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * @author lmx
 *         Created by lmx on 2017/7/14.
 */

public class StickerSound
{
    /**
     * 音效类型<br/>
     * bgm - 背景音乐,循环播放<br/>
     * se1 - sound effect 1 动画播放到某时刻才播放<br/>
     * se2 - sound effect 2 根据动作播放<br/>
     */
    public TypeValue.SoundType mSoundType = TypeValue.SoundType.NONE;
    public String mType;

    public String mResourceName;//音频名
    public String mResourcePath;//音频路径
    public double mDelayDuration;//延时到单位毫秒
    public boolean isSolo;//是否独占
    public boolean isBgmContinue; //当solo为true时此参数才有效,播放完声音是否恢复背景音乐

    public TypeValue.TriggerType mActionTriggerType;
    public boolean isActionTrigger = false;//是否动作触发音效
    public String mAction;//脸部动作触发类型

    public boolean isLooping;//是否循环，当音效为bgm音效此参数才有效

    public double mFrameDuration;//动画时长单位毫秒

    public void setSoundType(TypeValue.SoundType mSoundType)
    {
        this.mSoundType = mSoundType;
        if (isBGMSound())
        {
            isLooping = true;
        }
        else
        {
            isLooping = false;
        }
    }

    public void setType(String mType)
    {
        this.mType = mType;
    }

    public void setResourceName(String mResourceName)
    {
        this.mResourceName = mResourceName;
    }

    public void setResourcePath(String mResourcePath)
    {
        this.mResourcePath = mResourcePath;
    }

    public void setDelayDuration(double mDelayDuration)
    {
        this.mDelayDuration = mDelayDuration;
    }

    public void setFrameDuration(double mFrameDuration)
    {
        this.mFrameDuration = mFrameDuration;
    }

    public void setSolo(boolean isSolo)
    {
        this.isSolo = isSolo;
    }

    public void setBgmContinue(boolean isBgmContinue)
    {
        this.isBgmContinue = isBgmContinue;
    }

    public void setActionTrigger(boolean actionTrigger)
    {
        isActionTrigger = actionTrigger;
    }

    public void setActionTriggerType(TypeValue.TriggerType mActionTriggerType)
    {
        this.mActionTriggerType = mActionTriggerType;
    }

    public void setAction(String mAction)
    {
        this.mAction = mAction;
    }

    public boolean isBGMSound()
    {
        return mSoundType == TypeValue.SoundType.BGM;
    }

    public TypeValue.SoundType getSoundType()
    {
        return mSoundType;
    }

    public Uri getResourceUri()
    {
        if (!TextUtils.isEmpty(mResourcePath))
        {
            try
            {
                return Uri.parse(mResourcePath);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }

    public File getResourceFile()
    {
        if (!TextUtils.isEmpty(mResourcePath))
        {
            try
            {
                return new File(mResourcePath);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }
}
