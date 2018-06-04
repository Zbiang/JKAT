package cn.evaidc.JKAT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import cn.evaidc.utils.LuceneEntrance;
/**
 * 问题匹配处理主类
 * JKAT的名字是瞎起的
 * @author Nathan
 *
 */
public class JKAT 
{
	private ArrayList<ArrayList<String>> ql;
	private ArrayList<ArrayList<Integer>> ttl;
	private float resultThrs;
	private float compCoef;
	private int maxLifetime;
	private int presetQuestion;
	
	/**
	 * 初始化函数，会初始化对象中存储的内容。
	 * 考虑到之后可能会做扩展所以没有写在构造函数里
	 * 总之使用的时候要注意JKAT对象要先调用这个函数
	 */
	public void start()
	{
		ql = new ArrayList<ArrayList<String>>();
		ttl = new ArrayList<ArrayList<Integer>>();
		resultThrs = .0002f;
		compCoef = 1.0f;
		maxLifetime = 100;
		presetQuestion = 5;
	}
	
	/**
	 * 搜索所提出的问题对应的问题集，
	 * 每个问题-问题的相关度由单字/分词的公共子串和编辑距离共四项合成<br>
	 * 然后第二次合成获得问题-问题集的相关度，并以此对问题集相关度进行排序<br>
	 * 最后按 “标准差*平均值 &lt; 筛选阈值” 的条件按相关度由高至低添加，并返回最终结果
	 * @param s 提出的问题字符串
	 * @return 处理后的待选择结果
	 */
	public ArrayList<SearchResult> search(String s)
	{
		//各个问题集的相关度列表
		ArrayList<SearchResult> relation = new ArrayList<SearchResult>();
		for(int i=0;i<ql.size();i++)
		{
			//该问题集中的所有问题的列表
			ArrayList<String> list = ql.get(i);
			
			//分词后的字符串列表
			ArrayList<String> anaStr = LuceneEntrance.analysis(s);
			//单字字符串列表
			ArrayList<String> str = toList(s);
			
			//第一次合成所用的列表
			ArrayList<Float> relList = new ArrayList<Float>();
			//第二次合成所用的列表
			ArrayList<Float> rrList = new ArrayList<Float>();
			
			//单字公共字串相关度
			for(int j=0;j<list.size();j++)
				relList.add(getLCSRel(toList(list.get(j)), str));
			
			rrList.add(synthesisRel(relList));
			relList.clear();
			
			//单字文本编辑距离相关度
			for(int j=0;j<list.size();j++)
				relList.add(getLDRel(toList(list.get(j)), str));
			
			rrList.add(synthesisRel(relList));
			relList.clear();
			
			//分词公共字串相关度
			for(int j=0;j<list.size();j++)
				relList.add(getLCSRel(LuceneEntrance.analysis(list.get(j)), anaStr));
			
			rrList.add(synthesisRel(relList));
			relList.clear();
			
			//分词文本编辑距离相关度
			for(int j=0;j<list.size();j++)
				relList.add(getLDRel(LuceneEntrance.analysis(list.get(j)), anaStr));
			
			rrList.add(synthesisRel(relList));
			relList.clear();
			
			//总相关度
			float rr = synthesisRel(rrList);
			
			//调试信息#1
			System.out.println("\n将问题与\"" + getQuestion(i)  + "\"(size: " 
					+ list.size() + ")对比：");
			System.out.println("单字公共子串相似度：" + rrList.get(0));
			System.out.println("单字编辑距离相似度：" + rrList.get(1));
			System.out.println("分词公共子串相似度：" + rrList.get(2));
			System.out.println("分词编辑距离相似度：" + rrList.get(3));
			System.out.println("总相似度：" + rr + "\n");
			
			relation.add(new SearchResult(i, rr));
		}
		//按相关度由高至低排序
		relation.sort(new cmp());
		
		//调试信息#2
		for(int i=0;i<relation.size();i++)
		{
			System.out.println(i + ": " + relation.get(i).reliability + " -> "
					+ this.getQuestion(relation.get(i).question));
		}
		
		//输出用的列表
		ArrayList<SearchResult> result = new ArrayList<SearchResult>();
		
		float dif = 0;			//计算标准差*平均值
		int index = 0;			//列表中已加入的数量
		boolean flag = false;	//用来确定跳出循环的原因
		do{
			flag = false;
			dif = .0f;
			float avg = 0;
			
			if(index == relation.size())
				break;
			System.out.println("\n" + index + ": ");
			result.add(relation.get(index++));
			
			for(int i=0;i<result.size();i++)
				avg += result.get(i).reliability;
			avg /= result.size();
			System.out.println("avg = " + avg);
			
			//如果result列表中有多于一项则计算“标准差*平均值”
			if(result.size() > 1)
			{
				for(int i=0;i<result.size();i++)
					dif += Math.pow(result.get(i).reliability - avg, 2);
				dif = (float) (Math.sqrt(dif) * avg / result.size());
			}
			flag = true;
			
			System.out.println("dif = " + dif + "\n");
		}
		while(dif <= this.resultThrs);
		
		//如果因为超出阈值跳出循环则移出最后一项
		if(flag)
			result.remove(index - 1);
		return result;
	}
	
	/**
	 * 生成公共子串相关度<br>
	 * 即getCommonSubstringRelativity, L是历史原因: ]<br>
	 * 时间效率是A*B<br>
	 * 相关度生成为r<sub>CS</sub> = ( ∑ l<sub>i</sub> ) / (A * B)，
	 * 其中l<sub>n</sub>表示n个公共字串。<br>
	 * 每个公共子串不重叠，即"arc"和"car"有且仅有两个公共子串，长度为{1, 2}
	 * @param a	一个字符串，习惯上建议在这里填写<b>问题集</b>问题
	 * @param b 另一个字符串，习惯上建议在这里填写<b>所问</b>问题
	 * @return 生成的相关度数值
	 */
	private float getLCSRel(ArrayList<String> a, ArrayList<String> b)
	{
		ArrayList<Integer> ll = LCS.getLCS(a, b);
		
		float div = a.size() * b.size();
		float x = .0f;
		
		for(int i=0;i<ll.size();i++)
			x += ll.get(i) * ll.get(i);
		
		return x/div;
	}
	
	/**
	 * 生成编辑距离相关度<br>
	 * 即getLevenshteinDistanceRelativity<br>
	 * 时间效率是A*B<br>
	 * 相关度生成为r<sub>LD</sub> = (1 - l / max{a,b})<sup>2</sup>
	 * @param a 一个字符串，习惯上建议在这里填写<b>问题集</b>问题
	 * @param b 另一个字符串，习惯上建议在这里填写<b>所问</b>问题
	 * @return 生成的相关度数值
	 */
	private float getLDRel(ArrayList<String> a, ArrayList<String> b)
	{
		int ll = LCS.getLD(a, b);
		
		return (float) Math.pow(1 - (float)ll / Math.max(a.size(), b.size()), 2);
	}
	
	/**
	 * 将字符串转换为单字的字符列表<br>
	 * 因为总归分词和单字是要格式统一的处理的嘛<br>
	 * 生硬的重载那些所有的生成相关度的函数很难看所以用这个吧<br>
	 * @param s 字符串
	 * @return 单字的字符串列表
	 */
	private static ArrayList<String> toList(String s)
	{
		ArrayList<String> sl = new ArrayList<String>();
		for(int i=0;i<s.length();i++)
			sl.add(String.format("%c", s.charAt(i)));
		return sl;
	}
	
	/**
	 * 相关度合成函数<br>
	 * 合成方法为R = sqrt( ∑ r<sub>i</sub><sup>2</sup> / n)
	 * @param al 待合成的相关度列表
	 * @return 合成后的相关度值
	 */
	private static float synthesisRel(ArrayList<Float> al)
	{
		float x = .0f;
		for(int i=0;i<al.size();i++)
			x += Math.pow(al.get(i), 2);
		return (float) Math.sqrt(x / al.size()); 
	}

	/**
	 * 向一个问题集中添加子问题<br>
	 * 如果该问题集中已有该子问题则按概率随机决定添加<br>
	 * 概率为p = e<sup>-k*x</sup>，其中k为常数，x为已有的重复问题个数<br>
	 * 添加成功时该问题集中所有非预设问题的“年龄”+1<br>
	 * “年龄”>预设值时该问题会被从问题集中删除<br>
	 * @param index 问题集编号
	 * @param s 子问题字符串
	 */
	public void addSubQuestion(int index, String s)
	{
		if(ql.get(index).contains(s))
		{
			int num = 0;
			for(int i=0;i<ql.get(index).size();i++)
				if(ql.get(index).get(i).equals(s))
					num++;
			
			float chance = (float) Math.exp(-compCoef * num);
			Random rand = new Random();
			if(rand.nextFloat() > chance)
				return ;
		}
		
		ql.get(index).add(s);
		ttl.get(index).add(0);
		
		
		for(int i=presetQuestion-1;i<ttl.get(index).size();i++)
		{
			ttl.get(index).set(i, ttl.get(index).get(i) + 1);
			if(ttl.get(index).get(i) >= maxLifetime)
			{
				ttl.get(index).remove(i);
				ql.get(index).remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 获得问题集编号对应的主问题<br>
	 * 主问题即该问题集中编号为0的问题
	 * @param index 问题集的编号
	 * @return 主问题字符串
	 */
	public String getQuestion(int index)
	{
		return ql.get(index).get(0);
	}
	
	/**
	 * 添加问题集<br>
	 * 必须要有主问题才能添加
	 * @param s 问题集的主问题
	 */
	public void addQuestion(String s)
	{
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Integer> life = new ArrayList<Integer>();
		list.add(s);
		life.add(0);
		ql.add(list);
		ttl.add(life);
	}
}

/**
 * 用来做SearchResult类的比较的类<br>
 * 讲道理的话没必要单开一个类随便谁用一下这个接口就行<br>
 * 嘛总之这样直观点喽？<br>
 * 比较的话是使相关度较高的对象排在前面
 * @author Nathan
 *
 */
class cmp implements Comparator<SearchResult>
{
	@Override
	public int compare(SearchResult o1, SearchResult o2) 
	{
		float f = o1.reliability - o2.reliability;
		return f == 0 ? 0 : (f > 0.f ? -1 : 1);		
	}
	
}
