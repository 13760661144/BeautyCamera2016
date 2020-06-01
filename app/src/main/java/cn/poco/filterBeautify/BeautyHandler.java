package cn.poco.filterBeautify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.beautify.ImageProcessor;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.face.FaceDataV2;
import cn.poco.filter4.WatermarkItem;
import cn.poco.framework.FileCacheMgr;
import cn.poco.image.CrazyShapeFilter;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.image.PocoCameraEffect;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.filter;
import cn.poco.image.filterori;
import cn.poco.resource.FilterRes;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.utils.FileUtil;
import cn.poco.utils.PhotoMark;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-27.
 *
 *  处理顺序： 美牙 -> 脸型 -> 美肤（磨皮）
 */

public class BeautyHandler extends Handler
{
    public static final int MSG_INIT = 1;//初始化
    public static final int MSG_SAVE = 1 << 1; //保存
    public static final int MSG_CANCEL = 1 << 2;//取消
    public static final int MSG_RESTORE = 1 << 3;//返回编辑
    public static final int MSG_ADJUST = 1 << 4;//美颜微调
    public static final int MSG_BLUR_DARK = 1 << 5;//深景+暗角
    public static final int MSG_ADVANCED = 1 << 6;//美颜美形
    public static final int MSG_FILTER = 1 << 7;//滤镜

    private Context mContext;
    private Handler mUIHandler;

    //脸型+美肤 缓存
    private Bitmap mShapeCache;

    //人脸数据
    private PocoFaceInfo[] pocoFaceInfos;

    //是否已检测人脸
    private boolean isDetectFace = false;

    //框架是否保存图片
    private boolean isSaveImageFile;

    //肤色处理file文件
    private byte[] mSkinBeautyByte;

    //框架保存图片
    private SaveImgFileThread mSaveImgFileThread;

    public BeautyHandler(Looper looper, Context mContext, Handler mUIHandler)
    {
        super(looper);
        this.mContext = mContext;
        this.mUIHandler = mUIHandler;
        mSkinBeautyByte = FileUtil.getAssetsByte(mContext, "skinStyle/level_table_1");
    }

    @Override
    public void handleMessage(Message msg)
    {
        long start = System.currentTimeMillis();
        switch (msg.what)
        {
            case MSG_INIT:
                handleInit(msg);
                break;
            case MSG_SAVE:
                handlerSave(msg);
                break;
            case MSG_CANCEL:
                handleCancel(msg);
                break;
            case MSG_RESTORE:
                handlerRestore(msg);
                break;
            case MSG_ADJUST:
                handleAdjust(msg);
                break;
            case MSG_BLUR_DARK:
                handleBlurDark(msg);
                break;
            case MSG_ADVANCED:
                handlerAdvanced(msg);
                break;
            case MSG_FILTER:
                handlerFilter(msg);
                break;
            default:
                break;
        }
        //Log.d("bbb", "BeautyHandler --> handleMessage: " + msg.what + " , " + (System.currentTimeMillis() - start) * 1f / 1000);
    }

    private void handleInit(Message msg)
    {
        BeautyMsg params = (BeautyMsg) msg.obj;
        msg.obj = null;

        //框架保存图片
        saveOriginalImageFile(params.mImgs);

        //原图在主线程上显示
        Bitmap orgBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        if (isRecycled(orgBmp))
        {
            sendUIMsg(MSG_INIT, params);
            return;
        }

        //人脸检测
        detectFaceInfoMulti(mContext, orgBmp);

        if (isRecycled(mShapeCache))
        {
            Bitmap dstBmp = orgBmp.copy(Bitmap.Config.ARGB_8888, true);
            dstBmp = doShape(mContext, dstBmp, pocoFaceInfos, params.mFilterBeautyParams, params.mFaceSize);
            mShapeCache = doSkinBeauty(mContext, dstBmp, mSkinBeautyByte, params.mFilterBeautyParams.getSkinTypeSize());
        }

        //美颜（磨皮）效果
        Bitmap beautyBmp = PocoBeautyFilter.CameraSmoothBeauty(mShapeCache.copy(Bitmap.Config.ARGB_8888, true),
                pocoFaceInfos, GetRealMopiSize(params.mAdjustSize), true);

        //底图
        Bitmap bottomBmp = beautyBmp.copy(Bitmap.Config.ARGB_8888, true);
        bottomBmp = doBlurDark(mContext, pocoFaceInfos, bottomBmp, params.isBlur, params.isDark);
        params.mBottomBmp = bottomBmp;
        bottomBmp = null;

        //顶图
        if (params.mFilterRes != null)
        {
            params.mTopBmp = doFilter(mContext, beautyBmp,
                    params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, false);
        }
        beautyBmp = null;
        sendUIMsg(MSG_INIT, params);
    }

    private void handlerRestore(Message msg)
    {
        RestoreMsg params = (RestoreMsg) msg.obj;
        msg.obj = null;

        //原图在主线程上显示
        Bitmap orgBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        if (isRecycled(orgBmp))
        {
            sendUIMsg(MSG_RESTORE, params);
            return;
        }

        //人脸检测
        detectFaceInfoMulti(mContext, orgBmp);

        Bitmap dstBmp = orgBmp.copy(Bitmap.Config.ARGB_8888, true);
        dstBmp = doShape(mContext, dstBmp, pocoFaceInfos, params.mFilterBeautyParams, params.mFaceSize);
        mShapeCache = doSkinBeauty(mContext, dstBmp, mSkinBeautyByte, params.mFilterBeautyParams.getSkinTypeSize());


        //如果恢复缓存图有效，则不需再次处理底图和顶图
        if (params.isRestoreValid)
        {
            sendUIMsg(MSG_RESTORE, params);
            return;
        }

        //美颜（磨皮）效果
        Bitmap beautyBmp = PocoBeautyFilter.CameraSmoothBeauty(mShapeCache.copy(Bitmap.Config.ARGB_8888, true),
                pocoFaceInfos, GetRealMopiSize(params.mAdjustSize), true);

        //底图
        Bitmap bottomBmp = beautyBmp.copy(Bitmap.Config.ARGB_8888, true);
        bottomBmp = doBlurDark(mContext, pocoFaceInfos, bottomBmp, params.isBlur, params.isDark);
        params.mBottomBmp = bottomBmp;
        bottomBmp = null;

        //顶图
        if (params.mFilterRes != null)
        {
            params.mTopBmp = doFilter(mContext, beautyBmp,
                    params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, false);
        }
        beautyBmp = null;
        sendUIMsg(MSG_RESTORE, params);
    }

    private void handleAdjust(Message msg)
    {
        BeautyMsg params = (BeautyMsg) msg.obj;
        msg.obj = null;

        if (isRecycled(mShapeCache))
        {
            sendUIMsg(MSG_ADJUST, params);
            return;
        }

        //美颜（磨皮）调动 效果
        Bitmap beautyBmp = PocoBeautyFilter.CameraSmoothBeauty(mShapeCache.copy(Bitmap.Config.ARGB_8888, true),
                pocoFaceInfos, GetRealMopiSize(params.mAdjustSize), true);

        //底图
        Bitmap bottomBmp = beautyBmp.copy(Bitmap.Config.ARGB_8888, true);
        bottomBmp = doBlurDark(mContext, pocoFaceInfos, bottomBmp, params.isBlur, params.isDark);
        params.mBottomBmp = bottomBmp;
        bottomBmp = null;

        //顶图
        if (params.mFilterRes != null)
        {
            params.mTopBmp = doFilter(mContext, beautyBmp,
                    params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, false);
        }
        beautyBmp = null;

        sendUIMsg(MSG_ADJUST, params);
    }

    public void handleBlurDark(Message msg)
    {
        BeautyMsg params = (BeautyMsg) msg.obj;
        msg.obj = null;

        if (!isRecycled(mShapeCache))
        {
            //美颜（磨皮）调动 效果
            Bitmap beautyBmp = PocoBeautyFilter.CameraSmoothBeauty(mShapeCache.copy(Bitmap.Config.ARGB_8888, true),
                    pocoFaceInfos, GetRealMopiSize(params.mAdjustSize), true);

            //底图
            Bitmap bottomBmp = beautyBmp.copy(Bitmap.Config.ARGB_8888, true);
            bottomBmp = doBlurDark(mContext, pocoFaceInfos, bottomBmp, params.isBlur, params.isDark);
            params.mBottomBmp = bottomBmp;
            bottomBmp = null;

            //顶图
            if (params.mFilterRes != null)
            {
                params.mTopBmp = doFilter(mContext, beautyBmp,
                        params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, false);
            }
            beautyBmp = null;
        }

        sendUIMsg(MSG_BLUR_DARK, params);
    }

    private void handlerFilter(Message msg)
    {
        BeautyMsg params = (BeautyMsg) msg.obj;
        msg.obj = null;

        //框架保存图片
        saveOriginalImageFile(params.mImgs);

        //原图在主线程上显示
        Bitmap orgBmp = params.mOrgBmp;
        params.mOrgBmp = null;

        if (isRecycled(orgBmp))
        {
            sendUIMsg(MSG_FILTER, params);
            return;
        }

        //人脸检测
        detectFaceInfoMulti(mContext, orgBmp);

        if (isRecycled(mShapeCache))
        {
            Bitmap dstBmp = orgBmp.copy(Bitmap.Config.ARGB_8888, true);
            dstBmp = doShape(mContext, dstBmp, pocoFaceInfos, params.mFilterBeautyParams ,params.mFaceSize);
            mShapeCache = doSkinBeauty(mContext, dstBmp, mSkinBeautyByte, params.mFilterBeautyParams.getSkinTypeSize());
        }

        //美颜（磨皮）效果
        Bitmap beautyBmp = PocoBeautyFilter.CameraSmoothBeauty(mShapeCache.copy(Bitmap.Config.ARGB_8888, true),
                pocoFaceInfos, GetRealMopiSize(params.mAdjustSize), true);

        //底图
        Bitmap bottomBmp = beautyBmp.copy(Bitmap.Config.ARGB_8888, true);
        bottomBmp = doBlurDark(mContext, pocoFaceInfos, bottomBmp, params.isBlur, params.isDark);
        params.mBottomBmp = bottomBmp;
        bottomBmp = null;

        //顶图
        if (params.mFilterRes != null)
        {
            params.mTopBmp = doFilter(mContext, beautyBmp,
                    params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, false);
        }
        beautyBmp = null;

        sendUIMsg(MSG_FILTER, params);
    }

    private void handlerAdvanced(Message msg)
    {
        if (mSaveImgFileThread != null) {
            try
            {
                mSaveImgFileThread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        AdvancedMsg params = (AdvancedMsg) msg.obj;
        msg.obj = null;

        Bitmap out = MakeOutBitmap(mContext, params);
        if (!isRecycled(out))
        {
            String path = FileCacheMgr.GetLinePath();
            path = Utils.SaveImg(mContext, out, path, 100, false);
            if (path != null)
            {
                params.mTempPath = path;
                RotationImg2 img = Utils.Path2ImgObj(path);
                if (img != null)
                {
                    params.mImgs = img;
                }
                out.recycle();
                out = null;
            }
        }

        sendUIMsg(MSG_ADVANCED, params);
    }

    private void handlerSave(Message msg)
    {
        if (mSaveImgFileThread != null) {
            try
            {
                mSaveImgFileThread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        SaveMsg params = (SaveMsg) msg.obj;
        msg.obj = null;

        Bitmap bitmap = MakeOutBitmap(mContext, params);
        if (SettingInfoMgr.GetSettingInfo(mContext).GetAddDateState())
        {
            //添加拍照日期
            PhotoMark.drawDataLeft(bitmap);
        }

        if (isRecycled(bitmap))
        {
            sendUIMsg(MSG_SAVE, params);
            return;
        }

        Object out = null;
        //保存成文件路径并且通知刷新相册
        if (params.saveToFile)
        {
            out = Utils.SaveImg(mContext, bitmap, Utils.MakeSavePhotoPath(mContext, (float) bitmap.getWidth() / bitmap.getHeight()), 100, true);
        }
        else
        {
            out = bitmap;
        }

        if (params.isShare && mUIHandler != null)
        {
            mUIHandler.obtainMessage(MSG_SAVE, 1, 0, out).sendToTarget();
        }
        else
        {
            sendUIMsg(MSG_SAVE, out);
        }
    }

    private void handleCancel(Message msg)
    {
        recycleBmp(mShapeCache);
        sendUIMsg(MSG_CANCEL, null);
    }

    public int GetRealMopiSize(int mopiUiSize)
    {
        return Math.round(BeautyShapeDataUtils.GetRealSkinBeautySize((mopiUiSize)));
    }


    public Bitmap MakeOutBitmap(Context context, SaveMsg params)
    {
        RotationImg2 info = null;
        Bitmap out = null;
        Bitmap orgBmp = null;

        if (params.mImgs instanceof RotationImg2[])
        {
            info = ((RotationImg2[]) params.mImgs)[0];
        }
        else if (params.mImgs instanceof ImageFile2)
        {
            info = ((ImageFile2) params.mImgs).GetRawImg()[0];
        }
        else if (params.mImgs instanceof RotationImg2)
        {
            info = (RotationImg2) params.mImgs;
        }
        else if (params.mImgs instanceof Bitmap)
        {
            orgBmp = (Bitmap) params.mImgs;
        }
        params.mImgs = null;

        if (info != null)
        {
            //原图
            orgBmp = cn.poco.imagecore.Utils.DecodeFinalImage(context, info.m_img, info.m_degree, -1, info.m_flip, params.mOutSize, params.mOutSize);
        }

        if (orgBmp == null) return null;

        if (params.needDetectFace)
        {
            redetectFaceInfoMulti(mContext, orgBmp);
        }
        else
        {
            detectFaceInfoMulti(mContext, orgBmp);
        }

        Bitmap shapeBmp = doShape(mContext, orgBmp, pocoFaceInfos, params.mFilterBeautyParams ,params.mFaceSize);
        doSkinBeauty(mContext, shapeBmp, mSkinBeautyByte, params.mFilterBeautyParams.getSkinTypeSize());

        //TODO 图片大小导致祛痘程度力度不一致
        out = PocoBeautyFilter.CameraSmoothBeauty(shapeBmp, pocoFaceInfos,
                GetRealMopiSize(params.mAdjustSize), false);

        if (!isRecycled(out)) {
            if (params.mFilterAlpha == 0) {
                //滤镜alpha为0，无滤镜效果
                out = doBlurDark(context, pocoFaceInfos, out, params.isBlur, params.isDark);
            } else if (params.mFilterAlpha == 100) {
                out = doFilter(context, out, params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, true);
                out = doBlurDark(context, pocoFaceInfos, out, params.isBlur, params.isDark);
            } else if (params.mFilterAlpha > 0 && params.mFilterAlpha < 100) {
                Bitmap filterBmp;
                if (params.mFilterRes != null) {
                    filterBmp = doFilter(context, out.copy(Bitmap.Config.ARGB_8888, true), params.mFilterRes, params.isBlur, params.isDark, pocoFaceInfos, true);
                }
                else {
                    filterBmp = out;
                }
                out = ImageProcessor.DrawMask2(out, filterBmp, params.mFilterAlpha);
                out = doBlurDark(context, pocoFaceInfos, out, params.isBlur, params.isDark);
            }
        }

        //水印处理
        if (params.hasWaterMark &&
                params.waterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(mContext))
        {
            try
            {
                WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(params.waterMarkId);
                if (watermarkItem != null
                        && watermarkItem.res != null
                        && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(mContext))
                {
                    Bitmap watermark = MakeBmpV2.DecodeImage(context, watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
                    PhotoMark.drawWaterMarkLeft(out, watermark, params.hasDateMark);
                    watermark = null;
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }

        return out;
    }

    /**
     * 颜色查表处理 - 光效处理 - 暗角深影处理
     *
     * @param context
     * @param destBmp
     * @param filterRes
     * @param isBlur
     * @param isDark
     * @param isSave    true 不处理暗角虚化操作，false 处理暗角虚拟化操作
     * @return
     */
    public Bitmap doFilter(Context context, Bitmap destBmp, FilterRes filterRes, boolean isBlur, boolean isDark, PocoFaceInfo[] pocoFaceInfos, boolean isSave)
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
                {   //mask遮罩
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

        }
        if (!isSave)
        {
            doBlurDark(context, pocoFaceInfos, out, isBlur, isDark);
        }
        return out;
    }


    public Bitmap doBlurDark(Context context, PocoFaceInfo[] pocoFaceInfos, Bitmap dst, boolean isBlur, boolean isDark)
    {
        Bitmap out = dst;
        if (isRecycled(dst)) return out;
        if (isBlur)
        {
            filter.circleBlur_v2(dst, context, pocoFaceInfos);
        }
        if (isDark)
        {
            filter.darkCorner_v2(dst, context);
        }
        return out;
    }


    /**
     * 脸型参数处理<br/>
     * 处理顺序 美牙 -> 脸型
     *
     * @param context
     * @param dest
     * @param pocoFaceInfos
     * @param params
     * @return
     */
    private Bitmap doShape(Context context,
                           Bitmap dest,
                           PocoFaceInfo[] pocoFaceInfos,
                           FilterBeautyParams params,
                           int faceSize)
    {
        Bitmap out = dest;
        if (out != null && !out.isRecycled() && params != null && params.shapeData != null)
        {
            if (pocoFaceInfos != null && pocoFaceInfos.length >= 1)
            {
                // if (faceSize <= 0)
                // {
                //     faceSize = 1;//只做一个人脸处理
                // }
                // else
                // {
                //     faceSize = Math.min(pocoFaceInfos.length, faceSize);
                // }

                //NOTE 人脸个数有底层人脸参数决定
                faceSize = pocoFaceInfos.length;

                //先处理美牙
                for (int i = 0; i < faceSize; i++)
                {
                    PocoFaceInfo faceInfo = pocoFaceInfos[i];
                    if (faceInfo != null)
                    {
                        //美牙处理
                        out = PocoCameraEffect.TeethWhiten(out, context, faceInfo, (int) params.getWhitenTeethSize());
                    }
                }

                //后处理脸型
                Bitmap faceMaskBitmap = decodeFaceMaskBitmap(context);
                out = CrazyShapeFilter.SuperCrazyShape_Multi(out, faceMaskBitmap, pocoFaceInfos, params.shapeData);
                if (faceMaskBitmap != null && !faceMaskBitmap.isRecycled() && faceMaskBitmap != out) {
                    faceMaskBitmap.recycle();
                }
                faceMaskBitmap = null;
            }
        }

        return out;
    }


    private Bitmap decodeFaceMaskBitmap(Context context) {
        Bitmap out = null;
        if (context != null && context.getResources() != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                out = BitmapFactory.decodeResource(context.getResources(), R.drawable.facemask, options);
                if (out != null && (!out.isMutable() || out.getConfig() != Bitmap.Config.ARGB_8888)) {
                    out = out.copy(Bitmap.Config.ARGB_8888, true);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return out;
    }

    //框架保存图片
    private void saveOriginalImageFile(Object img)
    {
        if (img == null || !(img instanceof ImageFile2)) return;

        if (isSaveImageFile) return;

        if (mSaveImgFileThread == null)
        {
            mSaveImgFileThread = new SaveImgFileThread();
        }
        if (mSaveImgFileThread.isAlive()) return;

        try
        {
            mSaveImgFileThread.context = mContext;
            mSaveImgFileThread.img = img;
            mSaveImgFileThread.start();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    // /**
    //  * 只做瘦脸、大眼、瘦鼻、美牙处理
    //  *
    //  * @param dest
    //  * @param shoulianSize
    //  * @param dayanSize
    //  * @return
    //  */
    // private Bitmap doShape(Context context,
    //                        Bitmap dest,
    //                        PocoFaceInfo[] pocoFaceInfos,
    //                        int shoulianSize,
    //                        int dayanSize,
    //                        int shoubiSize,
    //                        int meiyaSize,
    //                        int faceSize)
    // {
    //     Bitmap out = dest;
    //
    //     if (out != null && !out.isRecycled())
    //     {
    //
    //         if (pocoFaceInfos != null && pocoFaceInfos.length >= 1)
    //         {
    //             if (faceSize <= 0)
    //             {
    //                 faceSize = 1;//只做一个人脸处理
    //             }
    //             else
    //             {
    //                 faceSize = pocoFaceInfos.length >= faceSize ? faceSize : pocoFaceInfos.length;
    //             }
    //
    //             for (int i = 0; i < faceSize; i++)
    //             {
    //                 PocoFaceInfo info = pocoFaceInfos[i];
    //
    //                 //瘦脸
    //                 if (shoulianSize > 0)
    //                 {
    //                     out = PocoCameraEffect.RealtimeSmallFace(out, info, shoulianSize);
    //                 }
    //
    //                 //大眼
    //                 if (dayanSize > 0)
    //                 {
    //                     out = PocoCameraEffect.RealtimeBigEye(out, info, dayanSize);
    //                 }
    //
    //                 //瘦鼻
    //                 if (shoubiSize > 0)
    //                 {
    //                     out = PocoCameraEffect.ShinkNose(out, info, shoubiSize);
    //                 }
    //
    //                 //美牙
    //                 if (meiyaSize > 0)
    //                 {
    //                     out = PocoCameraEffect.TeethWhiten(out, context, info, meiyaSize);
    //                 }
    //             }
    //         }
    //
    //     }
    //     return out;
    // }

    /**
     * 美肤（肤色）处理
     */
    private Bitmap doSkinBeauty(Context context,
                                Bitmap dst,
                                byte[] handlerByte,
                                float skinTypeSize)
    {
        Bitmap out = dst;
        if (!isRecycled(dst) && handlerByte != null && skinTypeSize > 0)
        {
            out = PocoBeautyFilter.CameraSkinBeauty(dst, handlerByte, (int) skinTypeSize);
        }
        return out;
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
            {   //检测不成功，置空，不使用默认人脸数据
                pocoFaceInfos = FaceDataV2.RAW_POS_MULTI;
            }
            else
            {
                pocoFaceInfos = null;
            }
            isDetectFace = true;
        }
    }

    //等比压缩图不需要重新检测人脸数据
    private void redetectFaceInfoMulti(Context context, Bitmap out)
    {
        FaceDataV2.ResetData();
        pocoFaceInfos = null;
        isDetectFace = false;
        detectFaceInfoMulti(context, out);
    }


    public boolean isRecycled(Bitmap bitmap)
    {
        return bitmap == null || bitmap.isRecycled();
    }

    public void recycleBmp(Bitmap bitmap)
    {
        if (!isRecycled(bitmap))
        {
            bitmap.recycle();
        }
    }

    private void sendUIMsg(int what, Object obj)
    {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(what, obj).sendToTarget();
        }
    }

    protected final class SaveImgFileThread extends Thread
    {
        Object img;

        Context context;

        @Override
        public void run()
        {
            Thread.currentThread().setName("save_img_file_thread");

            if (img == null || context == null)
            {
                return;
            }
            try
            {
                if (img instanceof ImageFile2)
                {
                    ((ImageFile2) img).SaveImg2(context);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                isSaveImageFile = true;
            }
        }

        public void release()
        {
            try {
                this.interrupt();
            }
            catch (Throwable t1) {
                t1.printStackTrace();
            }
            finally
            {
                img = null;
                context = null;
            }
        }
    }

    public static class BeautyMsg
    {
        public FilterBeautyParams mFilterBeautyParams = new FilterBeautyParams();

        public boolean isBlur;
        public boolean isDark;

        public int mFilterAlpha;
        public int mFilterUri;

        protected int mFaceSize = 5;       //美形人脸个数（最大5个人脸）

        public FilterRes mFilterRes;

        public int mFrW;
        public int mFrH;

        public int mAdjustSize;         //美肤（肤质）

        //out
        public Bitmap mBottomBmp;
        public Bitmap mTopBmp;

        //in
        public Bitmap mOrgBmp;
        public Object mImgs;
    }

    public static class SaveMsg extends BeautyMsg
    {
        public boolean saveToFile = true;   //是否保存成文件路径
        public boolean isShare = false;     //是否是分享调用
        public int mOutSize;

        public boolean hasWaterMark;
        public boolean hasDateMark;

        public int waterMarkId;
        public boolean needDetectFace;  //重新检测人脸识别
    }

    public static class AdvancedMsg extends SaveMsg
    {
        public String mTempPath;
    }

    public static class RestoreMsg extends BeautyMsg
    {
        //如果恢复缓存图有效，则不需再次处理底图和顶图
        boolean isRestoreValid = true;
    }

    public void clear()
    {
        mShapeCache = null;
        mContext = null;
        mUIHandler = null;
        mSkinBeautyByte = null;
        if (mSaveImgFileThread != null) {
            mSaveImgFileThread.release();
        }
        mSaveImgFileThread = null;
    }


}
