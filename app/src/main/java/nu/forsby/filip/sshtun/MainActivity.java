package nu.forsby.filip.sshtun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Connection con;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOnclickListeners();
        inflateViews();

        this.rootView = findViewById(R.id.coordinatorLayout);



        // --- Set up connection ---
        Host host = new Host("10.20.30.58", "ha", "qweqwe", 22);
        con = new Connection(host) {
            @Override
            public void onExecuteSuccess(Command command) {
//                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
//                tView.setText("");
                for (String str : command.output) {
//                    tView.append(str);
                }
            }

            @Override
            public void onExecuteFail(Exception e) {
                e.printStackTrace();
                Log.e("SSHTUN", e.getClass().getName().toString() + " - " + e.getMessage().toString());
            }

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


        // --- Autocomplete ---
        String[] arr = {
                "Paries,France",
                "PA,United States",
                "Parana,Brazil",
                "Padua,Italy",
                "Pasadena,CA,United States"};

//        AutoCompleteTextView autocomplete = findViewById(R.id.actv);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arr);

//        autocomplete.setThreshold(2);
//        autocomplete.setAdapter(adapter);

    }

    private void inflateViews() {
        LinearLayout rootLinearLayout = findViewById(R.id.rootLinearLayout);

        View sshHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) sshHeaderView.findViewById(R.id.header)).setText("SSH CONNECTION");
        rootLinearLayout.addView(sshHeaderView);
//        ((TextView) getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, true).findViewById(R.id.heading)).setText("SSH CONNECTION");
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "User", "root").view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Host", "192.168.1.101").view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Port", "22").view);

        View localPortHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) localPortHeaderView.findViewById(R.id.header)).setText("LOCAL PORT PARAMETERS");
        rootLinearLayout.addView(localPortHeaderView);
//        ((TextView) getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, true).findViewById(R.id.heading)).setText("LOCAL PORT PARAMETERS");
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Local Port", "55555").view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Remote Host", "www.example.com").view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Remote Port", "80").view);

        View securityHeaderView = getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false);
        ((TextView) securityHeaderView.findViewById(R.id.header)).setText("SECURITY SETTINGS");
        rootLinearLayout.addView(securityHeaderView);
//        ((TextView) getLayoutInflater().inflate(R.layout.frame_list_heading, rootLinearLayout, false).findViewById(R.id.heading)).setText("SECURITY");
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Password", "*none*").view);
        rootLinearLayout.addView(new ListItem(rootLinearLayout, "Private Key", "private.key").view);
    }

    private void setOnclickListeners() {

//        // Dropdown menu
//        ImageButton down = findViewById(R.id.down);
//        down.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AutoCompleteTextView autocomplete = findViewById(R.id.actv);
//                autocomplete.showDropDown();
//            }
//        });
//
//        // Execute command
//        ImageButton butt = findViewById(R.id.button_default);
//        butt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText eText = findViewById(R.id.actv);
//                con.Execute(eText.getText().toString());
//                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
//            }
//        });

//        // Local port forward
//        ImageButton portFwBtn = findViewById(R.id.button23);
//        portFwBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                con.PortForward(55555, "127.0.0.1", 8123);
//            }
//        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    private class ListItem {

        public View view;

        public ListItem(ViewGroup rootView, String tag, String value) {
            view = getLayoutInflater().inflate(R.layout.frame_list_item, rootView, false);

            TextView tagView = view.findViewById(R.id.tag);
            tagView.setText(tag);

            TextView valueView = view.findViewById(R.id.value);
            valueView.setText(value);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSnackbar("You clicked " + ((TextView)view.findViewById(R.id.tag)).getText());
                }
            });
        }
    }
}
