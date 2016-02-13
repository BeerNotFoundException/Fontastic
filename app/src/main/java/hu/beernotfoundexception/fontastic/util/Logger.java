package hu.beernotfoundexception.fontastic.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import hu.beernotfoundexception.fontastic.domain.presenter.LogDisplay;

public class Logger {

    private static LogDisplay logDisplay;

    private static int logLevel = 2;

    public static void setLogDisplay(LogDisplay logDisplay) {
        Logger.logDisplay = logDisplay;
    }

    public static void setLogLevel(int logLevel) {
        Logger.logLevel = logLevel;
    }

    public static void i(String tag, String message, int level) {
        if (level > 1 && logDisplay != null) {
            logDisplay.logMessage(tag + " - " + message);
        }
        if (level > 0)
            Log.i(tag, message);
    }

    public static void i(String tag, String message) {
        i(tag, message, logLevel);
    }

    public static void d(String tag, String message) {
        i(tag + "_D", message);
    }

    public static void e(String tag, String message) {
        i(tag + "_E", message);
    }

    public static void e(final String tag, final String message, final Throwable e) {
        if (logLevel > 1 && logDisplay != null) {
                    logDisplay.logMessage("EXCEPTION");
                    logDisplay.logMessage(tag + " - " + message);
                    logDisplay.logMessage(e.getMessage());
        }
        Log.e(tag, message);
        Crashlytics.logException(e);
    }
}
