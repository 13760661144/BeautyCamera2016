package cn.poco.makeup.makeup2;


import cn.poco.face.FaceDataV2;

public class FaceDataBackups {
    private class TempFaceData
    {
        private float[] m_face_pos;
        private float[] m_face_eyebrow_pos;
        private float[] m_eye_pos;
        private float[] m_cheek_pos;
        private float[] m_lip_pos;
        private float[] m_nose_pos;
        private float[] m_chin_pos;
        private float[] m_raw_face;
        private float[] m_raw_all;
    }
    private TempFaceData m_data;
    //定点开始前备份数据
    public void backups()
    {
        m_data = new TempFaceData();
        if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_face_pos = FaceDataV2.FACE_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }
        if(FaceDataV2.EYEBROW_POS_MULTI != null && FaceDataV2.EYEBROW_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_face_eyebrow_pos = FaceDataV2.EYEBROW_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }
        if(FaceDataV2.EYE_POS_MULTI != null && FaceDataV2.EYE_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_eye_pos = FaceDataV2.EYE_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }
        if(FaceDataV2.CHEEK_POS_MULTI != null && FaceDataV2.CHEEK_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_cheek_pos = FaceDataV2.CHEEK_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }
        if(FaceDataV2.LIP_POS_MULTI != null && FaceDataV2.LIP_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_lip_pos = FaceDataV2.LIP_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }

        if(FaceDataV2.NOSE_POS_MULTI != null && FaceDataV2.LIP_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_nose_pos = FaceDataV2.NOSE_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }

        if(FaceDataV2.CHIN_POS_MULTI != null && FaceDataV2.CHIN_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_chin_pos = FaceDataV2.CHIN_POS_MULTI[FaceDataV2.sFaceIndex].clone();
        }

        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_raw_face = FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex].getFaceRect().clone();
        }

        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex] != null)
        {
            m_data.m_raw_all = FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex].getFaceFeaturesMakeUp().clone();
        }
    }

    //定点取消恢复原来定点数据
    public void restore()
    {
        if(m_data != null)
        {
            if(m_data.m_face_pos != null)
            {
                FaceDataV2.FACE_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_face_pos;
            }
            if(m_data.m_face_eyebrow_pos != null)
            {
                FaceDataV2.EYEBROW_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_face_eyebrow_pos;
            }
            if(m_data.m_eye_pos != null)
            {
                FaceDataV2.EYE_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_eye_pos;
            }
            if(m_data.m_cheek_pos != null)
            {
                FaceDataV2.CHEEK_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_cheek_pos;
            }
            if(m_data.m_lip_pos != null)
            {
                FaceDataV2.LIP_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_lip_pos;
            }

            if(m_data.m_nose_pos != null)
            {
                FaceDataV2.NOSE_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_nose_pos;
            }

            if(m_data.m_chin_pos != null)
            {
                FaceDataV2.CHIN_POS_MULTI[FaceDataV2.sFaceIndex] = m_data.m_chin_pos;
            }

            if(m_data.m_raw_face != null)
            {
                FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex].setFaceRect(m_data.m_raw_face);
            }

            if(FaceDataV2.RAW_POS_MULTI != null)
            {
                FaceDataV2.RAW_POS_MULTI[FaceDataV2.sFaceIndex].setMakeUpFeatures(m_data.m_raw_all);
            }
        }
    }
}
