package cn.evaidc.JKAT;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 测试使用的入口类
 * @author Nathan
 *
 */
public class TestEntrance {

	public static void main(String[] args) 
	{

		Scanner sac = new Scanner(System.in);
		
		JKAT jKat = new JKAT();
		jKat.start();
		jKat.addQuestion("补办校园卡");
		jKat.addQuestion("图书馆开门关门时间");
		jKat.addQuestion("校园网账号密码");
		
		while(true)
		{
			System.out.println("\n请输入问题：");
			String s = sac.nextLine();
			s = s.trim();
			
			/***********************************************************/
			/*****/ ArrayList<SearchResult> res = jKat.search(s); /*****/
			/***********************************************************/
			
			for(int i=0;i<res.size();i++)
				System.out.println("#" + (i+1) + ": " + res.get(i).reliability + " -> " 
						+ jKat.getQuestion(res.get(i).question));
			
			//根据反馈将用户提出的问题加到对应的问题集里，输入0代表跳过
			System.out.println("\n选择需要的问题编号：");
			String ansS = sac.nextLine();
			
			//万一有人写了个1000万呢对不对
			try{
				int ans = Integer.parseInt(ansS);
				if(ans == 0)
					continue;
				else
					jKat.addSubQuestion(res.get(ans - 1).question, s);
			}catch(Exception e){}
			
			//这段只是因为不写的话eclipse会疯狂提示我这里是个死循环Scanner流close不了
			//很烦所以要用这个来骗它:)
			int asldkfj = 4;
			if(asldkfj++ == 2)
				break;
		}
		sac.close();
	}

}
