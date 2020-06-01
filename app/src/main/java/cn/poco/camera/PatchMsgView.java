package cn.poco.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

/**
 * 预览拍照修正view
 *
 * @since v4.0
 */
public class PatchMsgView
{

    public static final String PATCH_VIEW_TAG = "patch_tag_view";

    public static final int PATCH_MSG_VIEW_BACKGROUND_COLOR = 0X9E000000;

    /**
     * {@link MsgView} or  {@link MsgView2}
     */
    public View mMsgView;

    public View mParentView;

    public static PatchMsgView createPatchView(Context context, @PatchDialogType int type, ClickListener listener)
    {
        switch (type)
        {
            case PatchDialogType.STEP_1_PATCH_TIPS:
            {
                MsgView msgView = new MsgView(context);
                msgView.setType(type);
                msgView.setListener(listener);
                msgView.initMsg();

                FrameLayout container = new FrameLayout(context);
                setBackgroundDrawable(context, container, getShapeDrawable(CameraPercentUtil.HeightPxToPercent(32), Color.WHITE));
                FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(408));
                lParams.gravity = Gravity.CENTER;
                container.addView(msgView, lParams);

                PatchMsgView patchMsgView = new PatchMsgView();
                patchMsgView.mMsgView = msgView;
                patchMsgView.mParentView = container;
                return patchMsgView;
            }
            case PatchDialogType.STEP_2_PATCH_CAMERA:
            {
                MsgView2 msgView2 = new MsgView2(context);
                msgView2.setType(type);
                msgView2.setListener(listener);

                PatchMsgView patchMsgView = new PatchMsgView();
                patchMsgView.mMsgView = msgView2;
                patchMsgView.mParentView = msgView2;
                return patchMsgView;
            }
            case PatchDialogType.STEP_3_PATCH_PICTURE:
            {
                MsgView msgView = new MsgView(context);
                msgView.setType(type);
                msgView.setListener(listener);
                msgView.initMsg();

                FrameLayout container = new FrameLayout(context);
                setBackgroundDrawable(context, container, getShapeDrawable(CameraPercentUtil.HeightPxToPercent(32), Color.WHITE));
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(581));
                lParams.gravity = Gravity.CENTER;
                container.addView(msgView, lParams);

                PatchMsgView patchMsgView = new PatchMsgView();
                patchMsgView.mMsgView = msgView;
                patchMsgView.mParentView = container;
                return patchMsgView;
            }
            case PatchDialogType.STEP_4_PATCH_FINISH:
            {
                MsgView msgView = new MsgView(context);
                msgView.setType(type);
                msgView.setListener(listener);
                msgView.initMsg();

                FrameLayout container = new FrameLayout(context);
                setBackgroundDrawable(context, container, getShapeDrawable(CameraPercentUtil.HeightPxToPercent(32), Color.WHITE));
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.HeightPxToPercent(340));
                lParams.gravity = Gravity.CENTER;
                container.addView(msgView, lParams);

                PatchMsgView patchMsgView = new PatchMsgView();
                patchMsgView.mMsgView = msgView;
                patchMsgView.mParentView = container;
                return patchMsgView;
            }
            default:
                return null;
        }
    }

    public interface ClickListener
    {
        /**
         * @param view  {@link MsgView2} or {@link MsgView}
         * @param type  {@link PatchDialogType}
         * @param which 0: save, 1:rotate
         */
        void onClick(View view, @PatchDialogType int type, int which);

        void onDismiss(View view, @PatchDialogType int type, int which);
    }

    public static class MsgView extends LinearLayout implements View.OnClickListener
    {
        @PatchDialogType
        private int mType;

        private ClickListener mListener;
        private int rotate;
        private boolean canQuit = true;

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
            initView();
        }

        public void setType(@PatchDialogType int mType)
        {
            this.mType = mType;
        }

        private void initView()
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
            PatchMsgView.setBackgroundDrawable(getContext(), rotateBtn,
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg)));

            rotateBtn.setGravity(Gravity.CENTER);
            rotateBtn.setTextColor(Color.WHITE);
            rotateBtn.setOnClickListener(this);
            btnLayout.addView(rotateBtn, lParams);

            lParams = new LayoutParams(CameraPercentUtil.WidthPxToPercent(220), CameraPercentUtil.HeightPxToPercent(78));
            lParams.setMargins(CameraPercentUtil.WidthPxToPercent(20), 0, CameraPercentUtil.WidthPxToPercent(20), 0);
            lParams.gravity = Gravity.CENTER;
            saveBtn = new Button(getContext());
            saveBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            PatchMsgView.setBackgroundDrawable(getContext(), saveBtn,
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg)));
            saveBtn.setGravity(Gravity.CENTER);
            saveBtn.setTextColor(Color.WHITE);
            saveBtn.setOnClickListener(this);
            btnLayout.addView(saveBtn, lParams);
        }

        private void initMsg()
        {
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
            if (pic != null)
            {
                pic.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
            }
        }

        public void setListener(ClickListener mListener)
        {
            this.mListener = mListener;
        }

        public int getRotate()
        {
            return rotate;
        }

        public void setCanQuitPatch(boolean quit)
        {
            canQuit = quit;
        }

        public boolean canQuitPatch()
        {
            return canQuit;
        }

        @Override
        public void onClick(View v)
        {
            canQuit = false;
            if (v == rotateBtn)
            {
                if (mType == PatchDialogType.STEP_1_PATCH_TIPS)
                {
                    //“知道了”， 退出dialog
                    canQuit = true;
                    if (mListener != null) mListener.onDismiss(MsgView.this, mType, 0);
                }
                else if (mType == PatchDialogType.STEP_3_PATCH_PICTURE)
                {
                    //矫正图片角度
                    rotate = (rotate + 90) % 360;
                    pic.setRotation(rotate);
                    canQuit = true;
                }
                else
                {
                    if (mListener != null)
                    {
                        mListener.onClick(this, mType, 0);
                    }
                }
            }
            else if (v == saveBtn)
            {
                if (mType == PatchDialogType.STEP_4_PATCH_FINISH)
                {
                    //ok完成矫正镜头
                    canQuit = true;
                    if (mListener != null)
                    {
                        mListener.onDismiss(MsgView.this, mType, 0);
                        mListener.onClick(this, mType, 1);
                    }
                }
                if (mType == PatchDialogType.STEP_3_PATCH_PICTURE)
                {
                    //完成矫正图片
                    if (mListener != null)
                    {
                        mListener.onClick(this, mType, 1);
                    }
                }
                else
                {
                    if (mListener != null)
                    {
                        mListener.onClick(this, mType, 1);
                    }
                }
            }

        }
    }

    /**
     * 主要针对居底部dialog
     */
    public static class MsgView2 extends LinearLayout implements View.OnClickListener
    {
        private @PatchDialogType
        int mType = PatchDialogType.STEP_2_PATCH_CAMERA;

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

        public void setType(@PatchDialogType int mType)
        {
            this.mType = mType;
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
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(64);
            addView(title, lp);


            msg = new TextView(getContext());
            msg.setGravity(Gravity.CENTER_HORIZONTAL);
            msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            msg.setTextColor(0xcc000000);
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(8);
            addView(msg, lp);

            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.topMargin = CameraPercentUtil.HeightPxToPercent(40);
            btnLayout = new LinearLayout(getContext());
            btnLayout.setOrientation(LinearLayout.HORIZONTAL);
            btnLayout.setGravity(Gravity.CENTER);
            addView(btnLayout, lp);

            lp = new LayoutParams(CameraPercentUtil.WidthPxToPercent(240), CameraPercentUtil.HeightPxToPercent(78));
            lp.setMargins(CameraPercentUtil.WidthPxToPercent(30), 0, CameraPercentUtil.WidthPxToPercent(30), 0);
            rotateBtn = new Button(getContext());
            rotateBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            rotateBtn.setGravity(Gravity.CENTER);
            rotateBtn.setTextColor(Color.WHITE);
            rotateBtn.setOnClickListener(this);
            rotateBtn.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                    ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_page_patch_btn_bg))));
            btnLayout.addView(rotateBtn, lp);

            lp = new LayoutParams(CameraPercentUtil.WidthPxToPercent(240), CameraPercentUtil.HeightPxToPercent(78));
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
                if (listener != null) listener.onClick(this, this.mType, 0);
            }
            else if (v == rotateBtn)
            {
                if (listener != null) listener.onClick(this, this.mType, 1);
            }
        }

        public ClickListener listener;

        public void setListener(ClickListener listener)
        {
            this.listener = listener;
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

    public static void setBackgroundDrawable(Context context, View view, Drawable drawable)
    {
        if (context != null && view != null && drawable != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                view.setBackground(drawable);
            }
            else
            {
                view.setBackgroundDrawable(drawable);
            }
        }
    }


    /**
     * 圆角边框
     *
     * @param radius 圆角大小
     * @param color  颜色
     * @return
     */
    private static final ShapeDrawable getShapeDrawable(int radius, int color)
    {
        float[] outerRadii = new float[8];
        //左上
        outerRadii[0] = radius;
        outerRadii[1] = radius;
        //右上
        outerRadii[2] = radius;
        outerRadii[3] = radius;
        //右下
        outerRadii[4] = radius;
        outerRadii[5] = radius;
        //左下
        outerRadii[6] = radius;
        outerRadii[7] = radius;

        RoundRectShape round = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shape = new ShapeDrawable(round);
        shape.getPaint().setColor(color);
        shape.getPaint().setAntiAlias(true);
        shape.getPaint().setDither(true);
        shape.getPaint().setStyle(Paint.Style.FILL);
        return shape;
    }
}
