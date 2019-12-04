import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Knn {

    private ArrayList<Point> points;
    private int k;
    private HashMap<Point, PriorityQueue<PointDetail>> distance;

    public Knn(ArrayList<Point> points, int k) {
        this.points = points;
        this.k = k;
        this.distance = new HashMap<>();
        setDistance(points, k);
    }

    private void setDistance(ArrayList<Point> pointsInCell, int k){
        for(Point origin: pointsInCell){
            for(Point point: pointsInCell){
                if (origin.equals(point)) continue;
                if(distance.containsKey(origin)){
                    PriorityQueue<PointDetail> queue = distance.get(origin);
                    PointDetail detail = new PointDetail(point, getEuclideanDistance(origin, point));
                    if (queue.size() < k) {
                        queue.offer(detail);
                    } else if (queue.peek().distance > detail.distance) {
                        queue.poll();
                        queue.offer(detail);
                    }
                    distance.put(origin,queue);
                }else {
                    PriorityQueue<PointDetail> queue = new PriorityQueue<PointDetail>(k,
                            new Comparator<PointDetail>() {
                                public int compare(PointDetail p1, PointDetail p2) {
                                    if (p2.distance > p1.distance)
                                        return 1;
                                    return -1;
                                }
                            });
                    queue.offer(new PointDetail(point,getEuclideanDistance(origin,point)));
                    distance.put(origin, queue);
                }
            }
        }
    }

    private double getEuclideanDistance(Point origin, Point neighbor){
        return Math.sqrt(Math.pow((origin.x - neighbor.x),2) + Math.pow((origin.y - neighbor.y),2));
    }

    public ArrayList<PointTuple> getKnnPoints(){
        ArrayList<PointTuple> res = new ArrayList<>();
        distance.forEach((point,array) -> {
            ArrayList<Point> neighbor = new ArrayList<>();
            while (!array.isEmpty()){
                neighbor.add(array.poll().point);
            }
            res.add(new PointTuple(point,neighbor));
        });
        return res;
    }

    @Override
    public String toString() {
        return "{distance=" + distance + '}';
    }

    public static void main(String[] args){

        ArrayList<Point> test = new ArrayList<>();
        test.add(new Point(0,0));
        test.add(new Point(0,8));
        test.add(new Point(0,9));
        test.add(new Point(0,5));
        test.add(new Point(0,4));
        test.add(new Point(0,1));
        test.add(new Point(0,2));
        test.add(new Point(0,3));
        test.add(new Point(0,6));
        test.add(new Point(0,10));

        Knn neighborsList = new Knn(test,5);
        System.out.println(neighborsList.getKnnPoints());
    }
}

class PointDetail {

    Point point;
    double distance;

    public PointDetail(Point point, double distance) {
        this.point = point;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "{"+ point +
                ", " + distance +
                '}';
    }
}