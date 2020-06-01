package cn.poco.ad65;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.util.ArrayList;

import cn.poco.ad65.imp.IAD65Model;
import cn.poco.beautify.EffectType;
import cn.poco.beautify.ImageProcessor;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.resource.FilterRes;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ImageUtils;
import my.beautyCamera.R;

public class AD65Model implements IAD65Model{

    private ThreadCallBack m_cb;
    private Handler m_handler;
    private Bitmap m_orgBmp;
    private Bitmap m_curBmp;
    private ArrayList<AD66Frame> m_frameRess;
    private Context m_context;
    private int m_progress = 70;
    private FilterRes m_filterRes;
    private float m_bmpRatio;
    private boolean m_onceFaceCheck = false;
    public AD65Model(Context context)
    {
        m_context = context;
        init();
    }

    private void init()
    {
        m_handler = new Handler();
        m_frameRess = getFrameDatas();
//        m_filterRes = new FilterRes();
//        m_filterRes.m_isUpDateToCamera = false;
//        m_filterRes.m_filterAlpha = 60;
//        m_filterRes.m_isHasvignette = false;
//        m_filterRes.m_isSkipFace = false;
//        m_filterRes.m_datas = new FilterRes.FilterData[1];
//        m_filterRes.m_datas[0] = new FilterRes.FilterData();
//        m_filterRes.m_datas[0].m_res = R.drawable.ad65_filter_needbmp;
//        m_filterRes.m_datas[0].m_isSkipFace = false;
//        m_filterRes.m_datas[0].m_params = new int[2];
//        m_filterRes.m_datas[0].m_params[0] = 1;
//        m_filterRes.m_datas[0].m_params[1] = 100;
    }

    private ArrayList<AD66Frame> getFrameDatas()
    {
        ArrayList<AD66Frame> out = new ArrayList<>();
//        AD66Frame frame = new AD66Frame();
//        frame.m_ress.add(R.drawable.ad65_frame1_11);
//        frame.m_ress.add(R.drawable.ad65_frame1_34);
//        frame.m_ratios.add(1f/1f);
//        frame.m_ratios.add(3f/4f);
//        out.add(frame);
//
//        frame = new AD66Frame();
//        frame.m_ress.add(R.drawable.ad65_frame2_11);
//        frame.m_ress.add(R.drawable.ad65_frame2_34);
//        frame.m_ratios.add(1f/1f);
//        frame.m_ratios.add(3f/4f);
//        out.add(frame);
        return out;
    }

    @Override
    public void setImage(Object object) {
        RotationImg2[] info = null;
        if (object instanceof RotationImg2[]) {
            info = ((RotationImg2[]) object);
        } else if (object instanceof ImageFile2) {
            info = ((ImageFile2) object).SaveImg2(m_context);
        }
        m_orgBmp = cn.poco.imagecore.Utils.DecodeFinalImage(m_context, info[0].m_img, info[0].m_degree, -1, info[0].m_flip, SysConfig.GetPhotoSize(m_context), SysConfig.GetPhotoSize(m_context));
        if(m_orgBmp != null)
        {
            m_bmpRatio = m_orgBmp.getWidth()*1f/m_orgBmp.getHeight()*1f;
        }
    }

    @Override
    public void changeBmpEffect(int progress) {
        m_progress = progress;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(m_orgBmp == null)
                {
                    return;
                }
                float ratio = m_progress*1.0f/100.0f;
                Bitmap bmp = (m_orgBmp).copy(Bitmap.Config.ARGB_8888,true);
                Bitmap beautifybmp = ImageProcessor.ConversionImgColorNew(m_context, false, bmp, EffectType.EFFECT_DEFAULT);
                m_curBmp = ImageProcessor.DrawMask2((m_orgBmp).copy(Bitmap.Config.ARGB_8888,true), beautifybmp, (int) (70*ratio));
                if(bmp != null)
                {
                    bmp.recycle();
                    bmp = null;
                }
                int alpha = (int) (m_filterRes.m_filterAlpha*ratio);

                 if(alpha == 0)
                 {
                      m_curBmp = m_orgBmp.copy(Bitmap.Config.ARGB_8888,true);
                 }
                 else
                 {
                         Bitmap temp = FilterBeautifyProcessor.ProcessFilter(m_context,m_curBmp.copy(Bitmap.Config.ARGB_8888,true),m_filterRes,null);
                        if(alpha == 100)
                        {
                         m_curBmp = temp;
                        }
                        else
                        {
                            m_curBmp = FilterBeautifyProcessor.ProcessFilterAlpha(m_context,m_curBmp, temp, alpha);
                        if(temp != null)
                        {
                            temp.recycle();
                            temp = null;
                        }
                        }
                 }

                if(!m_onceFaceCheck && m_orgBmp != null)
                {
                    FaceDataV2.CheckFace(m_context,m_orgBmp);
                }
                PocoBeautyFilter.AutoShrinkFace(m_curBmp,FaceDataV2.RAW_POS_MULTI);

                if(m_cb != null && m_curBmp != null)
                {
                    m_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            m_cb.MakeBmpEffectFinish(m_curBmp);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public Bitmap getFrameRes(int index) {
        Bitmap out = null;
        if(index < m_frameRess.size())
        {
            out = BitmapFactory.decodeResource(m_context.getResources(),m_frameRess.get(index).getFrameResByRatio(m_bmpRatio));
        }
        return out;
    }

    @Override
    public Bitmap getCurBmp() {
        return m_curBmp;
    }

    @Override
    public Bitmap getOrgBmp() {
        return m_orgBmp;
    }

    @Override
    public int getProgress() {
        return m_progress;
    }

    @Override
    public void setThreadCallBack(ThreadCallBack callBack) {
        m_cb = callBack;
    }

    @Override
    public void clear() {
        m_orgBmp = null;
        m_curBmp = null;
        FaceDataV2.ResetData();
    }

    private class AD66Frame
    {
        public ArrayList<Integer> m_ress = new ArrayList<>();
        public ArrayList<Float> m_ratios = new ArrayList<>();

        public int getFrameResByRatio(float ratio)
        {
            int out = -1;
            int index = ImageUtils.GetScale(ratio,m_ratios);
            if(index > -1 && index < m_ress.size())
            {
                out = m_ress.get(index);
            }
            return out;
        }
    }
}
