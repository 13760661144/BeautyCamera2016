package cn.poco.gldraw2;

import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraThread;

/**
 * Created by zwq on 2017/07/24 11:53.<br/><br/>
 */

public class RenderHelper {

    public static int sContextHashCode = -1;
    public static int sCameraOpenCount;
    public static CameraThread sCameraThread;
    public static int sCurrentCameraId = -1;

    public static boolean sCameraIsChange;
    public static long sPreviewDataLength;
    public static int sCameraWidth = 1440;
    public static int sCameraHeight = 1080;
    public static int sCameraSizeType = -1;
    public static float sCameraSizeRatio;

    public static int sPreviewDegree = 90;
    public static int sPictureDegree = 90;
    public static int sScreenOrientation;
    public static int sCameraOrientation = 3;
    public static boolean sIsFront = true;
    public static float sCameraFocusRatio = 1.0f;

    public static int sMaxTrackers = 5;//人脸检测返回人脸数据个数

    public static RenderThread sRenderThread;
    public static int sSurfaceWidth = 1080;
    public static int sSurfaceHeight = 1440;
    public static boolean sSurfaceIsChange;

    public static void clearAll() {
        sCameraOpenCount = 0;
        sCameraThread = null;
        sCurrentCameraId = -1;

        sCameraIsChange = false;
        sPreviewDataLength = 0;
        sCameraWidth = 1440;
        sCameraHeight = 1080;
        sCameraSizeType = -1;

        sPreviewDegree = 90;
        sPictureDegree = 90;
        sScreenOrientation = 0;
        sCameraOrientation = 3;
        sIsFront = true;
        sCameraFocusRatio = 1.0f;

        sCameraThread = null;
        sSurfaceWidth = 1080;
        sSurfaceHeight = 1440;
        sSurfaceIsChange = false;
    }

    public static CameraHandler getCameraHandler() {
        if (sCameraThread == null) {
            return null;
        }
        return sCameraThread.getHandler();
    }

    public static RenderHandler getRenderHandler() {
        if (sRenderThread == null) {
            return null;
        }
        return sRenderThread.getHandler();
    }
}
