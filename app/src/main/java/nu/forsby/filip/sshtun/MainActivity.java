package nu.forsby.filip.sshtun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements PortForwardTask.PortForwardResultListener {

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1337;
    public static final int PRIVATE_KEY_CHOOSER_RESULT_REQUEST_CODE = 1234;
    public static final int PUBLIC_KEY_CHOOSER_RESULT_REQUEST_CODE = 2345;

    private View fRootView;
    private PreferenceWrapper fPrefsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        fRootView = findViewById(R.id.coordinatorLayout);

        fPrefsWrapper = PreferenceWrapper.getInstance();
        fPrefsWrapper.init(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE));

        setOnclickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_about:
                openAlertDialog(R.string.about_title, R.string.about_message);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onPortForwardSuccess(int assignedPort) {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.connectButton).setEnabled(true);

        showSnackbar("Established connection on local port " + assignedPort);
    }

    @Override
    public void onPortForwardFail(Exception e) {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.connectButton).setEnabled(true);

        e.printStackTrace();
        Log.e("SSHTUN",  e.getClass().getName() + " - " + e.getMessage());
        showSnackbar("Failed to establish connection: " + e.getMessage());
    }

    private void onSshKeyPicked(int requestCode, Intent data) {
        // Get the Uri of the selected file
        Uri uri = data.getData();
        String fileName = getFileName(uri);

        int viewId;
        if (requestCode == PRIVATE_KEY_CHOOSER_RESULT_REQUEST_CODE) {
            viewId = R.id.private_key_list_item;
            fPrefsWrapper.set("Private Key", fileName);
        } else if (requestCode == PUBLIC_KEY_CHOOSER_RESULT_REQUEST_CODE) {
            viewId = R.id.public_key_list_item;
            fPrefsWrapper.set("Public Key", fileName);
        } else {
            return;
        }

        ListItem li = findViewById(viewId);
        ((TextView) li.findViewById(R.id.value)).setText(fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] bytes = new byte[1024];
            while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                buffer.write(bytes, 0, nRead);
            }

            buffer.flush();
            if (requestCode == PRIVATE_KEY_CHOOSER_RESULT_REQUEST_CODE) {
                fPrefsWrapper.setPrivateKey(buffer.toByteArray());
            } else if (requestCode == PUBLIC_KEY_CHOOSER_RESULT_REQUEST_CODE) {
                fPrefsWrapper.setPublicKey(buffer.toByteArray());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PRIVATE_KEY_CHOOSER_RESULT_REQUEST_CODE:
            case PUBLIC_KEY_CHOOSER_RESULT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    onSshKeyPicked(requestCode, data);
                }
                break;
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                break;
            default:
                Log.w("SSHTUN", "Unhandled request code: " + requestCode);
        }
    }

    private void setOnclickListeners() {
        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ProgressBar progressBar = findViewById(R.id.progressbar);
                progressBar.setVisibility(View.VISIBLE);
                view.setEnabled(false);

                new PortForwardTask(MainActivity.this).execute();
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(fRootView, message, Snackbar.LENGTH_LONG).show();
    }

    private void openAlertDialog(int title, int message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}

