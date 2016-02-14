package hu.beernotfoundexception.fontastic.domain.processor.tessprocessor;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.MissingResourceException;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR() {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        String language = "eng";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            throw new MissingResourceException(
                    "No tesseract training data folder found",
                    TessOCR.class.getName(),
                    "trainingData");
        mTess.init(datapath, language);
    }

    public void setImage(Bitmap bitmap) {
        mTess.setImage(bitmap);
    }

    public String getText() {
        return mTess.getUTF8Text();
    }

    public String getFont() {
        return null;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
