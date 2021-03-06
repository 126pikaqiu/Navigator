import java.util.ArrayList;

class MinHeap {
    private ArrayList<Vertex> heap = new ArrayList<>();

    private int parent(int i){//父节点索引
        return (i - 1) / 2;
    }

    private int left(int i){//左子节点索引
        return 2 * i + 1;
    }

    private int right(int i){//右子节点索引
        return 2 * i + 2;
    }

    MinHeap(ArrayList<Vertex> elements){
        heap.addAll(elements);
        build_min_heap();
    }

    private void build_min_heap(){
        for(int i = heap.size() / 2 + 1;i >= 0; i--){
            min_heapify(i);
        }
    }

    void insert(Vertex element){
        heap.add(element);
        int index = heap.size() - 1;
        while(index > 0 && heap.get(parent(index)).getDistance() > heap.get(index).getDistance()){
            Vertex temp = heap.get(index);
            heap.set(index, heap.get(parent(index)));
            heap.set(parent(index),temp);
            index = parent(index);
        }
    }

    private void min_heapify(int i){
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if(l < heap.size() && heap.get(l).getDistance() < heap.get(smallest).getDistance())
            smallest = l;
        if(r < heap.size() && heap.get(r).getDistance() < heap.get(smallest).getDistance())
            smallest = r;
        if(smallest != i){
            Vertex temp = heap.get(i);
            heap.set(i, heap.get(smallest));
            heap.set(smallest,temp);
            min_heapify(smallest);
        }
    }

    void update(Vertex element){
        int index;
        for(index = 0; index < heap.size(); index++){
            if(heap.get(index).getName().equals(element.getName())){
                break;
            }
        }
        while(index > 0 && heap.get(parent(index)).getDistance() > heap.get(index).getDistance()){
            Vertex temp = heap.get(index);
            heap.set(index, heap.get(parent(index)));
            heap.set(parent(index),temp);
            index = parent(index);
        }
    }

    Vertex extract_min(){//取出最小的元素
        if(heap.size() < 1){
            throw new Error("heap underflow");
        }
        Vertex min = heap.remove(0);
        if(heap.size() == 0){
            return min;
        }
        Vertex last = heap.remove(heap.size() - 1);
        heap.add(0,last);
        if(heap.size() > 1)
            min_heapify(0);
        return min;
    }
    
    boolean isEmpty(){
        return heap.isEmpty();
    }

}
