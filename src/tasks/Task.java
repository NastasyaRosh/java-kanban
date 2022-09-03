package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Statuses status;
    protected Integer duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Statuses status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Statuses status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Statuses status, int id, int duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Statuses status, int id, String duration, String startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        if (!duration.equals("null")) {
            this.duration = Integer.parseInt(duration);
        } else this.duration = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm|dd.MM.yyyy");
        if (!startTime.equals("null")) {
            this.startTime = LocalDateTime.parse(startTime, formatter);
        } else this.startTime = null;
    }

    protected Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Statuses status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm|dd.MM.yyyy");
        if (startTime != null) {
            return startTime.format(formatter);
        } else return null;
    }

    public Integer getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
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
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

}
