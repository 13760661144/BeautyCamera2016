package cn.poco.gldraw2;

import java.util.ArrayList;

import cn.poco.image.PocoFace;

public class FaceDataHelper {

    private static FaceDataHelper sInstance;

    public static FaceDataHelper getInstance() {
        if (sInstance == null) {
            synchronized (FaceDataHelper.class) {
                if (sInstance == null) {
                    sInstance = new FaceDataHelper();
                }
            }
        }
        return sInstance;
    }

    private FaceDataHelper() {
    }

    private ArrayList<PocoFace> mTempFaces;
    private boolean mFaceDataIsChange;

    private int mFaceSize;
    private ArrayList<PocoFace> mFaceList;
    private PocoFace mFace;

    public void setFaceData(ArrayList<PocoFace> faces) {
        mTempFaces = faces;
        mFaceDataIsChange = true;
    }

    public FaceDataHelper checkAndConvertData() {
        if (!mFaceDataIsChange) {
            return sInstance;
        }
        mFaceDataIsChange = false;

        mFaceList = mTempFaces;
        mTempFaces = null;
        mFace = null;
        if (mFaceList == null || mFaceList.isEmpty()) {
            mFaceSize = 0;
        } else {
            PocoFace face = null;
            for (int i = 0; i < mFaceList.size(); i++) {
                face = mFaceList.get(i);
                if (face != null) {
                    face.calculateOpenGLPoints();
                    face = null;
                }
            }
            mFaceSize = mFaceList.size();
            if (mFaceSize > 0) {
                mFace = mFaceList.get(0);
            }
        }
        return sInstance;
    }

    public FaceDataHelper changeFace(int faceIndex) {
        if (mFaceSize > 1 && mFaceList != null && faceIndex < mFaceList.size()) {
            mFace = mFaceList.get(faceIndex);
        }
        return sInstance;
    }

    public int getFaceSize() {
        return mFaceSize;
    }

    public ArrayList<PocoFace> getFaceList() {
        return mFaceList;
    }

    public PocoFace getFace() {
        if (mFace != null && mFace.mGLPoints == null) {
            mFace.calculateOpenGLPoints();
        }
        return mFace;
    }

    public void clearAll() {
        if (mTempFaces != null) {
            mTempFaces.clear();
            mTempFaces = null;
        }
        mFaceDataIsChange = false;
        if (mFaceList != null) {
            mFaceList.clear();
            mFaceList = null;
        }
        mFaceSize = 0;
        mFace = null;

        sInstance = null;
    }
}
