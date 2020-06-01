package cn.poco.noseAndtooth.abs;


import android.graphics.Bitmap;

public interface INATModel {

    public interface ThreadCallBack
    {
        public void updateBmp(Bitmap bmp);

        public void finishFaceCheck();
    }

    public void setOriInfo(Object object);


    public void setUpdateBmpCB(INATModel.ThreadCallBack cb);

    public String getTitle();

    public int getIconRes();

    public Bitmap getOrgBmp();

    public Bitmap getCurBmp();

    public int getProgress();

    public void facecheck();

    public void changeProgress(int progress);

    public void makeEffectBmp();

    public int initProgressValue();

    public void Clear();

}
