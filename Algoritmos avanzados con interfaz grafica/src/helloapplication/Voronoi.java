package helloapplication;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Voronoi {

    private int WIDTH;
    private int HEIGHT;
    private int NUM_POINTS;

    private Point[] points;
    private List<Edge> edges;

    public Voronoi(int w,int h,int num, Point[] p) {
        this.WIDTH = w;
        this.HEIGHT = h;
        this.NUM_POINTS = num;
        this.points = p.clone();
        edges = new ArrayList<>();
    }

    //Funcion auxiliar para encontrar la central mas cercada
    private int findClosestPointIndex(int x, int y) {
        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < points.length; i++) {
            double distance = Math.sqrt(Math.pow(x - points[i].x, 2) + Math.pow(y - points[i].y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }

        return minIndex;
    }

    //Funcion principal para hacer el diagrama de Voronoi
    //Es un algoritmo de fuerza bruta que compara cada posicion y checa que central le queda mas cerca
    //Complejidad O(n*m) 
    public void generateVoronoiDiagram() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int closestPointIndex = findClosestPointIndex(x, y);

                // Collect edges
                if (x > 0) {
                    int leftPointIndex = findClosestPointIndex(x - 1, y);
                    if (closestPointIndex != leftPointIndex) {
                        edges.add(new Edge(new Point(x, y), new Point(x - 1, y)));
                    }
                }
                if (y > 0) {
                    int abovePointIndex = findClosestPointIndex(x, y - 1);
                    if (closestPointIndex != abovePointIndex) {
                        edges.add(new Edge(new Point(x, y), new Point(x, y - 1)));
                    }
                }
            }
        }
    }

    //Clase Edge para generar las lineas graficamente
    public static class Edge {
        Point start;
        Point end;

        Edge(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    public List<Edge> getEdges() {
        return edges;
    }

}