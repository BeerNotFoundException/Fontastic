package hu.beernotfoundexception.fontastic.bus.event;

public class AbstractEventWithData<T> extends AbstractEvent {
    public final T data;

    public AbstractEventWithData(T data) {
        this.data = data;
    }
}
