package bg.sofia.uni.fmi.mjt.server.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class FriendshipTest {
    private String firstUser;
    private String secondUser;
    private Friendship friendship;

    @Before
    public void initialize() {
        firstUser = "First User";
        secondUser = "Second User";
        friendship = new Friendship("Friendship", firstUser, secondUser);
    }

    @Test
    public void testAddPaymentWithNoPreviousPayments() {
        friendship.addPayment(new Payment(firstUser, secondUser, BigDecimal.valueOf(10.0)));

        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(10.0);

        assertEquals(0, expectedPaymentAmount.compareTo(friendship.getPayment().getAmount()));
    }

    @Test
    public void testAddPaymentWthExistingPreviousPaymentsAndNoPaymentSwapButSameSender() {
        friendship.addPayment(new Payment(firstUser, secondUser, BigDecimal.valueOf(10.0)));
        friendship.addPayment(new Payment(firstUser, secondUser, BigDecimal.valueOf(10.0)));

        String expectedPaymentSender = "First User";
        String expectedPaymentReceiver = "Second User";
        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(20.0);

        assertEquals(expectedPaymentSender, friendship.getPayment().getSender());
        assertEquals(expectedPaymentReceiver, friendship.getPayment().getReceiver());
        assertEquals(0, expectedPaymentAmount.compareTo(friendship.getPayment().getAmount()));

    }

    @Test
    public void testAddPaymentWthExistingPreviousPaymentsAndNoPaymentSwapButDifferentSender() {
        friendship.addPayment(new Payment(firstUser, secondUser, BigDecimal.valueOf(10.0)));
        friendship.addPayment(new Payment(secondUser, firstUser, BigDecimal.valueOf(7.0)));

        String expectedPaymentSender = "First User";
        String expectedPaymentReceiver = "Second User";
        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(3.0);

        assertEquals(expectedPaymentSender, friendship.getPayment().getSender());
        assertEquals(expectedPaymentReceiver, friendship.getPayment().getReceiver());
        assertEquals(0, expectedPaymentAmount.compareTo(friendship.getPayment().getAmount()));

    }

    @Test
    public void testAddPaymentWthExistingPreviousPaymentsAndPaymentSwap() {
        friendship.addPayment(new Payment(firstUser, secondUser, BigDecimal.valueOf(10.0)));
        friendship.addPayment(new Payment(secondUser, firstUser, BigDecimal.valueOf(23.0)));

        String expectedPaymentSender = "Second User";
        String expectedPaymentReceiver = "First User";
        BigDecimal expectedPaymentAmount = BigDecimal.valueOf(13.0);

        assertEquals(expectedPaymentSender, friendship.getPayment().getSender());
        assertEquals(expectedPaymentReceiver, friendship.getPayment().getReceiver());
        assertEquals(0, expectedPaymentAmount.compareTo(friendship.getPayment().getAmount()));

    }

}
