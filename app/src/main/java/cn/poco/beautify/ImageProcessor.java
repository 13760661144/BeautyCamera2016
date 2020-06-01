package cn.poco.beautify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;

import cn.poco.common.LineData;
import cn.poco.common.PointData;
import cn.poco.face.FaceDataV2;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.image.PocoCompositeOperator;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.PocoMakeUpV2;
import cn.poco.image.PocoMakeUpV3;
import cn.poco.image.filter;
import cn.poco.makeup.MakeupADUtil;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupType;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

import static android.R.attr.id;

public class ImageProcessor
{
	/**
	 * @param context
	 * @param bmp
	 * @param params  [0]uri,[1]light,[2]blur,[3]hue
	 * @return
	 */
	public static Bitmap ConversionImgColorNew(Context context, boolean readConfig, Bitmap bmp, int... params)
	{
		Bitmap out = bmp;

		if(context != null && bmp != null)
		{
			PocoFaceInfo[] faceData = null;
			if(FaceDataV2.CHECK_FACE_SUCCESS)
			{
				faceData = FaceDataV2.RAW_POS_MULTI;
			}
			switch(params[0])
			{
				case EffectType.EFFECT_NEWBEE: //新效果
					out = PocoBeautyFilter.crazyBeautyDefault(bmp, faceData, false);
					break;

				case EffectType.EFFECT_DEFAULT:
					out = PocoBeautyFilter.RealTimeBeautyDefault(bmp, faceData, false);
					break;

				case EffectType.EFFECT_NATURE: //自然
				case EffectType.EFFECT_AD62:
					out = PocoBeautyFilter.moreBeauteNormalCache(bmp, faceData, false);
					break;

				case EffectType.EFFECT_MIDDLE: //亮白
					out = PocoBeautyFilter.moreBeauteMiddCache(bmp, faceData, false);
					break;

				case EffectType.EFFECT_LITTLE: //轻微
					out = PocoBeautyFilter.moreBeauteLittleCache(bmp, faceData, false);
					break;

				case EffectType.EFFECT_MOONLIGHT: //朦胧
					out = PocoBeautyFilter.moreBeauteMoonlightCache(bmp, faceData, false);
					break;

				case EffectType.EFFECT_USER: //自定义
					out = PocoBeautyFilter.moreBeuateUserCache(bmp, faceData, params[1], params[2], params[3], false);
					break;
				case EffectType.EFFECT_CLEAR://TODO 净白（美白固定75%，磨皮55%(默认底层参数60)）
					byte[] assetsByte = FileUtil.getAssetsByte(context, "skinStyle/level_table_1");
					Bitmap tmp = PocoBeautyFilter.CameraSkinBeauty(bmp, assetsByte, 75);
					out = PocoBeautyFilter.CameraSmoothBeauty(tmp, faceData, 60, false);
					break;

				case EffectType.EFFECT_NONE: // 无
				default:
					out = bmp;
					break;
			}
		}

		return out;
	}

	/**
	 * 加了保存文件的
	 *
	 * @param context
	 * @param bmp
	 * @param params  [0]uri,[1]light,[2]blur,[3]hue
	 * @return
	 */
	public static Bitmap ConversionImgColorCache(Context context, boolean readConfig, Bitmap bmp, int... params)
	{
		Bitmap out = bmp;

		if(context != null && bmp != null)
		{
			PocoFaceInfo[] faceData = null;
			if(FaceDataV2.CHECK_FACE_SUCCESS)
			{
				faceData = FaceDataV2.RAW_POS_MULTI;
			}
			switch(params[0])
			{
				case EffectType.EFFECT_NEWBEE: //新效果
					out = PocoBeautyFilter.crazyBeautyDefault(bmp, faceData, true);
					break;

				case EffectType.EFFECT_DEFAULT:
					out = PocoBeautyFilter.RealTimeBeautyDefault(bmp, faceData, true);
					break;

				case EffectType.EFFECT_NATURE: //自然
				case EffectType.EFFECT_AD62: //
					out = PocoBeautyFilter.moreBeauteNormalCache(bmp, faceData, true);
					break;

				case EffectType.EFFECT_MIDDLE: //亮白
					out = PocoBeautyFilter.moreBeauteMiddCache(bmp, faceData, true);
					break;

				case EffectType.EFFECT_LITTLE: //轻微
					out = PocoBeautyFilter.moreBeauteLittleCache(bmp, faceData, true);
					break;

				case EffectType.EFFECT_MOONLIGHT: //朦胧
					out = PocoBeautyFilter.moreBeauteMoonlightCache(bmp, faceData, true);
					break;

				case EffectType.EFFECT_USER: //自定义
					out = PocoBeautyFilter.moreBeuateUserCache(bmp, faceData, params[1], params[2], params[3], true);
					break;
				/*case EffectType.EFFECT_CLEAR://TODO 净白（美白固定75%，磨皮55%(默认底层参数60)）
					byte[] assetsByte = FileUtil.getAssetsByte(context, "skinStyle/level_table_1");
					Bitmap tmp = PocoBeautyFilter.CameraSkinBeauty(bmp, assetsByte, 75);
					out = PocoBeautyFilter.CameraSmoothBeauty(tmp, faceData, 60, true);
					break;*/

				case EffectType.EFFECT_NONE: // 无
				default:
					out = bmp;
					break;
			}
		}

		return out;
	}

	/**
	 * 净白（美白固定75%）
	 */
	public static Bitmap whitening(Bitmap bmp, PocoFaceInfo[] faceData, byte[] handlerByte, int alpha, boolean isCache) {
		if (faceData == null && FaceDataV2.CHECK_FACE_SUCCESS) {
			faceData = FaceDataV2.RAW_POS_MULTI;
		}
		Bitmap out = PocoBeautyFilter.CameraSkinBeauty(bmp, handlerByte, 75);
		return PocoBeautyFilter.CameraSmoothBeauty(out, faceData, alpha, isCache);
	}

	/**
	 * 注意参数混淆
	 *
	 * @param src   原图(不会修改)
	 * @param mask  效果图(会修改,也就是返回的图片)
	 * @param alpha
	 * @return
	 */
	public static Bitmap DrawMask(boolean readConfig, Bitmap src, Bitmap mask, int alpha)
	{
		Bitmap out = mask;

		if(alpha < 0 || alpha > 100)
		{
			alpha = 100;
		}

		PocoFaceInfo[] faceData = null;
		if(FaceDataV2.CHECK_FACE_SUCCESS)
		{
			faceData = FaceDataV2.RAW_POS_MULTI;
		}
		out = PocoBeautyFilter.blendBmp(src, mask, faceData, alpha);

		return out;
	}

	/**
	 * @param dst   原图(会修改,也就是返回的图片)
	 * @param mask  效果图(不会修改)
	 * @param alpha
	 * @return
	 */
	public static Bitmap DrawMask2(Bitmap dst, Bitmap mask, int alpha)
	{
		Canvas canvas = new Canvas(dst);
		Paint pt = new Paint();
		pt.setAlpha((int)((float)alpha / 100f * 255));
		canvas.drawBitmap(mask, new Matrix(), pt);
		return dst;
	}

	public static Bitmap DrawNewBeeMask3(Bitmap dst, Bitmap mask, int alpha, boolean isCache)
	{
		if(alpha < 0 || alpha > 100)
		{
			alpha = 100;
		}

		PocoFaceInfo[] faceData = null;
		if(FaceDataV2.CHECK_FACE_SUCCESS)
		{
			faceData = FaceDataV2.RAW_POS_MULTI;
		}
		Bitmap out = PocoBeautyFilter.crazyBeautyMicroAdjust(dst, mask, faceData, alpha, isCache);

		return out;
	}

	public static Bitmap DrawDefaultMask4(Bitmap dst, Bitmap mask, int alpha, boolean isCache)
	{
		if(alpha < 0 || alpha > 100)
		{
			alpha = 100;
		}

		PocoFaceInfo[] faceData = null;
		if(FaceDataV2.CHECK_FACE_SUCCESS)
		{
			faceData = FaceDataV2.RAW_POS_MULTI;
		}
		Bitmap out = PocoBeautyFilter.realtimeBeautyMicroAdjust(dst, mask, faceData, alpha, isCache);

		return out;
	}

	/**
	 * @param context
	 * @param bmp
	 * @param params  [0]type my.Liquefaction.ModeType.FACE_NO...,[1]value
	 * @return
	 */
	public static Bitmap ConversionImgShape(Context context, int faceIndex, Bitmap bmp, int... params)
	{
		if(bmp != null && FaceDataV2.RAW_POS_MULTI != null)
		{
			switch(params[0])
			{
				case FaceType.FACE_NO:
					break;

				case FaceType.FACE_AWL:
					PocoBeautyFilter.starFace_v(bmp, FaceDataV2.RAW_POS_MULTI[faceIndex], params[1]);
					break;

				case FaceType.FACE_OVAL:
					PocoBeautyFilter.starFace_oval(bmp, FaceDataV2.RAW_POS_MULTI[faceIndex], params[1]);
					break;

				case FaceType.FACE_EGG:
					PocoBeautyFilter.starFace_circle(bmp, FaceDataV2.RAW_POS_MULTI[faceIndex], params[1]);
					break;

				default:
					break;
			}
		}

		return bmp;
	}

	public static Bitmap LoadBitmap(Context context, Object img, int outW, int outH)
	{
		if(img != null)
		{
			Bitmap out = null;

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			if(img instanceof String)
			{
				BitmapFactory.decodeFile((String)img, opts);
			}
			else if(img instanceof Integer)
			{
				BitmapFactory.decodeResource(context.getResources(), (Integer)img, opts);
			}

			opts.inSampleSize = opts.outWidth / outW < opts.outHeight / outH ? opts.outWidth / outW : opts.outHeight / outH;
			if(opts.inSampleSize < 1)
			{
				opts.inSampleSize = 1;
			}

			long maxMem = Runtime.getRuntime().maxMemory() / 2;
			int bpp = 4;
			long imgMem = opts.outWidth / opts.inSampleSize * opts.outHeight / opts.inSampleSize * bpp;
			if(imgMem > maxMem)
			{
				opts.inSampleSize = (int)Math.ceil(Math.sqrt((long)opts.outWidth * opts.outHeight * bpp / (double)maxMem));
			}

			opts.inJustDecodeBounds = false;
			opts.inDither = true;
			opts.inPreferredConfig = Config.ARGB_8888;
			if(img instanceof String)
			{
				out = cn.poco.imagecore.Utils.DecodeFile((String)img, opts);
			}
			else if(img instanceof Integer)
			{
				out = BitmapFactory.decodeResource(context.getResources(), (Integer)img, opts);
			}

			if(out != null)
			{
				if(out.getConfig() != Config.ARGB_8888)
				{
					Bitmap temp = out.copy(Config.ARGB_8888, true);
					out.recycle();
					out = null;
					out = temp;
				}

				return out;
			}
		}

		return null;
	}

	public static Bitmap LoadRawBitmap(Context context, Object img)
	{
		Bitmap out = null;

		if(img instanceof String)
		{
			out = BitmapFactory.decodeFile((String)img);
		}
		else if(img instanceof Integer)
		{
			out = BitmapFactory.decodeResource(context.getResources(), (Integer)img);
		}
		else if(img instanceof Bitmap)
		{
			out = (Bitmap)img;
		}

		return out;
	}

	/**
	 * @param context
	 * @param bmp
	 * @param datas
	 * @param params  length:10,   0:eyebrow 1:eye 2:kohl 3:eyelash_up 4:eyelash_down
	 *                5:eyeline_up 6:eyeline_down 7:cheek 8:mouth 9:foundation
	 * @return
	 */
	public static Bitmap DoMakeup(Context context, int faceIndex, Bitmap bmp, ArrayList<MakeupRes.MakeupData> datas, int[] params)
	{
		Bitmap out = bmp;

		if(datas != null && FaceDataV2.RAW_POS_MULTI != null)
		{
			PocoMakeUpV2.MakeUpV2 makeups = null;
			Bitmap[] tempBmps;
			MakeupRes.MakeupData temp;
			int len = datas.size();
			for(int i = 0; i < len; i++)
			{
				temp = datas.get(i);
				MakeupType type = MakeupType.GetType(temp.m_makeupType);
				switch(type)
				{
					case LIP:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							if(MakeupADUtil.isADLipEffect(temp.m_id))
							{
								PocoMakeUpV2.ShuuemuraLipstick(bmp, context, MakeupADUtil.getColorById(temp.m_id), FaceDataV2.RAW_POS_MULTI[faceIndex], params[8]);
							}
							else if(MakeupADUtil.isBenefitEffectLip(temp.m_id))
							{
								PocoMakeUpV2.BenefitDoubleLipstick(bmp,context,MakeupADUtil.getColorByIdForBenefit(temp.m_id),FaceDataV2.RAW_POS_MULTI[faceIndex], params[8]);
							}
							else if(MakeupADUtil.isArManiAD(temp.m_id))
							{
								PocoMakeUpV2.AmaniLipstick(bmp,context,MakeupADUtil.getArmaniColorBy(temp.m_id),FaceDataV2.RAW_POS_MULTI[faceIndex], params[8]);
							}
							//兰蔻唇彩
							else if(MakeupADUtil.isLCId(temp.m_id))
							{
								int id = temp.m_id;
								PocoMakeUpV3.MakeUpLipstick(out,MakeupADUtil.getLancuoNeedBmpById(context,id),
										MakeupADUtil.getlancuoColorById(id),
                                 MakeupADUtil.getlcheType(id),params[8],null,0,0,MakeupADUtil.getlcscBmpById(context,id),
										MakeupADUtil.getmouthUParams(id),
										MakeupADUtil.getlancuoMixTypeUById(id),MakeupADUtil.getlancuoAlphaUById(id),
										MakeupADUtil.getlcxcBmpById(context,id),MakeupADUtil.getmouthDParams(id),MakeupADUtil.getlancuoMixTypeDById(id),MakeupADUtil.getlancuoAlphaDById(id),FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex],PocoMakeUpV3.LIPGEXING);
							}
							//ysl唇彩
							else if(MakeupADUtil.isYSLId(temp.m_id))
							{
								Bitmap needBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.yls_needbmp);
								int[] color = MakeupADUtil.getYSLColorById(temp.m_id);
								Bitmap uBmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.yls_ubmp);
								Bitmap dBmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.yls_dbmp);
								PocoMakeUpV3.MakeUpLipstick(out,needBmp,color,PocoCompositeOperator.SoftLightCompositeOp,
										params[8],null,0,0,uBmp,MakeupADUtil.getmouthUParams(id),PocoCompositeOperator.ScreenCompositeOp,28,
										dBmp,MakeupADUtil.getmouthDParams(id),PocoCompositeOperator.ScreenCompositeOp,100,FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex],PocoMakeUpV3.LIPGEXING);
							}
							else
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new PocoMakeUpV2.Lipstick(null, null, 0, params[8]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
						}
						break;

					case CHEEK_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
							makeups = new PocoMakeUpV2.Blush(null, null, 0, params[7]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
						}
						break;

					case KOHL_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							//商业
							/*if(temp.m_id == 55728)
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new EyeShadow(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, (Integer)temp.m_ex, params[2]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_ALL_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;

								float[] pos = new float[]{203, 106, 121, 60, 55, 90, 120, 123};
								int res = R.drawable.__mak__1234163916367244;

								tempBmps = new Bitmap[]{LoadRawBitmap(context, res)};
								makeups = new EyeShadow(FaceDataV2.Logical2Physical(pos, 1, 1), null, (Integer)temp.m_ex, params[2]);
								makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_ALL_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
							else if(temp.m_id == 55744)
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new EyeShadow(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, (Integer)temp.m_ex, params[2]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_ALL_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;

								float[] pos = new float[]{203, 106, 121, 60, 55, 90, 120, 123};
								int res = R.drawable.__mak__1234164034402887;

								tempBmps = new Bitmap[]{LoadRawBitmap(context, res)};
								makeups = new EyeShadow(FaceDataV2.Logical2Physical(pos, 1, 1), null, (Integer)temp.m_ex, params[2]);
								makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_ALL_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
							else*/
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new PocoMakeUpV2.EyeShadow(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, (Integer)temp.m_ex, params[2]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
						}
						break;

					case EYELINER_DOWN_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
							makeups = new PocoMakeUpV2.EyeLineDown(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, PocoCompositeOperator.DarkenCompositeOp, params[6]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
						}
						break;

					case EYELINER_UP_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
							makeups = new PocoMakeUpV2.EyeLineUp(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, PocoCompositeOperator.DarkenCompositeOp, params[5]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
						}
						break;

					case EYEBROW_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
							makeups = new PocoMakeUpV2.EyeBrow(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, PocoCompositeOperator.DarkenCompositeOp, params[0]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
						}
						break;

					case EYELASH_DOWN_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
							makeups = new PocoMakeUpV2.EyeLashDown(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), null, (Integer)temp.m_ex, params[4]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
						}
						break;

					case EYELASH_UP_L:
						if(temp.m_res != null && temp.m_res.length >= 2)
						{
							tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0]), LoadRawBitmap(context, temp.m_res[1])};
							makeups = new PocoMakeUpV2.EyeLashUp(FaceDataV2.Logical2Physical(temp.m_pos, 1, 1), (float[])temp.m_params, (Integer)temp.m_ex, params[3]);
							makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
							tempBmps[0].recycle();
							tempBmps[0] = null;
							tempBmps[1].recycle();
							tempBmps[1] = null;
						}
						break;

					case EYE_L:
						if(temp.m_res != null && temp.m_res.length >= 1)
						{
							//Clalen商业
							if(temp.m_id == 56688 || temp.m_id == 56696 || temp.m_id == 56704)
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new PocoMakeUpV2.EyeContact_ad(null, null, 0, params[1]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
							else
							{
								tempBmps = new Bitmap[]{LoadRawBitmap(context, temp.m_res[0])};
								makeups = new PocoMakeUpV2.EyeContact(null, null, 0, params[1]);
								makeups.selfdraw(bmp, tempBmps, false, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								makeups.selfdraw(bmp, tempBmps, true, FaceDataV2.RAW_POS_MULTI[faceIndex]);
								tempBmps[0].recycle();
								tempBmps[0] = null;
							}
						}
						break;

					case FOUNDATION:
						PocoMakeUpV2.MakeUp_Foundation(bmp, FaceDataV2.RAW_POS_MULTI[faceIndex], (Integer)temp.m_ex, params[9]);
						break;

					default:
						break;
				}
			}
		}

		return out;
	}


	/**
	 * @param context
	 * @param bmp
	 * @param res
	 * @param params  [0]align1_1
	 *                [1]align4_3
	 *                [2]align16_9
	 *                [3]align3_4
	 *                [4]align9_16
	 *                [5]composite
	 *                [6]alpha
	 * @return
	 */
	public static Bitmap ConversionImgDecorate(Context context, Bitmap bmp, Object res, int... params)
	{
		Bitmap out = bmp;

		if(context != null && bmp != null && res != null)
		{
			Bitmap temp = LoadBitmap(context, res, bmp.getWidth(), bmp.getHeight());
			if(temp == null)
			{
				RuntimeException ex = new RuntimeException("MyLog--res does not exist! ConversionImgDecorate path:" + res);
				throw ex;
			}
			out = filter.ornamentComposition(bmp, temp, params[5], GetAlign(bmp.getWidth(), bmp.getHeight(), params), params[6]);
			temp.recycle();
			temp = null;
		}

		return out;
	}

	/**
	 * @param inW
	 * @param inH
	 * @param params [0]align1_1
	 *               [1]align4_3
	 *               [2]align16_9
	 *               [3]align3_4
	 *               [4]align9_16
	 * @return
	 */
	private static int GetAlign(int inW, int inH, int... params)
	{
		int out = -1;

		if(params != null && params.length >= 5)
		{
			float scale = (float)inW / (float)inH;
			float s1 = Math.abs(scale - 1);
			float s2 = Math.abs(scale - 4f / 3f);
			float s3 = Math.abs(scale - 16f / 9f);
			float s4 = Math.abs(scale - 3f / 4f);
			float s5 = Math.abs(scale - 9f / 16f);
			scale = Math.min(Math.min(Math.min(Math.min(s1, s2), s3), s4), s5);
			if(s1 == scale)
			{
				out = params[0];
			}
			else if(s2 == scale)
			{
				out = params[1];
			}
			else if(s3 == scale)
			{
				out = params[2];
			}
			else if(s4 == scale)
			{
				out = params[3];
			}
			else
			{
				out = params[4];
			}
		}

		return out;
	}

	public static Bitmap DelAcne(Context context, Bitmap bmp, ArrayList<PointData> acneDatas)
	{
		Bitmap out = bmp;

		if(acneDatas != null)
		{
			PointData temp;
			int len = acneDatas.size();
			if(len > 0)
			{
				float[] ps = new float[len << 1];
				float[] rs = new float[len];
				for(int i = 0; i < len; i++)
				{
					temp = acneDatas.get(i);
					ps[i << 1] = temp.m_x;
					ps[(i << 1) + 1] = temp.m_y;
					rs[i] = temp.m_r;
				}
				out = filter.remove_blemish_continuous(out, ps, rs, len);
			}
		}

		return out;
	}

	public static Bitmap Slim(Context context, Bitmap bmp, ArrayList<LineData> slimDatas)
	{
		Bitmap out = bmp;

		if(slimDatas != null)
		{
			LineData temp;
			int len = slimDatas.size();
			if(len > 0)
			{
				float[] rs = new float[len];
				float[] ps1 = new float[len << 1];
				float[] ps2 = new float[len << 1];
				for(int i = 0; i < len; i++)
				{
					temp = slimDatas.get(i);
					rs[i] = temp.m_r;
					ps1[i << 1] = temp.m_x;
					ps1[(i << 1) + 1] = temp.m_y;
					ps2[i << 1] = temp.m_x2;
					ps2[(i << 1) + 1] = temp.m_y2;
				}
				out = filter.manualThinBodyContinuous(out, rs, ps1, ps2, len);
			}
		}

		return out;
	}

	public static Bitmap SlimTool(Context context, Bitmap bmp, ArrayList<LineData> slimToolDatas)
	{
		Bitmap out = bmp;

		if(slimToolDatas != null)
		{
			LineData temp;
			int len = slimToolDatas.size();
			if(len > 0)
			{
				int[] rs = new int[len];
				float[] ps1 = new float[len << 1];
				float[] ps2 = new float[len << 1];
				int[] ss = new int[len];
				for(int i = 0; i < len; i++)
				{
					temp = slimToolDatas.get(i);
					rs[i] = (int)(temp.m_r * out.getWidth());
					ps1[i << 1] = temp.m_x;
					ps1[(i << 1) + 1] = temp.m_y;
					ps2[i << 1] = temp.m_x2;
					ps2[(i << 1) + 1] = temp.m_y2;
					ss[i] = 4;
				}
				out = filter.liquefyContinuous(out, rs, ps1, ps2, ss, len);
			}
		}

		return out;
	}


}
