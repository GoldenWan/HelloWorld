package cn.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SubTime {

	public static long getDaySub(String endDateStr) 
	{ 
	
	long hours=0;//小时
		try 
		{ //服务器时间格式
			DateFormat format = new SimpleDateFormat("MMM dd,yyyy KK:mm:ss aa", Locale.ENGLISH);
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			
			 Date endDate= format1.parse(endDateStr); //将时间字符串parse转换成Data类型
			 Date nowDate=format.parse(format.format(new Date()));//把当前时间format转换成String
			 long temp=endDate.getTime()-nowDate.getTime();//截止时间的总微秒
			// long day=temp/(24*60*60*1000); 
			 hours = temp / 1000 / 3600;                //相差小时数
			 //long mins = (temp % (1000 * 3600)) / 1000 / 60;  //相差分钟数
		    } catch (ParseException e){ 
			e.printStackTrace(); 
			} 
		return hours; //直至套餐结束，剩余多少小时
	}
		
	
		/*public static void main(String [] args){
			//2016-04-09 07:22:39至2016-05-08 23:59:59
			String str = "2016-05-01 10:03:12至2016-05-30 23:59:59";
			String str1=str.substring(20);//去掉网关流量:
			System.out.println(str1);
			long endTmie=getDaySub(str1);
			System.out.println(endTmie);
		}*/
}
