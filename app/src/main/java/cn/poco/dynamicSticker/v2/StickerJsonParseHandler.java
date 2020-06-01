package cn.poco.dynamicSticker.v2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.dynamicSticker.StickerHelper;
import cn.poco.utils.FileUtil;

/**
 * @author lmx
 *         Created by lmx on 2017/5/19.
 */

public class StickerJsonParseHandler extends Handler
{
    public static final int MSG_PARSE = 1;
    public static final int MSG_CANCEL = 1 << 1;

    private Context mContext;
    private Handler mUihandler;

    private volatile boolean isNewParse = false;
    private volatile boolean isParse = false;

    public StickerJsonParseHandler(Looper looper, Context context, Handler uiHandler)
    {
        super(looper);
        this.mContext = context;
        this.mUihandler = uiHandler;
    }

    public void setNewParse(boolean newParse)
    {
        this.isNewParse = newParse;
    }

    public boolean isParse()
    {
        return isParse;
    }

    @Override
    public void handleMessage(Message msg)
    {
        if (msg != null)
        {
            switch (msg.what)
            {
                case MSG_PARSE:
                {
                    ParseMsg param = (ParseMsg) msg.obj;
                    msg.obj = null;
                    if (param != null && param.m_res != null)
                    {
                        isParse = true;
                        cn.poco.resource.VideoStickerRes res = param.m_res;

                        //根据id构建目标路径
                        String foldPath = StickerHelper.GetStickerIdFolderPath(res);

                        //1、assets目录的zip
                        //2、已下载目录的zip

                        if (StickerHelper.isAssetFile(mContext, res.m_res_path))
                        {
                            //先删除旧包
                            //asset拷贝到SD卡 file:///android_asset/sticker/xxx.zip
                            FileUtil.assets2SD(mContext, StickerHelper.GetAssetStickerZipPath(res),
                                    StickerHelper.GetStickerZipPath(res), true);
                        }

                        param.m_out = StickerJsonParse.parseZipRes(foldPath, res.m_res_name, param.m_deleteZip);
                        if (param.m_out != null && param.m_out.mStickerSoundRes != null)
                        {
                            param.m_out.mStickerSoundRes.mStickerId = res.m_id;
                        }
                        isParse = false;

                        if (!isNewParse)
                        {
                            sendUIMessage(MSG_PARSE, param);
                        }
                        isNewParse = false;
                    }
                    break;
                }

                case MSG_CANCEL:
                {
                    //TODO
                    break;
                }

                default:
                {
                    //TODO
                    break;
                }
            }
        }
    }

    private void sendUIMessage(int what, Object obj)
    {
        if (mUihandler != null)
        {
            Message msg = mUihandler.obtainMessage();
            msg.what = what;
            msg.obj = obj;
            mUihandler.sendMessage(msg);
        }
    }

    public static class ParseMsg
    {
        public int m_sticker_id = -1;
        public int m_position;
        public int m_groupPosition;

        public cn.poco.resource.VideoStickerRes m_res;

        public boolean m_deleteZip = false;

        public StickerRes m_out; //stickerres
    }
}
