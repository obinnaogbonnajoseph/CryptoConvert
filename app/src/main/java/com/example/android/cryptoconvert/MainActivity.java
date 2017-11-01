package com.example.android.cryptoconvert;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends AppCompatActivity implements CurrencyAdapter.CurrencyAdapterOnClickHandler {
    private CurrencyAdapter adapter;
    private List<Currency> currencies;
    private TextView mErrorMessageDisplay;
    private RecyclerView recyclerView;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
        swipeAndRefresh();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        currencies = new ArrayList<>();
        adapter = new CurrencyAdapter(currencies, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        try {
            Glide.with(this).load(R.drawable.bit_ether_theme3).into((ImageView)
                    findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(checkNetworkConnectivity()) {
            jsonVolleyRequest(myUrl());
        } else {
            mErrorMessageDisplay.setText(R.string.no_internet_connection);
            showErrorMessage();
        }

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar currencyName on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the currencyName when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(String[] infoSend) {
        Context context = this;
        Class destinationClass = CurrencyConverter.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(Intent.EXTRA_TEXT,infoSend);
        startActivity(intent);
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;


        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if(includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.
                applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Builds url
     * @return string which is the url
     */
    private String myUrl() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("https")
        .authority("min-api.cryptocompare.com")
        .appendPath("data")
        .appendPath("pricemulti")
        .appendQueryParameter("fsyms","BTC,ETH")
        .appendQueryParameter("tsyms","NGN,USD,EUR,JPY,GBP,AUD,CHF,CAD,CNY,SEK,MXN,NZD,SGD,HKD," +
                "NOK,KRW,TRY,INR,RUB,BRL").build();

        return uri.toString();
    }

    /**
     * Checks if there is internet access
     */
    private Boolean checkNetworkConnectivity() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, return true, else return false
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * A method that prepares the volley request using the Singleton class created
     * @param url is the JSON url
     */
    public void jsonVolleyRequest(String url){
        showLoadingIndicator();
        // Create the request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Try to parse the JSON response string. A try catch block is used
                        // to catch exceptions if any.
                        ArrayList<String> currencyName = new ArrayList<>();
                        ArrayList<String> currencySymbol = new ArrayList<>();
                        ArrayList<String> currencyLetSymbol = new ArrayList<>();

                        currencyName.addAll(Arrays.asList("Naira","U.S. Dollar","Euro",
                                "Japanese Yen","British Pound","Australian Dollar","Swiss Franc",
                                "Canadian Dollar","Chinese Yuan Renminbi","Swedish Krona",
                                "Mexican Peso", "New Zealand Dollar","Singapore Dollar",
                                "Hong Kong Dollar", "Norwegian Krone","South Korean Won",
                                "Turkish Lira","Indian Ruppee","Russian Ruble","Brazilian Real"));

                        currencySymbol.addAll(Arrays.asList("\u20a6","\u0024","\u20ac","\u00a5",
                                "\u00a3","\u0024","\u20a3","\u0024","\u00a5", "\u006b","\u20b1",
                                "\u0024","\u0024","\u0024","\u0072","\u20a9","\u00a3","\u20a8",
                                "\u20bd","\u0052"));

                        currencyLetSymbol.addAll(Arrays.asList("NGN","USD","EUR","JPY","GBP","AUD",
                                "CHF","CAD","CNY","SEK","MXN","NZD","SGD","HKD","NOK","KRW","TRY",
                                "INR","RUB","BRL"));
                        try {

                            // Extract the JSONArray associated with the key called "items"
                            // which represents a list of developers.
                            JSONObject bitcoinObject = response.getJSONObject("BTC");
                            JSONObject ethereumObject = response.getJSONObject("ETH");
                            for (int i = 0; i < currencyLetSymbol.size(); i++) {

                                String btcPrice = currencySymbol.get(i) + " " +
                                        twoDecimalPlaces(bitcoinObject.getDouble
                                                (currencyLetSymbol.get(i)));

                                String ethPrice = currencySymbol.get(i) + " " +
                                        twoDecimalPlaces(ethereumObject.getDouble
                                                (currencyLetSymbol.get(i)));

                                String curLetSymbol = currencyLetSymbol.get(i);

                                Currency currency = new Currency(currencyName.get(i),
                                        currencySymbol.get(i),curLetSymbol,btcPrice,ethPrice);

                                currencies.add(currency);
                            }
                            adapter.notifyDataSetChanged();
                            showDataView();
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If error results log the error and display an error message
                        mErrorMessageDisplay.setText(R.string.some_error);
                        showErrorMessage();
                        error.printStackTrace();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    /**
     * Shows the error message, when an error occurs
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        recyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        /* Also hide the progress bar */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * This method shows the progress bar while loading
     */
    private void showLoadingIndicator() {
        recyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Show the loaded data and hide other displays
     */
    private void showDataView() {
        /* First hide the error message and loading indicator if visible*/
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Then show the data */
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Performs the swipe and refresh feature
     */
    private void swipeAndRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        //Clear the recyclerView and its contents
        currencies.clear();
        adapter = new CurrencyAdapter(currencies, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        jsonVolleyRequest(myUrl());
        swipeRefreshLayout.setRefreshing(false);
    }

    private String twoDecimalPlaces(double result) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        return df2.format(result);
    }

}
