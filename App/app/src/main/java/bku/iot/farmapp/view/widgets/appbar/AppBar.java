package bku.iot.farmapp.view.widgets.appbar;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import bku.iot.farmapp.R;

public class AppBar {
    private final ImageView backButton;
    private final TextView headerText;

    public AppBar(AppCompatActivity context, OnBackButtonClick onBackButtonClick){
        backButton = context.findViewById(R.id.appbar_backButton);
        headerText = context.findViewById(R.id.appbar_headerText);

        backButton.setOnClickListener(v -> onBackButtonClick.handle());
    }

    public void setHeaderText(String header){
        headerText.setText(header);
    }

    public interface OnBackButtonClick {
        void handle();
    }
}
