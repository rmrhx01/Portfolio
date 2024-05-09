package helloapplication;

import java.util.LinkedList;

class Flow {
    private Integer vertices;
    
    //Funcion auxiliar para saber si todavia existe algun camino posible
    boolean bfs(int residualGraph[][], int source, int sink, int parent[]) {
        boolean visited[] = new boolean[vertices];
        for (int i = 0; i < vertices; ++i)
            visited[i] = false;

        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < vertices; v++) {
                if (visited[v] == false && residualGraph[u][v] > 0) {
                    if (v == sink) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        return false;
    }

    //Algoritmo de Ford-Fulkerson para encontrar el flujo maximo entre dos puntos
    //Complejidad O(Flujo*E)
    int fordFulkerson(int originalGraph[][], int source, int sink) {
        vertices = originalGraph.length;
        int u, v;

        int residualGraph[][] = new int[vertices][vertices];

        for (u = 0; u < vertices; u++)
            for (v = 0; v < vertices; v++)
                residualGraph[u][v] = originalGraph[u][v];

        int parent[] = new int[vertices];

        int maxFlow = 0;

        while (bfs(residualGraph, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }
}
