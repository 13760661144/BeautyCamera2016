package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.poco.glfilter.shape.ShapeInfoData;

/**
 * 鼻子变形V2版   瘦鼻
 */
public class CrazyNoseShapeFilterV2 extends CrazyTriShapeFilter {

    private float mNoseStrength = 0f;  //调节力度
    private float mNoseRadius = 0f;    //暂时没用

    public CrazyNoseShapeFilterV2(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;
        this.mNoseRadius = shapeData.noseRadius;
        this.mNoseStrength = shapeData.noseStrength;

        if (mNoseStrength < 0f) {
            mNoseStrength = 0f;
        } else if (mNoseStrength > 100.f) {
            mNoseStrength = 100.f;
        }
        mNoseStrength = (mNoseStrength * 0.18f) / 100.0f;
    }

    @Override
    public boolean setIndexBuffer() {
        short[] index =
                {
                        0, 1, 3,
                        1, 2, 3,
                        2, 3, 4,
                        4, 2, 7,
                        4, 7, 9,
                        4, 8, 9,
                        14, 4, 8,
                        16, 3, 4,
                        16, 18, 4,
                        18, 19, 4,
                        19, 20, 4,
                        20, 14, 4,

                        16, 12, 3,
                        3, 12, 0,

                        0, 1, 5,
                        1, 2, 5,
                        2, 5, 6,
                        2, 6, 7,
                        6, 7, 10,
                        6, 11, 10,
                        15, 6, 11,

                        17, 5, 6,
                        17, 21, 6,
                        21, 22, 6,
                        22, 23, 6,
                        23, 15, 6,

                        17, 13, 5,
                        5, 13, 0
                };

        if (drawIndex == null) {

            mIndexLength = index.length;  //
            drawIndex = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder());
            drawIndex.asShortBuffer().put(index);
        }

        drawIndex.position(0);
        return true;
    }

    @Override
    protected float[] getSrcPoint(PointF[] points) {
        int idx[] = {43, 45, 46, 80, 81, 82, 83, 49, 73, 76, 6, 7, 8, 9, 26, 25, 24, 23, 8, 24, 84, 85, 89, 90, 4, 28, 5, 6, 7, 27, 26, 25};

        float ret[] = new float[24 * 2];

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF _t_43_p = idxPoint[0];
        PointF _t_45_p = idxPoint[1];
        PointF _t_46_p = idxPoint[2];

        PointF _t_80_p = idxPoint[3];
        PointF _t_81_p = idxPoint[4];
        PointF _t_82_p = idxPoint[5];
        PointF _t_83_p = idxPoint[6];
        PointF _t_49_p = idxPoint[7];

        PointF _t_73_p = idxPoint[8];
        PointF _t_76_p = idxPoint[9];

        PointF _t_6_p = idxPoint[10];
        PointF _t_7_p = idxPoint[11];
        PointF _t_8_p = idxPoint[12];
        PointF _t_9_p = idxPoint[13];

        PointF _t_26_p = idxPoint[14];
        PointF _t_25_p = idxPoint[15];
        PointF _t_24_p = idxPoint[16];
        PointF _t_23_p = idxPoint[17];

        float _t_left_face_ref_p_x = (_t_6_p.x + _t_7_p.x + _t_8_p.x + _t_9_p.x) * 0.25f;
        float _t_left_face_ref_p_y = (_t_6_p.y + _t_7_p.y + _t_8_p.y + _t_9_p.y) * 0.25f;
        PointF _t_left_face_ref_p = new PointF(_t_left_face_ref_p_x, _t_left_face_ref_p_y);

        float _t_right_face_ref_p_x = (_t_26_p.x + _t_25_p.x + _t_24_p.x + _t_23_p.x) * 0.25f;
        float _t_right_face_ref_p_y = (_t_26_p.y + _t_25_p.y + _t_24_p.y + _t_23_p.y) * 0.25f;
        PointF _t_right_face_ref_p = new PointF(_t_right_face_ref_p_x, _t_right_face_ref_p_y);

        float _t_left_face_center_p_x = _t_73_p.x + (_t_left_face_ref_p.x - _t_73_p.x) * 0.7f;
        float _t_left_face_center_p_y = _t_73_p.y + (_t_left_face_ref_p.y - _t_73_p.y) * 0.7f;
        float _t_right_face_center_p_x = _t_76_p.x + (_t_right_face_ref_p.x - _t_76_p.x) * 0.7f;
        float _t_right_face_center_p_y = _t_76_p.y + (_t_right_face_ref_p.y - _t_76_p.y) * 0.7f;

        PointF _t_left_face_center_p = new PointF(_t_left_face_center_p_x, _t_left_face_center_p_y);
        PointF _t_right_face_center_p = new PointF(_t_right_face_center_p_x, _t_right_face_center_p_y);

        _t_left_face_center_p = idxPoint[18];
        _t_right_face_center_p = idxPoint[19];

        PointF _t_offset_nose_ref_left_0 = new PointF(_t_46_p.x + (_t_80_p.x - _t_46_p.x) * 1.7f,
                _t_46_p.y + (_t_80_p.y - _t_46_p.y) * 1.7f);
        PointF _t_offset_nose_ref_right_0 = new PointF(_t_46_p.x + (_t_81_p.x - _t_46_p.x) * 1.7f,
                _t_46_p.y + (_t_81_p.y - _t_46_p.y) * 1.7f);

        float _t_offset_nose_left_0_x = _t_46_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_left_0_y = _t_46_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y) * 1.0f;
        float _t_offset_nose_right_0_x = _t_46_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_right_0_y = _t_46_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y) * 1.0f;

        float _t_offset_nose_left_1_x = _t_82_p.x;
        float _t_offset_nose_left_1_y = _t_82_p.y;
        float _t_offset_nose_right_1_x = _t_83_p.x;
        float _t_offset_nose_right_1_y = _t_83_p.y;

        PointF _t_84_p = idxPoint[20];
        PointF _t_85_p = idxPoint[21];
        PointF _t_89_p = idxPoint[22];
        PointF _t_90_p = idxPoint[23];

        float offset_left_eyelid_p_x = _t_73_p.x + (_t_left_face_center_p.x - _t_73_p.x) * 0.1f;
        float offset_left_eyelid_p_y = _t_73_p.y + (_t_left_face_center_p.y - _t_73_p.y) * 0.1f;
        float offset_right_eyelid_p_x = _t_76_p.x + (_t_right_face_center_p.x - _t_76_p.x) * 0.1f;
        float offset_right_eyelid_p_y = _t_76_p.y + (_t_right_face_center_p.y - _t_76_p.y) * 0.1f;

        PointF face_polar_left = idxPoint[24];
        PointF face_polar_right = idxPoint[25];

        ret[0] = _t_43_p.x;
        ret[1] = _t_43_p.y;
        ret[2] = _t_45_p.x;
        ret[3] = _t_45_p.y;
        ret[4] = _t_46_p.x;
        ret[5] = _t_46_p.y;

        ret[6] = _t_offset_nose_left_0_x;
        ret[7] = _t_offset_nose_left_0_y;
        ret[8] = _t_offset_nose_left_1_x;
        ret[9] = _t_offset_nose_left_1_y;
        ret[10] = _t_offset_nose_right_0_x;
        ret[11] = _t_offset_nose_right_0_y;
        ret[12] = _t_offset_nose_right_1_x;
        ret[13] = _t_offset_nose_right_1_y;

        ret[14] = _t_49_p.x;
        ret[15] = _t_49_p.y;

        ret[16] = _t_84_p.x;
        ret[17] = _t_84_p.y;
        ret[18] = _t_85_p.x;
        ret[19] = _t_85_p.y;
        ret[20] = _t_89_p.x;
        ret[21] = _t_89_p.y;
        ret[22] = _t_90_p.x;
        ret[23] = _t_90_p.y;

        ret[24] = offset_left_eyelid_p_x;
        ret[25] = offset_left_eyelid_p_y;
        ret[26] = offset_right_eyelid_p_x;
        ret[27] = offset_right_eyelid_p_y;

        ret[28] = _t_left_face_center_p.x;
        ret[29] = _t_left_face_center_p.y;
        ret[30] = _t_right_face_center_p.x;
        ret[31] = _t_right_face_center_p.y;

        ret[32] = face_polar_left.x;
        ret[33] = face_polar_left.y;
        ret[34] = face_polar_right.x;
        ret[35] = face_polar_right.y;

        ret[36] = idxPoint[26].x;
        ret[37] = idxPoint[26].y;
        ret[38] = idxPoint[27].x;
        ret[39] = idxPoint[27].y;
        ret[40] = idxPoint[28].x;
        ret[41] = idxPoint[28].y;

        ret[42] = idxPoint[29].x;
        ret[43] = idxPoint[29].y;
        ret[44] = idxPoint[30].x;
        ret[45] = idxPoint[30].y;
        ret[46] = idxPoint[31].x;
        ret[47] = idxPoint[31].y;

        for (int i = 0; i < 24; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {
        int idx[] = {43, 45, 46, 80, 81, 82, 83, 49, 73, 76, 6, 7, 8, 9, 26, 25, 24, 23, 8, 24, 84, 85, 89, 90, 4, 28, 5, 6, 7, 27, 26, 25};

        float ret[] = new float[24 * 2];

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF _t_43_p = idxPoint[0];
        PointF _t_45_p = idxPoint[1];
        PointF _t_46_p = idxPoint[2];

        PointF _t_80_p = idxPoint[3];
        PointF _t_81_p = idxPoint[4];
        PointF _t_82_p = idxPoint[5];
        PointF _t_83_p = idxPoint[6];
        PointF _t_49_p = idxPoint[7];

        PointF _t_73_p = idxPoint[8];
        PointF _t_76_p = idxPoint[9];

        PointF _t_6_p = idxPoint[10];
        PointF _t_7_p = idxPoint[11];
        PointF _t_8_p = idxPoint[12];
        PointF _t_9_p = idxPoint[13];

        PointF _t_26_p = idxPoint[14];
        PointF _t_25_p = idxPoint[15];
        PointF _t_24_p = idxPoint[16];
        PointF _t_23_p = idxPoint[17];

        float _t_left_face_ref_p_x = (_t_6_p.x + _t_7_p.x + _t_8_p.x + _t_9_p.x) * 0.25f;
        float _t_left_face_ref_p_y = (_t_6_p.y + _t_7_p.y + _t_8_p.y + _t_9_p.y) * 0.25f;
        PointF _t_left_face_ref_p = new PointF(_t_left_face_ref_p_x, _t_left_face_ref_p_y);

        float _t_right_face_ref_p_x = (_t_26_p.x + _t_25_p.x + _t_24_p.x + _t_23_p.x) * 0.25f;
        float _t_right_face_ref_p_y = (_t_26_p.y + _t_25_p.y + _t_24_p.y + _t_23_p.y) * 0.25f;
        PointF _t_right_face_ref_p = new PointF(_t_right_face_ref_p_x, _t_right_face_ref_p_y);

        float _t_left_face_center_p_x = _t_73_p.x + (_t_left_face_ref_p.x - _t_73_p.x) * 0.7f;
        float _t_left_face_center_p_y = _t_73_p.y + (_t_left_face_ref_p.y - _t_73_p.y) * 0.7f;
        float _t_right_face_center_p_x = _t_76_p.x + (_t_right_face_ref_p.x - _t_76_p.x) * 0.7f;
        float _t_right_face_center_p_y = _t_76_p.y + (_t_right_face_ref_p.y - _t_76_p.y) * 0.7f;

        PointF _t_left_face_center_p = new PointF(_t_left_face_center_p_x, _t_left_face_center_p_y);
        PointF _t_right_face_center_p = new PointF(_t_right_face_center_p_x, _t_right_face_center_p_y);

        _t_left_face_center_p = idxPoint[18];
        _t_right_face_center_p = idxPoint[19];


        PointF _t_84_p = idxPoint[20];
        PointF _t_85_p = idxPoint[21];
        PointF _t_89_p = idxPoint[22];
        PointF _t_90_p = idxPoint[23];

        float offset_left_eyelid_p_x = _t_73_p.x + (_t_left_face_center_p.x - _t_73_p.x) * 0.1f;
        float offset_left_eyelid_p_y = _t_73_p.y + (_t_left_face_center_p.y - _t_73_p.y) * 0.1f;
        float offset_right_eyelid_p_x = _t_76_p.x + (_t_right_face_center_p.x - _t_76_p.x) * 0.1f;
        float offset_right_eyelid_p_y = _t_76_p.y + (_t_right_face_center_p.y - _t_76_p.y) * 0.1f;

        PointF face_polar_left = idxPoint[24];
        PointF face_polar_right = idxPoint[25];

        PointF _t_offset_nose_ref_left_0 = new PointF(_t_46_p.x + (_t_80_p.x - _t_46_p.x) * 1.7f,
                _t_46_p.y + (_t_80_p.y - _t_46_p.y) * 1.7f);
        PointF _t_offset_nose_ref_right_0 = new PointF(_t_46_p.x + (_t_81_p.x - _t_46_p.x) * 1.7f,
                _t_46_p.y + (_t_81_p.y - _t_46_p.y) * 1.7f);

        float _t_offset_nose_left_0_x = _t_46_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_left_0_y = _t_46_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y) * 1.0f;
        float _t_offset_nose_right_0_x = _t_46_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_right_0_y = _t_46_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y) * 1.0f;

        float _t_offset_nose_left_1_x = _t_82_p.x;
        float _t_offset_nose_left_1_y = _t_82_p.y;
        float _t_offset_nose_right_1_x = _t_83_p.x;
        float _t_offset_nose_right_1_y = _t_83_p.y;


        float _t_offset_nose_0_l_strength = mNoseStrength;
        float _t_offset_nose_0_r_strength = mNoseStrength;
        float _t_offset_nose_1_l_strength = mNoseStrength * 0.8f;
        float _t_offset_nose_1_r_strength = mNoseStrength * 0.8f;

        //排除异常情况
        //==================================== 左脸 ==================================//
        PointF _t_5_p = new PointF(points[5].x * mWidth, points[5].y * mHeight);
        PointF _t_27_p = new PointF(points[27].x * mWidth, points[27].y * mHeight);

        PointF[] nose_left_ranges = new PointF[11];

        nose_left_ranges[0] = new PointF(offset_left_eyelid_p_x, offset_left_eyelid_p_y);
        nose_left_ranges[1] = new PointF(_t_offset_nose_left_0_x, _t_offset_nose_left_0_y);


        for (int i = 2; i < nose_left_ranges.length; i++)
            nose_left_ranges[i] = new PointF();

        nose_left_ranges[2] = _t_46_p;
        nose_left_ranges[3] = _t_49_p;
        nose_left_ranges[4] = _t_85_p;
        nose_left_ranges[5] = _t_84_p;
        nose_left_ranges[6] = _t_left_face_center_p;
        nose_left_ranges[7] = _t_7_p;
        nose_left_ranges[8] = _t_6_p;
        nose_left_ranges[9] = _t_5_p;
        nose_left_ranges[10] = face_polar_left;

        //越过外边界
        if (!isPointInsidePolygon(nose_left_ranges, new PointF(_t_offset_nose_left_1_x, _t_offset_nose_left_1_y))) {
            _t_offset_nose_0_l_strength = 0;
            _t_offset_nose_1_l_strength = 0;
        }
        //==================================== 左脸 ==================================//

        //==================================== 右脸 ==================================//

        PointF[] nose_right_ranges = new PointF[11];

        nose_right_ranges[0] = new PointF(offset_right_eyelid_p_x, offset_right_eyelid_p_y);
        nose_right_ranges[1] = new PointF(_t_offset_nose_right_0_x, _t_offset_nose_right_0_y);

        for (int i = 2; i < nose_right_ranges.length; i++)
            nose_right_ranges[i] = new PointF();

        nose_right_ranges[2] = _t_46_p;
        nose_right_ranges[3] = _t_49_p;
        nose_right_ranges[4] = _t_89_p;
        nose_right_ranges[5] = _t_90_p;
        nose_right_ranges[6] = _t_right_face_center_p;
        nose_right_ranges[7] = _t_25_p;
        nose_right_ranges[8] = _t_26_p;
        nose_right_ranges[9] = _t_27_p;
        nose_right_ranges[10] = face_polar_right;
        //==================================== 右脸 ==================================//
        if (!isPointInsidePolygon(nose_right_ranges, new PointF(_t_offset_nose_right_1_x, _t_offset_nose_right_1_y))) {
            _t_offset_nose_0_r_strength = 0;
            _t_offset_nose_1_r_strength = 0;
        }

        float _t_offset_adjust_nose_left_0_x = _t_offset_nose_left_0_x * (1.0f - _t_offset_nose_0_l_strength) + _t_45_p.x * _t_offset_nose_0_l_strength;
        float _t_offset_adjust_nose_left_0_y = _t_offset_nose_left_0_y * (1.0f - _t_offset_nose_0_l_strength) + _t_45_p.y * _t_offset_nose_0_l_strength;
        float _t_offset_adjust_nose_right_0_x = _t_offset_nose_right_0_x * (1.0f - _t_offset_nose_0_r_strength) + _t_45_p.x * _t_offset_nose_0_r_strength;
        float _t_offset_adjust_nose_right_0_y = _t_offset_nose_right_0_y * (1.0f - _t_offset_nose_0_r_strength) + _t_45_p.y * _t_offset_nose_0_r_strength;


        PointF _t_nose_slight_1_p = _t_49_p;
        float _t_offset_adjust_nose_left_1_x = _t_offset_nose_left_1_x * (1.0f - _t_offset_nose_1_l_strength)
                + _t_nose_slight_1_p.x * _t_offset_nose_1_l_strength;
        float _t_offset_adjust_nose_left_1_y = _t_offset_nose_left_1_y * (1.0f - _t_offset_nose_1_l_strength)
                + _t_nose_slight_1_p.y * _t_offset_nose_1_l_strength;
        float _t_offset_adjust_nose_right_1_x = _t_offset_nose_right_1_x * (1.0f - _t_offset_nose_1_r_strength)
                + _t_nose_slight_1_p.x * _t_offset_nose_1_r_strength;
        float _t_offset_adjust_nose_right_1_y = _t_offset_nose_right_1_y * (1.0f - _t_offset_nose_1_r_strength)
                + _t_nose_slight_1_p.y * _t_offset_nose_1_r_strength;

        //=================================================         ============================================//

        ret[0] = _t_43_p.x;
        ret[1] = _t_43_p.y;
        ret[2] = _t_45_p.x;
        ret[3] = _t_45_p.y;
        ret[4] = _t_46_p.x;
        ret[5] = _t_46_p.y;

        ret[6] = _t_offset_adjust_nose_left_0_x;
        ret[7] = _t_offset_adjust_nose_left_0_y;
        ret[8] = _t_offset_adjust_nose_left_1_x;
        ret[9] = _t_offset_adjust_nose_left_1_y;
        ret[10] = _t_offset_adjust_nose_right_0_x;
        ret[11] = _t_offset_adjust_nose_right_0_y;
        ret[12] = _t_offset_adjust_nose_right_1_x;
        ret[13] = _t_offset_adjust_nose_right_1_y;

        ret[14] = _t_49_p.x;
        ret[15] = _t_49_p.y;

        ret[16] = _t_84_p.x;
        ret[17] = _t_84_p.y;
        ret[18] = _t_85_p.x;
        ret[19] = _t_85_p.y;
        ret[20] = _t_89_p.x;
        ret[21] = _t_89_p.y;
        ret[22] = _t_90_p.x;
        ret[23] = _t_90_p.y;

        ret[24] = offset_left_eyelid_p_x;
        ret[25] = offset_left_eyelid_p_y;
        ret[26] = offset_right_eyelid_p_x;
        ret[27] = offset_right_eyelid_p_y;

        ret[28] = _t_left_face_center_p.x;
        ret[29] = _t_left_face_center_p.y;
        ret[30] = _t_right_face_center_p.x;
        ret[31] = _t_right_face_center_p.y;

        ret[32] = face_polar_left.x;
        ret[33] = face_polar_left.y;
        ret[34] = face_polar_right.x;
        ret[35] = face_polar_right.y;

        ret[36] = idxPoint[26].x;
        ret[37] = idxPoint[26].y;
        ret[38] = idxPoint[27].x;
        ret[39] = idxPoint[27].y;
        ret[40] = idxPoint[28].x;
        ret[41] = idxPoint[28].y;

        ret[42] = idxPoint[29].x;
        ret[43] = idxPoint[29].y;
        ret[44] = idxPoint[30].x;
        ret[45] = idxPoint[30].y;
        ret[46] = idxPoint[31].x;
        ret[47] = idxPoint[31].y;


        for (int i = 0; i < 24; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

}
