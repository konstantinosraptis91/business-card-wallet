package gr.bcw.business_card_wallet.webservice;

import java.util.List;

import gr.bcw.business_card_wallet.model.Company;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 19/9/2017.
 */

public interface CompanyWebService extends WebService {

    String COMPANY = "company";

    List<Company> findByName(String name) throws WebServiceException;

}
