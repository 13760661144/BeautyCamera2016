package cn.poco.noseAndtooth.abs;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.noseAndtooth.site.AbsNoseAndToothPageSite;


public abstract class AbsNATPresenter implements INATPresenter {

    private Context m_context;
    private INATModel m_model;
    private INATPage m_natImp;
    private AbsNoseAndToothPageSite m_site;
    public AbsNATPresenter(Context context, INATPage inatPage, BaseSite site)
    {
        m_site = (AbsNoseAndToothPageSite) site;
        m_context = context;
        m_model = getModel();
        m_natImp = inatPage;
        init();
    }

    private void init()
    {
        INATModel.ThreadCallBack callBack = new INATModel.ThreadCallBack() {
            @Override
            public void updateBmp(Bitmap bmp) {
                m_natImp.updateBmp(bmp);
            }

            @Override
            public void finishFaceCheck() {
                m_natImp.finishFaceCheck();
            }
        };
        m_model.setUpdateBmpCB(callBack);
    }

    @Override
    public int getInitProgress() {
        return m_model.initProgressValue();
    }

    @Override
    public void Clear() {
        m_model.Clear();
    }

    public abstract AbsNATModel getModel();


    @Override
    public String getTitle() {
        return m_model.getTitle();
    }

    @Override
    public int getIconRes() {
        return m_model.getIconRes();
    }

    @Override
    public Bitmap getCurBmp() {
        return m_model.getCurBmp();
    }

    @Override
    public Bitmap getOrgBmp() {
        return m_model.getOrgBmp();
    }


    @Override
    public void setImage(Object object)
    {
        m_model.setOriInfo(object);
        m_natImp.setViewImage(m_model.getOrgBmp());
    }

    @Override
    public void faceCheck()
    {
        m_natImp.showWaitUI();
        m_model.facecheck();
    }

    @Override
    public void setProgress(int progress)
    {
        m_model.changeProgress(progress);
    }

    @Override
    public int getCurProgress()
    {
        return m_model.getProgress();
    }

    @Override
    public void sendEffectMsg()
    {
        m_natImp.showWaitUI();
        m_model.makeEffectBmp();
    }

    @Override
    public void back() {
        if (m_natImp.isChanged()) {
            m_natImp.showExitDialog();
        } else {
            onExit();
        }
    }

    @Override
    public void ok() {
        HashMap<String,Object> params = new HashMap<>();
        if(m_model.getCurBmp() != null)
        {
            params.put("img",m_model.getCurBmp());
        }
        else
        {
            params.put("img",m_model.getOrgBmp());
        }
        params.putAll(m_natImp.getBackAnimParams());
        m_site.onSave(m_context, params);
    }

    @Override
    public void onExit()
    {
        HashMap<String,Object> params = new HashMap<>();
        params.put("img",m_model.getOrgBmp());
        params.putAll(m_natImp.getBackAnimParams());
        m_site.onBack(m_context, params);
    }
}
