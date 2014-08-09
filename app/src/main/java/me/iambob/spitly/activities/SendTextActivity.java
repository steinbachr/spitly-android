package me.iambob.spitly.activities;

import me.iambob.spitly.fragments.FragmentSendText;
import android.app.Activity;
import android.os.Bundle;

import me.iambob.spitly.R;


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


}
