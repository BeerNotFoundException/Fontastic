package hu.beernotfoundexception.fontastic.bus.event.ui;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEventWithData;

public class DetectionResultEvent extends AbstractEventWithData<String> {
    public DetectionResultEvent(String data) {
        super(data);
    }
}
