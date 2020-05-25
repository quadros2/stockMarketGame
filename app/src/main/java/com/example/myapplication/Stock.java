package com.example.myapplication;

public class Stock {

    private String dateAndTime;

    private String priceBought;

    private String type;

    private String quantity;

    private String name;

    private String symbol;

    Stock(String setDateAndTime, String setPriceBought, String setType, String setQuantity, String setName,
          String setSymbol) {
        dateAndTime = setDateAndTime;
        priceBought = setPriceBought;
        type = setType;
        quantity = setQuantity;
        name = setName;
        symbol = setSymbol;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getPriceBought() {
        return priceBought;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
