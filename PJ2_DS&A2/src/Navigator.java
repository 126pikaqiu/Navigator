import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Navigator {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private HashMap<String,Vertex> map = new HashMap<>();
    void loadMap(String path) {
        File input = new File(path);
        try {
            Workbook book = Workbook.getWorkbook(input);
            int sheetSize = book.getNumberOfSheets();
            String name;
            String stationName;
            String time1 = null;
            String time2;
            int startRow;//起始行
            Vertex newStation;
            Vertex preStation;
            Vertex binStation;//两个方向的站点
            for(int k = 0; k < sheetSize; k++) {
                name = book.getSheetNames()[k];
                Sheet sheet = book.getSheet(k);

                if(k == 9 || k == 10) {
                    startRow = 2;
                }else {
                    startRow = 1;
                }

                newStation = null;
                int j = sheet.getRows();//两个方向的站点对应的索引
                String time3 = null;
                binStation = null;

                //导入中间站点
                for(int i = startRow; i < sheet.getRows(); i++) { //第一个方向和第二个方向的部分站点

                    preStation = newStation;
                    stationName = sheet.getCell(0, i).getContents();
                    time2 = sheet.getCell(1, i).getContents();

                    //两个浦电路站,特殊处理
                    if(name.equals("Line 4") && stationName.equals("浦电路")){
                        stationName = stationName + "4";
                    }else if(name.equals("Line 6") && stationName.equals("浦电路")){
                        stationName = stationName + "6";
                    }

                    if(time2.equals("--") || (sheet.getColumns() > 2 && sheet.getCell(2, i).getContents().equals("--"))){ //跳过某些站点
                        if(binStation == null){
                            binStation = preStation;
                            j = i;
                            time3 = time1;
                        }
                        if(time2.equals("--"))
                            continue;
                    }

                    newStation = exist(stationName);

                    if(preStation != null){ //也就第一个站点的问题
                        preStation.getVertices().add(newStation);
                        newStation.getVertices().add(preStation);
                        Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                        preStation.getEdges().add(edge);
                        newStation.getEdges().add(edge);
                        edge = null; //我想释放资源
                    }
                    time1 = time2;
                }

                preStation = binStation;
                time1 = time3;
                for(; j < sheet.getRows(); j++) { //第二个方向的分站点
                    stationName = sheet.getCell(0, j).getContents();
                    time2 = sheet.getCell(2, j).getContents();

                    if(time2.equals("--")){ //跳过某些站点
                        continue;
                    }

                    newStation = exist(stationName);
                    assert preStation != null;
                    preStation.getVertices().add(newStation);
                    newStation.getVertices().add(preStation);
                    Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                    preStation.getEdges().add(edge);
                    newStation.getEdges().add(edge);
                    edge = null; //我想释放资源
                    time1 = time2;
                    preStation = newStation;
                }

            }

            //四号线是环线
            Vertex vertex1 = exist("宜山路");
            Vertex vertex2 = exist("虹桥路");
            Edge edge = new Edge(vertex1,vertex2,"Line 4",3);//3分钟
            assert vertex1 != null;
            vertex1.getEdges().add(edge);
            assert vertex2 != null;
            vertex2.getEdges().add(edge);
            vertex1.getVertices().add(vertex2);
            vertex2.getVertices().add(vertex1);

        }catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    private Vertex exist(String station) {  //判断是否已经导入了点
        Vertex newStation = map.get(station);
        if(newStation == null){
            newStation = new Vertex(station);
            vertices.add(newStation);
            map.put(station,newStation);
        }
        return  newStation;
    }


    private int getTime(String start, String end) { //返回分钟数
        int length1 = start.length();
        int length2 = end.length();
        int minute = (int)end.charAt(length2 - 1) - (int)start.charAt(length1 - 1);
        int ten = (int)end.charAt(length2 - 2) - (int)start.charAt(length1 - 2);
        int hour = (int)end.charAt(length2 - 4) - (int)start.charAt(length1 - 4);
        hour = hour >= 0? hour : hour + 4;
        return hour * 60 + ten * 10 + minute;
    }

    private ArrayList<ArrayList<Vertex>> getPath1(String start, String end){
        Dijkstra dijkstra = new Dijkstra();
        Vertex startV = exist(start);
        Vertex endV = exist(end);
        ArrayList<Vertex> path = new ArrayList<>();
        if(startV == endV){
            path.add(startV);
            ArrayList<ArrayList<Vertex>> paths = new ArrayList<>();
            paths.add(path);
            return paths;
        }
        return dijkstra.getPath(vertices,startV,endV);
    }

    private String[] getPath(String start, String end){
        ArrayList<ArrayList<Vertex>> paths = getPath1(start,end);
        String[] result = new String[paths==null?1:paths.size()];
        if(paths == null){
            result[0] = "您输入的站点不存在";
            return result;
        }
        int changeTime[] = new int[paths.size()];
        int minChangeTime = 0;
        int index = 0;
        String line;
        Vertex startVertex = map.get(start); //起始站点
        Vertex vertex,preVertex;

        for(ArrayList<Vertex> path:paths){  //统计换乘次数并形式化输出格式
            preVertex = startVertex;
            line = "";
            result[index] = "";
            for (int i = path.size() - 2; i > 0; i--){
                vertex = path.get(i);
                if(!vertex.getEdge(preVertex).getLine().equals(line)){
                    line = vertex.getEdge(preVertex).getLine();
                    result[index] += preVertex.getName();
                    result[index] += "-";
                    result[index] += line;
                    result[index] += "-";
                    changeTime[index]++;
                }
                preVertex = vertex;
            }
            if(minChangeTime > changeTime[index] || minChangeTime == 0){
                minChangeTime = changeTime[index];
            }
            result[index++] += end;
        }
        String[] result1 = new String[result.length];
        int k = 0;
        for(int i = 0; i < changeTime.length; i++){ //选出换乘次数最少的路线
            if(changeTime[i] == minChangeTime){
                result1[k++] = result[i];
            }
        }
        String[] temp = new String[k];
        System.arraycopy(result1,0,temp,0,k);
        return temp;
    }

    String[] getPath(String[] station){
        String[] result;
        for(String value:station){
            if(map.get(value) == null){
                result = new String[1];
                result[0] = "您输入的站点不存在";
                return result;
            }
        }

        String pre = station[0];
        result = getPath(pre,station[1]);
//        getPath(pre,station[station.length - 1]); //走一遍从起点到终点的路线，可以作为记忆,可能极其重要哟。
        String[] path1;
        pre = station[1];
        for(int i = 2; i < station.length; i++){
            path1 = getPath(pre,station[i]);
            result = merge(result,path1,pre);
            pre = station[i];
        }
        return result;
    }

    private String[] merge(String[] path1, String[] path2, String mid){ //合并两类路线
        String[] result = new String[path1.length * path2.length];
        int index = 0;
        for (String aPath1 : path1) {
            for (String aPath2 : path2) {
                result[index++] = aPath1 + aPath2.replace(mid, ""); //细节处理
            }
        }
        return result;
    }

}
