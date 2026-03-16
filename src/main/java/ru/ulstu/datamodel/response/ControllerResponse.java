package ru.ulstu.datamodel.response;

class ControllerResponse<D, E> {
    private final D data;
    private final ControllerResponseError<E> error;

    ControllerResponse(D data) {
        this.data = data;
        this.error = null;
    }

    ControllerResponse(ControllerResponseError<E> error) {
        this.data = null;
        this.error = error;
    }

    public D getData() {
        return data;
    }

    public ControllerResponseError<E> getError() {
        return error;
    }
}
