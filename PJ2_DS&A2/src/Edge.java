class Edge { //定义边
    private Vertex vertex1;
    private Vertex vertex2;
    private String line;//哪条线
    private int weight;

    Edge(Vertex vertex1,Vertex vertex2,String line,int weight){
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.line = line;
        this.weight = weight;
    }

    Edge(){}

    String getLine(){ //返回所属地铁线路
        return line;
    }

    int getWeight(){ //返回边的权值
        return weight;
    }

    Vertex getOtherVertex(Vertex vertex){
        if(vertex == vertex1){
            return vertex2;
        }else if(vertex == vertex2){
            return vertex1;
        }else {
            return null;
        }
    }

    Vertex getVertex1(){
        return vertex1;
    }
    Vertex getVertex2(){
        return vertex2;
    }
}
