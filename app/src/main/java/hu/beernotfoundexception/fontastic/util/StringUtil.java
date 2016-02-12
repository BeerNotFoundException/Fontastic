package hu.beernotfoundexception.fontastic.util;

import android.text.Editable;
import android.text.TextWatcher;

public class StringUtil {

    public static TextWatcher getIpTextWatcher() {
        return new TextWatcher() {
            boolean deleting = false;
            int lastCount = 0;

            @Override
            public void afterTextChanged(Editable s) {
                if (!deleting) {
                    String working = s.toString();
                    String[] split = working.split("\\.");
                    String string = split[split.length - 1];
                    if (string.length() == 3 || string.equalsIgnoreCase("0")
                            || (string.length() == 2 && Character.getNumericValue(string.charAt(0)) > 1)) {
                        s.append('.');
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleting = lastCount >= count;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing happens here
            }
        };
    }
}
