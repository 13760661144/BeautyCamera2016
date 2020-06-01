package cn.poco.glfilter.sticker;

import android.graphics.PointF;

/**
 * Created by zwq on 2016/01/20 14:01.<br/><br/>
 */
public class PointsUtils {

    /**
     * 计算两点的距离
     *
     * @param pointF1
     * @param pointF2
     * @return
     */
    public static float getDistance(PointF pointF1, PointF pointF2) {
        float dx = pointF1.x - pointF2.x;
        float dy = pointF1.y - pointF2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float getDistance(PointF pointF1, PointF pointF2, float xRatio, float yRatio) {
        if (xRatio <= 0.0f) {
            xRatio = 1.0f;
        }
        if (yRatio <= 0.0f) {
            yRatio = 1.0f;
        }
        float dx = (pointF1.x - pointF2.x) * xRatio;
        float dy = (pointF1.y - pointF2.y) * yRatio;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float getDistanceWithXOffset(PointF pointF1, PointF pointF2) {
        float offset = 1.0f;
        float dx = pointF1.x - pointF2.x;
        float dy = pointF1.y - pointF2.y;
        offset = dx / Math.abs(dx);
        return (float) Math.sqrt(dx * dx + dy * dy) * offset;
    }

    public static float getDistanceWithYOffset(PointF pointF1, PointF pointF2) {
        float offset = 1.0f;
        float dx = pointF1.x - pointF2.x;
        float dy = pointF1.y - pointF2.y;
        offset = dy / Math.abs(dy);
        return (float) Math.sqrt(dx * dx + dy * dy) * offset;
    }

    /**
     * 旋转, 弧度
     * pointF1和pointF2用于计算旋转角度
     *
     * @param pointF1
     * @param pointF2
     * @return
     */
    public static float getRadians(PointF pointF1, PointF pointF2) {
        float dx = pointF2.x - pointF1.x;
        float dy = pointF2.y - pointF1.y;
        return (float) Math.atan2(dy, dx);//弧度
    }

    public static PointF getCenterPoint(PointF pointF1, PointF pointF2) {
        return new PointF((pointF1.x + pointF2.x) / 2, (pointF1.y + pointF2.y) / 2);//弧度
    }

    /**
     * 根据旋转中心点和旋转弧度，计算旋转后的点
     *
     * @param pointF
     * @param center  旋转中心点
     * @param radians 弧度
     * @return
     */
    public static PointF rotate(PointF pointF, PointF center, float radians) {
        /*最后如果旋转中心为(a,b),在利用下面的公式时,需要把(a,b)沿向量(-a,-b)移动到原点,此时(x,y)变成(x-a,y-b),(x',y')变成(x'-a,y'-b),整理得
        x'=(x-a)cosα+(y-b)sinα+a
        y'=-(x-a)sinα+(y-b)cosα+b*/
        float x = (float) ((pointF.x - center.x) * Math.cos(radians) + (pointF.y - center.y) * Math.sin(radians) + center.x);
        float y = (float) (-(pointF.x - center.x) * Math.sin(radians) + (pointF.y - center.y) * Math.cos(radians) + center.y);
        return new PointF(x, y);
    }

    public static float rotateX(float px, float py, float cx, float cy, float radians, float whRatio) {
        return (float) ((px - cx) * Math.cos(radians) + (py - cy) * Math.sin(radians) / whRatio + cx);
    }

    public static float rotateY(float px, float py, float cx, float cy, float radians, float whRatio) {
        return (float) (-(px - cx) * Math.sin(radians) * whRatio + (py - cy) * Math.cos(radians) + cy);
    }

    /**
     * @param points
     * @param cx
     * @param cy
     * @param radians
     * @param whRatio     坐标系的 y/x 比例，（viewHeight / viewWidth）
     * @param orientation 四个旋转方向(正常时为0 顺时针分别为1， 2， 3)
     * @param rotateX
     * @param rotateY
     * @return
     */
    public static float[] rotate(float[] points, float cx, float cy, float radians, float whRatio, int orientation, int rotateX, int rotateY) {
        if (points == null) return null;
        float[] result = new float[points.length];
        for (int i = 0; i < points.length / 2; i++) {
            if (orientation == 1) {
                result[i * 2 + 0] = rotateX == 1 ? rotateY(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 0];
                result[i * 2 + 1] = rotateY == 1 ? -rotateX(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 1];
            } else if (orientation == 2) {
                result[i * 2 + 0] = rotateX == 1 ? -rotateX(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 0];
                result[i * 2 + 1] = rotateY == 1 ? -rotateY(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 1];
            } else if (orientation == 3) {
                result[i * 2 + 0] = rotateX == 1 ? -rotateY(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 0];
                result[i * 2 + 1] = rotateY == 1 ? rotateX(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 1];
            } else {
                result[i * 2 + 0] = rotateX == 1 ? rotateX(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 0];
                result[i * 2 + 1] = rotateY == 1 ? rotateY(points[i * 2 + 0], points[i * 2 + 1], cx, cy, radians, whRatio) : points[i * 2 + 1];
            }
        }
        return result;
    }

    public static float[] rotate(float[] points, float cx, float cy, float radians, int orientation, int rotateX, int rotateY) {
        return rotate(points, cx, cy, radians, 1.0f, orientation, rotateX, rotateY);
    }

    //https://wenku.baidu.com/view/6dac0c22915f804d2b16c17c.html
    public static float rotateX_Y(float py, float pz, float cy, float cz, float radians, float whRatio) {
        return (float) ((py - cy) * Math.cos(radians) + (pz - cz) * Math.sin(radians) / whRatio + cy);
    }

    public static float rotateX_Z(float py, float pz, float cy, float cz, float radians, float whRatio) {
        return (float) (-(py - cy) * Math.sin(radians) * whRatio + (pz - cz) * Math.cos(radians) + cz);
    }

    public static float rotateY_X(float px, float pz, float cx, float cz, float radians, float whRatio) {
        return (float) ((px - cx) * Math.cos(radians) - (pz - cz) * Math.sin(radians) / whRatio + cx);
    }

    public static float rotateY_Z(float px, float pz, float cx, float cz, float radians, float whRatio) {
        return (float) ((px - cx) * Math.sin(radians) * whRatio + (pz - cz) * Math.cos(radians) + cz);
    }
}
