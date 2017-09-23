package gr.bcw.business_card_wallet.webservice;

import java.util.List;

import gr.bcw.business_card_wallet.model.Profession;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 19/9/2017.
 */

public interface ProfessionWebService extends WebService {

    String PROFESSION = "profession";

    List<Profession> searchByName(String name) throws WebServiceException;

}
