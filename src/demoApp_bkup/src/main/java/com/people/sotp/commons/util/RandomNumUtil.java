package com.people.sotp.commons.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomNumUtil {

	private BufferedImage image;// 图像
	private String str;// 验证码
	// 验证码序列。
	/*
	 * private static final char[] randomSequence = new char[] { 'A', 'B', 'C',
	 * 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
	 * 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
	 * '5', '6', '7', '8', '9' };
	 */
	private static final char[] randomSequence = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private RandomNumUtil() {
		init();// 初始化属性
	}

	public static RandomNumUtil Instance() {
		return new RandomNumUtil();
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public String getString() {
		return this.str;
	}

	private void init() {
		// 在内存中创建图象
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 创建字体，字体的大小应该根据图片的高度来定。
		Font font = new Font("Times New Roman", Font.PLAIN, height - 2);
		// 设置字体。
		g.setFont(font);

		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		// 取随机产生的认证码(4位数字)
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			int index = random.nextInt(9);
			String rand = String.valueOf(randomSequence[index]);
			sRand = sRand + rand;
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(90), 20 + random.nextInt(100), 20 + random.nextInt(110)));
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 13 * i + 5, 16);
		}
		this.str = sRand;
		// 图象生效
		g.dispose();
		this.image = image;
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
