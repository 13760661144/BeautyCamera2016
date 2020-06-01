package cn.poco.ad65.imp;


import android.graphics.Bitmap;

public interface IAD65UI {

    public void updateBmp(Bitmap bmp);

    public void dismissWaitDlg();

    public void showWaitDlg();

    public int getCurPage();

    public Bitmap getOutputBmp();

    public int getCurFrameIndex();
}
