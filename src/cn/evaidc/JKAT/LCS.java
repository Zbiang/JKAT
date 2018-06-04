package cn.evaidc.JKAT;

import java.util.ArrayList;

/**
 * 封装字符串算法的类。
 * @author Nathan
 *
 */
public class LCS 
{
	/**
	 * 获取所有的公共字串的长度<br>
	 * 用的就是很简单的那个铺一个方框的算法<br>
	 * 所以时间效率是A*B，空间效率是B
	 * @param a 一个字符串列表
	 * @param b 另一个字符串列表
	 * @return 长度列表
	 */
	public static ArrayList<Integer> getLCS(ArrayList<String> a, ArrayList<String> b)
	{	
		ArrayList<Integer> al = new ArrayList<Integer>();

		int[] c = new int[b.size()]; 
		
//		System.out.print("  ");
//		for(int i=0;i<b.size();i++)
//			System.out.print(b.get(i) + " ");
//		System.out.println(" ");
		
		for(int i=0;i<a.size();i++)
		{
			String tmp = a.get(i);
			for(int j=b.size()-1;j>=0;j--)
			{
				if(tmp.equals(b.get(j)))
				{
					if(j == 0)
						c[j] = 1;
					else
						c[j] = c[j-1] + 1;
				}
				else
				{
					if(j != 0 && c[j-1] != 0)
						al.add(c[j-1]);
					c[j] = 0;
				}
			}
			
//			System.out.print(tmp + " ");
//			for(int k=0;k<b.size();k++)
//				System.out.print(c[k] + " ");
//			System.out.println(" ");
		}
		
		for(int i=0;i<b.size();i++)
			if(c[i] != 0)
				al.add(c[i]);
		
		return al;
	}
	
	/**
	 * 获取最小编辑距离<br>
	 * 用的好像还是差不多的算法所以效率还是A*B<br>
	 * 空间复杂度还是B
	 * @param a  一个字符串列表
	 * @param b 另一个字符串列表
	 * @return 最小编辑距离
	 */
	public static int getLD(ArrayList<String> a, ArrayList<String> b)
	{
		int[] c = new int[b.size()]; 
		for(int i=0;i<b.size();i++)
			c[i] = i + 1;
		
//		System.out.print("[flat]\n   ");
//		for(int i=0;i<b.size();i++)
//			System.out.print(b.get(i) + "  ");
		
		for(int i=0;i<a.size();i++)
		{
			String tmp = a.get(i);
//			System.out.print("\n" + a.get(i));
			for(int j=b.size()-1;j>=0;j--)
			{
				//�༭���ַ�ʱ��ǰλ�õľ���
				int editD = j==0 ? i : c[j-1];
				if(!tmp.equals(b.get(j)))
					editD++;
				
				if(editD < ++c[j])
					c[j] = editD;
			}
			for(int j=0;j<b.size()-1;j++)
				if(c[j+1] > c[j] + 1)
					c[j+1] = c[j] + 1;

//			for(int j=0;j<b.size();j++)
//				System.out.print(String.format("%3d", c[j]));
		}
		
		return c[b.size() - 1];
	}
}
