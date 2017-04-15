package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

/**
 * Created by Amr on 15/04/17.
 */

public class StockHawkAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(QuoteSyncJob.ACTION_DATA_UPDATED)) {
            ComponentName componentName = new ComponentName(context, StockHawkAppWidgetProvider.class);
            int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            Intent intent = new Intent(context, StockHawkService.class);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
