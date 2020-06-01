package cn.poco.ad66;


import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.ad.abs.ADAbsAdapter;
import cn.poco.ad66.imp.IAD66Model;
import cn.poco.ad66.imp.IAD66UI;
import cn.poco.ad66.site.AD66PageSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.utils.Utils;

/**
 * 连接ui和数据的类
 */
public class AD66Presenter {

    private Context m_context;
    private AD66PageSite m_site;
    private IAD66UI m_ad66ui;
    private IAD66Model m_model;
    public AD66Presenter(Context context,AD66PageSite site,IAD66UI ui)
    {
        m_context = context;
        m_site = site;
        m_ad66ui = ui;
        m_model = new AD66Model(context);
        m_model.setThreadCallBack(m_threadCB);
    }


    private IAD66Model.ThreadCallBack m_threadCB = new IAD66Model.ThreadCallBack() {
        @Override
        public void finishFaceCheck() {
            m_ad66ui.finishFaceCheck();
        }

        @Override
        public void finishMakeEffect(Bitmap bmp) {
            m_ad66ui.updateBmp(bmp);
        }
    };

    //设置图片
    public void setImage(Object image)
    {
        m_model.setImage(image);
        m_ad66ui.setImageBmp(m_model.getOrgBmp());
    }


    //人脸识别
    public void facecheck()
    {
        m_ad66ui.ShowWaitUI();
        m_model.faceckeck();
    }

    //做滤镜效果
    public void sendAD66Effect_filter()
    {
        m_ad66ui.ShowWaitUI();
        m_model.makeAD66EffectFilter();
    }

     //做彩妆效果
     public void sendAD66Effect_makeup()
    {
        m_ad66ui.ShowWaitUI();
        m_model.makeAD66EffectMakeup();
    }


    //点击下一步按钮
    public void onOk()
    {
        if(m_ad66ui.getCurMode() == AD66Page.FILTER_MODE)
        {

        }
        else if(m_ad66ui.getCurMode() == AD66Page.COLOR_MODE)
        {
            HashMap<String,Object> params = new HashMap<>();
            String path = FileCacheMgr.GetLinePath();
            Bitmap bmp = m_model.getCurBmp();
            if (Utils.SaveTempImg(bmp, path))
            {
                params.put("img", path);
            }
            Utils.UrlTrigger(m_context,"http://cav.adnonstop.com/cav/fe0a01a3d9/0073002937/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
            m_site.onSave(m_context,params);
        }
    }


    //点击返回按钮
    public void onBack()
    {
        if(m_ad66ui.getCurMode() == AD66Page.FILTER_MODE)
        {
            m_site.onBack(m_context);
        }
        else if(m_ad66ui.getCurMode() == AD66Page.COLOR_MODE)
        {

        }
    }

    public void clearLipEffectBmp()
    {
        m_model.clearLipEffectBmp();
    }

    //获取当前的滤镜的透明度
    public int getProgressValue()
    {
        return m_model.getCurProgressValue();
    }


    //获取当前的彩妆的透明度
    public int getColorProgressValue()
    {
        return m_model.getCurColorProgressValue();
    }

    //获取滤镜列表的相关数据集合
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos1()
    {
        return m_model.getItemInfos1();
    }


     //获取彩妆唇彩列表的相关数据集合
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos2()
    {
        return m_model.getItemInfos2();
    }

      //获取滤镜的当前选项Index
    public int getCurSelectIndex1()
    {
        return m_model.getStyleIndex();
    }

    //获取彩妆唇彩的当前选项Index
    public int getCurSelectIndex2()
    {
        return m_model.getColorIndex();
    }

    //获取当前滤镜的图片
    public Bitmap getFilterBmp()
    {
        return m_model.getFilterBmp();
    }

    //改变滤镜选项
    public void changeStyleIndex(int index)
    {
        m_ad66ui.ShowWaitUI();
        m_model.changeStyleIndex(index);
    }

      //改变彩妆唇彩选项
    public void changeColorIndex(int index)
    {
        m_ad66ui.ShowWaitUI();
        m_model.changeColorIndex(index);
    }


    //重置透明度
    public void reSetAlphas()
    {
        m_model.resetAlphas();
    }

     //改变滤镜的透明度
    public void changeFilterAlpha(int alpha)
    {
        m_model.changeProgress_filter(alpha);
    }

    //改变当前彩妆选项的透明度
    public void changeMakeupAlpha(int alpha)
    {
        m_model.changeProgress_makeup(alpha);
    }

    //获取当前显示图片
    public Bitmap getCurBmp()
    {
        return m_model.getCurBmp();
    }

    //获取原图
    public Bitmap getOrgBmp()
    {
        return m_model.getOrgBmp();
    }

    public void ClearAll()
    {
        m_model.Clear();
    }
}
