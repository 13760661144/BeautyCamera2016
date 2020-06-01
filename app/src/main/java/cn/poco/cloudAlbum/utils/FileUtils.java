package cn.poco.cloudAlbum.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.poco.cloudAlbum.model.TransportInfo;
import cn.poco.tianutils.CommonUtils;

import static java.io.File.separator;

/**
 * Created by: fwc
 * Date: 2016/10/8
 */
public class FileUtils {

	private static String PHOTO_DIR;

	public static void init() {
		if (TextUtils.isEmpty(PHOTO_DIR)) {
			PHOTO_DIR = getPhotoSavePath();
		}
	}

	/**
	 * 获取图片保存位置
	 */
	public static String getPhotoSavePath() {
		String out;

		File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		if (dcim != null) {
			out = dcim.getAbsolutePath() + separator + "Camera";
		} else {
			out = Environment.getExternalStorageDirectory().getAbsolutePath() + separator + "DCIM" + separator + "Camera";
		}
		//魅族的默认相册路径不同，原来的路径图库不显示
		if (isMeizuManufacturer())
		{
			out = Environment.getExternalStorageDirectory().getAbsolutePath() + separator + "Camera";
		}

		CommonUtils.MakeFolder(out);

		return out;
	}

	/**
	 * 是否魅族设备
	 * @return
	 */
	public static boolean isMeizuManufacturer()
	{
		boolean out = false;
		String manufacturer = android.os.Build.MANUFACTURER;
		if (manufacturer != null) {
			manufacturer = manufacturer.toLowerCase(Locale.getDefault());
			if (manufacturer.contains("meizu")) {
				out = true;
			}
		}
		return out;
	}

	/**
	 * 生成文件
	 *
	 * @param filename 文件名
	 * @param size     图片大小
	 * @param copy     是否强制复制
	 * @return 如果为null则已存在，否则返回新的文件
	 */
	public static File createNewFile(String filename, long size, boolean copy) {
		// 确保目录存在（如果在应用相机运行期间删除Camera相册导致目录不存在）
		init();
		CommonUtils.MakeFolder(PHOTO_DIR);
		File file = new File(PHOTO_DIR, filename);
		String newName;
		int index = 1;

		int pos = filename.lastIndexOf('.');
		String name, extension;
		if (pos > -1 && pos < (filename.length() - 1)) {
			name = filename.substring(0, pos);
			extension = filename.substring(pos + 1);
		} else {
			name = filename;
			extension = "jpg";
		}

		while (file.exists()) {
			if (file.length() == size && !copy) {
				return null;
			}

			newName = name + "(" + index++ + ")." + extension;
			file = new File(PHOTO_DIR, newName);
		}

		return file;
	}

	/**
	 * 使用文件通道的方式复制文件
	 *
	 * @param s 源文件
	 * @param t 复制到的新文件
	 */
	public static void fileChannelCopy(File s, File t) {

		FileChannel in = null;
		FileChannel out = null;

		try {
			in = new FileInputStream(s).getChannel();//得到对应的文件通道
			out = new FileOutputStream(t).getChannel();//得到对应的文件通道
			in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 记录传输的信息
	 *
	 * @param context   上下文
	 * @param filename  文件名
	 * @param separator 分隔符
	 * @param info      传输信息对象
	 */
	public static void writeTransportInfo(Context context, String filename, String separator, TransportInfo info) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND)));

			if (TransportInfo.UPLOAD.equals(info.type)) {

				// 格式：类型+separator+acid+separator+图片路径+separator+文件夹id+separator+文件夹名字
				writer.write(info.type);
				writer.write(separator);
				writer.write(String.valueOf(info.acid));
				writer.write(separator);
				writer.write(info.path);
				writer.write(separator);
				writer.write(info.folderId);
				writer.write(separator);
				writer.write(info.folderName);
				writer.write('\n');

			} else if (TransportInfo.DOWNLOAD.equals(info.type)) {

				// 格式：类型+separator+acid+separator+图片url+separator+图片大小+separator+文件夹名字
				writer.write(info.type);
				writer.write(separator);
				writer.write(String.valueOf(info.acid));
				writer.write(separator);
				writer.write(info.path);
				writer.write(separator);
				writer.write(String.valueOf(info.size));
				writer.write(separator);
				writer.write(info.folderName);
				writer.write('\n');
			}

			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 记录传输的信息
	 *
	 * @param context   上下文
	 * @param filename  文件名
	 * @param separator 分隔符
	 * @param infos     传输信息列表
	 */
	public static void writeTransportInfos(Context context, String filename, String separator, List<TransportInfo> infos) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND)));

			for (TransportInfo info : infos) {
				if (TransportInfo.UPLOAD.equals(info.type)) {

					// 格式：类型+separator+acid+separator+图片路径+separator+文件夹id+separator+文件夹名字
					writer.write(info.type);
					writer.write(separator);
					writer.write(String.valueOf(info.acid));
					writer.write(separator);
					writer.write(info.path);
					writer.write(separator);
					writer.write(info.folderId);
					writer.write(separator);
					writer.write(info.folderName);
					writer.write('\n');

				} else if (TransportInfo.DOWNLOAD.equals(info.type)) {

					// 格式：类型+separator+acid+separator+图片url+separator+图片大小+separator+文件夹名字
					writer.write(info.type);
					writer.write(separator);
					writer.write(String.valueOf(info.acid));
					writer.write(separator);
					writer.write(info.path);
					writer.write(separator);
					writer.write(info.size);
					writer.write(separator);
					writer.write(info.folderName);
					writer.write('\n');
				}
			}

			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取传输信息
	 *
	 * @param context   上下文
	 * @param filename  文件名
	 * @param separator 分隔符
	 * @return 传输信息列表
	 */
	public static ArrayList<TransportInfo> readTransportInfos(Context context, String filename, String separator) {

		ArrayList<TransportInfo> transportInfos = new ArrayList<>();
		BufferedReader reader = null;

		String line;
		String[] splits;

		TransportInfo info;
		try {
			reader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
			while ((line = reader.readLine()) != null) {
				splits = line.split(separator);
				info = new TransportInfo();
				if (splits[0].equals(TransportInfo.UPLOAD)) {

					info.type = TransportInfo.UPLOAD;
					info.acid = Integer.valueOf(splits[1]);
					info.path = splits[2];
					info.folderId = splits[3];
					info.folderName = splits[4];
					transportInfos.add(info);

				} else if (splits[0].equals(TransportInfo.DOWNLOAD)) {

					info.type = TransportInfo.DOWNLOAD;
					info.acid = Integer.valueOf(splits[1]);
					info.path = splits[2];
					info.size = splits[3];
					info.folderName = splits[4];
					transportInfos.add(info);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		clearTransportInfos(context, filename);

		return transportInfos;
	}

	/**
	 * 清除某个传输对象
	 * @param context 上下文
	 * @param filename 文件名
	 * @param separator 分隔符
	 * @param acid ACID
	 */
	public static void clearTransportInfo(Context context, String filename, String separator, int acid) {
		List<TransportInfo> infos = readTransportInfos(context, filename, separator);
		if (infos == null || infos.isEmpty()) {
			return;
		}
		TransportInfo info;
		for (int i = 0; i < infos.size(); i++) {
			info = infos.get(i);
			if (info.acid == acid) {
				infos.remove(i);
				break;
			}
		}

		writeTransportInfos(context, filename, separator, infos);
	}

	/**
	 * 清除所有传输信息
	 *
	 * @param context  上下文
	 * @param filename 文件名
	 */
	public static void clearTransportInfos(Context context, String filename) {
		OutputStream os = null;
		try {
			os = context.openFileOutput(filename, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
