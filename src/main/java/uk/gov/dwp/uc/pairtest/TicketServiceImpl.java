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
    public static final int MAXIMUM_TICKET_TO_PURCHASE_PER_TRANSACTION = 20;
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
        //Building Ticket summary
        for (TicketTypeRequest ticket : ticketTypeRequests) {
            ticketSummary.put(ticket.getTicketType().name(), ticket.getNoOfTickets());

        }


        if (accountId < 1) {
            throw new InvalidPurchaseException();
        }
        //Calculating total number of tickets
        totalNoOfTickets = getTotalNoOfTickets(ticketTypeRequests, ticketSummary);
        if (totalNoOfTickets > MAXIMUM_TICKET_TO_PURCHASE_PER_TRANSACTION) {
            throw new InvalidPurchaseException();
        }



        //Validate children guardian(Adult present) at the cinema
        if (isAdultNotWithMinors(ticketSummary)) {
            throw new InvalidPurchaseException();
        }

        //calculating the correct amount to be sent to payment service
        totalAmount = getTotalAmount(ticketTypeRequests, ticketSummary);

        //Calculating the number of seat to be assigned based on the business rules provided

        totalSeat = getTotalSeat(ticketSummary);


        ticketPaymentService.makePayment(accountId, totalAmount);//Assumed that this service does not fail
        seatReservationService.reserveSeat(accountId, totalSeat);//Assumes that this service does not fail

    }

    private static int getTotalNoOfTickets(TicketTypeRequest[] ticketTypeRequests, Map<String, Integer> ticketSummary) {
        int totalNoOfTickets;

        totalNoOfTickets = ticketSummary.values().stream().reduce(0, (sum, noOfTickets) -> sum + noOfTickets);
        return totalNoOfTickets;
    }

    private static int getTotalAmount(TicketTypeRequest[] ticketTypeRequests, Map<String, Integer> ticketSummary) {
        int totalAmount;
        List<Integer> priceList = new ArrayList<>();

        for (TicketTypeRequest ticketRequest : ticketTypeRequests) {
            String type = ticketRequest.getTicketType().name();

            Integer ticketNo = ticketSummary.get(type);

            Integer amountPerType = ticketNo * PriceUnitUtil.getPriceUnits().get(type);
            priceList.add(amountPerType);

        }
        totalAmount = priceList.stream().reduce(0, Integer::sum);
        return totalAmount;
    }

    private static boolean isAdultNotWithMinors(Map<String, Integer> ticketSummary) {
        return (!ticketSummary.containsKey(TicketTypeRequest.Type.ADULT.name()) || ticketSummary
                .get(TicketTypeRequest.Type.ADULT.name()) < 1);
    }

    private  int getTotalSeat(Map<String, Integer> ticketSummary) {
        int totalSeat;
        Map<String,Integer> seatSummary = new HashMap<>();


        for(Map.Entry<String,Integer> entry : ticketSummary.entrySet()){
            if (!entry.getKey().equals(TicketTypeRequest.Type.INFANT.name())) {
                seatSummary.put(entry.getKey(),entry.getValue());
            }
        }

        totalSeat = seatSummary.values().stream().reduce(0, Integer::sum);
        return totalSeat;
    }

}
