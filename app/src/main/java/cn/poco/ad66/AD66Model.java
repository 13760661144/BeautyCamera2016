package cn.poco.ad66;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.ArrayList;

import cn.poco.ad.abs.ADAbsAdapter;
import cn.poco.ad66.imp.IAD66Model;
import cn.poco.beautify.ImageProcessor;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.resource.FilterRes;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupType;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;


/**
 * 兰蔻商业用到的数据
 */
public class AD66Model implements IAD66Model {
    private final int FACECHECK = 1;
    private final int AD66EFFECT_FILTER = 2;
    private final int AD66EFFECT_MAKEUP = 3;
    private Bitmap m_orgBmp;
    private Bitmap m_curBmp;
    private Bitmap m_filterBmp;
    private HandlerThread m_thread;
    private Handler m_threadHandler;
    private Handler m_uiHandler;
    private Context m_context;
    private AD66Model.ThreadCallBack m_threadCB;
    private int m_curStyleIndex = -1;
    private int m_curColorIndex = -1;
    private int m_filterAlpha = 70;
    private int[] m_lipEffectAlpha = {80,100,100,100,100,80,80,80,80,80};
    private ArrayList<ADAbsAdapter.ADItemInfo> m_itemInfos1;
    private ArrayList<ADAbsAdapter.ADItemInfo> m_itemInfos2;

    private ArrayList<FilterRes> m_filterRess;
    private MakeupRes m_makeupRes;
    private ArrayList<MakeupRes.MakeupData> m_lipMakeupDatas;
    private ArrayList<Integer> m_makeupIds = new ArrayList<>();
    private ArrayList<Integer> m_makeupLipAlphas = new ArrayList<>();
    public AD66Model(Context context)
    {
        m_context = context;
        init();
    }

    private void init()
    {
        //189 57640
        //132 57664
        m_filterRess = getAllFrameRess();
        m_itemInfos1 = getItemInfos1Real();
        m_itemInfos2 = getItemInfos2Real();
        //兰蔻唇彩的id，domakeup接口那里会根据是否是兰蔻的唇彩id调用另外的接口来做效果，所以这里要记录兰蔻的id，再根据不同选项Index传不同的id到domakeup的接口
        m_makeupIds.add(57664);
        m_makeupIds.add(57640);
        m_makeupIds.add(57632);
        m_makeupIds.add(57648);
        m_makeupIds.add(57656);
        m_makeupIds.add(57672);
        m_makeupIds.add(57680);
        m_makeupIds.add(57688);
        m_makeupIds.add(57696);
        m_makeupIds.add(57704);
        //兰蔻不同唇彩的透明度
        m_makeupLipAlphas.add(85);
        m_makeupLipAlphas.add(60);
        m_makeupLipAlphas.add(60);
        m_makeupLipAlphas.add(60);
        m_makeupLipAlphas.add(60);
        m_makeupLipAlphas.add(85);
        m_makeupLipAlphas.add(85);
        m_makeupLipAlphas.add(85);
        m_makeupLipAlphas.add(85);
        m_makeupLipAlphas.add(85);
        //构造做彩妆用到的数据
        m_makeupRes = new MakeupRes();
        m_makeupRes.m_groupRes = new MakeupRes.MakeupData[5];
//		MakeupRes.MakeupData data = new MakeupRes.MakeupData();
//				data.m_defAlpha = 50;
//				data.m_ex = 1;
//				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
//				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
//				data.m_res = new Object[]{R.drawable.ad66_shangyanxian};
//				m_makeupRes.m_groupRes[0] = data;
//
//				data = new MakeupRes.MakeupData();
//				data.m_defAlpha = 50;
//				data.m_ex = 1;
//				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
//				data.m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
//				data.m_res = new Object[]{R.drawable.ad66_xiayanxian};
//				m_makeupRes.m_groupRes[1] = data;
//
//                data = new MakeupRes.MakeupData();
//				data.m_defAlpha = 40;
//				data.m_ex = 38;
//				data.m_makeupType = MakeupType.KOHL_L.GetValue();
//				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
//				data.m_res = new Object[]{R.drawable.ad66_yanying};
//				m_makeupRes.m_groupRes[2] = data;
//
//                data = new MakeupRes.MakeupData();
//				data.m_defAlpha = 50;
//				data.m_ex = 20;
//				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
//				data.m_params = new float[]{0.5f,10.0f,1.0f,0.0f};
//				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
//				data.m_res = new Object[]{R.drawable.ad66_shangjiemao1,R.drawable.ad66_shangjiemao2};
//				m_makeupRes.m_groupRes[3] = data;
//
//                data = new MakeupRes.MakeupData();
//				data.m_defAlpha = 85;
//				data.m_id = m_makeupIds.get(0);
//				data.m_makeupType = MakeupType.LIP.GetValue();
//                data.m_res = new Object[]{R.drawable.__mak__15154344820171012151929_7185_2909937169};
//				m_makeupRes.m_groupRes[4] = data;

        m_uiHandler = new Handler();
        m_thread = new HandlerThread("ad66");
        m_thread.start();
        m_threadHandler = new Handler(m_thread.getLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case FACECHECK:
                    {
                            if(msg.obj != null)
                            {
                                //根据底层做效果的需求，调用face++的人脸检测
                                FaceDataV2.CheckFaceAD2(m_context, (Bitmap) msg.obj);
//                                FaceDetector.CheckFace(getContext(),m_org);
                                m_uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(m_threadCB != null)
                                        {
                                            m_threadCB.finishFaceCheck();
                                        }
                                    }
                                });
                            }
                            break;
                    }
                    case AD66EFFECT_FILTER:
                    {
                            {
                                //做磨皮效果
                                Bitmap temp = FilterBeautifyProcessor.ProcessBeautyAdjust(m_context,FaceDataV2.RAW_POS_MULTI,m_orgBmp.copy(Bitmap.Config.ARGB_8888,true),40);
                               float ratio = m_filterAlpha*1.0f/100.0f;
                                FilterRes filterRes = m_filterRess.get(m_curStyleIndex);
                                int alpha = (int) (filterRes.m_filterAlpha*ratio);
                                //透明度等于0的时候不用做滤镜，直接返回当前的图片
                                if(alpha == 0)
                                {
                                    m_filterBmp = temp;
                                }
                                else
                                {
                                    //做滤镜
                                     temp = FilterBeautifyProcessor.ProcessFilter(m_context,temp,filterRes,null);
                                    if(alpha == 100)
                                    {
                                       m_filterBmp = temp;
                                    }
                                     else
                                    {
                                        //合成相应透明度的滤镜图片
                                          m_filterBmp = FilterBeautifyProcessor.ProcessFilterAlpha(m_context,m_orgBmp.copy(Bitmap.Config.ARGB_8888,true), temp, alpha);
                                       if(temp != null)
                                       {
                                            temp.recycle();
                                            temp = null;
                                       }
                                    }
                                }
                            }
                            m_uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(m_threadCB != null)
                                    {
                                        m_threadCB.finishMakeEffect(m_filterBmp);
                                    }
                                }
                            });
                        break;
                    }
                    case AD66EFFECT_MAKEUP:
                    {
                        if(m_curColorIndex == 0)
                        {
                            m_curBmp = m_filterBmp;
                        }
                        else
                        {
                            float ratio = m_lipEffectAlpha[m_curColorIndex - 1]*1.0f/100f;
                            //复制一张图片做效果
                            Bitmap tempBmp = m_filterBmp.copy(Bitmap.Config.ARGB_8888,true);
                            int index = m_curColorIndex - 1;
                            if(index > -1)
                            {
                               m_makeupRes.m_groupRes[4].m_id = m_makeupIds.get(index);
                                m_makeupRes.m_groupRes[4].m_defAlpha = m_makeupLipAlphas.get(index);
                            }
                            MakeupRes.MakeupData[] tempdatas = m_makeupRes.m_groupRes;
                            changeArray(tempdatas);

                            //做彩妆
                         m_curBmp = ImageProcessor.DoMakeup(m_context, FaceDataV2.sFaceIndex, tempBmp, m_lipMakeupDatas, new int[]{(int) (100*ratio), (int) (100*ratio),
                                 (int) (m_makeupRes.m_groupRes[2].m_defAlpha*ratio),
                                 (int) (m_makeupRes.m_groupRes[3].m_defAlpha*ratio),
                                 (int) (m_makeupRes.m_groupRes[3].m_defAlpha*ratio),
                                 (int) (m_makeupRes.m_groupRes[0].m_defAlpha*ratio),
                                 (int) (m_makeupRes.m_groupRes[1].m_defAlpha*ratio),
                                 (int) (100*ratio),
                                 (int) (m_makeupRes.m_groupRes[4].m_defAlpha*ratio),
                                 (int) (100*ratio)});
                        }
                          m_uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(m_threadCB != null)
                                    {
                                        m_threadCB.finishMakeEffect(m_curBmp);
                                    }
                                }
                            });
                        break;
                    }
                }
            }
        };
    }

    private void changeArray(MakeupRes.MakeupData[] datas)
    {
        if(m_lipMakeupDatas == null)
        {
            m_lipMakeupDatas = new ArrayList<>();
            for(int i = 0; i < datas.length; i++)
            {
                m_lipMakeupDatas.add(datas[i]);
            }
        }
    }

    //做滤镜要用到的数据
    private ArrayList<FilterRes> getAllFrameRess()
    {
        ArrayList<FilterRes> out = new ArrayList<>();
        //1
//        FilterRes filterRes = new FilterRes();
//        filterRes.m_isUpDateToCamera = false;
//        filterRes.m_filterAlpha = 100;
//        filterRes.m_isHasvignette = false;
//        filterRes.m_isSkipFace = false;
//        filterRes.m_datas = new FilterRes.FilterData[4];
//        filterRes.m_datas[0] = new FilterRes.FilterData();
//        filterRes.m_datas[0].m_res = R.drawable.ad66_filter1_needbmp1;
//        filterRes.m_datas[0].m_isSkipFace = false;
//        filterRes.m_datas[0].m_params = new int[2];
//        filterRes.m_datas[0].m_params[0] = 1;
//        filterRes.m_datas[0].m_params[1] = 100;
//
//        filterRes.m_datas[1] = new FilterRes.FilterData();
//        filterRes.m_datas[1].m_res = R.drawable.ad66_filter1_needbmp2;
//        filterRes.m_datas[1].m_isSkipFace = false;
//        filterRes.m_datas[1].m_params = new int[2];
//        filterRes.m_datas[1].m_params[0] = 61;
//        filterRes.m_datas[1].m_params[1] = 100;
//
//        filterRes.m_datas[2] = new FilterRes.FilterData();
//        filterRes.m_datas[2].m_res = R.drawable.ad66_filter1_needbmp3;
//        filterRes.m_datas[2].m_isSkipFace = false;
//        filterRes.m_datas[2].m_params = new int[2];
//        filterRes.m_datas[2].m_params[0] = 38;
//        filterRes.m_datas[2].m_params[1] = 26;
//
//        filterRes.m_datas[3] = new FilterRes.FilterData();
//        filterRes.m_datas[3].m_res = R.drawable.ad66_filter1_needbmp4;
//        filterRes.m_datas[3].m_isSkipFace = false;
//        filterRes.m_datas[3].m_params = new int[2];
//        filterRes.m_datas[3].m_params[0] = 45;
//        filterRes.m_datas[3].m_params[1] = 100;
//        out.add(filterRes);
//
//        //2
//        filterRes = new FilterRes();
//        filterRes.m_isUpDateToCamera = false;
//        filterRes.m_filterAlpha = 100;
//        filterRes.m_isHasvignette = false;
//        filterRes.m_isSkipFace = false;
//        filterRes.m_datas = new FilterRes.FilterData[2];
//        filterRes.m_datas[0] = new FilterRes.FilterData();
//        filterRes.m_datas[0].m_res = R.drawable.ad66_filter2_needbmp1;
//        filterRes.m_datas[0].m_isSkipFace = false;
//        filterRes.m_datas[0].m_params = new int[2];
//        filterRes.m_datas[0].m_params[0] = 1;
//        filterRes.m_datas[0].m_params[1] = 100;
//
//        filterRes.m_datas[1] = new FilterRes.FilterData();
//        filterRes.m_datas[1].m_res = R.drawable.ad66_filter2_needbmp2;
//        filterRes.m_datas[1].m_isSkipFace = false;
//        filterRes.m_datas[1].m_params = new int[2];
//        filterRes.m_datas[1].m_params[0] = 45;
//        filterRes.m_datas[1].m_params[1] = 100;
//        out.add(filterRes);
//
//        //5
//        filterRes = new FilterRes();
//        filterRes.m_isUpDateToCamera = false;
//        filterRes.m_filterAlpha = 100;
//        filterRes.m_isHasvignette = false;
//        filterRes.m_isSkipFace = false;
//        filterRes.m_datas = new FilterRes.FilterData[2];
//        filterRes.m_datas[0] = new FilterRes.FilterData();
//        filterRes.m_datas[0].m_res = R.drawable.ad66_filter5_needbmp1;
//        filterRes.m_datas[0].m_isSkipFace = false;
//        filterRes.m_datas[0].m_params = new int[2];
//        filterRes.m_datas[0].m_params[0] = 1;
//        filterRes.m_datas[0].m_params[1] = 100;
//
//         filterRes.m_datas[1] = new FilterRes.FilterData();
//        filterRes.m_datas[1].m_res = R.drawable.ad66_filter5_needbmp2;
//        filterRes.m_datas[1].m_isSkipFace = false;
//        filterRes.m_datas[1].m_params = new int[2];
//        filterRes.m_datas[1].m_params[0] = 38;
//        filterRes.m_datas[1].m_params[1] = 100;
//        out.add(filterRes);
//
//
//        //4
//        filterRes = new FilterRes();
//        filterRes.m_isUpDateToCamera = false;
//        filterRes.m_filterAlpha = 100;
//        filterRes.m_isHasvignette = false;
//        filterRes.m_isSkipFace = false;
//        filterRes.m_datas = new FilterRes.FilterData[3];
//        filterRes.m_datas[0] = new FilterRes.FilterData();
//        filterRes.m_datas[0].m_res = R.drawable.ad66_filter4_needbmp1;
//        filterRes.m_datas[0].m_isSkipFace = false;
//        filterRes.m_datas[0].m_params = new int[2];
//        filterRes.m_datas[0].m_params[0] = 1;
//        filterRes.m_datas[0].m_params[1] = 100;
//
//        filterRes.m_datas[1] = new FilterRes.FilterData();
//        filterRes.m_datas[1].m_res = R.drawable.ad66_filter4_needbmp2;
//        filterRes.m_datas[1].m_isSkipFace = false;
//        filterRes.m_datas[1].m_params = new int[2];
//        filterRes.m_datas[1].m_params[0] = 45;
//        filterRes.m_datas[1].m_params[1] = 100;
//
//           filterRes.m_datas[2] = new FilterRes.FilterData();
//        filterRes.m_datas[2].m_res = R.drawable.ad66_filter4_needbmp2;
//        filterRes.m_datas[2].m_isSkipFace = false;
//        filterRes.m_datas[2].m_params = new int[2];
//        filterRes.m_datas[2].m_params[0] = 46;
//        filterRes.m_datas[2].m_params[1] = 60;
//        out.add(filterRes);
//
//         //3
//        filterRes = new FilterRes();
//        filterRes.m_isUpDateToCamera = false;
//        filterRes.m_filterAlpha = 100;
//        filterRes.m_isHasvignette = false;
//        filterRes.m_isSkipFace = false;
//        filterRes.m_datas = new FilterRes.FilterData[2];
//        filterRes.m_datas[0] = new FilterRes.FilterData();
//        filterRes.m_datas[0].m_res = R.drawable.ad66_filter3_needbmp1;
//        filterRes.m_datas[0].m_isSkipFace = false;
//        filterRes.m_datas[0].m_params = new int[2];
//        filterRes.m_datas[0].m_params[0] = 1;
//        filterRes.m_datas[0].m_params[1] = 100;
//
//         filterRes.m_datas[1] = new FilterRes.FilterData();
//        filterRes.m_datas[1].m_res = R.drawable.ad66_filter3_needbmp2;
//        filterRes.m_datas[1].m_isSkipFace = false;
//        filterRes.m_datas[1].m_params = new int[2];
//        filterRes.m_datas[1].m_params[0] = 38;
//        filterRes.m_datas[1].m_params[1] = 100;
//        out.add(filterRes);

        return out;
    }

    private ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos1Real()
    {
        ArrayList<ADAbsAdapter.ADItemInfo> out = new ArrayList<>();
//        AD66Item itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_filter1_thumb;
//        itemInfo.m_selectRes = R.drawable.ad66_filter1_thumb_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_filter2_thumb;
//        itemInfo.m_selectRes = R.drawable.ad66_filter2_thumb_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_filter7_thumb;
//        itemInfo.m_selectRes = R.drawable.ad66_filter3_thumb_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_filter4_thumb;
//        itemInfo.m_selectRes = R.drawable.ad66_filter4_thumb_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_filter8_thumb;
//        itemInfo.m_selectRes = R.drawable.ad66_filter5_thumb_s;
//        out.add(itemInfo);

        return out;
    }

    private ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos2Real()
    {
       ArrayList<ADAbsAdapter.ADItemInfo> out = new ArrayList<>();

//        ADAbsAdapter.ADNullItem itemNullInfo = new ADAbsAdapter.ADNullItem();
//        itemNullInfo.m_res = R.drawable.ad66_lip_none_n;
//        itemNullInfo.m_selectRes = R.drawable.ad66_lip_none_s;
//        out.add(itemNullInfo);
//
//        AD66Item itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip15_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip15_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip2_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip2_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip1_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip1_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip3_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip3_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip4_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip4_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip6_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip6_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip7_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip7_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip8_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip8_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip9_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip9_s;
//        out.add(itemInfo);
//
//        itemInfo = new AD66Item();
//        itemInfo.m_res = R.drawable.ad66_lip10_n;
//        itemInfo.m_selectRes = R.drawable.ad66_lip10_s;
//        out.add(itemInfo);

        return out;
    }

    @Override
    public Bitmap getOrgBmp() {
        return m_orgBmp;
    }

    @Override
    public Bitmap getCurBmp() {
        return m_curBmp;
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
    }

    @Override
    public void faceckeck() {
        Message message = Message.obtain();
        message.what = FACECHECK;
        message.obj = m_orgBmp;
        m_threadHandler.sendMessage(message);
    }

    @Override
    public void makeAD66EffectFilter() {
        Message message = Message.obtain();
        message.what = AD66EFFECT_FILTER;
        m_threadHandler.sendMessage(message);
    }

    @Override
    public void makeAD66EffectMakeup() {
         Message message = Message.obtain();
        message.what = AD66EFFECT_MAKEUP;
        m_threadHandler.sendMessage(message);
    }

    @Override
    public void setThreadCallBack(ThreadCallBack callBack) {
        m_threadCB = callBack;
    }

    @Override
    public void changeProgress_filter(int value) {
        m_filterAlpha = value;
    }

    @Override
    public void changeProgress_makeup(int value) {
        m_lipEffectAlpha[m_curColorIndex - 1] = value;
    }

    @Override
    public void changeStyleIndex(int index) {
        m_curStyleIndex = index;
        makeAD66EffectFilter();
    }

    @Override
    public void changeColorIndex(int index) {
        m_curColorIndex = index;
        if(m_curColorIndex > -1)
        {
            makeAD66EffectMakeup();
        }
    }

    @Override
    public int getStyleIndex() {
        return m_curStyleIndex;
    }

    @Override
    public int getColorIndex() {
        return m_curColorIndex;
    }

    @Override
    public int getCurProgressValue() {
        return m_filterAlpha;
    }

    @Override
    public int getCurColorProgressValue() {
        return m_lipEffectAlpha[m_curColorIndex - 1];
    }

    @Override
    public Bitmap getFilterBmp() {
        return m_filterBmp;
    }

    @Override
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos1() {
        return m_itemInfos1;
    }

    @Override
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos2() {
        return m_itemInfos2;
    }

    @Override
    public void resetAlphas() {
        m_lipEffectAlpha[0] = 80;
        m_lipEffectAlpha[1] = 100;
        m_lipEffectAlpha[2] = 100;
        m_lipEffectAlpha[3] = 100;
        m_lipEffectAlpha[4] = 100;
        m_lipEffectAlpha[5] = 80;
        m_lipEffectAlpha[6] = 80;
        m_lipEffectAlpha[7] = 80;
        m_lipEffectAlpha[8] = 80;
        m_lipEffectAlpha[9] = 80;
    }

    @Override
    public void clearLipEffectBmp() {
        m_curBmp = null;
    }

    @Override
    public void Clear() {
        if(m_thread != null)
        {
            m_thread.quit();
        }
        FaceDataV2.ResetData();
        m_orgBmp = null;
        m_curBmp = null;
    }

    public class AD66Item extends ADAbsAdapter.ADItemInfo
    {
        public int m_selectRes;
    }
}
