package hu.beernotfoundexception.fontastic.bus.event.ui;

import android.support.annotation.Nullable;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEventWithData;

public class ProcessFinishedEvent extends AbstractEventWithData<ProcessFinishedEvent.FinishMode> {

    public final @Nullable String message;

    public final @Nullable Object payload;

    public ProcessFinishedEvent(FinishMode data, @Nullable String message, @Nullable Object payload) {
        super(data);
        this.message = message;
        this.payload = payload;
    }

    public enum FinishMode {
        Done,
        Interrupted,
        Error
    }
}
