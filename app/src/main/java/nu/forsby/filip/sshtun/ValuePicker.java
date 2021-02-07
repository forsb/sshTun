package nu.forsby.filip.sshtun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ValuePicker implements View.OnClickListener {

    private String fLabel;
    private ValueSelectedListener fListener;

    public interface ValueSelectedListener {
        void valueSelected(String value);
    }

    public ValuePicker(String label, ValueSelectedListener listener) {
        fLabel = label;
        fListener = listener;
    }

    @Override
    public void onClick(View view) {
        // Read from prefs instead?
        final TextView valueView = view.findViewById(R.id.value);
        CharSequence initialValue = valueView.getText();

        final View dialogView = LayoutInflater.from(view.getContext()).inflate(
                R.layout.frame_dialog_input, null);

        EditText dialogText = dialogView.findViewById(R.id.dialogText);
        dialogText.setText(initialValue);
        dialogText.setSelection(dialogText.getText().length());

        new AlertDialog.Builder(view.getContext())
                .setTitle(fLabel)
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText dialogText = dialogView.findViewById(R.id.dialogText);
                        String dialogTextValue = dialogText.getText().toString();
                        fListener.valueSelected(dialogTextValue);
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
