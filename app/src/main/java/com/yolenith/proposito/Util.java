package com.yolenith.proposito;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Util {

    public  Util(){

    }

    private String redondearDecimales(float numero){
        return String.format("%.0f", numero);
    }

    public String formatoMoneda(float numero) {
        String num = "0";
        NumberFormat formatoMoneda = new DecimalFormat("#0,000");
        if (numero >= 1000) {
            num = formatoMoneda.format(numero);
        } else {
            num = redondearDecimales(numero);
        }
        return num;
    }


}
