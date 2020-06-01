package cn.poco.beautifyEyes.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ProcessQueue;

/**
 * Created by Shine on 2016/12/5.
 */

public class BeautifyEyesHandler extends Handler{
    public static final int MSG_INIT = 0x00000001;
    public static final int MSG_CYC_QUEUE = 0x00000010; // 处理消息队列
    public static final int MSG_UPDATE_AFTER_PINPOINT = 0x00000020;
    public static final int MSG_UPDATE_UI = 0x00000030; //更新界面
    public static final int MSG_ADJUST_EFFECT_UI = 0x00000040; //修正效果

    protected Context mContext;
    protected Handler mUIHandler;

    public ProcessQueue mQueue;
    public Bitmap mShapeBmp;
    private boolean mIsInitial;

    public BeautifyEyesHandler(Looper looper, Context context, Handler ui) {
        super(looper);
        mContext = context;
        mUIHandler = ui;
        mQueue = new ProcessQueue();
    }

    @Override
    public void handleMessage(Message msg) {
        Message uiMsg;
        Bitmap tempBmp;
        switch (msg.what) {
            case MSG_INIT: {
                InitMsg params = (InitMsg) msg.obj;
                msg.obj = null;
                //图片在主线程已解析
                if (params != null && params.mDisplayBitmap != null) {
                tempBmp = params.mDisplayBitmap;

                // 当其它页面没有进行人脸检测时，应先进行人脸检测
                Boolean faceDetectResult;
                if (FaceDataV2.RAW_POS_MULTI == null) {
                    // 记录下人脸检测的结果
                    faceDetectResult = FaceDataV2.CheckFace(mContext, tempBmp);
                } else{
                    // 人脸检测结果
                    faceDetectResult = FaceDataV2.CHECK_FACE_SUCCESS ? true : false;
                }

                // 人脸检测失败,跳去定点页面
                if (!faceDetectResult) {
                    params.mShouldJumpToDetectFace = true;
                } else {
                    // 否则，初始化页面布局
                    mIsInitial = true;
                    if (FaceDataV2.sFaceIndex == -1) {
                        params.mShowMultifacedetect = true;
                    } else {
                        params.mShowMultifacedetect = false;
                        if (FaceDataV2.RAW_POS_MULTI.length > 1) {
                            params.mShowChangeface = true;
                        }
                    }
                }
                mShapeBmp = tempBmp.copy(Bitmap.Config.ARGB_8888, true);
                }
                uiMsg = mUIHandler.obtainMessage();
                uiMsg.obj = params;
                uiMsg.what = MSG_INIT;
                mUIHandler.sendMessage(uiMsg);
                break;
            }

            case MSG_CYC_QUEUE: {
                CmdMsg item = (CmdMsg) mQueue.GetItem();
                if (item != null) {
                    //大眼,亮眼或者祛眼袋
                    Bitmap temp;
                    if (mShapeBmp != null) {
                        temp = mShapeBmp.copy(Bitmap.Config.ARGB_8888, true);
                        DoFaceFeature(temp, item.m_faceLocalData);
                        item.mDisplayBitmap = temp;
                    }
                }
                uiMsg = mUIHandler.obtainMessage();
                uiMsg.obj = item;
                uiMsg.what = MSG_UPDATE_UI;
                mUIHandler.sendMessage(uiMsg);
                break;
            }

            case MSG_UPDATE_AFTER_PINPOINT : {
                PinPointMsg params = (PinPointMsg) msg.obj;
                msg.obj = null;
                if (params != null) {
                    if (!mIsInitial) {
                        if (FaceDataV2.sFaceIndex == -1) {
                            params.mInitMsg.mShowMultifacedetect = true;
                        } else {
                            params.mInitMsg.mShowMultifacedetect = false;
                            if (FaceDataV2.RAW_POS_MULTI.length > 1) {
                                params.mInitMsg.mShowChangeface = true;
                            }
                        }
                    }

                    if (FaceDataV2.CHECK_FACE_SUCCESS || !mIsInitial) {
                        if (mShapeBmp != null && params.mCmdMsg != null) {
                            Bitmap temp = mShapeBmp.copy(Bitmap.Config.ARGB_8888, true);
                            DoFaceFeature(temp, params.mCmdMsg.m_faceLocalData);
                            params.mCmdMsg.mDisplayBitmap = temp.copy(Bitmap.Config.ARGB_8888, true);
                        }
                    }
                }

                mIsInitial = true;
                uiMsg = mUIHandler.obtainMessage();
                uiMsg.obj = params;
                uiMsg.what = MSG_ADJUST_EFFECT_UI;
                mUIHandler.sendMessage(uiMsg);
                break;
            }
        }
    }


    public static Bitmap DoFaceFeature(Bitmap bmp, FaceLocalData faceLocalData)
    {
        Bitmap out = bmp;

        if(out != null && faceLocalData != null && faceLocalData.m_faceNum > 0) {
            for(int i = 0; i < faceLocalData.m_faceNum; i++) {
                if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 0) {
                    //亮眼
                    if(faceLocalData.m_brightEyeLevel_multi[i] > 0)
                    {
                        out = PocoBeautyFilter.brightEye(out, FaceDataV2.RAW_POS_MULTI[i], faceLocalData.m_brightEyeLevel_multi[i]);
                    }
                    //大眼
                    if(faceLocalData.m_bigEyeLevel_multi[i] > 0)
                    {
                        out = PocoBeautyFilter.bigeye(out, FaceDataV2.RAW_POS_MULTI[i], faceLocalData.m_bigEyeLevel_multi[i]);
                    }
                    //祛眼袋
                    if (faceLocalData.m_eyeBagsLevel_multi[i] > 0) {
                        out = PocoBeautyFilter.remove_circle(out, FaceDataV2.RAW_POS_MULTI[i], faceLocalData.m_eyeBagsLevel_multi[i]);
                    }

                }
            }
        }
        return out;
    }


    /**
     * 原图保存在params.mDisplayBitmap
     *
     * @param context
     * @param params
     */
    public static void initParams(Context context, InitMsg params)
    {
        Bitmap temp = null;
        int rotation;
        int flip = MakeBmpV2.FLIP_NONE; //顺序(旋转-翻转)
        float scale = ImageUtils.GetImgScale(context, params.m_imgs, params.m_layoutMode);
        if(params.m_imgs instanceof ImageFile2)
        {
            RotationImg2[] img = ((ImageFile2)params.m_imgs).GetRawImg();
            Object data = img[0].m_img;
            rotation = img[0].m_degree;
            flip = img[0].m_flip;
            //temp = Utils.DecodeShowImage((Activity)context, data, rotation, scale, flip);
            temp = cn.poco.imagecore.Utils.DecodeFinalImage(context, data, rotation, -1, flip, params.m_w, params.m_h);
            if(temp == null)
            {
                RuntimeException ex = new RuntimeException("MyLog--Image data does not exist!");
                throw ex;
            }
        }
        else if(params.m_imgs instanceof RotationImg2[])
        {
            rotation = ((RotationImg2[])params.m_imgs)[0].m_degree;
            flip = ((RotationImg2[])params.m_imgs)[0].m_flip;
            //temp = Utils.DecodeShowImage((Activity)context, ((RotationImg2[])params.m_imgs)[0].m_img, rotation, scale, flip);
            temp = cn.poco.imagecore.Utils.DecodeFinalImage(context, ((RotationImg2[])params.m_imgs)[0].m_img, rotation, -1, flip, params.m_w, params.m_h);
            if(temp == null)
            {
                RuntimeException ex = new RuntimeException("MyLog--Image does not exist! path:" + ((RotationImg2[])params.m_imgs)[0].m_img);
                throw ex;
            }
        } else if (params.m_imgs instanceof RotationImg2) {
            rotation = ((RotationImg2)params.m_imgs).m_degree;
            flip = ((RotationImg2)params.m_imgs).m_flip;
            //temp = Utils.DecodeShowImage((Activity)context, ((RotationImg2)params.m_imgs).m_img, rotation, scale, flip);
            temp = cn.poco.imagecore.Utils.DecodeFinalImage(context, ((RotationImg2)params.m_imgs).m_img, rotation, -1, flip, params.m_w, params.m_h);
            if(temp == null)
            {
                RuntimeException ex = new RuntimeException("MyLog--Image does not exist! path:" + ((RotationImg2[])params.m_imgs)[0].m_img);
                throw ex;
            }
        } else {
            RuntimeException ex = new RuntimeException("MyLog--Image type error!");
            throw ex;
        }
        params.mDisplayBitmap = temp;
    }

    public static class UiBaseMsg {
        // 是否弹出检测到多人的提醒
        public boolean mShowMultifacedetect;
        // 是否显示更换脸的icon
        public boolean mShowChangeface;
    }

    public static class InitMsg extends UiBaseMsg{
        public Object m_imgs;//可修改为object
        public int m_layoutMode;
        public int m_w;
        public int m_h;
        public Bitmap mDisplayBitmap;

        public boolean mShouldJumpToDetectFace; // 先检查这个人脸检测的结果
    }

    public static class PinPointMsg{
        public InitMsg mInitMsg;
        public CmdMsg mCmdMsg;
    }

    public static class CmdMsg {
        public FaceLocalData m_faceLocalData;
        public Bitmap mDisplayBitmap;
    }

    public void clearData() {
        if (mShapeBmp != null && !mShapeBmp.isRecycled()) {
            mShapeBmp.recycle();
            mShapeBmp = null;
        }

        if (mQueue != null) {
            mQueue.ClearAll();
            mQueue = null;
        }
    }



}

