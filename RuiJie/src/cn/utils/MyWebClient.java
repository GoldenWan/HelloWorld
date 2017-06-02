package cn.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.domain.mainpage;
import cn.domain.netRecord;
import cn.domain.userInfo;
import cn.domain.zhangWu;
import cn.myparser.MyParser;
import cn.service.Userservice;
import cn.service.model;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class MyWebClient {
	protected WebClient client;

	public MyWebClient() {
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setTimeout(10000);
	}

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 * @throws Exception
	 */
	public void login(String account, String password) throws Exception {
		String verifyUrl = "http://210.47.160.36:8080/selfservice/common/web/verifycode.jsp"; // 验证码图片
		String judgeUrl = "http://210.47.160.36:8080/selfservice/module/scgroup/web/login_judge.jsf"; // 登录验证页面
		try {
			/*
			 * ① 获取验证码图片并解析，与此同时，验证码图片对应的cookie会自动保存在client对象中
			 */
			InputStream stream = client.getPage(verifyUrl).getWebResponse().getContentAsStream();
			BufferedImage bi = null;
			try {
				bi = ImageIO.read(stream); // 读取文件，生成对应的输入流
			} catch (Exception e) {
				e.printStackTrace();
			}
			String verifyCode = VerifyUtils.parserVerifyCodeImage(bi);

			/*
			 * ② 通过POST请求，提交表单到验证登录的界面中
			 */
			URL url = new URL(judgeUrl);//1.action
			WebRequest webRequest = new WebRequest(url, HttpMethod.POST);//2.method
			List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
			reqParam.add(new NameValuePair("act", "add"));
			reqParam.add(new NameValuePair("name", account));
			reqParam.add(new NameValuePair("password", password));
			reqParam.add(new NameValuePair("verify", verifyCode));
			webRequest.setRequestParameters(reqParam);
			client.getPage(webRequest);
			/*
			 * ③ 登录成功后，就可以到处跳转了
			 */
			// 首页界面
			String mainUrl = "http://210.47.160.36:8080/selfservice/module/webcontent/web/content_self.jsf"; // 登录之后主界面
			HtmlPage mainPage = client.getPage(mainUrl);
			Document doc = Jsoup.parse(mainPage.asXml());
			MyParser parser = new MyParser(); // 定义一个解析类的对象
			// System.out.println(doc);
			/*
			 * 首页，有三种情况
			 */
			mainpage main = parser.parserMainPage(doc);
//想给用户返回，然后在存储用户信息
			
			
			if (model.checkMainPageExist(account)) {// 用户名已存在){
				if (!main.getPackageflow().equals(Userservice.findPackageflow(account))
						|| !main.getCycleTime().equals(Userservice.findCycleTime(account))) {
					Userservice.updateMainPage(account, main.getStatus(), main.getAccountway(), main.getCycleTime(),
							main.getBalance(), main.getPackageflow(), main.getWithhold());
					System.out.println("信息更新完成！");
				}
			} else {
				mainpage main1 = new mainpage();
				main1.setAccount(account);
				main1.setStatus(main.getStatus());
				main1.setAccountway(main.getAccountway());
				main1.setCycleTime(main.getCycleTime());
				main1.setBalance(main.getBalance());
				main1.setPackageflow(main.getPackageflow());
				main1.setWithhold(main.getWithhold());
				model userInfo = new model();
				userInfo.addMainpage(main1);
				System.out.println("信息添加成功");

			}

			// 上网记录界面
			String recordUrl = "http://210.47.160.36:8080/selfservice/module/onlineuserself/web/onlinedetailself_list.jsf";
			HtmlPage recordPage = client.getPage(recordUrl);
			Document doc1 = Jsoup.parse(recordPage.asXml());

			MyParser parser1 = new MyParser();
			// ?????????????????
			ArrayList<netRecord> records = parser1.parserRecordPage(doc1);
			for (int i = 0; i < records.size(); i++) {
				netRecord record = records.get(i);
				if (model.checkRecordPageExist(account, record.getOntime())) {
					System.out.println("信息已存在！");

				} else {

					netRecord record1 = new netRecord();
					record1.setAccount(account);
					record1.setOntime(record.getOntime());
					record1.setOfftime(record.getOfftime());
					record1.setUserIPv4(record.getUserIPv4());
					record1.setService(record.getService());
					record1.setOffreason(record.getOffreason());

					record1.setOncost(record.getOncost());
					model Record = new model();
					Record.addRecordpage(record1);
					System.out.println("添加成功");
				}
			}

			// 账务流水界面
			String zhangwuUrl = "http://210.47.160.36:8080/selfservice/module/billself/web/accountflowself_list.jsf";
			HtmlPage zhangwuPage = client.getPage(zhangwuUrl);
			Document doc2 = Jsoup.parse(zhangwuPage.asXml());
			MyParser parser2 = new MyParser(); // 定义一个解析类的对象
			ArrayList<zhangWu> zhangWus = parser2.parserZhangwuPage(doc2);
			for (int i = 0; i < zhangWus.size(); i++) {
				zhangWu zhangwu = zhangWus.get(i);
				if (model.checkZhangwuPageExist(account, zhangwu.getGeneratedtime())) {// 用户名已存在){
					System.out.println("信息已存在！");

				} else {
					zhangWu zhangwu1 = new zhangWu();
					zhangwu1.setUsername(zhangwu.getUsername());
					zhangwu1.setAccount(zhangwu.getAccount());
					zhangwu1.setBillsource(zhangwu.getBillsource());
					zhangwu1.setFee(zhangwu.getFee());
					zhangwu1.setNowbalance(zhangwu.getNowbalance());
					zhangwu1.setNowwithhold(zhangwu.getNowwithhold());
					zhangwu1.setGeneratedtime(zhangwu.getGeneratedtime());
					zhangwu1.setBusinessvolume(zhangwu.getBusinessvolume());
					zhangwu1.setWithholdrole(zhangwu.getWithholdrole());
					model Zhangwu = new model();
					Zhangwu.addZhangWupage(zhangwu1);
					System.out.println("添加成功");
				}
			}

			/*
			 * //自助账务 String zizhuUrl =
			 * "http://210.47.160.36:8080/selfservice/module/webcontent/web/accountself_summary.jsf";
			 * HtmlPage zizhuPage = client.getPage(zizhuUrl);
			 * FileUtils.writeToLocalFile(zizhuPage.asXml(), "zizhu.xml");
			 */
			// 个人信息
			String personUrl = "http://210.47.160.36:8080/selfservice/module/userself/web/regpassuserinfo_login.jsf";
			HtmlPage personPage = client.getPage(personUrl);
			Document doc3 = Jsoup.parse(personPage.asXml());
			MyParser parser3 = new MyParser();
			userInfo userinfo = parser3.parserUserInfoPage(doc3);
			// 所有代码更换
			if (model.checkPersonPageExist(account, userinfo.getUserIPv4())) {// 用户名已存在){
				System.out.println("信息已存在！");

			} else {
				userInfo user = new userInfo();
				user.setAccount(userinfo.getAccount());
				user.setUsername(userinfo.getUsername());
				user.setPassword(userinfo.getPassword());
				user.setZjh(userinfo.getZjh());
				user.setSex(userinfo.getSex());
				user.setUserIPv4(userinfo.getUserIPv4());
				user.setConnway(userinfo.getConnway());
				user.setBalance(userinfo.getBalance());
				user.setStatus(userinfo.getStatus());
				user.setWithholding(userinfo.getWithholding());
				model userInfo = new model();
				userInfo.userInfo(user);
				System.out.println("添加成功");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String account = "179140026";
		String password = "m328131b";
		MyWebClient client = new MyWebClient();
		client.login(account, password);
	}

}
