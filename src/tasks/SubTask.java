package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    protected int idEpic;

    public SubTask(String name, String description, Statuses status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Statuses status, int id, int idEpic) {
        super(name, description, status, id);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Statuses status, int idEpic, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Statuses status, int id, int idEpic, String duration, String startTime) {
        super(name, description, status, id, duration, startTime);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", idEpic=" + idEpic +
                ", duration='" + duration + '\'' +
                ", startTime='" + getStartTimeFormat() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return super.equals(o) && Objects.equals(idEpic, subTask.idEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }

}
