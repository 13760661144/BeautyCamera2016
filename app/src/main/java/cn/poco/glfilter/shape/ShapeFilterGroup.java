package cn.poco.glfilter.shape;

import android.content.Context;

import cn.poco.glfilter.base.AbsFilterGroup;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by zwq on 2016/12/08 15:56.<br/><br/>
 * 变形滤镜管理
 */
public class ShapeFilterGroup extends AbsFilterGroup {

    protected int mOriginId;

    public ShapeFilterGroup(Context context) {
        super(context);
    }

    private boolean isSuperShapeFilter(int filterId) {
        return filterId >= 17 && filterId <= 24;
    }

    @Override
    public void changeFilterById(int filterId) {
        mOriginId = filterId;
        boolean result = isSuperShapeFilter(filterId);
        if (result) {
            filterId = 24;//<17, 24>
        }
        super.changeFilterById(filterId);
    }

    @Override
    public boolean filterIsChange() {
        boolean result = isSuperShapeFilter(mOriginId);
        if (result) {
            if (mNewFilter != null) {
                ((SuperShapeFilter) mNewFilter).setShapeFilterId(mOriginId);
            } else if (mCurrentFilter != null) {
                ((SuperShapeFilter) mCurrentFilter).setShapeFilterId(mOriginId);
            }
            mOriginId = 0;
        }
        return super.filterIsChange();
    }

    @Override
    protected boolean isValidId(int filterId) {
        return filterId >= 0 && filterId <= 24;
    }

    @Override
    protected DefaultFilter initFilterById(int filterId) {
        DefaultFilter newFilter = null;
        int programHandle = 0;
        int pointIndexes[] = null;
        switch (filterId) {
            case 0:
                break;
            case 1:
                int indexesArray1[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray1;
                programHandle = PGLNativeIpl.loadShapeSnakefaceProgram();
                break;
            case 2:
                int indexesArray2[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray2;
                programHandle = PGLNativeIpl.loadShapeBigeyeProgram();
                break;
            case 3:
                int indexesArray3[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray3;
                programHandle = PGLNativeIpl.loadShapeBigmouthProgram();
                break;
            case 4:
                int indexesArray4[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray4;
                programHandle = PGLNativeIpl.loadShapeFaceExtrusionProgram();
                break;

            case 5:
                int indexesArray5[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray5;
                programHandle = PGLNativeIpl.loadShapeUpdownstrechProgram();
                break;
            case 6:
                int indexesArray6[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray6;
                programHandle = PGLNativeIpl.loadShapeFatfaceProgram();
                break;
            case 7:
                int indexesArray7[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray7;
                programHandle = PGLNativeIpl.loadShapeSadnessfaceProgram();
                break;
            case 8:
                int indexesArray8[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray8;
                programHandle = PGLNativeIpl.loadShapeGourdfaceProgram();
                break;
            case 9:
                int indexesArray9[] = {105, 104, 40, 35, 46, 49, 84, 90, 87, 93, 16, 1, 31, 3, 29, 7, 25, 10, 22, 12, 20, 14, 18, 15, 17, 26, 6, 98, 102, 74, 77, 82, 83, 49, 84, 90, 46};
                pointIndexes = indexesArray9;
                programHandle = PGLNativeIpl.loadShapeMosaicProgram();
                break;

            case 10:
                int indexesArray10[] = {5, 16, 27, 46, 74, 77, 84, 90, 10, 22, 13, 19};
                pointIndexes = indexesArray10;
                programHandle = PGLNativeIpl.loadShapePalmFaceProgram();
                break;
            case 11:
                int indexesArray11[] = {6, 16, 26, 46, 74, 77, 84, 90, 13, 19};
                pointIndexes = indexesArray11;
                programHandle = PGLNativeIpl.loadShapeBigeyeFatFaceProgram();
                break;
            case 12:
                int indexesArray12[] = {5, 16, 27, 46, 74, 77, 84, 90, 10, 22, 13, 19, 52, 55, 58, 61};
                pointIndexes = indexesArray12;
                programHandle = PGLNativeIpl.loadShapeBigeyeSlightFaceProgram();
                break;
            case 13:
                int indexesArray13[] = {5, 16, 27, 46, 74, 77, 84, 90, 10, 22, 13, 19};
                pointIndexes = indexesArray13;
                programHandle = PGLNativeIpl.loadShapeNaturalProgram();
                break;
            case 14:
                newFilter = new CrazyShapeFilter(mContext, 0);
                break;
            case 15:
                newFilter = new CrazyShapeFilter(mContext, 1);
                break;
            case 16:
                newFilter = new CrazyShapeFilter(mContext, 2);
                break;

            case 24:
                newFilter = new SuperShapeFilter(mContext);
                break;

            case 25:
                break;
            default:
                break;
        }
        if (newFilter == null && programHandle > 0) {
            newFilter = new FaceShapeFilter(mContext, programHandle, pointIndexes);
        }
        return newFilter;
    }

    @Override
    protected boolean isValidName(String filterName) {
        return false;
    }

    @Override
    protected DefaultFilter initFilterByName(String filterName) {
        return null;
    }

}
