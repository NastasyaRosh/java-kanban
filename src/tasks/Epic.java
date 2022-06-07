package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.status = Statuses.NEW;
        idSubtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(ArrayList<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "idSubtasks=" + idSubtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
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
