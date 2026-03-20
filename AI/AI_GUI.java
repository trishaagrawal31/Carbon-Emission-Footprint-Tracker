package ZeroCarbonFootprintTracker.AI;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

import ZeroCarbonFootprintTracker.EmissionSource;
import ZeroCarbonFootprintTracker.EnergyEmission;
import ZeroCarbonFootprintTracker.FoodEmission;
import ZeroCarbonFootprintTracker.FootprintTracker;
import ZeroCarbonFootprintTracker.TransportationEmission;

public class AI_GUI extends Application {
    private FootprintTracker tracker = new FootprintTracker("GreenPrint");
    private Label totalLabel = new Label("Grand Total System Impact: 0.00 kg CO2");
    private ListView<EmissionSource> logListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        // Load data from text file on startup
        PersistenceManager.loadState(tracker);
        
        TabPane tabPane = new TabPane();
        
        // Tab 1: Operations (The main control center)
        Tab opsTab = new Tab("Operations", createOperationsTab());
        opsTab.setClosable(false);

        // Tab 2: Live Dashboard (Color-coded list)
        Tab dashTab = new Tab("Live Dashboard", createDashboard());
        dashTab.setClosable(false);

        // Tab 3: Carbon Offset (Payments and Receipts)
        Tab offsetTab = new Tab("Carbon Offset", createOffsetTab());
        offsetTab.setClosable(false);

        tabPane.getTabs().addAll(opsTab, dashTab, offsetTab);
        
        // Initial UI Update
        updateDashboard();
        
        Scene scene = new Scene(tabPane, 950, 750);
        primaryStage.setTitle("GreenPrint Carbon Tracker | AI Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- TAB 1: OPERATIONS (ADD & SEARCH) ---
    private VBox createOperationsTab() {
        VBox mainLayout = new VBox(25);
        mainLayout.setPadding(new Insets(20));

        // ADD ENTRY BLOCK
        VBox addBox = new VBox(15);
        addBox.setStyle("-fx-border-color: #2e7d32; -fx-padding: 20; -fx-border-radius: 10; -fx-background-color: #f1f8e9;");
        Label addHeader = new Label("1. LOG NEW EMISSION DATA");
        addHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1b5e20;");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(12);
        
        TextField userIn = new TextField(); userIn.setPromptText("e.g., user123");
        TextField nameIn = new TextField(); nameIn.setPromptText("e.g., John Doe");
        TextField dateIn = new TextField(LocalDate.now().toString()); 
        
        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Transportation", "Energy", "Food");
        catBox.setPromptText("Select Category");

        ComboBox<String> subBox = new ComboBox<>();
        subBox.setPromptText("Select Type");
        subBox.setDisable(true);

        TextField valIn = new TextField(); valIn.setPromptText("Quantity");

        catBox.setOnAction(e -> {
            subBox.setDisable(false);
            subBox.getItems().clear();
            String cat = catBox.getValue();
            if ("Transportation".equals(cat)) {
                subBox.getItems().addAll("car", "bus", "train", "cycle");
                valIn.setPromptText("Distance (km)");
            } else if ("Energy".equals(cat)) {
                subBox.getItems().addAll("grid", "solar", "wind", "coal", "diesel");
                valIn.setPromptText("Energy (kWh)");
            } else if ("Food".equals(cat)) {
                subBox.getItems().addAll("vegan", "vegetarian", "poultry", "beef");
                valIn.setPromptText("Num. of Meals");
            }
        });

        grid.addRow(0, new Label("Username:"), userIn, new Label("Full Name:"), nameIn);
        grid.addRow(1, new Label("Date (YYYY-MM-DD):"), dateIn, new Label("Category:"), catBox);
        grid.addRow(2, new Label("Specific Type:"), subBox, new Label("Value:"), valIn);

        Button addBtn = new Button("Submit Transaction");
        addBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        ProgressIndicator pInd = new ProgressIndicator();
        pInd.setVisible(false); pInd.setMaxSize(25, 25);

        addBtn.setOnAction(e -> handleAddTransaction(userIn, nameIn, dateIn, catBox, subBox, valIn, addBtn, pInd));

        addBox.getChildren().addAll(addHeader, grid, new HBox(15, addBtn, pInd));

        // SEARCH BLOCK
        VBox searchBox = new VBox(10);
        searchBox.setStyle("-fx-border-color: #1976d2; -fx-padding: 20; -fx-border-radius: 10; -fx-background-color: #e3f2fd;");
        Label searchHeader = new Label("2. AGGREGATE USER HISTORY");
        searchHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0d47a1;");

        TextField searchField = new TextField(); searchField.setPromptText("Enter Username to filter...");
        Button searchBtn = new Button("Search & Sum");
        TextArea searchRes = new TextArea(); searchRes.setEditable(false); searchRes.setPrefHeight(150);
        
        searchBtn.setOnAction(e -> handleUserSearch(searchField.getText(), searchRes));
        searchBox.getChildren().addAll(searchHeader, new HBox(10, searchField, searchBtn), searchRes);

        mainLayout.getChildren().addAll(addBox, searchBox);
        return mainLayout;
    }

    // --- TAB 2: LIVE DASHBOARD ---
    private VBox createDashboard() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label dashTitle = new Label("Live Activity Feed");
        dashTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        logListView.setCellFactory(lv -> new ListCell<EmissionSource>() {
            @Override
            protected void updateItem(EmissionSource item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    String[] catParts = item.getCategory().split(":");
                    String fullName = (catParts.length > 1) ? catParts[1] : "N/A";
                    
                    setText(String.format("[%s] USER: %s (%s) | %s", 
                            item.getDate(), item.getUserName().toUpperCase(), fullName, item.toString()));
                    
                    double val = item.calculateEmission();
                    // Color thresholds
                    if (val < 5.0) setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #1b5e20;");
                    else if (val <= 20.0) setStyle("-fx-background-color: #fff9c4; -fx-text-fill: #f57f17;");
                    else setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #b71c1c;");
                    
                    setStyle(getStyle() + "-fx-border-color: white; -fx-padding: 12; -fx-font-family: 'Segoe UI';");
                }
            }
        });

        vbox.getChildren().addAll(dashTitle, totalLabel, new Label("Real-time logs:"), logListView);
        VBox.setVgrow(logListView, Priority.ALWAYS);
        return vbox;
    }

    // --- TAB 3: OFFSET ---
    private VBox createOffsetTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.TOP_CENTER);
        
        Label offsetTitle = new Label("Carbon Offset Payment Terminal");
        offsetTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField payUser = new TextField(); payUser.setPromptText("Search Username");
        Button valBtn = new Button("Validate Balance");
        TextArea recArea = new TextArea(); recArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
        Button payBtn = new Button("Process Payment & Receipt");
        payBtn.setDisable(true);
        payBtn.setStyle("-fx-background-color: #1b5e20; -fx-text-fill: white; -fx-font-weight: bold;");

        valBtn.setOnAction(e -> {
            String target = payUser.getText().trim();
            double total = tracker.getEntries().stream()
                    .filter(en -> en.getUserName().equalsIgnoreCase(target))
                    .mapToDouble(EmissionSource::calculateEmission).sum();
            payBtn.setDisable(total <= 0);
        });

        payBtn.setOnAction(e -> {
            String user = payUser.getText().trim();
            String fullName = "System User";
            double totalKg = 0;
            for(EmissionSource es : tracker.getEntries()) {
                if(es.getUserName().equalsIgnoreCase(user)) {
                    totalKg += es.calculateEmission();
                    if(es.getCategory().contains(":")) fullName = es.getCategory().split(":")[1];
                }
            }
            recArea.setText(String.format(
                "==========================================\n" +
                "           GREENPRINT OFFICIAL            \n" +
                "==========================================\n" +
                " DATE:     %s\n" +
                " USERNAME: %s\n" +
                " NAME:     %s\n" +
                "------------------------------------------\n" +
                " AGGREGATED CO2:    %10.2f kg\n" +
                " OFFSET RATE:       $      0.15/kg\n" +
                "------------------------------------------\n" +
                " TOTAL PAID:        $%10.2f\n" +
                "==========================================\n" +
                "      STATUS: CARBON NEUTRAL SECURED      \n" +
                "==========================================", 
                LocalDate.now(), user.toUpperCase(), fullName.toUpperCase(), totalKg, totalKg * 0.15));
        });

        vbox.getChildren().addAll(offsetTitle, new HBox(15, payUser, valBtn), payBtn, recArea);
        return vbox;
    }

    // --- LOGIC HELPERS ---
    private void handleAddTransaction(TextField u, TextField n, TextField d, ComboBox<String> c, ComboBox<String> s, TextField v, Button b, ProgressIndicator p) {
        String user = u.getText().trim();
        String name = n.getText().trim();
        String date = d.getText().trim();
        String valS = v.getText().trim();

        // VALIDATION
        if (!user.matches("^[a-zA-Z0-9]+$") || !name.matches("^[a-zA-Z\\s]+$") || !date.matches("^\\d{4}-\\d{2}-\\d{2}$") || !valS.matches("^\\d+(\\.\\d+)?$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Data Format! Please check all fields.").show();
            return;
        }

        b.setDisable(true); p.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { Thread.sleep(2000); return null; }
            @Override protected void succeeded() {
                // Store Name inside Category field using tag delimiter ":"
                String categoryLabel = c.getValue() + ":" + name;
                addEntryToTracker(user, categoryLabel, s.getValue(), Double.parseDouble(valS), date);
                PersistenceManager.saveState(tracker.getEntries());
                updateDashboard();
                u.clear(); n.clear(); v.clear(); b.setDisable(false); p.setVisible(false);
            }
        };
        new Thread(task).start();
    }

    private void handleUserSearch(String target, TextArea res) {
        if (target == null || target.isEmpty()) return;
        double sum = 0;
        String fullName = "Not Recorded";
        StringBuilder sb = new StringBuilder();
        for (EmissionSource e : tracker.getEntries()) {
            if (e.getUserName().equalsIgnoreCase(target)) {
                sum += e.calculateEmission();
                if(e.getCategory().contains(":")) fullName = e.getCategory().split(":")[1];
                sb.append(String.format("[%s] Category: %-12s | Impact: %.2f kg\n", 
                        e.getDate(), e.getCategory().split(":")[0], e.calculateEmission()));
            }
        }
        res.setText("FULL REPORT FOR: " + target.toUpperCase() + " (" + fullName.toUpperCase() + ")\n" +
                   "----------------------------------------------------------\n" + sb.toString() + 
                   "----------------------------------------------------------\n" +
                   "AGGREGATED TOTAL: " + String.format("%.2f", sum) + " kg CO2");
    }

    private void addEntryToTracker(String user, String catInfo, String sub, double val, String date) {
        String id = "ID-" + (System.currentTimeMillis() % 10000);
        String cat = catInfo.split(":")[0];
        switch (cat) {
            case "Transportation" -> tracker.addEntry(new TransportationEmission(id, catInfo, date, user, val, sub));
            case "Energy" -> tracker.addEntry(new EnergyEmission(id, catInfo, date, user, val, sub));
            case "Food" -> tracker.addEntry(new FoodEmission(id, catInfo, date, user, sub, (int)val));
        }
    }

    private void updateDashboard() {
        totalLabel.setText(String.format("Grand Total System Impact: %.2f kg CO2", tracker.getTotalEmissions()));
        logListView.getItems().setAll(tracker.getEntries());
    }

    public static void main(String[] args) { launch(args); }
}
