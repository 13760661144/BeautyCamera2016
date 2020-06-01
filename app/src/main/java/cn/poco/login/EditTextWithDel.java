package cn.poco.login;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.poco.advanced.ImageUtils;
import cn.poco.utils.CommonUI;

public class EditTextWithDel extends AppCompatEditText
{
	private final static String TAG = "EditTextWithDel";
    private Drawable imgInable;
    private Drawable imgAble;
    private Context mContext;
    private TextWatcher textWatcher = null;
	public EditTextWithDel(Context context,int imgInableRes,int imgAbleRes) {
        super(context);	
		mContext = context;
		init(imgInableRes,imgAbleRes);
	}
	
	public EditTextWithDel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditTextWithDel(Context context) {
		super(context);
	}

	private void init(int imgInableRes, int imgAbleRes) {
        if(imgInableRes != -1){
            imgInable = mContext.getResources().getDrawable(imgInableRes);
        }
        if(imgAbleRes != -1){
            imgAble = mContext.getResources().getDrawable(imgAbleRes);
        }
        if(textWatcher == null){
            textWatcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setDrawable();
                }
            };
        }
        addTextChangedListener(textWatcher);
        setDrawable();
        CommonUI.modifyEditTextCursor(this, ImageUtils.GetSkinColor(0xffe75988));
    }

	public void setDrawable() {
		if(length() < 1){
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable, null);
        }
        else{
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
        }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - imgAble.getIntrinsicWidth() - 20;
            if(rect.contains(eventX, eventY) && isEnabled())
                setText("");
        }
		return super.onTouchEvent(event);
	}
	
	

}
