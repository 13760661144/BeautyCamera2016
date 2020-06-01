package cn.poco.ad66.imp;


import android.graphics.Bitmap;

import java.util.ArrayList;

import cn.poco.ad.abs.ADAbsAdapter;

public interface IAD66Model {

    //获取原图
    public Bitmap getOrgBmp();

    //获取当前显示的图片
    public Bitmap getCurBmp();

    //设置原图
    public void setImage(Object object);

    //人脸识别
    public void faceckeck();

    //做滤镜的效果
    public void makeAD66EffectFilter();


    //做彩妆的效果
    public void makeAD66EffectMakeup();

    //设置耗时操作之后的回调
    public void setThreadCallBack(ThreadCallBack callBack);

      //改变滤镜的透明度
    public void changeProgress_filter(int value);

    //改变选中的彩妆的透明度
    public void changeProgress_makeup(int value);

    //改变滤镜的Index
    public void changeStyleIndex(int index);

    //改变唇彩的Index
    public void changeColorIndex(int index);

    //获取现在滤镜的选择的Index
    public int getStyleIndex();

    //获取现在唇彩的选择的Index
    public int getColorIndex();

      //获取现在滤镜的透明度的值
    public int getCurProgressValue();

    //获取当前选中的唇彩选项的透明度的值
    public int getCurColorProgressValue();

    //获取当前的滤镜的图片
    public Bitmap getFilterBmp();

    //获取滤镜的列表显示用到的数据
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos1();

    //获取彩妆唇彩的列表显示用到的数据
    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos2();

    //重置各个彩妆的透明度
    public void resetAlphas();

    public void clearLipEffectBmp();

    public void Clear();


    //耗时操作结束的回调
    public interface ThreadCallBack
    {
        public void finishFaceCheck();

        public void finishMakeEffect(Bitmap bmp);
    }

}
