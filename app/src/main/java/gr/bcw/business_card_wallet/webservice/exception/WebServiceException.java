package gr.bcw.business_card_wallet.webservice.exception;

/**
 * Created by konstantinos on 15/4/2017.
 */

public class WebServiceException extends Exception {

    public WebServiceException() {

    }

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(Throwable t) {
        super(t);
    }

    public WebServiceException(String message, Throwable t) {
        super(message, t);
    }

}
