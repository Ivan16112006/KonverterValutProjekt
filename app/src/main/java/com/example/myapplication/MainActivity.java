package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Document doc;
    private Thread secThread;
    private Runnable runnable;
    EditText inputv;
    TextView result;
    Spinner valut1;
    Spinner valut2;
    double[] zna;
    String[] ValutAr;
    double n;
    String a;
    double rub;
    double usd;
    double eur;
    double jpy;
    double gbp;
    double byn;
    double cny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initKurs();

// Создание ArrayAdapter, для присвоения  спиннерам значений
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.arrayValutes,
                android.R.layout.simple_spinner_item
        );
// Макет, который будет использоваться при отображении списка вариантов.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Примените адаптер к прядильщику.
        valut1.setAdapter(adapter);
        valut2.setAdapter(adapter);

        inputv.setOnKeyListener(new View.OnKeyListener() {
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                            if (inputv.getText().length() != 0) {
                                                result.setText(otv(n));
                                            } else {
                                                result.setText(String.valueOf(0));
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                }
        );


    }

    //Инициализация элементов активити
    public void init() {
        inputv = (EditText) findViewById(R.id.inputValut);
        result = (TextView) findViewById(R.id.otvet);
        valut1 = (Spinner) findViewById(R.id.spinnerfrom);
        valut2 = (Spinner) findViewById(R.id.spinnerto);

    }

    public void initKurs() {
        rub = 1; //рубль
        usd = 74.76; //доллор
        eur = 79.61; //евро
        jpy = 0.56; //японская иена
        gbp = 89.79; //фунт стерлингов
        byn = 77; //белорусский рубль
        cny = 10.84; //китайский юань
        zna = new double[]{rub, usd, eur, jpy, gbp, byn, cny};
        String rubs = "RUB";
        ValutAr = new String[]{"RUB", "USD", "EUR", "JPY", "GBP", "BYN", "CNY"};
        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }
    private void getWeb(){
        try {

            doc = Jsoup.connect("https://cbr.ru/currency_base/daily/").get();
            int j = 1;
            int[] stroki = {15, 16, 44, 39, 5, 24};
            for(int i:stroki){
                zna[j] =Double.parseDouble(doc.select("table.data > tbody > tr:nth-child("+i+") > td:nth-child(5)").text().replace(",", "."));
                j++;
            }
            zna[3] = zna[3]/100;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int index(String[] arr, String element) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == element) {
                return i;
            }

        }
        return -1;
    }

    String otv(double n){
        String valutaName1 = valut1.getSelectedItem().toString();
        String valutaName2 = valut2.getSelectedItem().toString();
        double znachValut1 = 0;
        double znachValut2 = 0;
        n = Double.parseDouble(String.valueOf(inputv.getText()));
        int i = 0;
        for (String a: ValutAr) {

            if (sravn(a, valutaName1)){
                znachValut1 = zna[i];

            }
            i++;
        }
        i = 0;
        for (String a: ValutAr) {
            if (sravn(a, valutaName2)){
                znachValut2 = zna[i];
            }
            i++;
        }
        double c = n*znachValut1/znachValut2;
        return String.format("%.2f", c);

    }
    boolean sravn(String a, String b){
        if (a.charAt(0) == b.charAt(0) && a.charAt(1) == b.charAt(1) && a.charAt(2) == b.charAt(2)) {
            return true;
        }
        return false;
    }
}
