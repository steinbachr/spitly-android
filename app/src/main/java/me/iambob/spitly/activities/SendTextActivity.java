package me.iambob.spitly.activities;

import me.iambob.spitly.fragments.FragmentSendText;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.iambob.spitly.R;
import me.iambob.spitly.utils.Messaging;


public class SendTextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentSendText())
                    .commit();
        }
    }

    public void scheduleMessage(View v) {
        String message = ((TextView)this.findViewById(R.id.message)).getText().toString();
        Messaging.sendMessage("15182818509", message);
    }
}
