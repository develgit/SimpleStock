package de.wei.app.simplestock;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
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

    private StockAdapter stocklisteAdapter;
    public StockListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stocklist, container, false);

        initialStockListView(view);


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
        fetchStockDataTask.execute(stocklisteAdapter.getStocks());
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

    private void initialStockListView(View view) {
        // Construct the data source
        List<Stock> arrayOfStocks = Stock.getStocks();
        // Create the adapter to convert the array to views
        stocklisteAdapter = new StockAdapter(this.getActivity(), arrayOfStocks);

        ListView stocklisteListView = (ListView) view.findViewById(R.id.stocks_listview);
        stocklisteListView.setAdapter(stocklisteAdapter);

        stocklisteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String aktienInfo = (String) adapterView.getItemAtPosition(position);
                // Intent erzeugen und Starten der AktiendetailActivity mit explizitem Intent
                Intent stockdetailIntent = new Intent(getActivity(), StockdetailActivity.class);
                stockdetailIntent.putExtra(Intent.EXTRA_TEXT, aktienInfo);
                startActivity(stockdetailIntent);

            }
        });

        stocklisteListView.setLongClickable(true);
        stocklisteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i(LOG_TAG, "onItemLongClick:" + position + ", " + id);
                final Stock itemSelected = stocklisteAdapter.getItem(position);
                Log.i(LOG_TAG, "onItemLongClick: Selected Item: " + itemSelected.getValues());
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        StockListFragment.this.getActivity());
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete record");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        stocklisteAdapter.remove(itemSelected);
                        stocklisteAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });
    }

}