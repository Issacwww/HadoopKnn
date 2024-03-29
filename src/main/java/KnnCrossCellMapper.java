import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;

public class KnnCrossCellMapper extends Mapper<Object, Text, Text, Text> {

    private Text cellId = new Text();
    private QTree qTree;
    private int K;

    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Gson gson = new Gson();
        String mergedQTree = conf.get("qTree");
        qTree = gson.fromJson(mergedQTree, QTree.class);
        K = conf.getInt("K", 3);
    }

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        //map by point Id with its neighbors
        String[] knnOutput = value.toString().split("\t");
        Point originPoint = new Point(knnOutput[0]);
        String[] cellIdAndNeighbors = knnOutput[1].split("#");
        Node originNode = qTree.findNodeById(cellIdAndNeighbors[0]);
        Point farthest = new Point(cellIdAndNeighbors[1]);
        String neighbors = "";
        for (int i = 1; i <= K; i++) {
            neighbors = neighbors.concat("#" + cellIdAndNeighbors[i]);
        }
        // check boundary cross
        boolean notCrossBoundary = notCrossBoundary(originPoint, farthest, originNode);
        if (notCrossBoundary) {
            cellId.set(originNode.id);
            context.write(cellId, new Text(originPoint.toString() + neighbors + "#" + notCrossBoundary));
        } else {
            HashSet<String> neighborNodes = qTree.findAdjacentCells(originNode);
            for (String node : neighborNodes) {
                if (!node.equals(originNode.id)) {
                    notCrossBoundary = false;
                    cellId.set(node);
                    //map by cellId with origin Point and its neighbors
                    context.write(cellId, new Text(originPoint.toString() + neighbors + "#" + notCrossBoundary));
                }
            }
        }
    }

    private boolean notCrossBoundary(Point originPoint, Point farthest, Node originNode){
        double radius = originPoint.getEuclideanDistance(farthest);
        double px = originPoint.x;
        double py = originPoint.y;
        double x = originNode.x;
        double y = originNode.y;
        double len = originNode.sideLen;
        return (x<px-radius)
                && (x+len > px+radius)
                && (y<py-radius)
                && (y+len > py+radius);
    }
}
