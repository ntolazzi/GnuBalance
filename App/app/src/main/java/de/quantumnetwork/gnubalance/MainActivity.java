package de.quantumnetwork.gnubalance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SwipeListener{

    final String TRANSACTIONS_KEY= "TRANSACTIONS";
    ListView listView;
    private Handler handler;
//    private SwipeRefreshLayout swipeContainer;
    ArrayList<Transaction> transactions = new ArrayList<>();
    public TransactionAdapter adapter;
    int colorB;
    int colorA;
    int colorText;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.id.update:
//                getListFromServer();
//                return true;
//            case R.id.upload:
//                sendListToServer();
//                return true;
//        }
//        return(super.onOptionsItemSelected(item));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorA = ContextCompat.getColor(MainActivity.this, R.color.ButtonColorPersonA);
        colorB = ContextCompat.getColor(MainActivity.this, R.color.ButtonColorPersonB);
        colorText = ContextCompat.getColor(MainActivity.this, R.color.TextColor);

//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        listView = findViewById(R.id.listview_transctions);

//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int topRowVerticalPosition =
//                        (listView == null || listView.getChildCount() == 0) ?
//                                0 : listView.getChildAt(0).getTop();
//                swipeContainer.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
//            }
//        });

        if (savedInstanceState != null) {
            // Restore transaction list from saved state
            transactions = stringToTransactionList(savedInstanceState.getString(TRANSACTIONS_KEY));
        } else {
            // if there is no saved state the app is on a fresh start, so load from shared prefs instead
            SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            if(sharedPref.contains(TRANSACTIONS_KEY)) {
                transactions = stringToTransactionList(sharedPref.getString(TRANSACTIONS_KEY, ""));
            }
        }
        updateTotalBalance();


        final int interval = 30000;
        handler = new Handler();
        Runnable updTask = new Runnable() {
            @Override
            public void run() {
                getListFromServer();
                handler.postDelayed(this, interval);
            }
        };
        handler.postDelayed(updTask, 100);


        adapter = new TransactionAdapter(transactions, getApplicationContext(), MainActivity.this);
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.transactionAdd);
        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText textfieldName = findViewById(R.id.transactionText);
                EditText textfieldValue = findViewById(R.id.transactionValue);
                ToggleButton direction = findViewById(R.id.transactionDirection);
                Double value = 0.0;
                try {
                    value = Double.parseDouble(textfieldValue.getText().toString());
                }catch (NumberFormatException e){
                    return;
                }
                Transaction transaction = new Transaction(textfieldName.getText().toString(), direction.isChecked(), value);
                transactions.add(transaction);
                adapter.notifyDataSetChanged();
                updateTotalBalance();
                sendListToServer();
            }
        });

        //ToDo: implement settings where color can be chosen
//        final ColorPicker cp = new ColorPicker(MainActivity.this);
//        cp.show();
//        Button okColor = cp.findViewById(R.id.okColorButton);
//        okColor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String selectedColor = String.format("#%08X", (cp.getColor()));
//                EditText textfieldName = findViewById(R.id.transactionText);
//                cp.dismiss();
//                textfieldName.setText(selectedColor);
//            }
//        });

        ToggleButton direction = findViewById(R.id.transactionDirection);
        direction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    buttonView.setBackgroundColor(colorB);
                }else{
                    buttonView.setBackgroundColor(colorA);
                }
            }
        });

    }

    public void debug(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
//        toast.show();
    }

    public void getListFromServer(){
        debug("GettingList");
        RestCommunicator rest = new RestCommunicator(this);
        rest.execute("GET");
    }

    public void sendListToServer(){
        debug("SendingList");
        RestCommunicator rest = new RestCommunicator(this);
        rest.execute("POST");
    }

    public void updateTotalBalance(){
        Double totalBalance = 0.0;
        for (Transaction item : transactions){
            Boolean direction = item.getDirection();
            Double amount = item.getValueAsDouble();
            if(direction==Boolean.TRUE) {
                totalBalance += amount;
            }else{
                totalBalance -= amount;
            }
        }

        TextView totalBalanceResult = findViewById(R.id.totalBalanceResult);
        String stringOfNumber = String.format (Locale.ENGLISH,"%.2f", abs(totalBalance));
        totalBalanceResult.setText(stringOfNumber);
        if(totalBalance>0) {
            totalBalanceResult.setTextColor(colorB);
        }else if(totalBalance<0){
            totalBalanceResult.setTextColor(colorA);
        }else{
            totalBalanceResult.setTextColor(colorText);
        }
    }

    public String transactionListToString(ArrayList<Transaction> trans){
        return new Gson().toJson(trans);
    }

    public ArrayList<Transaction> stringToTransactionList (String jsonString){
        return new Gson().fromJson(jsonString, new TypeToken<ArrayList<Transaction>>(){}.getType());
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TRANSACTIONS_KEY, transactionListToString(transactions));
        editor.apply();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(TRANSACTIONS_KEY, transactionListToString(transactions));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void dismissItem() {
        sendListToServer();
        updateTotalBalance();
    }
}
