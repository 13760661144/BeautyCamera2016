package cn.poco.filterBeautify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.poco.beautify.ImageProcessor;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.face.FaceDataV2;
import cn.poco.image.CrazyShapeFilter;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.image.PocoCameraEffect;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.filter;
import cn.poco.image.filterori;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/4/25.
 */

//美形 -- 美颜（磨皮）-- 滤镜 -- 虚化暗角
public class FilterBeautifyProcessor
{

    public static final int ONLY_BEAUTY = 1;    //单独处理美颜

    public static final int ONLY_SHAPE = 1 << 1;//单独处理美形

    public static final int ONLY_FILTER = 1 << 2;//单独处理滤镜

    public static final int FILTER_BEAUTY = ONLY_BEAUTY | ONLY_SHAPE | ONLY_FILTER;//美颜美形滤镜

    public static int PROCESS_MAX_SIZE = 1980;

    /**
     * @param mask 运算值
     * @param arg  状态
     * @return true运算值拥有该状态
     */
    public static boolean HasMask(int mask, int arg)
    {
        return (mask & arg) > 0;
    }

    public static boolean HasBeauty(int mask)
    {
        return HasMask(mask, ONLY_BEAUTY);
    }

    public static boolean HasShape(int mask)
    {
        return HasMask(mask, ONLY_SHAPE);
    }

    public static boolean HasFilter(int mask)
    {
        return HasMask(mask, ONLY_FILTER);
    }


    public static final int MSG_FILTER_BEAUTIFY_PROCESS = 0x11222222;

    public static int PROCESS_FACE_SIZE = 1;

    public static Bitmap ProcessFilterBeautify(Context context, FilterBeautifyInfo info)
    {
        ShareData.InitData(context);

        return ProcessFilterBeautify(context, info, true);
    }

    public static Bitmap ProcessFilterBeautify(Context context, FilterBeautifyInfo info, boolean isClearFaceData)
    {
        Bitmap out = null;

        if (info != null && info.imgs != null && context != null)
        {

            Object img = info.imgs;
            info.imgs = null;

            // 限制图片大小
           // long start = System.currentTimeMillis();
            final int DEF_SIZE = PROCESS_MAX_SIZE;
            int w = ShareData.m_screenRealWidth;
            if(w > DEF_SIZE)
            {
                w = DEF_SIZE;
            }
            int h = ShareData.m_screenRealHeight;
            if(h > DEF_SIZE)
            {
                h = DEF_SIZE;
            }

            if (img instanceof RotationImg2[])
            {
                out = cn.poco.imagecore.Utils.DecodeFinalImage(context, ((RotationImg2[]) img)[0].m_img, ((RotationImg2[]) img)[0].m_degree, -1, ((RotationImg2[]) img)[0].m_flip, w, h);
            }
            else if (img instanceof RotationImg2)
            {
                out = cn.poco.imagecore.Utils.DecodeFinalImage(context, ((RotationImg2) img).m_img, ((RotationImg2) img).m_degree, -1, ((RotationImg2) img).m_flip, w, h);
            }
            else if (img instanceof ImageFile2)
            {
                RotationImg2[] rawImg = ((ImageFile2) img).GetRawImg();
                out = cn.poco.imagecore.Utils.DecodeFinalImage(context, rawImg[0].m_img, rawImg[0].m_degree, -1, rawImg[0].m_flip, w, h);
            }
            else if (img instanceof Bitmap)
            {
                out = (Bitmap) img;
            }
//            Log.d("mmm", "ProcessFilterBeautify decode Img: " + (System.currentTimeMillis() - start));


            if (out != null && !out.isRecycled())
            {

//                start = System.currentTimeMillis();

                boolean is_face_detected = true;
                boolean is_shape = info.m_shape;
                boolean is_beauty = info.m_beauty;
                boolean is_dark = info.m_dark;
                boolean is_blur = info.m_blur;
                boolean is_filter = info.m_filter;
                int filter_uri = info.m_filter_uri;
                int filter_alpha = info.m_filter_alpha;
                /*int shape_size = info.m_shape_size;*/
                if (info.mFilterBeautyParams == null)
                {
                    info.mFilterBeautyParams = new FilterBeautyParams();
                    info.mFilterBeautyParams.setDefault();
                }
                FilterBeautyParams params = info.mFilterBeautyParams;

                boolean isNeedFaceDetect = false;//是否需要检测人脸

                FilterRes filterRes = FilterResMgr2.getInstance().GetRes(filter_uri);
                // 参数不为0， 滤镜处理且避开人脸 虚化处理  三者条件满足其一，则需要做人脸检测操作
                if (filterRes != null && filterRes.m_isSkipFace)
                {
                    isNeedFaceDetect = true;
                }

                if (!isNeedFaceDetect && is_shape)//美形定制
                {
                    isNeedFaceDetect = true;
                }

                if (!isNeedFaceDetect && is_blur)
                {
                    isNeedFaceDetect = true;
                }

                is_face_detected = isNeedFaceDetect;


                //人脸数据检测
                if (is_face_detected)
                {
                    ProcessFaceDetected(context, out);
//                    Log.d("mmm", "ProcessFilterBeautify face detected Img: " + (System.currentTimeMillis() - start));
                }

                PocoFaceInfo[] faceInfos = null;
                if (FaceDataV2.CHECK_FACE_SUCCESS)
                {
                    faceInfos = FaceDataV2.RAW_POS_MULTI;
                }
                else
                {
                    faceInfos = null;
                }

                //美形
                if (is_shape)
                {
//                    start = System.currentTimeMillis();
                    out = ProcessShape(context, out, params, faceInfos);
//                    Log.d("mmm", "ProcessFilterBeautify shape Img: " + (System.currentTimeMillis() - start));
                }

                //美颜
                if (is_beauty)
                {
//                    start = System.currentTimeMillis();
                    //out = ProcessBeauty(context, out, params[TailorMadeType.MOPI]);
                    out = ProcessSkinBeauty(context, out, params.getSkinTypeSize());
                    out = ProcessSmoothBeauty(context, faceInfos, out, params.getSkinBeautySize());
//                    out = ProcessBeautyAdjust(context, faceInfos, out, params[TailorMadeType.MOPI]);
//                    Log.d("mmm", "ProcessFilterBeautify beauty " + params[BeautyShapeType.BEAUTY] + "% Img: " + (System.currentTimeMillis() - start));
                }


                if (is_filter)
                {
                    Bitmap tempFilterBmp = null;
                    if (out != null && !out.isRecycled())
                    {
                        tempFilterBmp = out.copy(Bitmap.Config.ARGB_8888, true);
                    }

                    //滤镜
//                    start = System.currentTimeMillis();
                    //先处理滤镜
                    tempFilterBmp = ProcessFilter(context, tempFilterBmp, filterRes, faceInfos);
//                    Log.d("mmm", "ProcessFilterBeautify filter Img: " + (System.currentTimeMillis() - start));


                    //透明度
//                start = System.currentTimeMillis();
                    out = ProcessFilterAlpha(context, out, tempFilterBmp, filter_alpha);
//                Log.d("mmm", "ProcessFilterBeautify alpha " + filter_alpha + "% Img: " + (System.currentTimeMillis() - start));
                }

                //虚化暗角
//                start = System.currentTimeMillis();
                out = ProcessBlurDark(context, out, is_blur, is_dark, faceInfos);
//                Log.d("mmm", "ProcessFilterBeautify dark Img: " + (System.currentTimeMillis() - start));
            }
        }

        ClearFaceDetected();

        return out;
    }

    /**
     * 处理顺序 美牙 -> 脸型
     * @param context
     * @param dest
     * @param params
     * @param faceInfos
     * @return
     */
    public static Bitmap ProcessShape(Context context, Bitmap dest,
                                      FilterBeautyParams params,
                                      PocoFaceInfo[] faceInfos/*, int faceSize*/)
    {
        Bitmap out = dest;

        if (out != null && !out.isRecycled())
        {

            if (faceInfos != null && faceInfos.length >= 1)
            {

                // if (faceSize <= 0)
                // {
                //     faceSize = 1;//只做一个人脸处理
                // }
                // else
                // {
                //     faceSize = Math.min(faceInfos.length, faceSize);
                // }

                //NOTE 人脸个数由底层决定
                int faceSize = faceInfos.length;

                for (int i = 0; i < faceSize; i++)
                {
                    //美牙处理
                    out = PocoCameraEffect.TeethWhiten(out, context, faceInfos[i], (int) params.getWhitenTeethSize());
                }

                //脸型处理
                Bitmap faceMaskBitmap = decodeFaceMaskBitmap(context);
                out = CrazyShapeFilter.SuperCrazyShape_Multi(out, faceMaskBitmap, faceInfos, params.shapeData);
                if (faceMaskBitmap != null && !faceMaskBitmap.isRecycled() && faceMaskBitmap != out) {
                    faceMaskBitmap.recycle();
                }
                faceMaskBitmap = null;
            }
        }
        return out;
    }

    private static final Bitmap decodeFaceMaskBitmap(Context context) {
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


    public static boolean ProcessFaceDetected(Context context, Bitmap dest)
    {
        boolean success = false;
        if (!FaceDataV2.CHECK_FACE_SUCCESS)
        {
            if (dest != null && !dest.isRecycled())
            {
                FaceDataV2.CheckFace(context, dest);//人脸检测
            }
        }

        if (FaceDataV2.CHECK_FACE_SUCCESS)
        {
            success = true;
        }
        else
        {
            FaceDataV2.ResetData();
        }

        return success;
    }

    /**
     * 美肤（肤色）处理
     *
     * @param context
     * @param dest
     * @param skinBeautySize 肤色值
     * @return
     */
    public static Bitmap ProcessSkinBeauty(Context context, Bitmap dest, float skinBeautySize)
    {
        Bitmap out = dest;
        if (dest != null && !dest.isRecycled())
        {
            try
            {
                byte[] handlerByte = FileUtil.getAssetsByte(context, "skinStyle/level_table_1");
                out = PocoBeautyFilter.CameraSkinBeauty(dest, handlerByte, (int) skinBeautySize);
            }
            catch (Throwable t)
            {
            }
        }
        return out;
    }

    /**
     * 新版美颜磨皮
     *
     * @param context
     * @param pocoFaceInfos
     * @param dest
     * @param beautySize
     * @return
     */
    public static Bitmap ProcessSmoothBeauty(Context context, PocoFaceInfo[] pocoFaceInfos, Bitmap dest, float beautySize)
    {
        Bitmap out = dest;
        if (dest != null && !dest.isRecycled())
        {
            try
            {
                out = PocoBeautyFilter.CameraSmoothBeauty(dest, pocoFaceInfos, Math.round(BeautyShapeDataUtils.GetRealSkinBeautySize((int) beautySize)), false);
            }
            catch (Throwable t)
            {
            }
        }
        return out;
    }

    /**
     * 美颜 使用新版4.1.3接口{@link #ProcessSmoothBeauty(Context, PocoFaceInfo[], Bitmap, float)}
     *
     * @param context
     * @param pocoFaceInfos
     * @param dest
     * @param beautySize
     * @return
     * @deprecated
     */
    public static Bitmap ProcessBeautyAdjust(Context context, PocoFaceInfo[] pocoFaceInfos, Bitmap dest, int beautySize)
    {
        Bitmap out = null;

        if (dest != null && !dest.isRecycled())
        {
            out = PocoCameraEffect.realtimeBeautyMicroAdjust(dest, pocoFaceInfos, beautySize);
        }

        return out;
    }


    public static Bitmap ProcessFilter(Context context, Bitmap dest, int filterUri, PocoFaceInfo[] faceInfos)
    {
        if (filterUri > 0)
        {
            FilterRes res = FilterResMgr2.getInstance().GetRes(filterUri);
            if (res != null) ProcessFilter(context, dest, res, faceInfos);
        }
        return dest;
    }

    //滤镜处理
    public static Bitmap ProcessFilter(Context context, Bitmap dest, FilterRes filterRes, PocoFaceInfo[] faceInfos)
    {
        Bitmap out = dest;

        if (out != null && !out.isRecycled() && context != null && filterRes != null)
        {

            FilterRes.FilterData[] datas = filterRes.m_datas;
            if (datas != null && datas.length > 0)
            {
                Bitmap colorBmp = null;

                Bitmap[] mask = null;
                int[] comop = null;
                int[] opacity = null;
                boolean isHollow = filterRes.m_isSkipFace;//是否避开人脸，默认取颜色查表资源的值

                int size = datas.length;

                if (size > 1)
                {
                    int maskSize = size - 1;//第1个下标开始为mask
                    mask = new Bitmap[maskSize];
                    comop = new int[maskSize];
                    opacity = new int[maskSize];
                }

                for (int i = 0; i < size; i++)
                {
                    FilterRes.FilterData data = datas[i];
                    if (data == null || data.m_res == null || data.m_params == null) break;

                    if (i == 0)
                    {   //颜色查表
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

                out = filterori.loadFilterV2_rs(context, out, colorBmp, mask, comop, opacity, faceInfos, isHollow);


                if (colorBmp != null)
                {
                    RecycleBmp(colorBmp);
                }

                if (mask != null)
                {
                    for (Bitmap bitmap : mask)
                    {
                        RecycleBmp(bitmap);
                    }
                }
            }
        }

        return out;
    }


    //虚化&暗角
    public static Bitmap ProcessBlurDark(Context context, Bitmap dest, boolean isBlur, boolean isDark, PocoFaceInfo[] faceInfo)
    {
        Bitmap out = dest;

        if (out != null && !out.isRecycled() && context != null)
        {
            if (isBlur)
            {
                filter.circleBlur_v2(out, context, faceInfo);
            }

            if (isDark)
            {
                filter.darkCorner_v2(out, context);
            }
        }

        return out;
    }


    /**
     * @param context
     * @param dest    原图(会修改,也就是返回的图片)
     * @param mask    效果图(不会修改)
     * @param alpha
     * @return
     */
    public static Bitmap ProcessFilterAlpha(Context context, Bitmap dest, Bitmap mask, int alpha)
    {
        Bitmap out = dest;

        if (dest != null && !dest.isRecycled() && mask != null && !mask.isRecycled() && alpha >= 0 && alpha <= 100)
        {
            if (alpha == 0)
            {
                out = dest;
            }
            else if (alpha == 100)
            {
                out = mask;
            }
            else
            {
                out = ImageProcessor.DrawMask2(dest, mask, alpha);
            }
        }
        return out;
    }

    public static void RecycleBmp(Bitmap bmp)
    {
        if (bmp != null && !bmp.isRecycled())
        {
            bmp.recycle();
        }
        bmp = null;
    }


    public static void ClearFaceDetected()
    {
        FaceDataV2.ResetData();
    }
}
