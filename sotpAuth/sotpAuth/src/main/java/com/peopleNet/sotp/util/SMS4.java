package com.peopleNet.sotp.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SMS4 {

	private static final long[] FK = { 0xa3b1bac6, 0x56aa3350, 0x677d9197, 0xb27022dc };

	private static final long[] CK = { 0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269, 0x70777e85, 0x8c939aa1,
	        0xa8afb6bd, 0xc4cbd2d9, 0xe0e7eef5, 0xfc030a11, 0x181f262d, 0x343b4249, 0x50575e65, 0x6c737a81, 0x888f969d,
	        0xa4abb2b9, 0xc0c7ced5, 0xdce3eaf1, 0xf8ff060d, 0x141b2229, 0x30373e45, 0x4c535a61, 0x686f767d, 0x848b9299,
	        0xa0a7aeb5, 0xbcc3cad1, 0xd8dfe6ed, 0xf4fb0209, 0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279 };

	private static final byte[] SBOX = {

	        (byte) 0xd6, (byte) 0x90, (byte) 0xe9, (byte) 0xfe, (byte) 0xcc, (byte) 0xe1, (byte) 0x3d, (byte) 0xb7,
	        (byte) 0x16, (byte) 0xb6, (byte) 0x14, (byte) 0xc2, (byte) 0x28, (byte) 0xfb, (byte) 0x2c, (byte) 0x05,
	        (byte) 0x2b, (byte) 0x67, (byte) 0x9a, (byte) 0x76, (byte) 0x2a, (byte) 0xbe, (byte) 0x04, (byte) 0xc3,
	        (byte) 0xaa, (byte) 0x44, (byte) 0x13, (byte) 0x26, (byte) 0x49, (byte) 0x86, (byte) 0x06, (byte) 0x99,
	        (byte) 0x9c, (byte) 0x42, (byte) 0x50, (byte) 0xf4, (byte) 0x91, (byte) 0xef, (byte) 0x98, (byte) 0x7a,
	        (byte) 0x33, (byte) 0x54, (byte) 0x0b, (byte) 0x43, (byte) 0xed, (byte) 0xcf, (byte) 0xac, (byte) 0x62,
	        (byte) 0xe4, (byte) 0xb3, (byte) 0x1c, (byte) 0xa9, (byte) 0xc9, (byte) 0x08, (byte) 0xe8, (byte) 0x95,
	        (byte) 0x80, (byte) 0xdf, (byte) 0x94, (byte) 0xfa, (byte) 0x75, (byte) 0x8f, (byte) 0x3f, (byte) 0xa6,
	        (byte) 0x47, (byte) 0x07, (byte) 0xa7, (byte) 0xfc, (byte) 0xf3, (byte) 0x73, (byte) 0x17, (byte) 0xba,
	        (byte) 0x83, (byte) 0x59, (byte) 0x3c, (byte) 0x19, (byte) 0xe6, (byte) 0x85, (byte) 0x4f, (byte) 0xa8,
	        (byte) 0x68, (byte) 0x6b, (byte) 0x81, (byte) 0xb2, (byte) 0x71, (byte) 0x64, (byte) 0xda, (byte) 0x8b,
	        (byte) 0xf8, (byte) 0xeb, (byte) 0x0f, (byte) 0x4b, (byte) 0x70, (byte) 0x56, (byte) 0x9d, (byte) 0x35,
	        (byte) 0x1e, (byte) 0x24, (byte) 0x0e, (byte) 0x5e, (byte) 0x63, (byte) 0x58, (byte) 0xd1, (byte) 0xa2,
	        (byte) 0x25, (byte) 0x22, (byte) 0x7c, (byte) 0x3b, (byte) 0x01, (byte) 0x21, (byte) 0x78, (byte) 0x87,
	        (byte) 0xd4, (byte) 0x00, (byte) 0x46, (byte) 0x57, (byte) 0x9f, (byte) 0xd3, (byte) 0x27, (byte) 0x52,
	        (byte) 0x4c, (byte) 0x36, (byte) 0x02, (byte) 0xe7, (byte) 0xa0, (byte) 0xc4, (byte) 0xc8, (byte) 0x9e,
	        (byte) 0xea, (byte) 0xbf, (byte) 0x8a, (byte) 0xd2, (byte) 0x40, (byte) 0xc7, (byte) 0x38, (byte) 0xb5,
	        (byte) 0xa3, (byte) 0xf7, (byte) 0xf2, (byte) 0xce, (byte) 0xf9, (byte) 0x61, (byte) 0x15, (byte) 0xa1,
	        (byte) 0xe0, (byte) 0xae, (byte) 0x5d, (byte) 0xa4, (byte) 0x9b, (byte) 0x34, (byte) 0x1a, (byte) 0x55,
	        (byte) 0xad, (byte) 0x93, (byte) 0x32, (byte) 0x30, (byte) 0xf5, (byte) 0x8c, (byte) 0xb1, (byte) 0xe3,
	        (byte) 0x1d, (byte) 0xf6, (byte) 0xe2, (byte) 0x2e, (byte) 0x82, (byte) 0x66, (byte) 0xca, (byte) 0x60,
	        (byte) 0xc0, (byte) 0x29, (byte) 0x23, (byte) 0xab, (byte) 0x0d, (byte) 0x53, (byte) 0x4e, (byte) 0x6f,
	        (byte) 0xd5, (byte) 0xdb, (byte) 0x37, (byte) 0x45, (byte) 0xde, (byte) 0xfd, (byte) 0x8e, (byte) 0x2f,
	        (byte) 0x03, (byte) 0xff, (byte) 0x6a, (byte) 0x72, (byte) 0x6d, (byte) 0x6c, (byte) 0x5b, (byte) 0x51,
	        (byte) 0x8d, (byte) 0x1b, (byte) 0xaf, (byte) 0x92, (byte) 0xbb, (byte) 0xdd, (byte) 0xbc, (byte) 0x7f,
	        (byte) 0x11, (byte) 0xd9, (byte) 0x5c, (byte) 0x41, (byte) 0x1f, (byte) 0x10, (byte) 0x5a, (byte) 0xd8,
	        (byte) 0x0a, (byte) 0xc1, (byte) 0x31, (byte) 0x88, (byte) 0xa5, (byte) 0xcd, (byte) 0x7b, (byte) 0xbd,
	        (byte) 0x2d, (byte) 0x74, (byte) 0xd0, (byte) 0x12, (byte) 0xb8, (byte) 0xe5, (byte) 0xb4, (byte) 0xb0,
	        (byte) 0x89, (byte) 0x69, (byte) 0x97, (byte) 0x4a, (byte) 0x0c, (byte) 0x96, (byte) 0x77, (byte) 0x7e,
	        (byte) 0x65, (byte) 0xb9, (byte) 0xf1, (byte) 0x09, (byte) 0xc5, (byte) 0x6e, (byte) 0xc6, (byte) 0x84,
	        (byte) 0x18, (byte) 0xf0, (byte) 0x7d, (byte) 0xec, (byte) 0x3a, (byte) 0xdc, (byte) 0x4d, (byte) 0x20,
	        (byte) 0x79, (byte) 0xee, (byte) 0x5f, (byte) 0x3e, (byte) 0xd7, (byte) 0xcb, (byte) 0x39, (byte) 0x48

	};
	// //////////////////////////////////////////////////////////////
	private final int KEY_SIZE = 16;

	public static void main(String[] args) throws UnsupportedEncodingException {

		/*
		 * Random rm = new Random(); double pross = (1 + rm.nextDouble());
		 * String fixLenthString = String.valueOf(pross);
		 * System.out.println(fixLenthString + " " + fixLenthString.substring(1,
		 * 10));
		 *
		 * SMS4 sMS4 = new SMS4(); // String mk =
		 * "0123456789abcdeffedcba98aaaa3220"; // 32字节 String mk =
		 * "FPFvBm0onYpGyPJBOvVY1g==";
		 *
		 * System.out.println("key=" + mk); byte[] bmk =
		 * ByteConvert.toByteArray(mk); System.out.println("bmk=" + bmk.length);
		 *
		 * String plaint = "e FAOmLxshoIn5O3WnuyPg==";
		 *
		 * byte[] text = plaint.getBytes(); System.out.println("ecb加密明文=" +
		 * plaint);
		 */

		// miwen(123456): yZ3jIJeJ/LVNbFXwnuMhwQ==
		// key: tMRauit93Qa7sdHSVtCuYhH/S4WLbEt/A8/IFzdn+xg=
		/*
		 * byte[] bmk = Base64.decode(mk.toCharArray()); byte[] ecb =
		 * Base64.decode(data.toCharArray()); byte[] mbmk = new byte[16];
		 * for(int i = 0; i<=15; i++) mbmk[i] = bmk[i];
		 */

		// byte[] ecbdata = sMS4.encrypt(text, bmk, 0, null);// ECB
		// System.out.println("ecb密文=" + ByteConvert.bytesToHexString(ecbdata));
		// String key = "dqXPSoKPlzbB9KYe2OMTKQ==";
		// byte[] bmk = Base64.decode(key.toCharArray());
		// byte[] mbmk = new byte[16];
		// SMS4 sMS4 = new SMS4();
		// System.arraycopy(bmk, 0, mbmk, 0, 16);
		// String plaint = "RY9FMkNRL2vlAFJPWOuKtw==";
		// byte[] text = Base64.decode(plaint.toCharArray());
		// System.out.println(text.length);
		// byte[] plaintext = sMS4.decrypt(text, bmk, 0, null);
		// System.out.println("ecb解密结果=" + new String(plaintext));

		String key = "dqXPSoKPlzbB9KYe2OMTKQ==";
		byte[] bmk = Base64.decode(key.toCharArray());
		byte[] mbmk = new byte[16];
		System.arraycopy(bmk, 0, mbmk, 0, 16);
		// 加密
		SMS4 sMS4 = new SMS4();
		byte[] text = "11".getBytes("UTF-8");
		byte[] encrypt = sMS4.encrypt(text, mbmk, 0, null);
		char[] encode = Base64.encode(encrypt);
		String base64cipher = new String(encode);
		System.out.println(base64cipher);

	}

	private long leftShift(long x, int y) {

		return (long) ((x << y) | (x >> (32 - y)));
	}

	/* S盒置换函数，8bits输入8bits输出 */
	private byte sbox(byte b) {

		int col = (byte) (b & 0xf);
		int row = (byte) ((b >> 4) & 0xf);

		return SBOX[row * 16 + col];
	}

	/* 非线性变换函数 */
	private long r(long index) {

		byte[] b = ByteConvert.uintToBytes(index);
		byte[] out = new byte[4];
		for (int i = 0; i < 4; i++) {
			out[i] = sbox(b[i]);
		}
		return ByteConvert.bytesToUint(out);
	}

	/* 线性变换函数L (密钥) */
	private long key_trans_L(long b) {

		long out = ((b ^ leftShift(b, 13)) ^ leftShift(b, 23));
		return out;
	}

	/* 合成置换函数T (密钥) */
	private long key_trans_T(long b) {

		return key_trans_L(r(b));
	}

	/* 密钥扩展函数 */
	private int[] genRoundKey(long[] mk, int mode) {

		long k[] = new long[36];
		for (int i = 0; i < 4; i++) {
			k[i] = mk[i] ^ FK[i];
		}

		int rk[] = new int[32];
		for (int i = 0; i < 32; i++) {

			k[i + 4] = k[i] ^ key_trans_T(k[i + 1] ^ k[i + 2] ^ k[i + 3] ^ CK[i]);
			rk[i] = (int) (k[i + 4]);
		}
		if (mode == 1) {// 解密
			int rk2[] = new int[32];
			for (int i = 0; i < 32; i++) {
				rk2[i] = rk[31 - i];
			}
			return rk2;
		}
		return rk;
	}

	/* 线性变换函数L */
	private long trans_L(long b) {

		long out = ((b ^ leftShift(b, 2)) ^ leftShift(b, 10) ^ leftShift(b, 18) ^ leftShift(b, 24));
		return out;
	}

	/* 合成置换函数T */
	private long trans_T(long b) {

		return trans_L(r(b));
	}

	/* 轮函数F */
	private long F(long x0, long x1, long x2, long x3, long rk) {

		return (x0 ^ trans_T(x1 ^ x2 ^ x3 ^ rk));
	}

	/**
	 *
	 * @param input
	 * @param mk
	 * @param flag
	 * @return
	 */
	protected byte[] process(byte[] input, byte[] mk, int flag) {

		long text[] = new long[32];
		long[] lmk = new long[4];
		for (int i = 0; i < 16; i += 4) {
			long txt = ByteConvert.bytesToUint(input, i);
			long key = ByteConvert.bytesToUint(mk, i);
			text[i / 4] = txt;
			lmk[i / 4] = key;
		}
		int rk[] = genRoundKey(lmk, flag);
		long entxt[] = new long[36];
		for (int i = 0; i < 4; i++) {
			entxt[i] = text[i];
		}

		for (int i = 0; i < 32; i++) {
			entxt[i + 4] = F(entxt[i], entxt[i + 1], entxt[i + 2], entxt[i + 3], rk[i]);
		}
		ByteBuffer buffer = ByteBuffer.allocate(16);
		for (int i = 0; i < 4; i++) {
			int n = (int) entxt[35 - i];
			buffer.put(ByteConvert.intToBytes(n));
		}
		return buffer.array();
	}

	private byte[] paddingPlainText(byte[] ptext) {

		int len = ptext.length;
		byte pad = (byte) (KEY_SIZE - len % KEY_SIZE);

		byte[] result = new byte[len + pad];
		System.arraycopy(ptext, 0, result, 0, len);
		Arrays.fill(result, len, result.length, pad);
		return result;
	}

	private int paddingCount(byte[] ptext) throws IllegalArgumentException {

		int len = ptext.length;
		byte padding = ptext[len - 1];
		int count = 0;
		while (count < padding && (ptext[len - count - 1] == padding)) {
			count++;
		}
		if (count != padding) {
			throw new IllegalArgumentException("wrong padding " + count + " " + padding);
		}
		return count;
	}

	/**
	 * SMS4加密接口
	 *
	 * @param input
	 *            明文
	 * @param mk
	 *            密钥 128bits
	 * @param mode
	 *            加密模式 0：ECB；1：CBC
	 * @param iv
	 *            初始向量
	 * @return
	 * @throws IllegalArgumentException
	 */
	public byte[] encrypt(byte[] input, byte[] mk, int mode, byte[] iv) throws IllegalArgumentException {

		if (input == null) {
			throw new IllegalArgumentException("ilegal param input");
		}
		if (mk == null || mk.length != KEY_SIZE)
			throw new IllegalArgumentException("need 128 bits data");

		byte[] buffer = paddingPlainText(input);// 填充
		int len = buffer.length;
		byte[] result = new byte[len];// 返回值
		int i = 0;
		// ECB
		if (mode == 0) {

			while (i < len) {
				// plain text block
				byte[] tmp = Arrays.copyOfRange(buffer, i, i + KEY_SIZE);
				byte out[] = process(tmp, mk, 0);
				System.arraycopy(out, 0, result, i, KEY_SIZE);
				i += KEY_SIZE;
			}
		}
		// CBC
		else if (mode == 1) {
			if (iv == null || iv.length != KEY_SIZE) {
				throw new IllegalArgumentException("ilegal param iv");
			}
			while (i < len) {
				// plain text block
				byte[] pblock = Arrays.copyOfRange(buffer, i, i + KEY_SIZE);
				for (int j = 0; j < 16; j++) {
					pblock[j] = (byte) (pblock[j] ^ iv[j]);
				}
				iv = process(pblock, mk, 0);
				System.arraycopy(iv, 0, result, i, KEY_SIZE);
				i += 16;
			}
		} else {
			throw new IllegalArgumentException("ilegal param mode");
		}

		return result;
	}

	/**
	 * 解密接口 密钥长度要求128 bits
	 *
	 * @param input
	 *            密文
	 * @param mk
	 *            密钥
	 * @param mode
	 *            0 ECB 1 CBC
	 * @param iv
	 *            初始向量
	 * @return 明文
	 * @throws IllegalArgumentException
	 */
	public byte[] decrypt(byte[] input, byte[] mk, int mode, byte[] iv) throws IllegalArgumentException {

		if (mk == null || mk.length != KEY_SIZE)
			throw new IllegalArgumentException("need 128 bits data");
		if (input == null || input.length == 0 || input.length % 16 != 0) {
			throw new IllegalArgumentException("ilegal param input");
		}

		int len = input.length;
		byte[] result = new byte[len];// 返回值
		int i = 0;
		// ECB
		if (mode == 0) {

			while (i < len) {
				// plain text block
				byte[] tmp = Arrays.copyOfRange(input, i, i + KEY_SIZE);
				byte out[] = process(tmp, mk, 1);
				System.arraycopy(out, 0, result, i, KEY_SIZE);
				i += KEY_SIZE;
			}
		} else if (mode == 1) {
			if (iv == null || iv.length != KEY_SIZE) {
				throw new IllegalArgumentException("ilegal param iv");
			}
			while (i < len) {
				// cipher text block
				byte[] cblock = Arrays.copyOfRange(input, i, i + KEY_SIZE);
				byte pblock[] = process(cblock, mk, 1);// decrypt
				for (int j = 0; j < 16; j++) {
					pblock[j] = (byte) (pblock[j] ^ iv[j]);
				}
				System.arraycopy(pblock, 0, result, i, KEY_SIZE);
				iv = cblock;
				i += KEY_SIZE;
			}
		}
		int paddingCount = paddingCount(result);
		byte tmp[] = new byte[result.length - paddingCount];
		System.arraycopy(result, 0, tmp, 0, tmp.length);
		return tmp;
	}

}