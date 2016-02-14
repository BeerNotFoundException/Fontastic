package hu.beernotfoundexception.fontastic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hu.beernotfoundexception.fontastic.domain.control.ControlInterface;

public class ControlDisplayActivity extends AppCompatActivity {

    private ControlInterface controlInterface;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        controlInterface = Bootstrap.getControlInterface();
    }

    public ControlInterface getControlInterface() {
        return controlInterface;
    }
}
