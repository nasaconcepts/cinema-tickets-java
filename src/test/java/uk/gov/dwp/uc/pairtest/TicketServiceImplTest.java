package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {
    @Mock
    TicketPaymentServiceImpl ticketPaymentService;
    @Mock
    SeatReservationServiceImpl seatReservationService;
    @InjectMocks
    TicketServiceImpl ticketService;

    /**
     * Business Requirements
     * # Business Rules
     *
     * - There are 3 types of tickets i.e. Infant, Child, and Adult.
     *
     * - The ticket prices are based on the type of ticket (see table below).
     *
     * - The ticket purchaser declares how many and what type of tickets they want to buy.
     *
     * - Multiple tickets can be purchased at any given time.
     *
     * - Only a maximum of 20 tickets that can be purchased at a time.
     *
     * - Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
     *
     * - Child and Infant tickets cannot be purchased without purchasing an Adult ticket.
     *
     * |   Ticket Type    |     Price   |
     *
     * | ---------------- | ----------- |
     *
     * |    INFANT        |    £0       |
     *
     * |    CHILD         |    £10      |
     *
     * |    ADULT         |    £20      |
     *
     */

    @Test

    public void testMultipleTicketPurchase(){
        // adopting TDD approach (RED -> GREEN -> REFACTOR
        fail("Initial Test resulting to fail -RED Status");
    }
}
