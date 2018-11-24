import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Navigator navigator = new Navigator();
        navigator.loadMap("src/Timetable.xls");
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("Please input the start station and the end station:(q for exit)");
            String line = scanner.nextLine();
            if(line.trim().equals("q")){
                break;
            }
            if(line.split(" ").length < 2){
                continue;
            }
            String start = line.split(" ")[0];
            String end = line.split(" ")[1];
            long time1 = System.nanoTime();
            ArrayList<String> path = navigator.getPath(start,end);
            long time2 = System.nanoTime();
            for (String aPath : path) {
                System.out.print(aPath);
            }
            System.out.println();
            System.out.println("查询时间为" + (time2 - time1) + " ns");
        }
        System.out.println("bye bye");
    }

}
