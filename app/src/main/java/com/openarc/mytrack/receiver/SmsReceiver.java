package com.openarc.mytrack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.service.HttpService;

/**
 * Created by dinuka on 12/10/2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();

    Intent httpIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    // if the SMS is not from our gateway, ignore the message
                    if (!message.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())) {
                        Log.e(TAG, "SMS is not for our app!");
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);
                    Toast.makeText(context,"OTP received: " + verificationCode,Toast.LENGTH_LONG).show();

                    if (!message.toLowerCase().contains(Config.SMS_PASSWORD_RESET.toLowerCase())) {
                        Toast.makeText(context,"OTP received for verify password reset: " + verificationCode,Toast.LENGTH_LONG).show();
                        httpIntent = new Intent(context, HttpService.class);
                        httpIntent.putExtra("otp", verificationCode);
                        context.startService(httpIntent);
                    }

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Config.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
