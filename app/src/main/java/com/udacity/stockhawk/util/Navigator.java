package com.udacity.stockhawk.util;

import android.content.Context;
import android.content.Intent;

import com.udacity.stockhawk.ui.StockDetailsActivity;

/**
 * Created by Amr on 09/04/17.
 */

public class Navigator {

    public static void openStockDetailsScreen(Context context, String symbol) {
        Intent intent = new Intent(context, StockDetailsActivity.class);
        intent.putExtra(Constants.IntentExtras.STOCK_SYMBOL, symbol);
        context.startActivity(intent);
    }
}
