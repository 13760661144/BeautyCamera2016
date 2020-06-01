package cn.poco.noseAndtooth.abs;


import android.graphics.Bitmap;

import java.util.HashMap;

public interface INATPage {

    public void setViewImage(Bitmap bmp);

    public void showWaitUI();

    public void finishFaceCheck();

    public void updateBmp(Bitmap bmp);

    public HashMap<String,Object> getBackAnimParams();

    public boolean isChanged();

    public void showExitDialog();
}
