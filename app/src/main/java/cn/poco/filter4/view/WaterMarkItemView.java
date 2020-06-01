package cn.poco.filter4.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;

/**
 * @author lmx
 *         Created by lmx on 2017/3/28.
 */

public class WaterMarkItemView extends FrameLayout {

    public ImageView mImg;

    public View mLine;

    /**
     * @param context
     * @param type    0:空状态水印 1:默认水印
     */
    public WaterMarkItemView(@NonNull Context context, int type) {
        super(context);

        mImg = new ImageView(context);
        mImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        LayoutParams fp;
        if (type == 0) {
            fp = new LayoutParams(ShareData.PxToDpi_xhdpi(140), ShareData.PxToDpi_xhdpi(140));
            fp.topMargin = ShareData.PxToDpi_xhdpi(46);
            fp.bottomMargin = ShareData.PxToDpi_xhdpi(46);
            fp.leftMargin = ShareData.PxToDpi_xhdpi(27);
            fp.rightMargin = ShareData.PxToDpi_xhdpi(17);
            fp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        } else {
            fp = new LayoutParams(ShareData.PxToDpi_xhdpi(200), ShareData.PxToDpi_xhdpi(180));
            fp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            fp.topMargin = ShareData.PxToDpi_xhdpi(26);
            fp.bottomMargin = ShareData.PxToDpi_xhdpi(26);
            fp.leftMargin = ShareData.PxToDpi_xhdpi(20);
            fp.rightMargin = ShareData.PxToDpi_xhdpi(20);
        }

        this.addView(mImg, fp);

        mLine = new View(context);

        mLine.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988));
        mLine.setVisibility(INVISIBLE);
        if (type == 0) {
            fp = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(6));
            fp.leftMargin = ShareData.PxToDpi_xhdpi(27);
            fp.rightMargin = ShareData.PxToDpi_xhdpi(17);
        } else {
            fp = new LayoutParams(ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(6));
        }
        fp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        fp.bottomMargin = ShareData.PxToDpi_xhdpi(12);
        mLine.setLayoutParams(fp);
        this.addView(mLine);
    }

    public void SetSelected(boolean selected) {
        mLine.setVisibility(selected ? VISIBLE : INVISIBLE);
    }
}
