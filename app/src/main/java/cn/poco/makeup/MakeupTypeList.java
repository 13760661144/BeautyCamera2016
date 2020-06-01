package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;


public class MakeupTypeList extends HorizontalScrollView{

    protected LinearLayout m_main;
    protected MakeupTypeListCallBack m_cb;
    protected BaseItem m_curChooseView;
    protected ArrayList<BaseItem> m_views;
    protected int m_curSelIndex = 0;
    protected boolean m_once = false;
    public MakeupTypeList(Context context) {
        super(context);
        initUI();
    }

    public MakeupTypeList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public MakeupTypeList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI()
    {
        this.setHorizontalScrollBarEnabled(false);
        m_main = new LinearLayout(getContext());
        m_main.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        m_main.setLayoutParams(fl);
        this.addView(m_main);
    }

    public void SetData(ArrayList<BaseItem> views, MakeupTypeListCallBack cb)
    {
        if(views != null && views.size() > 0)
        {
            if(cb != null)
            {
                m_cb = cb;
            }
            m_views = views;
            for(BaseItem view: views)
            {
                if(view != null)
                {
                    if(view.m_index == m_curSelIndex)
                    {
                        m_curChooseView = view;
                        view.SetChoose();
                    }
                    else
                    {
                        view.ClearChoose();
                    }
                    m_main.addView(view);
                    view.setOnClickListener(m_onclickListener);
                    if(!m_once)
                    {
                        m_once = true;
                        ImageView vLine = new ImageView(getContext());
                        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT);
                        vLine.setLayoutParams(ll);
                        vLine.setBackgroundColor(0x1a000000);//0xff9c9c9c
                        m_main.addView(vLine);
                    }
                }
            }
        }
    }

    public void SetSelectIndex(int index)
    {
        if(m_views != null && m_views.size() > 0)
        {
            for(BaseItem item: m_views)
            {
                if(item.m_index == index)
                {
                    if(m_curChooseView != null)
                    {
                        m_curChooseView.ClearChoose();
                    }
                    BaseItem view =  item;
                    m_curChooseView = view;
                    view.SetChoose();
                    m_curSelIndex = view.m_index;
                    scrollToCenter(view);


                    m_curSelIndex = index;
                    m_onclickListener.onClick(item);
                    break;
                }
            }
        }
    }


    View.OnClickListener m_onclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_cb != null)
            {
                BaseItem view = (BaseItem) v;
                if(m_curChooseView != null)
                {
                    m_curChooseView.ClearChoose();
                }
                if(view != null)
                {
                    m_curChooseView = view;
                    m_curSelIndex = view.m_index;
                    m_curChooseView.SetChoose();
                }
                m_cb.ItemOnclick(v,view.m_type,view.m_index);
            }
        }
    };

    private void scrollToCenter(View view)
    {
        int left = view.getLeft() - Math.abs(this.getScrollX());
        int centerPos = (int) ((this.getWidth() - view.getWidth())/2f);
        int dis = 0;
        if(left < centerPos)
        {
            dis = centerPos - left;
            if(this.getScrollX() >= dis)
            {
                this.smoothScrollBy(-dis,0);
            }
            else
            {
                this.smoothScrollBy(-this.getScrollX(),0);
            }
        }
        else
        {
            dis = left - centerPos;
            int temp = m_main.getWidth() - this.getScrollX() - this.getWidth();
            if(temp >= dis)
            {
                this.smoothScrollBy(dis,0);
            }
            else
            {
                this.smoothScrollBy(temp,0);
            }
        }

    }

    public void ClearAll()
    {
        if(m_main != null)
        {
            this.removeView(m_main);
        }
        if(m_views != null)
        {
            m_views.clear();
            m_views = null;
        }
        if(m_cb != null)
        {
            m_cb = null;
        }
    }

    interface MakeupTypeListCallBack
    {
        public void ItemOnclick(View view,int type,int index);
    }


    public static abstract class BaseItem extends FrameLayout
    {
        public int m_type = -1;
        public int m_index = -1;
        public BaseItem(Context context) {
            super(context);
        }

        public BaseItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BaseItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public abstract void SetChoose();

        public abstract void ClearChoose();

    }

    public static class MakeupTypeItem extends BaseItem
    {
        public static final int ALL = 0;//主题彩妆
        public static final int FOUNDATION = 1;//粉底
        public static final int CHEEK_L = 2;//腮红
        public static final int LIP = 3;//唇彩
        public static final int EYEBROW_L = 4;//眉毛
        public static final int KOHL_L = 5;//眼影
        public static final int EYELINER_UP_L = 6;//眼线
        public static final int EYELASH_UP_L = 7;//睫毛
        public static final int EYE_L = 8;//美瞳

        private int m_norRes = -1;
        private int m_hoverRes = -1;
        private ImageView m_img;
        private TextView m_text;
        private ImageView m_bottomLine;
        private static boolean m_once = false;

        public MakeupTypeItem(Context context) {
            super(context);
        }

        public MakeupTypeItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MakeupTypeItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void SetData(int normolRes , int hoverRes, String title)
        {
            m_norRes = normolRes;
            m_hoverRes = hoverRes;

            m_bottomLine = new ImageView(getContext());
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(this.getWidth(),ShareData.PxToDpi_xhdpi(4));
            fl.gravity = Gravity.BOTTOM;
            m_bottomLine.setLayoutParams(fl);
            Bitmap bkBmp = Bitmap.createBitmap(1,ShareData.PxToDpi_xhdpi(4), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bkBmp);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xffe85b8a);
            tempCanvas.drawRect(new RectF(0,0,1,ShareData.PxToDpi_xhdpi(4)),paint);
            ImageUtils.AddSkin(getContext(),bkBmp,0xffe85b8a);
            m_bottomLine.setImageBitmap(bkBmp);
            m_bottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
            m_bottomLine.setVisibility(GONE);
            this.addView(m_bottomLine);

            final LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int width = linearLayout.getWidth();
                    FrameLayout.LayoutParams temp = (LayoutParams) m_bottomLine.getLayoutParams();
                    temp.width = width;
                    m_bottomLine.setLayoutParams(temp);
                    linearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(50));
            fl.gravity = Gravity.CENTER;
            this.addView(linearLayout);
            linearLayout.setLayoutParams(fl);
            m_img = new ImageView(getContext());
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ll.leftMargin = ShareData.PxToDpi_xhdpi(20);
            m_img.setLayoutParams(ll);
            if(normolRes != -1)
            {
                m_img.setImageResource(normolRes);
            }
            linearLayout.addView(m_img);

            m_text = new TextView(getContext());
            ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ll.rightMargin = ShareData.PxToDpi_xhdpi(20);
            ll.gravity = Gravity.CENTER_VERTICAL;
            m_text.setLayoutParams(ll);
            m_text.setGravity(Gravity.CENTER);
            m_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
            m_text.setTextColor(Color.BLACK);
            if(title != null && title.length() > 0)
            {
                m_text.setText(title);
            }
            linearLayout.addView(m_text);
            ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(70));
            this.setLayoutParams(ll);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        public void SetChoose()
        {
            if(m_hoverRes != -1)
            {
                m_img.setImageResource(m_hoverRes);
                ImageUtils.AddSkin(getContext(),m_img,0xffe75988);
            }
            if(m_text != null)
            {
                m_text.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
            }

            if(m_bottomLine != null)
            {
                m_bottomLine.setVisibility(VISIBLE);
            }
        }

        @Override
        public void ClearChoose()
        {
            if(m_norRes != -1)
            {
                m_img.setImageResource(m_norRes);
                ImageUtils.RemoveSkin(getContext(),m_img);
            }
            if(m_text != null)
            {
                m_text.setTextColor(Color.BLACK);
            }
            if(m_bottomLine != null)
            {
                m_bottomLine.setVisibility(GONE);
            }
        }

        public static int getItemIndexByType(ArrayList<BaseItem> items,int type)
        {
            int out = -1;
            if(items!= null && items.size() > 0)
            {
                for(int i = 0; i < items.size() ; i++)
                {
                    if(items.get(i).m_type == type)
                    {
                        out = items.get(i).m_index;
                        break;
                    }
                }
            }
            return out;
        }
    }
}
