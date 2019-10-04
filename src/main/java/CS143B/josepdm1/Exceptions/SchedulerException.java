package CS143B.josepdm1.Exceptions;

public class SchedulerException extends Exception {
    private String message;
    public SchedulerException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error with Scheduling: " + message;
    }
}
