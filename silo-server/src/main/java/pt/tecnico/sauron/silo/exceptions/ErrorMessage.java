package pt.tecnico.sauron.silo.exceptions;

public enum ErrorMessage {

    EMPTY_INPUT("Input cannot be empty."),

    NO_ID_MATCH("No id match the expression."),

    ID_FOUND_WRONG_TYPE("Id was found, but wrong type. Try switch the type."),
    ID_NOT_FOUND("Person or Car does not exist, Id not found."),
    ID_NOT_VALID("Person or Car Id not valid."),

    CAM_NAME_ALREADY_EXIST("Camera name already exist in the system with other coords, change name please."),
    CAM_NAME_NOT_VALID("Camera name is not valid, it must be between 3 and 15, and alfa numeric characters."),
    CAM_COORDS_NOT_VALID("Camera coordinates must be floats.");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }

}
