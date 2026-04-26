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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javafx.application.Platform;

import ZeroCarbonFootprintTracker.src.model.EmissionSource;
import ZeroCarbonFootprintTracker.src.model.EnergyEmission;
import ZeroCarbonFootprintTracker.src.model.FoodEmission;
import ZeroCarbonFootprintTracker.src.model.FootprintTracker;
import ZeroCarbonFootprintTracker.src.model.TransportationEmission;
import ZeroCarbonFootprintTracker.src.util.InputValidator;
import ZeroCarbonFootprintTracker.src.util.Logger;
import ZeroCarbonFootprintTracker.src.util.TransactionHandler;
import ZeroCarbonFootprintTracker.ConnectionConfig;
import ZeroCarbonFootprintTracker.ResponseParser;





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
    public static final String IMPACT_FILE = "./ZeroCarbonFootprintTracker/greenprint_impact.txt";

    
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
    private TextField offsetUserField;
    private ComboBox<String> paymentCombo;
    private Label receiptArea;
    private ListView<String> offsetHistoryList;
    private Button requestDiscountBtn;
    private Label discountResultLabel;
    private Label discountErrorLabel;
    private java.util.Map<String, Integer> userDiscountPctMap = new java.util.HashMap<>();
    private java.util.Map<String, Double> userDiscountedValueMap = new java.util.HashMap<>();
    private VBox leaderboardBox;
    private ComboBox<String> ImpactCombo;

    @Override
    /**
     * Initializes UI components, loads persisted state entries, and configures event handlers for dashboard interactions.
     * @param stage primary stage provided by the JavaFX runtime
     */
    public void start(Stage stage) {
        loadState();
        
        String cssPath = getClass().getResource("primer-dark.css").toExternalForm();
        Application.setUserAgentStylesheet(cssPath);

        //TAB 1
        VBox dashLayout = new VBox(10);
        dashLayout.setPadding(new Insets(20));

        Label dashHeading = makeHeading("Emission History Visualiser");

        summaryLabel=new Label("Total Entries: 0  |  Total CO2: 0.00 kg  |  Top Emitter: N/A");
        summaryLabel.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        dashboardGrid=new FlowPane(10, 10);
        ScrollPane gridScroll = new ScrollPane(dashboardGrid);
        gridScroll.setFitToWidth(true);
        detailLabel = new Label("Click a card above to see its full details.");
        detailLabel.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(4), Insets.EMPTY)));
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
        totalEmissionsLabel.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(5), Insets.EMPTY)));
        offsetUserField=new TextField();
        offsetUserField.setPromptText("Username to offset");

        paymentCombo=new ComboBox<String>();// ComboBox for payment method
        paymentCombo.getItems().addAll("Credit Card", "Digital Wallet", "Campus Card");
        paymentCombo.setPromptText("Select Payment Method");

        ImpactCombo=new ComboBox<String>();// ComboBox for impact type
        ImpactCombo.getItems().addAll("Planting trees", "Build wind farms", "Install methane digesters","Support small farming businesses","Regenerative agriculture");
        ImpactCombo.setPromptText("Select Impact Type");
        Label rateNote=new Label("Rate: $15 per 1000 kg CO2  ($0.015 per kg)");
        rateNote.setTextFill(Color.GRAY);
        Button show_estimate=makeButton("Show Estimate", Color.DARKGREEN);
        Label estimate=new Label("");
        
        show_estimate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String user= offsetUserField.getText().trim();
                if (user.isEmpty()) {
                    estimate.setText("Please enter a username.");
                    return;
                }
                double userTotal = tracker.getTotalEmissionsForUser(user);
                if (userTotal <= 0) {
                    estimate.setText("No emissions found for user: " + user);
                    return;
                }
                double cost = tracker.getTotalEmissionsForUser(user) * TransactionHandler.OFFSET_RATE;
                estimate.setText(String.format("Estimated cost to offset %.2f kg CO2: $%.2f", userTotal, cost));
            }
        });
        Button buyBtn=makeButton("Purchase Offset", Color.DARKGREEN);
        Label processingLabel=new Label("");
        processingLabel.setTextFill(Color.MAGENTA);
        
       
        buyBtn.setOnAction(new PurchaseOffsetEvent(buyBtn, processingLabel));

        
        receiptArea=new Label("Your receipt will appear here after purchase.");// Receiptdisplay
        //receiptArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        receiptArea.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.THIN)));
        receiptArea.setPadding(new Insets(25));

        Label histHeading=makeHeading("Offset History:");
        offsetHistoryList= new ListView<String>();
        offsetHistoryList.setPrefHeight(130);
        
        requestDiscountBtn = new Button("Request Discount");
        requestDiscountBtn.setDisable(true);
        if (!tracker.getEntries().isEmpty()) {
        requestDiscountBtn.setDisable(false);
}
    requestDiscountBtn.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
            handleRequestDiscount();
        }
        
    });


    discountResultLabel = new Label();
    discountResultLabel.setTextFill(Color.LIGHTGREEN);

    discountErrorLabel = new Label();
    discountErrorLabel.setTextFill(Color.RED);
    discountErrorLabel.setVisible(false);
    HBox inputRow = new HBox(35,makeRow("Username:", offsetUserField), makeRow("Payment Method:", paymentCombo),makeRow("Impact:", ImpactCombo));
    HBox discountErrorBox = new HBox(10, requestDiscountBtn, discountErrorLabel);
    HBox buttons = new HBox(10, show_estimate, discountErrorBox);

    offsetLayout.getChildren().addAll(offsetHeading,totalEmissionsLabel, inputRow,
                rateNote,buttons,estimate, discountResultLabel,buyBtn,processingLabel,
                new Label("Receipt:"),receiptArea,histHeading,offsetHistoryList
            );

        offsetUserField.textProperty().addListener((obs, oldVal, newVal) -> {
            String user = newVal.trim().toLowerCase();
            estimate.setText("");
            discountResultLabel.setText("");
            if (userDiscountPctMap.containsKey(user)) {
                // They have a saved discount! Show it.
                int pct = userDiscountPctMap.get(user);
                double val = userDiscountedValueMap.get(user);
                discountResultLabel.setText("Saved discount: " + pct + "% (Adjusted: " + String.format("%.2f", val) + " kg)");
                requestDiscountBtn.setDisable(true); // Don't let them request again if they have one
            } else {
                // No discount for this name yet.
                discountResultLabel.setText("");
                // Re-enable button if there are entries (Assignment requirement)
                requestDiscountBtn.setDisable(tracker.getEntries().isEmpty());
            }
        });


        ScrollPane offsetScroll = new ScrollPane(offsetLayout);
        offsetScroll.setFitToWidth(true);


        //Assemble tabs
        TabPane tabPane= new TabPane();
        Tab dashTab=new Tab("Live Dashboard");
        Tab inputTab=new Tab("Input & Operations");
        Tab offsetTab=new Tab("Carbon Offset");
        offsetTab.setContent(offsetScroll);
        dashTab.setClosable(false);
        inputTab.setClosable(false);
        offsetTab.setClosable(false);
        dashTab.setContent(dashLayout);
        inputTab.setContent(inputLayout);

        Tab leaderboardTab = new Tab("Leaderboard");
        leaderboardTab.setClosable(false);
        leaderboardTab.setContent(buildLeaderboardTab());
        tabPane.getTabs().addAll(dashTab, inputTab, offsetTab, leaderboardTab);

        
        

        // Log on window close
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Logger.log(Logger.Operation.STATE_SAVED,tracker.getEntries().size() + " entries in " + STATE_FILE);
            }
        });

        Scene scene = new Scene(tabPane,1000,700);
        stage.setTitle("GreenPrint - Carbon Footprint Tracker");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("logo.png")));
        stage.show();

        refreshDashboard();
        totalEmissionsLabel.setText(String.format("Current Total: %.2f kg CO2", tracker.getTotalEmissions()));

    }

    /**
     * Reloads all emission cards on the dashboard from tracker entries
     * and refreshes summary metrics.
     * @return void
     */
    public void refreshDashboard() {
        dashboardGrid.getChildren().clear();
        for (EmissionSource e:tracker.getEntries()) dashboardGrid.getChildren().add(buildCard(e));
            updateSummaryBar();
            refreshLeaderboard();
    }

    /**
     * Creates a colored label card representing an emission entry.
     * @param entry emission entry model
     * @return label node for display in dashboard grid
     */
    
    public Label buildCard(EmissionSource entry) {
        double val = entry.calculateEmission();

        Color bg=val < 1.0 ?Color.GREEN:val<= 3.0?Color.GOLD:Color.TOMATO;
        Label card = new Label(entry.getSourceID()+"\n" +String.format("%.2f", val)+" kg CO2");
        card.setPrefSize(95, 80);
        card.setAlignment(Pos.CENTER);
        card.setTextFill(bg.equals(Color.GOLD)?Color.BLACK:Color.WHITE);
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
    public void updateSummaryBar() { 
        summaryLabel.setText(
            "Total Entries: "+tracker.getEntries().size()+" |  Total CO2: " +tracker.getTotalEmissions() +" kg  |  Top Emitter: "+ getTopEmitter());
    }

    

    /**
     * Determines the user with the highest total emissions.
     * @return username of the top emitter, or "N/A" if none
     */
    private String getTopEmitter() {
        String topUser  = "N/A";
        double topTotal = -1;
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
    public void updateDynamicFields(String type) {
    
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



    public void handleRequestDiscount() {
        // Get the username typed in the offset username field
        String user = offsetUserField.getText().trim();
 
        if (user.isEmpty()) {
            discountErrorLabel.setText("Please enter a username in the Username field above.");
            discountErrorLabel.setVisible(true);
            discountResultLabel.setText("");
            return;
        }
 
        // Get THIS user's emissions only — not the grand total
        double userEmission = tracker.getTotalEmissionsForUser(user);
 
        if (userEmission <= 0) {
            discountErrorLabel.setText("No emissions found for user: \"" + user + "\". Please add entries first.");
            discountErrorLabel.setVisible(true);
            discountResultLabel.setText("");
            return;
        }
 
        // Reset UI
        discountResultLabel.setText("");
        discountErrorLabel.setVisible(false);
        requestDiscountBtn.setDisable(true);
        requestDiscountBtn.setText("Requesting...");
 
        // Start background thread — never block the JavaFX thread
        DiscountThread t = new DiscountThread(user, userEmission);
        t.setDaemon(true);
        t.start();
    }
    /**
     * Event handler for adding a new entry to the emission tracker.
     * Performs input validation, model creation, persistence update, and UI refresh.
     */
    public class AddEntryEvent implements EventHandler<ActionEvent> {
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
                feedbackLabel.setText("✗ already in use. Use [A-Z]-###");
                return;
            }
            if (user.isEmpty()) {
                feedbackLabel.setText("✗ User name cannot be empty.");
                return;
            }
            // Date validation with d-M-yyyy format
            try {
                LocalDate parsed = LocalDate.parse(date, DATE_FMT);
                int year = parsed.getYear();
                if (year<1900||year>2026) {
                        feedbackLabel.setText("✗ Year must be between 1900 and 2026.");
                        return;
                    }
            } catch (Exception e) {
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

            String userName = userField.getText().trim();

            if (userName.isEmpty() || !userName.matches("[a-zA-Z ]+")) {
                feedbackLabel.setText("UserName must contain letters only.");
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
            requestDiscountBtn.setDisable(false);
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
    public class SearchEvent implements EventHandler<ActionEvent> {
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
    public class PurchaseOffsetEvent implements EventHandler<ActionEvent> {
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
                String user = offsetUserField.getText().trim().toLowerCase();
                String receipt;

                if (userDiscountedValueMap.containsKey(user)) {
                    int pct = userDiscountPctMap.get(user);
                    double discountedVal = userDiscountedValueMap.get(user);
                    receipt = transactionHandler.CalculateOffSetWithDiscount(user, payment, pct, discountedVal);
                    
                    // IMPORTANT: Remove the discount after use so they can't reuse it for a 0 balance
                    userDiscountPctMap.remove(user);
                    userDiscountedValueMap.remove(user);
                } else {
                    receipt = transactionHandler.CalculateOffSet(user, payment);
                }
                    receiptArea.setText(receipt);
                    tracker.getEntries().removeIf(entry -> entry.getUserName().equalsIgnoreCase(user));
                    // rewrite state file without the purchased user's entries
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, false))) {
                    for (EmissionSource entry : tracker.getEntries()) {
                        writer.write(entry.getCategory() + "|" + entry.getSourceID() + "|" 
                            + entry.getDate() + "|" + entry.getUserName() + "|");
                        // write the type-specific fields
                        if (entry instanceof EnergyEmission) {
                            EnergyEmission ee = (EnergyEmission) entry;
                            writer.write(ee.getEnergySource() + "|" + ee.getkWhUsed());
                        } else if (entry instanceof TransportationEmission) {
                            TransportationEmission te = (TransportationEmission) entry;
                            writer.write(te.getTransport() + "|" + te.getDistance());
                        } else if (entry instanceof FoodEmission) {
                            FoodEmission fe = (FoodEmission) entry;
                            writer.write(fe.getMealType() + "|" + fe.getNumberOfMeals());
                        }
                        writer.newLine();
                    }
                    } catch (IOException ex) {
                        System.err.println("Failed to rewrite state: " + ex.getMessage());
                    }
                    String impact = ImpactCombo.getValue();

                    //impact file logging
                    try (BufferedWriter impactWriter = new BufferedWriter(new FileWriter(IMPACT_FILE, true))) {
                        impactWriter.write(LocalDateTime.now().format(Logger.FORMATTER) + " | " + user + " | " + impact+ " | " + payment);
                        impactWriter.newLine();
                    } catch (IOException ex) {
                        System.err.println("Failed to write impact log: " );
                    }

                    refreshDashboard();

                    Logger.log(Logger.Operation.OFFSET_PURCHASED,"User: " + user + " | Payment: " + payment);
                    totalEmissionsLabel.setText(String.format("Current Total: %.2f kg CO2", tracker.getTotalEmissions()));

                    

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
    public void loadState() {
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
    public EmissionSource fromStateLine(String line) {
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

    public void appendStateLine(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("appendStateLine failed: " + e.getMessage());
        }
    }



    public class DiscountThread extends Thread {
        private String userName;
        private double userEmission;
 
        /**
         * @param userName     the user whose emissions are being discounted
         * @param userEmission the total emissions for that user only
         */
        public DiscountThread(String userName, double userEmission) {
            this.userName = userName;
            this.userEmission = userEmission;
        }
 
        public void run() {
            try {
                // Use ConnectionConfig so host/port are not hardcoded here
                ConnectionConfig config = new ConnectionConfig("localhost", 6000);
 
                // Connect to server — same pattern as SimpleClient.java from class
                Socket socket = new Socket(config.getHost(), config.getPort());
                socket.setSoTimeout(5000);
 
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
                // Send this user's emission to the server
                out.println(String.format("%.2f", userEmission));
                out.flush();
 
                // Read the server response e.g. "DISCOUNT:15:10.84"
                String response = in.readLine();
 
                // Close resources
                in.close();
                out.close();
                socket.close();
 
                // Use ResponseParser to extract discount and discounted value
                ResponseParser parser = new ResponseParser(response);//error handling
                int pct = parser.getDiscountPct();
                double discounted = parser.getDiscountedValue();
            
 
                Platform.runLater(() -> {
        // Save to maps
                    userDiscountPctMap.put(userName.toLowerCase(), pct);
                    userDiscountedValueMap.put(userName.toLowerCase(), discounted);
                    

                    discountResultLabel.setText("Server discount applied: " + pct + "% — " 
                        + userName + "'s adjusted footprint: " + String.format("%.2f", discounted) + " kg CO2");
                    
                    // Log according to Task 1 requirements
                    Logger.log(Logger.Operation.DISCOUNT_REQUESTED, "User: " + userName + " | Pct: " + pct);
                });            
            } catch (SocketTimeoutException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        discountErrorLabel.setText("Connection timed out. Please try again later.");
                        discountErrorLabel.setVisible(true);
                    }
                });
            } catch (java.net.ConnectException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        discountErrorLabel.setText("Could not reach server. Please make sure DiscountServer is running.");
                        discountErrorLabel.setVisible(true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        discountErrorLabel.setText("Network error: " + e.getMessage());
                        discountErrorLabel.setVisible(true);
                    }
                });
            }
                
 
            // Always re-enable the button so user can click again
            Platform.runLater(new Runnable() {
                public void run() {
                    requestDiscountBtn.setDisable(true);
                    requestDiscountBtn.setText("Request Discount");
                }
            });
        }
    }
    
    public ScrollPane buildLeaderboardTab() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.getChildren().add(makeHeading("🏆 Leaderboard"));

        HBox legend = new HBox(20);
        legend.setPadding(new Insets(10));
        Label high = new Label("Carbon Chaos  (> 5 kg)");
        high.setBackground(new Background(new BackgroundFill(Color.TOMATO, new CornerRadii(5), Insets.EMPTY)));
        high.setTextFill(Color.WHITE);
        high.setPadding(new Insets(10));

        Label med = new Label("Eco Hustler  (2-5 kg)");
        med.setBackground(new Background(new BackgroundFill(Color.GOLD, new CornerRadii(5), Insets.EMPTY)));
        med.setTextFill(Color.BLACK);
        med.setPadding(new Insets(10)); 
        Label low = new Label("Planet Pro  (< 2 kg)");
        low.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(5), Insets.EMPTY)));
        low.setTextFill(Color.BLACK);
        low.setPadding(new Insets(10));

        legend.getChildren().addAll(high, med, low);
        
        leaderboardBox = new VBox(15);
        layout.getChildren().addAll(legend, leaderboardBox);
        

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        return scroll;
    }

    public void refreshLeaderboard() {
            leaderboardBox.getChildren().clear();

            // Group by user
            HashMap<String, Double> totals = new HashMap<>();
            for (EmissionSource e : tracker.getEntries()) {
                String name = e.getUserName();
                if (totals.containsKey(name)) {
                    totals.put(name, totals.get(name) + e.calculateEmission());
                } else {
                    totals.put(name, e.calculateEmission());
                }
            }

            // Sort lowest to highest
            ArrayList<Map.Entry<String, Double>> sorted = new ArrayList<>(totals.entrySet());
            sorted.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));

            int rank = 1;
            for (Map.Entry<String, Double> entry: sorted) {
                Label rankLabel = new Label("#" + rank);
                rankLabel.setMinWidth(40);
                rank++;

                Label nameLabel = new Label(entry.getKey());
                nameLabel.setMinWidth(150);
               
                Label emissionLabel = new Label(String.format("%.2f kg CO2", entry.getValue()));
                emissionLabel.setMinWidth(120);
                emissionLabel.setTextFill(entry.getValue() > 5 ? Color.TOMATO : entry.getValue() > 2 ? Color.GOLD : Color.LIGHTGREEN);

                HBox row = new HBox(20, rankLabel, nameLabel, emissionLabel);
                
                leaderboardBox.getChildren().add(row);
            }
        }



    // formating helpers
       /**
     * Creates a standardized heading label used across tabs.
     *
     * @param text heading text
     * @return configured label instance
     */
    public Label makeHeading(String text) {
        Label l=new Label(text);
        l.setFont(new Font("Arial", 20));
        l.setTextFill(Color.CYAN);
        return l;
    }

    /**
     * Creates a button with standardized style used in the GUI.
     * @param text button label
     * @param bg background color of the button
     * @return styled button instance
     */
    public Button makeButton(String text, Color bg) {
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
    public HBox makeRow(String labelText, Node control) {
        Label lbl=new Label(labelText);
        HBox row = new HBox(10, lbl, control);
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