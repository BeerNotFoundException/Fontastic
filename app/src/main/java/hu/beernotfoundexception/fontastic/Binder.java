package hu.beernotfoundexception.fontastic;

import hu.beernotfoundexception.fontastic.domain.control.ControlInterface;
import hu.beernotfoundexception.fontastic.domain.control.FontasticControl;
import hu.beernotfoundexception.fontastic.domain.presenter.LogDisplay;
import hu.beernotfoundexception.fontastic.domain.presenter.Presenter;

public class Binder {

    static volatile ControlInterface controlInterface;

    public static ControlInterface getControlInterface(Presenter presenter) {
        if (controlInterface != null) {
            return controlInterface;
        }

        synchronized (ControlInterface.class) {
            if (controlInterface == null) {
                controlInterface = createControlInterface(presenter);
            }
        }

        return controlInterface;
    }

    private static ControlInterface createControlInterface(Presenter presenter) {
        return new FontasticControl(presenter);
    }

    public static void bindLogger(LogDisplay logDisplay) {
        controlInterface.setLogDisplay(logDisplay);
    }
}
