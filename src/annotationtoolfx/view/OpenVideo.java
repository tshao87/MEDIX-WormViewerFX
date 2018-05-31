package annotationtoolfx.view;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class OpenVideo extends AnchorPane implements Initializable {
	
	@FXML
    private ComboBox<String> wormStrainIdCombo;
	@FXML
    private ComboBox<String> saveAnnCombo;
	@FXML
    private ComboBox<String> editAnnCombo;
    @FXML
    private TextField predictedAnnotationText;
    @FXML
    private Button removeCompareAnnoationButton;
    @FXML
    private Button chooseCompareAnnoationButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;
    @FXML
    private Label annSetsMatchErrorLabel;

    
    public OpenVideo() {
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
   /* 	annSetsMatchErrorLabel.setVisible(false);
    	loader = new StrainTypeLoader();
    	ObservableList<String> list =  loader.getStrains();
		wormStrainIdCombo.setItems(list);
		
	
		chooseCompareAnnoationButton.setOnAction((event)-> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Predicted Annotations File");
			predictedAnnFile = fileChooser.showOpenDialog(stage);
			predictedAnnotationText.setText(predictedAnnFile.getPath());	
			
			
	    	if(predictedAnnFile != null) {

			}
			
		});

		removeCompareAnnoationButton.setOnAction((event)-> {
			predictedAnnFile = null;
			predictedAnnotationText.setText("");
		});
		
		wormStrainIdCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		      @Override public void changed(ObservableValue<? extends String> selected, String oldStrain, String newStrain) {

		    	  ObservableList<String> list = loader.getAnnotationsByStrains(newStrain);
		    	  saveAnnCombo.setItems(list);
		    	  editAnnCombo.setItems(list);
		      }
	      });
		
		saveAnnCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		      @Override public void changed(ObservableValue<? extends String> selected, String oldStrain, String newStrain) {
		    	  annSetsMatchErrorLabel.setVisible(newStrain.equals(editAnnCombo.getValue()));
		      }
	      });
		
		editAnnCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		      @Override public void changed(ObservableValue<? extends String> selected, String oldStrain, String newStrain) {
		    	  annSetsMatchErrorLabel.setVisible(newStrain.equals(saveAnnCombo.getValue()));
		      }
	      });
		
		okButton.setOnAction((event)-> {
			result = true;
			stage.getScene().setCursor(Cursor.WAIT);
			loader.LoadAnnFrameMgr(wormStrainIdCombo.getValue(), saveAnnCombo.getValue(), editAnnCombo.getValue(), 
					wormLocalVideoPathText.getText(), wormImageDirectory, predictedAnnFile, predictedAnnotationText.getText(), selectedPredCol);
			stage.getScene().setCursor(Cursor.DEFAULT);
			stage.hide();
		});
		

		*/
    }


    
   
}