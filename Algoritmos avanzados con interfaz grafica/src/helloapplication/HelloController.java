package helloapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.awt.Point;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileWriter;
import java.io.IOException;

public class HelloController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    FileChooser fileChooser = new FileChooser();
    private List<List<Integer>> coordenadas= new ArrayList<>();
    public Graph graph;
    private List<List<Integer>> centrales;

    @FXML
    void onCentralsClick(ActionEvent event) {
        Point[] p = new Point[this.centrales.size()];
        System.out.println(this.centrales.size());
        for(int i = 0; i < this.centrales.size(); i++){
            p[i] = new Point(this.centrales.get(i).get(0),this.centrales.get(i).get(1));
        }
   
        
        Voronoi voronoiDiagram = new Voronoi(800,600,this.graph.getNextId(),p);
        voronoiDiagram.generateVoronoiDiagram();
        System.out.println("Here");
        List<Voronoi.Edge> edges = voronoiDiagram.getEdges();
        anchorPane.getChildren().clear();
        for(int i = 0; i < this.centrales.size(); i++){
            drawCircle(p[i].x,p[i].y);
        }
        for(Voronoi.Edge e: edges){
            drawLine(e.start.x, e.start.y, e.end.x, e.end.y, Color.LIME);
        }
    }
    @FXML
    void onMaxFluxClick(ActionEvent event) {
        Flow flujo = new Flow();
        String[] name = new String[2];
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Choose node");
        dialog.setHeaderText("Enter the name of the node you want to start at:");
        dialog.showAndWait().ifPresent(result -> {
            name[0] = result;
        });
        dialog.setTitle("Choose node");
        dialog.setHeaderText("Enter the name of the node you want to end at:");
        dialog.showAndWait().ifPresent(result -> {
            name[1] = result;
        });
        int nodoInicio = Integer.parseInt(name[0].substring(1))-1;
        int nodoFinal = Integer.parseInt(name[1].substring(1))-1;
        int maxFlujo = flujo.fordFulkerson(this.graph.getCapacityMatrix(),nodoInicio,nodoFinal);
        showAlertBox("Flujo maximo",String.valueOf(maxFlujo));
    }
    @FXML
    void onAddNodeClick(ActionEvent event) {
        String[] name = new String[1];
        int id;
        List<Integer> coordinates= new ArrayList<>();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Node");
        dialog.setHeaderText("Enter the name of the node you want to add:");
        dialog.showAndWait().ifPresent(result -> {
            name[0] = result;
        });
        TextInputDialog dialog2 = new TextInputDialog();
        dialog2.setTitle("Add Node");
        dialog2.setHeaderText("Enter the coordinate X of the node you want to add:");
        dialog2.showAndWait().ifPresent(result -> {
            coordinates.add ( Integer.parseInt(result));
        });
        TextInputDialog dialog3 = new TextInputDialog();
        dialog3.setTitle("Add Node");
        dialog3.setHeaderText("Enter the coordinate Y of the node you want to add:");
        dialog3.showAndWait().ifPresent(result -> {
            coordinates.add ( Integer.parseInt(result));
        });
        Node node = new Node(name[0], coordinates,this.graph.getNextId());
        this.graph.addNode(node);
        drawNodes();

    }
    @FXML
     void onOpenFileClick(ActionEvent event) {
        File file = fileChooser.showOpenDialog(new Stage());
        try {
            JsonScanner reader = new JsonScanner(file.getPath());
            reader.leerArchivo();
            this.graph = reader.graph;
            this.centrales = reader.centrales;
            drawNodes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    @FXML
    void onSaveFileClick(ActionEvent event) {
       JSONObject file = new JSONObject();
       JSONObject ciudad = new JSONObject();
       JSONArray colonias = new JSONArray();
       for(Node n:this.graph.getNodes()){
           JSONObject colonia = new JSONObject();
           colonia.put("nombre",n.getName());
           colonia.put("coordenadaX",String.valueOf(n.getCoordinates().get(0)));
           colonia.put("coordenadaY",String.valueOf(n.getCoordinates().get(1)));
           colonias.put(colonia);
       }
       JSONArray enlaces = new JSONArray();
       for(Link l:this.graph.getLinks()){
           JSONObject enlace = new JSONObject();
           enlace.put("coloniaInicial",l.getSourceNode().getName());
           enlace.put("coloniaFinal",l.getTargetNode().getName());
           enlace.put("distancia",String.valueOf(l.getDistance()));
           enlace.put("capacidad",String.valueOf(l.getCapacity()));
           enlaces.put(enlace);
       }
       JSONArray centrales = new JSONArray();
       for(int i = 0; i < this.centrales.size(); i++){
           JSONObject central = new JSONObject();
           central.put("x", String.valueOf(this.centrales.get(i).get(0)));
           central.put("y", String.valueOf(this.centrales.get(i).get(1)));
           centrales.put(central);
       }
       JSONObject temp = new JSONObject();
       temp.put("colonias",colonias);
       temp.put("enlaces",enlaces);
       temp.put("centrales",centrales);
       file.put("ciudad",temp);
       
       
       
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save file");
        dialog.setHeaderText("Enter the desired name for the file:");
        dialog.showAndWait().ifPresent(result -> {
            try (FileWriter fileWriter = new FileWriter(result + ".json")) {
            fileWriter.write(file.toString());
            showAlertBox("File saved","File saved succesfully");
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        });
   }
     
    @FXML
    void onEditNodeClick(ActionEvent event) {
        String[] name = new String[1];
        List<Integer> coordinates= new ArrayList<>();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Node");
        dialog.setHeaderText("Enter the name of the node you want to edit:");
        dialog.showAndWait().ifPresent(result -> {
            Node node = this.graph.getNode(result);
            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Edit Node");
            dialog2.setHeaderText("Enter the new name of the node you want to edit:");
            dialog2.showAndWait().ifPresent(result2 -> {
                name[0] = result2;
            });
            TextInputDialog dialog3 = new TextInputDialog();
            dialog3.setTitle("Edit Node");
            dialog3.setHeaderText("Enter the coordinate X of the node you want to edit:");
            dialog3.showAndWait().ifPresent(result3 -> {
                coordinates.add(Integer.parseInt(result3));
            });
            TextInputDialog dialog4 = new TextInputDialog();
            dialog4.setTitle("Edit Node");
            dialog4.setHeaderText("Enter the coordinate Y of the node you want to edit:");
            dialog4.showAndWait().ifPresent(result4 -> {
                coordinates.add(Integer.parseInt(result4));
            });
            Node newNode = new Node(name[0], coordinates,node.getId());
            this.graph.editNode(node, newNode);
            drawNodes();
        });
    }
    @FXML
    void onDeleteNodeClick(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Node");
        dialog.setHeaderText("Enter the name of the node you want to delete:");
        dialog.showAndWait().ifPresent(result -> {
            this.graph.deleteNode(this.graph.getNode(result));
            drawNodes();

        });
    }
    @FXML
    void onAddLinkClick(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Link");
        dialog.setHeaderText("Enter the name of the initial node of the link you want to add:");
        dialog.showAndWait().ifPresent(result -> {
            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Add Link");
            dialog2.setHeaderText("Enter the name of the final node of the link you want to add:");
            dialog2.showAndWait().ifPresent(result2 -> {
                TextInputDialog dialog3 = new TextInputDialog();
                dialog3.setTitle("Add Link");
                dialog3.setHeaderText("Enter the distance of the link you want to add:");
                dialog3.showAndWait().ifPresent(result3 -> {
                    TextInputDialog dialog4 = new TextInputDialog();
                    dialog4.setTitle("Add Link");
                    dialog4.setHeaderText("Enter the capacity of the link you want to add:");
                    dialog4.showAndWait().ifPresent(result4 -> {
                        Node node1 = this.graph.getNode(result);
                        Node node2 = this.graph.getNode(result2);
                        if(this.graph.getLink(result3, result4) == null){
                            this.graph.addLink(new Link(node1, node2, Integer.parseInt(result3), Integer.parseInt(result4)));
                            drawNodes();
                        } else {
                            
                        }
                    });
                });
            });
        });
    }
    @FXML
    void onDeleteLinkClick(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Link");
        dialog.setHeaderText("Enter the name of the initial node of the link you want to delete:");
        dialog.showAndWait().ifPresent(result -> {
            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Delete Link");
            dialog2.setHeaderText("Enter the name of the final node of the link you want to delete:");
            dialog2.showAndWait().ifPresent(result2 -> {
                this.graph.deleteLink(this.graph.getLink(result, result2));
                drawNodes();
            });
        });
    }
    @FXML
    void onShowGraphClick(ActionEvent event) {
        drawNodes();
    }

    @FXML
    void onMSTClick(ActionEvent event) {
        drawNodes();
        MST mst = new MST();
        int[][] matrix = mst.primMST(this.graph.getDistanceMatrix());
        if (matrix != null){
            for (int i = 0; i < matrix.length; i++) {
                    int x = this.graph.getNode(matrix[i][0]-1).getCoordinates().get(0);
                    int y = this.graph.getNode(matrix[i][0]-1).getCoordinates().get(1);
                    int x2 = this.graph.getNode(matrix[i][1]-1).getCoordinates().get(0);
                    int y2 = this.graph.getNode(matrix[i][1]-1).getCoordinates().get(1);

                    drawLine(x, y, x2, y2, Color.RED);
                    drawLabel(matrix[i][2] + "", (x + x2) / 2, (y + y2) / 2, Color.RED);
            }
        } else {
            showAlertBox("Error","The current graph does not have a valid solution for this problem");
        }
    }

    @FXML
    void onShortestRouteClick(ActionEvent event) {
        setCoordenadas();
        ShortestRoute shortestRoute = new ShortestRoute(coordenadas);
        shortestRoute.setDistancias();
        int[] rutaMasCorta = shortestRoute.getRutaMasCorta();
        int longitudRuta = shortestRoute.getLongitudRuta();
        System.out.println(longitudRuta);
        anchorPane.getChildren().clear();
        drawNodes();
        for (int i = 0; i < rutaMasCorta.length - 1; i++) {
            int x1 = coordenadas.get(rutaMasCorta[i]).get(0);
            int y1 = coordenadas.get(rutaMasCorta[i]).get(1);
            int x2 = coordenadas.get(rutaMasCorta[i + 1]).get(0);
            int y2 = coordenadas.get(rutaMasCorta[i + 1]).get(1);
            drawLine(x1, y1, x2, y2, Color.LIGHTBLUE);

            int xlabel = (x1 + x2) / 2;
            int ylabel = (y1 + y2) / 2;
            drawLabel(Integer.toString(i + 1), xlabel, ylabel, Color.BLUEVIOLET);
        }
    }
    private void setCoordenadas(){
        this.coordenadas = new ArrayList<>();
        List<Node> nodes = this.graph.getNodes();
        for(int i = 0; i < nodes.size(); i++){
            List<Integer> coordenadas = nodes.get(i).getCoordinates();
            this.coordenadas.add(coordenadas);
        }
    }
    private void drawLabel(String text, double x, double y,Color textColor){
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setTextFill(textColor);
        anchorPane.getChildren().add(label);
    }
    private void drawLine(int x1, int y1, int x2, int y2, Color color) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(color); // Set the line color

        // Add the line to the anchorPane
        anchorPane.getChildren().add(line);
    }
    private void drawNodes(){
        List<Node> nodes = this.graph.getNodes();
        anchorPane.getChildren().clear();
        for(int i = 0; i < nodes.size(); i++){
            int x  = nodes.get(i).getCoordinates().get(0);
            int y = nodes.get(i).getCoordinates().get(1);
            drawCircle(x,y);
            drawLabel(nodes.get(i).getName(),x,y,Color.BLACK);
        }
    }
    private void drawCircle(int x,int y){
        Circle circle = new Circle(x, y, 5);
        circle.setFill(Color.BLACK);
        anchorPane.getChildren().add(circle);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser.setInitialDirectory(new File("C:\\Users\\RMRHX\\Downloads\\"));
    }
    private void showAlertBox(String title, String contentText) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // You can set a custom header text if needed
        alert.setContentText(contentText);

        // Adding a custom button (e.g., an "OK" button)
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);

        // Handling the button click event
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                System.out.println("OK button clicked");
                // You can add custom actions here if needed
            }
        });
    }
}