package helloapplication;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonScanner {
    private String file_path;
    private List<List<Integer>> coordenadas;
    private List<List<Integer>> distancias;
    private List<List<Integer>> capacidades;
    public List<List<Integer>> centrales;
    public Graph graph;

    public JsonScanner(String file_path) {
        this.file_path = file_path;
        this.centrales = new ArrayList<>();
    }

    //Funcion principal para leer el JSON con el formato establecido
    public void leerArchivo() {
        try {
            this.graph = new Graph();
            String content = new String(Files.readAllBytes(Paths.get(file_path)));
            JSONObject ciudadObject = new JSONObject(content).getJSONObject("ciudad");

            JSONArray colonias = ciudadObject.getJSONArray("colonias");
            JSONArray enlaces = ciudadObject.getJSONArray("enlaces");
            JSONArray centrales = ciudadObject.getJSONArray("centrales");

            for (int i = 0; i < colonias.length(); i++) {
                JSONObject colonia = colonias.getJSONObject(i);
                int coordenadaX = colonia.getInt("coordenadaX");
                int coordenadaY = colonia.getInt("coordenadaY");
                List<Integer> coordenada = new ArrayList<>();
                coordenada.add(coordenadaX);
                coordenada.add(coordenadaY);
                Node node = new Node(colonia.getString("nombre"), coordenada, i);
                graph.addNode(node);
            }

            for (int i = 0; i < enlaces.length(); i++) {

                JSONObject enlace = enlaces.getJSONObject(i);
                String coloniaInicial = enlace.getString("coloniaInicial");
                int coloniaInicialInt = Character.getNumericValue(coloniaInicial.charAt(coloniaInicial.length() - 1)) - 1;
                String coloniaFinal = enlace.getString("coloniaFinal");
                int coloniaFinalInt = Character.getNumericValue(coloniaFinal.charAt(coloniaFinal.length() - 1)) - 1;
                int distancia = enlace.getInt("distancia");
                int capacidad = enlace.getInt("capacidad");
                Link link = new Link(graph.getNodes().get(coloniaInicialInt), graph.getNodes().get(coloniaFinalInt), distancia, capacidad);
                graph.addLink(link);
            }

            for (int i = 0; i < centrales.length(); i++) {
                JSONObject central = centrales.getJSONObject(i);
                int x = central.getInt("x");
                int y = central.getInt("y");
                List<Integer> centralData = new ArrayList<>();
                centralData.add(x);
                centralData.add(y);
                this.centrales.add(centralData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JsonScanner reader = new JsonScanner("C:\\Users\\giuli\\Desktop\\5\\algoritmos\\ActInt\\ActInt\\src\\grafo.json");
        reader.leerArchivo();

        List<List<Integer>> centrales = reader.getCentrales();

        System.out.println("Centrales:");
        for (List<Integer> central : centrales) {
            System.out.println(central);
        }
    }

    public List<List<Integer>> getCentrales() {
        return centrales;
    }
}


