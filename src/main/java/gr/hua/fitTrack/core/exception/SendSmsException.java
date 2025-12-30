package gr.hua.fitTrack.core.exception;

public class SendSmsException extends RuntimeException{
    public SendSmsException(String content){
        super(content);
    }

    public SendSmsException(String content, Throwable cause){
        super(content, cause);
    }
}
