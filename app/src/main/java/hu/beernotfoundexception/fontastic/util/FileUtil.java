package hu.beernotfoundexception.fontastic.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileUtil {

    public static final String TAG = FileUtil.class.getSimpleName();

    @Nullable
    public static File saveImage(Bitmap bitmap) {
        File f3 = new File(Environment.getExternalStorageDirectory() + "/inpaint/");
        if (!f3.exists()) {
            f3.mkdirs();
        }
        OutputStream outStream;
        File file = new File(Environment.getExternalStorageDirectory() + "/inpaint/" + "seconds" + ".png");
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream);
            outStream.flush();
            outStream.close();
            return file;
        } catch (Exception e) {
            Logger.e(TAG, "Error while saving", e);
        }
        return null;
    }
}
