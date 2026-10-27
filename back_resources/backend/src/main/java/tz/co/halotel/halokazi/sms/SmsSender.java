package tz.co.halotel.halokazi.sms;

/**
 * Transport to an SMS gateway. Swap the implementation by registering a different bean;
 * delivery bookkeeping (SMS_LOG) is handled by {@link SmsService}, not by implementations.
 */
public interface SmsSender {

    /**
     * @param phone E.164 recipient
     * @return the gateway's message id, if it returns one
     * @throws RuntimeException if the gateway rejected or could not take the message
     */
    String send(String phone, String text);
}
