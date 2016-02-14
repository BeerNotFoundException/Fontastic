package hu.beernotfoundexception.fontastic.bus;

import android.content.Context;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.TinyBus;
import hu.beernotfoundexception.fontastic.bus.event.AbstractEvent;

public class Broadcast {

    private static Bus uiBus;

    public static void init(Context uiContext) {
        uiBus = TinyBus.from(uiContext);
    }

    public static void registerUi(Object obj) {
        if (!uiBus.hasRegistered(obj))
            uiBus.register(obj);
    }

    public static void unregisterUi(Object obj) {
        if (uiBus.hasRegistered(obj)) {
            uiBus.unregister(obj);
        }
    }

    public static void postUi(AbstractEvent event) {
        uiBus.post(event);
    }
}
