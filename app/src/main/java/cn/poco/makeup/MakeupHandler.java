package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import cn.poco.beautify.ImageProcessor;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.face.MakeupAlpha;
import cn.poco.resource.MakeupRes;


/**
 * 彩妆处理耗时操作的Handler
 */
public class MakeupHandler extends Handler {

    public static final int CHECK_FACE = 1;
    public static final int MAKEUP = 2;
    public static final int ALL = 3;
    public Handler m_uiHander;
    private Context m_context;
    public MakeupHandler(Handler uiHandler, Looper looper,Context context)
    {
        super(looper);
        if(uiHandler !=  null)
        {
            m_uiHander = uiHandler;
        }
        m_context = context;
    }
    @Override
    public void handleMessage(Message msg) {
        Bitmap tempBmp = null;
        if(msg != null)
        {
            switch (msg.what)
            {
                //人脸检测
                case CHECK_FACE:
                {
                    Bitmap tmp = null;
                    if(msg.obj != null)
                    {
                        tmp = (Bitmap) msg.obj;
                    }
                    if(tmp != null)
                    {
                        FaceDataV2.CheckFace(m_context, tmp);
                    }
                    Message message = Message.obtain();
                    message.what = CHECK_FACE;
                    m_uiHander.sendMessage(message);
                    break;
                }
                //做彩妆效果
                case MAKEUP :
                {
                    Bitmap out = null;
                    if(msg.obj != null)
                    {
                        MakeupMsg item = (MakeupMsg) msg.obj;
                        if(item.m_bmp != null && !item.m_bmp.isRecycled())
                        {
                            tempBmp = item.m_bmp.copy(Bitmap.Config.ARGB_8888,true);
                        }
                        //彩妆
                            if(FaceDataV2.RAW_POS_MULTI != null && item.m_faceLocalData != null && item.m_faceLocalData.m_makeupDatas_multi != null && item.m_faceLocalData.m_makeupAlphas_multi != null)
                            {
                                for(int i = 0; i < item.m_faceLocalData.m_faceNum; i++)
                                {
                                    MakeupAlpha ma = item.m_faceLocalData.m_makeupAlphas_multi[i];
                                    if (ma == null) {
                                        return;
                                    }
                                    float alphaScale = item.m_faceLocalData.m_asetAlpha_multi[i] / 100f;
                                    ArrayList<MakeupRes.MakeupData> datas = new ArrayList<>(item.m_faceLocalData.m_makeupDatas_multi[i]);
                                    out = ImageProcessor.DoMakeup(m_context, i, tempBmp, datas, new int[]{(int)(ma.m_eyebrowAlpha * alphaScale + 0.5f),
                                            (int)(ma.m_eyeAlpha * alphaScale + 0.5f), (int)(ma.m_kohlAlpha * alphaScale + 0.5f),
                                            (int)(ma.m_eyelashUpAlpha * alphaScale + 0.5f), (int)(ma.m_eyelashDownAlpha * alphaScale + 0.5f),
                                            (int)(ma.m_eyelineUpAlpha * alphaScale + 0.5f), (int)(ma.m_eyelineDownAlpha * alphaScale + 0.5f),
                                            (int)(ma.m_cheekAlpha * alphaScale + 0.5f), (int)(ma.m_lipAlpha * alphaScale + 0.5f),
                                            (int)(ma.m_foundationAlpha * alphaScale + 0.5f)});
                                }
                            }
                    }
                    if(m_uiHander != null)
                    {
                        Message uiMsg = Message.obtain();
                        uiMsg.what = MAKEUP;
                        uiMsg.obj = out;
                        m_uiHander.sendMessage(uiMsg);
                    }
                    break;
                }
            }
        }
    }

    public static class MakeupMsg
    {
        public Bitmap m_bmp;
        public FaceLocalData m_faceLocalData;
    }
}
