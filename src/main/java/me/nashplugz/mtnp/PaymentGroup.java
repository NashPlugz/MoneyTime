package me.nashplugz.mtnp;

public class PaymentGroup {

    private final String name;
    private final long interval;
    private final int amount;

    public PaymentGroup(String name, long interval, int amount) {
        this.name = name;
        this.interval = interval;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public long getInterval() {
        return interval;
    }

    public int getAmount() {
        return amount;
    }
}
