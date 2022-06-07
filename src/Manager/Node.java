package Manager;

public class Node<T> {

    public T data;
    public Node<T> nextLink;
    public Node<T> prevLink;

    public Node(Node<T> prevLink, T data, Node<T> nextLink) {
        this.prevLink = prevLink;
        this.data = data;
        this.nextLink = nextLink;
    }
}
