package pt.tecnico.sauron.silo.exceptions;

public class BadEntryException extends Exception{

    public BadEntryException(ErrorMessage error){
        super(error.toString());
    }

}
