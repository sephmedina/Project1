package CS143B.josepdm1.Exceptions;

public class PCBException extends Exception {
    private String message;
    public PCBException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error with handling a PCB: " + message;
    }
}
