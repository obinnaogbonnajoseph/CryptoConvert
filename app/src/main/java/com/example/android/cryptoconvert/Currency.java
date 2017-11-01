package com.example.android.cryptoconvert;

/**
 * Creates object instances for currencies
 */

class Currency {
    private String name;
    private String currSymbol;
    private String curLetSymbol;
    private String bitcoinPrice;
    private String ethereumPrice;

    Currency(String name, String currSymbol,String curLetSymbol, String bitcoinPrice,
             String ethereumPrice){
        this.name = name;
        this.currSymbol = currSymbol;
        this.curLetSymbol = curLetSymbol;
        this.bitcoinPrice = bitcoinPrice;
        this.ethereumPrice = ethereumPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getCurrSymbol() {
        return currSymbol;
    }

    String getBitcoinPrice() {return bitcoinPrice;}

    String getEthereumPrice() {return ethereumPrice;}

    String getCurLetSymbol() {return curLetSymbol;}
}