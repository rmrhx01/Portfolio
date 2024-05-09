
package helloapplication;



import java.util.ArrayList;
import java.util.List;

class Graph {
   private List<Node> nodes;
   private List<Link> links;

    public Graph() {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }
    
    public int[][] getDistanceMatrix(){
        int [][] matrix = new int[nodes.size()][nodes.size()];
        for (int i = 0;i<links.size();i++){
            int idx1 = links.get(i).getSourceNode().getId();
            int idx2 = links.get(i).getTargetNode().getId();
            matrix[idx1][idx2] = links.get(i).getDistance();
        }
        return matrix;
    }
    
    public int[][] getCapacityMatrix(){
        int [][] matrix = new int[nodes.size()][nodes.size()];
        for (int i = 0;i<links.size();i++){
            int idx1 = links.get(i).getSourceNode().getId();
            int idx2 = links.get(i).getTargetNode().getId();
            matrix[idx1][idx2] = links.get(i).getCapacity();
        }
        return matrix;
    }
    
    public void deleteNode(Node node) {
        nodes.remove(node);
        // Delete all associated links
        links.removeIf(link -> link.getSourceNode() == node || link.getTargetNode() == node);
        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).setId(i);
        }
    }

    public Node getNode(String name){
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
    
    public Node getNode(int id){
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    public void editNode(Node node, Node newNode) {
        int index = nodes.indexOf(node);
        if (index != -1) {
            nodes.set(index, newNode);
        }
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public void deleteLink(Link link) {
        links.remove(link);
    }

    public void editLink(Link link, Link newLink) {
        int index = links.indexOf(link);
        if (index != -1) {
            links.set(index, newLink);
        }
    }

    public Link getLink(String initial,String finali) {
        for (Link link : links) {
            if (link.getSourceNode().getName().equals(initial) && link.getTargetNode().getName().equals(finali)) {
                return link;
            }
        }
        return null;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Link> getLinks() {
        return links;
    }
    
    public int getNextId(){
        return this.nodes.size();
    }
}

class Node {
    private String name;
    private List<Integer> coordinates = new ArrayList<>();
    private Integer id;

    public Node(String name, List<Integer> coordinates, int id) {
        this.name = name;
        this.coordinates = coordinates;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
}

class Link {
    private Node sourceNode;
    private Node targetNode;
    private int distance;
    private int capacity;


    public Link(Node sourceNode, Node targetNode, int distance,int capacity) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.distance = distance;
        this.capacity = capacity;

    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public int getCapacity() {
        return capacity;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
