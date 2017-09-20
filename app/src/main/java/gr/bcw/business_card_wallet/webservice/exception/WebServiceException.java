package gr.bcw.business_card_wallet.webservice.exception;

/**
 * Created by konstantinos on 15/4/2017.
 */

public class WebServiceException extends Exception {

    private int httpCode;

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public WebServiceException(Throwable t, int httpCode) {
        super(t);
        this.httpCode = httpCode;
    }

    public WebServiceException(String message, Throwable t, int httpCode) {
        super(message, t);
        this.httpCode = httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

}
