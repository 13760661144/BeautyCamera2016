package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.tianutils.ShareData;

/**
 * Created by shine on 2016/10/12.
 */

public class MenuCellHorizontal extends BaseMenuCell {

    public MenuCellHorizontal(Context context) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        mIconImage = new ImageView(context);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.leftMargin = ShareData.PxToDpi_xhdpi(45);
        this.addView(mIconImage, params1);

        mTextBadgeView = new TextBadgeView(context);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params2.leftMargin = ShareData.PxToDpi_xhdpi(35);
        mTextBadgeView.setLayoutParams(params2);
        this.addView(mTextBadgeView);
    }



}




