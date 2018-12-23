import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class SimpleGraphView {
    private Graph<Integer, String> g;
    /** Creates a new instance of SimpleGraphView */

    public SimpleGraphView() {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        g = new SparseMultigraph<Integer, String>();
    }

    public void addVertex(int vertex){
        g.addVertex((Integer)vertex);
    }


    /**
     *
     * @param v1 children node
     * @param v2 parent node
     * @param weight
     */
    public void addEdge(int v1, int v2, int weight, EdgeType type){
        g.addEdge(weight + "", v1, v2, type);
    }

    public boolean isRepeated(int v, int edge){
        Collection<String> set = g.getIncidentEdges(v);
        if(set.contains(edge + ""))
            return true;
        else
            return false;
    }

    public void draw() {
//        SimpleGraphView sgv = new SimpleGraphView(); // This builds the graph
        // Layout<V, E>, BasicVisualizationServer<V,E>
        Layout<Integer, String> layout = new CircleLayout(g);
        layout.setSize(new Dimension(800,800));
        BasicVisualizationServer<Integer,String> vv =
                new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(850,850));
        // Setup up a new vertex to paint transformer...
        Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                return new Color(255, 204, 0);
            }
        };
        // Set up a new stroke Transformer for the edges
//        float dash[] = {10.0f};
//        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
//                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//        Transformer<String, Stroke> edgeStrokeTransformer =
//                new Transformer<String, Stroke>() {
//                    public Stroke transform(String s) {
//                        return edgeStroke;
//                    }
//                };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        test();
    }

    public static void test(){
        SimpleGraphView graph = new SimpleGraphView();

        graph.addVertex(0);
        graph.addVertex(1);
        graph.addVertex(3);
        graph.addVertex(2);

        if(!graph.isRepeated(1, 5))
            graph.addEdge(1, 3, 5, EdgeType.DIRECTED);

        if(!graph.isRepeated(2, 4))
            graph.addEdge(2, 3, 4, EdgeType.DIRECTED);

        if(!graph.isRepeated(3, 5))
            graph.addEdge(3, 1, 5, EdgeType.DIRECTED);


        if(!graph.isRepeated(0, 6))
            graph.addEdge(0, 1, 6, EdgeType.DIRECTED);

        graph.draw();
    }
}
