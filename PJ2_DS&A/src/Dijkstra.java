import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

class Dijkstra {
    private ArrayList<Vertex> lastPath1;
    private ArrayList<Vertex> lastPath2;
    private ArrayList<Vertex> lastPath3;
    private ArrayList<Vertex> lastPath4;  //记忆四次路线
    private void DIJKSTRA(ArrayList<Vertex> vertices, Vertex start, Vertex end){ //某个算法
        initialize_single_source(vertices,start);
        MinHeap minHeap = new MinHeap(vertices);
        while (!minHeap.isEmpty()){
            Vertex u = minHeap.extract_min();
            if(u == end ){   //找到了,结束
                break;
            }
            for(Vertex vertex:u.getVertices()){
                relex(u,vertex);
            }
            minHeap.build_min_heap();
        }
    }

    private void initialize_single_source(ArrayList<Vertex> graph, Vertex start){ //初始化所有点的距离
        for(Vertex vertex:graph){
            vertex.setDistance(Integer.MAX_VALUE);
        }
        start.setDistance(0);
    }

    private void relex(Vertex preVertex, Vertex now){ //松弛一条边
        if(preVertex.getDistance() != Integer.MAX_VALUE && now.getDistance() > preVertex.getDistance() + preVertex.getEdge(now,false).getWeight()){//防止加法溢出
            now.setDistance(preVertex.getDistance() + preVertex.getEdge(now,false).getWeight());
            now.setPreVertex(preVertex);
        }
    }

    ArrayList<Vertex> getPath(ArrayList<Vertex> vertices1, Vertex start, Vertex end, HashMap<String,Vertex> map){ //返回路径列表

        if(lastPath1 != null){ //记忆
            ArrayList<Vertex> mayPath = checkPath(start,end,lastPath1);
            if(mayPath != null){
                return mayPath;
            }
        }

        if(lastPath2 != null){ //记忆
            ArrayList<Vertex> mayPath = checkPath(start,end,lastPath2);
            if(mayPath != null){
                return mayPath;
            }
        }

        if(lastPath3 != null){ //记忆
            ArrayList<Vertex> mayPath = checkPath(start,end,lastPath3);
            if(mayPath != null){
                return mayPath;
            }
        }

        if(lastPath4 != null){ //记忆
            ArrayList<Vertex> mayPath = checkPath(start,end,lastPath4);
            if(mayPath != null){
                return mayPath;
            }
        }

        DIJKSTRA(vertices1,start,end);
        ArrayList<Vertex> path = new ArrayList<>();
        Stack<String> vertices = new Stack<>();
        vertices.push(end.getName());
        while (end.getPreVertex() != null && end.getPreVertex() != start){
            end = end.getPreVertex();
            vertices.push(end.getName());
        }
        if(end.getPreVertex() == null){
            return null;
        }else{
            path.add(start);
            while (!vertices.empty()){
                path.add(map.get(vertices.pop()));
            }
            if(lastPath1 == null){
                lastPath1 = path;
            }else if(lastPath2 == null){
                lastPath2 = path;
            }else if(lastPath3 == null){
                lastPath3 = path;
            }else if(lastPath4 == null){
                lastPath4 = path;
            }else{
                lastPath1 = lastPath2;
                lastPath2 = lastPath3;
                lastPath3 = lastPath4;
                lastPath4 = path;
            }
            return path;
        }
    }

    private ArrayList<Vertex> checkPath(Vertex start, Vertex end,ArrayList<Vertex> lastPath){ //提供记忆功能,记忆上一次的路线
        boolean get1 = false;
        boolean get2 = false;
        for(Vertex vertex:lastPath){
            if (start == vertex){
                get1 = true;
            }else if(end == vertex){
                get2 = true;
            }
        }
        if(get1 && get2){   //真的找到了这个路线
            int index1 = lastPath.indexOf(start);
            int index2 = lastPath.indexOf(end);
            return getf2(lastPath,index1,index2);
        }
        return null;
    }

    private ArrayList<Vertex> getf2(ArrayList<Vertex> path, int index1, int index2){
        boolean reverse = false;
        if(index1 > index2){
            reverse = true;
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        ArrayList<Vertex> newPath = new ArrayList<>();

        for(int i = index1; i <= index2; i++){
            newPath.add(path.get(i));
        }
        ArrayList<Vertex> newPath1 = new ArrayList<>();

        if(reverse){ //反转
            for(int i = newPath.size() - 1; i >= 0; i--)
                newPath1.add(newPath.get(i));
        }

        return newPath1;

    }
}
