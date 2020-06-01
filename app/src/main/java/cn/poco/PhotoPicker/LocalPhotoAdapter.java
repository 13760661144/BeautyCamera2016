package cn.poco.PhotoPicker;//package com.example.preview.temp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.model.PhotoInfo;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.imagecore.Utils;
import cn.poco.photoview.AbsLocalPhotoPage;
import cn.poco.photoview.AbsLocalThreadAdapter;
import cn.poco.photoview.AbsPhotoPage;
import cn.poco.photoview.BitmapInfo;
import cn.poco.photoview.PhotosViewPager;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.UndoRedoDataMgr;

/**
 * Created by lgd on 2017/9/25.
 */

public class LocalPhotoAdapter extends AbsLocalThreadAdapter<PhotoInfo>
{
   final protected float mMaxMemory =(long) (Runtime.getRuntime().maxMemory() * 0.15f);
   final protected int mMaxSize = (int) (ShareData.m_screenWidth * ShareData.m_screenHeight * 3.5f);  //红米note3 1080p 测试超过 3.7左右以上会卡    红米1s 720p流畅运行
    public LocalPhotoAdapter(PhotosViewPager viewPager, int width, int height)
    {
        super(viewPager, width, height);
    }
    @Override
    protected Bitmap decodeNormalBp(final Context context, final String uri, final int reqWidth, final int reqHeight)
    {
        Bitmap bmp = null;
        Object obj = null;
        synchronized (Beautify4Page.CACHE_THREAD_LOCK)
        {
            UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(uri);
            if (mgr != null)
            {
                obj = mgr.GetCurrentData();
            }
        }
        if (obj == null)
        {
            obj = uri;
        }
        if (obj != null)
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Utils.DecodeFile(uri, opts, true);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
            int inW = opts.outWidth;
            int inH = opts.outHeight;
            int bpp = 4;
            long imgMem = inW / opts.inSampleSize * inH / opts.inSampleSize * bpp;
            while (imgMem > mMaxMemory)
            {
                opts.inSampleSize *= 2;
                imgMem = inW / opts.inSampleSize * inH / opts.inSampleSize * bpp;
            }
            try
            {
                if (obj instanceof String)
                {
                    bmp = Utils.DecodeFile((String) obj, opts);
                } else
                {
                    bmp = ImageUtils.MakeBmp(context, obj, reqWidth, reqHeight);
                }
            } catch (OutOfMemoryError e)
            {
                bmp = null;
            }
            if (bmp != null)
            {
                if (opts.outMimeType == null || opts.outMimeType.equals("image/jpeg"))
                {
                    int[] imei = CommonUtils.GetImgInfo(uri);
                    int rotation = imei[0];
                    if (rotation != 0)
                    {
                        Matrix m = new Matrix();
                        m.setRotate(rotation);
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
//                        if (rotation != 0)
//                        {
//                            bmp = MakeBmp.CreateBitmap(bmp, bmp.getWidth(), bmp.getHeight(), -1, rotation, Bitmap.Config.ARGB_8888);
//                        }
                    }
                }
            }
        }
        return bmp;
    }

    @Override
    protected Bitmap decodeClearBp(final Context context,final String uri, final int reqWidth, final int reqHeight)
    {
        Bitmap bmp = null;
        Object obj = null;
        synchronized (Beautify4Page.CACHE_THREAD_LOCK)
        {
            UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(uri);
            if (mgr != null)
            {
                obj = mgr.GetCurrentData();
            }
        }
        if (obj == null)
        {
            obj = uri;
        }
        if (obj != null)
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Utils.DecodeFile(uri, opts, true);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            int size = calculateInSampleSize(opts, reqWidth, reqHeight);
            if (size <= 1)
            {
                bmp = null;
            } else
            {
                int temp = 1;
                int inW = opts.outWidth;
                int inH = opts.outHeight;
                while (temp < size)
                {
                    int bpp = 4;
                    long area = inW / opts.inSampleSize * inH / opts.inSampleSize;
                    if (area * bpp > mMaxMemory || area > mMaxSize)
                    {
                        opts.inSampleSize = temp;
                        temp *= 2;
                    } else
                    {
                        break;
                    }
                }
                if (temp >= size)
                {
                    bmp = null;
                } else
                {
                    try
                    {
                        if (obj instanceof String)
                        {
                            bmp = Utils.DecodeFile((String) obj, opts);
                        } else
                        {
                            bmp = ImageUtils.MakeBmp(context, obj, reqWidth, reqHeight);
                        }
                    } catch (OutOfMemoryError e)
                    {
                        bmp = null;
                    }
                }
            }
            if (bmp != null)
            {
                int rotation = 0;
                if (opts.outMimeType != null && opts.outMimeType.equals("image/jpeg"))
                {
                    int[] imei = CommonUtils.GetImgInfo((String) obj);
                    rotation = imei[0];
                }
                Matrix m = new Matrix();
                m.setRotate(rotation);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
            }
        }
        return bmp;
    }

    @Override
    protected AbsLocalPhotoPage initLocalPhotoPage(Context context)
    {
        return new LocalPhotoView(context);
    }

    @Override
    protected String getImgUri(PhotoInfo data)
    {
        return data.getImagePath();
    }

    /**
     * @param list
     * @param isReuseCache 缓存当前显示的页面，避免再加载
     * @param imgIndex
     */
    public void upDataInfo(List<PhotoInfo> list, boolean isReuseCache, int imgIndex)
    {
        if (isReuseCache)
        {
            int start = imgIndex - mViewPager.getOffscreenPageLimit();
            if (start < 0)
            {
                start = 0;
            }
            int end = imgIndex + mViewPager.getOffscreenPageLimit();
            if (end >= mItemInfos.size())
            {
                end = mItemInfos.size() - 1;
            }
            HashMap<Integer, BitmapInfo> tempInfoCache = mCacheInfo;
            HashMap<Integer,AbsPhotoPage> tempViewCache = mCacheView;
            mCacheView = new HashMap<>();
            mCacheInfo = new HashMap<>();
            mCacheDecodeUri.clear();
            //复用cache
            for (int i = start; i <= end; i++)
            {
                Iterator iterator = tempInfoCache.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    int key = (int) entry.getKey();
                    BitmapInfo info = (BitmapInfo) entry.getValue();
                    if(info.imgUri.equals(list.get(i).getImagePath())){
                        mCacheView.put(i, tempViewCache.get(key));
                        mCacheInfo.put(i, tempInfoCache.get(key));
                    }
                }
            }
            //没用被复用的cache清除
            Iterator iterator = mCacheView.keySet().iterator();
            while (iterator.hasNext())
            {
                int key = (int) iterator.next();
               tempViewCache.remove(key);
            }
            iterator = tempViewCache.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                AbsPhotoPage photoPage = (AbsPhotoPage) entry.getValue();
                if (photoPage != null)
                {
                    photoPage.onClose();
                }
            }
            tempViewCache.clear();
            tempInfoCache.clear();

            mBlockClearCache = true;
            mItemInfos.clear();
            mItemInfos.addAll(list);
            notifyDataSetChanged();
            mBlockClearCache = false;
        } else
        {
            setData(list);
        }
        mViewPager.setCurrentItem(imgIndex, false);
        mCurPageIndex = imgIndex;
    }

    public void updateImages(List<PhotoInfo> items, int position)
    {
        int diff = position - mCurPageIndex;
        int start = mViewPager.getCurrentItem() - mViewPager.getOffscreenPageLimit();
        if (start < 0)
        {
            start = 0;
        }
        int end = mViewPager.getCurrentItem() + mViewPager.getOffscreenPageLimit();
        if (end >= mItemInfos.size())
        {
            end = mItemInfos.size() - 1;
        }
        if (diff > 0)
        {
            //后移
            for (int i = end; i >= start; i--)
            {
                moveCache(i + diff, i);
            }

        } else if (diff < 0)
        {
            //前移
            for (int i = start; i <= end; i++)
            {
                moveCache(i + diff, i);
            }
        }
        //清除无效的cacheInfo
        for (int i = end+diff; i >= start+diff; i--)
        {
            if(mCacheInfo.containsKey(i) && i < items.size() && !mCacheInfo.get(i).imgUri.equals(items.get(i).getImagePath())){
                removeCache(i);
            }
        }
        mBlockClearCache = true;
        mItemInfos.clear();
        mItemInfos.addAll(items);
        notifyDataSetChanged();
        mBlockClearCache = false;
        mViewPager.setCurrentItem(position, false);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth)
//        {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth)
//            {
//                inSampleSize *= 2;
//            }
//            //另外加的限制条件
//            long area = height / inSampleSize * width / inSampleSize;
//            if(area > mMaxSize){
//                inSampleSize *= 2;
//            }
//
//        }
//        return inSampleSize;
        final int inW = options.outWidth;
        final int inH = options.outHeight;
        int minW = reqWidth;
        int minH = reqHeight;
        int inSampleSize = 1;
        float scale_w_h = 1;
        if(minW < 1)
        {
            minW = inW << 1;
        }
        if(minH < 1)
        {
            minH = inH << 1;
        }
        scale_w_h = (float)inW / (float)inH;
        float w = minH * scale_w_h;
        float h = minH;
        if(w > minW)
        {
            w = minW;
            h = minW / scale_w_h;
        }
        inSampleSize = (int)(inW / w < inH / h ? inW / w : inH / h);
        if(inSampleSize < 1)
        {
            inSampleSize = 1;
        }
        //另外加的限制条件
        long area = inW / inSampleSize * inH / inSampleSize;
        if(area > mMaxSize){
            inSampleSize *= 2;
        }
        return inSampleSize;
    }


//    @Override
//    protected Bitmap decodeNormalBp(String uri, int reqWidth, int reqHeight)
//    {
//        Bitmap bmp = null;
//        Object obj = null;
//        synchronized (Beautify4Page.CACHE_THREAD_LOCK)
//        {
//            UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(uri);
//            if (mgr != null)
//            {
//                obj = mgr.GetCurrentData();
//            }
//        }
//        if (obj == null)
//        {
//            obj = uri;
//        }
//        if (obj != null)
//        {
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            Utils.DecodeFile(uri, opts, true);
//            opts.inJustDecodeBounds = false;
//            opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
//            int inW =opts.outWidth;
//            int inH =opts.outHeight;
//            int bpp = 4;
//            long imgMem = inW / opts.inSampleSize * inH / opts.inSampleSize * bpp;
//            while(imgMem > mMaxMemory)
//            {
//                opts.inSampleSize *= 2;
//                imgMem = inW / opts.inSampleSize * inH / opts.inSampleSize * bpp;
//            }
//            try
//            {
//                if (obj instanceof String)
//                {
//                    bmp = Utils.DecodeFile((String) obj, opts);
//                } else
//                {
//                    bmp = ImageUtils.MakeBmp(context, obj, reqWidth, reqHeight);
//                }
//            } catch (OutOfMemoryError e)
//            {
//                bmp = null;
//            }
//            if (bmp != null)
//            {
//                if (opts.outMimeType == null || opts.outMimeType.equals("image/jpeg"))
//                {
//                    int[] imei = CommonUtils.GetImgInfo(uri);
//                    int rotation = imei[0];
//                    if (rotation != 0)
//                    {
//                        Matrix m = new Matrix();
//                        m.setRotate(rotation);
//                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
////                        if (rotation != 0)
////                        {
////                            bmp = MakeBmp.CreateBitmap(bmp, bmp.getWidth(), bmp.getHeight(), -1, rotation, Bitmap.Config.ARGB_8888);
////                        }
//                    }
//                }
//            }
//        }
//        return bmp;
//    }
//
//    @Override
//    protected Bitmap decodeClearBp(String uri, int reqWidth, int reqHeight)
//    {
//        Bitmap bmp = null;
//        Object obj = null;
//        synchronized (Beautify4Page.CACHE_THREAD_LOCK)
//        {
//            UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(uri);
//            if (mgr != null)
//            {
//                obj = mgr.GetCurrentData();
//            }
//        }
//        if (obj == null)
//        {
//            obj = uri;
//        }
//        if (obj != null)
//        {
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            Utils.DecodeFile(uri, opts, true);
//            opts.inJustDecodeBounds = false;
//            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            int size = calculateInSampleSize(opts, reqWidth, reqHeight);
//            if (size <= 1)
//            {
//                return null;
//            } else
//            {
//                int temp = 1;
//                int inW =opts.outWidth;
//                int inH =opts.outHeight;
//                while (temp < size)
//                {
//                    int bpp = 4;
//                    long area = inW / opts.inSampleSize * inH / opts.inSampleSize;
//                    if (area * bpp > mMaxMemory || area > mMaxSize )
//                    {
//                        opts.inSampleSize = temp;
//                        temp *= 2;
//                    }else{
//                        break;
//                    }
//                }
//                if(temp >= size){
//                    return null;
//                }
//            }
//            try
//            {
//                if (obj instanceof String)
//                {
//                    bmp = Utils.DecodeFile((String) obj, opts);
//                } else
//                {
//                    bmp = ImageUtils.MakeBmp(context, obj, reqWidth, reqHeight);
//                }
//            } catch (OutOfMemoryError e)
//            {
//                bmp = null;
//            }
//            if (bmp != null)
//            {
//                int rotation = 0;
//                if (opts.outMimeType != null && opts.outMimeType.equals("image/jpeg"))
//                {
//                    int[] imei = CommonUtils.GetImgInfo((String) obj);
//                    rotation = imei[0];
//                }
//                Matrix m = new Matrix();
//                m.setRotate(rotation);
//                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
//            }
//        }
//        return bmp;
//    }

}
