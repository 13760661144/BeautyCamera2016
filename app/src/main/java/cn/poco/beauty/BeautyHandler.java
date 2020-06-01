package cn.poco.beauty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.beautify.EffectType;
import cn.poco.beautify.ImageProcessor;
import cn.poco.beauty.view.BeautyView;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;

import static cn.poco.beautify.EffectType.EFFECT_CLEAR;
import static cn.poco.beautify.EffectType.EFFECT_DEFAULT;
import static cn.poco.beautify.EffectType.EFFECT_NEWBEE;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class BeautyHandler extends Handler {

	public static final int MSG_INIT = 1;
	public static final int MSG_BEAUTY = 1 << 1;
	public static final int MSG_SAVE = 1 << 2;
	public static final int MSG_PUSH_THUMB = 1 << 3;
	public static final int MSG_UPDATE_UI = 1 << 4;

	private Context mContext;

	private Handler mUIHandler;

	private int[] mTypes;

	private byte[] handlerClearByte;

	private Bitmap mThumb;

	//是否已处理过净白缓存
	private boolean isHandlerEffectClearCache = false;

	public BeautyHandler(Looper looper, Context context, Handler UIHandler) {
		super(looper);

		mContext = context;
		mUIHandler = UIHandler;

		mTypes = new int[7];
		mTypes[0] = EffectType.EFFECT_DEFAULT;
		mTypes[1] = EffectType.EFFECT_NEWBEE;
		mTypes[2] = EffectType.EFFECT_LITTLE;
		mTypes[3] = EffectType.EFFECT_MIDDLE;
		mTypes[4] = EffectType.EFFECT_NATURE;
		mTypes[5] = EffectType.EFFECT_MOONLIGHT;
		mTypes[6] = EffectType.EFFECT_CLEAR;
		handlerClearByte = FileUtil.getAssetsByte(context, "skinStyle/level_table_1");
	}

	public void clear()
	{
		mTypes = null;
		mContext = null;
		mUIHandler = null;
		handlerClearByte = null;
		mThumb = null;
	}


	@Override
	public void handleMessage(Message msg) {

		BeautyMsg beautyMsg = (BeautyMsg)msg.obj;
		msg.obj = null;

		switch (msg.what) {
			case MSG_INIT:
				initThumb(beautyMsg.orgBitmap);

				if (!FaceDataV2.CHECK_FACE_SUCCESS) {
					//人脸检测
					FaceDataV2.CheckFace(mContext, beautyMsg.orgBitmap, false);
					//初始化人脸识别
					if (FaceDataV2.CHECK_FACE_SUCCESS) {
						FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
					}
				} else if (FaceLocalData.getInstance() == null){
					//初始化人脸识别
					FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
				}

				beautyMsg.outBitmap = handle(beautyMsg);
				if (beautyMsg.autoShrink && beautyMsg.type != EffectType.EFFECT_NONE) {
					beautyMsg.outBitmap = PocoBeautyFilter.AutoShrinkFace(beautyMsg.outBitmap, FaceDataV2.RAW_POS_MULTI);
				}
				beautyMsg.orgBitmap = null;
				sendUIMessage(MSG_INIT, beautyMsg);

				for (int type : mTypes) {
					beautyMsg = new BeautyMsg();
					beautyMsg.thumb = getBeautyThumb(mThumb, type, beautyMsg.colorAlpha);
					beautyMsg.type = type;
					sendUIMessage(MSG_PUSH_THUMB, beautyMsg);
				}

				break;
			case MSG_BEAUTY:
				beautyMsg.outBitmap = handle(beautyMsg);
				if (beautyMsg.autoShrink && beautyMsg.type != EffectType.EFFECT_NONE) {
					beautyMsg.outBitmap = PocoBeautyFilter.AutoShrinkFace(beautyMsg.outBitmap, FaceDataV2.RAW_POS_MULTI);
				}
				beautyMsg.orgBitmap = null;
				sendUIMessage(MSG_UPDATE_UI, beautyMsg);
				break;
			case MSG_SAVE:
				beautyMsg.outBitmap = beautyMsg.view.GetOutputBmp(beautyMsg.size);
				beautyMsg.view = null;
				sendUIMessage(MSG_SAVE, beautyMsg);
				break;
		}
	}

	private void initThumb(Bitmap orgBitmap) {
		int width = orgBitmap.getWidth();
		int height = orgBitmap.getHeight();

		int max = Math.max(width, height);

		int size = ShareData.PxToDpi_xhdpi(240);
		Matrix matrix = new Matrix();
		float scale = size * 1f / max;
		matrix.setScale(scale, scale);

		mThumb = MakeBmp.CreateFixBitmap(orgBitmap, ShareData.PxToDpi_xhdpi(146), ShareData.PxToDpi_xhdpi(146), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
	}

	private Bitmap getBeautyThumb(Bitmap org, int type, int alpha) {

		if (org == null || org.isRecycled()) return null;
		if (type == EFFECT_CLEAR) {
			//磨皮alpha为ui数值，UI显示值（0-100），换算底层真实数值为（10-100）
			alpha = Math.round(BeautyShapeDataUtils.GetRealSkinBeautySize(alpha));
			return ImageProcessor.whitening(org.copy(Bitmap.Config.ARGB_8888, true), FaceDataV2.RAW_POS_MULTI, handlerClearByte, alpha, false);
		}

		Bitmap mask, out = org;
		if (out == null || out.isRecycled()) return out;
		mask = ImageProcessor.ConversionImgColorNew(mContext, true, out.copy(Bitmap.Config.ARGB_8888, true), type);

		if (type != EffectType.EFFECT_NONE && alpha > 0 && alpha < 100) {
			if (type == EFFECT_NEWBEE) {
				out = ImageProcessor.DrawNewBeeMask3(out, mask, alpha, false);
			} else if (type == EFFECT_DEFAULT) {
				out = ImageProcessor.DrawDefaultMask4(out, mask, alpha, false);
			} else {
				out = ImageProcessor.DrawMask(true, out, mask, alpha);
			}
		} else {
			out = mask;
		}

		return out;
	}

	private void sendUIMessage(int what, Object obj) {
		if (mUIHandler != null)
		{
			Message message = mUIHandler.obtainMessage();
			message.what = what;
			message.obj = obj;
			mUIHandler.sendMessage(message);
		}
	}

	private Bitmap handle(BeautyMsg beautyMsg) {
		int type = beautyMsg.type;

		Bitmap temp = beautyMsg.orgBitmap;

		if (type == EffectType.EFFECT_CLEAR) {
			//磨皮alpha为ui数值，UI显示值（0-100），换算底层真实数值为（10-100）
			int alpha = Math.round(BeautyShapeDataUtils.GetRealSkinBeautySize((beautyMsg.colorAlpha)));
			Bitmap tmp = temp.copy(Bitmap.Config.ARGB_8888, true);
			if (!isHandlerEffectClearCache) {
				tmp = PocoBeautyFilter.CameraSkinBeauty(tmp, handlerClearByte, 75);
				isHandlerEffectClearCache = true;
			}
			return PocoBeautyFilter.CameraSmoothBeauty(tmp, FaceDataV2.RAW_POS_MULTI, alpha, true);
		}

		Bitmap mask;
		if (type == EffectType.EFFECT_USER) {
			mask = ImageProcessor.ConversionImgColorCache(mContext, true, temp.copy(Bitmap.Config.ARGB_8888, true), type, beautyMsg.whiteningValue, beautyMsg.buffingValue, beautyMsg.complexionValue);

		} else {
			mask = ImageProcessor.ConversionImgColorCache(mContext, true, temp.copy(Bitmap.Config.ARGB_8888, true), type);

		}

		if (type != EffectType.EFFECT_NONE &&
				beautyMsg.colorAlpha >= 0 && beautyMsg.colorAlpha <= 100) {

			if (type == EffectType.EFFECT_NEWBEE) {
				temp = ImageProcessor.DrawNewBeeMask3(temp, mask, beautyMsg.colorAlpha, true);
			} else if (type == EFFECT_DEFAULT) {
				temp = ImageProcessor.DrawDefaultMask4(temp, mask, beautyMsg.colorAlpha, true);
			} else {
				//注意修改的是mask,不能recycle
				temp = ImageProcessor.DrawMask(true, temp, mask, beautyMsg.colorAlpha);
			}
		} else {
			temp = mask;
		}

		return temp;
	}

	public static class BeautyMsg {

		BeautyView view;
		int size;

		int type;
		Bitmap orgBitmap;
		Bitmap outBitmap;
		int buffingValue; // 磨皮
		int whiteningValue; // 美白
		int complexionValue; // 肤色
		int colorAlpha;
		boolean autoShrink; // 美颜自动瘦脸

		Bitmap thumb;
	}
}
