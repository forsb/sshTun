package nu.forsby.filip.sshtun;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Connection con;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOnclickListeners();

        this.rootView = findViewById(R.id.coordinatorLayout);

        final TextView tView = findViewById(R.id.textView);
        tView.setTypeface(Typeface.MONOSPACE);

//        TextView cHead = findViewById(R.id.cardHead);

        String headStr = "<font color='#CCC65'>" +
                "ha" +
                "</font> @ <font color='#29B6F6'>" +
                "10.20.30.58" +
                "</font> : <font color='#FFA726'>" +
                "22" +
                "</font>";

//        cHead.setText(Html.fromHtml(headStr));
//        TextViewCompat.setAutoSizeTextTypeWithDefaults(cHead, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

        // --- Set up connection ---
        Host host = new Host("10.20.30.58", "ha", "qweqwe", 22);
        con = new Connection(host) {
            @Override
            public void onExecuteSuccess(Command command) {
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                tView.setText("");
                for (String str : command.output) {
                    tView.append(str);
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

}
