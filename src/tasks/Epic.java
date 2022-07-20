package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks;
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.status = Statuses.NEW;
        idSubtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic(String name, String description, Statuses status, int id, String duration, String startTime) {
        super(name, description, status, id);
        if (!duration.equals("null")) {
            this.duration = Integer.parseInt(duration);
        } else this.duration = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm|dd.MM.yyyy");
        if (!startTime.equals("null")) {
            setStartTime(LocalDateTime.parse(startTime, formatter));
        }

    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(ArrayList<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "idSubtasks=" + idSubtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + getStartTimeFormat() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return super.equals(o) && Objects.equals(idSubtasks, epic.idSubtasks);

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSubtasks);
    }

}
