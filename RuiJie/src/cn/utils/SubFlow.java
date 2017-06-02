package cn.utils;



public class SubFlow {
   //网关流量:5 GB 777.20 MB /22 GB 0.00 MB
   public static double getFlowSub(String Usedflow1, String Sumflow1) {
	  double Usedflow=0;//已使用流量
	  double Unusedflow=0;//未使用流量
	  double Sumflow=0;//套餐总流量
	 
	  String UnusedMflow=null;
	  String SumGflow=null;
	  String SumMflow=null;
	 // 5 GB 777.20 MB /22 GB 0.00 MB
	 if (Usedflow1.indexOf("GB")  > -1){
		 String UnusedGflow= Usedflow1.substring(0, Usedflow1.indexOf("GB")-1);//5
		 UnusedMflow= Usedflow1.substring(Usedflow1.indexOf("GB")+3, Usedflow1.indexOf("MB"));
	     Usedflow= Double.parseDouble(UnusedGflow.trim())*1024+ Double.parseDouble(UnusedMflow.trim());
	     SumGflow= Sumflow1.substring(0, Sumflow1.indexOf("GB")-1);//5
	     SumMflow= Sumflow1.substring(Sumflow1.indexOf("GB")+3, Sumflow1.indexOf("MB"));
	     Sumflow= Double.parseDouble(SumGflow.trim())*1024+ Double.parseDouble(SumMflow.trim());
    	 Unusedflow=Sumflow-Usedflow;//以MB为单位的剩余流量
	 		 }
        else {
       //  UnusedMflow= Usedflow1.substring(0,Usedflow1.indexOf("MB"));
   	     Usedflow=Double.parseDouble(Usedflow1.substring(0,Usedflow1.indexOf("MB")).trim());
   	     SumGflow= Sumflow1.substring(0, Sumflow1.indexOf("GB")-1);//5
   	     SumMflow= Sumflow1.substring(Sumflow1.indexOf("GB")+3, Sumflow1.indexOf("MB"));
   	     Sumflow= Double.parseDouble(SumGflow.trim())*1024+ Double.parseDouble(SumMflow.trim());
       	 Unusedflow=Sumflow-Usedflow;//以MB为单位的剩余流量
   	     
       	  }
       
	  return Unusedflow;
    	  
      }
 
}