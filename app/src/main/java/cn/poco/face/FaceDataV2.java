package cn.poco.face;

import android.content.Context;
import android.graphics.Bitmap;

import cn.poco.image.ADD;
import cn.poco.image.PocoDetector;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.PocoLandmarks;
import cn.poco.image.PocoMakeUpV2;
import cn.poco.image.filter;
import cn.poco.tianutils.ImageUtils;

public class FaceDataV2
{
	// FIXME: 2017/6/22 加入同步
	public static boolean CHECK_FACE_SUCCESS = false;

	/*
	 * 左眼、右眼、嘴
	 * 00-02
	 * -04-
	 * 脸矩形(x,y,w,h)
	 * 06-08
	 */
	public static float[][] FACE_POS_MULTI; //UI显示用
	/*
	 * 左眉毛
	 * 00-02-04
	 * ---06---
	 * 右眉毛
	 * 08-10-12
	 * ---14---
	 */
	public static float[][] EYEBROW_POS_MULTI;
	/*
	 * 左眼
	 * ---02---
	 * 00-08-04
	 * ---06---
	 * 右眼
	 * ---12---
	 * 10-18-14
	 * ---16---
	 */
	public static float[][] EYE_POS_MULTI;
	/*
	 * 脸颊(边缘)
	 * 00-02
	 * 左脸(矩形)
	 * 04---
	 * ---06
	 * 右脸(矩形)
	 * 08---
	 * ---10
	 */
	public static float[][] CHEEK_POS_MULTI;
	/*
	 * 嘴唇
	 * ---02-04-06-08-10---
	 * 00----20-22-24----12
	 * ------30-28-26------
	 * ---18----16----14---
	 */
	public static float[][] LIP_POS_MULTI;

	public static float[][] NOSE_POS_MULTI;//鼻子

	public static float[][] CHIN_POS_MULTI;//下巴

	public static PocoFaceInfo[] RAW_POS_MULTI; //底层接口用

	public static int sFaceIndex = -1;

	public static boolean sIsFix = false; //是否手动修改过

	/**
	 * @param noFaceInit 没人脸时是否初始化默认的
	 */
	public synchronized static boolean CheckFace(Context context, Bitmap bmp, boolean noFaceInit)
	{
		//filter.preReadandWriteXML(context); //必须调用

		//System.out.println("w=" + bmp.getWidth() + " h=" + bmp.getHeight());
		if(RAW_POS_MULTI == null)
		{
//			RAW_POS_MULTI = filter.detectFaceInfoMulti(context, bmp);
			RAW_POS_MULTI = PocoDetector.getInstance().detectFace(context, bmp);
			if(RAW_POS_MULTI == null)
			{
				CHECK_FACE_SUCCESS = false;
				if(noFaceInit)
				{
					RAW_POS_MULTI = filter.initFaceInfo();
				}
			}
			else
			{
				CHECK_FACE_SUCCESS = true;
			}
		}

		if(RAW_POS_MULTI != null && RAW_POS_MULTI.length == 1)
		{
			sFaceIndex = 0;
		}
		else
		{
			sFaceIndex = -1;
		}

		Raw2Ripe(bmp.getWidth(), bmp.getHeight());

		return CHECK_FACE_SUCCESS;
	}

	public synchronized static boolean CheckFace(Context context, Bitmap bmp)
	{
		return CheckFace(context, bmp, true);
	}

	public synchronized static boolean CheckFaceAD(Context context, Bitmap bmp)
	{
		//filter.preReadandWriteXML(context); //必须调用

		//System.out.println("w=" + bmp.getWidth() + " h=" + bmp.getHeight());
		if(RAW_POS_MULTI == null)
		{
			RAW_POS_MULTI = ADD.BenefitFaceinfoMuti(context, bmp);
			if(RAW_POS_MULTI == null)
			{
				CHECK_FACE_SUCCESS = false;
				RAW_POS_MULTI = filter.initFaceInfo();
			}
			else
			{
				CHECK_FACE_SUCCESS = true;
			}
		}

		if(RAW_POS_MULTI != null && RAW_POS_MULTI.length == 1)
		{
			sFaceIndex = 0;
		}
		else
		{
			sFaceIndex = -1;
		}

		Raw2Ripe(bmp.getWidth(), bmp.getHeight());

		return CHECK_FACE_SUCCESS;
	}


	//商业face++人脸检测用
		public synchronized static boolean CheckFaceAD2(Context context, Bitmap bmp)
	{
		//filter.preReadandWriteXML(context); //必须调用

		//System.out.println("w=" + bmp.getWidth() + " h=" + bmp.getHeight());
		if(RAW_POS_MULTI == null)
		{
			RAW_POS_MULTI = ADD.BussinessFaceinfoMuti(context, bmp);
			if(RAW_POS_MULTI == null)
			{
				CHECK_FACE_SUCCESS = false;
				RAW_POS_MULTI = filter.initFaceInfo();
			}
			else
			{
				CHECK_FACE_SUCCESS = true;
			}
		}

		if(RAW_POS_MULTI != null && RAW_POS_MULTI.length == 1)
		{
			sFaceIndex = 0;
		}
		else
		{
			sFaceIndex = -1;
		}

		Raw2Ripe(bmp.getWidth(), bmp.getHeight());

		return CHECK_FACE_SUCCESS;
	}

	public synchronized static void Raw2Ripe(int w, int h)
	{
		FACE_POS_MULTI = null;
		if(RAW_POS_MULTI != null)
		{
			FACE_POS_MULTI = new float[RAW_POS_MULTI.length][10];
			for(int i = 0; i < RAW_POS_MULTI.length; i++)
			{
				float[] faceRect = RAW_POS_MULTI[i].getFaceRect();
				FACE_POS_MULTI[i][0] = faceRect[PocoLandmarks.lEyeCenterX];
				FACE_POS_MULTI[i][1] = faceRect[PocoLandmarks.lEyeCenterY];
				FACE_POS_MULTI[i][2] = faceRect[PocoLandmarks.rEyeCenterX];
				FACE_POS_MULTI[i][3] = faceRect[PocoLandmarks.rEyeCenterY];
				FACE_POS_MULTI[i][4] = faceRect[PocoLandmarks.MouthCenterX];
				FACE_POS_MULTI[i][5] = faceRect[PocoLandmarks.MouthCenterY];
				FACE_POS_MULTI[i][6] = faceRect[PocoLandmarks.FaceRectX];
				FACE_POS_MULTI[i][7] = faceRect[PocoLandmarks.FaceRectY];
				FACE_POS_MULTI[i][8] = faceRect[PocoLandmarks.FaceRectWidth];
				FACE_POS_MULTI[i][9] = faceRect[PocoLandmarks.FaceRectHeight];
			}
		}

		EYEBROW_POS_MULTI = null;
		EYE_POS_MULTI = null;
		CHEEK_POS_MULTI = null;
		LIP_POS_MULTI = null;
		NOSE_POS_MULTI = null;
		CHIN_POS_MULTI = null;
		if(RAW_POS_MULTI != null)
		{
			EYEBROW_POS_MULTI = new float[RAW_POS_MULTI.length][16];
			EYE_POS_MULTI = new float[RAW_POS_MULTI.length][20];
			CHEEK_POS_MULTI = new float[RAW_POS_MULTI.length][12];
			LIP_POS_MULTI = new float[RAW_POS_MULTI.length][32];
			NOSE_POS_MULTI = new float[RAW_POS_MULTI.length][4];
			CHIN_POS_MULTI = new float[RAW_POS_MULTI.length][2];

			float[] pos;
			for(int i = 0; i < RAW_POS_MULTI.length; i++)
			{
				float[] facePos = RAW_POS_MULTI[i].getFaceFeaturesMakeUp();
				EYEBROW_POS_MULTI[i][0] = facePos[PocoLandmarks.lEyebrowOuterX];
				EYEBROW_POS_MULTI[i][1] = facePos[PocoLandmarks.lEyebrowOuterY];
				EYEBROW_POS_MULTI[i][2] = facePos[PocoLandmarks.lEyebrowMidX1];
				EYEBROW_POS_MULTI[i][3] = facePos[PocoLandmarks.lEyebrowMidY1];
				EYEBROW_POS_MULTI[i][4] = facePos[PocoLandmarks.lEyebrowInnerX];
				EYEBROW_POS_MULTI[i][5] = facePos[PocoLandmarks.lEyebrowInnerY];
				EYEBROW_POS_MULTI[i][6] = facePos[PocoLandmarks.lEyebrowMidX2];
				EYEBROW_POS_MULTI[i][7] = facePos[PocoLandmarks.lEyebrowMidY2];
				EYEBROW_POS_MULTI[i][8] = facePos[PocoLandmarks.rEyebrowInnerX];
				EYEBROW_POS_MULTI[i][9] = facePos[PocoLandmarks.rEyebrowInnerY];
				EYEBROW_POS_MULTI[i][10] = facePos[PocoLandmarks.rEyebrowMidX1];
				EYEBROW_POS_MULTI[i][11] = facePos[PocoLandmarks.rEyebrowMidY1];
				EYEBROW_POS_MULTI[i][12] = facePos[PocoLandmarks.rEyebrowOuterX];
				EYEBROW_POS_MULTI[i][13] = facePos[PocoLandmarks.rEyebrowOuterY];
				EYEBROW_POS_MULTI[i][14] = facePos[PocoLandmarks.rEyebrowMidX2];
				EYEBROW_POS_MULTI[i][15] = facePos[PocoLandmarks.rEyebrowMidY2];

				EYE_POS_MULTI[i][0] = facePos[PocoLandmarks.lEyeOuterX];
				EYE_POS_MULTI[i][1] = facePos[PocoLandmarks.lEyeOuterY];
				EYE_POS_MULTI[i][2] = facePos[PocoLandmarks.lEyeTopX];
				EYE_POS_MULTI[i][3] = facePos[PocoLandmarks.lEyeTopY];
				EYE_POS_MULTI[i][4] = facePos[PocoLandmarks.lEyeInnerX];
				EYE_POS_MULTI[i][5] = facePos[PocoLandmarks.lEyeInnerY];
				EYE_POS_MULTI[i][6] = facePos[PocoLandmarks.lEyeBottomX];
				EYE_POS_MULTI[i][7] = facePos[PocoLandmarks.lEyeBottomY];
				EYE_POS_MULTI[i][8] = facePos[PocoLandmarks.lEyeX];
				EYE_POS_MULTI[i][9] = facePos[PocoLandmarks.lEyeY];
				EYE_POS_MULTI[i][10] = facePos[PocoLandmarks.rEyeInnerX];
				EYE_POS_MULTI[i][11] = facePos[PocoLandmarks.rEyeInnerY];
				EYE_POS_MULTI[i][12] = facePos[PocoLandmarks.rEyeTopX];
				EYE_POS_MULTI[i][13] = facePos[PocoLandmarks.rEyeTopY];
				EYE_POS_MULTI[i][14] = facePos[PocoLandmarks.rEyeOuterX];
				EYE_POS_MULTI[i][15] = facePos[PocoLandmarks.rEyeOuterY];
				EYE_POS_MULTI[i][16] = facePos[PocoLandmarks.rEyeBottomX];
				EYE_POS_MULTI[i][17] = facePos[PocoLandmarks.rEyeBottomY];
				EYE_POS_MULTI[i][18] = facePos[PocoLandmarks.rEyeX];
				EYE_POS_MULTI[i][19] = facePos[PocoLandmarks.rEyeY];

				CHEEK_POS_MULTI[i][0] = facePos[PocoLandmarks.chinLeftX];
				CHEEK_POS_MULTI[i][1] = facePos[PocoLandmarks.chinLeftY];
				CHEEK_POS_MULTI[i][2] = facePos[PocoLandmarks.chinRightX];
				CHEEK_POS_MULTI[i][3] = facePos[PocoLandmarks.chinRightY];
				pos = PocoMakeUpV2.GetBlushpos(facePos, w, h, false);
				float cx = pos[0];
				float cy = pos[1];
				float r = Math.abs(ImageUtils.Spacing(facePos[PocoLandmarks.lEyeInnerX] - facePos[PocoLandmarks.rEyeInnerX], facePos[PocoLandmarks.lEyeInnerY] - facePos[PocoLandmarks.rEyeInnerY]) / 3f);
				CHEEK_POS_MULTI[i][4] = cx - r;
				CHEEK_POS_MULTI[i][5] = cy - r;
				CHEEK_POS_MULTI[i][6] = cx + r;
				CHEEK_POS_MULTI[i][7] = cy + r;
				pos = PocoMakeUpV2.GetBlushpos(facePos, w, h, true);
				cx = pos[0];
				cy = pos[1];
				CHEEK_POS_MULTI[i][8] = cx - r;
				CHEEK_POS_MULTI[i][9] = cy - r;
				CHEEK_POS_MULTI[i][10] = cx + r;
				CHEEK_POS_MULTI[i][11] = cy + r;

				LIP_POS_MULTI[i][0] = facePos[PocoLandmarks.lMouthCornerX];
				LIP_POS_MULTI[i][1] = facePos[PocoLandmarks.lMouthCornerY];
				LIP_POS_MULTI[i][2] = facePos[PocoLandmarks.lMouthTop1X];
				LIP_POS_MULTI[i][3] = facePos[PocoLandmarks.lMouthTop1Y];
				LIP_POS_MULTI[i][4] = facePos[PocoLandmarks.lMouthTop2X];
				LIP_POS_MULTI[i][5] = facePos[PocoLandmarks.lMouthTop2Y];
				LIP_POS_MULTI[i][6] = facePos[PocoLandmarks.TopOfTopLipX];
				LIP_POS_MULTI[i][7] = facePos[PocoLandmarks.TopOfTopLipY];
				LIP_POS_MULTI[i][8] = facePos[PocoLandmarks.rMouthTop2X];
				LIP_POS_MULTI[i][9] = facePos[PocoLandmarks.rMouthTop2Y];
				LIP_POS_MULTI[i][10] = facePos[PocoLandmarks.rMouthTop1X];
				LIP_POS_MULTI[i][11] = facePos[PocoLandmarks.rMouthTop1Y];
				LIP_POS_MULTI[i][12] = facePos[PocoLandmarks.rMouthCornerX];
				LIP_POS_MULTI[i][13] = facePos[PocoLandmarks.rMouthCornerY];
				LIP_POS_MULTI[i][14] = facePos[PocoLandmarks.rMouthBot1X];
				LIP_POS_MULTI[i][15] = facePos[PocoLandmarks.rMouthBot1Y];
				LIP_POS_MULTI[i][16] = facePos[PocoLandmarks.BotOfBotLipX];
				LIP_POS_MULTI[i][17] = facePos[PocoLandmarks.BotOfBotLipY];
				LIP_POS_MULTI[i][18] = facePos[PocoLandmarks.lMouthBot1X];
				LIP_POS_MULTI[i][19] = facePos[PocoLandmarks.lMouthBot1Y];
				LIP_POS_MULTI[i][20] = facePos[PocoLandmarks.lMouthTop3X];
				LIP_POS_MULTI[i][21] = facePos[PocoLandmarks.lMouthTop3Y];
				LIP_POS_MULTI[i][22] = facePos[PocoLandmarks.BotOfTopLipX];
				LIP_POS_MULTI[i][23] = facePos[PocoLandmarks.BotOfTopLipY];
				LIP_POS_MULTI[i][24] = facePos[PocoLandmarks.rMouthTop3X];
				LIP_POS_MULTI[i][25] = facePos[PocoLandmarks.rMouthTop3Y];
				LIP_POS_MULTI[i][26] = facePos[PocoLandmarks.rMouthBot2X];
				LIP_POS_MULTI[i][27] = facePos[PocoLandmarks.rMouthBot2Y];
				LIP_POS_MULTI[i][28] = facePos[PocoLandmarks.TopOfBotLipX];
				LIP_POS_MULTI[i][29] = facePos[PocoLandmarks.TopOfBotLipY];
				LIP_POS_MULTI[i][30] = facePos[PocoLandmarks.lMouthBot2X];
				LIP_POS_MULTI[i][31] = facePos[PocoLandmarks.lMouthBot2Y];

				NOSE_POS_MULTI[i][0] = facePos[PocoLandmarks.NoseLeftX];
				NOSE_POS_MULTI[i][1] = facePos[PocoLandmarks.NoseLeftY];
				NOSE_POS_MULTI[i][2] = facePos[PocoLandmarks.NoseRightX];
				NOSE_POS_MULTI[i][3] = facePos[PocoLandmarks.NoseRightY];

				CHIN_POS_MULTI[i][0] = facePos[PocoLandmarks.chinX];
				CHIN_POS_MULTI[i][1] = facePos[PocoLandmarks.chinY];
			}
		}
	}

	public synchronized static void Ripe2Raw()
	{
		if(RAW_POS_MULTI != null && FACE_POS_MULTI != null)
		{
			for(int i = 0; i < RAW_POS_MULTI.length; i++)
			{
				float[] faceRect = RAW_POS_MULTI[i].getFaceRect();

				faceRect[PocoLandmarks.lEyeCenterX] = FACE_POS_MULTI[i][0];
				faceRect[PocoLandmarks.lEyeCenterY] = FACE_POS_MULTI[i][1];
				faceRect[PocoLandmarks.rEyeCenterX] = FACE_POS_MULTI[i][2];
				faceRect[PocoLandmarks.rEyeCenterY] = FACE_POS_MULTI[i][3];
				faceRect[PocoLandmarks.MouthCenterX] = FACE_POS_MULTI[i][4];
				faceRect[PocoLandmarks.MouthCenterY] = FACE_POS_MULTI[i][5];

				RAW_POS_MULTI[i].setFaceRect(faceRect);
			}
		}
		if(RAW_POS_MULTI != null && FACE_POS_MULTI != null && EYEBROW_POS_MULTI != null && EYE_POS_MULTI != null && CHEEK_POS_MULTI != null && LIP_POS_MULTI != null && NOSE_POS_MULTI != null && CHIN_POS_MULTI != null)
		{
			for(int i = 0; i < RAW_POS_MULTI.length; i++)
			{
				float[] facePos = RAW_POS_MULTI[i].getFaceFeaturesMakeUp();

				facePos[PocoLandmarks.lEyebrowOuterX] = EYEBROW_POS_MULTI[i][0];
				facePos[PocoLandmarks.lEyebrowOuterY] = EYEBROW_POS_MULTI[i][1];
				facePos[PocoLandmarks.lEyebrowMidX1] = EYEBROW_POS_MULTI[i][2];
				facePos[PocoLandmarks.lEyebrowMidY1] = EYEBROW_POS_MULTI[i][3];
				facePos[PocoLandmarks.lEyebrowInnerX] = EYEBROW_POS_MULTI[i][4];
				facePos[PocoLandmarks.lEyebrowInnerY] = EYEBROW_POS_MULTI[i][5];
				facePos[PocoLandmarks.lEyebrowMidX2] = EYEBROW_POS_MULTI[i][6];
				facePos[PocoLandmarks.lEyebrowMidY2] = EYEBROW_POS_MULTI[i][7];
				facePos[PocoLandmarks.rEyebrowInnerX] = EYEBROW_POS_MULTI[i][8];
				facePos[PocoLandmarks.rEyebrowInnerY] = EYEBROW_POS_MULTI[i][9];
				facePos[PocoLandmarks.rEyebrowMidX1] = EYEBROW_POS_MULTI[i][10];
				facePos[PocoLandmarks.rEyebrowMidY1] = EYEBROW_POS_MULTI[i][11];
				facePos[PocoLandmarks.rEyebrowOuterX] = EYEBROW_POS_MULTI[i][12];
				facePos[PocoLandmarks.rEyebrowOuterY] = EYEBROW_POS_MULTI[i][13];
				facePos[PocoLandmarks.rEyebrowMidX2] = EYEBROW_POS_MULTI[i][14];
				facePos[PocoLandmarks.rEyebrowMidY2] = EYEBROW_POS_MULTI[i][15];

				facePos[PocoLandmarks.lEyeOuterX] = EYE_POS_MULTI[i][0];
				facePos[PocoLandmarks.lEyeOuterY] = EYE_POS_MULTI[i][1];
				facePos[PocoLandmarks.lEyeTopX] = EYE_POS_MULTI[i][2];
				facePos[PocoLandmarks.lEyeTopY] = EYE_POS_MULTI[i][3];
				facePos[PocoLandmarks.lEyeInnerX] = EYE_POS_MULTI[i][4];
				facePos[PocoLandmarks.lEyeInnerY] = EYE_POS_MULTI[i][5];
				facePos[PocoLandmarks.lEyeBottomX] = EYE_POS_MULTI[i][6];
				facePos[PocoLandmarks.lEyeBottomY] = EYE_POS_MULTI[i][7];
				facePos[PocoLandmarks.lEyeX] = EYE_POS_MULTI[i][8];
				facePos[PocoLandmarks.lEyeY] = EYE_POS_MULTI[i][9];
				facePos[PocoLandmarks.rEyeInnerX] = EYE_POS_MULTI[i][10];
				facePos[PocoLandmarks.rEyeInnerY] = EYE_POS_MULTI[i][11];
				facePos[PocoLandmarks.rEyeTopX] = EYE_POS_MULTI[i][12];
				facePos[PocoLandmarks.rEyeTopY] = EYE_POS_MULTI[i][13];
				facePos[PocoLandmarks.rEyeOuterX] = EYE_POS_MULTI[i][14];
				facePos[PocoLandmarks.rEyeOuterY] = EYE_POS_MULTI[i][15];
				facePos[PocoLandmarks.rEyeBottomX] = EYE_POS_MULTI[i][16];
				facePos[PocoLandmarks.rEyeBottomY] = EYE_POS_MULTI[i][17];
				facePos[PocoLandmarks.rEyeX] = EYE_POS_MULTI[i][18];
				facePos[PocoLandmarks.rEyeY] = EYE_POS_MULTI[i][19];

				facePos[PocoLandmarks.chinLeftX] = CHEEK_POS_MULTI[i][0];
				facePos[PocoLandmarks.chinLeftY] = CHEEK_POS_MULTI[i][1];
				facePos[PocoLandmarks.chinRightX] = CHEEK_POS_MULTI[i][2];
				facePos[PocoLandmarks.chinRightY] = CHEEK_POS_MULTI[i][3];

				facePos[PocoLandmarks.lMouthCornerX] = LIP_POS_MULTI[i][0];
				facePos[PocoLandmarks.lMouthCornerY] = LIP_POS_MULTI[i][1];
				facePos[PocoLandmarks.lMouthTop1X] = LIP_POS_MULTI[i][2];
				facePos[PocoLandmarks.lMouthTop1Y] = LIP_POS_MULTI[i][3];
				facePos[PocoLandmarks.lMouthTop2X] = LIP_POS_MULTI[i][4];
				facePos[PocoLandmarks.lMouthTop2Y] = LIP_POS_MULTI[i][5];
				facePos[PocoLandmarks.TopOfTopLipX] = LIP_POS_MULTI[i][6];
				facePos[PocoLandmarks.TopOfTopLipY] = LIP_POS_MULTI[i][7];
				facePos[PocoLandmarks.rMouthTop2X] = LIP_POS_MULTI[i][8];
				facePos[PocoLandmarks.rMouthTop2Y] = LIP_POS_MULTI[i][9];
				facePos[PocoLandmarks.rMouthTop1X] = LIP_POS_MULTI[i][10];
				facePos[PocoLandmarks.rMouthTop1Y] = LIP_POS_MULTI[i][11];
				facePos[PocoLandmarks.rMouthCornerX] = LIP_POS_MULTI[i][12];
				facePos[PocoLandmarks.rMouthCornerY] = LIP_POS_MULTI[i][13];
				facePos[PocoLandmarks.rMouthBot1X] = LIP_POS_MULTI[i][14];
				facePos[PocoLandmarks.rMouthBot1Y] = LIP_POS_MULTI[i][15];
				facePos[PocoLandmarks.BotOfBotLipX] = LIP_POS_MULTI[i][16];
				facePos[PocoLandmarks.BotOfBotLipY] = LIP_POS_MULTI[i][17];
				facePos[PocoLandmarks.lMouthBot1X] = LIP_POS_MULTI[i][18];
				facePos[PocoLandmarks.lMouthBot1Y] = LIP_POS_MULTI[i][19];
				facePos[PocoLandmarks.lMouthTop3X] = LIP_POS_MULTI[i][20];
				facePos[PocoLandmarks.lMouthTop3Y] = LIP_POS_MULTI[i][21];
				facePos[PocoLandmarks.BotOfTopLipX] = LIP_POS_MULTI[i][22];
				facePos[PocoLandmarks.BotOfTopLipY] = LIP_POS_MULTI[i][23];
				facePos[PocoLandmarks.rMouthTop3X] = LIP_POS_MULTI[i][24];
				facePos[PocoLandmarks.rMouthTop3Y] = LIP_POS_MULTI[i][25];
				facePos[PocoLandmarks.rMouthBot2X] = LIP_POS_MULTI[i][26];
				facePos[PocoLandmarks.rMouthBot2Y] = LIP_POS_MULTI[i][27];
				facePos[PocoLandmarks.TopOfBotLipX] = LIP_POS_MULTI[i][28];
				facePos[PocoLandmarks.TopOfBotLipY] = LIP_POS_MULTI[i][29];
				facePos[PocoLandmarks.lMouthBot2X] = LIP_POS_MULTI[i][30];
				facePos[PocoLandmarks.lMouthBot2Y] = LIP_POS_MULTI[i][31];

				facePos[PocoLandmarks.NoseLeftX] = NOSE_POS_MULTI[i][0];
				facePos[PocoLandmarks.NoseLeftY] = NOSE_POS_MULTI[i][1];
				facePos[PocoLandmarks.NoseRightX] = NOSE_POS_MULTI[i][2];
				facePos[PocoLandmarks.NoseRightY] = NOSE_POS_MULTI[i][3];

				facePos[PocoLandmarks.chinX] = CHIN_POS_MULTI[i][0];
				facePos[PocoLandmarks.chinY] = CHIN_POS_MULTI[i][1];

				RAW_POS_MULTI[i].setMakeUpFeatures(facePos);
			}
		}
	}

	public static float[] Physical2Logical(int[] src, int w, int h)
	{
		float[] dst = null;
		if(src != null)
		{
			int len = src.length;
			dst = new float[len];
			for(int i = 0; i < len; i++)
			{
				if(i % 2 == 0)
				{
					dst[i] = (float)src[i] / (float)w;
				}
				else
				{
					dst[i] = (float)src[i] / (float)h;
				}
			}
		}

		return dst;
	}

	public static int[] Logical2Physical(float[] src, int w, int h)
	{
		int[] dst = null;
		if(src != null)
		{
			int len = src.length;
			dst = new int[len];
			for(int i = 0; i < len; i++)
			{
				if(i % 2 == 0)
				{
					dst[i] = (int)(src[i] * w);
				}
				else
				{
					dst[i] = (int)(src[i] * h);
				}
			}
		}

		return dst;
	}

	public synchronized static void ResetData()
	{
		sIsFix = false;
		CHECK_FACE_SUCCESS = false;

		FACE_POS_MULTI = null;
		EYEBROW_POS_MULTI = null;
		EYE_POS_MULTI = null;
		CHEEK_POS_MULTI = null;
		LIP_POS_MULTI = null;
		NOSE_POS_MULTI = null;
		CHIN_POS_MULTI = null;

		RAW_POS_MULTI = null;

		sFaceIndex = -1;
	}
}
