package ru.ulstu.datamodel;

import java.time.LocalDateTime;
import java.util.List;

public class DatesParams {
    private List<LocalDateTime> dates;
    private int finalDatesCount;

    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(List<LocalDateTime> dates) {
        this.dates = dates;
    }

    public int getFinalDatesCount() {
        return finalDatesCount;
    }

    public void setFinalDatesCount(int finalDatesCount) {
        this.finalDatesCount = finalDatesCount;
    }
}
