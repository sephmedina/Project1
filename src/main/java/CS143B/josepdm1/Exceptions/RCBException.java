package CS143B.josepdm1.Exceptions;

public class RCBException extends Exception {
    private String message;
    public RCBException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error with handling an RCB: " + message;
    }
}
