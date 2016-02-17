package hu.beernotfoundexception.fontastic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import de.halfbit.tinybus.Subscribe;
import hu.beernotfoundexception.fontastic.bus.Broadcast;
import hu.beernotfoundexception.fontastic.bus.event.ui.ConsoleLogEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.DetectionErrorEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.DetectionResultEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.ProcessFinishedEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.ShowBitmapEvent;
import hu.beernotfoundexception.fontastic.data.ConsoleLine;
import hu.beernotfoundexception.fontastic.util.TextUtil;
import hu.beernotfoundexception.fontastic.view.ConsoleRecyclerAdapter;
import io.fabric.sdk.android.Fabric;

import static de.halfbit.tinybus.Subscribe.Mode;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_CAMERA;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_DONE;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_FAIL;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_INTERRUPTED;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_NONE;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_RUNNING;
import static hu.beernotfoundexception.fontastic.MainActivity.UseMode.MODE_TEST;
import static hu.beernotfoundexception.fontastic.data.ConsoleLine.TYPE_IMAGE;
import static hu.beernotfoundexception.fontastic.data.ConsoleLine.TYPE_STRING;

public class MainActivity extends ControlDisplayActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 236;
    @Bind(R.id.txtIp)
    protected MaterialEditText txtIpEdit;
    @Bind(R.id.fab)
    protected FloatingActionButton fab;
    @Bind(R.id.fabProgressCircle)
    protected FABProgressCircle fabProgressCircle;
    @Bind(R.id.recConsole)
    protected RecyclerView console;
    @UseMode
    int currentUseMode = MODE_CAMERA;
    private ConsoleRecyclerAdapter logAdapter;

    @OnClick(R.id.btnClear)
    protected void onClearClick() {
        logAdapter.clear();
    }

    @OnClick(R.id.fab)
    protected void onFabClick() {
        hideKeyboard();
        switch (currentUseMode) {

            case MODE_CAMERA:
                openCamera();
                break;
            case MODE_TEST:
                getControlInterface().startTest(txtIpEdit.getText().toString());
                break;
            case MODE_RUNNING:
                Snackbar.make(fab, "Do you want to stop the current process?",
                        Snackbar.LENGTH_INDEFINITE).setAction("Yes",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getControlInterface().cancelPending();
                            }
                        }).show();
                break;
        }
    }

    @OnLongClick(R.id.fab)
    protected boolean onFabLongClick() {
        hideKeyboard();
        switch (currentUseMode) {
            case MODE_RUNNING:
                Snackbar.make(fab, "Do you want to stop the current process?",
                        Snackbar.LENGTH_INDEFINITE).setAction("Yes",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getControlInterface().cancelPending();
                            }
                        }).show();
                break;
            default:
                openCamera();
        }
        return true;
    }

    @OnClick(R.id.relContainer)
    protected void hideKeyboard() {
        View focus = this.getCurrentFocus();
        if (focus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    @OnTextChanged(R.id.txtIp)
    protected void onTxtIpChanged() {
        if (currentUseMode != MODE_RUNNING)
            setUseMode(TextUtil.isValidIp(txtIpEdit.getText().toString()) ?
                    MODE_TEST : MODE_CAMERA);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        Broadcast.init(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logAdapter = new ConsoleRecyclerAdapter();

        ButterKnife.bind(this);

        fabProgressCircle.attachListener(new FABProgressListener() {
            @Override
            public void onFABProgressAnimationEnd() {
                setUseMode(TextUtil.isValidIp(txtIpEdit.getText().toString()) ?
                        MODE_TEST : MODE_CAMERA);
            }
        });

        txtIpEdit.setFilters(new InputFilter[]{TextUtil.getIpInputFilter()});

        console.setAdapter(logAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            logImage(imageBitmap);
            getControlInterface().scanBitmap(imageBitmap);
        }
    }

    private void setIsLoading(final @Nullable Boolean isLoading) {
        if (isLoading == null) {
            fabProgressCircle.hide();
        } else if (isLoading) {
            fabProgressCircle.show();
        } else {
            fabProgressCircle.beginFinalAnimation();
        }
    }

    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                startCameraIntent();
            }
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Snackbar.make(fab, "The camera is not available.", Snackbar.LENGTH_SHORT);
        }
    }

    @Subscribe(mode = Mode.Main)
    public void onShowBitmapEvent(ShowBitmapEvent event) {
        logImage(event.bmp);
    }

    private void logMessage(final String msg1, final String msg2) {
        logMessage(msg1 + (msg2 != null ?
                "\nMessage: " + msg2 : ""));
    }

    private void logMessage(final String msg) {
        logAdapter.log(msg);
        console.smoothScrollToPosition(0);
    }

    @Subscribe(mode = Mode.Main)
    public void onConsoleLogEvent(ConsoleLogEvent event) {
        ConsoleLine line = event.data;
        switch (line.type) {
            case TYPE_IMAGE:
                logImage((Bitmap) line.data);
                break;
            case TYPE_STRING:
                logMessage((String) line.data);
        }
    }

    @Subscribe(mode = Mode.Main)
    public void onDetectionResult(DetectionResultEvent event) {
        logMessage("Font detected: " + event.data);
    }

    @Subscribe(mode = Mode.Main)
    public void onDetectionError(DetectionErrorEvent event) {
        logMessage("Detection error occured. Reason: " + event.data);
        setUseMode(MODE_FAIL);
    }

    @Subscribe(mode = Mode.Main)
    public void onProcessProgress(ProcessProgressEvent event) {
        switch (event.data) {

            case Start:
                setUseMode(MODE_RUNNING);
                logMessage("Process started.", event.message);
                break;
            case ConnectStart:
                logMessage("Opening connection...", event.message);
                break;
            case ConnectReady:
                logMessage("Connection ready.", event.message);
                break;
            case Download:
                logMessage("Downloading...", event.message);
                break;
            case OverallProgress:
                logMessage("Progress report...", event.message);
                break;
            case ScanStart:
                logMessage("Scanning picture...", event.message);
                break;
            case ScanProgress:
                logMessage("Scan progress: " + event.process + "%");
                break;
            case ScanFinish:
                logMessage("Scanned. Font: " + event.message);
                break;
            case ScanError:
                logMessage("Scanning failed.", event.message);
                break;

        }
    }

    @Subscribe(mode = Mode.Main)
    public void onProcessFinished(ProcessFinishedEvent event) {
        switch (event.data) {

            case Done:
                setUseMode(MODE_DONE);
                logMessage("Process done.", event.message);
                logMessage("Font: " + event.payload);
                break;
            case Interrupted:
                setUseMode(MODE_INTERRUPTED);
                logMessage("Process interrupted.", event.message);
                break;
            case Error:
                setUseMode(MODE_FAIL);
                logMessage("Process failed.", event.message);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Broadcast.registerUi(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Broadcast.unregisterUi(this);
    }

    private void logImage(Bitmap bitmap) {
        logAdapter.log(bitmap);
    }

    private void setUseMode(@UseMode int useMode) {
        if (useMode != this.currentUseMode || this.currentUseMode == MODE_NONE) {
            this.currentUseMode = useMode;
            switch (useMode) {

                case MODE_TEST:
                    setIsLoading(null);
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_media_play));
                    break;
                case MODE_CAMERA:
                    setIsLoading(null);
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_menu_camera));
                    break;
                case MODE_RUNNING:
                    setIsLoading(true);
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_lock_power_off));
                    break;
                case MODE_DONE:
                    setIsLoading(false);
                    break;
                case MODE_FAIL:
                case MODE_INTERRUPTED:
                    fabProgressCircle.hide();
                    setUseMode(TextUtil.isValidIp(txtIpEdit.getText().toString()) ?
                            MODE_TEST : MODE_CAMERA);
                    break;
            }
        }
    }

    private Drawable getDrawableCompat(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawable(id);
        } else {
            return getResources().getDrawable(id, null);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_NONE, MODE_TEST, MODE_CAMERA, MODE_RUNNING, MODE_FAIL, MODE_DONE, MODE_INTERRUPTED})
    public @interface UseMode {
        int MODE_NONE = -1;
        int MODE_TEST = 758;
        int MODE_CAMERA = 772;
        int MODE_DONE = 759;
        int MODE_INTERRUPTED = 762;
        int MODE_RUNNING = 760;
        int MODE_FAIL = 761;
    }
}
