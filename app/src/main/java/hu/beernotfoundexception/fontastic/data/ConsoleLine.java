package hu.beernotfoundexception.fontastic.data;

import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ConsoleLine {
    public static final int TYPE_STRING = 753;
    public static final int TYPE_IMAGE = 408;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_STRING, TYPE_IMAGE})
    public @interface LineType{}

    public final @ConsoleLine.LineType int type;

    public final Object data;

    public ConsoleLine(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    @Nullable
    public Class getDataClass() {
        switch (type) {
            case TYPE_STRING:
                return String.class;
            case TYPE_IMAGE:
                return Bitmap.class;
            default:
                return null;
        }
    }
}
