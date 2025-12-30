package gr.hua.fitTrack.core.exception;

public class PersonException extends RuntimeException{

    public PersonException(String content){
        super(content);
    }

    public PersonException(String content, Throwable cause){
        super(content, cause);
    }

}
