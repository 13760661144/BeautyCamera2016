package cn.poco.makeup.makeup2;


import java.util.ArrayList;

import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.face.MakeupAlpha;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupType;

import static cn.poco.face.FaceDataV2.sFaceIndex;

public class Makeup2LocalData{

    public int m_faceNum;
    public ArrayList<MakeupSPage.DecalRes>[] m_DecalResArr;
    public ArrayList<MakeupRes.MakeupData>[] m_makeupDataArr;
    public MakeupAlpha[] m_makeupAlphaArr;
    public int[] m_shapeTypes;
    public int[] m_asetAlphas;
    public int[] m_asetUri;
    public int m_waterMarkId = -1;

    public Makeup2LocalData(int FaceNum)
    {
        m_faceNum = FaceNum;
        init();
    }

    private void init()
    {
        if(m_faceNum > 0)
        {
            m_DecalResArr = new ArrayList[m_faceNum];
            m_makeupDataArr = new ArrayList[m_faceNum];
            m_shapeTypes = new int[m_faceNum];
            m_makeupAlphaArr = new MakeupAlpha[m_faceNum];
            m_asetAlphas = new int[m_faceNum];
            m_asetUri = new int[m_faceNum];

            for(int i = 0; i < m_faceNum; i++)
            {
                m_DecalResArr[i] = new ArrayList<>();
                m_makeupDataArr[i] = new ArrayList<>();
                m_shapeTypes[i] = -1;
                m_makeupAlphaArr[i] = new MakeupAlpha();
                m_asetAlphas[i] = 80;
                m_asetUri[i] = -1;
            }
        }
    }

    public void SetMakeupData(MakeupRes data)
    {
        if(data != null && sFaceIndex != -1)
        {
            //s_makeupDatas.clear();
            if(m_makeupDataArr[sFaceIndex] != null)
            {
                m_makeupDataArr[sFaceIndex].clear();
            }

            if(data.m_groupRes != null)
            {
                MakeupRes.MakeupData item;
                for(int i = 0; i < data.m_groupRes.length; i++)
                {
                    item = data.m_groupRes[i];

                    int index = BeautifyResMgr2.GetInsertIndex(m_makeupDataArr[sFaceIndex], MakeupType.GetType(item.m_makeupType));
                    if(index >= 0)
                    {
                        m_makeupDataArr[sFaceIndex].add(index, item);
                    }

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
                        m_makeupAlphaArr[sFaceIndex].m_kohlAlpha = alpha;
                    }
                    if(type == MakeupType.EYELINER_DOWN_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyelineDownAlpha = alpha;
                    }
                    if(type == MakeupType.EYELINER_UP_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyelineUpAlpha = alpha;
                    }
                    if(type == MakeupType.EYE_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyeAlpha = alpha;
                    }
                    if(type == MakeupType.EYELASH_DOWN_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyelashDownAlpha = alpha;
                    }
                    if(type == MakeupType.EYELASH_UP_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyelashUpAlpha = alpha;
                    }
                    if(type == MakeupType.EYEBROW_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_eyebrowAlpha = alpha;
                    }
                    if(type == MakeupType.LIP.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_lipAlpha = alpha;
                    }
                    if(type == MakeupType.CHEEK_L.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_cheekAlpha = alpha;
                    }
                    if(type == MakeupType.FOUNDATION.GetValue())
                    {
                        m_makeupAlphaArr[sFaceIndex].m_foundationAlpha = alpha;
                    }
                }
            }
        }
    }


    public void setAsetUri(int uri)
    {
        m_asetUri[sFaceIndex] = uri;
    }

    public void setDecalRess(ArrayList<MakeupSPage.DecalRes> decalRess)
    {
        if(m_DecalResArr[sFaceIndex] != null)
        {
            m_DecalResArr[sFaceIndex].clear();
        }
        if(decalRess != null && decalRess.size() > 0)
        {
            for(int i = 0; i < decalRess.size(); i++)
            {
                m_DecalResArr[sFaceIndex].add(decalRess.get(i));
            }
        }
    }

    public void setShapeType(int type)
    {
        m_shapeTypes[sFaceIndex] = type;
    }
}
