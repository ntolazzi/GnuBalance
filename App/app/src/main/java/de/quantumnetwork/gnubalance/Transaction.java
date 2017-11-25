package de.quantumnetwork.gnubalance;

import java.util.Locale;

/**
 * Created by nico on 01.10.17.
 */

public class Transaction {

    private String name;
    private Boolean direction;
    private Double value;

    public Transaction(String name, Boolean direction, Double value){
        this.name = name;
        this.direction = direction;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Boolean getDirection() {
        return direction;
    }

    public String getValue() {
        String stringOfNumber = String.format (Locale.ENGLISH,"%.2f", value);
        return stringOfNumber;
    }

    public Double getValueAsDouble() { return value;}


}
