package cn.poco.login;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<SortModel> {

//	public int compare(SortModel o1, SortModel o2) {
//		if (o1.getSortLetters().equals("@")
//				|| o2.getSortLetters().equals("#")) {
//			return -1;
//		} else if (o1.getSortLetters().equals("#")
//				|| o2.getSortLetters().equals("@")) {
//			return 1;
//		} else {
//			return o1.getSortLetters().compareTo(o2.getSortLetters());
//		}
//	}
	public int compare(SortModel o1, SortModel o2) {
		ArrayList<String>spell1=o1.getEachChineseSpell();
		ArrayList<String>spell2=o2.getEachChineseSpell();
		int length=(spell1.size()<spell2.size())?spell1.size():spell2.size();
		for(int i=0;i<length;i++)
		{
			if(0==spell1.get(i).compareTo(spell2.get(i))){
				continue;
			}else{
				return spell1.get(i).compareTo(spell2.get(i));
			}
		}
		return (spell1.size()<spell2.size())?-1:1;//上面比较完毕全相等，返回最小长度的那个
	}
}
