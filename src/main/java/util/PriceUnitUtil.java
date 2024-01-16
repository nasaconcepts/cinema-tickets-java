package util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.HashMap;
import java.util.Map;

public class PriceUnitUtil {
    public static Map<String,Integer> getPriceUnits(){
        Map<String,Integer> priceList = new HashMap<>();
        priceList.put(TicketTypeRequest.Type.INFANT.name(), 0);
        priceList.put(TicketTypeRequest.Type.CHILD.name(),  10);
        priceList.put(TicketTypeRequest.Type.ADULT.name(), 20);
        return priceList;
    }
}
