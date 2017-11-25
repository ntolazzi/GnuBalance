package de.quantumnetwork.gnubalance;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by nico on 03.10.17.
 */

public class RestCommunicator extends AsyncTask<String, String, String> {

    public MainActivity mActivity;
    public ArrayList<Transaction> transactionList;
    public String transactionListJSON;

    public RestCommunicator(MainActivity a) {
        mActivity = a;
        transactionListJSON = a.transactionListToString(a.transactions);
    }

    @Override
    protected String doInBackground(String... method) {
        String meth = method[0];
        String requestURL;
        String response = "FAIL";
        String BaseURL = "INSERT YOUR SERVER URL HERE";
        switch (meth) {
            case "POST":
                requestURL = BaseURL + "update";
                try {
                    URL tURL = new URL(requestURL);
                    HttpURLConnection conn = (HttpURLConnection) tURL.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.addRequestProperty("Accept", "application/json");
                    conn.addRequestProperty("Content-Type", "application/json");
                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(transactionListJSON);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    conn.connect();
                    response = "CPOST"+Integer.toString(conn.getResponseCode());
                } catch (MalformedURLException e) {
                    response = "EPOST: Malformed URL";
                    e.printStackTrace();
                } catch (IOException e) {
                    response = "EPOST: IOException";
                    e.printStackTrace();
                }
                break;
            case "GET":
                requestURL = BaseURL + "list";
                String inputLine;
                try{
                    URL tURL = new URL(requestURL);
                    HttpURLConnection conn =(HttpURLConnection) tURL.openConnection();
                    conn.setRequestMethod(meth);
                    conn.connect();

                    //Create a new InputStreamReader
                    InputStreamReader streamReader = new
                            InputStreamReader(conn.getInputStream());

                    //Create a new buffered reader and String Builder
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    //Check if the line we are reading is not null
                    while((inputLine = reader.readLine()) != null){
                        stringBuilder.append(inputLine);
                    }

                    //Close our InputStream and Buffered reader
                    reader.close();
                    streamReader.close();

                    //Set our result equal to our stringBuilder
                    response = stringBuilder.toString();
                } catch (MalformedURLException e) {
                    response = "EGET: Malformed URL";
                    e.printStackTrace();
                } catch (IOException e) {
                    response = "EGET: IOException";
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        mActivity.debug(s);
        if(s.startsWith("E") || s.startsWith("C")){
            mActivity.debug(s);
            return;
        }
        mActivity.transactions.clear();
        mActivity.transactions.addAll(mActivity.stringToTransactionList(s));
        mActivity.adapter.notifyDataSetChanged();
        mActivity.updateTotalBalance();
    }
}
