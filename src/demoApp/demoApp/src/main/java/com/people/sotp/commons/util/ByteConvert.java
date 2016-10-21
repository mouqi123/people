package com.people.sotp.commons.util;

public class ByteConvert {

    /**
     * 将16进制串转换成字节数组
     * 
     * @param hexString
     *         16进制字符串
     * @return 字节数组
     */
    public static byte[] toByteArray(String hexString) {

        if (hexString == null || hexString.length() == 0) {
            return new byte[0];
        }
        if (hexString.length() % 2 != 0) {
            return new byte[0];
        }
        hexString = hexString.replace(" ", "");
        int length = hexString.length() / 2;
        byte[] byteArray = new byte[length];
        for (int i = 0; i < length; i++) {
            byte high = (byte) Character.digit(hexString.charAt(2 * i), 16);
            byte low = (byte) Character.digit(hexString.charAt(2 * i + 1), 16);
            byteArray[i] = (byte) (high << 4 | low);
        }
        return byteArray;
    }

    /**
     * 将16进制串转换成字节数组
     * 
     * @param hexString
     *         16进制字符串
     * @return 字节数组
     */
    public static String bytesToHexString(byte[] bArray) {

        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString((int) bArray[i] & 0xff);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
        }
        return sb.toString().toUpperCase();
    }

    // 以下 是整型数 和网络字节序的 byte[] 数组之间的转换
    public static byte[] longToBytes(long n) {

        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    public static void longToBytes(long n, byte[] array, int offset) {

        array[7 + offset] = (byte) (n & 0xff);
        array[6 + offset] = (byte) (n >> 8 & 0xff);
        array[5 + offset] = (byte) (n >> 16 & 0xff);
        array[4 + offset] = (byte) (n >> 24 & 0xff);
        array[3 + offset] = (byte) (n >> 32 & 0xff);
        array[2 + offset] = (byte) (n >> 40 & 0xff);
        array[1 + offset] = (byte) (n >> 48 & 0xff);
        array[0 + offset] = (byte) (n >> 56 & 0xff);
    }

    public static long bytesToLong(byte[] array) {

        return ((((long) array[0] & 0xff) << 56)
                | (((long) array[1] & 0xff) << 48)
                | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32)
                | (((long) array[4] & 0xff) << 24)
                | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
    }

    public static long bytesToLong(byte[] array, int offset) {

        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8) | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static byte[] intToBytes(int n) {

        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static void intToBytes(int n, byte[] array, int offset) {

        array[3 + offset] = (byte) (n & 0xff);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static int bytesToInt(byte b[]) {

        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

    public static int bytesToInt(byte b[], int offset) {

        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8
                | (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
    }

    public static byte[] uintToBytes(long n) {

        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    public static void uintToBytes(long n, byte[] array, int offset) {

        array[3 + offset] = (byte) (n);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static long bytesToUint(byte[] array) {

        return ((long) (array[3] & 0xff)) | ((long) (array[2] & 0xff)) << 8
                | ((long) (array[1] & 0xff)) << 16
                | ((long) (array[0] & 0xff)) << 24;
    }

    public static long bytesToUint(byte[] array, int offset) {

        return ((long) (array[offset + 3] & 0xff))
                | ((long) (array[offset + 2] & 0xff)) << 8
                | ((long) (array[offset + 1] & 0xff)) << 16
                | ((long) (array[offset] & 0xff)) << 24;
    }

    public static byte[] shortToBytes(short n) {

        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void shortToBytes(short n, byte[] array, int offset) {

        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static short bytesToShort(byte[] b) {

        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

    public static short bytesToShort(byte[] b, int offset) {

        return (short) (b[offset + 1] & 0xff | (b[offset] & 0xff) << 8);
    }

    public static byte[] ushortToBytes(int n) {

        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void ushortToBytes(int n, byte[] array, int offset) {

        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {

        return b[1] & 0xff | (b[0] & 0xff) << 8;
    }

    public static int bytesToUshort(byte b[], int offset) {

        return b[offset + 1] & 0xff | (b[offset] & 0xff) << 8;
    }

    public static byte[] ubyteToBytes(int n) {

        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes(int n, byte[] array, int offset) {

        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte(byte[] array) {

        return array[0] & 0xff;
    }

    public static int bytesToUbyte(byte[] array, int offset) {

        return array[offset] & 0xff;
    }
    // char 类型、 float、double 类型和 byte[] 数组之间的转换关系还需继续研究实现。
}
