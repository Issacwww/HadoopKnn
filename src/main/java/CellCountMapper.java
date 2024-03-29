import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class CellCountMapper extends Mapper<Object, Text, Text, Point> {
    // Compute the data distribution in each cell using MapReduce
    private Text cellId = new Text();
    private QTree qTree;
    protected void setup(Context context) throws IOException,InterruptedException {
        Configuration conf = context.getConfiguration();
        int N = conf.getInt("N", 3);
        double S = conf.getDouble("S", 100);
        qTree = new QTree(N,S);
    }
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] pointInput = value.toString().split("\t");
        Point point = new Point(pointInput[0], Double.parseDouble(pointInput[1]), Double.parseDouble(pointInput[2]));
        // using the tree to locate the point
        Node node = qTree.findNodeByCoords(point);
        cellId.set(node.id);
        // map this point to its cellId
        context.write(cellId, point);
    }
}
