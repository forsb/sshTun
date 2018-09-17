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

        sharedPref.edit().putString("User", "root").commit();

        setOnclickListeners();
        inflateViews();



        // --- Set up connection ---
        Host host = new Host("10.20.30.58", "ha", "qweqwe", 22);
        con = new Connection(host) {
            @Override
            public void onPortForwardSuccess(int assigned_port) {
                showSnackbar("Established connection on local port " + assigned_port);
            }

            @Override
            public void onPortForwardFail(Exception e) {
                e.printStackTrace();
                Log.e("SSHTUN", e.getClass().getName().toString() + " - " + e.getMessage().toString());
                showSnackbar("Failed to establish connection");
            }
        };

    }

    private void inflateViews() {
        LinearLayout rootLinearLayout = findViewById(R.id.rootLinearLayout);

        View sshHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) sshHeaderView.findViewById(R.id.header)).setText("SSH CONNECTION");
        rootLinearLayout.addView(sshHeaderView);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "User", sharedPref.getString("User", "")).view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Host", sharedPref.getString("Host", "")).view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Port", sharedPref.getString("Port", "")).view);

        View localPortHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) localPortHeaderView.findViewById(R.id.header)).setText("LOCAL PORT PARAMETERS");
        rootLinearLayout.addView(localPortHeaderView);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Local Port", sharedPref.getString("Local Port", "")).view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Remote Host", sharedPref.getString("Remote Host", "")).view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Remote Port", sharedPref.getString("Remote Port", "")).view);

        View securityHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) securityHeaderView.findViewById(R.id.header)).setText("SECURITY SETTINGS");
        rootLinearLayout.addView(securityHeaderView);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Password", sharedPref.getString("Password", "")).view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Private Key", sharedPref.getString("Private Key", "")).view);
    }

    private void setOnclickListeners() {

        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                con.PortForward(55555, "127.0.0.1", 8123);
            }
        });
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
                    final View dialogView =  MainActivity.this.getLayoutInflater().inflate(R.layout.frame_dialog_input, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder
                            .setTitle(tag)
                            .setView(dialogView)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String dialogTextValue = ((EditText) dialogView.findViewById(R.id.dialogText)).getText().toString();
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
