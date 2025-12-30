package gr.hua.fitTrack.core.port;

public interface SmsNotificationPort {

    boolean sendSms(final String e164, final String content);
}
