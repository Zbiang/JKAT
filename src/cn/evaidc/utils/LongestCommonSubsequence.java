package cn.evaidc.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class LongestCommonSubsequence {

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
				//else
					//c[j] = 0;
			}
		}
		
		return maxLength;
	}
	
	private String source;
	private String target;
	
	private HashMap<Character, ArrayList<Integer>> tMap;
	
	public LongestCommonSubsequence()
	{
		tMap = new HashMap<Character, ArrayList<Integer>>();
		source = new String();
		target = new String();
	}
	public LongestCommonSubsequence(String source, String target)
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
		ArrayList<Integer> arr;
		int maxLength = 0;
		
		System.out.println(String.format("\n[compare]\nsource = %s, \ntarget = %s", source, target));
		
		for(int i=0;i<source.length();i++)
		{
			if((arr = tMap.get(source.charAt(i))) == null)
				continue;
			
			for(int j=arr.size()-1;j>=0;j--)
			{
				int tloc = arr.get(j);
				int tmax = 0;
				
				if(pairs.containsKey(tloc - 1))
				{
					pairs.put(tloc, tmax = pairs.get(tloc - 1) + 1);
					pairs.remove(tloc - 1);
				}
				else
					pairs.put(tloc, tmax = 1);
				
				System.out.println(String.format("[compare]Got a pair between source#%d:%c & target#%d:%c, %b, current streak is %d",
						i, source.charAt(i), tloc, target.charAt(tloc), arr.isEmpty(), tmax));
				
				if(tmax > maxLength) maxLength = tmax;
			}
		}
		
		return maxLength;
	}
}
