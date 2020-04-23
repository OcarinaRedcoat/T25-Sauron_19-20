package pt.tecnico.sauron.silo.exceptions;

public class BadEntryException extends Exception{

    private final ErrorMessage errorMessage;

    public BadEntryException(ErrorMessage errorMessage){
        super(errorMessage.label);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

}
