package bg.sofia.uni.fmi.mjt.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

public class GroupTest {
    private Set<String> members;
    private Group group;

    @Before
    public void initialize() {
        members = Set.of("Member 1", "Member 2", "Member 3", "Member 4");
        group = new Group("Group 1", members);
    }

    @Test
    public void testSplitAmountWithNoPreviousPayments() {
        User user = new User("Member 1", "password");

        group.splitAmount(user, BigDecimal.valueOf(10));

        Set<Payment> payments = group.getPayments();
        int expectedPayments = 3;
        assertEquals(expectedPayments, payments.size());

        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(2.50);
        String expectedReceiver = "Member 1";
        for (Payment payment : payments) {
            assertEquals(expectedReceiver, payment.getReceiver());
            assertEquals(0, payment.getAmount().compareTo(expectedPaymentAmount));
        }
    }

    @Test
    public void testSplitAmountWithExistingPreviousPayments() {
        User user = new User("Member 1", "password");

        group.splitAmount(user, BigDecimal.valueOf(10));
        group.splitAmount(user, BigDecimal.valueOf(10));

        Set<Payment> payments = group.getPayments();
        int expectedPayments = 3;
        assertEquals(expectedPayments, payments.size());

        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(5.0);
        String expectedReceiver = "Member 1";
        for (Payment payment : payments) {
            assertEquals(expectedReceiver, payment.getReceiver());
            assertEquals(0, payment.getAmount().compareTo(expectedPaymentAmount));
        }
    }

    @Test
    public void testSplitAmountWithTwoDifferentUsersSplitsAndExpectPaymentSwap() {
        User firstUser = new User("Member 1", "pass");
        User secondUser = new User("Member 2", "pass");

        group.splitAmount(firstUser, BigDecimal.valueOf(10));
        group.splitAmount(secondUser, BigDecimal.valueOf(20));

        Set<Payment> payments = group.getPayments();
        int expectedPayments = 5;
        assertEquals(expectedPayments, payments.size());

        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(2.5);
        boolean paymentExists = false;

        for (Payment payment : payments) {
            if (payment.getSender().equals(firstUser.getUsername())
                    && payment.getReceiver().equals(secondUser.getUsername())) {
                paymentExists = true;
                assertEquals(0, payment.getAmount().compareTo(expectedPaymentAmount));
            }
        }

        assertTrue(paymentExists);
    }

    @Test
    public void testSplitAmountWithDifferentUserSplits() {
        User firstUser = new User("Member 1", "pass");
        User secondUser = new User("Member 2", "pass");

        group.splitAmount(firstUser, BigDecimal.valueOf(20));
        group.splitAmount(secondUser, BigDecimal.valueOf(10));

        Set<Payment> payments = group.getPayments();
        int expectedPayments = 5;
        assertEquals(expectedPayments, payments.size());

        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(2.5);
        boolean paymentExists = false;

        for (Payment payment : payments) {
            if (payment.getSender().equals(secondUser.getUsername())
                    && payment.getReceiver().equals(firstUser.getUsername())) {
                paymentExists = true;
                assertEquals(0, payment.getAmount().compareTo(expectedPaymentAmount));
            }
        }

        assertTrue(paymentExists);
    }

}