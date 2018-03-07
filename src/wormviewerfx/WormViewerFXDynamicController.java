package wormviewerfx;

import graphics.DynamicLinePlotPanel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import object.DVDataset;
import singleton.ConfigurationManager;
import singleton.PostgresSQLDBManager;
import utils.GraphicUtils;

/**
 * FXML Controller class
 *
 * @author MSHAO1
 */
public class WormViewerFXDynamicController implements Initializable {

    static int FPS = 30;
    static int OFFSET = 7;
    private DynamicLinePlotPanel timelinePlotPanel;
    private AnimationTimer timer;
    private DVDataset dataset;
    private LinkedList<String> imagePathList;

    @FXML
    ComboBox datasetComboBox;

    @FXML
    ComboBox tableComboBox;

    @FXML
    ComboBox featureComboBox;

    @FXML
    Button playButton;

    @FXML
    Button pauseButton;

    @FXML
    Button resetButton;

    @FXML
    ImageView imageView;

    @FXML
    TextArea dataDisplayTextArea;

    @FXML
    AnchorPane timelinePane;
    
    @FXML
    AnchorPane dynamicDataPane;

    @FXML
    private void handleDatasetComboBox() {
        Object item = datasetComboBox.getSelectionModel().getSelectedItem();
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvStrainTypeId(item.toString());
    }

    @FXML
    private void handleTableComboBox() {
        Object item = tableComboBox.getSelectionModel().getSelectedItem();
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvTableName(item.toString());
        ArrayList<String> tableKeys = PostgresSQLDBManager.getAllKeysOfTable(item.toString());
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvTableKeys(tableKeys);
        GraphicUtils.populateFeatureComboBox(featureComboBox);
    }

    @FXML
    private void handleFeatureComboBox() {
        Object item = featureComboBox.getSelectionModel().getSelectedItem();
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvSelectedColumn(item.toString());
    }

    @FXML
    private void onPlayButtonClicked() {
        dataset = PostgresSQLDBManager.getDVEntriesFromTable();
        imagePathList = utils.Utils.loadImagePathByStrainTypeId(ConfigurationManager.getConfigurationManager().getDVConfiguration().getDvStrainTypeId(), OFFSET);
        String fpsStr = PostgresSQLDBManager.getFPSBySTID(ConfigurationManager.getConfigurationManager().getDVConfiguration().getDvStrainTypeId());

        try {
            FPS = Integer.parseInt(fpsStr);
        } catch (NumberFormatException nfe) {

        }

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000 / FPS) {
                    if (imagePathList != null && !imagePathList.isEmpty()) {
                        try {
                            System.out.println(imagePathList.peek());
                            Image img = new Image(new FileInputStream(imagePathList.pop()), 400.0, 300.0, true, true);
                            imageView.setImage(img);
                            dynamicDataPane.requestLayout();
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(WormViewerFXDynamicController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    lastUpdate = now;
                }
            }
        };
//
//        timelinePlotPanel = new graphics.DynamicLinePlotPanel(timer, dataset, dataDisplayTextArea);
//        timelinePlotPanel.setMaximumSize(new java.awt.Dimension(464, 188));
//        timelinePlotPanel.setMinimumSize(new java.awt.Dimension(464, 188));
//        timelinePlotPanel.setPreferredSize(new java.awt.Dimension(464, 188));
//        timelinePanel.add(timelinePlotPanel, java.awt.BorderLayout.CENTER);
//        super.revalidate();

        timer.start();
        pauseButton.setDisable(false);
        resetButton.setDisable(false);
        playButton.setDisable(true);
        datasetComboBox.setDisable(true);
        featureComboBox.setDisable(true);
        tableComboBox.setDisable(false);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
    }

    private void initData() {
        datasetComboBox.getItems().clear();
        ArrayList<String> resultList = PostgresSQLDBManager.getAllStrainTypeIDs();
        for (String s : resultList) {
            datasetComboBox.getItems().add(s);
        }
        datasetComboBox.getSelectionModel().select(0);
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvStrainTypeId(datasetComboBox.getSelectionModel().getSelectedItem().toString());

        tableComboBox.getItems().clear();
        resultList = PostgresSQLDBManager.getAllTableNames();
        for (String s : resultList) {
            tableComboBox.getItems().add(s);
        }
        tableComboBox.getSelectionModel().select(0);
//        System.out.print(ConfigurationManager.getConfigurationManager().getConfiguration().getTableName());
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvTableName(tableComboBox.getSelectionModel().getSelectedItem().toString());
        GraphicUtils.populateFeatureComboBox(featureComboBox);
        featureComboBox.getSelectionModel().select(0);
        ConfigurationManager.getConfigurationManager().getDVConfiguration().setDvSelectedColumn(featureComboBox.getSelectionModel().getSelectedItem().toString());
    }
}
