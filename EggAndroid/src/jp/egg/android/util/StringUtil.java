package jp.egg.android.util;

public class StringUtil {


    public static String makeDivideString(String[] arr_str, String divide_str) {
        if (arr_str == null) return null;
        if (arr_str.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String e : arr_str) {
            sb.append(e);
            sb.append(divide_str);
        }
        sb.delete(sb.length() - divide_str.length(), sb.length());
        return sb.toString();
    }

    public static final String deleteLastIf(String str, String eq_str) {
        if (str == null) return null;
        if (str.substring(str.length() - eq_str.length(), str.length()).equals(eq_str)) {
            return str.substring(0, str.length() - eq_str.length());
        } else {
            return str;
        }
    }

    public static final String deleteLast(String str, int num) {
        if (str == null) return null;
        return str.substring(0, str.length() - num);
    }


}
