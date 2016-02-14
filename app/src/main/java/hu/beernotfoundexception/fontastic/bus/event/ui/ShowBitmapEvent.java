package hu.beernotfoundexception.fontastic.bus.event.ui;

import android.graphics.Bitmap;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEvent;

public class ShowBitmapEvent extends AbstractEvent {

    public final Bitmap bmp;

    public ShowBitmapEvent(Bitmap bmp) {
        this.bmp = bmp;
    }
}
