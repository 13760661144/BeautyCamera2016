package my.beautyCamera.site.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;

import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.system.FolderMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.utils.Utils;
import my.beautyCamera.PocoCamera;

/**
 * Created by Raining on 2017/11/1.
 */
public class MainActivitySite extends BaseActivitySite
{
	@Override
	public Class<? extends Activity> getActivityClass()
	{
		return PocoCamera.class;
	}

	public void OnSave(Context context, HashMap<String, Object> inParams, HashMap<String, Object> params)
	{
		OnOutsideSave(context, inParams, params);
	}

	public static void OnOutsideSave(Context context, HashMap<String, Object> inParams, HashMap<String, Object> params)
	{
		int resultCode = Activity.RESULT_CANCELED;
		Intent data = new Intent();
		if(params != null)
		{
			Object img = params.get("img");
			if(img instanceof RotationImg2[])
			{
				img = ((RotationImg2[])img)[0];
			}

			String tempPath = null;
			if(img instanceof String)
			{
				tempPath = (String)img;
			}
			else if(img instanceof RotationImg2)
			{
				tempPath = (String)((RotationImg2)img).m_img;
			}
			else if(img instanceof Bitmap)
			{
				tempPath = Utils.SaveImg(context, (Bitmap)img, FileCacheMgr.GetLinePath(), 100, true);
			}

			if(tempPath != null)
			{
				int type = (Integer)inParams.get(MyFramework.EXTERNAL_CALL_TYPE);
				switch(type)
				{
					case MyFramework.EXTERNAL_CALL_TYPE_EDIT:
					{
						String path;
						if(FolderMgr.getInstance().IsCachePath(tempPath))
						{
							try
							{
								path = Utils.MakeSavePhotoPath(context, Beautify4Page.GetImgScaleWH(tempPath));
								FileUtils.copyFile(new File(tempPath), new File(path));
								Utils.FileScan(context, path);
							}
							catch(Throwable e)
							{
								e.printStackTrace();

								path = null;
							}
						}
						else
						{
							path = tempPath;
						}
						if(path != null)
						{
							data.setAction(Intent.ACTION_EDIT);
							data.setDataAndType(Uri.fromFile(new File(path)), "image/jpeg");
							resultCode = Activity.RESULT_OK;

							Toast.makeText(context.getApplicationContext(), "图片已保存到：" + path, Toast.LENGTH_LONG).show();
						}
						break;
					}

					case MyFramework.EXTERNAL_CALL_TYPE_CAMERA:
					{
						data.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
						Object obj = inParams.get(MyFramework.EXTERNAL_CALL_IMG_SAVE_URI);
						if(obj instanceof Uri)
						{
							byte[] jpg = CommonUtils.ReadFile(tempPath);
							if(jpg != null)
							{
								OutputStream os = null;
								try
								{
									os = context.getContentResolver().openOutputStream((Uri)obj);
									os.write(jpg);
									os.flush();
									resultCode = Activity.RESULT_OK;
								}
								catch(Throwable e)
								{
									e.printStackTrace();
								}
								finally
								{
									try
									{
										if(os != null)
										{
											os.close();
											os = null;
										}
									}
									catch(Throwable e)
									{
										e.printStackTrace();
									}
								}
								data.setData((Uri)obj);
							}
						}
						else
						{
							Bitmap outBmp = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(context, tempPath, 0, -1, 250, 250), 250, 250, -1, 0, Bitmap.Config.ARGB_8888);
							Bundle bundle = new Bundle();
							bundle.putParcelable("data", outBmp);
							data.putExtras(bundle);
							resultCode = Activity.RESULT_OK;
						}
						break;
					}

					default:
						break;
				}
			}
		}
		MyFramework.SITE_Finish(context, resultCode, data);
	}
}
