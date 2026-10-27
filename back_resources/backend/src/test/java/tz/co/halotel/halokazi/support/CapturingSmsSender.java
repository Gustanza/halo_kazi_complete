package tz.co.halotel.halokazi.support;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tz.co.halotel.halokazi.sms.SmsSender;

/** Test SMS gateway: records every message so tests can read the OTP, and can be told to fail. */
public class CapturingSmsSender implements SmsSender {

    private static final Pattern CODE = Pattern.compile("code is (\\d{6})");

    public record Sent(String phone, String text) {
        public String code() {
            Matcher m = CODE.matcher(text);
            if (!m.find()) {
                throw new AssertionError("No OTP code in SMS: " + text);
            }
            return m.group(1);
        }
    }

    private final List<Sent> sent = new CopyOnWriteArrayList<>();
    private volatile boolean failing;

    @Override
    public String send(String phone, String text) {
        if (failing) {
            throw new IllegalStateException("simulated gateway outage");
        }
        sent.add(new Sent(phone, text));
        return "test-" + UUID.randomUUID();
    }

    public List<Sent> sentTo(String phone) {
        return sent.stream().filter(s -> s.phone().equals(phone)).toList();
    }

    public String lastCodeFor(String phone) {
        List<Sent> messages = sentTo(phone);
        if (messages.isEmpty()) {
            throw new AssertionError("No SMS was sent to " + phone);
        }
        return messages.getLast().code();
    }

    public List<Sent> all() {
        return List.copyOf(sent);
    }

    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    public void reset() {
        sent.clear();
        failing = false;
    }
}
