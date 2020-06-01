package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2017/7/24.
 */

public class MenuCellVetical extends BaseMenuCell{

    public MenuCellVetical(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        mIconImage = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIconImage.setLayoutParams(params);
        this.addView(mIconImage);

        mTextBadgeView = new TextBadgeView(context);
        mTextBadgeView.menuText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTextBadgeView.menuText.setTextColor(0xff4c4c4c);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(12);
        mTextBadgeView.setLayoutParams(params);
        this.addView(mTextBadgeView);
    }


}
