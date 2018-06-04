package cn.evaidc.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class LevenshteinDistance 
{
	
	public static int getLCS_flat(String a, String b)
	{
		int[] c = new int[b.length()]; 
		for(int i=0;i<b.length();i++)
			c[i] = i + 1;
		
		System.out.print("[flat]\n   ");
		for(int i=0;i<b.length();i++)
			System.out.print(b.charAt(i) + "  ");
		
		for(int i=0;i<a.length();i++)
		{
			char tmp = a.charAt(i);
			System.out.print("\n" + a.charAt(i));
			for(int j=b.length()-1;j>=0;j--)
			{
				//�༭���ַ�ʱ��ǰλ�õľ���
				int editD = j==0 ? i : c[j-1];
				if(tmp != b.charAt(j))
					editD++;
				
				if(editD < ++c[j])
					c[j] = editD;
			}
			for(int j=0;j<b.length()-1;j++)
				if(c[j+1] > c[j] + 1)
					c[j+1] = c[j] + 1;

			for(int j=0;j<b.length();j++)
				System.out.print(String.format("%3d", c[j]));
		}
		
		return c[b.length() - 1];
	}
	
	private String source;
	private String target;
	
	private HashMap<Character, ArrayList<Integer>> tMap;
	
	public LevenshteinDistance()
	{
		tMap = new HashMap<Character, ArrayList<Integer>>();
		source = new String();
		target = new String();
	}
	public LevenshteinDistance(String source, String target)
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
	 * ��bug��
	 * 
	 * @return
	 */
	public int getLCS_flatplus()
	{
		if(target == null || source == null)
			return -1;

		int[] value = new int[target.length()];
		int[] depth = new int[target.length()];
		ArrayList<Integer> arr;
		
		System.out.println(String.format("\n[compare]\nsource = %s, \ntarget = %s",
				source, target));

		System.out.print("\n ");
		for(int j=0;j<target.length();j++)
			System.out.print("  " + target.charAt(j));
		
		for(int i=0;i<target.length();i++)
			value[i] = depth[i] = -1;
		for(int i=0;i<source.length();i++)
		{
			if((arr = tMap.get(source.charAt(i))) != null)
			{
				for(int j=arr.size()-1;j>=0;j--)
				{
					int tloc = arr.get(j);
					
					int k = -1;
					for(k=tloc-1;k>=0;k--)
						if(value[k] != -1)
							break;
					if(k == -1)
					{
						depth[tloc] = i;
						value[tloc] = Math.max(i, tloc);
						continue;
					}
					depth[tloc] = i;
					value[tloc] = Math.max((i - depth[k]), (tloc - k)) + value[k] - 1;
				}
			}
			
			System.out.print("\n" + source.charAt(i) + "");
			for(int j=0;j<target.length();j++)
			{
				String s = depth[j] == i ? String.format("%3d", value[j]) : "  -";
				System.out.print(s);
			}
		}
		for(int i=target.length()-1;i>=0;i--)
			if(depth[i] != -1)
				return value[i] + Math.max(source.length() - depth[i], target.length() - i) - 1;
		
		return Math.max(source.length(), target.length());
	}
	public int getLCS()
	{
		/**
		 * һ�ֿ��ܵĶԴ�����������Ż����뷨��
		 * 
		 * ע�⵽������ɱ�ת��Ϊһ�������Ѱ·���⣺
		 * ->�����ߣ�����Ϊһ
		 * ->�����ߣ�����Ϊһ
		 * ->�������ߣ�����Ϊһ����һЩ��������Ϊ��
		 * ������ϵ㵽���µ����̾���
		 * 
		 * �����Ϊ������������
		 * 
		 *    a  b  a  d  c  c  e
		 * c  -  -  -  *  *  *  *  
		 * d  *  -  -  -  *  *  *
		 * a  *  *  -  -  -  *  *
		 * b  *  *  *  -  -  -  *
		 * e  *  *  *  *  -  -  -
		 * 
		 * ����*����ͨ����
		 * һ��-�����ȼ���
		 * 
		 * ���Ѱ·����ͨ��������ôһ���ǡ���·�ġ�
		 * ������·�������ͨ��������ôһ����һ������㹹�ɵġ�ͨ������ʹ�á���·���Ķ���������ֵ�õ�
		 * 
		 * ���� - fghͨ��
		 *    a  b  a  e  f  g  h
		 * c  -  -  - \4  *  *  *  
		 * f  *\ -  -  4 \4  *  *
		 * g  *  *\ -  -  5 \4  *
		 * h  *  *  *\ -  -  6 \4
		 * e  *  *  *  *\ -  -  5
		 * �������� = 2*0.5 = 1 < ��·��ʡ = 3*1 = 3
		 * ���ճ��� = 5 < ԭ������ = 7
		 * 
		 * �ڵȼ����У����ǳ�����Ϊ����·���ǻ����ȼ۵ģ��ؼ��������Ƿ�ͨ�������
		 * 
		 * �ɴˣ��Եȼ������������㷨
		 * ÿ��һ�����ĸ�������п��ǣ�
		 * ��߽硢�ұ߽硢�������㡢��������
		 * ÿ������ĺ����궼��Ӧһ���ַ��ԡ��������ڶ�Ӧ�ַ���������ǰһ��ֵ
		 * ������߽硢�ұ߽�ָ������ӽ�ͨ�������ַ��ԣ�����������ָ��ӽ��Խ��ߵ������ַ���
		 * �Խ��߼�diag = (float)(target.length()-1) * i / (source.length()-1)��iΪ��ǰ����
		 * ��������ص�
		 * 
		 * ����ͨ����ʹ��flat�������㣬����ô洢ȫ������ͨ��HashMap<Integer, ?<Integer, Integer>>
		 * ÿ�λ�����ұ߽�ֵʱ��������ֵ������ͨ������ȫ�����ܳ��ڵ�ֵ��һ�����飩���ж�̬�滮��������ѽ�
		 * �ڼ���ȼ��������Ӧ����ֵʱ�����ѵ���/��ͨ������ֵ��Ϊһ������·������
		 * 
		 * �磺
		 *    a  b  a  b  c  c  d  f
		 * a|D0D -  2B|E  *  *  *  *
		 * b  E|B0  -  2B|3  *  *  *
		 * c  *  1| -  -  2B|2  *  *
		 * d  *  *  2| -  -  - |2  *
		 * c  *  *  *  3|B3 L4B - |3
		 * f  *  *  *  *  4| -  - D3D|
		 * 
		 * D-/-D : Diagonal		B- : left Boundary		-B: right Boundary
		 * L- : Lower approach	-U : Upper approach (δ���֣�ȫ����-B����)
		 * E : Entrance
		 * 
		 * ���Ӧ���ܹ��õ�һ���൱�õĽ��ƽ⣬���ǿ��ǵ��ȼ������
		 * width = target.length() - source.length() + 1,
		 * �����������������˶������㣬
		 * ���ַ����Ƚ�Сʱ��ȫ���ֲ������κ����ƣ��������Ӧ�ò�������
		 * 
		 * ͬʱ������ȡ������������Ӧ����һ��̰���㷨����һ�����ʲ��ܵõ����Ž�
		 * ���ֻ������������ʱ���ǲ���
		 * 
		 * ���Ǿ͸ɴ�ûȥʵ�֡�
		 * �������뵽�ķ��������Ӧ���ǻ����ַ��Ե�Ч����ߵ�һ���㷨�ɣ�
		 * Ҳ���˵���˻����ַ�������뷨��������ⲻ�Ǻ�����
		 */
		return -1;
	}
}
