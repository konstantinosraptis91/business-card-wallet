package gr.bcw.business_card_wallet.webservice;

import java.util.List;

import gr.bcw.business_card_wallet.model.WalletEntry;
import gr.bcw.business_card_wallet.model.retriever.BusinessCardResponse;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 5/5/2017.
 */

public interface WalletEntryWebService extends WebService {

    String WALLET_ENTRY = "walletentry";

    BusinessCardResponse saveWalletEntry(WalletEntry entry, String token) throws WebServiceException;

    List<BusinessCardResponse> getWallet(long id, String token) throws WebServiceException;

    void deleteWalletEntry(WalletEntry entry, String token) throws WebServiceException;

}
