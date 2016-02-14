package hu.beernotfoundexception.fontastic.bus.event.ui;

import android.support.annotation.Nullable;

import hu.beernotfoundexception.fontastic.bus.event.AbstractEventWithData;

public class ProcessProgressEvent extends AbstractEventWithData<ProcessProgressEvent.ProgressType> {

    public final @Nullable String message;
    public final float process;

    public ProcessProgressEvent(ProgressType data, @Nullable String message, float process) {
        super(data);
        this.message = message;
        this.process = process;
    }

    public enum ProgressType {
        Start,
        ConnectStart,
        ConnectReady,
        Download,
        OverallProgress,

        ScanStart,
        ScanProgress,
        ScanFinish,
        ScanError
    }
}
