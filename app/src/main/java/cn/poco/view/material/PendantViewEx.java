package cn.poco.view.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.MotionEvent;

import java.util.List;

import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.view.RelativeView;
import my.beautyCamera.R;

/**
 * 贴图
 * Created by admin on 2017/1/13.
 */

public class PendantViewEx extends RelativeView
{
    protected Shape def_delete_res; // 删除 --> 左上角
    protected Shape def_flip_res; // 翻转 --> 右上角
    protected Shape def_rotation_res; // 其他变换 --> 右\左下角
    protected boolean drawDeleteBtn = false;
    protected boolean drawFlipBtn = false;
    protected boolean drawRotateBtn = false;
    protected boolean m_clickRotateBtn = false;// 是否点击旋转按钮
    protected boolean m_clickFlipBtn = false;// 是否点击镜像按钮

    public PendantViewEx(Context context, ControlCallback cb)
    {
        super(context);
        m_cb = cb;
    }

    @Override
    protected void InitData()
    {
        super.InitData();

        def_flip_res = new Shape();
        def_flip_res.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_flip);

        def_delete_res = new Shape();
        def_delete_res.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_delete);

        def_rotation_res = new Shape();
        def_rotation_res.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_rotation);
    }

    @Override
    protected float getScaleByH(Shape target)
    {
        float[] curPos = getImgLogicPos(target);
        if (curPos != null)
        {
            float curImgH = ImageUtils.Spacing(curPos[0] - curPos[6], curPos[1] - curPos[7]);
            float[] src = new float[]{target.m_bmp.getWidth(), target.m_bmp.getHeight()};// 根据画布大小来控制素材原始高
            global.m_matrix.mapPoints(src);
            float orgImgH = src[1];
            return curImgH / orgImgH;
        }
        return -1;
    }

    @Override
    protected float getScaleByW(Shape target)
    {
        float[] curPos = getImgLogicPos(target);
        if (curPos != null)
        {
            float curImgW = ImageUtils.Spacing(curPos[0] - curPos[2], curPos[1] - curPos[3]);
            float[] src = new float[]{target.m_bmp.getWidth(), target.m_bmp.getHeight()};// 根据画布大小来控制素材原始宽
            global.m_matrix.mapPoints(src);
            float orgImgW = src[0];
            return curImgW / orgImgW;
        }
        return -1;
    }

    @Override
    protected void updateContent(int width, int height)
    {
        super.updateContent(width, height);
        updateResBtn(mTarget);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 画贴图、毛玻璃
        if (mPendantArr != null && mPendantArr.size() > 0)
        {
            for (Shape pendant : mPendantArr)
            {
                if (pendant != null && pendant.m_bmp != null && !pendant.m_bmp.isRecycled())
                {
                    canvas.save();
                    canvas.translate(canvas_l, canvas_t);
                    mPaint.reset();
                    mPaint.setAntiAlias(true);
                    mPaint.setFilterBitmap(true);
                    canvas.concat(global.m_matrix);
                    canvas.drawBitmap(pendant.m_bmp, pendant.m_matrix, mPaint);
                    canvas.restore();
                }
            }
        }

        // 画贴图的边框和按钮
        if (mPendantArr.size() > 0)
        {
            for (Shape pendant : mPendantArr)
            {
                if (mTarget == pendant)
                {
                    canvas.save();
                    mPaint.reset();
                    mPaint.setAntiAlias(true);
                    mPaint.setStrokeWidth(1);
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setColor(Color.WHITE);
                    canvas.translate(canvas_l, canvas_t);
                    canvas.concat(global.m_matrix);
                    canvas.concat(pendant.m_matrix);
                    canvas.drawRect(0, 0, pendant.m_bmp.getWidth(), pendant.m_bmp.getHeight(), mPaint);
                    canvas.restore();

                    if (drawDeleteBtn)
                    {
                        drawBtn(canvas, def_delete_res);
                    }

                    if (drawFlipBtn)
                    {
                        drawBtn(canvas, def_flip_res);
                    }

                    if (drawRotateBtn)
                    {
                        drawBtn(canvas, def_rotation_res);
                    }
                    break;
                }
            }
        }
    }

    private void drawBtn(Canvas canvas, Shape target)
    {
        canvas.save();
        canvas.translate(canvas_l, canvas_t);
        canvas.concat(global.m_matrix);
        canvas.drawBitmap(target.m_bmp, target.m_matrix, mPaint);
        canvas.restore();
    }

    @Override
    protected void OddDown(MotionEvent event)
    {
        mTween.M1End();
        getShowMatrix();
        if (m_clickRotateBtn)
        {
            float[] imgPos = getImgShowPos(mTarget);
            if (imgPos != null)
            {
                Init_RZ_Data_Btn(mTarget, (imgPos[0] + imgPos[4]) / 2f, (imgPos[1] + imgPos[5]) / 2f, mDownX, mDownY);
            }
        }
        isDrawResBtn(false, false, false);
        Init_M_Data(mTarget, mDownX, mDownY);
        invalidate();
    }

    @Override
    protected void OddMove(MotionEvent event)
    {
        if (!m_clickFlipBtn && !m_clickRotateBtn)// 点击镜像按钮时不能移动
        {
            super.OddMove(event);
        }
        else if (m_clickRotateBtn)
        {
            float[] imgPos = getImgShowPos(mTarget);
            if (imgPos != null)
            {
                Run_RZ_Btn(mTarget, (imgPos[0] + imgPos[4]) / 2f, (imgPos[1] + imgPos[5]) / 2f, event.getX(), event.getY());
            }
            this.invalidate();
        }
    }

    @Override
    protected void OddUp(MotionEvent event)
    {
        super.OddUp(event);
        if (m_clickRotateBtn)
        {
            m_clickRotateBtn = false;
        }

        if (m_clickFlipBtn)
        {
            m_clickFlipBtn = false;
        }
        updateResBtn(mTarget);
        invalidate();
    }

    @Override
    protected void EvenDown(MotionEvent event)
    {
        mTween.M1End();
        Init_MRZ_Data(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
        this.invalidate();
    }

    @Override
    protected void EvenMove(MotionEvent event)
    {
        if (!m_clickRotateBtn)
        {
            Run_MRZ(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        }
        this.invalidate();
    }

    @Override
    protected void Run_MRZ(Shape target, float x1, float y1, float x2, float y2)
    {
        target.m_matrix.set(mOldMatrix);
        Run_R(target, x1, y1, x2, y2);
        Run_Z(target, x1, y1, x2, y2);
        if (!m_clickRotateBtn)
        {
            Run_M(target.m_matrix, (x1 + x2) / 2f, (y1 + y2) / 2f);
        }
    }


    private void Init_RZ_Data_Btn(Shape target, float x1, float y1, float x2, float y2)
    {
        mOldMatrix.set(target.m_matrix);
        Init_R_Data(x1, y1, x2, y2);
        Init_Z_Data(x1, y1, x2, y2);
    }

    protected void Run_RZ_Btn(Shape target, float x1, float y1, float x2, float y2)
    {
        target.m_matrix.set(mOldMatrix);
        Run_R_Btn(target, x1, y1, x2, y2);
        Run_Z_Btn(target, x1, y1, x2, y2);
    }

    private void Run_Z_Btn(Shape target, float x1, float y1, float x2, float y2)
    {
        float scale = 1f;
        Matrix record = new Matrix();
        record.set(target.m_matrix);
        // 计算down时图片的缩放比例
        float sw1 = getScaleByW(target);
        float sh1 = getScaleByH(target);

        float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
        if (tempDist > 10)
        {
            scale = tempDist / mDelta;
        }

        Run_Z_Btn(target, scale, scale);

        // 计算move后图片的缩放比例
        float sw2 = getScaleByW(target);
        float sh2 = getScaleByH(target);
        float newScaleX = 1f, newScaleY = 1f;

        if (target instanceof ShapeEx)
        {
            float def_img_min_scale = ((ShapeEx) target).MIN_SCALE;
            float def_img_max_scale = ((ShapeEx) target).MAX_SCALE;
            if (sw2 != -1 && sw1 != -1)
            {
                // 限制图片缩放比例
                if (sw2 <= def_img_min_scale)
                {
                    sw2 = def_img_min_scale;
                }

                if (sw2 >= def_img_max_scale)
                {
                    sw2 = def_img_max_scale;
                }
                newScaleX = sw2 / sw1;
            }

            if (sh2 != -1 && sh1 != -1)
            {
                // 限制图片缩放比例
                if (sh2 <= def_img_min_scale)
                {
                    sh2 = def_img_min_scale;
                }

                if (sh2 >= def_img_max_scale)
                {
                    sh2 = def_img_max_scale;
                }
                newScaleY = sh2 / sh1;
            }

            if (sw2 == def_img_min_scale || sw2 == def_img_max_scale)
            {
                newScaleY = newScaleX;
            }

            if (sh2 == def_img_min_scale || sh2 == def_img_max_scale)
            {
                newScaleX = newScaleY;
            }

            // 恢复缩放前的状态
            target.m_matrix.set(record);

            Run_Z_Btn(target, newScaleX, newScaleY);
        }
    }

    private void Run_Z_Btn(Shape target, float scaleX, float scaleY)
    {
        if (target == global) // 整体缩放
        {
            target.m_matrix.postScale(scaleX, scaleY, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
        }
        else
        {
            float[] imgPos = getImgLogicPos(target);
            float[] src = new float[]{(imgPos[0] + imgPos[4]) / 2f, (imgPos[1] + imgPos[5]) / 2f};
            float[] dst = new float[src.length];
            Matrix[] matrices = new Matrix[]{global.m_matrix};
            inverseCount(dst, src, matrices);
            target.m_matrix.postScale(scaleX, scaleY, dst[0], dst[1]);
        }
    }

    private void Run_R_Btn(Shape target, float x1, float y1, float x2, float y2)
    {
        float tempAngle;
        if (x1 - x2 == 0)
        {
            if (y1 >= y2)
            {
                tempAngle = 90;
            }
            else
            {
                tempAngle = -90;
            }
        }
        else if (y1 - y2 != 0)
        {
            tempAngle = (float) Math.toDegrees(Math.atan(((double) (y1 - y2)) / (x1 - x2)));
            if (x1 < x2)
            {
                tempAngle += 180;
            }
        }
        else
        {
            if (x1 >= x2)
            {
                tempAngle = 0;
            }
            else
            {
                tempAngle = 180;
            }
        }

        float[] imgPos = getImgLogicPos(target);
        float[] src = new float[]{(imgPos[0] + imgPos[4]) / 2f, (imgPos[1] + imgPos[5]) / 2f};
        float[] dst = new float[src.length];
        Matrix[] matrices = new Matrix[]{global.m_matrix};
        inverseCount(dst, src, matrices);

        target.m_matrix.postRotate(tempAngle - mBeta, dst[0], dst[1]);
    }


    /**
     * 添加饰品
     */
    public int addPendant(Object info, Bitmap bmp)
    {
        int out = -1;
        Shape item = new ShapeEx();
        if (bmp != null)
        {
            item.m_bmp = bmp;
        }
        else
        {
            item.m_bmp = m_cb.MakeShowPendant(info, getWidth(), getHeight());
        }

        if (item.m_bmp != null && !item.m_bmp.isRecycled())
        {
            item.m_ex = info;
            mPendantArr.add(item);
            out = mPendantArr.size() - 1;
            // 为了与图片相对global的缩放比例一致
            syncScaling();

            if (ShareData.m_screenWidth <= 480)
            {
                ((ShapeEx) item).SetScaleXY(0.6f, 0.6f);
            }
            else if (ShareData.m_screenWidth <= 720)
            {
                ((ShapeEx) item).SetScaleXY(0.8f, 0.8f);
            }

            float left = canvas_l;
            float top = canvas_t;
            float right = canvas_r;
            float bottom = canvas_b;

            //将图片平移到中心位置
            float[] imgRect = getImgLogicRect(item);
            float imgCenterX = (imgRect[0] + imgRect[2]) / 2f;
            float imgCenterY = (imgRect[1] + imgRect[3]) / 2f;

            float[] src = new float[]{(right - left) / 2f - imgCenterX, (bottom - top) / 2f - imgCenterY};
            Matrix[] matrices = new Matrix[]{global.m_matrix};
            inverseCount(src, matrices);
            item.m_matrix.postTranslate(src[0], src[1]);
            mTarget = item;
            updateResBtn(item);

            invalidate();
        }

        return out;
    }

    /**
     * 更新按钮位置
     *
     * @param item 小饰品素材
     * @param res  三个小图标，删除、镜像、旋转
     */
    private void resetResBtn(Shape item, Shape res)
    {
        if (item.m_bmp == null)
        {
            return;
        }
        float dis = res.m_bmp.getWidth() / 2f;
        float[] itemPts = getImgLogicPos(item); // 小饰品图片
        if (itemPts != null)
        {
            float[] rectPos = getImgLogicRect(item);// img外切矩形的坐标
            float[] change = new float[rectPos.length];
            // 扩大外切矩形的范围
            change[0] = rectPos[0] - dis;
            change[1] = rectPos[1] - dis;
            change[2] = rectPos[2] + dis;
            change[3] = rectPos[3] + dis;

            // 求出扩大范围后与原来的宽高比例
            float wScale = (change[2] - change[0]) / (rectPos[2] - rectPos[0]);
            float hScale = (change[3] - change[1]) / (rectPos[3] - rectPos[1]);
            float scale = Math.min(wScale, hScale);

            // 用宽高比例将图片图片缩放，---> 在img外再计算一层隐藏矩形，用来放置res图标
            Matrix temp = new Matrix();
            temp.set(item.m_matrix);

            float[] src = new float[]{(itemPts[0] + itemPts[4]) / 2f, (itemPts[1] + itemPts[5]) / 2f};
            Matrix[] matrices = new Matrix[]{global.m_matrix};
            inverseCount(src, matrices);

            item.m_matrix.postScale(scale, scale, src[0], src[1]);
            float[] hideRect = getImgLogicPos(item);
            item.m_matrix.set(temp);
            // 隐藏矩形的坐标
            // left-top
            float hr_lt_x = hideRect[0];
            float hr_lt_y = hideRect[1];
            // right-top
            float hr_rt_x = hideRect[2];
            float hr_rt_y = hideRect[3];
            // right-bottom
            float hr_rb_x = hideRect[4];
            float hr_rb_y = hideRect[5];
            // left-bottom
            float hr_lb_x = hideRect[6];
            float hr_lb_y = hideRect[7];

            boolean isFlip = false;// 判断是否进行了镜像变换
            if (item instanceof ShapeEx)
            {
                // 镜像的次数为偶数，不用改变lt-rt,lb-rb
                float flipCount = ((ShapeEx) item).mFlipCount;
                if (flipCount >= 0 && flipCount % 2 != 0)
                {
                    isFlip = true;
                    float mid = hr_rt_x;
                    hr_rt_x = hr_lt_x;
                    hr_lt_x = mid;

                    mid = hr_rt_y;
                    hr_rt_y = hr_lt_y;
                    hr_lt_y = mid;

                    mid = hr_lb_y;
                    hr_lb_y = hr_rb_y;
                    hr_rb_y = mid;

                    mid = hr_lb_x;
                    hr_lb_x = hr_rb_x;
                    hr_rb_x = mid;
                }
            }

            res.m_matrix.reset();
            global.m_matrix.invert(res.m_matrix);

            float[] resPts = getImgLogicPos(res);// 计算res图标位置
            if (resPts != null && resPts.length == 8)
            {
                // res图标中心点
                float res_cen_x = (resPts[0] + resPts[4]) / 2f;
                float res_cen_y = (resPts[1] + resPts[5]) / 2f;

                src = new float[4];
                src[0] = res_cen_x;
                src[1] = res_cen_y;

                if (res == def_delete_res)// 删除按钮
                {
                    if (isFlip)// 镜像变换之后，lt-rt交换位置，lb-rb交换位置
                    {
                        src[2] = itemPts[2];
                        src[3] = itemPts[3];
                    }
                    else
                    {
                        src[2] = itemPts[0];
                        src[3] = itemPts[1];
                    }
                }
                else if (res == def_flip_res)// 镜像按钮
                {
                    src[2] = hr_rt_x;
                    src[3] = hr_rt_y;
                }
                else if (res == def_rotation_res)// 旋转缩放按钮
                {
                    src[2] = hr_rb_x;
                    src[3] = hr_rb_y;

                    getShowPos(hideRect);

                    if (hideRect[6] > canvas_l && hideRect[6] < canvas_r && hideRect[7] > canvas_t && hideRect[7] < canvas_b) // 左下角不超过canvas边界
                    {
                        if (hideRect[4] <= canvas_l || hideRect[5] <= canvas_t || hideRect[4] >= canvas_r || hideRect[5] >= canvas_b) // 右下角超过
                        {
                            src[2] = hr_lb_x;
                            src[3] = hr_lb_y;
                        }
                    }
                }
                inverseCount(src, matrices);
                res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
            }
        }
    }

    protected void updateResBtn(Shape item)
    {
        isDrawResBtn(true, true, true);
        resetResBtn(item, def_delete_res);
        resetResBtn(item, def_flip_res);
        resetResBtn(item, def_rotation_res);
    }

    protected void isDrawResBtn(boolean drawDeleteBtn, boolean drawFlipBtn, boolean drawRotateBtn)
    {
        this.drawDeleteBtn = drawDeleteBtn;
        this.drawFlipBtn = drawFlipBtn;
        this.drawRotateBtn = drawRotateBtn;
    }

    /**
     * 计算点的坐标在某个矩阵变换范围内
     *
     * @param pts 坐标
     * @return 在某个matrix范围内，返回这个Matrix的shape对象集合
     */
    @Override
    protected Shape getShowMatrix(float... pts)
    {
        Shape result = mInit;
        int count = pts.length;
        if (count % 2 != 0)
        {
            return result;
        }

        int i = 0;
        float[] dst = new float[count];
        float[] src = new float[count];
        for (float p : pts)
        {
            src[i] = p;
            i++;
        }

        count = mPendantArr.size();
        // 屏幕坐标转换为逻辑坐标
        getLogicPos(src);

        for (int j = 0; j < count; j++)
        {
            Shape shape = mPendantArr.get(j);
            // 逆矩阵求点的坐标
            Matrix[] matrices = new Matrix[]{global.m_matrix, shape.m_matrix};
            inverseCount(dst, src, matrices);
            if (0 <= dst[0] && dst[0] <= shape.m_bmp.getWidth())
            {
                if (0 <= dst[1] && dst[1] <= shape.m_bmp.getHeight())
                {
                    result = shape;   // 保留层次比较靠上的矩阵
                }
            }
        }

        // 判断flip按钮
        Matrix[] matrices = new Matrix[]{global.m_matrix, def_flip_res.m_matrix};
        inverseCount(dst, src, matrices);
        if (drawFlipBtn)
        {
            if (0 <= dst[0] && dst[0] <= def_flip_res.m_bmp.getWidth())
            {
                if (0 <= dst[1] && dst[1] <= def_flip_res.m_bmp.getHeight())
                {
                    result = def_flip_res;
                }
            }
        }

        // 判断rotation按钮
        matrices[1] = def_rotation_res.m_matrix;
        inverseCount(dst, src, matrices);
        if (drawRotateBtn)
        {
            if (0 <= dst[0] && dst[0] <= def_rotation_res.m_bmp.getWidth())
            {
                if (0 <= dst[1] && dst[1] <= def_rotation_res.m_bmp.getHeight())
                {
                    result = def_rotation_res;
                }
            }
        }

        // 判断delete按钮
        matrices[1] = def_delete_res.m_matrix;
        inverseCount(dst, src, matrices);
        if (drawDeleteBtn)
        {
            if (0 <= dst[0] && dst[0] <= def_delete_res.m_bmp.getWidth())
            {
                if (0 <= dst[1] && dst[1] <= def_delete_res.m_bmp.getHeight())
                {
                    result = def_delete_res;
                }
            }
        }

        return result;
    }

    private void getShowMatrix()
    {
        // 找到手指按下的矩阵
        Shape shape = getShowMatrix(mDownX, mDownY);

        // delete 按钮
        if (shape == def_delete_res)
        {
            mPendantArr.remove(mTarget);
            mTarget = mInit;// 不能置null mTarget，只能给一个初始值
        }
        else if (shape == def_flip_res)
        {
            // 镜像
            m_clickFlipBtn = true;
            Matrix invert = new Matrix();
            Matrix temp = new Matrix();
            temp.set(mTarget.m_matrix);
            mTarget.m_matrix.invert(invert);
            // 图片原始位置
            mTarget.m_matrix.postConcat(invert);
            float[] imgPos = getImgLogicPos(mTarget);
            Matrix[] matrices = new Matrix[]{global.m_matrix};
            inverseCount(imgPos, matrices);
            if (imgPos != null)
            {
                float lt_x = imgPos[0];
                float lt_y = imgPos[1];
                float rt_x = imgPos[2];
                float rt_y = imgPos[3];
                // 绕中心轴做镜像
                mTarget.m_matrix.postScale(-1, 1, (rt_x + lt_x) / 2f, (rt_y + lt_y) / 2f);
                // 还原图片状态
                mTarget.m_matrix.postConcat(temp);
            }
            // 记录镜像次数
            if (mTarget instanceof ShapeEx)
            {
                ((ShapeEx) mTarget).mFlipCount += 1;
            }
        }
        else if (shape == def_rotation_res)
        {
            // 旋转缩放，不能平移
            m_clickRotateBtn = true;
        }
        else
        { // 图片本身
            mTarget = shape;
            for (Shape pendant : mPendantArr)
            {
                if (mTarget == pendant)
                {
                    mPendantArr.remove(pendant);
                    mPendantArr.add(pendant);
                    break;
                }
            }
        }
    }

    /**
     * 按照img最合适的尺寸输出,主要是img小于边框的情况<br/>
     * 重新创建一个指定size的bitmap,可以先清掉显示缓存再创建
     *
     * @param size
     * @return
     */
    public Bitmap getOutPutBmp2(int size)
    {
        // 当前画布宽高、宽高比例
        float canvasW = canvas_r - canvas_l;
        float canvasH = canvas_b - canvas_t;
        float tempScale = canvasW / canvasH;
        // 确定输出画布size
        float outW = size;
        float outH = outW / tempScale;
        if (outH > size)
        {
            outH = size;
            outW = outH * tempScale;
        }

        float tempW;
        float tempH;

        // 对比画布和输出size宽高比，缩放当前画布内容
        float tempScaleX = outW / canvasW;
        float tempScaleY = outH / canvasH;
        global.m_matrix.postScale(tempScaleX, tempScaleY);

        Bitmap imgBmp = null;
        if (img != null)
        {
            // 确定当前画布内大图片的宽高
            float[] imgPos = getImgLogicPos(img);
            tempW = imgPos[2] - imgPos[0];
            tempH = imgPos[7] - imgPos[1];
            imgBmp = m_cb.MakeOutputImg(img.m_ex, (int) (tempW + 0.5), (int) (tempH + 0.5));
            if (imgBmp != null && imgBmp.getWidth() > 0 && imgBmp.getHeight() > 0)
            {
                if (Math.max(tempW, tempH) > Math.max(imgBmp.getWidth(), imgBmp.getHeight()))
                {
                    //修正(图片小于输出size)
                    float scaleX = (float) imgBmp.getWidth() / tempW;
                    float scaleY = (float) imgBmp.getHeight() / tempH;
                    float scale = Math.min(scaleX, scaleY);
                    global.m_matrix.postScale(scale, scale);
                    outW = canvasW * tempScaleX * scale;
                    outH = outW / tempScale;
                }
            }
            else
            {
                imgBmp = null;
            }
        }

        Bitmap outBmp = Bitmap.createBitmap((int) outW, (int) outH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        Bitmap tempBmp;
        canvas.drawColor(bkColor);

        if (img != null && imgBmp != null)
        {
            tempBmp = imgBmp;
            if (tempBmp != null)
            {
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setFilterBitmap(true);
                canvas.save();
                canvas.concat(global.m_matrix);
                canvas.drawBitmap(tempBmp, img.m_matrix, mPaint);
                canvas.restore();
                img.m_bmp.recycle();
                img.m_bmp = null;
                tempBmp.recycle();
                tempBmp = null;
            }
        }

        int len = mPendantArr.size();
        Shape pendant;
        for (int i = 0; i < len; i++)
        {
            pendant = mPendantArr.get(i);
            float[] oldPos = getImgLogicPos(pendant);
            float pendantW = oldPos[2] - oldPos[0];
            float pendantH = oldPos[7] - oldPos[1];
            tempBmp = m_cb.MakeOutputPendant(pendant.m_ex, (int) (pendantW + 0.5), (int) (pendantH + 0.5));
            if (tempBmp != null)
            {
                pendant.m_bmp.recycle();
                pendant.m_bmp = tempBmp;
                // 让新图用旧图的matrix变换一次
                float[] newPos = getImgLogicPos(pendant);
                // 用global的逆矩阵计算点坐标
                Matrix[] matrices = new Matrix[]{global.m_matrix};
                inverseCount(newPos, matrices);
                inverseCount(oldPos, matrices);
                // 计算新图和旧图之间的缩放比例 ---> 对角线长度比
                float s1 = ImageUtils.Spacing(newPos[4] - newPos[0], newPos[5] - newPos[1]) / ImageUtils.Spacing(oldPos[4] - oldPos[0], oldPos[5] - oldPos[1]);
                // 将新图的缩放比例还原与旧图一致
                pendant.m_matrix.postScale(s1, s1);
                // 再计算此时新图的坐标位置
                newPos = getImgLogicPos(pendant);
                inverseCount(newPos, matrices);
                // 移动新图到旧图原来的位置
                float[] src = new float[]{oldPos[0], oldPos[1], newPos[0], newPos[1]};
                inverseCount(src, matrices);
                pendant.m_matrix.postTranslate(src[0] - src[2], src[1] - src[3]);

                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setFilterBitmap(true);
                canvas.save();
                canvas.concat(global.m_matrix);
                canvas.drawBitmap(pendant.m_bmp, pendant.m_matrix, mPaint);
                canvas.restore();
                tempBmp.recycle();
                tempBmp = null;
            }
        }
        return outBmp;
    }

    public int GetPendantMaxNum()
    {
        long mem = Runtime.getRuntime().maxMemory() / 1048576;
        int max;
        if (mem >= 96)
        {
            max = 32;
        }
        else if (mem >= 64)
        {
            max = 24;
        }
        else if (mem >= 32)
        {
            max = 12;
        }
        else if (mem >= 24)
        {
            max = 8;
        }
        else
        {
            max = 6;
        }
        return max;
    }

    public int getCurPendantNum()
    {
        return mPendantArr.size();
    }

    public List<Shape> getPendantArray()
    {
        return mPendantArr;
    }

    /**
     * 缩放
     */
    @Override
    protected void Run_Z(Shape target, float x1, float y1, float x2, float y2)
    {
        float scale = 1f;
        Matrix record = new Matrix();
        record.set(target.m_matrix);
        // 计算down时图片的缩放比例
        float sw1 = getScaleByW(target);
        float sh1 = getScaleByH(target);

        float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
        if (tempDist > 10)
        {
            scale = tempDist / mDelta;
        }

        Run_Z(target, scale, scale);

        // 计算move后图片的缩放比例
        float sw2 = getScaleByW(target);
        float sh2 = getScaleByH(target);
        float newScaleX = 1f, newScaleY = 1f;

        if (target instanceof ShapeEx)
        {
            float def_img_min_scale = ((ShapeEx) target).MIN_SCALE;
            float def_img_max_scale = ((ShapeEx) target).MAX_SCALE;
            if (sw2 != -1 && sw1 != -1)
            {
                // 限制图片缩放比例
                if (sw2 <= def_img_min_scale)
                {
                    sw2 = def_img_min_scale;
                }

                if (sw2 >= def_img_max_scale)
                {
                    sw2 = def_img_max_scale;
                }
                newScaleX = sw2 / sw1;
            }

            if (sh2 != -1 && sh1 != -1)
            {
                // 限制图片缩放比例
                if (sh2 <= def_img_min_scale)
                {
                    sh2 = def_img_min_scale;
                }

                if (sh2 >= def_img_max_scale)
                {
                    sh2 = def_img_max_scale;
                }
                newScaleY = sh2 / sh1;
            }

            if (sw2 == def_img_min_scale || sw2 == def_img_max_scale)
            {
                newScaleY = newScaleX;
            }

            if (sh2 == def_img_min_scale || sh2 == def_img_max_scale)
            {
                newScaleX = newScaleY;
            }

            // 恢复缩放前的状态
            target.m_matrix.set(record);

            Run_Z(target, newScaleX, newScaleY);
        }
    }

    private void Run_Z(Shape target, float scaleX, float scaleY)
    {
        if (target == global) // 整体缩放
        {
            target.m_matrix.postScale(scaleX, scaleY, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
        }
        else
        {
            float[] src = new float[]{(mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f};
            float[] dst = new float[src.length];
            Matrix[] matrices = new Matrix[]{global.m_matrix};
            inverseCount(dst, src, matrices);
            target.m_matrix.postScale(scaleX, scaleY, dst[0], dst[1]);
        }
    }

    public static class ShapeEx extends Shape
    {
        public float MAX_SCALE = 2f;
        public float MIN_SCALE = 0.5f;

        // 记录使用镜像次数
        public int mFlipCount = 0;

        public void SetScaleXY(float scaleX, float scaleY)
        {
            float m_scaleX, m_scaleY;
            if (scaleX > MAX_SCALE)
            {
                m_scaleX = MAX_SCALE;
            }
            else if (scaleX < MIN_SCALE)
            {
                m_scaleX = MIN_SCALE;
            }
            else
            {
                m_scaleX = scaleX;
            }

            if (scaleY > MAX_SCALE)
            {
                m_scaleY = MAX_SCALE;
            }
            else if (scaleY < MIN_SCALE)
            {
                m_scaleY = MIN_SCALE;
            }
            else
            {
                m_scaleY = scaleY;
            }
            //
            m_matrix.postScale(m_scaleX, m_scaleY);
        }
    }
}