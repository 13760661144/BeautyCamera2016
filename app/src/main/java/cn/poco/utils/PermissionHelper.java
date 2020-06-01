package cn.poco.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * Created by zwq on 2017/09/05 10:03.<br/><br/>
 */

public class PermissionHelper {

    public static final int CAMERA_REQUEST_CODE = 1;

    /**
     * 检查判断使用新版或旧版权限查询
     *
     * @param context
     * @return true 新版运行时权限， false 旧版权限检查
     */
    public static boolean checkVersion(Context context) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int targetSdkVersion = 1;
        if (applicationInfo != null) {
            targetSdkVersion = applicationInfo.targetSdkVersion;
            applicationInfo = null;
        }
        if (targetSdkVersion < 23 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0
            return false;
        }
        return true;
    }

    /**
     * 权限请求回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //已有权限
            } else {
                //用户勾选了不再询问
                //提示用户收到打开权限
//                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
//                    //相机权限已被禁止
//                }
            }
        }
    }

    /**
     * 镜头权限
     *
     * @return
     */
    public static boolean queryCameraPermission(final Context context) {
//        if (checkVersion(context)) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                //第一次请求权限时，用户如果拒绝，
//                //下一次请求shouldShowRequestPermissionRationale()返回true
//                //向用户解释为什么需要这个权限
//                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
//                    new AlertDialog.Builder(context)
//                            .setMessage("申请相机权限")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //申请相机权限
//                                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//                                }
//                            })
//                            .show();
//                } else {
//                    //申请相机权限
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//                }
//            } else {
//                //已经有相机权限
//            }
//            return true;
//        }

        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Throwable e) {
            //e.printStackTrace();
            camera = null;
        }
        if (camera != null) {
            boolean hasPermission = true;

            if (Build.MODEL.toLowerCase().contains("vivo")) {
                Field field = null;
                try {
                    field = camera.getClass().getDeclaredField("mHasPermission");
                } catch (NoSuchFieldException e) {
                    //e.printStackTrace();
                } catch (Throwable t) {
                    //t.printStackTrace();
                }
                if (field != null) {
                    field.setAccessible(true);
                    try {
                        hasPermission = field.getBoolean(camera);
                    } catch (IllegalAccessException e) {
                        //e.printStackTrace();
                        hasPermission = false;
                    } catch (Throwable t) {
                        //t.printStackTrace();
                        hasPermission = false;
                    }
                }
            }
            try {
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (Throwable e) {
                //e.printStackTrace();
            }
            return hasPermission;
        }
        return false;
    }

    /**
     * 录音权限
     *
     * @return
     */
    public static boolean queryAudioRecordPermission() {
        int SIMPLE_RATE = 44100;
        int READ_SIZE = 2048;

        int min = AudioRecord.getMinBufferSize(SIMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (min < READ_SIZE) {
            min = READ_SIZE;
        }
        boolean hasPermission = true;
        AudioRecord recordInstance = null;
        try {
            recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, SIMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min);
            if (recordInstance != null) {
                recordInstance.startRecording();
                hasPermission = true;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            hasPermission = false;
        } catch (Exception e) {
            e.printStackTrace();
            hasPermission = false;
        }
        if (hasPermission) {
            if (recordInstance != null) {
                if (recordInstance.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    hasPermission = false;
                }
                if (hasPermission) {
                    byte[] tempBuffer = new byte[READ_SIZE];
                    int bufferRead = recordInstance.read(tempBuffer, 0, READ_SIZE);
                    if (bufferRead <= 0 /*AudioRecord.ERROR_INVALID_OPERATION*/) {
                        hasPermission = false;
                    }
                    tempBuffer = null;
                }
            } else {
                hasPermission = false;
            }
        }
        try {
            if (recordInstance != null) {
                recordInstance.release();
                recordInstance = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasPermission;
    }

}
