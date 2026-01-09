package gr.hua.fitTrack.core.exception;

public class MaxActiveAppointmentsExceededException extends RuntimeException {

    public MaxActiveAppointmentsExceededException(String message) {
        super(message);
    }
}

