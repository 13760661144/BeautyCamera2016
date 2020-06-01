package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/20.
 */

public class EditUserInfoBtn extends FrameLayout{
    private EdgeCircleCell mEdgeCircleCell;
    public ImageView mBackground;
    private ImageView mEditBtn;

    public EditUserInfoBtn(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mEdgeCircleCell = new EdgeCircleCell(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(41), ShareData.PxToDpi_xhdpi(41), Gravity.CENTER);
        mEdgeCircleCell.setLayoutParams(params);
        this.addView(mEdgeCircleCell);

        mBackground = new ImageView(context);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mBackground.setImageResource(R.drawable.campaigncenter_edit_bg);
        mBackground.setLayoutParams(params1);
        this.addView(mBackground);

        mEditBtn = new ImageView(context);
        mEditBtn.setImageResource(R.drawable.homes_edit_userinfo_btn);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mEditBtn.setLayoutParams(params2);
        this.addView(mEditBtn);
    }
}
