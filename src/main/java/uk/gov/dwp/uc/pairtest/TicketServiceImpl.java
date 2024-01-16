package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import util.PriceUnitUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        totalSeat = getTotalSeat(ticketSummary);

        //Validate children guardian(Adult present) at the cinema
        if(isAdultNotWithMinors(ticketSummary)){
            throw new InvalidPurchaseException();
        }

        //calculating the correct amount to be sent to payment service
        totalAmount = getTotalAmount(ticketTypeRequests, ticketSummary);


        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeat);

    }

    private static int getTotalAmount(TicketTypeRequest[] ticketTypeRequests, Map<String, Integer> ticketSummary) {
        int totalAmount;
        List<Integer> priceList = new ArrayList<>();
        for(TicketTypeRequest ticketRequest: ticketTypeRequests){
            String type = ticketRequest.getTicketType().name();
            int ticketNo = ticketSummary.get(type);
            int amountPerType = ticketNo* PriceUnitUtil.getPriceUnits().get(type);
            priceList.add(amountPerType);

        }
        totalAmount = priceList.stream().reduce(0,Integer::sum);
        return totalAmount;
    }

    private static boolean isAdultNotWithMinors(Map<String, Integer> ticketSummary) {
       return (!ticketSummary.containsKey(TicketTypeRequest.Type.ADULT.name()) || ticketSummary
                .get(TicketTypeRequest.Type.ADULT.name()) < 1) ;
    }

    private static int getTotalSeat(Map<String, Integer> ticketSummary) {
        int totalSeat;
        Map<String, Integer> seatSummary;
        seatSummary = ticketSummary;

        if (seatSummary.containsKey(TicketTypeRequest.Type.INFANT.name())) {
            seatSummary.remove(TicketTypeRequest.Type.INFANT.name());
        }

        totalSeat = seatSummary.values().stream().reduce(0, Integer::sum);
        return totalSeat;
    }

}
