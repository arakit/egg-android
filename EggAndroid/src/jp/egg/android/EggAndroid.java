package jp.egg.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jp.egg.android.task.EggTaskCentral;

public class EggAndroid {


    //private static boolean sIsEnableDb = false;

//    public static final void initialize(Context context){
//        Context appContext = context.getApplicationContext();
//        //sIsEnableDb = enableDb;
//        EggTaskCentral.initialize(appContext);
////        if (sIsEnableDb) {
////            ActiveAndroid.initialize(appContext);
////        }
//    }

    public static final void initialize(Context context) {
        Context appContext = context.getApplicationContext();
        //sIsEnableDb = aaConfiguration!=null;
        EggTaskCentral.initialize(appContext);
//        if(sIsEnableDb) {
//            ActiveAndroid.initialize(aaConfiguration);
//        }
    }


    public static final void terminate() {
        //if(sIsEnableDb) ActiveAndroid.dispose();
        EggTaskCentral.destroy();
    }


    public static String getAppKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            //Log Log.d("app", "num = "+info.signatures.length);
            for (Signature signature : info.signatures) {
                //Log.d("app", "signature = "+signature.toCharsString());
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                //Log.d("keyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            //Log.d("app", "exception ", e);
        } catch (NoSuchAlgorithmException e) {
            //Log.d("app", "exception", e);
        }
        return null;
    }

}
