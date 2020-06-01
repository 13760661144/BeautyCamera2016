package cn.poco.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ThumbnailUtils;

import cn.poco.imagecore.ImageUtils;
import cn.poco.tianutils.MakeBmp;

import static cn.poco.imagecore.ImageUtils.JpgEncode;

public class CameraUtils {

    //静音拍照
    public static boolean sHasSetVolume = false;
    public static int old_volume1;
    private static int old_volume2;

    /**
     * @param silence 是否静音
     */
    public static void setSystemVolume(Context context, boolean silence) {
        if (silence) {
            if (sHasSetVolume) {
                return;
            }
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            old_volume1 = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            old_volume2 = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, old_volume1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, old_volume2, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            sHasSetVolume = true;

        } else {
            if (!sHasSetVolume) {
                return;
            }
            // 声音
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (old_volume1 != 0) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, old_volume1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            if (old_volume2 != 0) {
                am.setStreamVolume(AudioManager.STREAM_SYSTEM, old_volume2, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            sHasSetVolume = false;
        }
    }

    /**
     * 旋转、裁剪图片
     *
     * @param data
     * @param hMirror     水平镜像
     * @param degree
     * @param ratio
     * @param topScale
     * @param maxSize     最大不能超过maxSize  -1:不限制
     * @param isThumbnail
     * @return
     */
    public static Bitmap rotateAndCropPicture(byte[] data, boolean hMirror, int degree, float ratio, float topScale, int maxSize, boolean isThumbnail) {
        if (data == null) {
            return null;
        }
        float maxMem = 0.25f;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        opt.inJustDecodeBounds = false;
        int bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;

        float srcRatio = opt.outHeight * 1.0f / opt.outWidth;
        if (srcRatio < 1) {
            srcRatio = 1 / srcRatio;
        }
        if (ratio > srcRatio) {
            bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
            if (maxSize == -1) {
                maxSize = bigOne;
            }
            int sampleSize = bigOne / maxSize;
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            int cw = opt.outWidth / sampleSize;
            int ch = opt.outHeight / sampleSize;
            int memUse = cw * ch * 4;
            if (memUse > Runtime.getRuntime().maxMemory() * maxMem) {
                bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
            }
        }
        if (maxSize == -1) {
            maxSize = bigOne;
        }
        if (bigOne > maxSize) {
            opt.inSampleSize = bigOne / maxSize;
        }
        if (opt.inSampleSize < 1) {
            opt.inSampleSize = 1;
        }
        if (isThumbnail) {
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inSampleSize = 8;
        } else {
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }
        Bitmap bitmap = ImageUtils.DecodeJpg(data, opt.inSampleSize);
        if (bitmap == null || bitmap.isRecycled())
            return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if (hMirror) {
            matrix.postScale(-1, 1);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap == null || bitmap.isRecycled())
            return null;
        //裁剪
        srcRatio = 1.0f;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = 0, y = 0;

        if (height > width) {
            srcRatio = height * 1.0f / width;
            x = 0;
            y = (int) (height * topScale);
            height = (int) (width * ratio);

        } else if (height < width) {
            srcRatio = width * 1.0f / height;
            x = (int) (width * topScale);
            y = 0;
            width = (int) (height * ratio);
        }
        Bitmap target = null;
        if (srcRatio == ratio) {
            target = bitmap;
        } else {
            if (x > bitmap.getWidth()) {
                x = 0;
            }
            if (y > bitmap.getHeight()) {
                y = 0;
            }
            if (x + width > bitmap.getWidth()) {
                width = bitmap.getWidth() - x;
            }
            if (y + height > bitmap.getHeight()) {
                height = bitmap.getHeight() - y;
            }
            target = Bitmap.createBitmap(bitmap, x, y, width, height);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        if (isThumbnail && target != null && !target.isRecycled()) {
            target = ThumbnailUtils.extractThumbnail(target, 96, 96, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return target;
    }

    public static Bitmap rotateAndCropPicture(byte[] data, boolean hMirror, int degree, float ratio, float topScale, int maxSize) {
        return rotateAndCropPicture(data, hMirror, degree, ratio, topScale, maxSize, false);
    }

    public static byte[] rotateAndCropPicture2byteArr(byte[] data, boolean hMirror, int degree, float ratio, float topScale, int maxSize, int quality) {
        Bitmap target = rotateAndCropPicture(data, hMirror, degree, ratio, topScale, maxSize);
        if (target == null || target.isRecycled())
            return null;
        return JpgEncode(target, quality);
    }

    public static byte[] cutBitmap(final byte[] data, final float ratio, int rotateDegree, boolean reverse, int photoSize) {
        Bitmap bitmap = cutBitmap2(data, ratio, rotateDegree, reverse, photoSize);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmapToByteArray(bitmap, true);
        }
        return null;
    }

    public static Bitmap cutBitmap2(final byte[] data, final float ratio, int rotateDegree, boolean reverse, int photoSize) {
        Bitmap bitmap = decodeFileByRatio(data, ratio, reverse, photoSize, 0.25f);
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float targetRatio = ratio;
            if (w < h) {
                targetRatio = 1 / ratio;
            }
            Bitmap temp_bitmap = createBitmap(bitmap, targetRatio, rotateDegree, photoSize);
            bitmap.recycle();
            bitmap = temp_bitmap;
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
        return null;
    }


    public static Bitmap decodeFileByRatio(byte[] image, double ratio, boolean reverse, int size, float maxMem) {
        if (ratio > 1) {
            ratio = 1 / ratio;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeByteArray(image, 0, image.length, opt);
        int ref = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
        double srcRatio = (double) opt.outWidth / (double) opt.outHeight;

        if (srcRatio > 1) {
            srcRatio = 1 / srcRatio;
        }
        if (ratio > srcRatio) {
            ref = opt.outWidth > opt.outHeight ? opt.outHeight : opt.outWidth;
            int sampleSize = ref / size;
            if (sampleSize == 0) {
                sampleSize = 1;
            }
            int cw = opt.outWidth / sampleSize;
            int ch = opt.outHeight / sampleSize;
            int memUse = cw * ch * 4;
            if (memUse > Runtime.getRuntime().maxMemory() * maxMem) {
                ref = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
            }
        }
        if (ref > size) {
            opt.inSampleSize = ref / size;
        }
        opt.inJustDecodeBounds = false;
        if (opt.inSampleSize < 1) {
            opt.inSampleSize = 1;
        }

        Bitmap bitmap = ImageUtils.DecodeJpg(image, opt.inSampleSize);
        if (reverse && bitmap != null) {
            //左右翻转
            Matrix matrix = new Matrix();
            matrix.postScale(1, -1);
            Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (temp != null) {
                bitmap.recycle();
                bitmap = temp;
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap bitmap, float ratio, int rotate, int size) {
        int x = 0, y = 0, w = 0, h = 0;
        int bmpW = bitmap.getWidth();
        int bmpH = bitmap.getHeight();
        float srcRatio = (float) bmpW / (float) bmpH;
        if (srcRatio < ratio) {
            w = bmpW;
            h = (int) (bmpW / ratio);
        } else {
            h = bmpH;
            w = (int) (bmpH * ratio);
        }
        x = (bmpW - w) / 2;
        y = (bmpH - h) / 2;
        int bigOne = w > h ? w : h;
        float scale = (float) size / (float) bigOne;
        if (scale > 1) {
            scale = 1;
        }
        Matrix mtx = new Matrix();
        mtx.postScale(scale, scale);
        mtx.postRotate(rotate, w / 2, h / 2);
        int t;
        float scale_w_h;
        switch (rotate) {
            case 0:
                break;
            case 90:
                t = w;
                w = h;
                h = t;
                break;
            case 180:
                break;
            case 270:
                t = w;
                w = h;
                h = t;
                break;
        }
        scale_w_h = (float) w / (float) h;
        Bitmap bmp = MakeBmp.CreateBitmap(bitmap, size, size, scale_w_h, rotate, Bitmap.Config.ARGB_8888);
        return bmp;
    }

    public static byte[] bitmapToByteArray(final Bitmap bitmap, final boolean needRecycle) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        byte[] out = JpgEncode(bitmap, 100);
        if (needRecycle) {
            bitmap.recycle();
        }
        return out;
    }
}
