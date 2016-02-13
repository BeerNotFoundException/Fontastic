package hu.beernotfoundexception.fontastic;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import hu.beernotfoundexception.fontastic.util.TextUtil;

public class MainActivity extends AppCompatActivity implements MainInterface {

    private static final int MODE_TEST = 758;
    private static final int MODE_USE = 772;
    protected EditText txtIpEdit;
    protected FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "A camera should open here.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txtIpEdit = (EditText) findViewById(R.id.txtIp);

        txtIpEdit.setFilters(new InputFilter[]{TextUtil.getIpInputFilter()});

        txtIpEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setFabMode(TextUtil.isValidIp(s.toString()) ?
                        MODE_TEST : MODE_USE);
            }
        });
    }

    private void setFabMode(@UseMode int useMode) {

        switch (useMode) {
            case MODE_TEST:
                fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                break;
            case MODE_USE:
                fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                break;
        }
    }

    @Override
    public void onLogMessage(String msg) {

    }

    @Override
    public void onDetectionResult(String fontName) {

    }

    @Override
    public void onDetectionError(String reason) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onNoConnection() {

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_TEST, MODE_USE})
    public @interface UseMode {
    }
}
