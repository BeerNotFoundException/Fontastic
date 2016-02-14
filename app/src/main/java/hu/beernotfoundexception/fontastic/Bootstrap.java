package hu.beernotfoundexception.fontastic;

import hu.beernotfoundexception.fontastic.domain.control.ControlInterface;
import hu.beernotfoundexception.fontastic.domain.control.ControlOnBgControl;
import hu.beernotfoundexception.fontastic.domain.processor.ImageProcessor;
import hu.beernotfoundexception.fontastic.domain.processor.ImageProcessorStub;

public class Bootstrap {

    static ControlInterface controlInterface;
    static ImageProcessor imageProcessor;

    public static ControlInterface getControlInterface() {
        if (controlInterface == null) {
            synchronized (ControlInterface.class) {
                if (controlInterface == null) {
                    imageProcessor = new ImageProcessorStub(); // TODO: 2/14/2016 replace with real proc
                    controlInterface = new ControlOnBgControl(imageProcessor);
                }
            }
        }
        return controlInterface;
    }
}
