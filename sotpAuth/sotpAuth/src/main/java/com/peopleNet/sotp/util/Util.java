package com.peopleNet.sotp.util;


import java.math.BigInteger;
import java.util.Locale;

public class Util {

    public static byte[] asUnsigned32ByteArray(BigInteger n) {
        return asUnsignedNByteArray(n, 32);
    }

    public static byte[] asUnsignedNByteArray(BigInteger x, int length) {
        if (x == null) {
            return null;
        }

        byte[] tmp = new byte[length];
        int len = x.toByteArray().length;
        if (len > length + 1) {
            return null;
        }

        if (len == length + 1) {
            if (x.toByteArray()[0] != 0) {
                return null;
            }
            else {
                System.arraycopy(x.toByteArray(), 1, tmp, 0, length);
                return tmp;
            }
        }
        else {
            System.arraycopy(x.toByteArray(), 0, tmp, length - len, len);
            return tmp;
        }
    }

    public static int bigEndianByteToInt(byte[] bytes) {
        return Util.byteToInt(back(bytes));
    }

    public static byte[] bigEndianIntToByte(int num) {
        return back(Util.intToByte(num));
    }

    /**
     * 字节数组逆序
     * 
     * @param in
     * @return
     */
    public static byte[] back(byte[] in) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = in[out.length - i - 1];
        }

        return out;
    }

    /**
     * 左移位
     * @param in
     * @param byteLen 要移动的位数
     * @return
     */
    public static byte[] byteCycleLeft(byte[] in, int byteLen) {
        byte[] tmp = new byte[in.length];
        System.arraycopy(in, byteLen, tmp, 0, in.length - byteLen);
        System.arraycopy(in, 0, tmp, in.length - byteLen, byteLen);

        return tmp;
    }

    public static int bitCycleLeft(int n, int bitLen) {
        bitLen %= 32;
        byte[] tmp = Util.bigEndianIntToByte(n);
        int byteLen = bitLen / 8;
        int len = bitLen % 8;
        if (byteLen > 0) {
            tmp = Util.byteCycleLeft(tmp, byteLen);
        }

        if (len > 0) {
            tmp = bitSmall8CycleLeft(tmp, len);
        }

        return Util.bigEndianByteToInt(tmp);
    }

    public static byte[] bitSmall8CycleLeft(byte[] in, int len) {
        byte[] tmp = new byte[in.length];
        int t1, t2, t3;
        for (int i = 0; i < tmp.length; i++) {
            t1 = (byte) ((in[i] & 0x000000ff) << len);
            t2 = (byte) ((in[(i + 1) % tmp.length] & 0x000000ff) >> (8 - len));
            t3 = (byte) (t1 | t2);
            tmp[i] = (byte) t3;
        }

        return tmp;
    }

    /**
     * 四个字节的字节数据转换成一个整形数据
     * @param bytes 4个字节的字节数组
     * @return 一个整型数据
     */
    public static int byteToInt(byte[] bytes) {
        int num = 0;
        int temp;
        temp = (0x000000ff & (bytes[0])) << 0;
        num = num | temp;
        temp = (0x000000ff & (bytes[1])) << 8;
        num = num | temp;
        temp = (0x000000ff & (bytes[2])) << 16;
        num = num | temp;
        temp = (0x000000ff & (bytes[3])) << 24;
        num = num | temp;

        return num;
    }

    /**
     * 整形转换成网络传输的字节流（字节数组）型数据
     * @param num 一个整型数据
     * @return 4个字节的自己数组
     */
    public static byte[] intToByte(int num) {
        byte[] bytes = new byte[4];
        // System.out.println((num));
        bytes[0] = (byte) (0xff & (num >> 0));
        // System.out.println(Integer.toHexString(bytes[0] & 0xFF));
        bytes[1] = (byte) (0xff & (num >> 8));
        // System.out.println(Integer.toHexString(bytes[1] & 0xFF));
        bytes[2] = (byte) (0xff & (num >> 16));
        // System.out.println(Integer.toHexString(bytes[2] & 0xFF));
        bytes[3] = (byte) (0xff & (num >> 24));
        // System.out.println(Integer.toHexString(bytes[3] & 0xFF));

        return bytes;
    }

    public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static byte[] longToByte(long num) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (0xff & (num >> (i * 8)));
        }

        return bytes;
    }

    /**
     * 转换long型为byte数组
     * 
     * @param bb
     * @param x
     * @param index
     */
    public static byte[] longToByte2(long x) {
        byte[] bb = new byte[8];
        bb[7] = (byte) (x >> 56);
        bb[6] = (byte) (x >> 48);
        bb[5] = (byte) (x >> 40);
        bb[4] = (byte) (x >> 32);
        bb[3] = (byte) (x >> 24);
        bb[2] = (byte) (x >> 16);
        bb[1] = (byte) (x >> 8);
        bb[0] = (byte) (x >> 0);
        return bb;
    }

    public static String toHexString(byte[] data) {
        byte temp;
        int n;
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i <= data.length; i++) {
            temp = data[i - 1];
            n = (int) ((temp & 0xf0) >> 4);
            buf.append(intToHex(n));
            n = (int) ((temp & 0x0f));
            buf.append(intToHex(n));
        }

        return buf.toString();
    }

    // public static byte[] toHex(String data) {
    // byte b;
    // byte[] reByte=new byte[data.length()/2] ;
    // for (int i = 1; i < data.length(); i++) {
    // int b = Byte.parseByte(data.substring(i-1, i), 16);
    // ++i;
    // int a = Integer.parseInt(data.substring(i-1, i), 16);
    // System.arraycopy(a.getBytes(),0 , reByte, i-1, a.getBytes().length);
    // }
    // return reByte;
    // }

    public static byte[] toHex(String data) {
        final byte[] byteArray = new byte[data.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(data.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(data.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    public static void printWithHex(byte[] data) {
        System.out.println(toHexString(data));
    }

    public static String intToHex(int n) {
        if (n > 15 || n < 0) {
            return "";
        }
        else if ((n >= 0) && (n <= 9)) {
            return "" + n;
        }
        else {
            switch (n) {
            case 10: {
                return "A";
            }
            case 11: {
                return "B";
            }
            case 12: {
                return "C";
            }
            case 13: {
                return "D";
            }
            case 14: {
                return "E";
            }
            case 15: {
                return "F";
            }
            default:
                return "";
            }
        }
    }

    public static byte[] convert(int[] arr) {
        byte[] out = new byte[arr.length * 4];
        byte[] tmp = null;
        for (int i = 0; i < arr.length; i++) {
            tmp = Util.bigEndianIntToByte(arr[i]);
            System.arraycopy(tmp, 0, out, i * 4, 4);
        }

        return out;
    }

    /**
     * 转为ASC码形式的int[]
     * @param arr
     * @return
     */
    public static int[] convert(byte[] arr) {
        int[] out = new int[arr.length / 4];
        byte[] tmp = new byte[4];
        for (int i = 0; i < arr.length; i += 4) {
            System.arraycopy(arr, i, tmp, 0, 4);
            out[i / 4] = Util.bigEndianByteToInt(tmp);
        }

        return out;
    }

    public static int[] convert(byte mbyte) {
        int[] out = new int[4];
        byte[] mm = new byte[mbyte];
        byte[] tmp = new byte[4];
        for (int j = 0; j < 4; j++) {
            tmp[j] = mm[j];
        }
        out[0] = Util.bigEndianByteToInt(tmp);
        return out;
    }

    /**
     * 打印函数
     * @param arr
     */
    public static void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(Integer.toHexString(arr[i]) + " ");
            if ((i + 1) % 16 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static void print(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            /*
             * System.out.print(PrintUtil.toHexString(back(ConvertUtil.IntToByte(arr[i]))) + "
             * "); if((i+1) % 8 == 0) { System.out.println(); }
             */
            System.out.print(Integer.toHexString(arr[i]) + " ");
            if ((i + 1) % 16 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static String bytes2HexString(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                buf.append('0');
                buf.append(hex);
            }
            buf.append(hex.toUpperCase(Locale.US));
        }
        return buf.toString();
    }

    public static byte[] putDouble(double x) {
        // byte[] b = new byte[8];
        byte[] bb = new byte[4];
        long l = Double.doubleToLongBits(x);
        System.out.println("long is ==" + l);
        for (int i = 0; i < 4; i++) {
            bb[i] = Long.valueOf(l).byteValue();
            l = l >> 8;
        }
        return bb;
    }

    public static float toFloat(byte[] b) {
        // 4 bytes
        int accum = 0;
        for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
            accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
        }
        return Float.intBitsToFloat(accum);
    }

}