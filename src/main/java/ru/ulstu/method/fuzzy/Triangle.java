package ru.ulstu.method.fuzzy;

public class Triangle {
    private final static String FUZZY_LABEL_TEMPLATE = "X%s";
    private String label;
    private double start;  //  левая граница треугольника
    private double end;   // правая граница треугольника
    private double top;  // вершина треугольника

    public double getStart() {
        return start;
    }

    public Triangle(double start, double top, double end, int number) {
        this.start = start;
        this.top = top;
        this.end = end;
        this.label = String.format(FUZZY_LABEL_TEMPLATE, number);
    }

    public void setStart(int start) {
        this.start = start;
    }


    public double getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public double getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public double getValueAtPoint(double crispValue) {
        if (crispValue == this.getTop()) {
            return 1;
        } else if ((crispValue >= this.getEnd()) || (crispValue <= this.getStart())) {
            return 0;
        } else if (crispValue < this.getTop()) {
            return (crispValue - this.getStart()) / (this.getTop() - this.getStart());
        } else if (crispValue > this.getTop()) {
            return -(crispValue - this.getEnd()) / (this.getEnd() - this.getTop());
        }
        return 0;
    }

    public String getLabel() {
        return label;
    }
}
