package ru.ulstu.datamodel.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response<D> extends ResponseEntity<Object> {

    public Response(D data) {
        super(new ControllerResponse<D, Void>(data), HttpStatus.OK);
    }

    public Response(ErrorConstants error) {
        super(new ControllerResponse<Void, Void>(new ControllerResponseError<>(error, null)), HttpStatus.OK);
    }
}
