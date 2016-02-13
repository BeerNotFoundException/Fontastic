package hu.beernotfoundexception.fontastic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.github.jorgecastilloprz.FABProgressCircle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import hu.beernotfoundexception.fontastic.domain.control.ControlInterface;
import hu.beernotfoundexception.fontastic.domain.presenter.LogDisplay;
import hu.beernotfoundexception.fontastic.domain.presenter.Presenter;
import hu.beernotfoundexception.fontastic.util.Logger;
import hu.beernotfoundexception.fontastic.util.TextUtil;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements Presenter, LogDisplay {

    public static final int MODE_NONE = -1;
    public static final int MODE_TEST = 758;
    public static final int MODE_CAMERA = 772;
    public static final int MODE_TEST_RUNNING = 759;
    public static final int MODE_CAMERA_RUNNING = 773;
    private static final int REQUEST_CAMERA_PERMISSION = 838;
    private static final int REQUEST_IMAGE_CAPTURE = 236;
    protected EditText txtIpEdit;
    protected FloatingActionButton fab;
    protected FABProgressCircle fabProgressCircle;
    protected ImageView imgPreview;
    protected ListView listConsole;
    private ArrayAdapter<String> logAdapter;
    private ControlInterface controlInterface = Binder.getControlInterface(this);
    private
    @UseMode
    int currentUseMode = MODE_CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentUseMode) {

                    case MODE_CAMERA:
                        openCamera();
                        break;
                    case MODE_TEST:
                        startTest();
                        break;
                    case MODE_TEST_RUNNING:
                        controlInterface.cancelTest();
                        break;
                    case MODE_CAMERA_RUNNING:
                        Snackbar.make(fab, "Please wait while the processing is over.", Snackbar.LENGTH_LONG);
                        break;
                }
            }
        });

        listConsole = (ListView) findViewById(R.id.listConsole);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);

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
                if (currentUseMode != MODE_TEST_RUNNING && currentUseMode != MODE_CAMERA_RUNNING)
                    setFabMode(TextUtil.isValidIp(s.toString()) ?
                            MODE_TEST : MODE_CAMERA);
            }
        });

        logAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        listConsole.setAdapter(logAdapter);

        Logger.setLogDisplay(this);

        Binder.bindLogger(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            showImage(imageBitmap);
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

    @Override
    public void showImage(final Bitmap image) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                imgPreview.setImageBitmap(image);
            }
        });
    }

    private void startTest() {
        Snackbar.make(fab, "Starting test.", Snackbar.LENGTH_LONG).show();

        currentUseMode = MODE_TEST_RUNNING;

        controlInterface.onTestRequest(txtIpEdit.getText().toString(), new ControlInterface.TestProgressListener() {
            @Override
            public void onStart() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        fabProgressCircle.show();
                    }
                });
            }

            @Override
            public void onProgress(float p) {

            }

            @Override
            public void onFinish() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        fabProgressCircle.beginFinalAnimation();
                    }
                });
            }
        });
    }

    private void setFabMode(@UseMode int useMode) {
        if (useMode != this.currentUseMode || this.currentUseMode == MODE_NONE) {
            this.currentUseMode = useMode;
            switch (useMode) {

                case MODE_TEST:
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_media_play));
                    break;
                case MODE_CAMERA:
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_menu_camera));
                    break;
                case MODE_CAMERA_RUNNING:
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_menu_camera));
                    break;
                case MODE_TEST_RUNNING:
                    fab.setImageDrawable(getDrawableCompat(android.R.drawable.ic_media_pause));
                    break;
            }
        }
    }

    public Drawable getDrawableCompat(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawable(id);
        } else {
            return getResources().getDrawable(id, null);
        }
    }

    @Override
    public void logMessage(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                logAdapter.insert(msg, 0);
            }
        });
    }

    @Override
    public void onDetectionResult(String fontName) {
        logMessage("Font detected: " + fontName);
    }

    @Override
    public void onDetectionError(String reason) {
        logMessage("Detection error occured. Reason: " + reason);
    }

    @Override
    public void onConnected() {
        logMessage("Connected");
    }

    @Override
    public void onNoConnection() {
        logMessage("No connection can be estabilished");
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_NONE, MODE_TEST, MODE_CAMERA, MODE_TEST_RUNNING, MODE_CAMERA_RUNNING})
    public @interface UseMode {
    }

}
