package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import cn.poco.beautify.BeautifyView2;
import cn.poco.face.FaceDataV2;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


public class BeautifyView3 extends BeautifyView2 {

//    public static final int POS_EYE_L = 0x0020;//左眼
//    public static final int POS_EYE_R = 0x0040;//右眼

    public boolean m_isMovePoint = false;

    public BeautifyView3(Context context, int frW, int frH) {
        super(context, frW, frH);
    }

    @Override
    protected void DrawPoint(Canvas canvas) {
        if((m_showPosFlag & POS_THREE) != 0)
        {
            if(m_facePos != null)
            {
                int len = m_facePos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_facePos.get(i));
                }
            }
        }
        if((m_showPosFlag & POS_EYEBROW) != 0)
        {
            if(m_leyebrowPos != null)
            {
                int len = m_leyebrowPos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_leyebrowPos.get(i));
                }
            }
            if(m_reyebrowPos != null)
            {
                int len = m_reyebrowPos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_reyebrowPos.get(i));
                }
            }
        }
        if((m_showPosFlag & POS_EYE) != 0)
        {
            if(m_leyePos != null)
            {
                int len = m_leyePos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_leyePos.get(i));
                }
            }

            if(m_reyePos != null)
            {
                int len = m_reyePos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_reyePos.get(i));
                }
            }
        }
        if((m_showPosFlag & POS_CHEEK) != 0)
        {
            if(m_cheekPos != null)
            {
                int len = m_cheekPos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_cheekPos.get(i));
                }
            }
        }
        if((m_showPosFlag & POS_LIP) != 0)
        {
            if(m_lipPos != null)
            {
                int len = m_lipPos.size();
                for(int i = 0; i < len; i++)
                {
                    DrawButton2(canvas, m_lipPos.get(i));
                }
            }
        }
    }

    public void DoAnim2EyeL()
    {
        RectF rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 20);
        ZoomRect(rect, 1.2f);
        DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
    }


    public void DoAnim2EyeR()
    {
        RectF rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 20);
        ZoomRect(rect, 1.2f);
        DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
    }

    @Override
    protected void DrawButton2(Canvas canvas, ShapeEx item) {
        if(item == m_target && m_isMovePoint && canvas == m_tempSonWinCanvas)
        {
            return;
        }
        if(item != null)
        {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
//            if((m_moveAllFacePos || m_target == item) && temp_color_filter != null)
//            {
//                temp_paint.setColorFilter(temp_color_filter);
//            }
            GetShowMatrixNoScale(temp_matrix, item);
            canvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);
        }
    }

    @Override
    protected void OddDown(MotionEvent event) {
        super.OddDown(event);

//        //只显示左眼，右眼定点不能移动
//        if(m_showPosFlag == POS_EYE_L)
//        {
//            if(m_reyePos != null && m_reyePos.size() > 0)
//            {
//                for(int i = 0;i < m_reyePos.size(); i++)
//                {
//                    if(m_target == m_reyePos.get(i))
//                    {
//                        m_target = m_origin;
//                        Init_M_Data(m_target, m_downX, m_downY);
//                        break;
//                    }
//                }
//            }
//        } //只显示右眼，左眼定点不能移动
//        else if(m_showPosFlag == POS_EYE_R)
//        {
//            if(m_leyePos != null && m_leyePos.size() > 0)
//            {
//                for(int i = 0;i < m_leyePos.size(); i++)
//                {
//                    if(m_target == m_leyePos.get(i))
//                    {
//                        m_target = m_origin;
//                        Init_M_Data(m_target, m_downX, m_downY);
//                        break;
//                    }
//                }
//            }
//        }
    }

    public int getOperateMode()
    {
        return m_operateMode;
    }


    @Override
    protected void DrawToSonWin2(ShapeEx item)
    {
        if(item != null && m_img != null && m_img.m_bmp != null && !m_img.m_bmp.isRecycled() && item != m_origin)
        {
            m_isMovePoint = true;
            m_sonWinRadius = (int) (ShareData.PxToDpi_xhdpi(180)/2f);
            int size = m_sonWinRadius * 2;
            int offset = ShareData.PxToDpi_xhdpi(10);
            int border = ShareData.PxToDpi_xhdpi(5);

            if(m_sonWinBmp == null || m_sonWinCanvas == null)
            {
                if(m_sonWinBmp != null)
                {
                    m_sonWinBmp.recycle();
                    m_sonWinBmp = null;
                }
                m_sonWinBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                m_sonWinCanvas = new Canvas(m_sonWinBmp);
                m_sonWinCanvas.setDrawFilter(temp_filter);
            }
            if(m_tempSonWinBmp == null)
            {
                m_tempSonWinBmp = m_sonWinBmp.copy(Bitmap.Config.ARGB_8888, true);
                m_tempSonWinCanvas = new Canvas(m_tempSonWinBmp);
                m_tempSonWinCanvas.setDrawFilter(temp_filter);
            }

            //清理
            m_sonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            m_tempSonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

            //画图到临时
            float[] src = {item.m_x + item.m_centerX, item.m_y + item.m_centerY};
            float[] dst = new float[2];
            GetShowPos(dst, src);
            m_tempSonWinCanvas.save();
            m_tempSonWinCanvas.translate(-dst[0] + m_sonWinRadius, -dst[1] + m_sonWinRadius);
            DrawToCanvas(m_tempSonWinCanvas, m_operateMode);
            m_tempSonWinCanvas.restore();

            //draw mask
            temp_paint.reset();
            temp_paint.setStyle(Paint.Style.FILL);
            temp_paint.setColor(0xFFFFFFFF);
            temp_paint.setAntiAlias(true);
            m_sonWinCanvas.drawRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, temp_paint);

            //临时画到sonWin
            temp_paint.reset();
            temp_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            temp_paint.setFilterBitmap(true);
            m_sonWinCanvas.drawBitmap(m_tempSonWinBmp, 0, 0, temp_paint);


            //画中间标记
            temp_paint.reset();
            temp_paint.setFilterBitmap(true);
            Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_changepoint_sonwindow_center_icon);
            int startX = (size - ShareData.PxToDpi_xhdpi(62))/2;
            int startY = (size - ShareData.PxToDpi_xhdpi(62))/2;
            RectF rectF = new RectF(startX,startY,startX + ShareData.PxToDpi_xhdpi(62),startY + ShareData.PxToDpi_xhdpi(62));
            m_sonWinCanvas.drawBitmap(temp,null,rectF,temp_paint);


//            //画白边
//            temp_paint.reset();
//            temp_paint.setStyle(Paint.Style.FILL);
//            temp_paint.setColor(0xA0FFFFFF);
//            temp_paint.setAntiAlias(true);
//            temp_paint.setFilterBitmap(true);
//            temp_path.reset();
//            temp_path.setFillType(Path.FillType.EVEN_ODD);
//            temp_path.addRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, Path.Direction.CW);
//            temp_path.addRoundRect(new RectF(offset + border, offset + border, size - offset - border, size - offset - border), border << 1, border << 1, Path.Direction.CW);
//            m_sonWinCanvas.drawPath(temp_path, temp_paint);
        }
        else
        {
            m_sonWinBmp = null;
        }
    }

    private float[] m_face_pos;
    private float[] m_face_eyebrow_pos;
    private float[] m_eye_pos;
    private float[] m_cheek_pos;
    private float[] m_lip_pos;
    private float[] m_raw_face;
    private float[] m_raw_all;


    //开始定点时复制一份脸部数据
    public void copyFaceData()
    {
        if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI[m_faceIndex] != null)
        {
            m_face_pos = FaceDataV2.FACE_POS_MULTI[m_faceIndex].clone();
        }
        if(FaceDataV2.EYEBROW_POS_MULTI != null && FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex] != null)
        {
            m_face_eyebrow_pos = FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex].clone();
        }
        if(FaceDataV2.EYE_POS_MULTI != null && FaceDataV2.EYE_POS_MULTI[m_faceIndex] != null)
        {
           m_eye_pos = FaceDataV2.EYE_POS_MULTI[m_faceIndex].clone();
        }
        if(FaceDataV2.CHEEK_POS_MULTI != null && FaceDataV2.CHEEK_POS_MULTI[m_faceIndex] != null)
        {
           m_cheek_pos = FaceDataV2.CHEEK_POS_MULTI[m_faceIndex].clone();
        }
        if(FaceDataV2.LIP_POS_MULTI != null && FaceDataV2.LIP_POS_MULTI[m_faceIndex] != null)
        {
           m_lip_pos = FaceDataV2.LIP_POS_MULTI[m_faceIndex].clone();
        }

        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[m_faceIndex] != null)
        {
            m_raw_face = FaceDataV2.RAW_POS_MULTI[m_faceIndex].getFaceRect().clone();
        }

        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[m_faceIndex] != null)
        {
            m_raw_all = FaceDataV2.RAW_POS_MULTI[m_faceIndex].getFaceFeaturesMakeUp().clone();
        }
    }

    //定点取消时恢复数据
    public void reSetFaceData()
    {
        if(m_face_pos != null)
        {
            FaceDataV2.FACE_POS_MULTI[m_faceIndex] = m_face_pos;
        }
        if(m_face_eyebrow_pos != null)
        {
            FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex] = m_face_eyebrow_pos;
        }
        if(m_eye_pos != null)
        {
            FaceDataV2.EYE_POS_MULTI[m_faceIndex] = m_eye_pos;
        }
        if(m_cheek_pos != null)
        {
            FaceDataV2.CHEEK_POS_MULTI[m_faceIndex] = m_cheek_pos;
        }
        if(m_lip_pos != null)
        {
            FaceDataV2.LIP_POS_MULTI[m_faceIndex] = m_lip_pos;
        }

        if(m_raw_face != null)
        {
            FaceDataV2.RAW_POS_MULTI[m_faceIndex].setFaceRect(m_raw_face);
        }

        if(FaceDataV2.RAW_POS_MULTI != null)
        {
            FaceDataV2.RAW_POS_MULTI[m_faceIndex].setMakeUpFeatures(m_raw_all);
        }
    }

}
