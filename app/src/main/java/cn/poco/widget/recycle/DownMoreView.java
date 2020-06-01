package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/7/10.
 */

public class DownMoreView extends BaseDownMoreRecommendView
{
    protected static final int BLUR_R = 6;
    protected static final int OFFSET = 4;
    protected static final int SPACE_W = 11;

    private ImageView mlogoView;
    private TextView mNumView;

    public DownMoreView(Context context, @NonNull AbsConfig mConfig, Object[] logos, int num)
    {
        super(context, mConfig, logos, num);
    }

    @Override
    public void initView()
    {
        super.initView();
        mlogoView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(mW, mW);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        mBaseFr.addView(mlogoView, params);
        Bitmap img = createImg();
        if (img != null)
        {
            mlogoView.setImageBitmap(img);
        }

        FrameLayout bottomFr = new FrameLayout(getContext());
        bottomFr.setBackgroundColor(Color.WHITE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mH - mW);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mBaseFr.addView(bottomFr, params);
        {
            Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_download_logo2);
            temp = ImageUtils.AddSkin(getContext(), temp);
            ImageView icon = new ImageView(getContext());
            icon.setImageBitmap(temp);
            params = new LayoutParams(temp.getWidth(), temp.getHeight());
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            params.leftMargin = ShareData.PxToDpi_xhdpi(15);
            bottomFr.addView(icon, params);

            mNumView = new TextView(getContext());
            mNumView.setGravity(Gravity.CENTER);
            mNumView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 6f);
            mNumView.setTextColor(Color.WHITE);
            mNumView.setBackgroundResource(R.drawable.material_not_download_tip);
            if (mNum > 0) {
                if (mNumView.getVisibility() != View.VISIBLE) {
                    mNumView.setVisibility(View.VISIBLE);
                }
                mNumView.setText(String.valueOf(mNum));
            } else {
                mNumView.setVisibility(View.GONE);
            }
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.leftMargin = ShareData.PxToDpi_xhdpi(32);
            params.topMargin = ShareData.PxToDpi_xhdpi(2);
            bottomFr.addView(mNumView, params);

            TextView textView2 = new TextView(getContext());
            textView2.setText(R.string.recommend_download_more);
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
            textView2.setTextColor(ImageUtils.GetSkinColor(Color.RED));
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            params.leftMargin = ShareData.PxToDpi_xhdpi(54);
            bottomFr.addView(textView2, params);
        }
    }

    @Override
    public void SetData(Object[] logos, int num, int index)
    {
        super.SetData(logos, num, index);
        Bitmap img = createImg();
        if (img != null)
        {
            mlogoView.setImageBitmap(img);
        }
        if (mNum > 0) {
            if (mNumView.getVisibility() != View.VISIBLE) {
                mNumView.setVisibility(View.VISIBLE);
            }
            mNumView.setText(String.valueOf(mNum));
        } else {
            mNumView.setVisibility(View.GONE);
        }
    }

    public Bitmap createImg()
    {
        if (mLogos == null || mLogos.length == 0) return null;

        Bitmap bitmap = Bitmap.createBitmap(mW, mW, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //画图
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x33ffffff);
        canvas.drawRect(0, 0, mW, mW, paint);
        int len = mLogos.length - 1;
        if (len > 3)
        {
            len = 3;
        }
        int subW = ShareData.PxToDpi_xhdpi(115);
        int allSubW = subW;
        int sw = ShareData.PxToDpi_xhdpi(SPACE_W);
        for (int i = 0; i < len - 1; i++)
        {
            allSubW += sw;
        }
        allSubW += BLUR_R;
        float ex = (mW - allSubW) / 2f + (len - 1) * sw;
        float ey = ex;
        for (int i = len - 1; i > -1; i--)
        {
            DrawSubBmp(getContext(), canvas, mLogos[i + 1], ex, ey, subW, subW);
            ex -= sw;
            ey -= sw;
        }

        return bitmap;
    }

    private static void DrawSubBmp(Context context, Canvas canvas, Object res, float x, float y, int w, int h)
    {
        Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(context, res, 0, -1, -1, -1);
        if (bmp != null)
        {
            //阴影
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setColor(0x20000000);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(BLUR_R, ShareData.PxToDpi_xhdpi(OFFSET), ShareData.PxToDpi_xhdpi(OFFSET), 0x20000000);
            canvas.drawRect(x, y, x + w, x + h, paint);

            //画图
            Matrix matrix = new Matrix();
            matrix.postScale((float) w / (float) bmp.getWidth(), (float) h / (float) bmp.getHeight());
            matrix.postTranslate(x, y);
            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bmp, matrix, paint);
        }
    }
}
