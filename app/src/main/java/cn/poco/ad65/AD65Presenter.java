package cn.poco.ad65;


import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.ad65.imp.IAD65Model;
import cn.poco.ad65.imp.IAD65UI;
import cn.poco.ad65.site.AD65PageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.utils.Utils;

public class AD65Presenter {


    private AD65PageSite m_site;
    private Context m_context;
    private IAD65UI m_ui;
    private IAD65Model m_model;
    public AD65Presenter(BaseSite site, Context context,IAD65UI ui)
    {
        m_site = (AD65PageSite) site;
        m_context = context;
        m_ui = ui;
        m_model = new AD65Model(m_context);
        m_model.setThreadCallBack(m_threadCallBack);
    }

    private IAD65Model.ThreadCallBack m_threadCallBack = new IAD65Model.ThreadCallBack() {
        @Override
        public void MakeBmpEffectFinish(Bitmap bmp) {
            m_ui.updateBmp(bmp);
            m_ui.dismissWaitDlg();
        }
    };


    public void setImage(Object img)
    {
        m_model.setImage(img);
        m_ui.updateBmp(m_model.getOrgBmp());
    }


    public void onOk()
    {
        if(m_ui.getCurPage() == AD65Page.BEAUTY_PAGE)
        {

        }
        else if(m_ui.getCurPage() == AD65Page.FRAME_PAGE)
        {
            HashMap<String ,Object> params = new HashMap<>();
            String path = FileCacheMgr.GetLinePath();
            Bitmap bmp = m_ui.getOutputBmp();
            if (Utils.SaveTempImg(bmp, path))
            {
                params.put("img", path);
            }
            params.put(AD65Page.KEYVALUE_PROGRESS,m_model.getProgress());
            params.put(AD65Page.KEYVALUE_BACK,true);
            params.put(AD65Page.KEYVALUE_FRAMEINDEX,m_ui.getCurFrameIndex());
            m_site.onSave(m_context,params);
        }
    }

    public void onBack()
    {
        if(m_ui.getCurPage() == AD65Page.BEAUTY_PAGE)
        {
            m_site.onback(m_context);
        }
        else if(m_ui.getCurPage() == AD65Page.FRAME_PAGE)
        {

        }
    }

    public Bitmap getFrameRes(int index)
    {
        return m_model.getFrameRes(index);
    }

    public void changeEffectByProgress(int progress)
    {
        m_ui.showWaitDlg();
        m_model.changeBmpEffect(progress);
    }

    public Bitmap getOrgBmp()
    {
        return m_model.getOrgBmp();
    }

    public void clear()
    {

    }

    public int getProgress()
    {
        return m_model.getProgress();
    }

    public Bitmap getCurBmp()
    {
        return m_model.getCurBmp();
    }
}
