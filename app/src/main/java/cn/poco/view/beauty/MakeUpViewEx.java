package cn.poco.view.beauty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.face.FaceDataV2;
import cn.poco.resource.MakeupType;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;

import static cn.poco.face.FaceDataV2.FACE_POS_MULTI;

/**
 * 彩妆 -- 没有人脸定点
 * Created by admin on 2017/2/13.
 */

public class MakeUpViewEx extends BeautyCommonViewEx
{
	protected long m_showRectFlagTime;
	public int m_showRectFlagInterval = 300;
	protected ArrayList<RectF> m_rectFlags = new ArrayList<>();

	public MakeUpViewEx(Context context, ControlCallback cb)
	{
		super(context);
		mCallBack = cb;
		m_faceIndex = 0;
	}

	@Override
	protected void InitData()
	{
		super.InitData();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(m_rectFlags != null && m_rectFlags.size()>0)
		{
			canvas.save();
			DrawRectFlag(canvas);
			canvas.restore();
		}
	}

	/**
	 * 画五官位置矩形框
	 *
	 * @param canvas
	 */
	protected void DrawRectFlag(Canvas canvas)
	{
		long ctime = System.currentTimeMillis();
		if(m_showRectFlagTime > ctime)
		{
			if(((m_showRectFlagTime - ctime) / m_showRectFlagInterval) % 2 == 0)
			{
				mPaint.reset();
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setColor(0xFFFFFFFF);
				mPaint.setStrokeCap(Paint.Cap.SQUARE);
				mPaint.setStrokeJoin(Paint.Join.MITER);

				int len = m_rectFlags.size();
				if(len > 0)
				{
					RectF temp_rect = new RectF();
					temp_rect.set(m_rectFlags.get(0));

					temp_rect = ChangeRatioToShow(temp_rect);// 百分比矩形 --> 屏幕坐标矩形

					float r = (temp_rect.width() > temp_rect.height() ? temp_rect.width() : temp_rect.height()) / 8f;
					float r1 = temp_rect.width() / 3f;
					float r2 = temp_rect.height() / 3f;
					if(r > r1)
					{
						r = r1;
					}
					if(r > r2)
					{
						r = r2;
					}
					if(r < 1)
					{
						r = 1;
					}
					float rw = r;
					float rh = r;
					float bw = r / 10f;
					if(bw < 1)
					{
						bw = 1;
					}
					mPaint.setStrokeWidth(bw);

					for(int i = 0; i < len; i++)
					{
						temp_rect.set(m_rectFlags.get(i));
						temp_rect = ChangeRatioToShow(temp_rect);

						canvas.drawLine(temp_rect.left, temp_rect.top, temp_rect.left + rw, temp_rect.top, mPaint);
						canvas.drawLine(temp_rect.left, temp_rect.top, temp_rect.left, temp_rect.top + rh, mPaint);
						canvas.drawLine(temp_rect.right, temp_rect.top, temp_rect.right - rw, temp_rect.top, mPaint);
						canvas.drawLine(temp_rect.right, temp_rect.top, temp_rect.right, temp_rect.top + rh, mPaint);
						canvas.drawLine(temp_rect.right, temp_rect.bottom, temp_rect.right - rw, temp_rect.bottom, mPaint);
						canvas.drawLine(temp_rect.right, temp_rect.bottom, temp_rect.right, temp_rect.bottom - rh, mPaint);
						canvas.drawLine(temp_rect.left, temp_rect.bottom, temp_rect.left + rw, temp_rect.bottom, mPaint);
						canvas.drawLine(temp_rect.left, temp_rect.bottom, temp_rect.left, temp_rect.bottom - rh, mPaint);
					}
				}
			}
			this.invalidate();
		}
	}

	public void SetShowRectFlag()
	{
		m_showRectFlagTime = System.currentTimeMillis() + m_showRectFlagInterval * 3;
		this.invalidate();
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		//判断点中哪个部位
		if(m_isClick && mCallBack != null && m_operateMode != MODE_MAKEUP && m_operateMode == BeautyCommonViewEx.MODE_NORMAL) // 单击事件才会响应
		{
			if(FaceDataV2.RAW_POS_MULTI != null)
			{
				ArrayList<RectF> tempArr = new ArrayList<>();
				MakeupType type = GetClickFacePos(event.getX(), event.getY(), tempArr, false);
				if(type != null)
				{
					switch(type)
					{
						case EYEBROW_L:
							m_rectFlags.clear();
							AddEyebrowRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchEyebrow(true);
							break;

						case EYEBROW_R:
							m_rectFlags.clear();
							AddEyebrowRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchEyebrow(false);
							break;

						case EYE_L:
							m_rectFlags.clear();
							AddEyeRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchEye(true);
							break;

						case EYE_R:
							m_rectFlags.clear();
							AddEyeRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchEye(false);
							break;

						case LIP:
							m_rectFlags.clear();
							AddLipRectFlag(m_rectFlags, tempArr.get(0));
							((ControlCallback)mCallBack).OnTouchLip();
							break;

						case CHEEK_L:
							m_rectFlags.clear();
							AddCheckRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchCheek(true);
							break;

						case CHEEK_R:
							m_rectFlags.clear();
							AddCheckRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1));
							((ControlCallback)mCallBack).OnTouchCheek(false);
							break;

						case FOUNDATION:
							m_rectFlags.clear();
							AddFoundationRectFlag(m_rectFlags, tempArr.get(0));
							((ControlCallback)mCallBack).OnTouchFoundation();
							break;

						default:
							break;
					}
				}
			}
		}

		if (mCallBack != null)
		{
			((ControlCallback)mCallBack).onFingerUp();
		}

		super.OddUp(event);
		this.invalidate();
	}

	/**
	 * 获取眉毛最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 */
	protected void AddEyebrowRectFlag(ArrayList<RectF> in_out, RectF l, RectF r)
	{
		if(in_out != null && FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取眼睛最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 */
	protected void AddEyeRectFlag(ArrayList<RectF> in_out, RectF l, RectF r)
	{
		if(in_out != null && FaceDataV2.EYE_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取腮红最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 */
	protected void AddCheckRectFlag(ArrayList<RectF> in_out, RectF l, RectF r)
	{
		if(in_out != null && FaceDataV2.CHEEK_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4);
			}
			in_out.add(temp);
		}
	}

	protected void AddFoundationRectFlag(ArrayList<RectF> in_out, RectF foundation)
	{
		if(in_out != null && FACE_POS_MULTI != null)
		{
			RectF temp;
			if(foundation != null)
			{
				temp = foundation;
			}
			else
			{
				float[] pos = FACE_POS_MULTI[m_faceIndex];
				temp = new RectF(pos[6], pos[7], pos[6] + pos[8], pos[7] + pos[9]);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取嘴唇最小矩形
	 *
	 * @param in_out
	 * @param lip
	 *            可以为null,为null时会重新获取
	 */
	protected void AddLipRectFlag(ArrayList<RectF> in_out, RectF lip)
	{
		if(in_out != null && FaceDataV2.LIP_POS_MULTI != null)
		{
			RectF temp;
			if(lip != null)
			{
				temp = lip;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 计算点与矩形中心是否在一定范围内
	 * @param pointF 点
	 * @param rectF 矩形
	 * @return <=0 在一定范围内， >0 不在范围内
	 */
	private float computePtsWithRect(PointF pointF, RectF rectF)
	{
		float rect_cen_x = (rectF.left + rectF.right) / 2f;
		float rect_cen_y = (rectF.top + rectF.bottom) / 2f;
		// 以矩形中心点为圆心，中心点到四个角的距离为半径 radius
		float radius = ImageUtils.Spacing(rectF.left - rect_cen_x, rectF.top - rect_cen_y);
		// 计算 pts 与 圆心距离 distance
		float distance = ImageUtils.Spacing(pointF.x - rect_cen_x, pointF.y - rect_cen_y);
		// 比较 dis 与 radius 距离
		return distance - radius;
	}

	/**
	 * 判断数组所有数据是否都大于零
	 * @param date
	 * @return
	 */
	private boolean AllMoreThanZero(float[] date)
	{
		for(float f: date)
		{
			if(f < 0)
			{
				return false;
			}
		}
		return true;
	}

	private int getClickIndex(float[] date, boolean mtz)
	{
		int index = -1;
		int len = date.length - 1;
		if(mtz)
		{
			for(int i=0;i<len;i++)
			{
				float f = date[i];
				if(f <= ShareData.PxToDpi_xhdpi(20))
				{
					if(index <= -1)
					{
						index = i;
					}
					else
					{
						if(f < date[index])
						{
							index = i;
						}
					}
				}
			}
		}
		else
		{
			for(int i=0;i<len;i++)
			{
				float f = date[i];
				if(f <= 0)
				{
					if(index == -1)
					{
						index = i;
					}
					else
					{
						if(f < date[index])
						{
							index = i;
						}
					}
				}
			}
		}
		return index;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param outArr
	 *            输出矩形,未必全,可能只有一边
	 * @param clickRect
	 *            点击矩形还是显示矩形
	 * @return
	 */
	protected MakeupType GetClickFacePos(float x, float y, ArrayList<RectF> outArr, boolean clickRect)
	{
		MakeupType out = null;

		if(FaceDataV2.RAW_POS_MULTI != null && m_faceIndex != -1)
		{
			PointF pointF = new PointF(x,y);

			float[] distance = new float[8];
			// 左眉毛
			RectF rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8);
			RectF showRect = ChangeRatioToShow(rect);
			distance[0] = computePtsWithRect(pointF,showRect);

			// 右眉毛
			rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8);
			showRect = ChangeRatioToShow(rect);
			distance[1] = computePtsWithRect(pointF,showRect);

			// 左眼
			rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10);
			showRect = ChangeRatioToShow(rect);
			distance[2] = computePtsWithRect(pointF,showRect);

			// 右眼
			rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10);
			showRect = ChangeRatioToShow(rect);
			distance[3] = computePtsWithRect(pointF,showRect);

			// 左脸颊
			rect = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4);
			showRect = ChangeRatioToShow(rect);
			distance[4] = computePtsWithRect(pointF,showRect);

			// 右脸颊
			rect = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4);
			showRect = ChangeRatioToShow(rect);
			distance[5] = computePtsWithRect(pointF,showRect);

			// 嘴唇
			rect = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
			showRect = ChangeRatioToShow(rect);
			distance[6] = computePtsWithRect(pointF,showRect);

			// 粉底
			float[] pos = FACE_POS_MULTI[m_faceIndex];
			rect = new RectF(pos[6], pos[7], pos[6] + pos[8], pos[7] + pos[9]);
			showRect = ChangeRatioToShow(rect);
			distance[7] = computePtsWithRect(pointF,showRect);

			// 至少在粉底范围内
			if(distance[7] <= 0)
			{
				boolean mtz = AllMoreThanZero(distance);

				int index = getClickIndex(distance,mtz);

				if(index <= -1)
				{
					// 粉底
					out = MakeupType.FOUNDATION;
					if(outArr != null)
					{
						outArr.add(rect);
					}
				}
				else
				{
					switch(index)
					{
						// 眉毛
						case 0:
							out = MakeupType.EYEBROW_L;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8));
								outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8));
							}
							break;
						case 1:
							out = MakeupType.EYEBROW_R;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8));
								outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8));
							}
							break;
						// 眼睛
						case 2:
							out = MakeupType.EYE_L;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10));
								outArr.add(GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10));
							}
							break;
						case 3:
							out = MakeupType.EYE_R;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10));
								outArr.add(GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10));
							}
							break;
						// 脸颊
						case 4:
							out = MakeupType.CHEEK_L;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4));
								outArr.add(GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4));
							}
							break;
						case 5:
							out = MakeupType.CHEEK_R;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4));
								outArr.add(GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4));
							}
							break;
						// 嘴唇
						case 6:
							out = MakeupType.LIP;
							if(outArr != null)
							{
								outArr.add(GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32));
							}
							break;
					}
				}
			}
		}
		return out;
	}

	public RectF GetMinRect(float[] pos, int startIndex, int len)
	{
		return GetMinRect(pos, startIndex, len, null);
	}

	/**
	 * @param pos
	 * @param startIndex
	 * @param len
	 * @param recycle
	 *            重复利用对象,可为null
	 * @return
	 */
	public RectF GetMinRect(float[] pos, int startIndex, int len, RectF recycle)
	{
		RectF out;
		if(recycle != null)
		{
			out = recycle;
			out.setEmpty();
		}
		else
		{
			out = new RectF();
		}

		if(pos != null && pos.length >= startIndex + len)
		{
			len = len / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				if(i == 0)
				{
					out.left = pos[startIndex + i];
					out.right = pos[startIndex + i];
					out.top = pos[startIndex + i + 1];
					out.bottom = pos[startIndex + i + 1];
				}
				else
				{
					if(out.left > pos[startIndex + i])
					{
						out.left = pos[startIndex + i];
					}
					else if(out.right < pos[startIndex + i])
					{
						out.right = pos[startIndex + i];
					}
					if(out.top > pos[startIndex + i + 1])
					{
						out.top = pos[startIndex + i + 1];
					}
					else if(out.bottom < pos[startIndex + i + 1])
					{
						out.bottom = pos[startIndex + i + 1];
					}
				}
			}
		}

		return out;
	}

	/**
	 * @param ratioRect 以图片为基础的 百分比矩形
	 * @return 屏幕坐标系的矩形
	 */
	protected RectF ChangeRatioToShow(RectF ratioRect)
	{
		if(ratioRect == null)
		{
			return null;
		}

		RectF curImgShowRect = getCurImgShowRect();

		RectF showRect = new RectF();
		showRect.left = curImgShowRect.left + curImgShowRect.width() * ratioRect.left;
		showRect.right = curImgShowRect.left + curImgShowRect.width() * ratioRect.right;
		showRect.top = curImgShowRect.top + curImgShowRect.height() * ratioRect.top;
		showRect.bottom = curImgShowRect.top + curImgShowRect.height() * ratioRect.bottom;

		return showRect;
	}

	public interface ControlCallback extends BeautyCommonViewEx.ControlCallback
	{
		void OnTouchEyebrow(boolean isLeft);

		void OnTouchEye(boolean isLeft);

		void OnTouchCheek(boolean isLeft);

		void OnTouchLip();

		void OnTouchFoundation();

		void UpdateSonWin(Bitmap bitmap, float x, float y);

		void On3PosModify();

		void OnAllPosModify();

		void onTouchWatermark();

		void onFingerUp();
	}
}