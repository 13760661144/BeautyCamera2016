package cn.poco.dynamicSticker;

import java.util.ArrayList;

/**
 * @author lmx
 *         Created by lmx on 2017/7/14.
 */

public class StickerSoundRes
{
    public int mStickerId = -1;

    public ArrayList<StickerSound> mStickerSounds;

    public StickerSoundRes()
    {
        mStickerSounds = new ArrayList<>();
    }

    public void add(StickerSound stickerSound)
    {
        if (mStickerSounds != null)
        {
            mStickerSounds.add(stickerSound);
        }
    }


    public void clear()
    {
        if (mStickerSounds != null)
        {
            mStickerSounds.clear();
        }
        mStickerSounds = null;
    }
}
