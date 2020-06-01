package cn.poco.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.adnonstop.beautymall.constant.BeautyUser;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.blogcore.Tools;
import cn.poco.exception.MyApplication;
import cn.poco.framework.MyFramework2App;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.CommonUtils;

/**
 * 系统设置信息(对用户开放的)
 *
 * @author POCO
 */
public class SettingInfo
{
	protected final HashMap<String, String> m_data = new HashMap<String, String>();
	protected boolean m_change = false;

	protected static final String CAMERA_SOUND = "CAMERA_SOUND"; //拍照声音
	protected static final String SAVE_PHOTO = "SAVE_PHOTO"; //自动保存图片
	protected static final String ACTUAL_BEAUTY = "ACTUAL_BEAUTY"; //实时美颜
	protected static final String OPEN_CAMERA = "OPEN_CAMERA"; //进入打开相机
	protected static final String ADD_DATE = "ADD_DATE"; //添加拍照日期
	protected static final String QUALITY = "QUALITY"; //图片质量
	protected static final String SAVE_PATH = "SAVE_PATH"; //图片保存路径
	protected static final String SAVE_LAST_COLOR = "SAVE_LAST_COLOR"; //美颜是否自动保存最后使用的颜色
	protected static final String AUTO_QUDOU = "AUTO_QUDOU"; //自动祛痘
	protected static final String AUTO_QUYANDAI = "AUTO_QUYANDAI"; //自动祛眼袋
	protected static final String AUTO_LIANGYAN = "AUTO_LIANGYAN"; //自动亮眼
	protected static final String AUTO_BIGEYE = "AUTO_BIGEYE"; //自动大眼
	protected static final String AUTO_THINFACE = "AUTO_THINFACE"; //自动瘦脸
	protected static final String TICK_SOUND = "TICK_SOUND"; //延时读秒声音
	protected static final String BEAUTY_AUTO_THINFACE = "BEAUTY_AUTO_THINFACE"; //美颜时自动瘦脸
	protected static final String PHOTO_WATERMARK_ID = "PHOTO_WATERMARK_ID"; //照片、萌装照、一键萌装水印

	//新浪
	protected static final String SINA_TOKEN = "SINA_TOKEN";
	protected static final String SINA_UID = "SINA_UID";
	protected static final String SINA_EXPIRES_IN = "SINA_EXPIRES_IN";
	protected static final String SINA_SAVE_TIME = "SINA_SAVE_TIME";
	protected static final String SINA_USER_NAME = "SINA_USER_NAME";
	protected static final String SINA_USER_NICK = "SINA_USER_NICK";
	//QQ空间
	protected static final String QZONE_TOKEN = "QZONE_TOKEN";
	protected static final String QZONE_OPENID = "QZONE_OPENID";
	protected static final String QZONE_EXPIRES_IN = "QZONE_EXPIRES_IN";
	protected static final String QZONE_SAVE_TIME = "QZONE_SAVE_TIME";
	protected static final String QZONE_USER_NAME = "QZONE_USER_NAME";
	//美人通行证
	protected static final String POCO2_ID = "POCO2_ID2"; //修改key清理之前的登录
	protected static final String POCO2_PSW = "POCO2_PSW";
	protected static final String POCO2_TOKEN = "POCO2_TOKEN2"; //修改key清理之前的登录
	protected static final String POCO2_REFRESH_TOKEN = "POCO2_REFRESH_TOKEN";
	protected static final String POCO2_PHONE = "POCO2_PHONE";
	protected static final String POCO2_AREA_CODE = "POCO2_AREA_CODE";
	protected static final String POCO2_EXPIRESIN = "POCO2_EXPIRESIN";
	protected static final String POCO2_HEAD_PATH = "POCO2_HEAD_PATH";
	protected static final String POCO2_HEAD_URL = "POCO2_HEAD_URL";
	protected static final String POCO2_CREDIT = "POCO2_CREDIT";
	protected static final String POCO2_BIRTHDAY_YEAR = "POCO2_BIRTHDAY_YEAR";
	protected static final String POCO2_BIRTHDAY_MONTH = "POCO2_BIRTHDAY_MONTH";
	protected static final String POCO2_BIRTHDAY_DAY = "POCO2_BIRTHDAY_DAY";
	protected static final String POCO2_SEX = "POCO2_SEX";
	protected static final String POCO2_LOCATION_ID = "POCO2_LOCATION_ID";
	protected static final String POCO2_REGISTER_TIME = "POCO2_REGISTER_TIME";
	protected static final String POCO_NICK = "POCO_NICK";

	protected void DataChange(boolean change)
	{
		m_change = change;
	}

	//照片水印id
    public void SetPhotoWatermarkId(int watermarkId)
    {
        m_data.put(PHOTO_WATERMARK_ID, String.valueOf(watermarkId));
        DataChange(true);
    }

    /**
     * 返回水印素材id（水印id为水印的统计id值）
     * @param defId 默认水印id {@link cn.poco.resource.WatermarkResMgr2#GetDefaultWatermarkId(Context)}
     * @return
     */
    public int GetPhotoWatermarkId(int defId)
    {
        int out = defId;

        String temp = m_data.get(PHOTO_WATERMARK_ID);
        if (temp != null && temp.length() > 0)
        {
            try
            {
                out = Integer.parseInt(temp);
            }
            catch (Throwable ignored)
            {
            }
        }
        return out;
    }

	//拍照声音
	public void SetCameraSoundState(boolean state)
	{
		if(state)
		{
			m_data.remove(CAMERA_SOUND);
		}
		else
		{
			m_data.put(CAMERA_SOUND, "1");
		}
		DataChange(true);
	}

	public boolean GetCameraSoundState()
	{
		boolean out = true;

		String temp = m_data.get(CAMERA_SOUND);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//拍照后自动保存图片
	public void SetAutoSaveCameraPhotoState(boolean state)
	{
		if(state)
		{
			m_data.remove(SAVE_PHOTO);
		}
		else
		{
			m_data.put(SAVE_PHOTO, "1");
		}
		DataChange(true);
	}

	public boolean GetAutoSaveCameraPhotoState()
	{
		boolean out = true;

		String temp = m_data.get(SAVE_PHOTO);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	public void SetActualBeautyState(boolean state)
	{
		if(state)
		{
			m_data.remove(ACTUAL_BEAUTY);
		}
		else
		{
			m_data.put(ACTUAL_BEAUTY, "1");
		}
		DataChange(true);
	}

	public boolean GetActualBeautyState()
	{
		boolean out = true;

		String temp = m_data.get(ACTUAL_BEAUTY);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//直接打开镜头
	public void SetOpenCameraState(boolean state)
	{
		if(state)
		{
			m_data.put(OPEN_CAMERA, "1");
		}
		else
		{
			m_data.remove(OPEN_CAMERA);
		}
		DataChange(true);
	}

	public boolean GetOpenCameraState()
	{
		boolean out = false;

		String temp = m_data.get(OPEN_CAMERA);
		if(temp != null)
		{
			out = true;
		}

		return out;
	}

	//照片加日期
	public void SetAddDateState(boolean state)
	{
		if(state)
		{
			m_data.put(ADD_DATE, "1");
		}
		else
		{
			m_data.remove(ADD_DATE);
		}
		DataChange(true);
	}

	public boolean GetAddDateState()
	{
		boolean out = false;

		String temp = m_data.get(ADD_DATE);
		if(temp != null)
		{
			out = true;
		}

		return out;
	}

	//延时读秒声音
	public void setTickSoundState(boolean state)
	{
		if(state)
		{
			m_data.put(TICK_SOUND, "1");
		}
		else
		{
			m_data.remove(TICK_SOUND);
		}
		DataChange(true);
	}

	public boolean GetTickSoundState()
	{
		boolean out = false;

		String temp = m_data.get(TICK_SOUND);
		if(temp != null)
		{
			out = true;
		}

		return out;
	}


	//画质
	public void SetQualityState(boolean state)
	{
		if(state)
		{
			m_data.put(QUALITY, "1");
		}
		else
		{
			m_data.put(QUALITY, "0");
			//m_data.remove(QUALITY);
		}
		DataChange(true);
	}

	public boolean GetQualityState()
	{
		boolean out = false;

		String temp = m_data.get(QUALITY);
		if(temp == null || temp.equals("1"))
		{
			out = true;
		}

		return out;
	}

	//图片的保存路径
	public void SetPhotoSavePath(String path)
	{
		m_data.put(SAVE_PATH, path);
		DataChange(true);
	}

	public String GetPhotoSavePath()
	{
		String out = m_data.get(SAVE_PATH);

		if(out == null || out.length() <= 0)
		{
			File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			if(dcim != null)
			{
				out = dcim.getAbsolutePath() + File.separator + "Camera";
			}
			else
			{
				out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera";
			}
			//魅族的默认相册路径不同，原来的路径图库不显示
			String manufacturer = android.os.Build.MANUFACTURER;
			if(manufacturer != null)
			{
				manufacturer = manufacturer.toLowerCase(Locale.getDefault());
				if(manufacturer.contains("meizu"))
				{
					out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Camera";
				}
			}
		}

		CommonUtils.MakeFolder(out);

		return out;
	}

	//保存最后一次使用的颜色
	public void SetSaveLastColor(boolean state)
	{
		if(state)
		{
			m_data.remove(SAVE_LAST_COLOR);
		}
		else
		{
			m_data.put(SAVE_LAST_COLOR, "1");
		}
		DataChange(true);
	}

	public boolean GetLastSaveColor()
	{
		boolean out = true;

		String temp = m_data.get(SAVE_LAST_COLOR);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//保存美颜时自动瘦脸
	public void SetBeautyAutoThinface(boolean state)
	{
		if(state)
		{
			m_data.remove(BEAUTY_AUTO_THINFACE);
		}
		else
		{
			m_data.put(BEAUTY_AUTO_THINFACE, "1");
		}
		DataChange(true);
	}

	public boolean GetBeautyAutoThinface()
	{
		boolean out = true;

		String temp = m_data.get(BEAUTY_AUTO_THINFACE);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//自动祛痘
	public void SetAutoQudou(boolean state)
	{
		if(state)
		{
			m_data.remove(AUTO_QUDOU);
		}
		else
		{
			m_data.put(AUTO_QUDOU, "1");
		}
		DataChange(true);
	}

	public boolean GetAutoQudou()
	{
		boolean out = true;

		String temp = m_data.get(AUTO_QUDOU);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//自动祛眼袋
	public void SetAutoQuyandai(boolean state)
	{
		if(state)
		{
			m_data.remove(AUTO_QUYANDAI);
		}
		else
		{
			m_data.put(AUTO_QUYANDAI, "1");
		}
		DataChange(true);
	}

	public boolean GetAutoQuyandai()
	{
		boolean out = true;

		String temp = m_data.get(AUTO_QUYANDAI);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//自动亮眼
	public void SetAutoLiangyan(boolean state)
	{
		if(state)
		{
			m_data.remove(AUTO_LIANGYAN);
		}
		else
		{
			m_data.put(AUTO_LIANGYAN, "1");
		}
		DataChange(true);
	}

	public boolean GetAutoLiangyan()
	{
		boolean out = true;

		String temp = m_data.get(AUTO_LIANGYAN);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//自动大眼
	public void SetAutoBigeye(boolean state)
	{
		if(state)
		{
			m_data.remove(AUTO_BIGEYE);
		}
		else
		{
			m_data.put(AUTO_BIGEYE, "1");
		}
		DataChange(true);
	}

	public boolean GetAutoBigeye()
	{
		boolean out = true;

		String temp = m_data.get(AUTO_BIGEYE);
		if(temp != null)
		{
			out = false;
		}

		return out;
	}

	//自动瘦脸
	public void SetAutoThinface(boolean state)
	{
		if(state)
		{
			m_data.put(AUTO_THINFACE, "1");
		}
		else
		{
			m_data.remove(AUTO_THINFACE);
		}
		DataChange(true);
	}

	public boolean GetAutoThinface()
	{
		boolean out = false;

		String temp = m_data.get(AUTO_THINFACE);
		if(temp != null)
		{
			out = true;
		}

		return out;
	}

	//新浪
	public void SetSinaAccessToken(String token)
	{
		if(token != null && token.length() > 0)
		{
			m_data.put(SINA_TOKEN, token);
		}
		else
		{
			m_data.remove(SINA_TOKEN);
		}
		DataChange(true);
	}

	public String GetSinaAccessToken()
	{
		if(Tools.isBindExpired(GetSinaExpiresIn(), GetSinaSaveTime(), 0)) return null;
		return m_data.get(SINA_TOKEN);
	}

	public void SetSinaUid(String uid)
	{
		if(uid != null && uid.length() > 0)
		{
			m_data.put(SINA_UID, uid);
		}
		else
		{
			m_data.remove(SINA_UID);
		}
		DataChange(true);
	}

	public String GetSinaUid()
	{
		if(Tools.isBindExpired(GetSinaExpiresIn(), GetSinaSaveTime(), 0)) return null;
		return m_data.get(SINA_UID);
	}

	public void SetSinaExpiresIn(String expiresIn)
	{
		if(expiresIn != null && expiresIn.length() > 0)
		{
			m_data.put(SINA_EXPIRES_IN, expiresIn);
		}
		else
		{
			m_data.remove(SINA_EXPIRES_IN);
		}
		DataChange(true);
	}

	public String GetSinaExpiresIn()
	{
		return m_data.get(SINA_EXPIRES_IN);
	}

	public void SetSinaSaveTime(String saveTime)
	{
		if(saveTime != null && saveTime.length() > 0)
		{
			m_data.put(SINA_SAVE_TIME, saveTime);
		}
		else
		{
			m_data.remove(SINA_SAVE_TIME);
		}
		DataChange(true);
	}

	public String GetSinaSaveTime()
	{
		return m_data.get(SINA_SAVE_TIME);
	}

	public void SetSinaUserName(String userName)
	{
		if(userName != null && userName.length() > 0)
		{
			m_data.put(SINA_USER_NAME, userName);
		}
		else
		{
			m_data.remove(SINA_USER_NAME);
		}
		DataChange(true);
	}

	public String GetSinaUserName()
	{
		return m_data.get(SINA_USER_NAME);
	}

	public void SetSinaUserNick(String userNick)
	{
		if(userNick != null && userNick.length() > 0)
		{
			m_data.put(SINA_USER_NICK, userNick);
		}
		else
		{
			m_data.remove(SINA_USER_NICK);
		}
		DataChange(true);
	}

	public String GetSinaUserNick()
	{
		return m_data.get(SINA_USER_NICK);
	}

	//QQ空间
	public void SetQzoneAccessToken(String token)
	{
		if(token != null && token.length() > 0)
		{
			m_data.put(QZONE_TOKEN, token);
		}
		else
		{
			m_data.remove(QZONE_TOKEN);
		}
		DataChange(true);
	}

	public String GetQzoneAccessToken()
	{
		if(Tools.isBindExpired(GetQzoneExpiresIn(), GetQzoneSaveTime(), 0)) return null;
		return m_data.get(QZONE_TOKEN);
	}

	public void SetQzoneOpenid(String openid)
	{
		if(openid != null && openid.length() > 0)
		{
			m_data.put(QZONE_OPENID, openid);
		}
		else
		{
			m_data.remove(QZONE_OPENID);
		}
		DataChange(true);
	}

	public String GetQzoneOpenid()
	{
		if(Tools.isBindExpired(GetQzoneExpiresIn(), GetQzoneSaveTime(), 0)) return null;
		return m_data.get(QZONE_OPENID);
	}

	public void SetQzoneExpiresIn(String expiresIn)
	{
		if(expiresIn != null && expiresIn.length() > 0)
		{
			m_data.put(QZONE_EXPIRES_IN, expiresIn);
		}
		else
		{
			m_data.remove(QZONE_EXPIRES_IN);
		}
		DataChange(true);
	}

	public String GetQzoneExpiresIn()
	{
		return m_data.get(QZONE_EXPIRES_IN);
	}

	public void SetQzoneSaveTime(String saveTime)
	{
		if(saveTime != null && saveTime.length() > 0)
		{
			m_data.put(QZONE_SAVE_TIME, saveTime);
		}
		else
		{
			m_data.remove(QZONE_SAVE_TIME);
		}
		DataChange(true);
	}

	public String GetQzoneSaveTime()
	{
		return m_data.get(QZONE_SAVE_TIME);
	}

	public void SetQzoneUserName(String userName)
	{
		if(userName != null && userName.length() > 0)
		{
			m_data.put(QZONE_USER_NAME, userName);
		}
		else
		{
			m_data.remove(QZONE_USER_NAME);
		}
		DataChange(true);
	}

	public String GetQzoneUserName()
	{
		return m_data.get(QZONE_USER_NAME);
	}

	//美人通行证
	public void SetPoco2Id(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_ID, value);
		}
		else
		{
			m_data.remove(POCO2_ID);
		}
		DataChange(true);
	}

	public static String GetPoco2Id(SharedPreferences sp, boolean check_expired)
	{
		String out = null;

		try
		{
			if(check_expired && Tools.isBindExpired(GetPoco2ExpiresIn(sp), MyFramework2App.getInstance().GetRunTime() / 1000, 86400))
			{
				out = null;
			}
			else
			{
				out = sp.getString(POCO2_ID, null);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public void SetPocoNick(String nick)
	{
		if(nick != null && nick.length() > 0)
		{
			m_data.put(POCO_NICK, nick);
		}
		else
		{
			m_data.remove(POCO_NICK);
		}
		DataChange(true);
	}

	public String GetPocoNick()
	{
		return m_data.get(POCO_NICK);
	}

	public String GetPoco2Id(boolean check_expired)
	{
		if(check_expired && Tools.isBindExpired(GetPoco2ExpiresIn(), MyFramework2App.getInstance().GetRunTime() / 1000, 86400))
			return null;
		return m_data.get(POCO2_ID);
	}

	public void SetPoco2Password(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_PSW, value);
		}
		else
		{
			m_data.remove(POCO2_PSW);
		}
		DataChange(true);
	}

	public String GetPoco2Password()
	{
		return m_data.get(POCO2_PSW);
	}

	public void SetPoco2Token(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_TOKEN, value);
		}
		else
		{
			m_data.remove(POCO2_TOKEN);
		}
		DataChange(true);
	}

	public static String GetPoco2Token(SharedPreferences sp, boolean check_expired)
	{
		String out = null;

		try
		{
			if(check_expired && Tools.isBindExpired(GetPoco2ExpiresIn(sp), MyFramework2App.getInstance().GetRunTime() / 1000, 86400))
			{
				out = null;
			}
			else
			{
				out = sp.getString(POCO2_TOKEN, null);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public String GetPoco2Token(boolean check_expired)
	{
		if(check_expired && Tools.isBindExpired(GetPoco2ExpiresIn(), MyFramework2App.getInstance().GetRunTime() / 1000, 86400))
			return null;
		return m_data.get(POCO2_TOKEN);
	}

	public void SetPoco2RefreshToken(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_REFRESH_TOKEN, value);
		}
		else
		{
			m_data.remove(POCO2_REFRESH_TOKEN);
		}
		DataChange(true);
	}

	public String GetPoco2RefreshToken()
	{
		return m_data.get(POCO2_REFRESH_TOKEN);
	}

	public void SetPoco2Phone(String value)
	{
		if(value != null && value.length() > 0)
		{
			BeautyUser.telNumber = value;
			m_data.put(POCO2_PHONE, value);
		}
		else
		{
			BeautyUser.telNumber = null;
			m_data.remove(POCO2_PHONE);
		}
		DataChange(true);
	}

	public String GetPoco2Phone()
	{
		return m_data.get(POCO2_PHONE);
	}

	public void SetPoco2AreaCode(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_AREA_CODE, value);
		}
		else
		{
			m_data.remove(POCO2_AREA_CODE);
		}
		DataChange(true);
	}

	public String GetPoco2AreaCode()
	{
		return m_data.get(POCO2_AREA_CODE);
	}

	public void SetPoco2ExpiresIn(String value)
	{
		if(value != null && value.length() > 0)
		{
			m_data.put(POCO2_EXPIRESIN, value);
		}
		else
		{
			m_data.remove(POCO2_EXPIRESIN);
		}
		DataChange(true);
	}

	public static String GetPoco2ExpiresIn(SharedPreferences sp)
	{
		String out = null;

		try
		{
			out = sp.getString(POCO2_EXPIRESIN, null);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public String GetPoco2ExpiresIn()
	{
		return m_data.get(POCO2_EXPIRESIN);
	}

	public void SetPoco2HeadPath(String path)
	{
		if(path != null && path.length() > 0)
		{
			m_data.put(POCO2_HEAD_PATH, path);
		}
		else
		{
			m_data.remove(POCO2_HEAD_PATH);
		}
		DataChange(true);
	}

	public String GetPoco2HeadPath()
	{
		return m_data.get(POCO2_HEAD_PATH);
	}

	public void SetPoco2HeadUrl(String url)
	{
		if(url != null && url.length() > 0)
		{
			m_data.put(POCO2_HEAD_URL, url);
		}
		else
		{
			m_data.remove(POCO2_HEAD_URL);
		}
		DataChange(true);
	}

	public String GetPoco2HeadUrl()
	{
		return m_data.get(POCO2_HEAD_URL);
	}

	public void SetPoco2Credit(String credit)
	{
		if(credit != null && credit.length() > 0)
		{
			m_data.put(POCO2_CREDIT, credit);
		}
		else
		{
			m_data.remove(POCO2_CREDIT);
		}
		DataChange(true);
	}

	public String GetPoco2Credit()
	{
		return m_data.get(POCO2_CREDIT);
	}


	public void SetPoco2BirthdayYear(String year)
	{
		if(year != null && year.length() > 0)
		{
			m_data.put(POCO2_BIRTHDAY_YEAR, year);
		}
		else
		{
			m_data.remove(POCO2_BIRTHDAY_YEAR);
		}
		DataChange(true);
	}

	public String GetPoco2BirthdayYear()
	{
		return m_data.get(POCO2_BIRTHDAY_YEAR);
	}

	public void SetPoco2BirthdayMonth(String month)
	{
		if(month != null && month.length() > 0)
		{
			m_data.put(POCO2_BIRTHDAY_MONTH, month);
		}
		else
		{
			m_data.remove(POCO2_BIRTHDAY_MONTH);
		}
		DataChange(true);
	}

	public String GetPoco2BirthdayMonth()
	{
		return m_data.get(POCO2_BIRTHDAY_MONTH);
	}

	public void SetPoco2BirthdayDay(String day)
	{
		if(day != null && day.length() > 0)
		{
			m_data.put(POCO2_BIRTHDAY_DAY, day);
		}
		else
		{
			m_data.remove(POCO2_BIRTHDAY_DAY);
		}
		DataChange(true);
	}

	public String GetPoco2BirthdayDay()
	{
		return m_data.get(POCO2_BIRTHDAY_DAY);
	}

	public void SetPoco2Sex(String sex)
	{
		if(sex != null && sex.length() > 0)
		{
			m_data.put(POCO2_SEX, sex);
		}
		else
		{
			m_data.remove(POCO2_SEX);
		}
		DataChange(true);
	}

	public String GetPoco2Sex()
	{
		return m_data.get(POCO2_SEX);
	}

	public void SetPoco2LocationId(String locationId)
	{
		if(locationId != null && locationId.length() > 0)
		{
			m_data.put(POCO2_LOCATION_ID, locationId);
		}
		else
		{
			m_data.remove(POCO2_LOCATION_ID);
		}
		DataChange(true);
	}

	public String GetPoco2LocationId()
	{
		return m_data.get(POCO2_LOCATION_ID);
	}

	public void SetPoco2RegisterTime(String time)
	{
		if(time != null && time.length() > 0)
		{
			m_data.put(POCO2_REGISTER_TIME, time);
		}
		else
		{
			m_data.remove(POCO2_REGISTER_TIME);
		}
		DataChange(true);
	}

	public String GetPoco2RegisterTime()
	{
		return m_data.get(POCO2_REGISTER_TIME);
	}

	public void ClearPoco2()
	{
		SetPoco2AreaCode(null);
		SetPoco2ExpiresIn(null);
		SetPoco2Id(null);
		SetPoco2Password(null);
		SetPoco2Phone(null);
		SetPoco2RefreshToken(null);
		SetPoco2Token(null);
		SetPoco2HeadPath(null);
		SetPoco2HeadUrl(null);
		SetPoco2Credit(null);
		SetPoco2BirthdayYear(null);
		SetPoco2BirthdayMonth(null);
		SetPoco2BirthdayDay(null);
		SetPoco2Sex(null);
		SetPoco2LocationId(null);
		SetPoco2RegisterTime(null);

		BeautyUser.userId = null;
		BeautyUser.telNumber = null;
		MyBeautyStat.checkLogin(MyApplication.getInstance());
	}
}
