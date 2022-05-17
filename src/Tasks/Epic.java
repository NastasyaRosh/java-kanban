package Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.status = "NEW";
        idSubtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
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
        return id == epic.id && Objects.equals(name, epic.name) && Objects.equals(description, epic.description) && Objects.equals(idSubtasks, epic.idSubtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, idSubtasks);
    }

}