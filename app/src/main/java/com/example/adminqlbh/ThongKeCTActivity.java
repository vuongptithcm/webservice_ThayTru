package com.example.adminqlbh;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adminqlbh.Api.ApiService;
import com.example.adminqlbh.Api.ApiUtils;
import com.example.adminqlbh.Models.ThongKeThang;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongKeCTActivity extends AppCompatActivity {

    private List<ThongKeThang> listTopSell;
    private TextView message;
    private PieChart pieChart;
    private Button btn_SelectMonth;
    private String monthYear;
    private ApiService apiService;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thongke);
        setControl();
        ActionBar();
        setEvent();
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setEvent() {
        btn_SelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMonth();
                Log.d("isSelectMonthDone", "onClick: " + monthYear);
            }
        });
    }

    public void setControl(){
        pieChart = findViewById(R.id.pieChart);
        btn_SelectMonth = findViewById(R.id.btn_selectMonth);
        message = findViewById(R.id.textView_message);
        toolbar=findViewById(R.id.toolbar);
    }


    public void fillPieChart(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 0; i < listTopSell.size(); i++){
            pieEntries.add(new PieEntry(listTopSell.get(i).getTong(), listTopSell.get(i).getId()));
        }


        ArrayList<Integer> colors = new ArrayList<Integer>();

        int red;
        int blue;
        int green;

        int color;
       for (int i = 0; i < listTopSell.size(); i++)
       {
           Random generator = new Random();

           red = generator.nextInt(256);
           blue = generator.nextInt(256);
           green = generator.nextInt(256);
           color = Color.rgb(red,blue,green);
           colors.add(color);


       }

//        dataSet.setColors(colors);


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "M?? H??ng h??a");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        // Format value PieEntry from float to int
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("S??? l?????ng b??n ra");

        Legend legend = pieChart.getLegend(); // get legend of pie
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        legend.setDrawInside(false); // set if legend should be drawn inside or not

        pieChart.setDrawEntryLabels(false); // To remove labels from piece of pie
        pieChart.animate();
    }

    public void SelectMonth(){
        Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        monthYear = String.valueOf(month + 1) + "-" + String.valueOf(year);
                        Log.d("Th??ng v?? n??m ", "onClick: " + monthYear);
                        getTopSellProctByMonth();
                    }
                }, nam, thang, ngay);
        // Show
        datePickerDialog.show();
    }

    // L???y top h??ng h??a b??n ch???y trong th??ng
    public void getTopSellProctByMonth(){
        apiService = ApiUtils.getApiService();
        apiService.getListTopSellByMonth(monthYear).enqueue(new Callback<List<ThongKeThang>>() {
            @Override
            public void onResponse(Call<List<ThongKeThang>> call, Response<List<ThongKeThang>> response) {
                if(response.isSuccessful()){
                    listTopSell = response.body();
                    Log.d("Top sp b??n ch???y ", "onResponse: " + listTopSell.toString());
                    if(listTopSell.isEmpty()){
                        // Kh??ng c?? d??? li???u
                        Toast.makeText(getApplicationContext(), "Th??ng " + monthYear + " ???" , Toast.LENGTH_LONG).show();
                        message.setText("Th??ng " + monthYear + " kh??ng b??n ???????c s???n ph???m n??o");
                        if(pieChart.getData() != null){
                            pieChart.getData().clearValues();
                            pieChart.clear();
                        }
                    }
                    else{
                        message.setText("Th??ng " + monthYear + " c?? thu nh???p");
                        Toast.makeText(getApplicationContext(), "B???m gi???a m??n h??nh ????? hi???n bi???u ????? ! ", Toast.LENGTH_LONG).show();
                        fillPieChart();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ThongKeThang>> call, Throwable t) {
                Log.d("L???i ", "onFailure: " + t.getMessage());
            }
        });
    }
}
