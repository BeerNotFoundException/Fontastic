package hu.beernotfoundexception.fontastic.bus.event.ui;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEventWithData;
import hu.beernotfoundexception.fontastic.data.ConsoleLine;

public class ConsoleLogEvent extends AbstractEventWithData<ConsoleLine> {
    public ConsoleLogEvent(ConsoleLine data) {
        super(data);
    }
}
