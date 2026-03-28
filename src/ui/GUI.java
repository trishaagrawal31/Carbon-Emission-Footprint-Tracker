package ZeroCarbonFootprintTracker.src.ui;
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

import ZeroCarbonFootprintTracker.src.model.EmissionSource;
import ZeroCarbonFootprintTracker.src.model.EnergyEmission;
import ZeroCarbonFootprintTracker.src.model.FoodEmission;
import ZeroCarbonFootprintTracker.src.model.FootprintTracker;
import ZeroCarbonFootprintTracker.src.model.TransportationEmission;
import ZeroCarbonFootprintTracker.src.util.InputValidator;
import ZeroCarbonFootprintTracker.src.util.Logger;
import ZeroCarbonFootprintTracker.src.util.TransactionHandler;


/**
 * GUI for the ZeroCarbonFootprintTracker.
 * The GUI supports three tabs: live dashboard, input and operations, 
 * and carbon offset market. It manages state persistence, entry
 * validation, and transaction flow.
 * @author Trisha and Aaliya
 */
public class GUI extends Application {

    public static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("d-M-yyyy");
    public static final String STATE_FILE = "./ZeroCarbonFootprintTracker/greenprint_state.txt"; 
    private FootprintTracker tracker = new FootprintTracker("RIT GreenPrint 2026");
    private TransactionHandler transactionHandler = new TransactionHandler(tracker);

    private FlowPane dashboardGrid;
    private Label summaryLabel;
    private Label detailLabel;
    private TextField idField;
    private Label validationLabel;
    private TextField userField;
    private TextField dateField;
    private ComboBox<String> typeCombo;
    private VBox dynamicFields;
    private ComboBox<String> stringCombo;
    private TextField NumberField;
    private Label feedbackLabel;
    private TextField searchField;
    private ListView<String> searchList;
    private Label totalEmissionsLabel;
    private double updatedTotalEmissions;
    private TextField offsetUserField;
    private ComboBox<String> paymentCombo;
    private Label receiptArea;
    private ListView<String> offsetHistoryList;

    @Override
    /**
     * Initializes UI components, loads persisted state entries, and configures event handlers for dashboard interactions.
     * @param stage primary stage provided by the JavaFX runtime
     */
    public void start(Stage stage) {
        loadState();

        //TAB 1
        VBox dashLayout = new VBox(10);
        dashLayout.setPadding(new Insets(20));

        Label dashHeading = makeHeading("Emission History Visualiser");

        summaryLabel=new Label("Total Entries: 0  |  Total CO2: 0.00 kg  |  Top Emitter: N/A");
        summaryLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        dashboardGrid=new FlowPane(10, 10);
        ScrollPane gridScroll = new ScrollPane(dashboardGrid);
        gridScroll.setFitToWidth(true);
        detailLabel = new Label("Click a card above to see its full details.");
        detailLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(4), Insets.EMPTY)));
        detailLabel.setWrapText(true);
        dashLayout.getChildren().addAll(dashHeading,summaryLabel,gridScroll,new Label("Entry Details:"), detailLabel);

        //TAB 2
        VBox inputLayout = new VBox(10);
        inputLayout.setPadding(new Insets(20));
        Label inputHeading =makeHeading("Add New Emission Entry");
        idField =new TextField();// source ID
        idField.setPromptText("e.g. T-001");

        validationLabel=new Label();

        //realtime validation 
        idField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs,String oldVal, String newVal) {
                if (InputValidator.validateSourceId(newVal)) {
                    validationLabel.setText("✓ Valid");
                    validationLabel.setTextFill(Color.GREEN);
                } else {
                    validationLabel.setText("✗ Invalid  [A-Z]-###");
                    validationLabel.setTextFill(Color.RED);
                }
            }
        });

        HBox idRow =new HBox(idField, validationLabel);

        userField=new TextField();
        userField.setPromptText("e.g. Alice");
        dateField=new TextField();
        dateField.setPromptText("e.g. 6-3-2026");
        typeCombo=new ComboBox<String>();// Emission type ComboBox 
        typeCombo.getItems().addAll("Transportation", "Food", "Energy");
        typeCombo.setPromptText("Select Emission Type");
        dynamicFields=new VBox(10);// Container that swaps content when type changes

        typeCombo.valueProperty().addListener(new ChangeListener<String>() { // shows the correct sub-fields
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldVal, String newVal) {
                updateDynamicFields(newVal);
            }
        });

        feedbackLabel=new Label(""); // feedback label 
        Button addBtn=makeButton("Add Entry", Color.GREEN);
        addBtn.setOnAction(new AddEntryEvent());

        Label searchHeading=makeHeading("Search by User");// Search by User section
        searchField=new TextField();
        searchField.setPromptText("Enter username");
        Button searchBtn=makeButton("Search", Color.STEELBLUE);
        searchBtn.setOnAction(new SearchEvent());

        searchList=new ListView<String>();
        searchList.setMinHeight(150);

        HBox searchRow=new HBox(10, searchField, searchBtn);
        

        inputLayout.getChildren().addAll(inputHeading, makeRow("Source ID:", idRow),
        makeRow("User Name:", userField),makeRow("Date:",dateField),
            makeRow("Type:",typeCombo),dynamicFields, feedbackLabel,
            addBtn, searchHeading, searchRow,new Label("Results:"),searchList
        );

        //TAB 3: Carbon Offset 
        VBox offsetLayout=new VBox(10);
        offsetLayout.setPadding(new Insets(20));
        Label offsetHeading=makeHeading("Carbon Offset Market");

        totalEmissionsLabel=new Label("Current Total Emissions: 0.00 kg CO2");// Total emissions banner
        totalEmissionsLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(5), Insets.EMPTY)));
        offsetUserField=new TextField();
        offsetUserField.setPromptText("Username to offset");

        paymentCombo=new ComboBox<String>();// ComboBox for payment method
        paymentCombo.getItems().addAll("Credit Card", "Digital Wallet", "Campus Card");
        paymentCombo.setPromptText("Select Payment Method");
        Label rateNote=new Label("Rate: $15 per 1000 kg CO2  ($0.015 per kg)");
        rateNote.setTextFill(Color.GRAY);

        Button buyBtn=makeButton("Purchase Offset", Color.DARKGREEN);
        Label processingLabel=new Label("");
        processingLabel.setTextFill(Color.BLUE);
       
        buyBtn.setOnAction(new PurchaseOffsetEvent(buyBtn, processingLabel));

        
        receiptArea=new Label("Your receipt will appear here after purchase.");// Receiptdisplay
        receiptArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        receiptArea.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.THIN)));

        Label histHeading=makeHeading("Offset History:");
        offsetHistoryList= new ListView<String>();
        offsetHistoryList.setPrefHeight(130);
        offsetLayout.getChildren().addAll(offsetHeading,totalEmissionsLabel,makeRow("Username:", offsetUserField),
            makeRow("Payment Method:", paymentCombo),rateNote,buyBtn,processingLabel,
            new Label("Receipt:"),receiptArea,histHeading,offsetHistoryList
        );

        //Assemble tabs
        TabPane tabPane= new TabPane();
        Tab dashTab=new Tab("Live Dashboard");
        Tab inputTab=new Tab("Input & Operations");
        Tab offsetTab=new Tab("Carbon Offset");
        dashTab.setClosable(false);
        inputTab.setClosable(false);
        offsetTab.setClosable(false);
        dashTab.setContent(dashLayout);
        inputTab.setContent(inputLayout);
        offsetTab.setContent(offsetLayout);
        tabPane.getTabs().addAll(dashTab,inputTab,offsetTab);

        // Log on window close
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Logger.log(Logger.Operation.STATE_SAVED,
                    tracker.getEntries().size() + " entries in " + STATE_FILE);
            }
        });

        Scene scene = new Scene(tabPane,1000,700);
        stage.setTitle("GreenPrint - Carbon Footprint Tracker");
        stage.setScene(scene);
        stage.show();

        refreshDashboard();
        totalEmissionsLabel.setText(String.format("Current Total: %.2f kg CO2", tracker.getTotalEmissions()));

    }

    /**
     * Reloads all emission cards on the dashboard from tracker entries
     * and refreshes summary metrics.
     * @return void
     */
    private void refreshDashboard() {
        dashboardGrid.getChildren().clear();
        for (EmissionSource e:tracker.getEntries()) dashboardGrid.getChildren().add(buildCard(e));
            updateSummaryBar();
    }

    /**
     * Creates a colored label card representing an emission entry.
     * @param entry emission entry model
     * @return label node for display in dashboard grid
     */
    
    private Label buildCard(EmissionSource entry) {
        double val = entry.calculateEmission();

        Color bg=val < 1.0 ?Color.GREEN:val<= 3.0?Color.YELLOW:Color.RED;
        Label card = new Label(entry.getSourceID()+"\n" +String.format("%.2f", val)+" kg CO2");
        card.setPrefSize(95, 80);
        card.setAlignment(Pos.CENTER);
        card.setTextFill(bg.equals(Color.YELLOW)?Color.BLACK:Color.WHITE);
        card.setBackground(new Background(new BackgroundFill(bg, new CornerRadii(6), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderStroke.THIN)));
        // Click - show full toString()
        card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                detailLabel.setText(entry.toString());
            }
        });
        return card;
    }

    /**
     * Updates the top summary bar with current totals and top contributor.
     * @return void
     */
    private void updateSummaryBar() { 
        summaryLabel.setText(
            "Total Entries: "+tracker.getEntries().size()+" |  Total CO2: " +tracker.getTotalEmissions() +" kg  |  Top Emitter: "+ getTopEmitter());
    }

    /**
     * Determines the user with the highest total emissions.
     * @return username of the top emitter, or "N/A" if none
     */
    private String getTopEmitter() {
        String topUser  = "N/A";
        double topTotal = 0;
        for (EmissionSource e:tracker.getEntries()) {
            double t = tracker.getTotalEmissionsForUser(e.getUserName());
            if (t >topTotal) {topTotal=t; topUser = e.getUserName(); }
        }
        return topUser;
    }

    /**
     * Updates the form section that is displayed for the selected emission type.
     * @param type selected emission type, e.g. "Transportation", "Food", "Energy"
     * @return void
     */
    // always the same pattern: ComboBox (string) then TextField (number)
    private void updateDynamicFields(String type) {
        dynamicFields.getChildren().clear();
        if (type == null) return;
        stringCombo = new ComboBox<String>();
        NumberField = new TextField();

        if (type.equals("Transportation")) {
            stringCombo.getItems().addAll("Car", "Bus", "Train", "Cycle");
            stringCombo.setPromptText("Select Mode");
            NumberField.setPromptText("Distance in km");
            dynamicFields.getChildren().addAll(makeRow("Mode:", stringCombo),makeRow("Distance (km):", NumberField));

        } else if (type.equals("Energy")) {
            stringCombo.getItems().addAll("Grid", "Solar", "Wind", "Coal", "Natural Gas", "Nuclear", "Diesel", "Hydro");
            stringCombo.setPromptText("Select Source");
            NumberField.setPromptText("kWh consumed");
            dynamicFields.getChildren().addAll(makeRow("Source:",stringCombo),makeRow("kWh Used:", NumberField));

        } else if (type.equals("Food")) {
            stringCombo.getItems().addAll("Vegan", "Vegetarian", "Poultry", "Beef");
            stringCombo.setPromptText("Select Meal Type");
            NumberField.setPromptText("Number of meals");
            dynamicFields.getChildren().addAll( makeRow("Meal Type:", stringCombo),makeRow("No. of Meals:", NumberField));
        }
    }

    /**
     * Event handler for adding a new entry to the emission tracker.
     * Performs input validation, model creation, persistence update, and UI refresh.
     */
    private class AddEntryEvent implements EventHandler<ActionEvent> {
        /**
         * Handle click of "Add Entry" button. Validates user input, creates
         * the corresponding EmissionSource, updates state, and refreshes the UI.
         * @param event action event triggered by button press
         */

        @Override
        public void handle(ActionEvent event) {
            feedbackLabel.setTextFill(Color.RED);
            String id = idField.getText().trim();
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
            // Date validation with d-M-yyyy format
            try {
                LocalDate.parse(date, DATE_FMT);
            } catch (Exception dateEx) {
                feedbackLabel.setText("✗ Date invalid. Use format d-M-yyyy e.g. 6-3-2026");
                return;
            }
            if (type== null) {
                feedbackLabel.setText("✗ Please select an emission type.");
                return;
            }
            // Both shared fields used for every type
            if (stringCombo.getValue()==null) {
                feedbackLabel.setText("✗ Please select an option.");
                return;
            }
            String stringVal=stringCombo.getValue().toLowerCase();
            double numVal;
            try {
                numVal = Double.parseDouble(NumberField.getText().trim());
                if (numVal<= 0){
                    feedbackLabel.setText("✗ Number must be a positive value.");
                    return;
                }
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("✗ Please enter a valid positive number.");
                return;
            }

            EmissionSource entry;
            if (type.equals("Transportation")) {
                entry=new TransportationEmission(id, "Transportation", date, user, numVal, stringVal);
            } else if (type.equals("Energy")) {
                entry=new EnergyEmission(id, "Energy", date, user, numVal, stringVal);
            } else if (type.equals("Food")) {
                if (numVal!= (int)numVal) {
                    feedbackLabel.setText("✗ Number of meals must be a whole number.");
                    return;
                }
                entry = new FoodEmission(id, "Food", date, user, stringVal, (int) numVal);
            } else { return; }

            tracker.addEntry(entry);
            Logger.log(Logger.Operation.ENTRY_ADDED, entry.toString());
            appendStateLine(type +"|"+id +"|"+date+"|"+user+"|"+stringVal+"|"+numVal);
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

    /**
     * Event handler for searching entries by username and displaying matches.
     */
    private class SearchEvent implements EventHandler<ActionEvent> {
        /**
         * Handle click of "Search" button. Searches entries by username and
         * updates result list with matching entries or no-result message.
         * @param event action event triggered by button press
         * @return void
         */
        @Override
        public void handle(ActionEvent event) {
            searchList.getItems().clear();
            String name = searchField.getText().trim();
            if (name.isEmpty()) {
                searchList.getItems().add("Please enter a username to search.");
                return;
            }

            boolean found = false;
            for (EmissionSource e:tracker.getEntries()) {
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

    /**
     * Event handler for processing an offset purchase transaction with simulated delay.
     */
    private class PurchaseOffsetEvent implements EventHandler<ActionEvent> {
        private final Button buyBtn;
        private final Label  processingLabel;

        /**
         * @param buyBtn button used to trigger purchase
         * @param processingLabel label used to show processing state
         */
        public PurchaseOffsetEvent(Button buyBtn,Label processingLabel) {
            this.buyBtn= buyBtn;
            this.processingLabel = processingLabel;
        }

        /**
         * Handle click of "Purchase Offset" button. Validates user/payment,
         * performs simulated offset transaction, logs activity, and updates UI.
         * @param event action event triggered by button press
         * @return void
         */
        @Override
        public void handle(ActionEvent event) {
            String user= offsetUserField.getText().trim();
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
            processingLabel.setText("please wait 2 seconds.");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    String receipt = transactionHandler.CalculateOffSet(user, payment);
                    if (receipt.startsWith("No emissions found")) {
                        receiptArea.setText(receipt);
                        processingLabel.setText("");
                        buyBtn.setDisable(false);
                        return;
                    }
                    receiptArea.setText(receipt);

                    Logger.log(Logger.Operation.OFFSET_PURCHASED,"User: " + user + " | Payment: " + payment);
                    totalEmissionsLabel.setText(String.format("Current Total: %f kg CO2", tracker.getTotalEmissions()-tracker.getTotalEmissionsForUser(user)));
                    

                    // Add to offset history ListView
                    String summary = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +"  |  "+ user+"  |  " + payment;
                    offsetHistoryList.getItems().add(summary);
                    processingLabel.setText("✓ Transaction complete.");
                    buyBtn.setDisable(false);
                }
            });
            pause.play();
        }
    }

    //loging offset puchase state in log file 

    /**
     * Loads saved state entries from local file storage into the in-memory tracker.
     * @return void
     */
    private void loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) return;

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                EmissionSource entry = fromStateLine(line);
                if (entry != null) {
                    tracker.addEntry(entry);
                    count++;
                }
            }
            if (count > 0) {
                Logger.log(Logger.Operation.STATE_LOADED,"Restored "+count+" entries from "+STATE_FILE);
            }
        } catch (IOException e) {
            System.err.println("loadState failed: " + e.getMessage());
        }
    }

    /**
     * Parses a saved state line into the corresponding EmissionSource object.
     * @param line saved state text line
     * @return Deserialized EmissionSource object, or null if parsing failed
     */
    // p[0]=category  p[1]=id  p[2]=date  p[3]=user  p[4]=stringVal  p[5]=numVal
    private EmissionSource fromStateLine(String line) {
        try {
            String[] p = line.split("\\|");
            double numVal = Double.parseDouble(p[5]);
            if (p[0].equals("Transportation")) return new TransportationEmission(p[1], p[0], p[2], p[3], numVal, p[4]);
            if (p[0].equals("Energy")) return new EnergyEmission(p[1], p[0], p[2], p[3], numVal, p[4]);
            if (p[0].equals("Food")) return new FoodEmission(p[1], p[0], p[2], p[3], p[4], (int) numVal);
        } catch (Exception e) {
            System.err.println("Could not parse state line: " + line);
        }
        return null;
    }

    /**
     * Appends a single entry line to the persistent state fileq
     * @param line formatted entry data in the pipe-separated state format
     * @return void
     */

    private void appendStateLine(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("appendStateLine failed: " + e.getMessage());
        }
    }

    
    // formating helpers
       /**
     * Creates a standardized heading label used across tabs.
     *
     * @param text heading text
     * @return configured label instance
     */
    private Label makeHeading(String text) {
        Label l=new Label(text);
        l.setFont(new Font("Arial", 16));
        l.setTextFill(Color.DARKGREEN);
        return l;
    }

    /**
     * Creates a button with standardized style used in the GUI.
     * @param text button label
     * @param bg background color of the button
     * @return styled button instance
     */
    private Button makeButton(String text, Color bg) {
        Button btn = new Button(text);
        btn.setTextFill(Color.WHITE);
        btn.setBackground(new Background( new BackgroundFill(bg, new CornerRadii(5), Insets.EMPTY)));
        return btn;
    }

    /**
     * Wraps a label and input control in a consistent HBox row style.
     * @param labelText row label text
     * @param control node to display to the right of the label
     * @return styled HBox container
     */
    private HBox makeRow(String labelText, Node control) {
        Label lbl=new Label(labelText);
        HBox row = new HBox(10, lbl, control);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Application launcher
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }
}