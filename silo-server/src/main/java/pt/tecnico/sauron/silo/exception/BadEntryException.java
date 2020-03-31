package pt.tecnico.sauron.silo.exception;

public class BadEntryException extends Exception {


    /** Bad bad entry specification. */
    private String _entrySpecification;

    /**
     * @param entrySpecification
     */
    public BadEntryException(String entrySpecification) {
        _entrySpecification = entrySpecification;
    }

    /**
     * @param entrySpecification
     * @param cause
     */
    public BadEntryException(String entrySpecification, Exception cause) {
        super(cause);
        _entrySpecification = entrySpecification;
    }

    /**
     * @return the bad entry specification.
     */
    public String getEntrySpecification() {
        return _entrySpecification;
    }

}