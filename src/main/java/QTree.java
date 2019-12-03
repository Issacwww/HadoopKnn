import java.util.ArrayList;
import java.util.HashMap;
class Point{
    double x;
    double y;
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
class Node {
    double x;
    double y;
    double sideLen;
    String id;
    private Node parent;
    ArrayList<Point> points;
    ArrayList<Node> children;
    public Node(){}
    public Node(double x0, double y0, double sideLen, Node parent, String id){
        this.x = x0;
        this.y = y0;
        this.sideLen = sideLen;
        this.parent = parent;
        this.id = id;
        this.points = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Node getParent() {
        return parent;
    }

    public int getSum() {
        return this.points.size();
    }

    @Override
    public String toString() {
        StringBuilder childrenStr = new StringBuilder("[ ");
        for(Node child :children)
            childrenStr.append("'" + child.id + "', ");
        childrenStr.append("]");
        StringBuilder pointsStr = new StringBuilder("[");
        for(Point point :points)
            pointsStr.append(point);
        pointsStr.append("]");
        return "Node {" +
                "x=" + x +
                ", y=" + y +
                ", sideLen=" + sideLen +
                ", id='" + id + '\'' +
                ", parent='" + parent.id + '\'' +
                ", children=" + childrenStr.toString() +
                ", points=" + pointsStr.toString() +
                "}";
    }
}
public class QTree {
    int levels;
    double sideLen;
    Node root;
    private HashMap<String, Node> leaves;
    public QTree(int levels, double sideLen){
        this.levels = levels + 1;
        this.sideLen = sideLen;
        this.root = new Node(0,0, sideLen, new Node(), "0");
        this.leaves = new HashMap<>();
        this.recursive_subdivide(this.root);
    }

    private void recursive_subdivide(Node node) {
        if (node.id.length() == this.levels) {
            this.leaves.put(node.id, node);
            return;
        }
        /**
         *  divide the cell in following way at each call of this function
         *  1 3
         *  0 2
         */
        double subside = node.sideLen / 2;

        Node x0 = new Node(node.x, node.y, subside, node, node.id + "0");
        this.recursive_subdivide(x0);

        Node x1 = new Node(node.x, node.y + subside, subside, node, node.id + "1");
        this.recursive_subdivide(x1);

        Node x2 = new Node(node.x + subside, node.y, subside, node, node.id + "2");
        this.recursive_subdivide(x2);

        Node x3 = new Node(node.x + subside, node.y + subside, subside, node, node.id + "3");
        this.recursive_subdivide(x3);

        node.children.add(x0);
        node.children.add(x1);
        node.children.add(x2);
        node.children.add(x3);


    }

    public void display(Node node){
        if(this.leaves.containsKey(node.id)){
            System.out.println(node);
            return;
        }
        System.out.println(node);
        for(Node child: node.children){
            display(child);
        }
    }
    public HashMap getLeaves(){
        return this.leaves;
    }

    public Node findNodeById(String id){
        if(id == "0")
            return this.root;
        Node cur = this.root;
        int curIdx = 1;
        while(curIdx < id.length()){
            cur = cur.children.get(Character.getNumericValue(id.charAt(curIdx)));
        }
        return cur;
    }

    public Node findNodeByCoords(double x, double y){
        return this.findNodeByCoords(this.root, x, y);
    }

    private Node findNodeByCoords(Node node, double x, double y) {
        if(this.leaves.containsKey(node.id)) {
            node.points.add(new Point(x, y));
            return node;
        }
        double midX = node.x + node.sideLen / 2, midY =  node.y + node.sideLen / 2;
        if(x < midX && y < midY)
            return findNodeByCoords(node.children.get(0), x, y);
        else if(x < midX && y >= midY)
            return findNodeByCoords(node.children.get(1), x, y);
        else if(x >= midX && y < midY)
            return findNodeByCoords(node.children.get(2), x, y);
        return findNodeByCoords(node.children.get(3), x, y);
    }

    public void merge(Node node){
        for(Node child: node.children){
            if (child.points.isEmpty() && !this.leaves.containsKey(child.id))
                merge(child);
            node.points.addAll(child.points);
            this.leaves.remove(child.id);
        }
        this.leaves.put(node.id,node);
        node.children.clear();
    }

    public static void main(String[] args) {
        QTree qt = new QTree(1,20);
        qt.findNodeByCoords(16.6,15);
        qt.findNodeByCoords(2,4);

        qt.merge(qt.root.children.get(0));
        qt.display(qt.root);

        qt.merge(qt.root);
        qt.display(qt.root);

    }

}