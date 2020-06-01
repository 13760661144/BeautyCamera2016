package cn.poco.camera3.beauty.data;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public interface ResInfoArr<ResInfoType, ResInfoArrType>
{
    int GetResInfoArrSize(ResInfoArrType arr);

    ResInfoArrType MakeResInfoArrObj();

    boolean ResInfoArrAddItem(ResInfoArrType arr, ResInfoType item);
}
