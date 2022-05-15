public class SubTask extends Task {
    protected int idEpic;

    public SubTask(String name, String description, int id, String status, int idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", idEpic=" + idEpic +
                '}';
    }
}
