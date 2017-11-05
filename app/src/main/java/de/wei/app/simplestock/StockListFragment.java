package de.wei.app.simplestock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 04.11.17.
 */

public class StockListFragment extends Fragment{
    private static final String LOG_TAG = StockListFragment.class.getSimpleName();

    private ArrayAdapter<String> stocklisteAdapter;
    public StockListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stocklist, container, false);

        registerStockDataAdapter(view);


        registerClickListenerForFloatingAddBtn(view);

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_stocklist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == R.id.action_update_all_stock) {
            clickOptionItemUpdateAllStock(item);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clickOptionItemUpdateAllStock(MenuItem item){
        FetchStockDataTask fetchStockDataTask = new FetchStockDataTask(getActivity(), stocklisteAdapter);
        fetchStockDataTask.execute("Fetch Stock");
        Toast.makeText(getActivity(), "Aktiendaten werden abgefragt!", Toast.LENGTH_LONG).show();
    }

    private void registerClickListenerForFloatingAddBtn(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_stock);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void registerStockDataAdapter(View view) {

        String [] stockArray = {
                "Adidas - Kurs: 73,45 €",
                "Allianz - Kurs: 145,12 €",
                "BASF - Kurs: 84,27 €",
                "Bayer - Kurs: 128,60 €",
                "Beiersdorf - Kurs: 80,55 €",
                "BMW St. - Kurs: 104,11 €",
                "Commerzbank - Kurs: 12,47 €",
                "Continental - Kurs: 209,94 €",
                "Continental - Kurs: 209,94 €",
                "Continental - Kurs: 209,94 €",
                "Daimler - Kurs: 84,33 €"
        };

        List<String> stockListe = new ArrayList<>(Arrays.asList(stockArray));

        Log.d(LOG_TAG, "Stock: " + stockListe);

        stocklisteAdapter =
                new ArrayAdapter<>(
                        getActivity(), // Die aktuelle Umgebung (diese Activity)
                        R.layout.stocks_listitem, // ID der XML-Layout Datei
                        R.id.stocks_listview_textview, // ID des TextViews
                        stockListe); // Beispieldaten in einer ArrayList


        ListView stocklisteListView = (ListView) view.findViewById(R.id.stocks_listview);
        stocklisteListView.setAdapter(stocklisteAdapter);
    }

}