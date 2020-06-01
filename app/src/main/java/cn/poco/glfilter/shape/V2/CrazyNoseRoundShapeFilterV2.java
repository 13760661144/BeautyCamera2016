package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.poco.glfilter.shape.ShapeInfoData;

/**
 * Created by liujx on 2018/1/26.
 * 此类实现鼻翼和鼻高效果
 */
public class CrazyNoseRoundShapeFilterV2 extends CrazyTriShapeFilter {

    private float mNoseWingStrength = 0;      //鼻翼
    private float mNoseLengthStrength = 0;    //鼻高

    public CrazyNoseRoundShapeFilterV2(Context context) {
        super(context);
    }

    @Override
    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;

        this.mNoseWingStrength = shapeData.noseWingStrength;   //for test
        this.mNoseLengthStrength = shapeData.noseLengthStrength;

        mNoseWingStrength = (mNoseWingStrength - 50) * 2.0f * 0.05f / 100.f;
        mNoseLengthStrength = (mNoseLengthStrength - 50) * 2.0f * 0.042f / 100.f;
    }

    @Override
    public boolean setIndexBuffer() {
        short[] index =
                {0, 1, 8,
                        1, 6, 8,
                        1, 6, 41,
                        1, 3, 41,
                        3, 38, 41,
                        38, 40, 41,
                        37, 38, 40,
                        6, 40, 41,
                        6, 14, 40,
                        36, 37, 39,
                        37, 39, 40,
                        14, 39, 40,
                        14, 16, 39,
                        36, 35, 39,
                        16, 34, 39,
                        39, 34, 35,
                        16, 25, 34,

                        0, 2, 9,
                        2, 7, 9,
                        2, 7, 49,
                        2, 4, 49,
                        4, 49, 46,
                        7, 15, 48,
                        7, 48, 49,
                        49, 46, 48,
                        46, 48, 45,
                        15, 17, 47,
                        15, 48, 47,
                        48, 45, 47,
                        45, 47, 44,
                        44, 47, 43,
                        17, 42, 47,
                        47, 42, 43,
                        17, 26, 42,

                        0, 5, 8,
                        0, 5, 9,
                        5, 6, 8,
                        5, 7, 9,
                        5, 6, 11,
                        5, 7, 12,
                        5, 10, 11,
                        5, 10, 12,
                        6, 11, 14,
                        11, 14, 18,
                        11, 10, 18,
                        18, 10, 13,
                        7, 12, 15,
                        12, 15, 19,
                        12, 10, 19,
                        10, 13, 19,

                        14, 16, 18,
                        16, 18, 20,
                        18, 13, 20,
                        15, 17, 19,
                        17, 19, 21,
                        19, 13, 21,
                        16, 20, 25,
                        20, 25, 23,
                        20, 23, 13,
                        13, 22, 23,
                        17, 26, 21,
                        21, 26, 24,
                        21, 24, 13,
                        13, 22, 24,

                        34, 25, 27,
                        25, 27, 28,
                        25, 23, 28,
                        23, 28, 29,
                        23, 22, 29,
                        22, 29, 30,

                        42, 26, 33,
                        26, 32, 33,
                        24, 26, 32,
                        24, 31, 32,
                        22, 24, 31,
                        22, 30, 31};

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

        PointF idxPoint[] = new PointF[points.length];
        for (int i = 0; i < points.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[i].x * mWidth;
            idxPoint[i].y = points[i].y * mHeight;
        }

        PointF nose_wing_ps[] = new PointF[50];
        for (int i = 0; i < nose_wing_ps.length; i++)
            nose_wing_ps[i] = new PointF();


        nose_wing_ps[0] = idxPoint[43];
        nose_wing_ps[1] = idxPoint[78];
        nose_wing_ps[2] = idxPoint[79];
        nose_wing_ps[3] = idxPoint[55];
        nose_wing_ps[4] = idxPoint[58];
        nose_wing_ps[5] = idxPoint[44];
        nose_wing_ps[14] = idxPoint[80];
        nose_wing_ps[15] = idxPoint[81];
        nose_wing_ps[6].x = nose_wing_ps[5].x +
                (nose_wing_ps[14].x - nose_wing_ps[15].x) * 0.25f;
        nose_wing_ps[6].y = nose_wing_ps[5].y +
                (nose_wing_ps[14].y - nose_wing_ps[15].y) * 0.25f;
        nose_wing_ps[7].x = nose_wing_ps[5].x +
                (nose_wing_ps[15].x - nose_wing_ps[14].x) * 0.25f;
        nose_wing_ps[7].y = nose_wing_ps[5].y +
                (nose_wing_ps[15].y - nose_wing_ps[14].y) * 0.25f;
        nose_wing_ps[8].x = (nose_wing_ps[1].x + nose_wing_ps[5].x) * 0.5f;
        nose_wing_ps[8].y = (nose_wing_ps[1].y + nose_wing_ps[5].y) * 0.5f;
        nose_wing_ps[9].x = (nose_wing_ps[2].x + nose_wing_ps[5].x) * 0.5f;
        nose_wing_ps[9].y = (nose_wing_ps[2].y + nose_wing_ps[5].y) * 0.5f;
        nose_wing_ps[10] = idxPoint[45];
        nose_wing_ps[11].x = (nose_wing_ps[6].x + nose_wing_ps[10].x) * 0.5f;
        nose_wing_ps[11].y = (nose_wing_ps[6].y + nose_wing_ps[10].y) * 0.5f;
        nose_wing_ps[12].x = (nose_wing_ps[7].x + nose_wing_ps[10].x) * 0.5f;
        nose_wing_ps[12].y = (nose_wing_ps[7].y + nose_wing_ps[10].y) * 0.5f;
        nose_wing_ps[13] = idxPoint[46];

        PointF _82_p = idxPoint[82];
        PointF _83_p = idxPoint[83];
        float offset_coe_0 = 0.1f;
        nose_wing_ps[16].x = _82_p.x + (nose_wing_ps[13].x - _82_p.x) * offset_coe_0;
        nose_wing_ps[16].y = _82_p.y + (nose_wing_ps[13].y - _82_p.y) * offset_coe_0;
        nose_wing_ps[17].x = _83_p.x + (nose_wing_ps[13].x - _83_p.x) * offset_coe_0;
        nose_wing_ps[17].y = _83_p.y + (nose_wing_ps[13].y - _83_p.y) * offset_coe_0;
        nose_wing_ps[18].x = (nose_wing_ps[13].x + nose_wing_ps[14].x) * 0.5f;
        nose_wing_ps[18].y = (nose_wing_ps[13].y + nose_wing_ps[14].y) * 0.5f;
        nose_wing_ps[19].x = (nose_wing_ps[13].x + nose_wing_ps[15].x) * 0.5f;
        nose_wing_ps[19].y = (nose_wing_ps[13].y + nose_wing_ps[15].y) * 0.5f;
        nose_wing_ps[20].x = (nose_wing_ps[13].x + nose_wing_ps[16].x) * 0.5f;
        nose_wing_ps[20].y = (nose_wing_ps[13].y + nose_wing_ps[16].y) * 0.5f;
        nose_wing_ps[21].x = (nose_wing_ps[13].x + nose_wing_ps[17].x) * 0.5f;
        nose_wing_ps[21].y = (nose_wing_ps[13].y + nose_wing_ps[17].y) * 0.5f;

        nose_wing_ps[22] = idxPoint[49];
        PointF _47_p = idxPoint[47];
        PointF _48_p = idxPoint[48];
        PointF _50_p = idxPoint[50];
        PointF _51_p = idxPoint[51];
        float offset_coe_1 = 0.2f;
        nose_wing_ps[23].x = _48_p.x + (_48_p.x - nose_wing_ps[20].x) * offset_coe_1;
        nose_wing_ps[23].y = _48_p.y + (_48_p.y - nose_wing_ps[20].y) * offset_coe_1;
        nose_wing_ps[24].x = _50_p.x + (_50_p.x - nose_wing_ps[21].x) * offset_coe_1;
        nose_wing_ps[24].y = _50_p.y + (_50_p.y - nose_wing_ps[21].y) * offset_coe_1;
        float offset_coe_2 = 0.35f;
        nose_wing_ps[25].x = _47_p.x + (_47_p.x - nose_wing_ps[20].x) * offset_coe_2;
        nose_wing_ps[25].y = _47_p.y + (_47_p.y - nose_wing_ps[20].y) * offset_coe_2;
        nose_wing_ps[26].x = _51_p.x + (_51_p.x - nose_wing_ps[21].x) * offset_coe_2;
        nose_wing_ps[26].y = _51_p.y + (_51_p.y - nose_wing_ps[21].y) * offset_coe_2;

        PointF _85_p = idxPoint[85];
        nose_wing_ps[27].x = (_82_p.x + _85_p.x) * 0.5f;
        nose_wing_ps[27].y = (_82_p.y + _85_p.y) * 0.5f;
        PointF _86_p = idxPoint[86];
        nose_wing_ps[29].x = (_48_p.x + _86_p.x) * 0.5f;
        nose_wing_ps[29].y = (_48_p.y + _86_p.y) * 0.5f;
        nose_wing_ps[28].x = (nose_wing_ps[27].x + nose_wing_ps[29].x) * 0.5f;
        nose_wing_ps[28].y = (nose_wing_ps[27].y + nose_wing_ps[29].y) * 0.5f;
        PointF _87_p = idxPoint[87];
        nose_wing_ps[30].x = (nose_wing_ps[22].x + _87_p.x) * 0.5f;
        nose_wing_ps[30].y = (nose_wing_ps[22].y + _87_p.y) * 0.5f;
        PointF _88_p = idxPoint[88];
        nose_wing_ps[31].x = (_50_p.x + _88_p.x) * 0.5f;
        nose_wing_ps[31].y = (_50_p.y + _88_p.y) * 0.5f;
        PointF _89_p = idxPoint[89];
        nose_wing_ps[33].x = (_83_p.x + _89_p.x) * 0.5f;
        nose_wing_ps[33].y = (_83_p.y + _89_p.y) * 0.5f;
        nose_wing_ps[32].x = (nose_wing_ps[31].x + nose_wing_ps[33].x) * 0.5f;
        nose_wing_ps[32].y = (nose_wing_ps[31].y + nose_wing_ps[33].y) * 0.5f;

        PointF _7_p = idxPoint[7];
        nose_wing_ps[35].x = (_7_p.x + _82_p.x) * 0.5f;
        nose_wing_ps[35].y = (_7_p.y + _82_p.y) * 0.5f;
        nose_wing_ps[34].x = (nose_wing_ps[27].x + nose_wing_ps[35].x) * 0.5f;
        nose_wing_ps[34].y = (nose_wing_ps[27].y + nose_wing_ps[35].y) * 0.5f;
        PointF _25_p = idxPoint[25];
        nose_wing_ps[43].x = (_25_p.x + _83_p.x) * 0.5f;
        nose_wing_ps[43].y = (_25_p.y + _83_p.y) * 0.5f;
        nose_wing_ps[42].x = (nose_wing_ps[33].x + nose_wing_ps[43].x) * 0.5f;
        nose_wing_ps[42].y = (nose_wing_ps[33].y + nose_wing_ps[43].y) * 0.5f;

        PointF _4_p = idxPoint[4];
        nose_wing_ps[36].x = (_4_p.x + _82_p.x) * 0.5f;
        nose_wing_ps[36].y = (_4_p.y + _82_p.y) * 0.5f;
        nose_wing_ps[37].x = nose_wing_ps[36].x +
                (nose_wing_ps[3].x - nose_wing_ps[36].x) * 0.3f;
        nose_wing_ps[37].y = nose_wing_ps[36].y +
                (nose_wing_ps[3].y - nose_wing_ps[36].y) * 0.3f;
        nose_wing_ps[38].x = nose_wing_ps[36].x +
                (nose_wing_ps[3].x - nose_wing_ps[36].x) * 0.6f;
        nose_wing_ps[38].y = nose_wing_ps[36].y +
                (nose_wing_ps[3].y - nose_wing_ps[36].y) * 0.6f;
        PointF left_nose_round_vec = new PointF(nose_wing_ps[14].x - nose_wing_ps[13].x,
                nose_wing_ps[14].y - nose_wing_ps[13].y);
        nose_wing_ps[37].x = nose_wing_ps[37].x + left_nose_round_vec.x * 0.2f;
        nose_wing_ps[37].y = nose_wing_ps[37].y + left_nose_round_vec.y * 0.2f;
        nose_wing_ps[38].x = nose_wing_ps[38].x + left_nose_round_vec.x * 0.15f;
        nose_wing_ps[38].y = nose_wing_ps[38].y + left_nose_round_vec.y * 0.15f;
        nose_wing_ps[39].x = (nose_wing_ps[16].x + nose_wing_ps[36].x) * 0.5f;
        nose_wing_ps[39].y = (nose_wing_ps[16].y + nose_wing_ps[36].y) * 0.5f;
        nose_wing_ps[40].x = (nose_wing_ps[14].x + nose_wing_ps[37].x) * 0.5f;
        nose_wing_ps[40].y = (nose_wing_ps[14].y + nose_wing_ps[37].y) * 0.5f;
        nose_wing_ps[41].x = (nose_wing_ps[6].x + nose_wing_ps[38].x) * 0.5f;
        nose_wing_ps[41].y = (nose_wing_ps[6].y + nose_wing_ps[38].y) * 0.5f;

        PointF _28_p = idxPoint[28];
        nose_wing_ps[44].x = (_28_p.x + _83_p.x) * 0.5f;
        nose_wing_ps[44].y = (_28_p.y + _83_p.y) * 0.5f;
        nose_wing_ps[45].x = nose_wing_ps[44].x +
                (nose_wing_ps[4].x - nose_wing_ps[44].x) * 0.3f;
        nose_wing_ps[45].y = nose_wing_ps[44].y +
                (nose_wing_ps[4].y - nose_wing_ps[44].y) * 0.3f;
        nose_wing_ps[46].x = nose_wing_ps[44].x +
                (nose_wing_ps[4].x - nose_wing_ps[44].x) * 0.6f;
        nose_wing_ps[46].y = nose_wing_ps[44].y +
                (nose_wing_ps[4].y - nose_wing_ps[44].y) * 0.6f;
        nose_wing_ps[47].x = (nose_wing_ps[17].x + nose_wing_ps[44].x) * 0.5f;
        nose_wing_ps[47].y = (nose_wing_ps[17].y + nose_wing_ps[44].y) * 0.5f;
        nose_wing_ps[48].x = (nose_wing_ps[15].x + nose_wing_ps[45].x) * 0.5f;
        nose_wing_ps[48].y = (nose_wing_ps[15].y + nose_wing_ps[45].y) * 0.5f;
        nose_wing_ps[49].x = (nose_wing_ps[7].x + nose_wing_ps[46].x) * 0.5f;
        nose_wing_ps[49].y = (nose_wing_ps[7].y + nose_wing_ps[46].y) * 0.5f;

        float ret[] = new float[nose_wing_ps.length * 2];
        for (int i = 0; i < 50; i++) {
            ret[i * 2] = nose_wing_ps[i].x / mWidth;
            ret[i * 2 + 1] = nose_wing_ps[i].y / mHeight;
        }

        return ret;
    }

    @Override
    protected float[] getOffsetPoint(PointF[] points) {
        if (points == null || points.length < 106)
            return null;

        float offset_nosewing_coordinate[] = getSrcPoint(points);

        PointF _nose_wing_left = new PointF(
                offset_nosewing_coordinate[16 * 2], offset_nosewing_coordinate[16 * 2 + 1]);
        PointF _nose_wing_right = new PointF(
                offset_nosewing_coordinate[17 * 2], offset_nosewing_coordinate[17 * 2 + 1]);

        PointF _nose_wing_lo = new PointF(_nose_wing_right.x - _nose_wing_left.x,
                _nose_wing_right.y - _nose_wing_left.y);
        PointF _nose_wing_ro = new PointF(_nose_wing_left.x - _nose_wing_right.x,
                _nose_wing_left.y - _nose_wing_right.y);

        PointF _nose_top = points[43];
        PointF _nose_bottom = points[49];
        PointF _nose_length_offset = new PointF(_nose_top.x - _nose_bottom.x,
                _nose_top.y - _nose_bottom.y);

        int nose_wing_offset_index[] = {
                18, 20, 14, 16, 39, 40, 25,
                19, 21, 15, 17, 47, 48, 26
        };
        float nose_wing_offset_coe[] =
                {
                        0.6f, 0.6f, 0.8f, 1.0f, 1.0f, 0.4f, 0.85f,
                        0.6f, 0.6f, 0.8f, 1.0f, 1.0f, 0.4f, 0.85f
                };

        float nosew_l_s = mNoseWingStrength;
        float nosew_r_s = mNoseWingStrength;

        PointF[] nose_l_ranges = new PointF[6];
        nose_l_ranges[0] = new PointF(offset_nosewing_coordinate[10 * 2] * mWidth, offset_nosewing_coordinate[10 * 2 + 1] * mHeight);
        nose_l_ranges[1] = new PointF(offset_nosewing_coordinate[13 * 2] * mWidth, offset_nosewing_coordinate[13 * 2 + 1] * mHeight);
        nose_l_ranges[2] = new PointF(offset_nosewing_coordinate[20 * 2] * mWidth, offset_nosewing_coordinate[20 * 2 + 1] * mHeight);
        nose_l_ranges[3] = new PointF(offset_nosewing_coordinate[16 * 2] * mWidth, offset_nosewing_coordinate[16 * 2 + 1] * mHeight);
        nose_l_ranges[4] = new PointF(offset_nosewing_coordinate[14 * 2] * mWidth, offset_nosewing_coordinate[14 * 2 + 1] * mHeight);
        nose_l_ranges[5] = new PointF(offset_nosewing_coordinate[11 * 2] * mWidth, offset_nosewing_coordinate[11 * 2 + 1] * mHeight);
        boolean nosew_l_inside = isPointInsidePolygon(nose_l_ranges, new PointF(offset_nosewing_coordinate[18 * 2] * mWidth, offset_nosewing_coordinate[18 * 2 + 1] * mHeight));

        nose_l_ranges[0] = new PointF(offset_nosewing_coordinate[14 * 2] * mWidth, offset_nosewing_coordinate[14 * 2 + 1] * mHeight);
        nose_l_ranges[1] = new PointF(offset_nosewing_coordinate[18 * 2] * mWidth, offset_nosewing_coordinate[18 * 2 + 1] * mHeight);
        nose_l_ranges[2] = new PointF(offset_nosewing_coordinate[20 * 2] * mWidth, offset_nosewing_coordinate[20 * 2 + 1] * mHeight);
        nose_l_ranges[3] = new PointF(offset_nosewing_coordinate[23 * 2] * mWidth, offset_nosewing_coordinate[23 * 2 + 1] * mHeight);
        nose_l_ranges[4] = new PointF(offset_nosewing_coordinate[25 * 2] * mWidth, offset_nosewing_coordinate[25 * 2 + 1] * mHeight);
        nose_l_ranges[5] = new PointF(offset_nosewing_coordinate[39 * 2] * mWidth, offset_nosewing_coordinate[39 * 2 + 1] * mHeight);
        nosew_l_inside &= isPointInsidePolygon(nose_l_ranges, new PointF(offset_nosewing_coordinate[16 * 2] * mWidth, offset_nosewing_coordinate[16 * 2 + 1] * mHeight));

        if (!nosew_l_inside) {
            nosew_l_s = 0;
        }


        PointF[] nose_r_ranges = new PointF[6];
        nose_r_ranges[0] = new PointF(offset_nosewing_coordinate[10 * 2] * mWidth, offset_nosewing_coordinate[10 * 2 + 1] * mHeight);
        nose_r_ranges[1] = new PointF(offset_nosewing_coordinate[13 * 2] * mWidth, offset_nosewing_coordinate[13 * 2 + 1] * mHeight);
        nose_r_ranges[2] = new PointF(offset_nosewing_coordinate[21 * 2] * mWidth, offset_nosewing_coordinate[21 * 2 + 1] * mHeight);
        nose_r_ranges[3] = new PointF(offset_nosewing_coordinate[17 * 2] * mWidth, offset_nosewing_coordinate[17 * 2 + 1] * mHeight);
        nose_r_ranges[4] = new PointF(offset_nosewing_coordinate[15 * 2] * mWidth, offset_nosewing_coordinate[15 * 2 + 1] * mHeight);
        nose_r_ranges[5] = new PointF(offset_nosewing_coordinate[12 * 2] * mWidth, offset_nosewing_coordinate[12 * 2 + 1] * mHeight);

        boolean nosew_r_inside = isPointInsidePolygon(nose_r_ranges, new PointF(offset_nosewing_coordinate[19 * 2] * mWidth, offset_nosewing_coordinate[19 * 2 + 1] * mHeight));

        nose_r_ranges[0] = new PointF(offset_nosewing_coordinate[15 * 2] * mWidth, offset_nosewing_coordinate[15 * 2 + 1] * mHeight);
        nose_r_ranges[1] = new PointF(offset_nosewing_coordinate[19 * 2] * mWidth, offset_nosewing_coordinate[19 * 2 + 1] * mHeight);
        nose_r_ranges[2] = new PointF(offset_nosewing_coordinate[21 * 2] * mWidth, offset_nosewing_coordinate[21 * 2 + 1] * mHeight);
        nose_r_ranges[3] = new PointF(offset_nosewing_coordinate[24 * 2] * mWidth, offset_nosewing_coordinate[24 * 2 + 1] * mHeight);
        nose_r_ranges[4] = new PointF(offset_nosewing_coordinate[26 * 2] * mWidth, offset_nosewing_coordinate[26 * 2 + 1] * mHeight);
        nose_r_ranges[5] = new PointF(offset_nosewing_coordinate[47 * 2] * mWidth, offset_nosewing_coordinate[47 * 2 + 1] * mHeight);
        nosew_r_inside &= isPointInsidePolygon(nose_r_ranges, new PointF(offset_nosewing_coordinate[17 * 2] * mWidth, offset_nosewing_coordinate[17 * 2 + 1] * mHeight));

        if (!nosew_r_inside) {
            nosew_r_s = 0;
        }

        for (int i = 0; i < 7; i++) {
            offset_nosewing_coordinate[nose_wing_offset_index[i] * 2] =
                    offset_nosewing_coordinate[nose_wing_offset_index[i] * 2] +
                            _nose_wing_lo.x * nosew_l_s * nose_wing_offset_coe[i];

            offset_nosewing_coordinate[nose_wing_offset_index[i] * 2 + 1] =
                    offset_nosewing_coordinate[nose_wing_offset_index[i] * 2 + 1] +
                            _nose_wing_lo.y * nosew_l_s * nose_wing_offset_coe[i];
        }
        for (int i = 7; i < 14; i++) {
            offset_nosewing_coordinate[nose_wing_offset_index[i] * 2] =
                    offset_nosewing_coordinate[nose_wing_offset_index[i] * 2] +
                            _nose_wing_ro.x * nosew_r_s * nose_wing_offset_coe[i];

            offset_nosewing_coordinate[nose_wing_offset_index[i] * 2 + 1] =
                    offset_nosewing_coordinate[nose_wing_offset_index[i] * 2 + 1] +
                            _nose_wing_ro.y * nosew_r_s * nose_wing_offset_coe[i];
        }

        int nose_length_offset_index[] = {
                5, 10, 13, 22,/* 30,*/

                8, 6, 11, 14, 18, 16, 20, 25, 23,
                41, 40, 39,

                9, 7, 12, 15, 19, 17, 21, 26, 24,
                49, 48, 47
        };
        float nose_length_offset_coe[] = {
                0.5f, 1.0f, 1.0f, 1.0f, /*1.0f,*/

                0.1f, 0.23f, 0.6f, 0.85f, 0.85f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.28f, 0.6f, 0.6f,

                0.1f, 0.23f, 0.6f, 0.85f, 0.85f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.28f, 0.6f, 0.6f
        };

        if (nosew_l_inside && nosew_r_inside) {
            for (int i = 0; i < nose_length_offset_index.length; i++) {
                offset_nosewing_coordinate[nose_length_offset_index[i] * 2] =
                        offset_nosewing_coordinate[nose_length_offset_index[i] * 2] +
                                _nose_length_offset.x * mNoseLengthStrength * nose_length_offset_coe[i];

                offset_nosewing_coordinate[nose_length_offset_index[i] * 2 + 1] =
                        offset_nosewing_coordinate[nose_length_offset_index[i] * 2 + 1] +
                                _nose_length_offset.y * mNoseLengthStrength * nose_length_offset_coe[i];
            }
        }

        return offset_nosewing_coordinate;
    }

}
