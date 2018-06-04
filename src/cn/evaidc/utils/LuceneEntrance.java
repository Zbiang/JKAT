package cn.evaidc.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LuceneEntrance {

	public static void main(String[] args) 
	{
        String keyWord = "小明讲了个笑话真好笑啊哈哈哈哈哈哈啊哈";  

        IKAnalyzer analyzer = new IKAnalyzer();  
        
        analyzer.setUseSmart(false);  

        try {  
        	printAnalysisResult(analyzer, keyWord);
            HashMap<String, Integer> map = getFreqMap(analyzer, keyWord);  
            map.forEach(new PrintNode());
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
	/**
	 * 分词使用的主方法
	 * @param keyWord 待分词的字符串
	 * @return 分词后的词语序列
	 */
	public static ArrayList<String> analysis(String keyWord)
	{
		ArrayList<String> al = new ArrayList<String>();
		
        IKAnalyzer analyzer = new IKAnalyzer();  
        
        analyzer.setUseSmart(false);  

        try 
        {  
            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));  
            tokenStream.addAttribute(CharTermAttribute.class);  
            tokenStream.reset();
            while (tokenStream.incrementToken()) 
            {  
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                al.add(charTermAttribute.toString());
            }  
            
            tokenStream.close();
            analyzer.close();
            
            return al;
        }
        catch (Exception e) 
        {  
            e.printStackTrace();  
            return null;
        }  
	}
	
    private static void printAnalysisResult(Analyzer analyzer, String keyWord)  
            throws Exception {  
        System.out.println("\n["+keyWord+"]:");  
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));  
        tokenStream.addAttribute(CharTermAttribute.class);  
        tokenStream.reset();
        while (tokenStream.incrementToken()) {  
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);  
            System.out.print(charTermAttribute.toString() + "|");
        }  
        tokenStream.close();
    }  
    
    private static HashMap<String, Integer> getFreqMap(Analyzer analyzer, String keyWord) throws Exception {  
        System.out.println("\n\n[Freq]");  
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));  
        tokenStream.addAttribute(CharTermAttribute.class);  
        tokenStream.reset();
        
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        
        while (tokenStream.incrementToken()) {  
            String s = tokenStream.getAttribute(CharTermAttribute.class).toString();
            int base = map.containsKey(s) ? map.get(s) : 0;
            map.put(s, ++base);
        }  
        tokenStream.close();
        return map;
    }  
}

class PrintNode implements BiConsumer<String, Integer>
{
	public void accept(String s, Integer i)
	{
	    System.out.println(s + ": " + i);
	}
}
