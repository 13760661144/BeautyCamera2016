package cn.poco.PhotoPicker;//package com.example.preview.temp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.photoview.AbsLocalPhotoPage;

/**
 * Created by lgd on 2017/9/25.
 */

public class LocalPhotoView extends AbsLocalPhotoPage
{

    private final TextView tip;

    public LocalPhotoView(@NonNull Context context)
    {
        super(context);
        tip = new TextView(getContext());
        tip.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
        LayoutParams fl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        addView(tip,fl);
    }

    @Override
    protected void fileNotExists()
    {
        tip.setText("图片已被删除");
    }
    @Override
    protected void decodingBitmap()
    {
        tip.setText("加载中...");
    }

    @Override
    protected void decodeBitmapSucceed()
    {
        tip.setText("");
    }

    @Override
    protected void decodeBitmapFailed()
    {
        tip.setText("图片解析失败");
    }

}
