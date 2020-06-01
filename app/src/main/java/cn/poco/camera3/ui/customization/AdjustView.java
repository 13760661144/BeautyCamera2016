package cn.poco.camera3.ui.customization;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera.TailorMadeConfig;
import cn.poco.camera3.info.TailorMadeItemInfo;
import cn.poco.camera3.ui.decoration.AdjustItemDecoration;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

public class AdjustView extends FrameLayout
{
    private OnParamsListener mParamsListener;

    public void SetOnParamsListener(OnParamsListener listener)
    {
        mParamsListener = listener;
    }

    public interface OnParamsListener
    {
        void onSeekProgressChanged();

        void onClickOk(boolean switchOn);

        void onClickCancel(boolean switchOn);

        void onSwitchStateChange(boolean switchOn);
    }

    private PressedButton mDelBtn;
    private SwitchView mSwitchView;
    private PressedButton mSaveBtn;

    private ColorSeekBar mSeekBar;
    private CirclePanel mCirclePanel;

    private RecyclerView mRecyclerView;

    private View mMaskView;

    private TailorMadeAdapter mAdapter;

    private int mSelType; // 正在调整的类型

    private TailorMadeConfig mTailorConfig;

    public AdjustView(@NonNull Context context)
    {
        super(context);
        initView();
    }

    public void SetTailorConfig(@NonNull TailorMadeConfig config)
    {
        mTailorConfig = config;
        InitSeekBerProgress();
    }

    private void initView()
    {
        FrameLayout topLayout = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
        addView(topLayout, params);
        {
            // 叉
            mDelBtn = new PressedButton(getContext());
            mDelBtn.setOnClickListener(mClickListener);
            mDelBtn.setButtonImage(R.drawable.beautify_cancel, R.drawable.beautify_cancel, 0.5f);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(26);
            params.gravity = Gravity.CENTER_VERTICAL;
            topLayout.addView(mDelBtn, params);

            RelativeLayout titleLayout = new RelativeLayout(getContext());
            titleLayout.setGravity(Gravity.CENTER);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            topLayout.addView(titleLayout, params);
            {
                TextView tv = new TextView(getContext());
                tv.setId(R.id.ding_zhi_adjust_title);
                tv.setText(R.string.tailor_title);
                tv.setTextColor(ImageUtils.GetSkinColor());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rl.addRule(RelativeLayout.CENTER_VERTICAL);
                titleLayout.addView(tv, rl);

                // 开关
                mSwitchView = new SwitchView(getContext());
                mSwitchView.setOnClickListener(mClickListener);
                rl = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(60), ViewGroup.LayoutParams.MATCH_PARENT);
                rl.addRule(RelativeLayout.CENTER_VERTICAL);
                rl.addRule(RelativeLayout.RIGHT_OF, R.id.ding_zhi_adjust_title);
                rl.leftMargin = CameraPercentUtil.WidthPxToPercent(12);
                titleLayout.addView(mSwitchView, rl);
            }

            // 勾
            mSaveBtn = new PressedButton(getContext());
            mSaveBtn.setOnClickListener(mClickListener);
            mSaveBtn.setButtonImage(R.drawable.beautify_ok, R.drawable.beautify_ok, ImageUtils.GetSkinColor(), 0.5f);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(32);
            topLayout.addView(mSaveBtn, params);
        }

        View line = new View(getContext());
        line.setBackgroundColor(Color.BLACK);
        line.getBackground().setAlpha((int) (255 * 0.06f));
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(1));
        params.topMargin = CameraPercentUtil.HeightPxToPercent(88);
        addView(line, params);

        // 进度
        mSeekBar = new ColorSeekBar(getContext());
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(169));
        params.topMargin = CameraPercentUtil.HeightPxToPercent(89);
        params.leftMargin = params.rightMargin = CameraPercentUtil.WidthPxToPercent(46);
        addView(mSeekBar, params);

        // 显示进度的圆
        mCirclePanel = new CirclePanel(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(258));
        addView(mCirclePanel, params);

        // tab
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new AdjustItemDecoration());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(135));
        params.gravity = Gravity.BOTTOM;
        addView(mRecyclerView, params);

        // 开关关的时候-遮罩
        mMaskView = new View(getContext());
        mMaskView.setVisibility(GONE);
        mMaskView.setClickable(true);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(304));
        params.gravity = Gravity.BOTTOM;
        addView(mMaskView, params);

        initData();
    }

    private void initData()
    {
        ArrayList<TailorMadeItemInfo> data = new ArrayList<>();
        {
            TailorMadeItemInfo info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_beauty;
            info.mText = getContext().getString(R.string.tailor_beauty_text);
            info.mIsSelected = true;
            info.mEx = TailorMadeConfig.TailorMadeType.MOPI;
            info.mUIIndex = data.size();
            data.add(info);

            info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_skin;
            info.mEx = TailorMadeConfig.TailorMadeType.SKINBEAUTY;
            info.mText = getContext().getString(R.string.tailor_skin_beauty_text);
            info.mUIIndex = data.size();
            data.add(info);

            info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_face;
            info.mText = getContext().getString(R.string.tailor_thin_face_text);
            info.mEx = TailorMadeConfig.TailorMadeType.SHOULIAN;
            info.mUIIndex = data.size();
            data.add(info);

            info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_eye;
            info.mText = getContext().getString(R.string.tailor_big_eye_text);
            info.mEx = TailorMadeConfig.TailorMadeType.DAYAN;
            info.mUIIndex = data.size();
            data.add(info);

            info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_nose;
            info.mText = getContext().getString(R.string.tailor_thin_nose_text);
            info.mEx = TailorMadeConfig.TailorMadeType.SHOUBI;
            info.mUIIndex = data.size();
            data.add(info);

            info = new TailorMadeItemInfo();
            info.mResId = R.drawable.tailor_made_tooth;
            info.mEx = TailorMadeConfig.TailorMadeType.MEIYA;
            info.mText = getContext().getString(R.string.tailor_beauty_tooth_text);
            info.mUIIndex = data.size();
            data.add(info);
        }

        mAdapter = new TailorMadeAdapter(getContext(), data);
        mAdapter.SetOnItemClickListener(mRecyclerItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置美形定制开关
     */
    public void SetSwitchState(boolean isOn)
    {
        mSwitchView.setSwitchOn(isOn);
    }

    public void SetControlUIEnable(boolean enable)
    {
        mMaskView.setVisibility(enable ? GONE : VISIBLE);
        SetControlBtnAlpha(enable ? 1 : 0.2f);
    }

    private void SetControlBtnAlpha(float alpha)
    {
        mSeekBar.setAlpha(alpha);
        mRecyclerView.setAlpha(alpha);
    }

    public void SetSeekBarProgress(int progress)
    {
        if (mSeekBar != null)
        {
            mSeekBar.setProgress(progress);
        }
    }

    public void InitSeekBerProgress()
    {
        if (mTailorConfig != null && mSeekBar != null)
        {
            mSeekBar.setProgress(mTailorConfig.get(mSelType));
        }
    }

    /**
     * 默认选中重置为 0
     */
    public void ResetSelState()
    {
        mSelType = TailorMadeConfig.TailorMadeType.MOPI;

        if (mAdapter != null)
        {
            mAdapter.SetSelIndex(0);
        }
//        InitSeekBerProgress();
    }

    private OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mParamsListener != null)
            {
                if (v == mSwitchView)
                {
                    boolean isOn = mSwitchView.isSwitchOn(); // 已经改变的状态
                    TongJi2.AddCountByRes(getContext(), isOn? R.integer.拍照_开启美形定制 : R.integer.拍照_关闭美形定制);
                    mParamsListener.onSwitchStateChange(isOn);
                }
                else if (v == mSaveBtn)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_确定);
                    mParamsListener.onClickOk(mSwitchView.isSwitchOn());
                }
                else if (v == mDelBtn)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_退出);
                    mParamsListener.onClickCancel(mSwitchView.isSwitchOn());
                }
            }
        }
    };

    private TailorMadeAdapter.OnItemClickListener mRecyclerItemClickListener = new TailorMadeAdapter.OnItemClickListener()
    {
        @Override
        public void onItemClick(View v, int type)
        {
            mSelType = type;
            int size = mTailorConfig.get(mSelType);
            switch (type)
            {
                case TailorMadeConfig.TailorMadeType.MOPI:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_美颜);
                    break;
                }
                case TailorMadeConfig.TailorMadeType.SKINBEAUTY:
                {
                    break;
                }
                case TailorMadeConfig.TailorMadeType.SHOULIAN:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_瘦脸);
                    break;
                }
                case TailorMadeConfig.TailorMadeType.DAYAN:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_大眼);
                    break;
                }
                case TailorMadeConfig.TailorMadeType.SHOUBI:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_瘦鼻);
                    break;
                }
                case TailorMadeConfig.TailorMadeType.MEIYA:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_美牙);
                }
            }

            SetSeekBarProgress(size);
        }
    };

    private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener()
    {

        @Override
        public void onProgressChanged(ColorSeekBar seekBar, int progress)
        {
            mTailorConfig.set(mSelType, progress);
            showCircle(seekBar, progress);
            mParamsListener.onSeekProgressChanged();
        }

        @Override
        public void onStartTrackingTouch(ColorSeekBar seekBar)
        {
            mTailorConfig.set(mSelType, seekBar.getProgress());
            showCircle(seekBar, seekBar.getProgress());
            mParamsListener.onSeekProgressChanged();
        }

        @Override
        public void onStopTrackingTouch(ColorSeekBar seekBar)
        {
            mCirclePanel.hide();
            mTailorConfig.set(mSelType, seekBar.getProgress());
            mParamsListener.onSeekProgressChanged();
        }
    };

    private void showCircle(ColorSeekBar seekBar, int progress)
    {
        float radius = progress / 100f + ShareData.PxToDpi_xhdpi(55);
        int seekBarWidth = seekBar.getWidth();
        float circleX = ShareData.PxToDpi_xhdpi(50) / 2 + CameraPercentUtil.WidthPxToPercent(61) + progress / 100f * (seekBarWidth - ShareData.PxToDpi_xhdpi(50));
        float circleY = ShareData.PxToDpi_xhdpi(79);
        mCirclePanel.change(circleX, circleY, radius);
        mCirclePanel.setText(String.valueOf(progress));
        mCirclePanel.show();
    }

    public void ClearMemory()
    {
        mAdapter.SetOnItemClickListener(null);
        mSeekBar.setOnSeekBarChangeListener(null);
        mSaveBtn.setOnClickListener(null);
        mDelBtn.setOnClickListener(null);
        mSwitchView.setOnClickListener(null);

        mClickListener = null;
        mRecyclerItemClickListener = null;
        mSeekBarListener = null;
        mParamsListener = null;

        mSwitchView.ClearMemory();
    }
}
