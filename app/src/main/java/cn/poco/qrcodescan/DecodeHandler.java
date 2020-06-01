package cn.poco.qrcodescan;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.circle.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Vector;

public class DecodeHandler {
	private boolean mDecoding = false;
	private boolean mDecodeFinish = false;
	private boolean mIsDark = false;
	private byte[] mYuvData;
	private int mWidth;
	private int mHeight;
	private Rect mRect;
	private MultiFormatReader mMultiFormatReader;
	private OnDecodeCompleteListener mOnDecodeCompleteListener;
	private String mImgPath;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private boolean mImgDecoding = false;
	private boolean mImgDecodeFinish = false;
	
	
	public interface OnDecodeCompleteListener
	{
		void onComplete(Result result);
	}
	
	public DecodeHandler(String characterSet)
	{
		Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
	    decodeFormats.add(BarcodeFormat.UPC_A);
	    decodeFormats.add(BarcodeFormat.UPC_E);
	    decodeFormats.add(BarcodeFormat.EAN_13);
	    decodeFormats.add(BarcodeFormat.EAN_8);
	    decodeFormats.add(BarcodeFormat.RSS_14);
	    
	    decodeFormats.add(BarcodeFormat.CODE_39);
	    decodeFormats.add(BarcodeFormat.CODE_93);
	    decodeFormats.add(BarcodeFormat.CODE_128);
	    decodeFormats.add(BarcodeFormat.ITF);
	    
	    decodeFormats.add(BarcodeFormat.QR_CODE);
	    decodeFormats.add(BarcodeFormat.DATA_MATRIX);
	    
	    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(3);
	    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

	    if(characterSet != null) {
	      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
	    }
		mMultiFormatReader = new MultiFormatReader();
		mMultiFormatReader.setHints(hints);
	}
	
	public void decodeYuv(byte[] data, int width, int height, Rect area)
	{
		if(mDecoding == false && mDecodeFinish == false)
		{
			mDecoding = true;
			
			mYuvData = null;
			mYuvData = new byte[data.length];
			System.arraycopy(data, 0, mYuvData, 0, data.length);
			mWidth = width;
			mHeight = height;
			mRect = area;
			
			new Thread(mDecodingRunnable).start();
		}
	}
	
	public void decodeImg(String path)
	{
		if(mImgDecoding == false && mImgDecodeFinish == false)
		{
			mImgDecoding = true;
			mImgPath = path;
			new Thread(mDecodingImgRunnable).start();
		}
	}

	public boolean isDark()
	{
		return mIsDark;
	}
	
	private Result scanningImage() {
        if (TextUtils.isEmpty(mImgPath)) {
            return null;  
        }
		Result result = null;
        Bitmap scanBitmap = Utils.decodeFile(mImgPath, 1024);
		if(scanBitmap != null) {
			BitmapLuminanceSource source = new BitmapLuminanceSource(scanBitmap);
			BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
			try {
				mHandler.postDelayed(mDecodingImgTimeOutRunnable, 3000);
				result = mMultiFormatReader.decodeWithState(bitmap1);
				mHandler.removeCallbacks(mDecodingImgTimeOutRunnable);
			} catch(ReaderException re) {
			} finally {
				mMultiFormatReader.reset();
			}
		}
		return result;
    }  
	
	private Runnable mDecodingImgTimeOutRunnable = new Runnable()
	{
		@Override
		public void run() {
			if(mImgDecoding == true)
			{
				mMultiFormatReader.reset();
			}
			Result result = new Result("yue_err_404", null, null, null);
			mOnDecodeCompleteListener.onComplete(result);
		}
	};
	
	
	private Runnable mDecodingImgRunnable = new Runnable()
	{
		@Override
		public void run() {
			final Result result = scanningImage();
			if(result != null)
			{
				mImgDecodeFinish = true;
				if(mOnDecodeCompleteListener != null)
				{
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable()
					{
						@Override
						public void run() {
							mOnDecodeCompleteListener.onComplete(result);
						}
					});
				}
			}
			mImgDecoding = false;
		}
	};
	
	public void setOnDecodeCompleteListener(OnDecodeCompleteListener l)
	{
		mOnDecodeCompleteListener = l;
	}
	
	private Runnable mDecodingRunnable = new Runnable()
	{
		@Override
		public void run() {
			final Result result = decode();
			if(result != null)
			{
				mDecodeFinish = true;
				if(mOnDecodeCompleteListener != null)
				{
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable()
					{
						@Override
						public void run() {
							mOnDecodeCompleteListener.onComplete(result);
						}
					});
				}
			}
			mDecoding = false;
		}
	};

	private Result decode()
	{
		if(mYuvData != null)
		{
			byte[] data = mYuvData;
			
			byte[] rotatedData = new byte[data.length];
			boolean computered = false;
			Result result = null;
			for(int i = 120; i <= 200; i+=20)
			{
				int width = mWidth;
				int height = mHeight;
				int color = 0;
				long clrTotal = 0;
				if(computered == false) {
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							color = ((int)data[x + y * width])&0xff;
							clrTotal += color;
							rotatedData[x * height + height - y - 1] = (byte) ((color & 0xff) > i ? 255 : 0);
						}
					}
					computered = true;
					if(clrTotal/(width*height) < 80){
						mIsDark = true;
					}else{
						mIsDark = false;
					}
				}
				else {
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							rotatedData[x * height + height - y - 1] = (byte) (((int)data[x + y * width] & 0xff) > i ? 255 : 0);
						}
					}
				}

			    int tmp = width;
			    width = height;
			    height = tmp;
				
				PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(rotatedData, width, height, mRect.left, mRect.top, mRect.width(), mRect.height());
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				
				try {
					result = mMultiFormatReader.decodeWithState(bitmap);
				} catch (ReaderException re) {
				} finally {
					mMultiFormatReader.reset();
				}
				if(result != null)
				{
					break;
				}
			}
			return result;
		}
		return null;
	}
}
