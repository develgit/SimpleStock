package de.wei.app.simplestock;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wei on 06.11.17.
 */

public class StockdetailFragment extends Fragment {

    public StockdetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stockdetail, container, false);

        // Die AktiendetailActivity wurde über einen Intent aufgerufen
        // Wir lesen aus dem empfangenen Intent die übermittelten Daten aus
        Intent empfangenerIntent = getActivity().getIntent();
        if (empfangenerIntent != null && empfangenerIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String aktienInfo = empfangenerIntent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.stockdetail_text))
                    .setText(aktienInfo);
        }

        return rootView;

    }
}
