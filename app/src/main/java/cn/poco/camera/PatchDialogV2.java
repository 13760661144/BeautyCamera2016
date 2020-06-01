package cn.poco.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * 预览拍照修正dialog
 *
 * @since v4.0
 */
public class PatchDialogV2 extends AlertDialogV1 implements View.OnClickListener
{
    @PatchDialogType
    private int mType;

    private int rotate;
    private boolean canQuit = true;

    private MsgView mMsgView;

    /**
     * @param type 1:开始提示，2:镜头校正，3:照片校正，4:完成 ,{@link PatchDialogType}
     */
    public PatchDialogV2(Context context, @PatchDialogType int type)
    {
        super(context);
        mType = type;
        initView();
        initMsg();
    }

    public static class MsgView extends LinearLayout
    {
        protected TextView title;
        protected ImageView pic;
        protected TextView msg;
        protected TextView msg2;
        protected LinearLayout btnLayout;
        protected Button rotateBtn;
        protected Button saveBtn;

        public MsgView(Context context)
        {
            super(context);
        }

        public void init(OnClickListener listener)
        {
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER);

            LayoutParams lParams = new LayoutParams(CameraPercentUtil.WidthPxToPercent(218), CameraPercentUtil.HeightPxToPercent(326));
            lParams.gravity = Gravity.CENTER_HORIZONTAL;
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(20);
            pic = new ImageView(getContext());
            addView(pic, lParams);

            lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            title = new TextView(getContext());
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
            title.getPaint().setFakeBoldText(true);
            title.setTextColor(0xff333333);
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            addView(title, lParams);

            lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            msg = new TextView(getContext());
            msg.setGravity(Gravity.CENTER_HORIZONTAL);
            msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
            msg.setTextColor(0xff333333);
            addView(msg, lParams);

            lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            msg2 = new TextView(getContext());
            msg2.setGravity(Gravity.CENTER_HORIZONTAL);
            msg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
            msg2.setTextColor(0xff333333);
            msg2.setVisibility(View.GONE);
            addView(msg2, lParams);

            lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(20);
            lParams.bottomMargin = CameraPercentUtil.HeightPxToPercent(40);

            btnLayout = new LinearLayout(getContext());
            btnLayout.setOrientation(LinearLayout.HORIZONTAL);
            btnLayout.setGravity(Gravity.CENTER);
            addView(btnLayout, lParams);

            lParams = new LayoutParams(CameraPercentUtil.WidthPxToPercent(220), CameraPercentUtil.HeightPxToPercent(78));
            lParams.setMargins(CameraPercentUtil.WidthPxToPercent(20), 0, CameraPercentUtil.WidthPxToPercent(20), 0);
            lParams.gravity = Gravity.CENTER;
            rotateBtn = new Button(getContext());
            rotateBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            PatchDialogV2.setBackgroundDrawable(getContext(), rotateBtn,
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg)));

            rotateBtn.setGravity(Gravity.CENTER);
            rotateBtn.setTextColor(Color.WHITE);
            rotateBtn.setOnClickListener(listener);
            btnLayout.addView(rotateBtn, lParams);

            lParams = new LayoutParams(CameraPercentUtil.WidthPxToPercent(220), CameraPercentUtil.HeightPxToPercent(78));
            lParams.setMargins(CameraPercentUtil.WidthPxToPercent(20), 0, CameraPercentUtil.WidthPxToPercent(20), 0);
            lParams.gravity = Gravity.CENTER;
            saveBtn = new Button(getContext());
            saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            PatchDialogV2.setBackgroundDrawable(getContext(), saveBtn,
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg)));
            saveBtn.setGravity(Gravity.CENTER);
            saveBtn.setTextColor(Color.WHITE);
            saveBtn.setOnClickListener(listener);
            btnLayout.addView(saveBtn, lParams);
        }
    }

    /**
     * 主要针对居底部dialog
     */
    public static class MsgView2 extends LinearLayout implements View.OnClickListener
    {
        private TextView title;
        private TextView msg;
        private LinearLayout btnLayout;
        private Button rotateBtn;
        private Button saveBtn;

        public MsgView2(Context context)
        {
            super(context);
            initView();
        }

        private void initView()
        {
            LayoutParams lp;
            setOrientation(VERTICAL);
            setBackgroundColor(Color.WHITE);
            setClickable(true);

            title = new TextView(getContext());
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
            title.getPaint().setFakeBoldText(true);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(64);
            addView(title, lp);


            msg = new TextView(getContext());
            msg.setGravity(Gravity.CENTER_HORIZONTAL);
            msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            msg.setTextColor(0xcc000000);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(8);
            addView(msg, lp);

            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(40);
            btnLayout = new LinearLayout(getContext());
            btnLayout.setOrientation(LinearLayout.HORIZONTAL);
            btnLayout.setGravity(Gravity.CENTER);
            addView(btnLayout, lp);

            lp = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(240), CameraPercentUtil.HeightPxToPercent(78));
            lp.setMargins(CameraPercentUtil.WidthPxToPercent(30), 0, CameraPercentUtil.WidthPxToPercent(30), 0);
            rotateBtn = new Button(getContext());
            rotateBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            rotateBtn.setGravity(Gravity.CENTER);
            rotateBtn.setTextColor(Color.WHITE);
            rotateBtn.setOnClickListener(this);
            rotateBtn.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg))));
            btnLayout.addView(rotateBtn, lp);

            lp = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(240), CameraPercentUtil.HeightPxToPercent(78));
            lp.setMargins(CameraPercentUtil.WidthPxToPercent(30), 0, CameraPercentUtil.WidthPxToPercent(30), 0);
            saveBtn = new Button(getContext());
            saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            saveBtn.setGravity(Gravity.CENTER);
            saveBtn.setTextColor(Color.WHITE);
            saveBtn.setOnClickListener(this);
            saveBtn.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg))));
            btnLayout.addView(saveBtn, lp);

            title.setText(R.string.camerapage_patch_view_tips_type_1_title);
            msg.setText(R.string.camerapage_patch_view_tips_type_1_content);
            rotateBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_left);
            saveBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_right);
        }

        @Override
        public void onClick(View v)
        {
            if (v == saveBtn)
            {
                if (listener != null) listener.onClick(0);
            }
            else if (v == rotateBtn)
            {
                if (listener != null) listener.onClick(1);
            }
        }

        public ClickListener listener;

        public void setListener(ClickListener listener)
        {
            this.listener = listener;
        }

        public interface ClickListener
        {
            /**
             * @param which 0: save, 1:rotate
             */
            void onClick(int which);
        }
    }

    private void initView()
    {
        LinearLayout.LayoutParams lParams = null;
        switch (mType)
        {
            case PatchDialogType.STEP_1_PATCH_TIPS:
            {
                lParams = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(408));
                mMsgView = new MsgView(getContext());
                mMsgView.init(this);
            }
            break;
            case PatchDialogType.STEP_2_PATCH_CAMERA:
            {
                //移到page构造view，不使用dialog，
            }
            break;
            case PatchDialogType.STEP_3_PATCH_PICTURE:
            {
                lParams = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(581));
                mMsgView = new MsgView(getContext());
                mMsgView.init(this);
            }
            break;
            case PatchDialogType.STEP_4_PATCH_FINISH:
            {
                lParams = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(340));
                mMsgView = new MsgView(getContext());
                mMsgView.init(this);
            }
            break;
        }

        if (mMsgView != null && lParams != null)
        {
            addContentView(mMsgView, lParams);
            setRadius(CameraPercentUtil.HeightPxToPercent(32));
        }
    }

    private static void setBackgroundDrawable(Context context, View view, Bitmap bitmap)
    {
        if (context != null && view != null && bitmap != null && !bitmap.isRecycled())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                view.setBackground(new BitmapDrawable(context.getResources(), bitmap));
            }
            else
            {
                view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
            }
        }
    }

    private void initMsg()
    {
        if (mMsgView == null)
        {
            return;
        }

        /**
         * 1:
         * 拍照方向修正工具
         * 如果你遇到拍照方向总是\n不对的问题，此向导可帮助解决。\n\n(也可以在"设置"进入)
         * 开始校对
         * 知道了
         * 2：
         * 第一步，请保持手机竖直
         * 现在看到的图像预览方向正确吗？
         * 不对，旋转      方向正确
         * 3：
         * 第二步，这张是自动拍下的
         * ----
         * 请问上面这张图像方向正确吗？
         * 不对，旋转      方向正确
         * 4：
         * 好了，校正完成
         * 关闭
         */

        LinearLayout.LayoutParams lParams;
        TextView title = mMsgView.title;
        TextView msg = mMsgView.msg;
        TextView msg2 = mMsgView.msg2;
        ImageView pic = mMsgView.pic;
        Button rotateBtn = mMsgView.rotateBtn;
        Button saveBtn = mMsgView.saveBtn;
        LinearLayout btnLayout = mMsgView.btnLayout;

        if (mType == PatchDialogType.STEP_1_PATCH_TIPS)
        {
            title.setText(R.string.camerapage_patch_view_tips_type_0_title);
            lParams = (LinearLayout.LayoutParams) title.getLayoutParams();
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(44);
            title.requestLayout();


            lParams = (LinearLayout.LayoutParams) msg.getLayoutParams();
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(17);
            msg.requestLayout();

            lParams = (LinearLayout.LayoutParams) msg2.getLayoutParams();
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(26);
            lParams.bottomMargin = CameraPercentUtil.HeightPxToPercent(12);
            msg2.requestLayout();
            msg2.setVisibility(View.VISIBLE);

            msg.setText(R.string.camerapage_patch_view_tips_type_0_content);
            msg2.setText(R.string.camerapage_patch_view_tips_type_0_content2);

            pic.setVisibility(View.GONE);
            rotateBtn.setText(R.string.camerapage_patch_view_tips_type_0_btn_left);
            saveBtn.setText(R.string.camerapage_patch_view_tips_type_0_btn_right);
        }
        else if (mType == PatchDialogType.STEP_2_PATCH_CAMERA)
        {
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f);
            msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
            msg.setTextColor(0xcc000000);
            title.setText(R.string.camerapage_patch_view_tips_type_1_title);
            pic.setVisibility(View.GONE);
            msg.setText(R.string.camerapage_patch_view_tips_type_1_content);
            rotateBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_left);
            saveBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_right);

        }
        else if (mType == PatchDialogType.STEP_3_PATCH_PICTURE)
        {
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            lParams = (LinearLayout.LayoutParams) title.getLayoutParams();
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(15);
            title.requestLayout();

            lParams = (LinearLayout.LayoutParams) msg.getLayoutParams();
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(8);
            msg.requestLayout();

            msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.0f);
            msg.setTextColor(0xcc000000);

            title.setText(R.string.camerapage_patch_view_tips_type_2_title);
            msg.setText(R.string.camerapage_patch_view_tips_type_2_content);
            rotateBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_left);
            saveBtn.setText(R.string.camerapage_patch_view_tips_type_1_btn_right);

        }
        else if (mType == PatchDialogType.STEP_4_PATCH_FINISH)
        {
            lParams = (LinearLayout.LayoutParams) title.getLayoutParams();
            lParams.gravity = Gravity.CENTER;
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(83);
            title.requestLayout();
            title.getPaint().setFakeBoldText(false);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f);
            title.setText(R.string.camerapage_patch_view_tips_type_3_title);
            pic.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            rotateBtn.setVisibility(View.GONE);

            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            lParams.topMargin = CameraPercentUtil.HeightPxToPercent(83);
            lParams.bottomMargin = CameraPercentUtil.HeightPxToPercent(40);
            btnLayout.setLayoutParams(lParams);

            lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(420), CameraPercentUtil.HeightPxToPercent(84));
            lParams.setMargins(CameraPercentUtil.WidthPxToPercent(15), 0, CameraPercentUtil.WidthPxToPercent(15), 0);
            saveBtn.setBackgroundDrawable(DrawableUtils.shapeDrawable(true, true, true, true, ImageUtils.GetSkinColor(0xffe75887), 60));

            saveBtn.setLayoutParams(lParams);
            saveBtn.setText(R.string.close);
        }
    }

    public void setPicture(Bitmap bitmap)
    {
        if (bitmap == null || bitmap.isRecycled())
        {
            return;
        }
        if (mMsgView != null && mMsgView.pic != null)
        {
            mMsgView.pic.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
        }
    }

    public int getRotate()
    {
        return rotate;
    }

    @Override
    public void onClick(View v)
    {
        if (mMsgView != null)
        {
            canQuit = false;
            if (v == mMsgView.rotateBtn)
            {
                if (mType == PatchDialogType.STEP_3_PATCH_PICTURE)
                {
                    rotate = (rotate + 90) % 360;
                    mMsgView.pic.setRotation(rotate);
                    canQuit = true;
                }
                else
                {
                    if (mListener != null)
                    {
                        mListener.onClick(this, 0);
                    }
                    if (mType == PatchDialogType.STEP_1_PATCH_TIPS)
                    {
                        canQuit = true;
                        this.dismiss();
                    }
                }

            }
            else if (v == mMsgView.saveBtn)
            {
                if (mType == PatchDialogType.STEP_4_PATCH_FINISH)
                {
                    canQuit = true;
                }
                if (mListener != null)
                {
                    mListener.onClick(this, 1);
                }
                this.dismiss();
            }
        }
    }

    public void setCanQuitPatch(boolean quit)
    {
        canQuit = quit;
    }

    public boolean canQuitPatch()
    {
        return canQuit;
    }
}
