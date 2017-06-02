package cn.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;


/**
 * 
 * @copyright ：神农大学生软件创新中心 版权所有 (c) 2016
 * 
 * 
 * 
 * 
 * 
 * date: 2016年12月2日 下午3:27:07 
 * @author 13信息_晚进军
 * @version 
 * @since JDK 1.7.0_51
 * @Description TODO
 *		读取本地文件进行解析
 */
public class Main {
	
	final static int ROW = 12;  	//数字矩阵的行数，即高度
	final static int COLUMN = 8;  //数字矩阵的列数，即宽度
	
	/*
	 * 内部类，存储每个数字矩阵横坐标的开始点，和结束点，（纵坐标开始点和结束点都是一样的）
	 */
	static class NumberRange {
		public NumberRange(int start, int end) {
			this.start = start; 
			this.end = end;
		}

		public int start;  //开始横坐标点
		public int end;		//结束纵坐标点
	}
	
	public static void main(String[] args) throws Exception {
		File file = getFile("12.jpg");   //从本地获取图片，都保存在doc目录下，可以改文件名
		if (file.exists()) {   
			//如果图片文在存在，则进行解析
			parserVerifyCodeImage(file);
		} else {
			System.out.println("The file is not exist");
		}
	}

	private static File getFile(String fileName){
		File file = new File("doc/" + fileName);
		return file;
	}

	/**
	 * 解析验证码图片
	 * @param file    验证码图片文件对象
	 * @throws Exception
	 */
	public static void parserVerifyCodeImage(File file) 
			throws Exception {
		
		int[] rgb = new int[3];     //rgb颜色值
		BufferedImage bi = null;    //图片流
		try {
			bi = ImageIO.read(file);  //读取文件，生成对应的输入流
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		for (int k = 0; k < ranger.length; k++) {
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
				System.out.print(number + " ");
			}
			if (tempOffset > 500) {
				//校准值增加到一定值之后，表示出错，此数字分析失败
				System.out.print("error");
				return;
			}
		}
		
	}

	
	// static short[][] patterns = {
	// { 56, 108, 68, 198, 198, 198, 198, 198, 198, 68, 108, 56 }, // 0
	// { 24, 30, 24, 24, 24, 24, 24, 24, 24, 24, 24, 126 }, // 1
	// { 60, 114, 97, 96, 96, 32, 48, 16, 8, 132, 254, 127 }, // 2
	// { 60, 115, 97, 96, 48, 56, 112, 96, 96, 96, 51, 31 }, // 3
	// { 96, 96, 112, 104, 100, 100, 98, 97, 255, 96, 96, 96 }, // 4
	// { 120, 120, 4, 28, 62, 112, 96, 64, 64, 64, 34, 30 }, // 5
	// { 224, 56, 12, 6, 58, 103, 195, 195, 195, 195, 102, 60 }, // 6
	// { 252, 252, 130, 64, 64, 64, 32, 32, 16, 16, 16, 8 }, // 7
	// { 60, 70, 195, 195, 102, 28, 60, 98, 195, 195, 70, 60 }, // 8
	// { 60, 102, 195, 195, 195, 195, 198, 124, 96, 48, 24, 7 }, };// 9
	/*
	 * 比较标准，每个值代表一个数字。
	 */
	static int[] patterns = { 1652, 396, 1080, 954, 1346, 798, 1401, 946, 1328, 1439 };
	/**
	 * 根据数字矩阵获取对应数字
	 * 
	 * @param numberMatrix
	 *            数字矩阵
	 * @return
	 */
	public static int getNumber(byte[][] numberMatrix) {
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
