package com.example.android.cryptoconvert;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * This is an adapter that adapts the cardView to the
 * the recycler view
 */

class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {

    private List<Currency> currencyList;

    /*
    * An on-click handler that we've defined to make it easy for an Activity to interface with
    * our RecyclerView
    */
    private final CurrencyAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    interface CurrencyAdapterOnClickHandler {
        void onClick(String[] infoSend);
    }

    /**
     * Constructor for the adapter that accepts clickHandler.
     */
    CurrencyAdapter(List<Currency> currencyList, CurrencyAdapterOnClickHandler mClickHandler) {
        this.currencyList = currencyList;
        this.mClickHandler = mClickHandler;
    }



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView currencyName, symbol, bitcoin, ethereum;


        MyViewHolder(View view) {
            super(view);
            currencyName = (TextView) view.findViewById(R.id.currencyName);
            symbol = (TextView) view.findViewById(R.id.symbol);
            bitcoin = (TextView) view.findViewById(R.id.bitcoin);
            ethereum = (TextView) view.findViewById(R.id.ethereum);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Currency currentCurrency = currencyList.get(position);
            String btcPrice = currentCurrency.getBitcoinPrice();
            String ethPrice = currentCurrency.getEthereumPrice();
            String curLetSymbol = currentCurrency.getCurLetSymbol();

            String[] infoSend = {btcPrice, ethPrice, curLetSymbol};
            mClickHandler.onClick(infoSend);
        }
    }



    @Override
    public CurrencyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CurrencyAdapter.MyViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.currencyName.setText(currency.getName());
        holder.symbol.setText(currency.getCurrSymbol());
        String btcPrice = currency.getBitcoinPrice();
        String ethPrice = currency.getEthereumPrice();
        String currencySymbol = btcPrice.substring(0,1).trim();
        String PriceBtc = returnTwoDecimalPlaces(Double
                .valueOf(btcPrice.substring(btcPrice.indexOf(" ")+1)));
        String PriceEth = returnTwoDecimalPlaces(Double.valueOf(ethPrice
                .substring(ethPrice.indexOf(" ")+1)));
        String sumBtcValue = currencySymbol + " " + PriceBtc;
        String sumEthValue = currencySymbol + " " + PriceEth;
        holder.bitcoin.setText(sumBtcValue);
        holder.ethereum.setText(sumEthValue);
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    private String returnTwoDecimalPlaces(double result) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        df2.setGroupingUsed(true);
        df2.setGroupingSize(3);
        return df2.format(result);
    }

}
