package gr.bcw.business_card_wallet.webservice;

import javax.validation.constraints.NotNull;

import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 18/4/2017.
 */

public interface BusinessCardWebService extends WebService {

    String BUSINESS_CARD = "businesscard";

    BusinessCard createBusinessCard(@NotNull BusinessCard businessCard) throws WebServiceException;

}
