package ru.ulstu.datamodel.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseExtended<E> extends ResponseEntity<Object> {

    public ResponseExtended(ErrorConstants error, E errorData) {
        super(new ControllerResponse<Void, E>(new ControllerResponseError<E>(error, errorData)), HttpStatus.OK);
    }
}
