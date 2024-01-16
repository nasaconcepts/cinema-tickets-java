package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
     * <p>
     * - There are 3 types of tickets i.e. Infant, Child, and Adult.
     * <p>
     * - The ticket prices are based on the type of ticket (see table below).
     * <p>
     * - The ticket purchaser declares how many and what type of tickets they want to buy.
     * <p>
     * - Multiple tickets can be purchased at any given time.
     * <p>
     * - Only a maximum of 20 tickets that can be purchased at a time.
     * <p>
     * - Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
     * <p>
     * - Child and Infant tickets cannot be purchased without purchasing an Adult ticket.
     * <p>
     * |   Ticket Type    |     Price   |
     * <p>
     * | ---------------- | ----------- |
     * <p>
     * |    INFANT        |    £0       |
     * <p>
     * |    CHILD         |    £10      |
     * <p>
     * |    ADULT         |    £20      |
     */

    @Test

    public void testMultipleTicketPurchase() {
        // adopting TDD approach (RED -> GREEN -> REFACTOR
        //Given
        Long accountId =12345L;

        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,3);
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        //Act
        ticketService.purchaseTickets(accountId,infantRequest,childRequest,adultRequest);

        //Assert
        verify(ticketPaymentService).makePayment(anyLong(),anyInt());
        verify(seatReservationService).reserveSeat(anyLong(),anyInt());
    }
    @Test(expected = InvalidPurchaseException.class)
    public void testAccountIdLessThanOneIsInvalid(){
       // fail("Account less than 1 are invalid");
        Long accountId =0L;

        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,3);
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        ticketService.purchaseTickets(accountId,infantRequest,childRequest,adultRequest);

        //Assert
        verify(ticketPaymentService,never()).makePayment(anyLong(),anyInt());
        verify(seatReservationService,never()).reserveSeat(anyLong(),anyInt());
    }

    @Test
    public void testMaximumOf20TicketsAllowedPerPurchase(){
        fail("testing if maximum of 20 tickets can be purchased --RED Status");


    }
}
