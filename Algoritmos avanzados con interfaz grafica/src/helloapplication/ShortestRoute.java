package helloapplication;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortestRoute {
    private int[] path;
    private List<List<Integer>> distancias;
    private List<List<Integer>> coordenadas;

    public ShortestRoute(List<List<Integer>> coordenadas){
        this.path = null;
        this.distancias = new ArrayList<>();
        this.coordenadas = coordenadas;
    }
    private static List<List<Integer>> generarMatrizAdyacencia(List<List<Integer>> coordenadas) {
        int n = coordenadas.size();
        List<List<Integer>> distancias = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            List<Integer> row = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                int distancia = calcularDistancia(coordenadas.get(i), coordenadas.get(j));
                row.add(distancia);
            }
            distancias.add(row);
        }

        return distancias;
    }

    private static int calcularDistancia(List<Integer> coordenada1, List<Integer> coordenada2) {
        int x1 = coordenada1.get(0);
        int y1 = coordenada1.get(1);
        int x2 = coordenada2.get(0);
        int y2 = coordenada2.get(1);

        int distancia = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        return distancia;
    }

    private static int[] tsp(List<List<Integer>> graph) {
        int n = graph.size();
        boolean[] visited = new boolean[n];
        int[] path = new int[n + 1];
        int[] minPath = new int[n + 1];
        int minCost = Integer.MAX_VALUE;

        // Inicia el recorrido desde el nodo 0
        path[0] = 0;
        visited[0] = true;
        tspUtil(graph, visited, 1, 0, path, minPath, minCost);

        return minPath;
    }
    private static void tspUtil(List<List<Integer>> graph, boolean[] visited, int pos, int cost, int[] path, int[] minPath, int minCost) {
        int n = graph.size();

        if (pos == n) {
            // Si hemos visitado todos los nodos, comparamos el costo actual con el mínimo conocido
            if (cost + graph.get(path[pos - 1]).get(0) < minCost) {
                System.arraycopy(path, 0, minPath, 0, n);
                minPath[n] = path[0];
            }
            return;
        }

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                visited[i] = true;
                path[pos] = i;
                tspUtil(graph, visited, pos + 1, cost + graph.get(path[pos - 1]).get(i), path, minPath, minCost);
                visited[i] = false;
            }
        }
    }
    private static int calculatePathLength(List<List<Integer>> graph, int[] path) {
        int length = 0;
        for (int i = 0; i < path.length - 1; i++) {
            length += graph.get(path[i]).get(path[i + 1]);
        }
        return length;
    }
    public void setDistancias(){
        this.distancias = generarMatrizAdyacencia(this.coordenadas);
    }
    public int[] getRutaMasCorta (){
        this.path = tsp(this.distancias);
        return this.path;
    }
    public int getLongitudRuta (){
        return calculatePathLength(this.distancias, this.path);
    }

}

