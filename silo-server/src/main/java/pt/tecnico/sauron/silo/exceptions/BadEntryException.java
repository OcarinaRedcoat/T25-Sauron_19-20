package pt.tecnico.sauron.silo.exceptions;

public class BadEntryException extends Exception{

    public BadEntryException(String erroMessage){
        super(erroMessage);
    }
}
