package springai.rag.poc.exception;

public class RowLimitExceededException extends RuntimeException{
    public RowLimitExceededException(String message){
        super(message);
    }
}
