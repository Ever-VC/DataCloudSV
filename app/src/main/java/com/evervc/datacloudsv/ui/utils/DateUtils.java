package com.evervc.datacloudsv.ui.utils;

import com.evervc.datacloudsv.models.AccountRegister;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getLastUpdatedMessage(AccountRegister account) {
        Long timestamp = (account.getModifiedAt() != null) ? account.getModifiedAt() : account.getCreatedAt();
        boolean wasModified = (account.getModifiedAt() != null);

        String prefix = wasModified ? "Modificado" : "Creado";
        return buildTimeAgoMessage(prefix, timestamp);
    }

    private static String buildTimeAgoMessage(String prefix, Long timestamp) {
        long now = System.currentTimeMillis();
        long diffMillis = now - timestamp;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffMillis);

        if (days > 5) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(timestamp));
            return prefix + " el " + dateStr;
        } else if (days >= 1) {
            return prefix + " hace " + days + " día" + (days > 1 ? "s" : "");
        } else if (hours >= 1) {
            return prefix + " hace " + hours + " hora" + (hours > 1 ? "s" : "");
        } else if (minutes >= 1) {
            return prefix + " hace " + minutes + " minuto" + (minutes > 1 ? "s" : "");
        } else {
            return prefix + " hace unos segundos";
        }
    }
}

