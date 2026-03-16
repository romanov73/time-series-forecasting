package ru.ulstu.method.ftransform;

/**
 * Треугольная функция принадлежности
 */
public class AComponent {
    private int start;  //  левая граница треугольника
    private int end;   // правая граница треугольника
    private int top;  // вершина треугольника

    public int getStart() {
        return start;
    }

    public AComponent() {
    }

    public AComponent(int start, int top, int end) {
        this.start = start;
        this.top = top;
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }


    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public double getValueAtPoint(int pointIndex) {
        if (pointIndex == this.getTop()) {
            return 1;
        } else if ((pointIndex >= this.getEnd()) || (pointIndex <= this.getStart())) {
            return 0;
        } else if (pointIndex < this.getTop()) {
            return (double) (pointIndex - this.getStart()) / (this.getTop() - this.getStart());
        } else if (pointIndex > this.getTop()) {
            return (double) -(pointIndex - this.getEnd()) / (this.getEnd() - this.getTop());
        }
        return 0;
    }
}
