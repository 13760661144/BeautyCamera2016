package cn.poco.slim;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import cn.poco.beautify.ImageProcessor;
import cn.poco.common.LineData;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;

/**
 * Created by: fwc
 * Date: 2016/12/24
 */
public class SlimHandler extends Handler {

	private Context mContext;
	private Handler mUIHandler;

	public static final int MSG_INIT = 1;
	public static final int MSG_SLIM = 1 << 1;
	public static final int MSG_UPDATE_UI = 1 << 2;

	public SlimHandler(Looper looper, Context context, Handler UIHandler) {
		super(looper);

		mContext = context;
		mUIHandler = UIHandler;
	}

	@Override
	public void handleMessage(Message msg) {
		SlimData data = (SlimData) msg.obj;
		msg.obj = null;
		Bitmap temp;
		switch (msg.what) {
			case MSG_INIT:
				if (!FaceDataV2.CHECK_FACE_SUCCESS) {
					//人脸检测
					FaceDataV2.CheckFace(mContext, data.orgBitmap);
					//初始化人脸识别
					FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
				} else if (FaceLocalData.getInstance() == null) {
					//初始化人脸识别
					FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
				}

				if (mUIHandler != null)
				{
					Message message = mUIHandler.obtainMessage();
					message.what = MSG_INIT;
					mUIHandler.sendMessage(message);
				}
				break;
			case MSG_SLIM:
				temp = data.orgBitmap.copy(Bitmap.Config.ARGB_8888, true);
				data.orgBitmap = null;

				temp = DoSlimFeature(mContext, temp, data.slimDatas, data.slimToolDatas, data.faceLocalData);
				data.outBitmap = temp;
				sendUIMessage(MSG_UPDATE_UI, data);

				break;
		}
	}

	public void clear()
	{
		mContext = null;
		mUIHandler = null;
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

	public static Bitmap DoSlimFeature(Context context, Bitmap bmp, ArrayList<LineData> slimDatas, ArrayList<LineData> slimToolDatas, FaceLocalData faceLocalData) {
		Bitmap out = bmp;

		if (out != null) {
			//瘦身
			out = ImageProcessor.Slim(context, out, slimDatas);
			out = ImageProcessor.SlimTool(context, out, slimToolDatas);
			//脸型
			if (faceLocalData != null) {
				for (int i = 0; i < faceLocalData.m_faceNum; i++) {
					if (faceLocalData.GetFaceLevel(i) > 0 && FaceDataV2.RAW_POS_MULTI != null) {
						out = ImageProcessor.ConversionImgShape(context, i, out, faceLocalData.m_faceType_multi[i], faceLocalData.GetFaceLevel(i));
					}
				}
			}
		}
		return out;
	}

	public static class SlimData {
		Bitmap orgBitmap;
		Bitmap outBitmap;

		ArrayList<LineData> slimDatas;
		ArrayList<LineData> slimToolDatas;
		FaceLocalData faceLocalData;
	}
}
