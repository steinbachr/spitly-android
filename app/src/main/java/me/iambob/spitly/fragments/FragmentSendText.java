package me.iambob.spitly.fragments;

import android.app.Fragment;
import android.content.Context;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.iambob.spitly.R;


public class FragmentSendText extends Fragment {
    Context c;

    public FragmentSendText() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_text, container, false);
        this.c = getActivity();

        this.createSpinners(rootView);

        return rootView;
    }

    /**-- Helpers --**/
    private void createSpinners(View rootView) {
        Spinner timeSpinner = (Spinner)rootView.findViewById(R.id.time_spinner);
        Spinner timeTypeSpinner = (Spinner)rootView.findViewById(R.id.time_type_spinner);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this.c, R.array.spinner_times, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        ArrayAdapter<CharSequence> timeTypeAdapter = ArrayAdapter.createFromResource(this.c, R.array.spinner_time_types, android.R.layout.simple_spinner_item);
        timeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeTypeSpinner.setAdapter(timeTypeAdapter);
    }
}