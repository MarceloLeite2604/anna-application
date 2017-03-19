package org.marceloleite.projetoanna;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Marcelo Leite on 03/05/2016.
 */
public class InformationView extends LinearLayout {

    private ProgressBar progressBar;
    private TextView textView;

    public InformationView(Context context){
        super(context);

        if ( context == null) {
            throw new IllegalArgumentException("Context cannot be null.");
        }

        View.inflate(context, R.layout.information, this);
        textView = (TextView)findViewById(R.id.information_text);
        progressBar = (ProgressBar)findViewById(R.id.information_progressbar);
    }

    public void showProgressBar(boolean show) {
        int value = View.GONE;
        if (show) {
            value = View.VISIBLE;
        }
        progressBar.setVisibility(value);
    }

    public void setInformationText(String text) {
        textView.setText(text);
    }
}
