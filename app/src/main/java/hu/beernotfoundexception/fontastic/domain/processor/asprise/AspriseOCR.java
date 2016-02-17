package hu.beernotfoundexception.fontastic.domain.processor.asprise;

import android.graphics.Bitmap;

import com.asprise.ocr.Ocr;

import java.io.File;

import hu.beernotfoundexception.fontastic.util.FileUtil;

public class AspriseOCR {

    Ocr oneEngine;

    public AspriseOCR() {
        Ocr.setUp();
        oneEngine = new Ocr();
    }

    public void start(Bitmap bmp){
        File[] files = {FileUtil.saveImage(bmp)};
        oneEngine.recognize(files, Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_XML);
    }
}
