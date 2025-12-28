package gr.hua.fitTrack.core.exception;

public class SmsException extends Exception{
    public sendSmsException(String content){
        super(content);
    }
}
