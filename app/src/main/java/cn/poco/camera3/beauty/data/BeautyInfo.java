package cn.poco.camera3.beauty.data;

import java.io.Serializable;

/**
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public class BeautyInfo extends BaseInfo implements Serializable
{
    public BeautyData data = new BeautyData();

    public BeautyData getData()
    {
        return data;
    }

    public void setData(BeautyData data)
    {
        this.data = data;
    }

    public void setParamsData(BeautyData defData)
    {
        if (this.data == null) this.data = new BeautyData();
        if (defData != null)
        {
            this.data.setSkinType(defData.getSkinType());
            this.data.setWhitenTeeth(defData.getWhitenTeeth());
            this.data.setSkinBeauty(defData.getSkinBeauty());
        }
    }

    public void setDefData()
    {
        setParamsData(SuperShapeData.GetDefBeautyData());
    }
}
