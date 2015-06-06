package com.gaoyuan4122.download.util;

public class MD5Util {
    /**
     * 获取字符串的md5值，保证唯一性
     *
     * @param str
     * @return
     */
    public static String getMD5Pass(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;

            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
