package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Amr on 15/04/17.
 */

@SuppressWarnings("WeakerAccess")
public class StockHawkRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private Context mContext;
    private Cursor mCursor;

    public StockHawkRemoteViewsFactory(Context context) {
        this.mContext = context;
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        mCursor = mContext.getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, null, null);
    }


    @Override
    public RemoteViews getViewAt(int position) {

        mCursor.moveToPosition(position);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        rv.setTextViewText(R.id.symbol, mCursor.getString(Contract.Quote.POSITION_SYMBOL));
        rv.setTextViewText(R.id.price, dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));

        Intent openStockHawkIntent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, openStockHawkIntent, 0);
        rv.setOnClickPendingIntent(R.id.list_item_quote_container, pendingIntent);

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);


        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            rv.setTextViewText(R.id.change, change);
        } else {
            rv.setTextViewText(R.id.change, percentage);
        }

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
