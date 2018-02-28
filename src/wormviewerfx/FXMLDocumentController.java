
package wormviewerfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

/**
 *
 * @author MSHAO1
 */


public class FXMLDocumentController implements Initializable {
    
    @FXML
    private ScrollPane mainScrollPane;
    
    @FXML
    private ScrollPane summaryScrollPane;
    
    @FXML
    private ComboBox tableComboBox;
    
    @FXML
    private ComboBox datasetComboBox;
    
    @FXML
    private Button downloadDatasetButton;
    
    @FXML
    private Button viewFeaturesButton;
    
    @FXML
    private ListView featureSelectorList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }      
}
