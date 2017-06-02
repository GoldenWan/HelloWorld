package cn.utils;

import java.awt.image.BufferedImage;

/**
 * 
 * 
 * @copyright ：神农大学生软件创新中心 版权所有 (c) 2016
 * 
 * @author Mr.Zhang
 * 
 * @version 1.0
 * 
 * @date 2016年3月14日 下午9:50:51
 * 
 * @Description TODO
 *   解析验证码图片工具类
 */
public class VerifyUtils {
	
	private final static int ROW = 12;  	//数字矩阵的行数，即高度
	private final static int COLUMN = 8;  //数字矩阵的列数，即宽度
	/*
	 * 内部类，存储每个数字矩阵横坐标的开始点，和结束点，（纵坐标开始点和结束点都是一样的）
	 */
	private static class NumberRange {
		public NumberRange(int start, int end) {
			this.start = start; 
			this.end = end;
		}

		public int start;  //开始横坐标点
		public int end;		//结束纵坐标点
	}

	/**
	 * 解析验证码图片
	 * @param file    验证码图片输入流
	 * @throws Exception
	 */
	public static String parserVerifyCodeImage(BufferedImage bi) throws Exception {
		StringBuilder sb = new StringBuilder();
		int[] rgb = new int[3];     //rgb颜色值
		
		/*
		 * 一张图面上面，四个数字所|在矩阵的范围（横坐标范围，纵坐标范围）：(6-13,4-15) (19-26,4-15) (32-39,4-15) (45-52,4-15)
		 * 并初始化
		 */
		NumberRange[] ranger = new NumberRange[4];
		ranger[0] = new NumberRange(6, 13);
		ranger[1] = new NumberRange(19, 26);
		ranger[2] = new NumberRange(32, 39);
		ranger[3] = new NumberRange(45, 52);
		
		int offset = 300; // 初始校准值
		byte[][] numberMatrix = new byte[ROW][COLUMN];  //存储数字矩阵分析出的0、1矩阵
		int tempOffset = offset;	//临时校准值，可根据实际情况自动改变校准值，已达到效果最佳
		for (int k = 0; k < ranger.length; k++) {  //一张图片有四个数字
			for (int j = 4; j <= 15; j++) {  
				for (int i = ranger[k].start; i <= ranger[k].end; i++) {
					int pixel = bi.getRGB(i, j); 
					// 下面三行代码将一个数字转换为RGB数字
					rgb[0] = (pixel & 0xff0000) >> 16;
					rgb[1] = (pixel & 0xff00) >> 8;
					rgb[2] = (pixel & 0xff);
					if (rgb[0] + rgb[1] + rgb[2] < tempOffset) {
						numberMatrix[j - 4][i - ranger[k].start] = 1;
					} else {
						numberMatrix[j - 4][i - ranger[k].start] = 0;
					}
				}
			}
			int number = getNumber(numberMatrix);
			if (number == -1) {
				//结果错误，自动改变校准值
				tempOffset += 10;
				k--;
			} else {
				//结果正确，恢复到最初校准值，并输出数字
				tempOffset = offset;
				sb.append(number);
			}
			if (tempOffset > 500) {
				//校准值增加到一定值之后，表示出错，此数字分析失败
				return null;
			}
		}
		return sb.toString();
	}
	
	private static int[] patterns = { 1652, 396, 1080, 954, 1346, 798, 1401, 946, 1328,
			1439 };

	/**
	 * 根据数字矩阵获取对应数字
	 * 
	 * @param numberMatrix
	 *            数字矩阵
	 * @return
	 */
	private static int getNumber(byte[][] numberMatrix) {
		int sum = 0;
		for (int i = 0; i < ROW; i++) {
			short temp = 1;
			short count = 0;
			for (int j = 0; j < COLUMN; j++) {
				count += numberMatrix[i][j] * temp;
				temp *= 2;
			}
			sum += count;
		}
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i] == sum) {
				return i;
			}
		}
		return -1;
	}

}
