package cn.poco.makeup.makeup2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import cn.poco.recycleview.AbsExConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


public class Makeup2ListConfig extends AbsExConfig {

    public Bitmap m_subItemSelBmp;
    public int def_sub_title_size = 30;
    public int def_sub_title_sel_size = ShareData.PxToDpi_xhdpi(17);
    public int def_title_color_out = Color.WHITE;
    public int def_alphafr_leftMargin = ShareData.PxToDpi_xhdpi(30);
    public Bitmap m_maskBmp;
    public Bitmap m_subItemColorBmp;
    public Bitmap m_alphaSelectBmp;
    public Context m_context;

    public Makeup2ListConfig(Context context)
    {
        m_context = context;
    }


    @Override
    public void InitData() {
        ClearAll();
        def_item_w = (int) (ShareData.PxToDpi_xhdpi(160)*0.91f);
        def_item_h = (int) (ShareData.PxToDpi_xhdpi(206)*0.91f);
        def_item_l = ShareData.PxToDpi_xhdpi(17);
        def_parent_left_padding = ShareData.PxToDpi_xhdpi(17);
        def_parent_right_padding = ShareData.PxToDpi_xhdpi(12);

        def_sub_w = (int) (ShareData.PxToDpi_xhdpi(135)* 0.91f);
        def_sub_h = (int) (ShareData.PxToDpi_xhdpi(147)*0.91f);
        def_sub_l = ShareData.PxToDpi_xhdpi(18);
        def_sub_padding_l = ShareData.PxToDpi_xhdpi(10);
        def_sub_padding_r = ShareData.PxToDpi_xhdpi(16);

        def_parent_top_padding = ShareData.PxToDpi_xhdpi(22);
        def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(22);
        def_parent_center_x = (int) (ShareData.m_screenWidth/2f);

        m_maskBmp = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.beautify_makeup2_mask_bmp);
        m_subItemColorBmp = Bitmap.createBitmap(def_sub_w, def_sub_h, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void ClearAll() {
        if(m_subItemColorBmp != null)
        {
            m_subItemColorBmp = null;
        }

        if(m_subItemSelBmp != null)
        {
            m_subItemSelBmp = null;
        }

        if(m_maskBmp != null)
        {
            m_maskBmp = null;
        }

        if(m_alphaSelectBmp != null)
        {
            m_alphaSelectBmp = null;
        }
    }
}
