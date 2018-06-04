package cn.evaidc.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * �����뷨���ȶ�target�ַ�������HashMap��O(n)��
 * Ȼ�����HashMap���ַ���Ϊ��������������O(m), m=�ַ��Ը�����
 * ���ǵ�target�ַ����ڸ��龰����Ԥ�õģ��Ҹ�HashMap�������㷨��Ҳ���õ�
 * �ر��Ǻ���Ի����ظ��ַ��ԣ�������Ϊһ����ͬ�����ַ��ԣ�����Զ����Ӣ��
 * �ɴ��ڸ��龰��Ч�ʺܿ��ܻ�������ϵ��㷨
 * ���⣬���target�ַ����ϳ���״��Ҳ���и��ߵ�Ч��
 * 
 * �������ڻ���֧���������������
 * 
 * @author Nathan
 *
 */
public class LongestCommonSubstring 
{
	/**
	 * �㷨�������ϵ���������з������Ӵ���Ǩ�ƣ��ڱ��龳�¿���Ч�����ƫ�ͣ�������ʵ�ֳ�������
	 * @param a String һ���ַ���
	 * @param b String ��һ���ַ���
	 * @return ������Ӵ�����
	 */
	public static int getLCS_flat(String a, String b)
	{
		int maxLength = 0;
		int[] c = new int[a.length()]; 
		
		for(int i=0;i<a.length();i++)
		{
			char tmp = a.charAt(i);
			for(int j=b.length()-1;j>=0;j--)
			{
				//��ĩβ�����ľ����ı߿���е��Ż�?
				if(j != 0)
					if(b.length() - j + c[j-1] < maxLength || a.length() - i + c[j-1] < maxLength)
						continue;
				
				if(tmp == b.charAt(j))
				{
					if(j == 0)
						c[j] = 1;
					else
						c[j] = c[j-1] + 1;
					
					System.out.println(String.format("[flat]a#%d:%c equals b#%d:%c, current streak is %d"
							, i, a.charAt(i), j, b.charAt(j), c[j]));
					
					if(c[j] > maxLength)
						maxLength = c[j];
				}
				else
					c[j] = 0;
			}
		}
		
		return maxLength;
	}
	
	private String source;
	private String target;
	
	private HashMap<Character, ArrayList<Integer>> tMap;
	
	public LongestCommonSubstring()
	{
		tMap = new HashMap<Character, ArrayList<Integer>>();
		source = new String();
		target = new String();
	}
	public LongestCommonSubstring(String source, String target)
	{
		tMap = new HashMap<Character, ArrayList<Integer>>();
		this.source = new String();
		this.target = new String();
		this.setSource(source);
		this.setTarget(target);
	}
	
	public String getSource() {
		return source;
	}
	public String getTarget() {
		return target;
	}
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * tMap�ṹ��
	 * keyΪ��Ӧ�ַ���valueΪһ��ArrayList������ṹ��
	 * ArrayList�а������ַ�ÿ�γ�����target���е�λ��
	 * 
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
		tMap = new HashMap<Character, ArrayList<Integer>>();
		ArrayList<Integer> arr;
		
		for(int i=0;i<target.length();i++)
		{
			if((arr = tMap.get(target.charAt(i))) != null)
				arr.add(i);
			else
			{
				arr = new ArrayList<Integer>();
				arr.add(i);
				tMap.put(target.charAt(i), arr);
			}
			System.out.println(String.format("[hashmap]target#%d:%c updated freq %d", i, target.charAt(i), arr.size()));
		}
	}
	/**
	 * ����ʵ�֣�
	 * 
	 * ���Ѿ�����tMap֮��
	 * ��source�ַ������б�����
	 * ���ٶ�ÿ��source���ַ�Ϊkey���ҳ���ArrayList���б����������ַ��Ա�����
	 * ��������λ�����������ַ�������+1��Ų���Լ���λ�ã�����˼·�ӽ����ϵ��㷨��
	 * ��ѭ����������
	 * 
	 * ����һ��������⻷�����Ż�����ȽϺ�
	 * 
	 * @return
	 */
	public int getLCS()
	{
		if(target == null || source == null)
			return -1;
		
		HashMap<Integer, Integer> pairs = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> pairt = new HashMap<Integer, Integer>();
		ArrayList<Integer> arr;
		int maxLength = 0;
		
		System.out.println(String.format("\n[compare]\nsource = %s, \ntarget = %s", source, target));

		System.out.print("\n  ");
		for(int j=0;j<target.length();j++)
			System.out.print(target.charAt(j) + " ");
			
		for(int i=0;i<source.length();i++)
		{
			pairt = new HashMap<Integer, Integer>();
			if((arr = tMap.get(source.charAt(i))) != null)
			{
				for(int j=arr.size()-1;j>=0;j--)
				{
					int tloc = arr.get(j);
					int tmax = 0;
					
					if(pairs.containsKey(tloc - 1))
						pairt.put(tloc, tmax = pairs.get(tloc - 1) + 1);
					else
						pairt.put(tloc, tmax = 1);
					
					if(tmax > maxLength) maxLength = tmax;
				}
			}
			pairs = pairt;
			
			System.out.print("\n" + source.charAt(i) + " ");
			for(int j=0;j<target.length();j++)
			{
				String s = pairs.containsKey(j) ? new Integer(pairs.get(j)).toString() : "-";
				System.out.print(s + " ");
			}
		}
		
		return maxLength;
	}
}
