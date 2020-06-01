package cn.poco.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;

import cn.poco.framework.FileCacheMgr;
import cn.poco.imagecore.ImageUtils;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.utils.Utils;

/**
 * 拍照返回的图片数据</br>
 * 操作顺序:旋转->翻转->裁剪
 *
 * @author POCO
 */
public class ImageFile2 {
    protected byte[] m_data;

    /**
     * 旋转</br>
     * 0,90,180,270...
     */
    protected int m_degree;

    /**
     * 翻转</br>
     * MakeBmpV2.FLIP_NONE</br>
     * MakeBmpV2.FLIP_H</br>
     * MakeBmpV2.FLIP_V</br>
     */
    protected int m_flip = MakeBmpV2.FLIP_NONE;

    /**
     * 横宽比例,-1为图片默认比例
     */
    protected float m_scale = -1;

    protected String m_cachePath;

    //最终保存的数据
    protected boolean m_autoSave;
    protected boolean m_fastSave; //true:可以直接保存图片数据不需要生成bitmap
    protected String m_finalOrgPath;
    protected String m_finalCachePath;
    protected int m_finalDegree;
    protected int m_finalFlip = MakeBmpV2.FLIP_NONE;

    /**
     * 设置图片数据(不能调用多次,否则有问题)</br>
     * 操作顺序:旋转->翻转->裁剪
     *
     * @param context
     * @param data
     * @param degree  角度,0,90,180,270...
     * @param flip    翻转</br>
     *                MakeBmpV2.FLIP_NONE</br>
     *                MakeBmpV2.FLIP_H</br>
     *                MakeBmpV2.FLIP_V</br>
     * @param scale   横宽比例,-1为图片默认比例
     */
    public synchronized void SetData(Context context, byte[] data, int degree, int flip, float scale) {
        m_data = data;
        m_degree = degree;
        m_degree = m_degree / 90 * 90;
        m_flip = flip;
        m_scale = scale;

        InitFinalData(context);
    }

    /**
     * 初始化最终保存数据
     */
    protected void InitFinalData(Context context) {
        m_autoSave = SettingInfoMgr.GetSettingInfo(context).GetAutoSaveCameraPhotoState();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(m_data, 0, m_data.length, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        if (m_degree % 180 != 0) {
            w += h;
            h = w - h;
            w = w - h;
        }

        m_fastSave = false;
        float scale = 1f;
        if (m_scale > 0) {
            if (m_flip == MakeBmpV2.FLIP_NONE && w > 0 && h > 0 && (float) w / (float) h == m_scale) {
                m_fastSave = true;
            }
            scale = m_scale;
        } else {
            if (m_flip == MakeBmpV2.FLIP_NONE) {
                m_fastSave = true;
            }
            if (w > 0 && h > 0) {
                scale = (float) w / (float) h;
            }
        }
        InitSavePath(context, scale); //自动创建缓存路径
        if (m_fastSave) {
            m_finalDegree = m_degree;
            m_finalFlip = m_flip;
        } else {
            m_finalDegree = 0;
            m_finalFlip = MakeBmpV2.FLIP_NONE;
        }
    }

    public void SetAutoSave(boolean autoSave)
    {
        this.m_autoSave = autoSave;
    }

    /**
     * 构造最终效果的bitmap</br>
     * 这里有2种情况:</br>
     * 1.图片已经保存为链接,m_data已经被清除,那么就直接读取路径的图片(需要读取最终的数据)</br>
     * 2.图片没保存,读取m_data数据,需要做原始的旋转翻转操作</br>
     *
     * @param context
     * @param minW
     * @param minH
     * @return
     */
    public synchronized Bitmap MakeBmp(Context context, int minW, int minH) {
        Bitmap out = null;

        if (m_data != null) {
            Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, m_data, m_degree, m_scale, minW, minH);
            if (temp != null) {
                out = MakeBmpV2.CreateBitmapV2(temp, m_degree, m_flip, m_scale, minW, minH, Config.ARGB_8888);
                temp.recycle();
                temp = null;
            }
        } else if (m_cachePath != null) {
            Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, m_cachePath, m_finalDegree, -1, minW, minH);
            if (temp != null) {
                out = MakeBmpV2.CreateBitmapV2(temp, m_finalDegree, m_finalFlip, -1, minW, minH, Config.ARGB_8888);
                temp.recycle();
                temp = null;
            }
        }

        return out;
    }

    public Bitmap MakeBmp(Context context) {
        return MakeBmp(context, -1, -1);
    }

    /**
     * 保存为路径后会清理数据,如果已经保存过就直接使用之前保存的路径</br>
     * 这个函数配合MakeBmp(...)一起使用</br>
     *
     * @param context
     * @param bmp
     */
    public synchronized void SaveImg(Context context, Bitmap bmp) {
        if (m_cachePath == null) {
            try {
                Bitmap temp = bmp;
                if (bmp.getConfig() != Config.ARGB_8888) {
                    temp = bmp.copy(Config.ARGB_8888, true);
                }
                boolean saveJpg = false;
                if (m_autoSave) {
                    if (ImageUtils.WriteJpg(temp, 100, m_finalOrgPath) == 0) {
                        //只有自动保存的才更新图库
                        saveJpg = true;
                        Utils.FileScan(context, m_finalOrgPath);
                    }
                }
                //ImageUtils.WriteFastBmp(temp, 100, m_finalCachePath);
                //2016/12/15 图片太大直接保持bmp也是很慢所以保存为jpg
                //2017/8/18 写jpg也很慢，所以改为copy
                if (saveJpg) {
                    FileUtils.copyFile(new File(m_finalOrgPath), new File(m_finalCachePath));
                } else {
                    ImageUtils.WriteJpg(temp, 100, m_finalCachePath);
                }
                if (temp != bmp) {
                    temp.recycle();
                    temp = null;
                }

                m_cachePath = m_finalCachePath;
                m_data = null;
                m_degree = 0;
                m_flip = MakeBmpV2.FLIP_NONE;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这个函数是MakeBmp(...)和SaveImg(...)的组合
     *
     * @param context
     */
    protected synchronized void SaveImg(Context context) {
        SaveImg(context, m_autoSave);
    }

    /**
     * 这个函数是MakeBmp(...)和SaveImg(...)的组合
     *
     * @param context
     * @param autoSave 是否保存到相册
     */
    protected synchronized void SaveImg(Context context, boolean autoSave) {
        if (m_cachePath == null) {
            if (m_fastSave) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(m_finalCachePath);
                    fos.write(m_data);
                    fos.close();
                    fos = null;

                    ExifInterface exif = new ExifInterface(m_finalCachePath);
                    int tag = CommonUtils.MakeExifTag(m_degree, m_flip);
                    exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(tag));
                    exif.saveAttributes();

                    m_cachePath = m_finalCachePath;

                    //只有自动保存的才更新图库
                    if (autoSave) {
                        FileUtils.copyFile(new File(m_finalCachePath), new File(m_finalOrgPath));
                        Utils.FileScan(context, m_finalOrgPath);
                    }

                    m_data = null;
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                            fos = null;
                        }
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                    }
                }
            } else {
                //如果不能直接保存就解图片保存
                Bitmap bmp = MakeBmp(context);
                if (bmp != null) {
                    SaveImg(context, bmp);
                    bmp.recycle();
                    bmp = null;
                }
            }
        }
    }

    /**
     * 获取最终保存链接和缓存链接
     *
     * @param context
     * @param scale
     */
    protected void InitSavePath(Context context, float scale) {
        if (m_finalCachePath == null) {
            if (m_autoSave) {
                m_finalOrgPath = Utils.MakeSavePhotoPath(context, scale);
                CommonUtils.MakeParentFolder(m_finalOrgPath);
            }
            m_finalCachePath = FileCacheMgr.GetLinePath();
            CommonUtils.MakeParentFolder(m_finalCachePath);
        }
    }

    public RotationImg2[] SaveImg2(Context context) {
        RotationImg2[] out = null;

        SaveImg(context);
        if (m_cachePath != null) {
            out = GetPreSaveImg();
        }

        return out;
    }

    public RotationImg2[] SaveTemp(Context context) {
        RotationImg2[] out = null;

        SaveImg(context, false);
        if (m_cachePath != null) {
            out = GetPreSaveImg();
        }

        return out;
    }

    public synchronized RotationImg2[] GetPreSaveImg() {
        RotationImg2[] out = null;

        out = new RotationImg2[1];
        out[0] = new RotationImg2();
        out[0].m_orgPath = m_finalOrgPath; //有可能为null,当没有自动保存图片时
        out[0].m_img = m_finalCachePath;
        out[0].m_degree = m_finalDegree;
        out[0].m_flip = m_finalFlip;

        return out;
    }

    /**
     * @return 路径/图片数据
     */
    public synchronized RotationImg2[] GetRawImg() {
        RotationImg2[] out = null;

        out = new RotationImg2[1];
        out[0] = new RotationImg2();
        if (m_cachePath == null) {
            out[0].m_img = m_data;
            out[0].m_degree = m_degree;
            out[0].m_flip = m_flip;
        } else {
            out[0].m_orgPath = m_finalOrgPath;
            out[0].m_img = m_finalCachePath;
            out[0].m_degree = m_finalDegree;
            out[0].m_flip = m_finalFlip;
        }

        return out;
    }

    public boolean IsSave() {
        return (m_cachePath != null);
    }

    public void ClearAll() {
        m_data = null;
    }
}
