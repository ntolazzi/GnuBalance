package de.quantumnetwork.gnubalance;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by nico on 01.10.17.
 */

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private ArrayList<Transaction> transactionList;
    private Context mContext;
    private SwipeListener mListener;

    public TransactionAdapter(ArrayList<Transaction> data, Context context, SwipeListener listener) {
        super(context, R.layout.item, data);
        transactionList = data;
        mContext = context;
        mListener = listener;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        Transaction transaction = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, container, false);
        }
        TextView name = convertView.findViewById(R.id.transaction_name);
        name.setText(transaction.getName());
        TextView value = convertView.findViewById(R.id.transaction_value);
        value.setText(transaction.getValue());
        if (transaction.getDirection()) {
            value.setTextColor(ContextCompat.getColor(getContext(), R.color.ButtonColorPersonB));
        } else {
            value.setTextColor(ContextCompat.getColor(getContext(), R.color.ButtonColorPersonA));
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            float initialX = Float.NaN;
            final int DELTA = 5;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (abs(event.getX() - initialX) > DELTA) {
                            transactionList.remove(position);
                            notifyDataSetChanged();
                            mListener.dismissItem();
                            return true;
                        }

                    default:
                        return true;
                }
            }
        });
        return convertView;
    }

}
