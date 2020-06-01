package cn.poco.gifEmoji;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * Created by admin on 2017/5/12.
 * 文字编辑页面
 */

public class EditPage extends LinearLayout implements View.OnClickListener
{
    private ArrayList<GIFTextInfo> mData;
    private FrameLayout mTopControlLayout;
    private GIFCaptionMgr mCaptionManager;
    private PressedButton mBackBtn;
    private TextView mSaveBtn;
    private FrameLayout mEditLayout;
    private EditText mEditText;
    private ImageView mDeleteBtn;
    private TextView mTipsText;
    private RecyclerView mTextListView;
    private TextAdapter mAdapter;
    private ProgressBar progressBar;
    private Toast mToast;

    // 最多可以输入的名字长度(控制英文)
    public static final int MAX_WORD_NUM = 16;

    // 相册名字长度的最大字节数(控制中文,一个中文3个字节)
    public static final int MAX_BYTE_NUM = 24;

    private OnEditControlListener mControlListener;

    public EditPage(Context context)
    {
        super(context);
        ShareData.InitData(context);
        this.setBackgroundColor(0xFFF5F5F5);
        this.setOrientation(VERTICAL);
        mCaptionManager = new GIFCaptionMgr();
        initView();
    }

    private void initToast()
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getContext(), R.string.gif_text_number_tip, Toast.LENGTH_SHORT);
            LinearLayout layout = (LinearLayout) mToast.getView();
            TextView tv = (TextView) layout.findViewById(android.R.id.message);

            layout.setBackgroundDrawable(null);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.gif_edit_toast_bk);
            tv.getBackground().setAlpha((int) (255 * 0.8f));
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(80));
            tv.setLayoutParams(params);

            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
    }

    public void setText(String text)
    {
        if(text != null && mEditText != null)
        {
            if(text.equals(getResources().getString(R.string.gif_edit_add_text_tip)))
            {
                text = "";
            }
            mEditText.setText(text);
            mEditText.requestFocus();
            mEditText.setCursorVisible(true);
            Editable eb = mEditText.getText();
            mEditText.setSelection(eb.length());
        }
    }

    public void ClearMemory()
    {
        mControlListener = null;
        mTextWatcherCallback = null;
        mTextWatcher.ClearAll();
        mTextWatcher = null;
        mDeleteBtn.setOnClickListener(null);
        mAdapter.SetOnItemClickListener(null);
        mSaveBtn.setOnClickListener(null);
        mEditText.setOnClickListener(null);
        mBackBtn.setOnClickListener(null);

        if (mData != null)
        {
            mData.clear();
            mData = null;
        }

        mEditText.clearFocus();
        mEditLayout.removeView(mEditText);

        mCaptionManager.ClearMemory();
        mAsyncCallback = null;
        mCaptionManager = null;

        mTextListView.clearOnScrollListeners();
        mTextListView.setAdapter(null);

        mAdapter.ClearMemory();
        for (int i = 0; i < mTextListView.getChildCount(); i++)
        {
            View v = mTextListView.getChildAt(i);
            if (v != null && v instanceof FrameLayout)
            {
                TextView tv = (TextView) v.findViewById(R.id.edit_page_list_text);
                tv.setOnTouchListener(null);
            }
        }

        removeAllViews();
        mDeleteBtn = null;
        mTipsText = null;
        mEditText = null;
        mTextListView = null;
        mAdapter = null;
    }

    private CommonUtils.TextWatcherCallback mTextWatcherCallback = new CommonUtils.TextWatcherCallback()
    {
        @Override
        public void OutOfBounds()
        {
            initToast();
            mToast.show();
        }
    };

    private CommonUtils.MyTextWatcher mTextWatcher = new CommonUtils.MyTextWatcher(MAX_WORD_NUM, MAX_BYTE_NUM, mTextWatcherCallback)
    {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            int len = s.length();
            if (len > 0)
            {
                if (mDeleteBtn.getVisibility() == View.GONE)
                {
                    mDeleteBtn.setVisibility(VISIBLE);
                }
            }
            else
            {
                mDeleteBtn.setVisibility(GONE);
            }
        }
    };

    private void initView()
    {
        mTopControlLayout = new FrameLayout(getContext());
        mTopControlLayout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
        this.addView(mTopControlLayout, ll);
        {
            // 返回
            mBackBtn = new PressedButton(getContext());
            mBackBtn.setButtonImage(R.drawable.framework_back_btn, R.drawable.framework_back_btn, ImageUtils.GetSkinColor(), 0.5f);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            mBackBtn.setOnClickListener(this);
            mTopControlLayout.addView(mBackBtn, params);

            // 保存
            mSaveBtn = new TextView(getContext());
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            params.rightMargin = ShareData.PxToDpi_xhdpi(28);
            mSaveBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            mSaveBtn.setTextColor(ImageUtils.GetSkinColor());
            mSaveBtn.setText(R.string.gif_edit_save);
            mSaveBtn.setGravity(Gravity.CENTER);
            mSaveBtn.setOnClickListener(this);
            mTopControlLayout.addView(mSaveBtn, params);

            // 标题
            TextView mTitle = new TextView(getContext());
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            mTitle.setText(R.string.gif_edit_title);
            mTitle.setTextColor(0xff333333);
            mTopControlLayout.addView(mTitle, params);
        }

        // 输入框
        mEditLayout = new FrameLayout(getContext());
        mEditLayout.setBackgroundColor(Color.WHITE);
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        ll.topMargin = ShareData.PxToDpi_xhdpi(30);
        this.addView(mEditLayout, ll);
        {
            mEditText = new EditText(getContext());
            mEditText.setOnClickListener(this);
            mEditText.setPadding(ShareData.PxToDpi_xhdpi(1), 0, ShareData.PxToDpi_xhdpi(10), 0);
            if (Build.VERSION.SDK_INT >= 16)
            {
                mEditText.setBackground(null);
            }
            else
            {
                mEditText.setBackgroundDrawable(null);
            }
            mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mEditText.setHintTextColor(0xffc3c3c3);
            mEditText.setTextColor(0xff333333);
            mEditText.setHint(R.string.gif_edittext_hint);
            mEditText.clearFocus();
            mEditText.setSingleLine(true);
            mEditText.setGravity(Gravity.CENTER_VERTICAL);
            mEditText.addTextChangedListener(mTextWatcher);

            setCursorDrawableColor(mEditText, ImageUtils.GetSkinColor()); // 修改光标颜色

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin = ShareData.PxToDpi_xhdpi(30);
            params.gravity = Gravity.CENTER_VERTICAL;
            mEditLayout.addView(mEditText, params);

            mDeleteBtn = new ImageView(getContext());
            mDeleteBtn.setImageResource(R.drawable.beauty_login_delete_logo);
            mDeleteBtn.setVisibility(GONE);
            mDeleteBtn.setOnClickListener(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            params.rightMargin = ShareData.PxToDpi_xhdpi(10);
            mEditLayout.addView(mDeleteBtn, params);
        }

        // 推荐字幕
        FrameLayout mTipsLayout = new FrameLayout(getContext());
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(mTipsLayout, ll);
        {
            mTipsText = new TextView(getContext());
            mTipsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            mTipsText.setTextColor(0xffacacac);
            mTipsText.setText(R.string.gif_recommend);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(36), 0, ShareData.PxToDpi_xhdpi(18));
            mTipsLayout.addView(mTipsText, params);

            progressBar = new ProgressBar(getContext());
            progressBar.setVisibility(GONE);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(48));
            params.gravity = Gravity.CENTER;
            mTipsLayout.addView(progressBar, params);
        }

        // 字幕列表
        mTextListView = new RecyclerView(getContext());
        mTextListView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                CloseInput();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mTextListView.setOverScrollMode(OVER_SCROLL_NEVER);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mTextListView.setLayoutManager(mLayoutManager);
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mTextListView, ll);

        initData();
    }

    /**
     * 反射修改 EditText 光标颜色
     */
    private void setCursorDrawableColor(EditText editText, int color)
    {
        try
        {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        }
        catch (Throwable ignored)
        {

        }
    }

    private void initData()
    {
        mAdapter = new TextAdapter();
        mAdapter.SetOnItemClickListener(new TextAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(String content)
            {
                if (content != null)
                {
                    mEditText.setText(content);
                    mEditText.requestFocus();
                    mEditText.setCursorVisible(true);
                    Editable eb = mEditText.getText();
                    mEditText.setSelection(eb.length());
                    mDeleteBtn.setVisibility(VISIBLE);
                }
            }
        });
        mTextListView.setAdapter(mAdapter);

        mCaptionManager.LoadCaptionResArr(getContext(), mAsyncCallback);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mDeleteBtn)
        {
            mEditText.setText("");
            mDeleteBtn.setVisibility(GONE);
        }
        else if (v == mEditText)
        {
            mEditText.requestFocus();
            mEditText.setCursorVisible(true);
        }
        else if (v == mSaveBtn)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包_预览_文字编辑_确定);
            MyBeautyStat.onClickByRes(R.string.拍照_表情包预览页_主页面_文字编辑_确定);
            MyBeautyStat.onPageStartByRes(R.string.拍照_表情包预览页_主页面);
            CloseInput();
            if (mControlListener != null)
            {
                String text = mEditText.getText().toString();
                mControlListener.onEditSave(text);
            }
        }
        else if (v == mBackBtn)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包_预览_文字编辑_返回);
            MyBeautyStat.onClickByRes(R.string.拍照_表情包预览页_主页面_文字编辑_返回);
            MyBeautyStat.onPageStartByRes(R.string.拍照_表情包预览页_主页面);
            CloseInput();
            if (mControlListener != null)
            {
                mControlListener.onEditBack();
            }
        }
    }

    private void startLoadingCloseAnim()
    {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(progressBar, "translationY", -ShareData.PxToDpi_xhdpi(48));
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mTipsText, "alpha", 0, 1);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(progressBar, "alpha", 1, 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim1, anim2, anim3);
        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mTipsText.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                progressBar.setVisibility(GONE);
            }
        });
        set.setDuration(300);
        set.start();
    }

    private void CloseInput()
    {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public void ShowInput()
    {
        mEditText.performClick();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
    }

    public void SetOnEditControlListener(OnEditControlListener listener)
    {
        mControlListener = listener;
    }

    private GIFCaptionMgr.AsyncCallback mAsyncCallback = new GIFCaptionMgr.AsyncCallback()
    {
        @Override
        public void onStart()
        {
            mTipsText.setVisibility(INVISIBLE);
            progressBar.setVisibility(VISIBLE);
            mData = mCaptionManager.getLocalData();
            mAdapter.SetData(mData);
        }

        @Override
        public void onSucceed(ArrayList data)
        {
            if (data == null)
            {
                return;
            }

            int size = data.size();
            for (int i = size - 1; i >= 0; i--)
            {
                GIFTextInfo info = new GIFTextInfo();
                Object obj = data.get(i);
                if (obj instanceof String)
                {
                    info.mName = (String) obj;
                    info.isLocal = false;
                    mData.add(0, info);
                }
            }

            startLoadingCloseAnim();
            mAdapter.SetData(mData);
        }

        @Override
        public void onFailed()
        {
            startLoadingCloseAnim();
        }
    };
}
