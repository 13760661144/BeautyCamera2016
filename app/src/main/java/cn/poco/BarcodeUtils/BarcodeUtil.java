package cn.poco.BarcodeUtils;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class BarcodeUtil {
	/**
	 * 生成普通自定义大小二维码的方法
	 * @param str 二维码内容
	 * @param qrcode_size 二维码的边长
	 * @return 返回二维码图像的bitmap
	 */
	public static Bitmap createQRCode(String str, int qrcode_size, boolean hasBorder){
		Bitmap encodeBitmap = null;
		try {
			encodeBitmap = EncodingHandler.createQRCode(str, qrcode_size,hasBorder);
			return encodeBitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return encodeBitmap;
	}

	public static Bitmap createLELQRCode(String str, int qrcode_size, boolean hasBorder){
		Bitmap encodeBitmap = null;
		try {
			encodeBitmap = EncodingHandler.createQRCode(str, ErrorCorrectionLevel.L, qrcode_size, hasBorder);
			return encodeBitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return encodeBitmap;
	}

	/**
	 * 生成普通默认大小二维码的方法
	 * @param str  二维码内容
	 * @return  返回二维码图像的bitmap
	 */
	public static Bitmap createQRCode(String str){
			Bitmap encodeBitmap = null;
			try {
				encodeBitmap = EncodingHandler.createQRCode(str);
				return encodeBitmap;
			} catch (WriterException e) {
				e.printStackTrace();
			}
			return encodeBitmap;
		}
	
	/**
	 * 生成自定义大小图片二维码的方法
	 * @param str 二维码内容
	 * @param qrcode_size 二维码的边长
	 * @param logoBitmap 参数为中间小图片bitmap
	 * @return 返回二维码图像的bitmap
	 */
	public static Bitmap createLogoQRCode(String str, int qrcode_size, Bitmap logoBitmap){
		Bitmap encodeBitmap = null;
		try {
			encodeBitmap = EncodingHandler.createQRCodeWithLogo(str, qrcode_size, logoBitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return encodeBitmap;
	}

	/**
	 * 生成自定义大小图片二维码的方法
	 * @param str 二维码内容
	 * @param qrcode_size 二维码的边长
	 * @param logoBitmap 参数为中间小图片bitmap
	 * @param logoSzie 中间图片的大小
	 * @return 返回二维码图像的bitmap
	 */
	public static Bitmap createLogoQRCode(String str, int qrcode_size, Bitmap logoBitmap, int logoSzie){
		Bitmap encodeBitmap = null;
		try {
			encodeBitmap = EncodingHandler.createQRCodeWithLogo(str, qrcode_size, logoBitmap, logoSzie);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return encodeBitmap;
	}

	
	/**
	 * 生成默认大小图片二维码的方法
	 * @param str 二维码内容
	 * @param logoBitmap 参数为中间小图片bitmap
	 * @return 返回二维码图像的bitmap
	 */
	public static Bitmap createLogoQRCode(String str, Bitmap logoBitmap){
		Bitmap encodeBitmap = null;
		try {
			encodeBitmap = EncodingHandler.createQRCodeWithLogo(str,logoBitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return encodeBitmap;
	}
}
