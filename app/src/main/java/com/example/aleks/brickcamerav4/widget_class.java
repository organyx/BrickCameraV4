package com.example.aleks.brickcamerav4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aleks on 30-Sep-15.
 */
public class widget_class extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int i = 0; i < appWidgetIds.length; i++)
        {
            int currentWidgetId = appWidgetIds[i];

            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String timetext = df.format(new Date());
            timetext = currentWidgetId+ ")      " + timetext;

            Intent startAppIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startAppIntent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
            views.setOnClickPendingIntent(R.id.btnPending, pendingIntent);
            views.setTextViewText(R.id.tvUpdate, timetext);

            appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }
}
