package cn.poco.campaignCenter.widget.Actionbar;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;


/**
 * Created by admin on 2016/7/27.
 */
public class ActionBar extends FrameLayout{
    public static class onActionbarMenuItemClick {
        public void onItemClick(int id){
        };
    }
    private TextView actionbarTitle;
    private ImageView leftImageBtn;
    private ImageView rightImageBtn;
    private TextView leftTextBtn;
    private TextView rightTextBtn;

    private Context mContext;
    private onActionbarMenuItemClick mListener;

    public final static int LEFT_MENU_ITEM_CLICK = 0;
    public final static int RIGHT_MENU_ITEM_CLICK = 1;


    public ActionBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        this.setBackgroundColor(Color.WHITE);
    }

    // 设置actionbar的背景颜色，默认为白色.
    public void setUpActionbarBackgroundColor(int color) {
    	this.setBackgroundColor(color);
    }

    // 设置actionbar的标题.
    public void setUpActionbarTitle(CharSequence title) {
        if (actionbarTitle == null) {
            createActionbarTitle( -1, -1.0f);
        }
        actionbarTitle.setText(title);
    }

    // 设置actionbar的标题，字体大小和颜色.
    public void setUpActionbarTitle(CharSequence title, int textColor, float textSize) {
        if (actionbarTitle == null) {
            createActionbarTitle(textColor, textSize);
        }
        actionbarTitle.setText(title);
    }

    public void setUpActionbarTitle(int titleId, int textColor, float textSize) {
        if (actionbarTitle == null) {
            createActionbarTitle(textColor, textSize);
        }
        actionbarTitle.setText(getResources().getString(titleId));
    }



    // 设置actionbar左边按钮的图片.
    public void setUpLeftImageBtn(int resId) {
        if (leftImageBtn == null) {
            createLeftImageBtn();
        }
        leftImageBtn.setImageResource(resId);
        leftImageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(LEFT_MENU_ITEM_CLICK);
                }
            }
        });
    }

    // 获取左边Icon按钮的引用
    public ImageView getLeftImageBtn() {
        return leftImageBtn;
    }


    // 设置actionbar右边按钮的图片
    public void setUpRightImageBtn(int resId) {
        if (rightImageBtn == null) {
            createRightImageBtn();
        }
        rightImageBtn.setImageResource(resId);
        rightImageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(RIGHT_MENU_ITEM_CLICK);
                }
            }
        });
    }

    // 获取右边icon按钮的引用
    public ImageView getRightImageBtn() {
        return rightImageBtn;
    }


    // 设置actionbar左边的文字按钮
    public void setLeftTextBtn(CharSequence content) {
        if (leftTextBtn == null) {
            createLeftTextTBtn(-1, -1.0f);
        }
        leftTextBtn.setText(content);
        leftTextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(LEFT_MENU_ITEM_CLICK);
                }
            }
        });
    }

    // 设置actionbar左边的文字按钮，并设置颜色和字体大小
    public void setLeftTextBtn(CharSequence content, int color, float size) {
        if (leftTextBtn == null) {
            createLeftTextTBtn(color, size);
        }
        leftTextBtn.setText(content);
        leftTextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(LEFT_MENU_ITEM_CLICK);
                }
            }
        });
    }
    
    
    // 设置actionbar右边的文字按钮.
    public void setRightTextBtn(CharSequence content) {
        if (rightTextBtn == null) {
            createRightTextBtn(-1, -1.0f);
        }
        rightTextBtn.setText(content);
        rightTextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(RIGHT_MENU_ITEM_CLICK);
                }
            }
        });
    }
    

    // 设置actionbar右边的文字按钮，并设置颜色和字体大小.
    public void setRightTextBtn(CharSequence content, int color, float size) {
        if (rightTextBtn == null) {
            createRightTextBtn(color, size);
        }
        rightTextBtn.setText(content);
        rightTextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(RIGHT_MENU_ITEM_CLICK);
                }
            }
        });
    }

    private void createActionbarTitle(int textColor, float textSize) {
        actionbarTitle = new TextView(mContext);
        actionbarTitle.setGravity(Gravity.CENTER);
//        actionbarTitle.setPadding(0, 15, 0, 15);

        if (textColor != -1) {
            actionbarTitle.setTextColor(textColor);
        } else {
            actionbarTitle.setTextColor(Color.BLACK);
        }

        if (textSize != -1) {
            actionbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        } else {
            actionbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        this.addView(actionbarTitle, layoutParams);
    }

    private void createLeftImageBtn() {
        leftImageBtn = new ImageView(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        this.addView(leftImageBtn, layoutParams);
    }

    private void createRightImageBtn() {
        rightImageBtn = new ImageView(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        this.addView(rightImageBtn, layoutParams);
    }

    private void createLeftTextTBtn(int textColor, float textSize) {
        leftTextBtn = new TextView(mContext);
        leftTextBtn.setGravity(Gravity.CENTER);

        if (textColor != -1) {
        leftTextBtn.setTextColor(textColor);
        } else {
        leftTextBtn.setTextColor(Color.parseColor("#ffd600"));
        }
        
        if (textSize != -1.0f) {
        	leftTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        } else {
        	leftTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        layoutParams.leftMargin = ShareData.PxToDpi_xhdpi(18);
        this.addView(leftTextBtn, layoutParams);
    }

    private void createRightTextBtn(int textColor, float textSize) {
        rightTextBtn = new TextView(mContext);
        rightTextBtn.setGravity(Gravity.CENTER);
        
        if (textColor != -1) {
        rightTextBtn.setTextColor(textColor);
        } else {
        rightTextBtn.setTextColor(Color.parseColor("#ffd600"));
        }
        
        if (textSize != -1.0f) {
        	rightTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        } else {
        	rightTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = ShareData.PxToDpi_xhdpi(15);
        this.addView(rightTextBtn, layoutParams);
    }

    // 获取actionbar的标题文字.
    public CharSequence getActionbarTitle() {
        return actionbarTitle.getText().toString();
    }

    // 获取actionbar右边文字按钮的引用.
    public TextView getRightTextBtn() {
    	if (rightTextBtn != null) {
    		return rightTextBtn;
    	}
    	return null;
    }


    // 设置actionbar菜单按钮的监听器
    public void setOnActionbarMenuItemClick(onActionbarMenuItemClick listener) {
        this.mListener = listener;
    }

}
