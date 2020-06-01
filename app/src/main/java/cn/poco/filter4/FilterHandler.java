package cn.poco.filter4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.beautify.ImageProcessor;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.filter;
import cn.poco.image.filterori;
import cn.poco.resource.FilterRes;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.utils.PhotoMark;

/**
 * @author lmx
 *         Created by lmx on 2016/12/24.
 */

public class FilterHandler extends Handler
{

    public static final int MSG_INIT = 0x00000001;//初始化
    public static final int MSG_SAVE = 0x00000004; //保存
    public static final int MSG_CANCEL = 0x00000006;//取消
    public static final int MSG_BLUR_DARK = 0x00000014;//深景+暗角

    public static final int MSG_FILTER_RES = 0x00000016;

    protected Context mContext;
    protected Handler mUIHandler;

    private PocoFaceInfo[] pocoFaceInfos;

    private boolean isDetectFace = false;

    //颜色查表+光效 缓存
    private Bitmap mEffectCache;


    public FilterHandler(Looper looper, Handler mUIHandler, Context mContext)
    {
        super(looper);
        this.mUIHandler = mUIHandler;
        this.mContext = mContext;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case MSG_INIT:
            {
                handlerInit(msg);
                break;
            }
            case MSG_BLUR_DARK:
            {
                handlerBlurDark(msg);
                break;
            }
            case MSG_SAVE:
            {
                handlerSave(msg);
                break;
            }
            case MSG_CANCEL:
            {
                handlerCancel(msg);
                break;
            }
            case MSG_FILTER_RES:
            {
                handlerFilterRes(msg);
                break;
            }
        }
    }

    public static boolean isRecycledBmp(Bitmap in)
    {
        if (in != null && !in.isRecycled()) return false;
        return true;
    }

    public void recycleBmp(Bitmap bmp)
    {
        if (!isRecycledBmp(bmp)) bmp.recycle();
        bmp = null;
    }

    private void detectFaceInfoMulti(Context context, Bitmap out)
    {
        //人脸识别获取特征点，目前只做一次检测
        if (!isDetectFace)
        {
            if (!FaceDataV2.CHECK_FACE_SUCCESS)
            {
                if (out != null)
                {
                    FaceDataV2.CheckFace(context, out);//人脸检测
                }
            }

            if (FaceDataV2.CHECK_FACE_SUCCESS)
            {
                pocoFaceInfos = FaceDataV2.RAW_POS_MULTI;
            }
            else
            {
                //检测不成功，置空，不使用默认人脸数据
                pocoFaceInfos = null;
            }

            isDetectFace = true;
        }
    }

    //等比压缩图不需要重新检测人脸数据
    private void reDetectFaceInfoMulti(Context context, Bitmap out)
    {
        FaceDataV2.ResetData();
        pocoFaceInfos = null;
        isDetectFace = false;
        detectFaceInfoMulti(context, out);
    }

    private void handlerInit(Message msg)
    {
        FilterMsg params = (FilterMsg) msg.obj;
        msg.obj = null;

        Bitmap tempBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        //人脸数据识别
        detectFaceInfoMulti(mContext, tempBmp);

        //暗角 深景作为底层效果显示
        params.mBottomBmp = tempBmp.copy(Bitmap.Config.ARGB_8888, true);
        processBlurDark(mContext, params.hasBlur, params.hasDark, params.mBottomBmp, pocoFaceInfos);

        recycleBmp(mEffectCache);

        if (params.res != null && params.res instanceof FilterRes)
        {
            //滤镜处理叠加深影暗角上层效果显示
            params.mTopBmp = processFilterRes(mContext, tempBmp.copy(Bitmap.Config.ARGB_8888, true), (FilterRes) params.res, params.hasBlur, params.hasDark, pocoFaceInfos);
        }
        else
        {
            params.mTopBmp = params.mBottomBmp.copy(Bitmap.Config.ARGB_8888, true);
        }

        sendUIMessage(MSG_INIT, params);
    }

    private void handlerFilterRes(Message msg)
    {
        FilterResMsg params = (FilterResMsg) msg.obj;
        msg.obj = null;

        Bitmap tempBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        //人脸数据识别
        detectFaceInfoMulti(mContext, tempBmp);

        params.mBottomBmp = tempBmp.copy(Bitmap.Config.ARGB_8888, true);
        processBlurDark(mContext, params.hasBlur, params.hasDark, params.mBottomBmp, pocoFaceInfos);

        recycleBmp(mEffectCache);
        if (params.res != null && params.res instanceof FilterRes)
        {
            params.mTopBmp = processFilterRes(mContext, tempBmp.copy(Bitmap.Config.ARGB_8888, true), (FilterRes) params.res, params.hasBlur, params.hasDark, pocoFaceInfos);
        }
        else
        {
            params.mTopBmp = params.mBottomBmp.copy(Bitmap.Config.ARGB_8888, true);
        }

        sendUIMessage(MSG_FILTER_RES, params);
    }

    private void handlerBlurDark(Message msg)
    {
        BlurDarkMsg params = (BlurDarkMsg) msg.obj;
        msg.obj = null;

        Bitmap tempBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        params.mBottomBmp = tempBmp.copy(Bitmap.Config.ARGB_8888, true);
        processBlurDark(mContext, params.hasBlur, params.hasDark, params.mBottomBmp, pocoFaceInfos);

        if (!isRecycledBmp(mEffectCache))
        {
            params.mTopBmp = mEffectCache.copy(Bitmap.Config.ARGB_8888, true);//滤镜 - 暗角虚化
            processBlurDark(mContext, params.hasBlur, params.hasDark, params.mTopBmp, pocoFaceInfos);
        }
        else if (params.res != null && params.res instanceof FilterRes)
        {
            params.mTopBmp = processFilterRes(mContext, tempBmp.copy(Bitmap.Config.ARGB_8888, true), (FilterRes) params.res, params.hasBlur, params.hasDark, pocoFaceInfos);
        }
        else
        {
            params.mTopBmp = params.mBottomBmp.copy(Bitmap.Config.ARGB_8888, true);
        }
        sendUIMessage(MSG_BLUR_DARK, params);
    }

    private void handlerSave(Message msg)
    {
        SaveMsg params = (SaveMsg) msg.obj;
        msg.obj = null;

        recycleBmp(mEffectCache);

        sendUIMessage(MSG_SAVE, MakeOutUpBmp(mContext, params));
    }

    private void handlerCancel(Message msg)
    {

        recycleBmp(mEffectCache);

        sendUIMessage(MSG_CANCEL, null);
    }

    private void processBlurDark(Context context, boolean hasBlur, boolean hasDark, Bitmap dest, PocoFaceInfo[] pocoFaceInfos)
    {
        if (hasBlur)
        {
            filter.circleBlur_v2(dest, context, pocoFaceInfos);
        }
        if (hasDark)
        {
            filter.darkCorner_v2(dest, context);
        }
    }

    public Bitmap processFilterRes(Context context, Bitmap destBmp, FilterRes filterRes, boolean isBlur, boolean isDark, PocoFaceInfo[] pocoFaceInfos)
    {
        return processFilterRes(context, destBmp, filterRes, isBlur, isDark, pocoFaceInfos, false);
    }

    /**
     * 颜色查表处理 - 光效处理 - 暗角深影处理
     *
     * @param context
     * @param destBmp
     * @param filterRes
     * @param isBlur
     * @param isDark
     * @param isSave    true 不对暗角虚化处理，在保存的最后一步操作
     * @return
     */
    public Bitmap processFilterRes(Context context, Bitmap destBmp, FilterRes filterRes, boolean isBlur, boolean isDark, PocoFaceInfo[] pocoFaceInfos, boolean isSave)
    {
        Bitmap out = destBmp;

        if (out == null || filterRes == null) return out;

        if (filterRes.m_datas != null && filterRes.m_datas.length > 0)
        {
            Bitmap colorBmp = null;

            Bitmap[] mask = null;
            int[] comop = null;
            int[] opacity = null;
            boolean isHollow = false;//是否避开人脸，默认取颜色查表资源的值

            FilterRes.FilterData[] m_datas = filterRes.m_datas;
            int size = m_datas.length;

            recycleBmp(mEffectCache);

            if (size > 1)
            {
                int maskSize = size - 1;//第1个下标开始为mask
                mask = new Bitmap[maskSize];
                comop = new int[maskSize];
                opacity = new int[maskSize];
            }

            for (int i = 0; i < size; i++)
            {
                FilterRes.FilterData data = m_datas[i];
                if (data == null || data.m_res == null || data.m_params == null) break;

                if (i == 0)
                {//颜色查表
                    isHollow = data.m_isSkipFace;
                    filterRes.m_isSkipFace = isHollow;
                    try
                    {
                        if (data.m_res instanceof String)
                        {
                            colorBmp = BitmapFactory.decodeFile((String) data.m_res);
                        }
                        else if (data.m_res instanceof Integer)
                        {
                            colorBmp = BitmapFactory.decodeResource(context.getResources(), (Integer) data.m_res);
                        }
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
                }
                else
                {//mask遮罩
                    if (mask != null && mask.length > i - 1)
                    {
                        try
                        {
                            if (data.m_res instanceof String)
                            {
                                mask[i - 1] = BitmapFactory.decodeFile((String) data.m_res);
                            }
                            else if (data.m_res instanceof Integer)
                            {
                                mask[i - 1] = BitmapFactory.decodeResource(context.getResources(), (Integer) data.m_res);
                            }
                        }
                        catch (Throwable t)
                        {
                            t.printStackTrace();
                        }
                        if (data.m_params != null && data.m_params.length == 2)
                        {
                            comop[i - 1] = data.m_params[0];
                            opacity[i - 1] = data.m_params[1];
                        }
                    }
                }
            }


            out = filterori.loadFilterV2_rs(context, out, colorBmp, mask, comop, opacity, pocoFaceInfos, isHollow);
            if (mask != null)
            {
                for (Bitmap bitmap : mask)
                {
                    recycleBmp(bitmap);
                }
            }
            recycleBmp(colorBmp);
            mEffectCache = out.copy(Bitmap.Config.ARGB_8888, true);
        }

        if (!isSave)
        {
            processBlurDark(context, isBlur, isDark, out, pocoFaceInfos);
        }
        return out;
    }

    private void sendUIMessage(int what, Object obj)
    {
        Message msg = mUIHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        mUIHandler.sendMessage(msg);
    }

    public Bitmap MakeOutUpBmp(Context context, SaveMsg params)
    {
        Bitmap out = null;
        RotationImg2 info = null;
        if (params.mImgs instanceof RotationImg2[])
        {
            info = ((RotationImg2[]) params.mImgs)[0];
        }
        else if (params.mImgs instanceof ImageFile2)
        {
            info = ((ImageFile2) params.mImgs).GetRawImg()[0];
        }
        else if (params.mImgs instanceof Bitmap)
        {
            out = (Bitmap) params.mImgs;
        }

        if (info != null)
        {
            out = cn.poco.imagecore.Utils.DecodeFinalImage(context, info.m_img, info.m_degree, -1, info.m_flip, params.outSize, params.outSize);
        }

        //reset face data
//        reDetectFaceInfoMulti(context, out);

        if (out == null) return null;

        Bitmap filterBmp;
        if (params.res != null && params.res instanceof FilterRes)
        {
            filterBmp = out.copy(Bitmap.Config.ARGB_8888, true);
            filterBmp = processFilterRes(context, filterBmp, (FilterRes) params.res, params.hasBlur, params.hasDark, pocoFaceInfos, true);
        }
        else
        {
            filterBmp = out;
        }

        //滤镜透明度
        if (params.filterAlpha == 0)
        {
        }
        else if (params.filterAlpha == 100)
        {
            out = filterBmp;
        }
        else if (params.filterAlpha > 0 && params.filterAlpha < 100)
        {
            out = ImageProcessor.DrawMask2(out, filterBmp, params.filterAlpha);
        }

        //最后处理暗角虚化效果
        processBlurDark(context, params.hasBlur, params.hasDark, out, pocoFaceInfos);

        //水印处理
        if (params.hasWaterMark && params.waterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(mContext))
        {
            try
            {
                WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(params.waterMarkId);
                if (watermarkItem != null && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(mContext))
                {
                    Bitmap watermark = MakeBmpV2.DecodeImage(context, watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
                    PhotoMark.drawWaterMarkLeft(out, watermark, params.hasDateMark);
                    recycleBmp(watermark);
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
        return out;
    }

    public void ReleaseMem()
    {
        recycleBmp(mEffectCache);
        pocoFaceInfos = null;
    }

    public static class BaseMsg
    {
        public Object res; //filterres

        //in
        public Object mImgs;
        public Bitmap mOrgBmp;

        //out
        public Bitmap mBottomBmp;
        public Bitmap mTopBmp;

        public int filterAlpha;
        public int filterUri;

        public boolean hasBlur;
        public boolean hasDark;
    }

    public static class FilterMsg extends BaseMsg
    {
    }

    public static class BlurDarkMsg extends BaseMsg
    {
    }

    public static class SaveMsg extends BaseMsg
    {
        public int outSize;
        public boolean hasWaterMark;
        public boolean hasDateMark;
        public int waterMarkId;
    }

    public static class FilterResMsg extends BaseMsg
    {
    }
}
