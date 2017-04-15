package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.models.StockHistory;
import com.udacity.stockhawk.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amr on 09/04/17.
 */

public class StockDetailsActivity extends AppCompatActivity {

    @BindView(R.id.stock_history_chart)
    LineChart mLineChart;

    @BindInt(R.integer.chartx_labels_count)
    int mChartXLabelsCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);

        String stockSymbol = getSentStockSymbol();
        setupToolbar(stockSymbol);
        List<StockHistory> stockHistory = getStockHistory(stockSymbol);
        if (stockHistory != null && stockHistory.size() > 0) {
            showStockHistory(stockHistory);
        } else {
            mLineChart.setNoDataText(getString(R.string.no_history_msg));
        }
    }

    @Nullable
    private List<StockHistory> getStockHistory(String stockSymbol) {
        Cursor cursor = getQueryCursor(stockSymbol);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            String historyString = cursor.getString(Contract.Quote.POSITION_HISTORY);
            cursor.close();
            return getListOfHistoryEntries(historyString);
        }
        return null;
    }

    private List<StockHistory> getListOfHistoryEntries(String historyString) {
        List<StockHistory> historyList = new ArrayList<>();
        String[] lines = historyString.split("\n");
        for (String line : lines) {
            String[] pair = line.split(",");
            historyList.add(new StockHistory(Long.parseLong(pair[0].trim()),
                    Float.parseFloat(pair[1].trim())));
        }
        return historyList;
    }

    private void showStockHistory(@NonNull List<StockHistory> stockHistoryList) {
        List<Entry> chartEntries = new ArrayList<>();
        for (StockHistory stockHistory : stockHistoryList) {
            chartEntries.add(new Entry(stockHistory.getTimeStampInMillis(), stockHistory.getValue()));
        }
        LineDataSet lineDataSet = setUpLineDataSet(chartEntries);

        mLineChart.getDescription().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(mChartXLabelsCount);
        xAxis.setValueFormatter(getValueFormatter());

        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    @NonNull
    private IAxisValueFormatter getValueFormatter() {
        return new IAxisValueFormatter() {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                cal.setTimeInMillis((long) (value * 1000));
                return DateFormat.format(getString(R.string.date_format), cal).toString();
            }
        };
    }

    private LineDataSet setUpLineDataSet(List<Entry> chartEntries) {
        LineDataSet lineDataSet = new LineDataSet(chartEntries, getString(R.string.stock_value_label));
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        lineDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.colorAccent));
        lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        return lineDataSet;
    }

    private Cursor getQueryCursor(String stockSymbol) {
        String selection = Contract.Quote.COLUMN_SYMBOL + " = \'" + stockSymbol + "\'";
        return getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                selection, null, null, null);
    }

    private void setupToolbar(String stockSymbol) {
        getSupportActionBar().setTitle(stockSymbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String getSentStockSymbol() {
        return getIntent().getStringExtra(Constants.IntentExtras.STOCK_SYMBOL);
    }
}
