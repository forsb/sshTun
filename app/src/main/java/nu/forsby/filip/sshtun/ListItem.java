package nu.forsby.filip.sshtun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ListItem extends FrameLayout implements ValuePicker.ValueSelectedListener {

    private String fTag;
    private int fType;
    private SharedPreferences fPrefs;
    private Activity fActivity;

    public ListItem(Context context) {
        super(context);
        init(context, null);
    }

    public ListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.frame_list_item, this);

        if (context instanceof Activity) {
            fActivity = (Activity) context;
        }

        if (attrs != null) {
            TypedArray style = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.ListItem, 0, 0);

            try {
                fTag = style.getString(R.styleable.ListItem_tag);
                fType = style.getInt(R.styleable.ListItem_type, -1);
            } finally {
                style.recycle();
            }
        }

        fPrefs = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    private void updateTag(String tag) {
        TextView tagView = findViewById(R.id.tag);
        tagView.setText(tag);
    }

    private void updateValue(String value) {
        TextView valueView = findViewById(R.id.value);
        valueView.setText(value);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            fActivity.startActivityForResult(
                    Intent.createChooser(intent, "Select a file"),
                    MainActivity.FILE_CHOOSER_RESULT_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ignored) {
            // ignore, for now
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        updateTag(fTag);
        updateValue(fPrefs.getString(fTag, ""));

        switch (fType) {
            case 0:
                setOnClickListener(new ValuePicker(fTag, this));
                break;
            case 1:
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFileChooser();
                    }
                });
                break;
            default:
                // Not supported
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void valueSelected(String value) {

        fPrefs.edit().putString(fTag, value).apply();
        updateValue(value);
    }

}
