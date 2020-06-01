package cn.poco.filterBeautify;

import android.graphics.Bitmap;

/**
 * @author lmx
 *         Created by lmx on 2017/4/25.
 */

public class FilterBeautifyInfo
{
    public FilterBeautyParams mFilterBeautyParams;

    //in
    public Object imgs;//必须

    //out
    public Bitmap out;

    public boolean m_filter;//是否滤镜效果
    public boolean m_dark;//是否暗角
    public boolean m_blur;//是否虚化

    public boolean m_shape;//是否美形
    public boolean m_beauty;//是否美颜

    private int m_shape_size = 5;//美形处理人脸个数（最大5个人脸）


    public int m_filter_alpha;
    public int m_filter_uri;//0为原图处理

    public FilterBeautifyInfo()
    {
    }
}
