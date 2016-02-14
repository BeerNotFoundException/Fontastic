package hu.beernotfoundexception.fontastic.bus.event.ui;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEventWithData;

public class DetectionErrorEvent extends AbstractEventWithData<String> {
    public DetectionErrorEvent(String data) {
        super(data);
    }
}
