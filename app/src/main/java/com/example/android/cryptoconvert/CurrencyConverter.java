package com.example.android.cryptoconvert;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;


public class CurrencyConverter extends AppCompatActivity {
    TextView letSymbol1, letSymbol2;
    EditText btcEditText, ethEditText, btcValueToBaseValue, ethValueToBaseValue;
    private double defaultEthValueInLocalCurrency, defaultBtcValueInLocalCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set up the Action bar for the home button
        ActionBar actionBar = this.getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        letSymbol1 = (TextView) findViewById(R.id.currency_text);
        letSymbol2 = (TextView) findViewById(R.id.currency_text2);
        btcEditText = (EditText) findViewById(R.id.bitcoin_edit_text);
        ethEditText = (EditText) findViewById(R.id.ethereum_edit_text);
        btcValueToBaseValue = (EditText) findViewById(R.id.currency_edit_text);
        ethValueToBaseValue = (EditText) findViewById(R.id.currency_edit_text2);

        Intent intent = getIntent();
        String[] currencyInfo = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
        String btcValue = currencyInfo[0].substring(currencyInfo[0].indexOf(" ")+1);
        String ethValue = currencyInfo[1].substring(currencyInfo[1].indexOf(" ")+1);
        String curLetSymbol = currencyInfo[2];

        letSymbol1.setText(curLetSymbol);
        letSymbol2.setText(curLetSymbol);

        btcValueToBaseValue.setText(btcValue);
        ethValueToBaseValue.setText(ethValue);

        btcEditText.setText(R.string.default_value);
        ethEditText.setText(R.string.default_value);

        // Set the default values
        defaultBtcValueInLocalCurrency = Double.valueOf(btcValue);
        defaultEthValueInLocalCurrency = Double.valueOf(ethValue);


        // Create EditorListeners
        btcEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Convert from btc to local currency
                    String value = convertBtcToLocalCurrency();
                    btcValueToBaseValue.clearComposingText();
                    btcValueToBaseValue.setText(value);
                    return true;
                }
                return false;
            }
        });

        ethEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Convert from eth to local currency
                    String value = convertEthToLocalCurrency();
                    ethValueToBaseValue.clearComposingText();
                    ethValueToBaseValue.setText(value);
                    return true;
                }
                return false;
            }
        });

        btcValueToBaseValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Convert from local currency to btc
                    String value = convertToBtc();
                    btcEditText.clearComposingText();
                    btcEditText.setText(value);
                    return true;
                }
                return false;
            }
        });

        ethValueToBaseValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Convert from btc to local currency
                    String value = convertToEth();
                    ethEditText.clearComposingText();
                    ethEditText.setText(value);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Converts from the base currency to Btc
     */
    private String convertToBtc() {
        // Formula is:
        // input * (1 / defaultBtcValueInLocalCurrency )
        // Convert to String
        double input = Double.valueOf(btcValueToBaseValue.getText().toString());
        double result = input * ( 1 / defaultBtcValueInLocalCurrency );
        return returnTwoDecimalPlaces(result);
    }

    /**
     * Converts from base currency to Eth
     */
    private String convertToEth() {
        // Formula is:
        // input * (1 / defaultEthValueInLocalCurrency)
        // Convert to String
        double input = Double.valueOf(ethValueToBaseValue.getText().toString());
        double result = input * ( 1 / defaultEthValueInLocalCurrency );
        return returnTwoDecimalPlaces(result);
    }

    private String convertBtcToLocalCurrency() {
        // Formula is:
        //  input * defaultBtcValueInLocalCurrency
        double input = Double.valueOf(btcEditText.getText().toString());
        double result = input * defaultBtcValueInLocalCurrency;
        return returnTwoDecimalPlaces(result);
    }

    private String convertEthToLocalCurrency() {
        // Formula is:
        //  input * defaultBtcValueInLocalCurrency
        double input = Double.valueOf(ethEditText.getText().toString());
        double result = input * defaultEthValueInLocalCurrency;
        return returnTwoDecimalPlaces(result);
    }

    private String returnTwoDecimalPlaces(double result) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        df2.setGroupingUsed(true);
        df2.setGroupingSize(3);
        return df2.format(result);
    }

}
