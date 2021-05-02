package bg.sofia.uni.fmi.mjt.server.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Friendship extends AbstractGroup implements Serializable {
    private static final long serialVersionUID = -6956504697666550817L;

    private final String firstUser;
    private final String secondUser;
    private Payment payment;

    public Friendship(String name, String firstUser, String secondUser) {
        super(name);
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        payment = new Payment(firstUser, secondUser, new BigDecimal(0));
    }

    public Friendship(String name, String firstUser, String secondUser, Payment payment) {
        super(name);
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.payment = payment;
    }

    public String getFirstUser() {
        return firstUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public Payment getPayment() {
        return payment;
    }

    public void addPayment(Payment newPayment) {
        if (newPayment.getSender().equals(payment.getReceiver())) {
            BigDecimal oldAmount = payment.getAmount();
            BigDecimal newAmount = newPayment.getAmount();

            if (oldAmount.compareTo(newAmount) < 0) {
                newPayment.subtractAmount(oldAmount);
                payment = newPayment;
            } else {
                payment.subtractAmount(newAmount);
            }
        } else {
            payment.addAmount(newPayment.getAmount());
        }
    }
}
