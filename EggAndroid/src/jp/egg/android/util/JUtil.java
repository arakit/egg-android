package jp.egg.android.util;

import java.io.*;

public class JUtil {

	public static final boolean isEmpty(String str){
		if(str==null) return true;
		if(str.length()==0) return true;
		return false;
	}

	public static final boolean equals(Object o1, Object o2){
		if(o1 == o2) return true;
		if(o1 == null || o2 == null) return false;
		return o1.equals(o2);
	}


//	public static final String makeRandomAlphabetString(int num){
//
//		String str = RandomStringUtils.randomAlphabetic(num);
//
//		return str;
//	}

    public static final String toStringFromFile(File file){
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(file)) );
            String line;
            while ( (line = reader.readLine())!=null ){
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static final int[] toPrimitiveArray(Integer[] arr){
        int[] ret = new int[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final long[] toPrimitiveArray(Long[] arr){
        long[] ret = new long[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }


    public static final Integer[] toWrappedArray(int[] arr){
        Integer[] ret = new Integer[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final Long[] toWrappedArray(long[] arr){
        Long[] ret = new Long[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final Boolean[] toWrappedArray(boolean[] arr){
        Boolean[] ret = new Boolean[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final Float[] toWrappedArray(float[] arr){
        Float[] ret = new Float[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final Double[] toWrappedArray(double[] arr){
        Double[] ret = new Double[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }
    public static final Character[] toWrappedArray(char[] arr){
        Character[] ret = new Character[arr.length];
        for (int i=0; i<ret.length; i++) ret[i] = arr[i];
        return ret;
    }


}
