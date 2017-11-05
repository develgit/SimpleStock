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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by wei on 05.11.17.
 */

public class FetchStockDataOLDTask extends AsyncTask<String, Integer, String[]> {
    private final static String LOG_TAG = FetchStockDataOLDTask.class.getSimpleName();

    private ArrayAdapter<String> stocklisteAdapter;
    private Activity activity;

    public FetchStockDataOLDTask(Activity activity, ArrayAdapter<String> stocklisteAdapter) {
        this.stocklisteAdapter = stocklisteAdapter;
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... strings) {

        if (strings.length == 0) { // Keine Eingangsparameter erhalten, daher Abbruch
            return null;
        }

        //Key: NHJJ0QINWJPKL86V https://www.alphavantage.co

        // Exakt so muss die Anfrage-URL an die YQL Platform gesendet werden:
  /*
  https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20csv%20where%20url
  %3D'http%3A%2F%2Fdownload.finance.yahoo.com%2Fd%2Fquotes.csv%3Fs%3D
  BMW.DE%2CDAI.DE%2C%255EGDAXI%26f%3Dsnc4xl1d1t1c1p2ohgv%26e%3D.csv'%20and%20columns%3D'
  symbol%2Cname%2Ccurrency%2Cexchange%2Cprice%2Cdate%2Ctime%2Cchange%2Cpercent%2C
  open%2Chigh%2Clow%2Cvolume'&diagnostics=true";
  */
    /*
    %20: leerzeichen
    %26: &
    %255E: ^ -- für Indiezes wie DAX (^GDAXI)
     */

        // Wir konstruieren die Anfrage-URL für die YQL Platform
        final String URL_PARAMETER = "https://query.yahooapis.com/v1/public/yql";
        final String SELECTOR = "select%20*%20from%20csv%20where%20";
        final String DOWNLOAD_URL = "http://download.finance.yahoo.com/d/quotes.csv";
        final String DIAGNOSTICS = "'&diagnostics=true";

        String symbols = "BMW.DE,DAI.DE,^GDAXI";
        symbols = symbols.replace("^", "%255E");
        String parameters = "snc4xl1d1t1c1p2ohgv";
        String columns = "symbol,name,currency,exchange,price,date,time," +
                "change,percent,open,high,low,volume";

        String anfrageString = URL_PARAMETER;
        anfrageString += "?q=" + SELECTOR;
        anfrageString += "url='" + DOWNLOAD_URL;
        anfrageString += "?s=" + symbols;
        anfrageString += "%26f=" + parameters;
        anfrageString += "%26e=.csv'%20and%20columns='" + columns;
        anfrageString += DIAGNOSTICS;

        Log.v(LOG_TAG, "Zusammengesetzter Anfrage-String: " + anfrageString);

        // Die URL-Verbindung und der BufferedReader, werden im finally-Block geschlossen
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        // In diesen String speichern wir die Aktiendaten im XML-Format
        String aktiendatenXmlString = "";

        try {
            URL url = new URL(anfrageString);

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

        // Hier parsen wir später die XML Aktiendaten
        return leseXmlAktiendatenAus(aktiendatenXmlString);
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


    private String[] leseXmlAktiendatenAus(String xmlString) {

        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlString));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        }

        Element xmlAktiendaten = doc.getDocumentElement();
        NodeList aktienListe = xmlAktiendaten.getElementsByTagName("row");

        int anzahlAktien = aktienListe.getLength();
        int anzahlAktienParameter = aktienListe.item(0).getChildNodes().getLength();

        String[] ausgabeArray = new String[anzahlAktien];
        String[][] alleAktienDatenArray = new String[anzahlAktien][anzahlAktienParameter];

        Node aktienParameter;
        String aktienParameterWert;
        for( int i=0; i<anzahlAktien; i++ ) {
            NodeList aktienParameterListe = aktienListe.item(i).getChildNodes();

            for (int j=0; j<anzahlAktienParameter; j++) {
                aktienParameter = aktienParameterListe.item(j);
                aktienParameterWert = aktienParameter.getFirstChild().getNodeValue();
                alleAktienDatenArray[i][j] = aktienParameterWert;
            }

            ausgabeArray[i]  = alleAktienDatenArray[i][0];                // symbol
            ausgabeArray[i] += ": " + alleAktienDatenArray[i][4];         // price
            ausgabeArray[i] += " " + alleAktienDatenArray[i][2];          // currency
            ausgabeArray[i] += " (" + alleAktienDatenArray[i][8] + ")";   // percent
            ausgabeArray[i] += " - [" + alleAktienDatenArray[i][1] + "]"; // name
            Log.v(LOG_TAG,"XML Output:" + ausgabeArray[i]);
            Log.v(LOG_TAG,"XML Output:" + ausgabeArray[i]);
        }

        return ausgabeArray;
    }
}

