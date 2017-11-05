package de.wei.app.simplestock;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * Created by wei on 05.11.17.
 */

public class FetchStockDataTask extends AsyncTask<String, Integer, String[]> {
    private final static String LOG_TAG = FetchStockDataTask.class.getSimpleName();

    private ArrayAdapter<String> stocklisteAdapter;
    private Activity activity;

    public FetchStockDataTask(Activity activity, ArrayAdapter<String> stocklisteAdapter) {
        this.stocklisteAdapter = stocklisteAdapter;
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... strings) {

        String[] ergebnisArray = new String[20];

        for (int i=0; i < 20; i++) {

            // Den StringArray füllen wir mit Beispieldaten
            ergebnisArray[i] = strings[0] + "_" + (i+1);

            // Alle 5 Elemente geben wir den aktuellen Fortschritt bekannt
            if (i%5 == 4) {
                publishProgress(i+1, 20);
            }

            // Mit Thread.sleep(600) simulieren wir eine Wartezeit von 600 ms
            try {
                Thread.sleep(600);
            }
            catch (Exception e) { Log.e(LOG_TAG, "Error ", e); }
        }

        return ergebnisArray;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        // Auf dem Bildschirm geben wir eine Statusmeldung aus, immer wenn
        // publishProgress(int...) in doInBackground(String...) aufgerufen wird
        Toast.makeText(getActivity(), values[0] + " von " + values[1] + " geladen",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPostExecute(String[] strings) {

        // Wir löschen den Inhalt des ArrayAdapters und fügen den neuen Inhalt ein
        // Der neue Inhalt ist der Rückgabewert von doInBackground(String...) also
        // der StringArray gefüllt mit Beispieldaten
        if (strings != null) {
            getStocklisteAdapter().clear();
            for (String aktienString : strings) {
                getStocklisteAdapter().add(aktienString);
            }
        }

        // Hintergrundberechnungen sind jetzt beendet, darüber informieren wir den Benutzer
        Toast.makeText(getActivity(), "Aktiendaten vollständig geladen!",
                Toast.LENGTH_SHORT).show();
    }

    private Activity getActivity() {
        return activity;
    }

    public ArrayAdapter<String> getStocklisteAdapter() {
        return stocklisteAdapter;
    }
}

