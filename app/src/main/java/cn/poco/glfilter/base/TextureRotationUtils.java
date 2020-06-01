package cn.poco.glfilter.base;

/**
 * Created by zwq on 2016/08/08 18:48.<br/><br/>
 */
public class TextureRotationUtils {

    public interface Rotation {
        public final int NORMAL = 0;
        public final int ROTATION_90 = 90;
        public final int ROTATION_180 = 180;
        public final int ROTATION_270 = 270;
    }

    public static final float CUBE[] = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    public static final float TEXTURE_ROTATED_90[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    public static final float TEXTURE_ROTATED_180[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };

    public static final float TEXTURE_ROTATED_270[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    /**
     * @param degree 0, 90, 180, 270
     * @param flipHorizontal
     * @param flipVertical
     * @return
     */
    public static float[] getRotation(final int degree, final boolean flipHorizontal, final boolean flipVertical) {
        float[] rotatedTex;
        switch (degree) {
            case Rotation.ROTATION_90:
                rotatedTex = TEXTURE_ROTATED_90;
                break;
            case Rotation.ROTATION_180:
                rotatedTex = TEXTURE_ROTATED_180;
                break;
            case Rotation.ROTATION_270:
                rotatedTex = TEXTURE_ROTATED_270;
                break;
            case Rotation.NORMAL:
            default:
                rotatedTex = TEXTURE_NO_ROTATION;
                break;
        }
        if (flipHorizontal) {
            rotatedTex = new float[]{
                    flip(rotatedTex[0]), rotatedTex[1],
                    flip(rotatedTex[2]), rotatedTex[3],
                    flip(rotatedTex[4]), rotatedTex[5],
                    flip(rotatedTex[6]), rotatedTex[7],
            };
        }
        if (flipVertical) {
            rotatedTex = new float[]{
                    rotatedTex[0], flip(rotatedTex[1]),
                    rotatedTex[2], flip(rotatedTex[3]),
                    rotatedTex[4], flip(rotatedTex[5]),
                    rotatedTex[6], flip(rotatedTex[7]),
            };
        }
        return rotatedTex;
    }

    /**
     * 在当前的基础上进行翻转
     * @param baseFlipBuffer
     * @param flipHorizontal
     * @param flipVertical
     * @return
     */
    public static float[] getRotation(final float[] baseFlipBuffer, final boolean flipHorizontal, final boolean flipVertical) {
        if (baseFlipBuffer == null) {
            return TEXTURE_NO_ROTATION;
        }
        if (flipHorizontal) {
            baseFlipBuffer[0] = flip(baseFlipBuffer[0]);
            baseFlipBuffer[2] = flip(baseFlipBuffer[2]);
            baseFlipBuffer[4] = flip(baseFlipBuffer[4]);
            baseFlipBuffer[6] = flip(baseFlipBuffer[6]);
        }
        if (flipVertical) {
            baseFlipBuffer[1] = flip(baseFlipBuffer[1]);
            baseFlipBuffer[3] = flip(baseFlipBuffer[3]);
            baseFlipBuffer[5] = flip(baseFlipBuffer[5]);
            baseFlipBuffer[7] = flip(baseFlipBuffer[7]);
        }
        return baseFlipBuffer;
    }

    private static float flip(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }


    /**
     * 按顺时针方向旋转坐标，0、90、180、270
     * @param degree
     * @param baseFlipBuffer
     * @return
     */
    public static float[] getRotation(final int degree, final float[] baseFlipBuffer) {
        if (baseFlipBuffer == null) {
            return TEXTURE_NO_ROTATION;
        }
        float[] flipBuffer = null;
        switch (degree) {
            case Rotation.ROTATION_90:
                flipBuffer = new float[8];
                flipBuffer[0] = baseFlipBuffer[2];
                flipBuffer[1] = baseFlipBuffer[3];

                flipBuffer[2] = baseFlipBuffer[6];
                flipBuffer[3] = baseFlipBuffer[7];

                flipBuffer[4] = baseFlipBuffer[0];
                flipBuffer[5] = baseFlipBuffer[1];

                flipBuffer[6] = baseFlipBuffer[4];
                flipBuffer[7] = baseFlipBuffer[5];
                break;

            case Rotation.ROTATION_180:
                flipBuffer = new float[8];
                flipBuffer[0] = baseFlipBuffer[6];
                flipBuffer[1] = baseFlipBuffer[7];

                flipBuffer[2] = baseFlipBuffer[4];
                flipBuffer[3] = baseFlipBuffer[5];

                flipBuffer[4] = baseFlipBuffer[2];
                flipBuffer[5] = baseFlipBuffer[3];

                flipBuffer[6] = baseFlipBuffer[0];
                flipBuffer[7] = baseFlipBuffer[1];
                break;

            case Rotation.ROTATION_270:
                flipBuffer = new float[8];
                flipBuffer[0] = baseFlipBuffer[4];
                flipBuffer[1] = baseFlipBuffer[5];

                flipBuffer[2] = baseFlipBuffer[0];
                flipBuffer[3] = baseFlipBuffer[1];

                flipBuffer[4] = baseFlipBuffer[6];
                flipBuffer[5] = baseFlipBuffer[7];

                flipBuffer[6] = baseFlipBuffer[2];
                flipBuffer[7] = baseFlipBuffer[3];
                break;

            case Rotation.NORMAL:
            default:
                flipBuffer = baseFlipBuffer;
                break;
        }
        return flipBuffer;
    }
}
