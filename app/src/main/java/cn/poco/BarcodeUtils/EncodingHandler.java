package cn.poco.BarcodeUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.circle.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 *
 */
public final class EncodingHandler {
    private static final int BLACK = 0xff000000;
    /**
     * 生成二维码图片默认大小
     */
    private static final int QRCODE_SIZE = 400;

    // 默认二维码大小的方法
    public static Bitmap createQRCode(String str) throws WriterException
	{
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    // 自定义二维码的方法
    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException
	{
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = matrix.get(x, y) ? BLACK : Color.TRANSPARENT;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    // 自定义二维码的方法
    public static Bitmap createQRCode(String str, int widthAndHeight, boolean hasBorder) throws WriterException
    {
        return createQRCode(str, ErrorCorrectionLevel.H, widthAndHeight, hasBorder);
    }

    public static Bitmap createQRCode(String str, ErrorCorrectionLevel correctionLevel, int widthAndHeight, boolean hasBorder) throws WriterException
	{
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, correctionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int[] pixels = new int[width * height];

        int minDrawX = width, minDrawY = height, maxDrawX = 0, maxDrawY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (x < minDrawX) {
                        minDrawX = x;
                    }
                    if (y < minDrawY) {
                        minDrawY = y;
                    }
                    if (x > maxDrawX) {
                        maxDrawX = x;
                    }
                    if (y > maxDrawY) {
                        maxDrawY = y;
                    }
                    pixels[y * width + x] = BLACK;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if (!hasBorder) {
            Bitmap resultMap = Bitmap.createBitmap(bitmap, minDrawX, minDrawY, maxDrawX - minDrawX, maxDrawY - minDrawY);
            Bitmap map = Utils.scaleBitmap(resultMap, widthAndHeight);
            resultMap.recycle();
            resultMap = null;
            return map;
        }
        return bitmap;
    }

    // 默认二维码大小的带小图片的方法
    public static Bitmap createQRCodeWithLogo(String str, Bitmap logoBitmap) throws WriterException
	{
        int portraitsize = QRCODE_SIZE / 6;
        Bitmap encodeBitmap = createQRCode(str, QRCODE_SIZE);
        createQRCodeBitmapWithLogo(encodeBitmap, logoBitmap, QRCODE_SIZE, portraitsize);
        return encodeBitmap;

    }

    // 自定义二维码大小的带小图片的方法
    public static Bitmap createQRCodeWithLogo(String str, int widthAndHeight, Bitmap logoBitmap) throws WriterException
	{
        int portraitsize = widthAndHeight / 6;
        Bitmap encodeBitmap = createQRCode(str, widthAndHeight, false);
        createQRCodeBitmapWithLogo(encodeBitmap, logoBitmap, widthAndHeight, portraitsize);
        return encodeBitmap;

    }

    public static Bitmap createQRCodeWithLogo(String str, int widthAndHeight, Bitmap logoBitmap, int logoSize) throws WriterException
	{
        Bitmap encodeBitmap = createQRCode(str, widthAndHeight, false);
        createQRCodeBitmapWithLogo(encodeBitmap, logoBitmap, widthAndHeight, logoSize);
        return encodeBitmap;

    }


    /**
     * 在二维码上绘制头像
     */
    private static void createQRCodeBitmapWithLogo(Bitmap qr, Bitmap logoBitmap, int widthAndHeight, int portraitsize) {

        // 对原有图片压缩显示大小
        Matrix mMatrix = new Matrix();
        float width = logoBitmap.getWidth();
        float height = logoBitmap.getHeight();
        mMatrix.setScale(portraitsize / width, portraitsize / height);
        Bitmap portrait = Bitmap.createBitmap(logoBitmap, 0, 0, (int) width, (int) height, mMatrix, true);

        // 头像图片的大小
        int portrait_W = portrait.getWidth();
        int portrait_H = portrait.getHeight();

        // 设置头像要显示的位置，即居中显示
        int left = (widthAndHeight - portrait_W) / 2;
        int top = (widthAndHeight - portrait_H) / 2;
        int right = left + portrait_W;
        int bottom = top + portrait_H;
        Rect rect1 = new Rect(left, top, right, bottom);

        // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
        Canvas canvas = new Canvas(qr);

        // 设置我们要绘制的范围大小，也就是头像的大小范围
        Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
        // 开始绘制
        canvas.drawBitmap(portrait, rect2, rect1, null);
    }

}
