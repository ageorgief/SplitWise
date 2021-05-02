package bg.sofia.uni.fmi.mjt.server.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Group extends AbstractGroup implements Serializable {
    private static final long serialVersionUID = 2777942872689816883L;
    private int x;
    private final Set<String> members;
    private final Set<Payment> payments;

    public int getX() {
        return x;
    }

    public Group(String name, Set<String> members) {
        super(name);
        this.members = members;

        payments = new HashSet<>();
    }

    public Group(String name, Set<String> members, Set<Payment> payments) {
        super(name);
        this.members = members;
        this.payments = payments;
    }

    public Set<String> getMembers() {
        return new HashSet<>(members);
    }

    public Set<Payment> getPayments() {
        return new HashSet<>(payments);
    }

    public void splitAmount(User user, BigDecimal amount) {
        String receiverUsername = user.getUsername();
        BigDecimal amountPerUser = amount.divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);
        Set<Payment> newPayments = generateNewPayments(receiverUsername, amountPerUser);
        addNewPayments(newPayments);
    }

    private Set<Payment> generateNewPayments(String receiverUsername, BigDecimal amount) {
        return members.stream()
                .filter(member -> !member.equals(receiverUsername))
                .map(member -> new Payment(member, receiverUsername, amount))
                .collect(Collectors.toSet());
    }

    private void addNewPayments(Set<Payment> newPayments) {
        Set<Payment> paymentsToAdd = new HashSet<>();

        for (Payment newPayment : newPayments) {
            boolean added = processNewPayment(newPayment, paymentsToAdd);

            if (!added) {
                paymentsToAdd.add(newPayment);
            }
        }

        payments.addAll(paymentsToAdd);
    }

    private boolean processNewPayment(Payment newPayment, Set<Payment> paymentsToAdd) {
        String newPaymentSender = newPayment.getSender();
        String newPaymentReceiver = newPayment.getReceiver();
        BigDecimal newPaymentAmount = newPayment.getAmount();

        for (Payment payment : payments) {
            String paymentSender = payment.getSender();
            String paymentReceiver = payment.getReceiver();

            if (newPaymentReceiver.equals(paymentSender) && newPaymentSender.equals(paymentReceiver)) {
                BigDecimal paymentAmount = payment.getAmount();

                if (paymentAmount.compareTo(newPaymentAmount) < 0) {
                    BigDecimal newAmount = newPaymentAmount.subtract(paymentAmount);
                    Payment paymentToAdd = new Payment(payment.getReceiver(), newPaymentReceiver, newAmount);
                    paymentsToAdd.add(paymentToAdd);
                    payments.remove(payment);
                } else {
                    payment.subtractAmount(newPaymentAmount);
                }
                return true;
            } else if (newPaymentReceiver.equals(paymentReceiver) && newPaymentSender.equals(paymentSender)) {
                payment.addAmount(newPaymentAmount);
                return true;
            }
        }

        return false;
    }
}
