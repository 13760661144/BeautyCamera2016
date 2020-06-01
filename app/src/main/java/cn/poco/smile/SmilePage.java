package cn.poco.smile;

import android.content.Context;

import java.util.HashMap;

import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.framework.BaseSite;
import cn.poco.nose.AbsShapePage;
import cn.poco.smile.site.SmileSite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/2
 */
public class SmilePage extends AbsShapePage {

	private SmileSite mSite;

	private HashMap<String, Object> mParams;

	public SmilePage(Context context, BaseSite site) {
		super(context, site, new ITaskInfo() {
			@Override
			public int getMessageWhat() {
				return AbsShapePage.SMILE;
			}

			@Override
			public int getTitle() {
				return R.string.smile;
			}

			@Override
			public int getIcon() {
				return R.drawable.beautify_smile;
			}
		});

		mSite = (SmileSite)site;

		TongJiUtils.onPageStart(context, R.string.微笑);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_微笑页面_主页面);
	}

	/**
	 * 设置数据
	 * @param params 传入参数
	 *               imgs: RotationImg2[]/Bitmap
	 */
	@Override
	public void SetData(HashMap<String, Object> params) {
		mParams = new HashMap<>();
		mParams.put("imgs", params.get("imgs"));
		super.SetData(params);
	}

	@Override
	protected void progressChanged(FaceLocalData data, int faceIndex, int progress) {
		if (FaceDataV2.FACE_POS_MULTI == null || faceIndex >= FaceDataV2.FACE_POS_MULTI.length) {
			return;
		}

		if (data == null) {
			data = FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
		}
		mValue = progress;
		data.m_smileLevel_multi[faceIndex] = progress;
	}

	@Override
	protected int getProgress(FaceLocalData data, int faceIndex) {
		return data.m_smileLevel_multi[faceIndex];
	}

	@Override
	protected void openFixPage(int faceIndex) {
        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_微笑_手动定点);
		MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_手动定点);
		mParams.put("type", 5);
		mParams.put("index", faceIndex);
		mSite.openFixPage(getContext(), mParams);
	}

	@Override
	protected void cancel(HashMap<String, Object> temp) {
        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_微笑_取消);
		MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_取消);
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mBitmap);
		params.putAll(temp);
		mSite.onBack(getContext(), params);
	}

	@Override
	protected void save(HashMap<String, Object> temp) {
        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_微笑_确认);
		MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_确认);
		MyBeautyStat.onUseSmile(mValue);
		HashMap<String, Object> tempParams = new HashMap<>();
		tempParams.put("img", mResult);
		tempParams.putAll(temp);
		mSite.OnSave(getContext(), tempParams);
	}

	@Override
	public void onBack() {
		onCancel();
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(getContext(), R.string.微笑);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(getContext(), R.string.微笑);
	}

	@Override
	public void onClose() {
		super.onClose();
		TongJiUtils.onPageEnd(getContext(), R.string.微笑);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_微笑页面_主页面);
	}
}
