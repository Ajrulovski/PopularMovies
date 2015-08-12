package ins.com.mk.popularmovies.helper;

/**
 * Created by Gazmend on 11/12/2014.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
            String fontPath = "fonts/Lobster.ttf";
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), fontPath);
            setTypeface(tf);
    }

//    private void init(AttributeSet attrs) {
//        if (attrs!=null) {
//            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);
//            String fontName = a.getString(R.styleable.MyTextView_fontName);
//            if (fontName!=null) {
//                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
//                setTypeface(myTypeface);
//            }
//            a.recycle();
//        }
//    }

}
