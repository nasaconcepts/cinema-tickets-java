package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;
import java.util.Map;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private TicketPaymentServiceImpl ticketPaymentService;
    private SeatReservationServiceImpl seatReservationService;

    public TicketServiceImpl(TicketPaymentServiceImpl ticketPaymentService, SeatReservationServiceImpl seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalSeat = 4;
        int totalAmount = 30;
        int totalNoOfTickets = 20;
        Map<String, Integer> ticketSummary = new HashMap<>();
        Map<String, Integer> seatSummary = new HashMap<>();
        if (accountId < 1) {
            throw new InvalidPurchaseException();
        }
        //Calculating total number of tickets
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            ticketSummary.put(ticketTypeRequest.getTicketType().name(), ticketTypeRequest.getNoOfTickets());
        }
        totalNoOfTickets = ticketSummary.values().stream().reduce(0, (sum, noOfTickets) -> sum + noOfTickets);
        if (totalNoOfTickets > 20) {
            throw new InvalidPurchaseException();
        }

        //Calculating the number of seat to be assigned based on the business rules provided
        seatSummary = ticketSummary;
        if(ticketSummary.containsKey(TicketTypeRequest.Type.INFANT.name())){
            seatSummary.remove(TicketTypeRequest.Type.INFANT);
        }
        totalSeat = seatSummary.values().stream().reduce(0,(sum,noOfTickets)->sum+noOfTickets);




        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeat);

    }

}
