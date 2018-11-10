package nu.forsby.filip.sshtun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    Context context;

    Connection con;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        rootView = findViewById(R.id.coordinatorLayout);
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        setOnclickListeners();
        inflateViews();

    }

    private void inflateViews() {
        addHeading("SSH CONNECTION");
        addOption(getString(R.string.user));
        addOption(getString(R.string.host));
        addOption(getString(R.string.port));

        addHeading("LOCAL PORT PARAMETERS");
        addOption(getString(R.string.local_port));
        addOption(getString(R.string.remote_host));
        addOption(getString(R.string.remote_port));

        addHeading("SECURITY SETTINGS");
        addOption(getString(R.string.password));
        addOption(getString(R.string.private_key));
    }

    private void setOnclickListeners() {

        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Host host = new Host(
                        sharedPref.getString("Host", ""),
                        sharedPref.getString("User", ""),
                        sharedPref.getString("Password", ""),
                        Integer.parseInt(sharedPref.getString("Port", "")));

                con = new Connection(host) {
                    @Override
                    public void onPortForwardSuccess(int assigned_port) {
                        showSnackbar("Established connection on local port " + assigned_port);
                    }

                    @Override
                    public void onPortForwardFail(Exception e) {
                        e.printStackTrace();
                        Log.e(
                                "SSHTUN",
                                e.getClass().getName().toString() +
                                        " - " +
                                        e.getMessage().toString());
                        showSnackbar("Failed to establish connection");
                    }
                };

                con.PortForward(
                        Integer.parseInt(sharedPref.getString("Local Port", "")),
                        sharedPref.getString("Remote Host", ""),
                        Integer.parseInt(sharedPref.getString("Remote Port", "")));
            }
        });
    }

    private void addHeading(String name) {
        LinearLayout rootLinearLayout = findViewById(R.id.rootLinearLayout);
        View headerView = getLayoutInflater().inflate(
                R.layout.frame_list_heading,
                rootLinearLayout,
                false);
        ((TextView) headerView.findViewById(R.id.header)).setText(name);
        rootLinearLayout.addView(headerView);
    }

    private void addOption(String name) {
        LinearLayout rootLinearLayout = findViewById(R.id.rootLinearLayout);
        rootLinearLayout.addView(new ListItem(
                rootLinearLayout,
                name,
                sharedPref.getString(name, "")).view);
    }

    private void showSnackbar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    private class ListItem {

        public View view;

        public ListItem(ViewGroup parentView, String tag, String value) {
            view = getLayoutInflater().inflate(R.layout.frame_list_item, parentView, false);

            TextView tagView = view.findViewById(R.id.tag);
            tagView.setText(tag);

            TextView valueView = view.findViewById(R.id.value);
            valueView.setText(value);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    final TextView valueView = view.findViewById(R.id.value);
                    final String tag = ((TextView) view.findViewById(R.id.tag)).getText().toString();
                    final View dialogView =
                            MainActivity.this.getLayoutInflater().inflate(
                                    R.layout.frame_dialog_input, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder
                            .setTitle(tag)
                            .setView(dialogView)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText dialogText = dialogView.findViewById(R.id.dialogText);
                                    String dialogTextValue = dialogText.getText().toString();
                                    sharedPref.edit().putString(tag, dialogTextValue).commit();
                                    valueView.setText(dialogTextValue);
                                    dialog.cancel();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder.show();
                }
            });
        }
    }
}
