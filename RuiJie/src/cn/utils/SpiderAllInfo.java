package cn.utils;

import java.util.List;
import java.util.TimerTask;

import cn.domain.User;
import cn.service.Userservice;
import cn.service.model;
import cn.servlet.Simulogin;

public class SpiderAllInfo {

	public static void test() throws Exception {
		model userService = new model();
		List<User> users = userService.ShowAllusers();

		if (users.isEmpty()) {
			System.out.println("当前用户信息为空！");
			return;
		} else {
			long start = System.currentTimeMillis(); // 开始执行时间
			for (int i = 0; i < users.size(); i++) {// 抓取所有用户信息
				User user = users.get(i);
				String account = user.getAccount();
				String password = user.getPassword();
				Simulogin Simu = new Simulogin();
				if (account != null && password != null) {
					try {
						String key = "3hxcgsrt3sxhs4zg";// 秘钥，获得发送推送消息的权限
						DES des = new DES();
						des.authcode(password, "DECODE", key);
						if (Simu.login(account, password)) {// 判断密码是否正确
							MyWebClient client = new MyWebClient();
							client.login(account, password);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.gc();//运行垃圾回收器
			}
			long end = System.currentTimeMillis(); // 结束时间
			// System.out.println("time:"+(end-start)/1000); //总共使用时间，
			Userservice userservice = new Userservice();
			userservice.addSpiderInfoTime((end - start) / 1000);
		}

	}

	public static void main(String[] args) throws Exception {
		test();
	}
}
