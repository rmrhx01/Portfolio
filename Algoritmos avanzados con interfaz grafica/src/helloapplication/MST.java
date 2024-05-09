package helloapplication;


import java.util.*;

class MST {

    static class Pair {
        int v;
        int wt;

        Pair(int v, int wt) {
            this.v = v;
            this.wt = wt;
        }
    }

    //Algoritmo de Prim para MST
    //Complejidad O(V^2) 
    int[][] primMST(int[][] matriz) {
        int n = matriz.length;
        int[] parent = new int[n];
        int[] key = new int[n];
        boolean[] mstSet = new boolean[n];
        List<List<Pair>> lista_adj = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            lista_adj.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] != 0) {

                    lista_adj.get(i).add(new Pair(j, matriz[i][j]));
                }
            }
        }
        
        if(this.hasIsolatedNodes(lista_adj)){
            return null;
        }

        for (int i = 0; i < n; i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        key[0] = 0;
        parent[0] = -1;

        for (int j = 0; j < n - 1; j++) {
            int min_ver = minKey(key, mstSet);
            mstSet[min_ver] = true;

            for (Pair pair : lista_adj.get(min_ver)) {
                int ver_adj = pair.v;
                int dist = pair.wt;
                if (!mstSet[ver_adj] && dist < key[ver_adj]) {
                    parent[ver_adj] = min_ver;
                    key[ver_adj] = dist;
                }
            }
        }

        return returnMST(parent, lista_adj);
    }

    int minKey(int key[], boolean mstSet[]) {
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (int v = 0; v < key.length; v++) {
            if (!mstSet[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    int[][] returnMST(int parent[], List<List<Pair>> lista_adj) {
        int result [][] = new int[lista_adj.size()-1][3];

        for (int i = 1; i < lista_adj.size(); i++) {
            result[i-1][0] = parent[i]+1;
            result[i-1][1] = i+1;
            result[i-1][2] = lista_adj.get(i).get(parent[i]).wt;

        }
        return result;
    }
    private boolean hasIsolatedNodes(List<List<Pair>> lista_adj) {
        for (int i = 0; i < lista_adj.size(); i++) {
            if (lista_adj.get(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        MST t = new MST();

        int graph[][] = {
                { 0, 16, 45, 32 },
                { 16, 0, 18, 21 },
                { 45, 18, 0, 7 },
                { 32, 21, 7, 0 }
        };

        t.primMST(graph);
    }
}

