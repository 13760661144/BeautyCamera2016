package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.view.View;

import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2016/11/23.
 */

public class EmptyCell extends View {
    private int mCellHeight;
    private Context mContext;

    public EmptyCell(Context context) {
        this(context, ShareData.PxToDpi_xhdpi(6));
    }
    public EmptyCell(Context context, int height) {
        super(context);
        mContext = context;
        this.mCellHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mCellHeight, MeasureSpec.EXACTLY));
    }
}
