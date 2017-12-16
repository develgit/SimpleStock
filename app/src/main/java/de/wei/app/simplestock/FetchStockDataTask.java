package de.wei.app.simplestock;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by wei on 05.11.17.
 */

public class FetchStockDataTask extends AsyncTask<Stock, Integer, Long> {
    private final static String LOG_TAG = FetchStockDataTask.class.getSimpleName();

    private StockAdapter stocklisteAdapter;
    private Activity activity;
    /*
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
     protected Long doInBackground(URL... urls) {
         int count = urls.length;
         long totalSize = 0;
         for (int i = 0; i < count; i++) {
             totalSize += Downloader.downloadFile(urls[i]);
             publishProgress((int) ((i / (float) count) * 100));
             // Escape early if cancel() is called
             if (isCancelled()) break;
         }
         return totalSize;
     }

     protected void onProgressUpdate(Integer... progress) {
         setProgressPercent(progress[0]);
     }

     protected void onPostExecute(Long result) {
         showDialog("Downloaded " + result + " bytes");
     }
 }
     */

    public FetchStockDataTask(Activity activity, StockAdapter stocklisteAdapter) {
        this.stocklisteAdapter = stocklisteAdapter;
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(Stock... stocks) {

        if (stocks.length == 0) { // Keine Eingangsparameter erhalten, daher Abbruch
            return null;
        }

        //Key: NHJJ0QINWJPKL86V https://www.alphavantage.co
        getStocklisteAdapter().clear();

        //List<Stock> stockDatas = new ArrayList<>();
        int index = 1;
        for(Stock stock: stocks){
            Stock stockData = readStockDataHttps(stock.getSymbol());
            //stockDatas.add(stockData);

            getStocklisteAdapter().add(stockData);

            Log.v(LOG_TAG,"Stock Data:(" + stock.getSymbol() + "):" + stockData);

            publishProgress(index++, stocks.length);
        }
        return (long)index;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {

        // Auf dem Bildschirm geben wir eine Statusmeldung aus, immer wenn
        // publishProgress(int...) in doInBackground(String...) aufgerufen wird
        Toast.makeText(getActivity(), values[0] + " von " + values[1] + " geladen",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPostExecute(Long result) {

        // Wir löschen den Inhalt des ArrayAdapters und fügen den neuen Inhalt ein
        // Der neue Inhalt ist der Rückgabewert von doInBackground(String...) also
        // der StringArray gefüllt mit Beispieldaten
        /*if (stocks != null) {
            getStocklisteAdapter().clear();
            for (Stock stock : stocks) {
                getStocklisteAdapter().add(stock);
            }
        }*/

        // Hintergrundberechnungen sind jetzt beendet, darüber informieren wir den Benutzer
        Toast.makeText(getActivity(), "Aktiendaten vollständig geladen!",
                Toast.LENGTH_SHORT).show();
    }

    private Activity getActivity() {
        return activity;
    }

    public StockAdapter getStocklisteAdapter() {
        return stocklisteAdapter;
    }

    private Stock readStockDataHttps(String stockSymbol){

        HttpsURLConnection httpsURLConnection = null;

        BufferedReader bufferedReader = null;

        String aktiendatenXmlString = "";

        try {

            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

// Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
            }

            //https://stooq.com/q/l/?s=ads.de&f=sd2t2ohlcv&h&e=csv
            String urlTemplate ="https://stooq.com/q/l/?s=%s&f=sd2t2ohlcv&h&e=csv";
            String urlString = String.format(urlTemplate, stockSymbol);
            URL url = new URL(urlString);

            // Aufbau der Verbindung zur YQL Platform
            httpsURLConnection = (HttpsURLConnection)url.openConnection();

            InputStream inputStream = httpsURLConnection.getInputStream();

            if (inputStream == null) { // Keinen Aktiendaten-Stream erhalten, daher Abbruch
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                aktiendatenXmlString += line + "\n";
            }
            if (aktiendatenXmlString.length() == 0) { // Keine Aktiendaten ausgelesen, Abbruch
                return null;
            }
            Log.v(LOG_TAG, "Aktiendaten XML-String: " + aktiendatenXmlString);

        } catch (IOException e) { // Beim Holen der Daten trat ein Fehler auf, daher Abbruch
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return Stock.parse(aktiendatenXmlString);
    }
    private String readStockDataHttp(String stockSymbol){
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String aktiendatenXmlString = "";

        try {
            //https://stooq.com/q/l/?s=ads.de&f=sd2t2ohlcv&h&e=csv
            String urlTemplate ="https://stooq.com/q/l/?s=%s&f=sd2t2ohlcv&h&e=csv";
            URL url = new URL(String.format(urlTemplate, stockSymbol));

            // Aufbau der Verbindung zur YQL Platform
            httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = httpURLConnection.getInputStream();

            if (inputStream == null) { // Keinen Aktiendaten-Stream erhalten, daher Abbruch
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                aktiendatenXmlString += line + "\n";
            }
            if (aktiendatenXmlString.length() == 0) { // Keine Aktiendaten ausgelesen, Abbruch
                return null;
            }
            Log.v(LOG_TAG, "Aktiendaten XML-String: " + aktiendatenXmlString);
            publishProgress(1, 1);


        } catch (IOException e) { // Beim Holen der Daten trat ein Fehler auf, daher Abbruch
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return aktiendatenXmlString;
    }

}

