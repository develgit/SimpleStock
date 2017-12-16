package de.wei.app.simplestock;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 07.11.17.
 */

public class Stock {
    private final static String LOG_TAG = FetchStockDataTask.class.getSimpleName();

    private String symbol;
    private String date;
    private String time;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;

    public static List<Stock> getStocks(){
        List<Stock> stocks = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            stocks.add(parse("BMW.DE,2017-11-06,17:35:00,89.4,90.68,89.21,89.97,1518663"));
        }
        return stocks;
    }
    public static Stock parse(String csv) {
        Stock stock = new Stock();
        /*
        Symbol,Date,Time,Open,High,Low,Close,Volume
        BMW.DE,2017-11-06,17:35:00,89.4,90.68,89.21,89.97,1518663
         */
        String[] stockValues = getStockValue(csv);
        stock.setSymbol(stockValues[0]);
        stock.setDate(stockValues[1]);
        stock.setTime(stockValues[2]);
        stock.setOpen(stockValues[3]);
        stock.setHigh(stockValues[4]);
        stock.setLow(stockValues[5]);
        stock.setClose(stockValues[6]);
        stock.setVolume(stockValues[7]);

        return stock;
    }

    private static String[] getStockValue(String csvData) {
        String csvValue = null;
        try {
            BufferedReader reader = new BufferedReader(new StringReader(csvData));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Symbol,")) {
                    csvValue = line;
                    break;
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        String[] stockValues = new String[8];
        /*
        Symbol,Date,Time,Open,High,Low,Close,Volume
        BMW.DE,2017-11-06,17:35:00,89.4,90.68,89.21,89.97,1518663
         */
        if (csvValue != null) {
            String[] csvValueSplitted = csvValue.split(",");
            if (stockValues.length == 8) {
                for(int i = 0; i < 8; i++){
                    stockValues[i] = csvValueSplitted[i];
                }
            }
        }
        if(stockValues[0] == null){
            Log.e(LOG_TAG, "Kein richtige CSV Daten aus csv: " + csvData);
            for(int i = 0; i < 8; i++){
                stockValues[i] = "";
            }
        }

        return stockValues;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getValues() {
        //String template = "Symbol  ,       Date,     Time,   Open,   High,    Low,  Close,  Volume";
          /*
        Symbol,Date,Time,Open,High,Low,Close,Volume
        BMW.DE,2017-11-06,17:35:00,89.4,90.68,89.21,89.97,1518663
         */
          StringBuilder builder = new StringBuilder();
          builder.append(addSpace(8, getSymbol()));
          builder.append(addSpace(11, getDate()));
          builder.append(addSpace(9, getTime()));
          builder.append(addSpace(5, getOpen()));
          builder.append(addSpace(5, getHigh()));
          builder.append(addSpace(5, getLow()));
          builder.append(addSpace(5, getClose()));
          builder.append(addSpace(8, getVolume()));
          return builder.toString();
    }
    private String addSpace(int size, String str){
        int spaceSize = size -str.length() ;
        String space ="";
        while (spaceSize > 0){
            space+=" ";
        }
        return space + str + "," ;
    }
}
