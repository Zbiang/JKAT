package cn.evaidc.JKAT;

/**
 * 保存搜索结果的类
 * @author Nathan
 *
 */
public class SearchResult 
{
	//对应的问题集编号
	public int question;
	//相关度。因为一些历史原因写成了可信度
	public float reliability;
	
	public SearchResult(int q, float r)
	{
		question = q;
		reliability = r;
	}
	
}
