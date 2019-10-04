package CS143B.josepdm1.Exceptions;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodes {
    public static final int TOO_MANY_PROCESSES = 1;
    private ErrorCodes() {}
    public static final Map<Integer, String> errors = new HashMap<Integer, String>();
    static {
        errors.put(TOO_MANY_PROCESSES, "More than N processes have been created");
    }
}
