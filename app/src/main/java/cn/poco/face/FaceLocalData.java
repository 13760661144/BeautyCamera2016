package cn.poco.face;

import android.util.SparseIntArray;

import java.util.ArrayList;

import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify.FaceType;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupType;

import static cn.poco.face.FaceDataV2.sFaceIndex;

public class FaceLocalData
{
	private static FaceLocalData sInstance = null;

	public int m_faceNum = -1;

	public int[] m_faceType_multi;
	public SparseIntArray[] m_faceLevelMap_multi;
	public int[] m_highNoseLevel_multi;
	public int[] m_brightEyeLevel_multi;
	public int[] m_eyeBagsLevel_multi;
	public int[] m_bigEyeLevel_multi;
	public int[] m_smileLevel_multi;
	public int[] m_ShrinkNose_multi;
	public int[] m_WhiteTeetch_multi;
	public ArrayList<MakeupRes.MakeupData>[] m_makeupDatas_multi;
	public MakeupAlpha[] m_makeupAlphas_multi;
	public int[] m_asetAlpha_multi;

	private FaceLocalData()
	{
	}

	public synchronized static FaceLocalData getInstance()
	{
		return sInstance;
	}

	public synchronized static FaceLocalData getInstance(int faceNum)
	{
		if(sInstance == null)
		{
			return getNewInstance(faceNum);
		}
		else
		{
			return getInstance();
		}
	}

	public synchronized static FaceLocalData getNewInstance(int faceNum)
	{
		ClearData();

		sInstance = new FaceLocalData();
		sInstance.Init(faceNum);

		return sInstance;
	}

	/**
	 * 如果不需要记录，那么在onClose的时候调用清理
	 */
	public synchronized static void ClearData()
	{
		sInstance = null;
	}

	protected void Init(int faceNum)
	{
		m_faceNum = faceNum;

		InitData();
	}

	protected void InitData()
	{
		if(m_faceNum > 0)
		{
			m_faceType_multi = new int[m_faceNum];
			m_faceLevelMap_multi = new SparseIntArray[m_faceNum];
			m_highNoseLevel_multi = new int[m_faceNum];
			m_brightEyeLevel_multi = new int[m_faceNum];
			m_eyeBagsLevel_multi = new int[m_faceNum];
			m_bigEyeLevel_multi = new int[m_faceNum];
			m_smileLevel_multi = new int[m_faceNum];
			m_ShrinkNose_multi = new int[m_faceNum];
			m_WhiteTeetch_multi = new int[m_faceNum];
			m_makeupDatas_multi = new ArrayList[m_faceNum];
			m_makeupAlphas_multi = new MakeupAlpha[m_faceNum];
			m_asetAlpha_multi = new int[m_faceNum];

			int len = m_faceNum;
			for(int i = 0; i < len; i++)
			{
				m_faceType_multi[i] = FaceType.FACE_OVAL;
				if(m_faceLevelMap_multi[i] == null)
				{
					m_faceLevelMap_multi[i] = new SparseIntArray();
				}
				else
				{
					m_faceLevelMap_multi[i].clear();
				}
				m_highNoseLevel_multi[i] = 0;
				m_brightEyeLevel_multi[i] = 0;
				m_eyeBagsLevel_multi[i] = 0;
				m_bigEyeLevel_multi[i] = 0;
				m_smileLevel_multi[i] = 0;
				m_ShrinkNose_multi[i] = 30;
				m_WhiteTeetch_multi[i] = 30;
				if(m_makeupDatas_multi[i] == null)
				{
					m_makeupDatas_multi[i] = new ArrayList<MakeupRes.MakeupData>();
				}
				else
				{
					m_makeupDatas_multi[i].clear();
				}
				if(m_makeupAlphas_multi[i] == null)
				{
					m_makeupAlphas_multi[i] = new MakeupAlpha();
				}
				else
				{
					m_makeupAlphas_multi[i].Reset();
				}
				m_asetAlpha_multi[i] = 100;
			}
		}
	}

	/*public void ResetData()
	{
		m_faceNum = -1;

		m_faceType_multi = null;
		m_faceLevelMap_multi = null;
		m_highNoseLevel_multi = null;
		m_brightEyeLevel_multi = null;
		m_eyeBagsLevel_multi = null;
		m_bigEyeLevel_multi = null;
		m_smileLevel_multi = null;
		m_makeupDatas_multi = null;
		m_makeupAlphas_multi = null;
		m_asetAlpha_multi = null;
	}*/

	public int GetFaceLevel()
	{
		return GetFaceLevel(FaceDataV2.sFaceIndex);
	}

	public int GetFaceLevel(int faceIndex)
	{
		int type = m_faceType_multi[faceIndex];
		return m_faceLevelMap_multi[faceIndex].get(type, 0);
	}

	public int GetEyeBagsLevel()
	{
		return GetEyeBagsLevel(FaceDataV2.sFaceIndex);
	}

	public int GetEyeBagsLevel(int faceIndex)
	{
		return m_eyeBagsLevel_multi[faceIndex];
	}

	public int GetBrightEyeLevel()
	{
		return GetBrightEyeLevel(FaceDataV2.sFaceIndex);
	}

	public int GetBrightEyeLevel(int faceIndex)
	{
		return m_brightEyeLevel_multi[faceIndex];
	}

	public int GetBigEyeLevel()
	{
		return GetBigEyeLevel(FaceDataV2.sFaceIndex);
	}

	public int GetBigEyeLevel(int faceIndex)
	{
		return m_bigEyeLevel_multi[faceIndex];
	}

	public int GetHighNoseLevel()
	{
		return GetHighNoseLevel(FaceDataV2.sFaceIndex);
	}

	public int GetHighNoseLevel(int faceIndex)
	{
		return m_highNoseLevel_multi[faceIndex];
	}

	public int GetSmileLevel()
	{
		return GetSmileLevel(FaceDataV2.sFaceIndex);
	}

	public int GetSmileLevel(int faceIndex)
	{
		return m_smileLevel_multi[faceIndex];
	}

	public int GetFaceType()
	{
		return GetFaceType(FaceDataV2.sFaceIndex);
	}

	public int GetFaceType(int faceIndex)
	{
		return m_faceType_multi[faceIndex];
	}

	public FaceLocalData Clone()
	{
		FaceLocalData out = new FaceLocalData();

		out.m_faceNum = m_faceNum;
		if(m_faceType_multi != null)
		{
			int len = m_faceType_multi.length;

			out.m_faceType_multi = new int[len];
			out.m_faceLevelMap_multi = new SparseIntArray[len];
			out.m_highNoseLevel_multi = new int[len];
			out.m_brightEyeLevel_multi = new int[len];
			out.m_eyeBagsLevel_multi = new int[len];
			out.m_bigEyeLevel_multi = new int[len];
			out.m_smileLevel_multi = new int[len];
			out.m_ShrinkNose_multi = new int[len];
			out.m_WhiteTeetch_multi = new int[len];
			out.m_makeupDatas_multi = new ArrayList[len];
			out.m_makeupAlphas_multi = new MakeupAlpha[len];
			out.m_asetAlpha_multi = new int[len];

			for(int i = 0; i < len; i++)
			{
				out.m_faceType_multi[i] = m_faceType_multi[i];

				{
					//out.m_faceLevelMap_multi[i] = m_faceLevelMap_multi[i].clone();
					SparseIntArray temp = new SparseIntArray();
					out.m_faceLevelMap_multi[i] = temp;
					SparseIntArray temp2 = m_faceLevelMap_multi[i];
					int l = temp2.size();
					for(int j = 0; j < l; j++)
					{
						temp.append(temp2.keyAt(j), temp2.valueAt(j));
					}
				}
				out.m_highNoseLevel_multi[i] = m_highNoseLevel_multi[i];
				out.m_brightEyeLevel_multi[i] = m_brightEyeLevel_multi[i];
				out.m_eyeBagsLevel_multi[i] = m_eyeBagsLevel_multi[i];
				out.m_bigEyeLevel_multi[i] = m_bigEyeLevel_multi[i];
				out.m_ShrinkNose_multi[i] = m_ShrinkNose_multi[i];
				out.m_WhiteTeetch_multi[i] = m_WhiteTeetch_multi[i];
				out.m_smileLevel_multi[i] = m_smileLevel_multi[i];
				{
					out.m_makeupDatas_multi[i] = new ArrayList<>();
					out.m_makeupDatas_multi[i].addAll(m_makeupDatas_multi[i]);
				}
				out.m_makeupAlphas_multi[i] = m_makeupAlphas_multi[i].Clone();
				out.m_asetAlpha_multi[i] = m_asetAlpha_multi[i];
			}
		}

		return out;
	}


	public void SetMakeupData(MakeupRes data)
	{
		if(data != null && sFaceIndex != -1)
		{
			//s_makeupDatas.clear();
			m_makeupDatas_multi[sFaceIndex].clear();

			if(data.m_groupRes != null)
			{
				MakeupRes.MakeupData item;
				for(int i = 0; i < data.m_groupRes.length; i++)
				{
					item = data.m_groupRes[i];
					AddMakeupItem(item);

					//重置透明度
					int type = item.m_makeupType;
					int alpha = item.m_defAlpha;
					//判断是否有套装颜色
					if(data.m_groupAlphas != null && data.m_groupAlphas.length > 0)
					{
						alpha = data.GetComboAlpha(type);
					}
					if(type == MakeupType.KOHL_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_kohlAlpha = alpha;
					}
					if(type == MakeupType.EYELINER_DOWN_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyelineDownAlpha = alpha;
					}
					if(type == MakeupType.EYELINER_UP_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyelineUpAlpha = alpha;
					}
					if(type == MakeupType.EYE_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyeAlpha = alpha;
					}
					if(type == MakeupType.EYELASH_DOWN_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyelashDownAlpha = alpha;
					}
					if(type == MakeupType.EYELASH_UP_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyelashUpAlpha = alpha;
					}
					if(type == MakeupType.EYEBROW_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_eyebrowAlpha = alpha;
					}
					if(type == MakeupType.LIP.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_lipAlpha = alpha;
					}
					if(type == MakeupType.CHEEK_L.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_cheekAlpha = alpha;
					}
					if(type == MakeupType.FOUNDATION.GetValue())
					{
						m_makeupAlphas_multi[sFaceIndex].m_foundationAlpha = alpha;
					}
				}
			}
		}
	}


	public boolean AddMakeupItem(MakeupRes.MakeupData data)
	{
		boolean out = false;

		if(sFaceIndex != -1 && m_makeupDatas_multi[sFaceIndex] != null && data != null)
		{
			int index = BeautifyResMgr2.GetInsertIndex(m_makeupDatas_multi[sFaceIndex], MakeupType.GetType(data.m_makeupType));
			if(index >= 0)
			{
				m_makeupDatas_multi[sFaceIndex].add(index, data);
			}
		}

		return out;
	}
}
