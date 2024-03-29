import com.google.gson.Gson;

import java.util.*;

public class QTree {
    int levels;
    double sideLen;
    Node root;

    public QTree(int levels, double sideLen) {
        this.levels = levels + 1;
        this.sideLen = sideLen;
        this.root = new Node(0, 0, sideLen, "0", "0");
        this.recursive_subdivide(this.root);
    }

    private void recursive_subdivide(Node node) {
        if (node.id.length() == this.levels) {
            node.isLeave = true;
            return;
        }
        /**
         *  divide the cell in following way at each call of this function
         *  1 3
         *  0 2
         */
        double subside = node.sideLen / 2;

        Node x0 = new Node(node.x, node.y, subside, node.id, node.id + "0");
        this.recursive_subdivide(x0);

        Node x1 = new Node(node.x, node.y + subside, subside, node.id, node.id + "1");
        this.recursive_subdivide(x1);

        Node x2 = new Node(node.x + subside, node.y, subside, node.id, node.id + "2");
        this.recursive_subdivide(x2);

        Node x3 = new Node(node.x + subside, node.y + subside, subside, node.id, node.id + "3");
        this.recursive_subdivide(x3);

        node.children.add(x0);
        node.children.add(x1);
        node.children.add(x2);
        node.children.add(x3);
    }

    public void display() {
        display(this.root);
    }

    private void display(Node node) {
        if (node.isLeave) {
            System.out.println(node);
            return;
        }
        System.out.println(node);
        for (Node child : node.children) {
            display(child);
        }
    }

    public Node findNodeById(String id) {
        if (id == "0")
            return this.root;
        Node cur = this.root;
        int curIdx = 1;
        while (curIdx < id.length()) {
            if (cur.children.isEmpty())
                return null;
            cur = cur.children.get(Character.getNumericValue(id.charAt(curIdx)));
            curIdx++;
        }
        return cur;
    }

    public Node findNodeByCoords(Point point) {
        return this.findNodeByCoords(this.root, point);
    }

    private Node findNodeByCoords(Node node, Point point) {
        if (node.isLeave) {
            if (point.isValid())
                node.points.add(point);
            return node;
        }
        double midX = node.x + node.sideLen / 2, midY = node.y + node.sideLen / 2;
        if (point.x < midX && point.y < midY)
            return findNodeByCoords(node.children.get(0), point);
        else if (point.x < midX && point.y >= midY)
            return findNodeByCoords(node.children.get(1), point);
        else if (point.x >= midX && point.y < midY)
            return findNodeByCoords(node.children.get(2), point);
        return findNodeByCoords(node.children.get(3), point);
    }


    public void merge(Node node) {
        for (Node child : node.children) {
            if (child.points.isEmpty() && !child.isLeave)
                merge(child);
            node.points.addAll(child.points);
            child.isLeave = true;
        }
        node.isLeave = true;
        node.children.clear();
    }

    public HashMap<String, String> getLeavesPoints(boolean removePoints) {
        HashMap<String, String> res = new HashMap<>();
        getLeaves(this.root, res, removePoints);
        return res;
    }

    private void getLeaves(Node node, HashMap<String, String> res, boolean removePoints) {
        if (node.isLeave) {
            StringBuilder pointsStr = new StringBuilder();
            for (Point point : node.points)
                pointsStr.append(point + "#");
            if (pointsStr.length() > 0)
                res.put(node.id, pointsStr.substring(0, pointsStr.length() - 1));
            if (removePoints)
                node.points.clear();
            return;
        }
        for (Node child : node.children) {
            getLeaves(child, res, removePoints);
        }
    }

    public HashSet<String> findAdjacentCells(Node node) {
        HashSet<String> res = new HashSet<>();
        findAdjacentCells(node, res);
        findAdjacentCells(findNodeById(node.getParentId()), res);
        return res;
    }

    private void findAdjacentCells(Node node, HashSet<String> res) {
        int[][] map = new int[][]{{0, 1, 2, 3}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 2, 1, 0}};
        int self = Character.getNumericValue(node.id.charAt(node.id.length() - 1));
        int[] order = map[self];
        Node[] siblings = findSiblings(node, order);
        findDiagonalCells(siblings[3], order, res);
        findBackDiagonalCells(siblings[1], order, 2, res);
        findBackDiagonalCells(siblings[2], order,1, res);

    }

    private void findDiagonalCells(Node node, int[] order, HashSet<String> res) {
        if (node.isLeave){
            res.add(node.id);
            return;
        }
        findDiagonalCells(node.children.get(order[0]),order,res);
    }

    private void findBackDiagonalCells(Node node,int[] order, int pattern, HashSet<String> res) {
        if (node.isLeave){
            res.add(node.id);
            return;
        }
        findBackDiagonalCells(node.children.get(order[0]),order, order[pattern],res);
        findBackDiagonalCells(node.children.get(order[pattern]),order, order[pattern],res);
    }

    private Node[] findSiblings(Node node, int[] order) {
        Node[] siblings = new Node[4];
        ArrayList<Node> children = findNodeById(node.getParentId()).children;
        for (int i : order)
            siblings[i] = children.get(i);
        return siblings;
    }
}
