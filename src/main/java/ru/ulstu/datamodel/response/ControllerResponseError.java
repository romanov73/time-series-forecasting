package ru.ulstu.datamodel.response;

class ControllerResponseError<D> {
    private final ErrorConstants description;
    private final D data;

    ControllerResponseError(ErrorConstants description, D data) {
        this.description = description;
        this.data = data;
    }

    public int getCode() {
        return description.getCode();
    }

    public String getMessage() {
        return description.getMessage();
    }

    public D getData() {
        return data;
    }
}
