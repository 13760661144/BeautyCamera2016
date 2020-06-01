package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/5/20.
 */
public class ThemeItem extends FrameLayout {
    private FrameLayout m_container;
    private ImageView m_themeImg;
    private ImageView m_lockIcon;
    private TextView m_themeName;
    private ImageView m_arrowBtn;
    private BaseItemInfo m_info;

    private int mCurrentSkinColor;

    public ThemeItem(Context context) {
        super(context);
        mCurrentSkinColor = ImageUtils.GetSkinColor();
        initUI();
    }

    private void initUI() {
//        int viewWidth = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(40);
        int mViewWidth = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(40);
        int mViewHeight = (int) (mViewWidth * 271f / 678f);
        int mArrowHeight = (int) (mViewWidth * 251f / 678f * 62f / 251f);
        if (ShareData.m_screenHeight < 856) {
            float scale = 678f / 720f;

            mViewWidth = (int) (ShareData.m_screenWidth * scale);
            mViewHeight = (int) (mViewWidth * 271f / 678f);
            mArrowHeight = (int) (mViewWidth * 251f / 678f * 62f / 251f);
        }

        m_container = new FrameLayout(getContext());
        m_container.setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(20));
        FrameLayout.LayoutParams fl_lp = new FrameLayout.LayoutParams(mViewWidth, mViewHeight);
        fl_lp.gravity = Gravity.CENTER;
        m_container.setLayoutParams(fl_lp);
        this.addView(m_container);

        m_themeImg = new ImageView(getContext());
        m_themeImg.setBackgroundDrawable(DrawableUtils.shapeDrawable(0xffffffff, ShareData.PxToDpi_xhdpi(10)));
        m_themeImg.setScaleType(ImageView.ScaleType.FIT_XY);
        fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        fl_lp.gravity = Gravity.CENTER;
        m_container.addView(m_themeImg, fl_lp);

        FrameLayout frameLayout = new FrameLayout(getContext());
        fl_lp = new FrameLayout.LayoutParams(mViewWidth, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
        frameLayout.setLayoutParams(fl_lp);
        m_container.addView(frameLayout);

        m_themeName = new TextView(getContext());
        m_themeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        m_themeName.setTextColor(0xc7000000);
        fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        fl_lp.setMargins(ShareData.PxToDpi_xhdpi(20), 0, 0, 0);
        frameLayout.addView(m_themeName, fl_lp);

        m_arrowBtn = new ImageView(getContext());
        m_arrowBtn.setScaleType(ImageView.ScaleType.CENTER);
        m_arrowBtn.setImageResource(R.drawable.new_material4_arrow_btn);
        fl_lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mArrowHeight);
        fl_lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        fl_lp.setMargins(0, 0, ShareData.PxToDpi_xhdpi(20), 0);
        frameLayout.addView(m_arrowBtn, fl_lp);

        m_lockIcon = new ImageView(getContext());
        m_lockIcon.setImageResource(R.drawable.new_materia4l_theme_lock);
        m_lockIcon.setVisibility(View.GONE);
        fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        fl_lp.rightMargin = ShareData.PxToDpi_xhdpi(30) + ShareData.PxToDpi_xhdpi(20);
        frameLayout.addView(m_lockIcon, fl_lp);
        ImageUtils.AddSkin(getContext(), m_lockIcon);
    }

    public void setThemeBmp(Bitmap bmp) {
        m_themeImg.setImageBitmap(bmp);
    }

    public void setItemInfo(BaseItemInfo info) {
        if (info != null) {
            m_info = info;
            m_themeName.setText(info.m_name);
            if (info.m_lock) {
                m_lockIcon.setVisibility(View.VISIBLE);
            } else {
                m_lockIcon.setVisibility(View.GONE);
            }
        }
    }

    public void unlock() {
        m_info.m_lock = false;
        m_lockIcon.setVisibility(View.GONE);
    }

    public void updateSkin() {
        if (m_lockIcon != null && ImageUtils.GetSkinColor() != mCurrentSkinColor) {
            ImageUtils.AddSkin(getContext(), m_lockIcon);
            mCurrentSkinColor = ImageUtils.GetSkinColor();
        }
    }
}
