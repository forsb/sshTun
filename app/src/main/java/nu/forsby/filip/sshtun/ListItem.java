package nu.forsby.filip.sshtun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItem extends FrameLayout implements ValuePicker.ValueSelectedListener {

    private static final int VALUE_TYPE = 0;
    private static final int FILE_TYPE = 1;

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

        if (fType == FILE_TYPE) {
            int resourceId = value.equals("") ? R.drawable.ic_chevron_right_black_24dp : R.drawable.ic_clear_black_24dp;
            ((ImageView) findViewById(R.id.image)).setImageResource(resourceId);
        }

    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            fActivity.startActivityForResult(
                    Intent.createChooser(intent, "Select a file"),
                    requestCode);
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
            case VALUE_TYPE:
                setOnClickListener(new ValuePicker(fTag, this));
                break;
            case FILE_TYPE:
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView valueView = findViewById(R.id.value);
                        CharSequence value = valueView.getText();
                        if (value.length() == 0) {
                            if (view.getId() == R.id.private_key_list_item) {
                                showFileChooser(MainActivity.PRIVATE_KEY_CHOOSER_RESULT_REQUEST_CODE);
                            } else if (view.getId() == R.id.public_key_list_item) {
                                showFileChooser(MainActivity.PUBLIC_KEY_CHOOSER_RESULT_REQUEST_CODE);
                            }
                        } else {
                            valueSelected("");
                        }

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
