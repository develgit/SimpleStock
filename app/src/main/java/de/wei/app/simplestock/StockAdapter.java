package de.wei.app.simplestock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 07.11.17.
 */

public class StockAdapter extends ArrayAdapter<Stock> {
    public StockAdapter(@NonNull Context context, @NonNull List<Stock> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stocks_listitem, parent, false);
        }
        // Get the data item for this position
        Stock stock = getItem(position);
        // Lookup view for data population
        TextView textViewSymbol = (TextView) convertView.findViewById(R.id.stock_symbol);
        TextView textViewValue = (TextView) convertView.findViewById(R.id.stock_value);
        // Populate the data into the template view using the data object
        textViewSymbol.setText(stock.getSymbol());
        textViewValue.setText(stock.getValues());
        // Return the completed view to render on screen
        return convertView;
    }

    public Stock[] getStocks(){
        List<Stock> stocks = new ArrayList<>();
        for(int i = 0; i < getCount(); i++){
            stocks.add(getItem(i));
        }
        return stocks.toArray(new Stock[getCount()]);
    }
}
