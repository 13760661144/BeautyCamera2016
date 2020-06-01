package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import cn.poco.glfilter.shape.ShapeInfoData;

/**
 * Created by liujx on 2018/1/26.
 * 颧骨
 */
public class CrazyCheekbonesShapeFilter extends CrazyTriShapeFilter {

    private float mCheekStrength = 0;
    private float mCheekRadius = 0;

    public CrazyCheekbonesShapeFilter(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null)
            return;

        float faceScale = shapeData.cheekBoneStrength / 100f;   //for test

        mCheekStrength = faceScale * 0.1f;

        mCheekRadius = shapeData.cheekBoneRadius / 100f;
    }

    @Override
    public boolean setIndexBuffer() {
        short[] index = {
                106, 0, 1,
                106, 107, 1,
                107, 1, 2,
                107, 108, 2,
                108, 2, 3,
                108, 109, 3,
                109, 3, 4,
                109, 110, 4,
                110, 4, 5,
                110, 111, 5,
                111, 5, 6,
                111, 112, 6,

                0, 1, 113,
                1, 2, 113,
                2, 3, 113,
                3, 113, 114,
                3, 4, 114,
                4, 5, 114,
                5, 6, 114,

                115, 32, 31,
                115, 116, 31,
                116, 31, 30,
                116, 117, 30,
                117, 30, 29,
                117, 118, 29,
                118, 29, 28,
                118, 119, 28,
                119, 28, 27,
                119, 120, 27,
                120, 27, 26,
                120, 121, 26,

                32, 31, 122,
                31, 30, 122,
                30, 29, 122,
                29, 122, 123,
                29, 28, 123,
                28, 27, 123,
                27, 26, 123,
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
        if (points == null || points.length < 106)
            return null;

        ArrayList<PointF> output_ori = new ArrayList<PointF>();

        PointF _t_p;
        for (int i = 0; i < 106; i++) {
            _t_p = new PointF(points[i].x, points[i].y);
            output_ori.add(_t_p);
        }

        //insert effect range
        PointF left_eye_center = points[104];
        PointF right_eye_center = points[105];
        PointF _t_49_p = points[49];

        PointF eye_center = new PointF((left_eye_center.x + right_eye_center.x) * 0.5f,
                (left_eye_center.y + right_eye_center.y) * 0.5f);
        PointF face_center = new PointF((_t_49_p.x + eye_center.x) * 0.5f,
                (_t_49_p.y + eye_center.y) * 0.5f);

        PointF _t_1_p = points[1];
        PointF _t_44_p = points[44];
        PointF _t_57_p = points[57];

        PointF face_inner_left_p0 = new PointF((_t_1_p.x + _t_44_p.x) * 0.5f,
                (_t_1_p.y + _t_44_p.y) * 0.5f);
        face_inner_left_p0.x = (face_inner_left_p0.x + _t_57_p.x) * 0.5f;
        face_inner_left_p0.y = (face_inner_left_p0.y + _t_57_p.y) * 0.5f;

        PointF _t_5_p = points[5];
        PointF face_inner_left_p1 = offsetPointF(_t_49_p, _t_5_p, 0.7f);

        PointF _t_31_p = points[31];
        PointF _t_62_p = points[62];

        PointF face_inner_right_p0 = new PointF((_t_31_p.x + _t_44_p.x) * 0.5f,
                (_t_31_p.y + _t_44_p.y) * 0.5f);
        face_inner_right_p0.x = (face_inner_right_p0.x + _t_62_p.x) * 0.5f;
        face_inner_right_p0.y = (face_inner_right_p0.y + _t_62_p.y) * 0.5f;

        PointF _t_27_p = points[27];
        PointF face_inner_right_p1 = offsetPointF(_t_49_p, _t_27_p, 0.7f);

        float base_protect_offset_cof = 1.1f;
        float base_round_offset_cof = 1.3f;

        for (int i = 0, j = 0; i <= 6; i++, j++) {
            _t_p = offsetPointF(face_center, points[i], base_round_offset_cof + mCheekRadius);
            output_ori.add(_t_p);

            _t_p = offsetPointF(face_center, points[i], base_protect_offset_cof);

            output_ori.get(i).x = _t_p.x;
            output_ori.get(i).y = _t_p.y;
        }

        output_ori.add(face_inner_left_p0);
        output_ori.add(face_inner_left_p1);

        for (int i = 32, j = 0; i >= 26; i--, j++) {
            _t_p = offsetPointF(face_center, points[i], base_round_offset_cof + mCheekRadius);

            output_ori.add(_t_p);

            _t_p = offsetPointF(face_center, points[i], base_protect_offset_cof);

            output_ori.get(i).x = _t_p.x;
            output_ori.get(i).y = _t_p.y;
        }

        output_ori.add(face_inner_right_p0);
        output_ori.add(face_inner_right_p1);

        float ret[] = new float[2 * output_ori.size()];

        for (int i = 0; i < output_ori.size(); i++) {
            ret[2 * i] = output_ori.get(i).x;
            ret[2 * i + 1] = output_ori.get(i).y;
        }

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {
        float ret[] = getSrcPoint(points);

        PointF _t_p = new PointF();

        PointF left_eye_center = points[104];
        PointF right_eye_center = points[105];
        PointF _t_49_p = points[49];

        PointF eye_center = new PointF((left_eye_center.x + right_eye_center.x) * 0.5f,
                (left_eye_center.y + right_eye_center.y) * 0.5f);
        PointF face_center = new PointF((_t_49_p.x + eye_center.x) * 0.5f,
                (_t_49_p.y + eye_center.y) * 0.5f);

        PointF _t_0_p = points[0];
        PointF _t_1_p = points[1];
        PointF _t_2_p = points[2];
        PointF _t_3_p = points[3];
        PointF _t_4_p = points[4];
        PointF _t_5_p = points[5];
        PointF _t_6_p = points[6];

        PointF _t_32_p = points[32];
        PointF _t_31_p = points[31];
        PointF _t_30_p = points[30];
        PointF _t_29_p = points[29];
        PointF _t_28_p = points[28];
        PointF _t_27_p = points[27];
        PointF _t_26_p = points[26];

        //侧脸时，前面一张脸因为定点偏移，不做效果。
        PointF _3_p_pic = new PointF(_t_3_p.x * mWidth, _t_3_p.y * mHeight);
        PointF _29_p_pic = new PointF(_t_29_p.x * mWidth, _t_29_p.y * mHeight);
        PointF _face_center_pic = new PointF(face_center.x * mWidth,
                face_center.y * mHeight);

        float left_right_distance = distanceOfPoint(_3_p_pic, _29_p_pic);
        float left_face_width = distanceOfPoint(_3_p_pic, _face_center_pic);
        float right_face_width = distanceOfPoint(_29_p_pic, _face_center_pic);
        float left_face_ratio = left_face_width / left_right_distance;
        float right_face_ratio = right_face_width / left_right_distance;

        float aligned_left_strength = 1.0f;
        if (left_face_ratio > 0.5f) {
            aligned_left_strength = (left_face_ratio - 0.5f) * 10;
            aligned_left_strength = aligned_left_strength < 0 ? 0 : (aligned_left_strength > 1 ? 1 : aligned_left_strength);
            aligned_left_strength = 1.0f - aligned_left_strength;
            aligned_left_strength = (float) Math.pow(aligned_left_strength, 1.1);
        }
        float aligned_right_strength = 1.0f;
        if (right_face_ratio > 0.5f) {
            aligned_right_strength = (right_face_ratio - 0.5f) * 10;
            aligned_right_strength = aligned_right_strength < 0 ? 0 : (aligned_right_strength > 1 ? 1 : aligned_right_strength);
            aligned_right_strength = 1.0f - aligned_right_strength;
            aligned_right_strength = (float) Math.pow(aligned_right_strength, 1.1);
        }

        float left_cheek_s = mCheekStrength * aligned_left_strength;
        float right_cheek_s = mCheekStrength * aligned_right_strength;

        float base_protect_offset_cof = 1.1f;

        _t_0_p = offsetPointF(face_center, _t_0_p, base_protect_offset_cof);
        _t_1_p = offsetPointF(face_center, _t_1_p, base_protect_offset_cof);
        _t_2_p = offsetPointF(face_center, _t_2_p, base_protect_offset_cof);
        _t_3_p = offsetPointF(face_center, _t_3_p, base_protect_offset_cof);
        _t_4_p = offsetPointF(face_center, _t_4_p, base_protect_offset_cof);
        _t_5_p = offsetPointF(face_center, _t_5_p, base_protect_offset_cof);
        _t_6_p = offsetPointF(face_center, _t_6_p, base_protect_offset_cof);

        _t_32_p = offsetPointF(face_center, _t_32_p
                , base_protect_offset_cof);
        _t_31_p = offsetPointF(face_center, _t_31_p
                , base_protect_offset_cof);
        _t_30_p = offsetPointF(face_center, _t_30_p
                , base_protect_offset_cof);
        _t_29_p = offsetPointF(face_center, _t_29_p
                , base_protect_offset_cof);
        _t_28_p = offsetPointF(face_center, _t_28_p
                , base_protect_offset_cof);
        _t_27_p = offsetPointF(face_center, _t_27_p
                , base_protect_offset_cof);
        _t_26_p = offsetPointF(face_center, _t_26_p
                , base_protect_offset_cof);

        PointF _t_44_p = points[44];
        PointF _t_57_p = points[57];
        PointF face_inner_left_p0 = new PointF((_t_1_p.x + _t_44_p.x) * 0.5f,
                (_t_1_p.y + _t_44_p.y) * 0.5f);
        face_inner_left_p0.x = (face_inner_left_p0.x + _t_57_p.x) * 0.5f;
        face_inner_left_p0.y = (face_inner_left_p0.y + _t_57_p.y) * 0.5f;

        PointF face_inner_left_p1 = offsetPointF(_t_49_p, _t_5_p, 0.7f);

        //offset effect
        PointF _3_offset_ref_p = new PointF((face_inner_left_p0.x + face_inner_left_p1.x) * 0.5f,
                (face_inner_left_p0.y + face_inner_left_p1.y) * 0.5f);

        PointF _offset_1_p = offsetPointF(_t_1_p, face_inner_left_p0
                , left_cheek_s * 0.2f);
        PointF _offset_2_p = offsetPointF(_t_2_p, face_inner_left_p0
                , left_cheek_s * 0.6f);
        PointF _offset_3_p = offsetPointF(_t_3_p, _3_offset_ref_p
                , left_cheek_s);
        PointF _offset_4_p = offsetPointF(_t_4_p, face_inner_left_p1
                , left_cheek_s * 0.6f);
        PointF _offset_5_p = offsetPointF(_t_5_p, face_inner_left_p1
                , left_cheek_s * 0.2f);

        PointF _t_62_p = points[62];
        PointF face_inner_right_p0 = new PointF((_t_31_p.x + _t_44_p.x) * 0.5f,
                (_t_31_p.y + _t_44_p.y) * 0.5f);
        face_inner_right_p0.x = (face_inner_right_p0.x + _t_62_p.x) * 0.5f;
        face_inner_right_p0.y = (face_inner_right_p0.y + _t_62_p.y) * 0.5f;
        PointF face_inner_right_p1 = offsetPointF(_t_49_p, _t_27_p, 0.7f);

        PointF _29_offset_ref_p = new PointF((face_inner_right_p0.x + face_inner_right_p1.x) * 0.5f,
                (face_inner_right_p0.y + face_inner_right_p1.y) * 0.5f);


        PointF _offset_31_p = offsetPointF(_t_31_p, face_inner_right_p0
                , right_cheek_s * 0.2f);
        PointF _offset_30_p = offsetPointF(_t_30_p, face_inner_right_p0
                , right_cheek_s * 0.6f);
        PointF _offset_29_p = offsetPointF(_t_29_p, _29_offset_ref_p
                , right_cheek_s);
        PointF _offset_28_p = offsetPointF(_t_28_p, face_inner_right_p1
                , right_cheek_s * 0.6f);
        PointF _offset_27_p = offsetPointF(_t_27_p, face_inner_right_p1
                , right_cheek_s * 0.2f);

        ret[2 * 0] = _t_0_p.x;
        ret[2 * 0 + 1] = _t_0_p.y;
        ret[2 * 1] = _offset_1_p.x;
        ret[2 * 1 + 1] = _offset_1_p.y;
        ret[2 * 2] = _offset_2_p.x;
        ret[2 * 2 + 1] = _offset_2_p.y;
        ret[2 * 3] = _offset_3_p.x;
        ret[2 * 3 + 1] = _offset_3_p.y;
        ret[2 * 4] = _offset_4_p.x;
        ret[2 * 4 + 1] = _offset_4_p.y;
        ret[2 * 5] = _offset_5_p.x;
        ret[2 * 5 + 1] = _offset_5_p.y;
        ret[2 * 6] = _t_6_p.x;
        ret[2 * 6 + 1] = _t_6_p.y;

        ret[2 * 32] = _t_32_p.x;
        ret[2 * 32 + 1] = _t_32_p.y;
        ret[2 * 31] = _offset_31_p.x;
        ret[2 * 31 + 1] = _offset_31_p.y;
        ret[2 * 30] = _offset_30_p.x;
        ret[2 * 30 + 1] = _offset_30_p.y;
        ret[2 * 29] = _offset_29_p.x;
        ret[2 * 29 + 1] = _offset_29_p.y;
        ret[2 * 28] = _offset_28_p.x;
        ret[2 * 28 + 1] = _offset_28_p.y;
        ret[2 * 27] = _offset_27_p.x;
        ret[2 * 27 + 1] = _offset_27_p.y;
        ret[2 * 26] = _t_26_p.x;
        ret[2 * 26 + 1] = _t_26_p.y;

        return ret;
    }


}
