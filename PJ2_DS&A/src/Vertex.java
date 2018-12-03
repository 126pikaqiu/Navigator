import java.util.ArrayList;

class Vertex {
    private String name;
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private int distance;
    private Vertex preVertex;

    Vertex(String name){
        this.name = name; //名字
    }

    Vertex(){}

    void AddEdge(Vertex vertex1,String line,int weight){  //加入一边
        Edge edge = new Edge(this,vertex1,line,weight);
        edges.add(edge);
    }

    String getName(){
        return name;
    }

    ArrayList<Edge> getEdges(){
        return edges;
    }

    void setDistance(int distance){
        this.distance = distance;
    }

    int getDistance(){
        return distance;
    }

    Edge getEdge(Vertex vertex1,boolean inverse){
        if(!inverse){
            for (Edge edge:edges){
                if (edge.getOtherVertex(this) == vertex1){
                    return edge;
                }
            }
        }else{
            for(int i = edges.size() - 1; i >= 0; i--){
                if(edges.get(i).getOtherVertex(this) == vertex1){
                    return edges.get(i);
                }
            }
        }

        return null;//一般不可到达，到达即为错误
    }

    void setPreVertex(Vertex vertex){ //设置前驱点
        preVertex = vertex;
    }

    Vertex getPreVertex(){  //获得前驱点
        return preVertex;
    }

    void addVertex(Vertex vertex){
        vertices.add(vertex);
    }

    ArrayList<Vertex> getVertices(){
        return vertices;
    }

}
