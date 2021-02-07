package nu.forsby.filip.sshtun;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListHeading extends LinearLayout {

    private String fLabel = "";

    public ListHeading(Context context) {
        super(context);
        init(context, null);
    }

    public ListHeading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ListHeading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.frame_list_heading, this);

        if (attrs != null) {
            TypedArray style = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.ListHeading, 0, 0);
            try {
                fLabel = style.getString(R.styleable.ListHeading_label);
            } finally {
                style.recycle();
            }
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        TextView textView = findViewById(R.id.header);
        textView.setText(fLabel);
    }

}
