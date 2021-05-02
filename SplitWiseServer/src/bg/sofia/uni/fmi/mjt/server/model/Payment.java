package bg.sofia.uni.fmi.mjt.server.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Payment implements Serializable {
    private static final long serialVersionUID = 8817442160329736343L;

    private final String sender;
    private final String receiver;
    private BigDecimal amount;

    public Payment(String sender, String receiver, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void addAmount(BigDecimal amountToAdd) {
        amount = amount.add(amountToAdd);
    }

    public void subtractAmount(BigDecimal amountToSubtract) {
        amount = amount.subtract(amountToSubtract);
    }

}
