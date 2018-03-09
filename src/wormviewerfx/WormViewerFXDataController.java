
package wormviewerfx;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.JFileChooser;
import object.FilePathConfiguration;
import singleton.ConfigurationManager;
import utils.DatabaseTableInserter;
import utils.MasterFileCreater;
import utils.TableCreater;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author MSHAO1
 */


public class WormViewerFXDataController implements Initializable {

    @FXML
    TextArea consoleDisplayTextArea;
    
    @FXML
    TextField filePathTextField;
    
    @FXML
    CheckBox outputHeadersCheckBox;
    
    @FXML
    Button mfPathButton;
    
    @FXML
    Button mfGenerationButton;
    
    @FXML
    Button tableGenerationButton;
    
    @FXML
    Button uploadIntoDBButton;
    
    @FXML
    private void onOutputHeadersCheckBoxClicked() {                                                      
        ConfigurationManager.getConfigurationManager().getGTConfiguration().setWithHeader(outputHeadersCheckBox.isSelected());
    }  
    
    @FXML
    private void onTableGenerationButtonClicked() {                                                      
        if (validateGTFilePath()) {
            setupFileConfiguration(ConfigurationManager.getConfigurationManager().getGTConfiguration(), filePathTextField.getText());
            TableCreater tc = new TableCreater(consoleDisplayTextArea);
            tc.createAllDBTables();
        }
    }                                                     
    
    @FXML
    private void onMfPathButtonClicked() {                                             
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose dataset directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile().getPath());
            filePathTextField.setText(chooser.getSelectedFile().getPath());
        } else {
            System.out.println("No Selection");
        }
    }                                            
    
    @FXML
    private void onMfGenerationButtonClicked() {                                                   
        if (validateMFFilePath()) {
            setupFileConfiguration(ConfigurationManager.getConfigurationManager().getMFConfiguration(), filePathTextField.getText());
            MasterFileCreater mfc = new MasterFileCreater(consoleDisplayTextArea);
            mfc.createMasterFile();
        }
    }                                                  
    
    @FXML
    private void onUploadIntoDBButtonClicked() {                                                   
        if (validateDPFilePath()) {
            setupFileConfiguration(ConfigurationManager.getConfigurationManager().getDPConfiguration(), filePathTextField.getText());
            DatabaseTableInserter dti = new DatabaseTableInserter(consoleDisplayTextArea);
            dti.insertIntoDatabase();
        }
    }  
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        outputHeadersCheckBox.setSelected(true);
    }    
    
    private boolean validateGTFilePath() {
        if (filePathTextField.getText().equals("")) {
            Utils.displayWarningMessage("Please select directory first!");
            return false;
        }
        if (!Files.exists(Paths.get(filePathTextField.getText() + "\\data"))) {
//            consoleDisplayTextArea.append("Error: no 'data' path under root!\n");
            Utils.displayErrorMessage("Error: no 'data' path under root!\n");
            return false;
        }
        return true;
    }

    private boolean validateMFFilePath() {
        if (filePathTextField.getText().equals("")) {
            Utils.displayWarningMessage("Please select directory first!");
            return false;
        }
        if (!Files.exists(Paths.get(filePathTextField.getText() + "\\data\\movementFeatures.csv"))) {
//            consoleDisplayTextArea.append("Error: no movementFeatures.csv under data folder!\n");
            Utils.displayErrorMessage("Error: no movementFeatures.csv under data folder!\n");
            return false;
        } else if (!Files.exists(Paths.get(filePathTextField.getText() + "\\matlab\\AllFeatures.csv"))) {
//            consoleDisplayTextArea.append("Error: no AllFeatures.csv under matlab folder!\n");
            Utils.displayErrorMessage("Error: no AllFeatures.csv under matlab folder!\n");
            return false;
        }
        consoleDisplayTextArea.setText("");
        return true;
    }

    private boolean validateDPFilePath() {
        if (filePathTextField.getText().equals("")) {
            Utils.displayWarningMessage("Please select directory first!");
            return false;
        }
        if (!Files.exists(Paths.get(filePathTextField.getText() + "\\dbtables"))) {
//            consoleDisplayTextArea.append("Error: no movementFeatures.csv under data folder!\n");
            Utils.displayErrorMessage("Error: no 'dbtables' folder under current folder!\n");
            return false;
        }
        
        String folderPath = filePathTextField.getText() + "\\dbtables";
        
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        HashSet<String> hSet = new HashSet<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                hSet.add(listOfFiles[i].getName().toLowerCase());
            }
        }

        if (hSet.size() < 17) {
//            consoleDisplayTextArea.append("Error: no 17 tables under current folder!\n");
            Utils.displayErrorMessage("Error: no 17 tables under current folder!\n");
            return false;
        }

        for (String s : ConfigurationManager.getConfigurationManager().getDPConfiguration().getTABLE_NAMES()) {
            if (!hSet.contains(s.toLowerCase() + ".csv")) {
//                consoleDisplayTextArea.append("Error: no " + s + " file under data folder!\n");
                Utils.displayErrorMessage("Error: no " + s + " file under current folder!\n");
                return false;
            }
        }
        consoleDisplayTextArea.setText("");
        return true;
    }

    private void setupFileConfiguration(FilePathConfiguration fc, String filePath) {
        String[] filePathComponents = filePath.split("\\\\");
        if (filePathComponents.length > 0) {
            String cName = filePathComponents[filePathComponents.length - 1];
            fc.setAllPaths(filePath, cName);
        }
    }
}
