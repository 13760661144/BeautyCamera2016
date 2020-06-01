package cn.poco.dynamicSticker;

import android.text.TextUtils;

import static cn.poco.dynamicSticker.FaceAction.Blink;
import static cn.poco.dynamicSticker.FaceAction.EyeBrow;
import static cn.poco.dynamicSticker.FaceAction.NodHead;
import static cn.poco.dynamicSticker.FaceAction.OpenMouth;

/**
 * @author lmx
 *         Created by lmx on 2017/7/13.
 */
public class TypeValue
{
    /**
     * 触发模式
     */
    public enum TriggerType
    {
        ALL("all"),                  //任意
        ANIM("anim"),                //动画
        OPEN_MOUTH(OpenMouth),       //张嘴
        BLINK_EYE(Blink),            //眨眼
        EYEBROW(EyeBrow),            //挑眉
        NODHEAD(NodHead);            //点头

        public boolean isOepnMouth()
        {
            return this == OPEN_MOUTH;
        }

        public boolean isBlinkEye()
        {
            return this == BLINK_EYE;
        }

        public boolean isEyeBrow()
        {
            return this == EYEBROW;
        }

        public boolean isNodHead()
        {
            return this == NODHEAD;
        }

        public boolean isAll()
        {
            return this == ALL;
        }

        public boolean isAnim()
        {
            return this == ANIM;
        }

        public boolean isFaceAction()
        {
            return this == OPEN_MOUTH || this == BLINK_EYE || this == EYEBROW || this == NODHEAD;
        }

        public String type;

        TriggerType(String type)
        {
            this.type = type;
        }

        public String getType()
        {
            return type;
        }

        public static TriggerType HasType(String type)
        {
            if (!TextUtils.isEmpty(type))
            {
                if (OPEN_MOUTH.getType().equals(type))
                {
                    return OPEN_MOUTH;
                }
                else if (BLINK_EYE.getType().equals(type))
                {
                    return BLINK_EYE;
                }
                else if (EYEBROW.getType().equals(type))
                {
                    return EYEBROW;
                }
                else if (NODHEAD.getType().equals(type))
                {
                    return NODHEAD;
                }
                else if (ALL.getType().equals(type))
                {
                    return ALL;
                }
                else if (ANIM.getType().equals(type))
                {
                    return ANIM;
                }
            }
            return null;
        }

        public static boolean IsExistAction(String action)
        {
            return FaceAction.isExistAction(action);
        }
    }

    /**
     * 音效类型
     */
    public enum SoundType
    {
        NONE(0, "none"),
        BGM(1, "bgm"),              //背景音乐,循环播放
        EFFECT_DELAY(2, "se1"),     //动画播放到某时刻才播放
        EFFECT_ACTION(3, "se2");    //根据动作播放

        private int value;
        private String type;

        SoundType(int value, String type)
        {
            this.value = value;
            this.type = type;
        }

        public boolean isEffectSound()
        {
            return this == EFFECT_DELAY || this == EFFECT_ACTION;
        }

        public boolean isEffectAction()
        {
            return this == EFFECT_ACTION;
        }

        public int getValue()
        {
            return this.value;
        }

        public String getType()
        {
            return type;
        }

        public static SoundType HasType(String type)
        {
            if (!TextUtils.isEmpty(type))
            {
                if (NONE.getType().equals(type))
                {
                    return NONE;
                }
                else if (BGM.getType().equals(type))
                {
                    return BGM;
                }
                else if (EFFECT_DELAY.getType().equals(type))
                {
                    return EFFECT_DELAY;
                }
                else if (EFFECT_ACTION.getType().equals(type))
                {
                    return EFFECT_ACTION;
                }
            }
            return NONE;
        }
    }

    /**
     * 音效状态
     */
    public enum SoundStatus
    {
        IDLE,
        PLAYING,
        STOP;

        public final boolean isPlaying()
        {
            return this == PLAYING;
        }

        public final boolean isStop()
        {
            return this == STOP;
        }
    }
}
