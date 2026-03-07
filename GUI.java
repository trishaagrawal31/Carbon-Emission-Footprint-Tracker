package ZeroCarbonFootprintTracker;
import javafx.application.Application;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GUI extends Application {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("d-M-yyyy");
    private static final String STATE_FILE = "./ZeroCarbonFootprintTracker/greenprint_state.txt"; 
    private FootprintTracker   tracker            = new FootprintTracker("RIT GreenPrint 2026");
    private TransactionHandler transactionHandler = new TransactionHandler(tracker);



    private FlowPane dashboardGrid;
    private Label    summaryLabel;
    private Label    detailLabel;

    private TextField        idField;
    private Label            validationLabel;
    private TextField        userField;
    private TextField        dateField;
    private ComboBox<String> typeCombo;
    private VBox             dynamicFields;
    private ComboBox<String> dynamicCombo;
    private TextField        dynamicNumberField;
    private Label            feedbackLabel;
    private TextField        searchField;
    private ListView<String> searchList;

    private Label            totalEmissionsLabel;
    private TextField        offsetUserField;
    private ComboBox<String> paymentCombo;
    private TextArea         receiptArea;
    private ListView<String> offsetHistoryList;

    @Override
    public void start(Stage stage) {
        loadState();

        // ── TAB 1: Live Dashboard ────────────────────────────────
        VBox dashLayout = new VBox(12);
        dashLayout.setPadding(new Insets(20));

        Label dashHeading = makeHeading("Emission History Visualiser");

        summaryLabel = new Label("Total Entries: 0  |  Total CO2: 0.00 kg  |  Top Emitter: N/A");
        summaryLabel.setFont(new Font("Arial", 13));
        summaryLabel.setBackground(new Background(
            new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        summaryLabel.setPadding(new Insets(8));

        dashboardGrid = new FlowPane(10, 10);
        dashboardGrid.setPadding(new Insets(10));
        ScrollPane gridScroll = new ScrollPane(dashboardGrid);
        gridScroll.setPrefHeight(360);

        detailLabel = new Label("Click a card above to see its full details.");
        detailLabel.setFont(new Font("Arial", 12));
        detailLabel.setPadding(new Insets(8));
        detailLabel.setBackground(new Background(
            new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(4), Insets.EMPTY)));
        detailLabel.setWrapText(true);

        dashLayout.getChildren().addAll(
            dashHeading, summaryLabel, gridScroll,
            new Label("Entry Details:"), detailLabel);

        // ── TAB 2: Input & Operations ────────────────────────────
        VBox inputLayout = new VBox(12);
        inputLayout.setPadding(new Insets(20));

        Label inputHeading = makeHeading("Add New Emission Entry");

        // Source ID with real-time regex validation
        idField = new TextField();
        idField.setPromptText("e.g. T-001");
        idField.setMaxWidth(200);

        validationLabel = new Label();
        validationLabel.setFont(new Font("Arial", 12));

        // ChangeListener on textProperty — fires on every keystroke
        idField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs,
                                String oldVal, String newVal) {
                if (InputValidator.validateSourceId(newVal)) {
                    validationLabel.setText("✓ Valid");
                    validationLabel.setTextFill(Color.GREEN);
                } else {
                    validationLabel.setText("✗ Invalid  [A-Z]-###");
                    validationLabel.setTextFill(Color.RED);
                }
            }
        });

        HBox idRow = new HBox(10, idField, validationLabel);
        idRow.setAlignment(Pos.CENTER_LEFT);

        userField = new TextField();
        userField.setPromptText("e.g. Alice");
        userField.setMaxWidth(200);

        dateField = new TextField();
        dateField.setPromptText("e.g. 6-3-2026");
        dateField.setMaxWidth(200);

        // Emission type ComboBox (as required by the assignment)
        typeCombo = new ComboBox<String>();
        typeCombo.getItems().addAll("Transportation", "Food", "Energy");
        typeCombo.setPromptText("Select Emission Type");
        typeCombo.setMaxWidth(200);

        // Container that swaps content when type changes
        dynamicFields = new VBox(10);

        // ChangeListener on typeCombo — shows the correct sub-fields
        typeCombo.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs,
                                String oldVal, String newVal) {
                updateDynamicFields(newVal);
            }
        });

        // Feedback label (red for errors, green for success)
        feedbackLabel = new Label("");
        feedbackLabel.setFont(new Font("Arial", 12));
        feedbackLabel.setWrapText(true);

        Button addBtn = makeButton("Add Entry", Color.GREEN);
        addBtn.setOnAction(new AddEntryEvent());

        // Search by User section
        Label searchHeading = makeHeading("Search by User");

        searchField = new TextField();
        searchField.setPromptText("Enter username");
        searchField.setMaxWidth(200);

        Button searchBtn = makeButton("Search", Color.STEELBLUE);
        searchBtn.setOnAction(new SearchEvent());

        searchList = new ListView<String>();
        searchList.setPrefHeight(160);

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        inputLayout.getChildren().addAll(
            inputHeading,
            makeRow("Source ID:", idRow),
            makeRow("User Name:", userField),
            makeRow("Date:",      dateField),
            makeRow("Type:",      typeCombo),
            dynamicFields,
            feedbackLabel,
            addBtn,
            new Separator(),
            searchHeading,
            searchRow,
            new Label("Results:"),
            searchList
        );

        // ── TAB 3: Carbon Offset ─────────────────────────────────
        VBox offsetLayout = new VBox(12);
        offsetLayout.setPadding(new Insets(20));

        Label offsetHeading = makeHeading("Carbon Offset Market");

        // Total emissions banner
        totalEmissionsLabel = new Label("Current Total: 0.00 kg CO2");
        totalEmissionsLabel.setFont(new Font("Arial", 13));
        totalEmissionsLabel.setBackground(new Background(
            new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(5), Insets.EMPTY)));
        totalEmissionsLabel.setPadding(new Insets(8));
        totalEmissionsLabel.setMaxWidth(Double.MAX_VALUE);

        offsetUserField = new TextField();
        offsetUserField.setPromptText("Username to offset");
        offsetUserField.setMaxWidth(220);

        // ComboBox for payment method (as required by assignment)
        paymentCombo = new ComboBox<String>();
        paymentCombo.getItems().addAll("Credit Card", "Digital Wallet", "Campus Card");
        paymentCombo.setPromptText("Select Payment Method");
        paymentCombo.setMaxWidth(220);

        Label rateNote = new Label("Rate: $15 per 1000 kg CO2  ($0.015 per kg)");
        rateNote.setTextFill(Color.GRAY);
        rateNote.setFont(new Font("Arial", 11));

        Button buyBtn = makeButton("Purchase Offset", Color.DARKGREEN);

        Label processingLabel = new Label("");
        processingLabel.setFont(new Font("Arial", 12));
        processingLabel.setTextFill(Color.STEELBLUE);

        buyBtn.setOnAction(new PurchaseOffsetEvent(buyBtn, processingLabel));

        // Receipt display
        receiptArea = new TextArea("Your receipt will appear here after purchase.");
        receiptArea.setEditable(false);
        receiptArea.setPrefRowCount(10);
        receiptArea.setFont(new Font("Courier New", 12));

        // Offset History ListView (as required by assignment)
        Label histHeading = makeHeading("Offset History (this session):");

        offsetHistoryList = new ListView<String>();
        offsetHistoryList.setPrefHeight(130);

        offsetLayout.getChildren().addAll(
            offsetHeading,
            totalEmissionsLabel,
            makeRow("Username:",       offsetUserField),
            makeRow("Payment Method:", paymentCombo),
            rateNote,
            buyBtn,
            processingLabel,
            new Label("Receipt:"),
            receiptArea,
            histHeading,
            offsetHistoryList
        );

        // ── Assemble tabs ────────────────────────────────────────
        TabPane tabPane = new TabPane();

        Tab dashTab   = new Tab("Live Dashboard");
        Tab inputTab  = new Tab("Input & Operations");
        Tab offsetTab = new Tab("Carbon Offset");

        dashTab.setClosable(false);
        inputTab.setClosable(false);
        offsetTab.setClosable(false);

        dashTab.setContent(dashLayout);
        inputTab.setContent(inputLayout);
        offsetTab.setContent(offsetLayout);

        tabPane.getTabs().addAll(dashTab, inputTab, offsetTab);

        // Log on window close
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Logger.log(Logger.Operation.STATE_SAVED,
                    tracker.getEntries().size() + " entries in " + STATE_FILE);
            }
        });

        Scene scene = new Scene(tabPane, 1000, 720);
        stage.setTitle("GreenPrint - Carbon Footprint Tracker");
        stage.setScene(scene);
        stage.show();

        refreshDashboard();
    }

    private void refreshDashboard() {
        dashboardGrid.getChildren().clear();
        for (EmissionSource e : tracker.getEntries()) dashboardGrid.getChildren().add(buildCard(e));
        updateSummaryBar();
    }

    private Label buildCard(EmissionSource entry) {
        double val = entry.calculateEmission();

        Color bg = val < 1.0 ? Color.GREEN : val <= 3.0 ? Color.YELLOW : Color.RED;

        Label card = new Label(entry.getSourceID() + "\n" + String.format("%.2f", val) + " kg CO2");
        card.setPrefSize(95, 68);
        card.setAlignment(Pos.CENTER);
        card.setFont(new Font("Arial", 11));
        card.setTextFill(bg.equals(Color.YELLOW) ? Color.BLACK : Color.WHITE);
        card.setBackground(new Background(
            new BackgroundFill(bg, new CornerRadii(6), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(
            Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderStroke.THIN)));

        // Click → show full toString()

        card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                detailLabel.setText(entry.toString());
            }
        });


        return card;
    }

    private void updateSummaryBar() {
        summaryLabel.setText(String.format(
            "Total Entries: %d  |  Total CO2: %.2f kg  |  Top Emitter: %s",
            tracker.getEntries().size(), tracker.getTotalEmissions(), getTopEmitter()));
    }

    private String getTopEmitter() {
        String topUser  = "N/A";
        double topTotal = -1;
        for (EmissionSource e : tracker.getEntries()) {
            double t = tracker.getTotalEmissionsForUser(e.getUserName());
            if (t > topTotal) { topTotal = t; topUser = e.getUserName(); }
        }
        return topUser;
    }

    // Swaps dynamic fields — always the same pattern: ComboBox (string) then TextField (number)
    private void updateDynamicFields(String type) {
        dynamicFields.getChildren().clear();
        if (type == null) return;
        dynamicCombo       = new ComboBox<String>();
        dynamicNumberField = new TextField();
        dynamicCombo.setMaxWidth(200);
        dynamicNumberField.setMaxWidth(200);

        if (type.equals("Transportation")) {
            dynamicCombo.getItems().addAll("Car", "Bus", "Train", "Bicycle");
            dynamicCombo.setPromptText("Select Mode");
            dynamicNumberField.setPromptText("Distance in km");
            dynamicFields.getChildren().addAll(
                makeRow("Mode:",          dynamicCombo),
                makeRow("Distance (km):", dynamicNumberField));

        } else if (type.equals("Energy")) {
            dynamicCombo.getItems().addAll(
                "Grid", "Solar", "Wind", "Coal", "Natural Gas", "Nuclear", "Diesel", "Hydro");
            dynamicCombo.setPromptText("Select Source");
            dynamicNumberField.setPromptText("kWh consumed");
            dynamicFields.getChildren().addAll(
                makeRow("Source:",   dynamicCombo),
                makeRow("kWh Used:", dynamicNumberField));

        } else if (type.equals("Food")) {
            dynamicCombo.getItems().addAll("Vegan", "Vegetarian", "Poultry", "Beef");
            dynamicCombo.setPromptText("Select Meal Type");
            dynamicNumberField.setPromptText("Number of meals");
            dynamicFields.getChildren().addAll(
                makeRow("Meal Type:",   dynamicCombo),
                makeRow("# of Meals:", dynamicNumberField));
        }
    }

    private class AddEntryEvent implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            feedbackLabel.setTextFill(Color.RED);

            String id   = idField.getText().trim();
            String user = userField.getText().trim();
            String date = dateField.getText().trim();
            String type = typeCombo.getValue();

            // Validate Source ID (format + uniqueness)
            if (!InputValidator.validateUniqueSourceId(id, tracker)) {
                feedbackLabel.setText("✗ Source ID is invalid or already in use. Use [A-Z]-###");
                return;
            }
            if (user.isEmpty()) {
                feedbackLabel.setText("✗ User name cannot be empty.");
                return;
            }
            // Date validation using LocalDate.parse with d-M-yyyy format
            try {
                LocalDate.parse(date, DATE_FMT);
            } catch (Exception dateEx) {
                feedbackLabel.setText("✗ Date invalid. Use format d-M-yyyy e.g. 6-3-2026");
                return;
            }
            if (type == null) {
                feedbackLabel.setText("✗ Please select an emission type.");
                return;
            }

            // Both shared fields used for every type: dynamicCombo = string, dynamicNumberField = number
            if (dynamicCombo.getValue() == null) {
                feedbackLabel.setText("✗ Please select an option.");
                return;
            }
            String stringVal = dynamicCombo.getValue().toLowerCase();
            double numVal;
            try {
                numVal = Double.parseDouble(dynamicNumberField.getText().trim());
                if (numVal <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("✗ Please enter a valid positive number.");
                return;
            }

            EmissionSource entry;
            if (type.equals("Transportation")) {
                if (stringVal.equals("bicycle")) stringVal = "cycle";
                entry = new TransportationEmission(id, "Transportation", date, user, numVal, stringVal);
            } else if (type.equals("Energy")) {
                entry = new EnergyEmission(id, "Energy", date, user, numVal, stringVal);
            } else if (type.equals("Food")) {
                if (numVal != (int) numVal) {
                    feedbackLabel.setText("✗ Number of meals must be a whole number.");
                    return;
                }
                entry = new FoodEmission(id, "Food", date, user, stringVal, (int) numVal);
            } else { return; }

            tracker.addEntry(entry);
            Logger.log(Logger.Operation.ENTRY_ADDED, entry.toString());
            appendStateLine(type + "|" + id + "|" + date + "|" + user + "|" + stringVal + "|" + numVal);
            refreshDashboard();

            idField.clear();
            userField.clear();
            dateField.clear();
            typeCombo.setValue(null);
            dynamicFields.getChildren().clear();

            feedbackLabel.setTextFill(Color.GREEN);
            feedbackLabel.setText("✓ Entry added and saved: " + id);
        }
    }

    private class SearchEvent implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            searchList.getItems().clear();
            String name = searchField.getText().trim();

            if (name.isEmpty()) {
                searchList.getItems().add("Please enter a username to search.");
                return;
            }

            boolean found = false;
            for (EmissionSource e : tracker.getEntries()) {
                if (e.getUserName().equalsIgnoreCase(name)) {
                    searchList.getItems().add(e.toString());
                    found = true;
                }
            }
            if (!found) {
                searchList.getItems().add("No entries found for: \"" + name + "\"");
            } else {
                searchList.getItems().add(String.format(
                    "--- Total for '%s': %.2f kg CO2 ---", name, tracker.getTotalEmissionsForUser(name)));
            }
        }
    }


    private class PurchaseOffsetEvent implements EventHandler<ActionEvent> {
        private final Button buyBtn;
        private final Label  processingLabel;

        public PurchaseOffsetEvent(Button buyBtn, Label processingLabel) {
            this.buyBtn          = buyBtn;
            this.processingLabel = processingLabel;
        }

        @Override
        public void handle(ActionEvent event) {
            String user    = offsetUserField.getText().trim();
            String payment = paymentCombo.getValue();

            if (user.isEmpty()) {
                receiptArea.setText("Please enter a username.");
                return;
            }
            if (payment == null) {
                receiptArea.setText("Please select a payment method.");
                return;
            }

            buyBtn.setDisable(true);
            processingLabel.setText("Processing transaction... please wait 2 seconds.");

            // PauseTransition — NOT Thread.sleep() as required by assignment
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    String receipt = transactionHandler.CalculateOffSet(user, payment);
                    receiptArea.setText(receipt);

                    Logger.log(Logger.Operation.OFFSET_PURCHASED,
                        "User: " + user + " | Payment: " + payment);

                    // Add to offset history ListView
                    String summary = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        + "  |  " + user + "  |  " + payment;
                    offsetHistoryList.getItems().add(summary);

                    processingLabel.setText("✓ Transaction complete.");
                    buyBtn.setDisable(false);
                }
            });
            pause.play();
        }
    }


    // =============================================================
    // STATE PERSISTENCE
    // =============================================================

    private void loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) return;

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                EmissionSource entry = fromStateLine(line);
                if (entry != null) {
                    tracker.addEntry(entry);
                    count++;
                }
            }
            if (count > 0) {
                Logger.log(Logger.Operation.STATE_LOADED,
                    "Restored " + count + " entries from " + STATE_FILE);
            }
        } catch (IOException e) {
            System.err.println("loadState failed: " + e.getMessage());
        }
    }

    // Appends a single new entry line to the state file — called immediately after entry creation
    // Line is built from user-entered values directly, no getters needed
    private void appendStateLine(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("appendStateLine failed: " + e.getMessage());
        }
    }

    // p[0]=category  p[1]=id  p[2]=date  p[3]=user  p[4]=stringVal  p[5]=numVal
    private EmissionSource fromStateLine(String line) {
        try {
            String[] p = line.split("\\|");
            double numVal = Double.parseDouble(p[5]);
            if (p[0].equals("Transportation")) return new TransportationEmission(p[1], p[0], p[2], p[3], numVal, p[4]);
            if (p[0].equals("Energy"))         return new EnergyEmission(p[1], p[0], p[2], p[3], numVal, p[4]);
            if (p[0].equals("Food"))           return new FoodEmission(p[1], p[0], p[2], p[3], p[4], (int) numVal);
        } catch (Exception e) {
            System.err.println("Could not parse state line: " + line);
        }
        return null;
    }

    // =============================================================
    // UTILITY HELPERS  (same pattern as makeLabel in GUI1.java)
    // =============================================================

    private Label makeHeading(String text) {
        Label l = new Label(text);
        l.setFont(new Font("Arial", 16));
        l.setTextFill(Color.DARKGREEN);
        return l;
    }



    private Button makeButton(String text, Color bg) {
        Button btn = new Button(text);
        btn.setFont(new Font("Arial", 13));
        btn.setTextFill(Color.WHITE);
        btn.setBackground(new Background(
            new BackgroundFill(bg, new CornerRadii(5), Insets.EMPTY)));
        btn.setPadding(new Insets(8, 18, 8, 18));
        return btn;
    }

    private HBox makeRow(String labelText, Node control) {
        Label lbl = new Label(labelText);
        lbl.setMinWidth(140);
        lbl.setFont(new Font("Arial", 12));
        HBox row = new HBox(10, lbl, control);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public static void main(String[] args) {
        launch(args);
    }
}