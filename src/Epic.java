import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> idSubtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.status = "NEW";
        idSubtasks = new ArrayList<>();
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
}
