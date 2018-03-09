package jp.egg.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jp.egg.android.task.EggTaskCentral;

public final class EggAndroid {

    public static final void initialize(Context context, @Nullable EggTaskCentral.Options options) {
        if (options == null) {
            options = new EggTaskCentral.Options();
        }
        Context appContext = context.getApplicationContext();
        EggTaskCentral.initialize(appContext, options);
    }

    public static final void terminate() {
        EggTaskCentral.destroy();
    }

    public static String getAppKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
