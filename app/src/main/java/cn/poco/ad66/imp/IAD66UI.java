package cn.poco.ad66.imp;


import android.graphics.Bitmap;

public interface IAD66UI {

    //显示耗时操作时弹出的等待ui
    public void ShowWaitUI();

    //结束人脸识别时调用的回调
    public void finishFaceCheck();

    //设置图片
    public void setImageBmp(Bitmap bmp);

    //更新做效果之后的图片
    public void updateBmp(Bitmap bmp);

    //隐藏耗时操作时弹出的等待ui
    public void dismissWaitUI();

    //获取当前的模式
    public int getCurMode();

}
