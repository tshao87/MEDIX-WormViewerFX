package annotationtoolfx.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import annotationtoolfx.db.AnnotationSaver;
import annotationtoolfx.db.ConnectionSingleton;
import annotationtoolfx.object.AnnotationSet;
import annotationtoolfx.object.FrameAnnotationInfo;
import annotationtoolfx.object.FrameAnnotationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SaveAnnotations  extends AnchorPane implements Initializable {
	
	@FXML
	private ComboBox<String> nameCombo;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@FXML
	private TextField emailText;
	@FXML
	private PasswordField passwordText;
	@FXML
	private CheckBox finalVersionCheck;
	@FXML
	private Label invalidLabel;
	@FXML
	private Label nameExistsLabel;
	@FXML 
	private Label nameBlankLabel;
	
	private FrameAnnotationManager frameMgr;
	private AnnotationSaver saver;
	
	private HashMap<String, AnnotationSet> namesSets;
	
	private boolean result = false;
	private Stage stage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		emailText.setText(ConnectionSingleton.getConnectionInstance().getLastLoginId());
		
		cancelButton.setOnAction((event)-> {
			result = false;
			stage.hide();
		});
		
		okButton.setOnAction((event)-> {
			nameExistsLabel.setVisible(false);
			nameBlankLabel.setVisible(false);

			if(!ConnectionSingleton.getConnectionInstance().Login(emailText.getText(), passwordText.getText())) {
				invalidLabel.setVisible(true);
			}
			else {
				
				try {
					
					if(namesSets.containsKey(nameCombo.getSelectionModel().getSelectedItem())) {
						AnnotationSet set = namesSets.get(nameCombo.getSelectionModel().getSelectedItem());
						if(set != null && set.getName().length() > 0) {
							saver.saveAnnotations(frameMgr, set, finalVersionCheck.isSelected());
							result = true;
						}
						else {
							result = save();
						}
					}
					else {
						result = save();
					}
				} catch (SQLException e) {
					
					//TODO:
/*					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Error saving annotations to the database");
					alert.setHeaderText("Look, a Confirmation Dialog with Custom Actions");
					alert.setContentText("Choose your option.");

					ButtonType buttonTypeSaveOffline = new ButtonType("Save Offline");
					ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

					alert.getButtonTypes().setAll(buttonTypeSaveOffline, buttonTypeCancel);

					Optional<ButtonType> selection = alert.showAndWait();
					if (selection.get() == buttonTypeSaveOffline){
						saveOffline();
					} else {
					    // ... user chose CANCEL or closed the dialog
					}*/
					e.printStackTrace();
					result = false;
				}
			}
			if(result){
				stage.hide();
				frameMgr.setAnnotationsChanged(false);
	    		new Alert(AlertType.CONFIRMATION, "Save Complete!").showAndWait();
			}
		});
	}
	

	
	private void saveOffline() {
		
	
	}



	private boolean save() throws SQLException {
		if(nameCombo.getSelectionModel().getSelectedItem().length() > 0) {
			if(saver.nameNotUsed(nameCombo.getSelectionModel().getSelectedItem())) {
				saver.saveAnnotations(frameMgr, this.emailText.getText(), nameCombo.getSelectionModel().getSelectedItem(), finalVersionCheck.isSelected());
				return true;
			}
			else {
				nameExistsLabel.setVisible(true);
				return false;
			}
		}
		else {
			nameBlankLabel.setVisible(true);
			return false;
		}
			
	}

	public void setFrameMgr(FrameAnnotationManager frameMgr) {
		this.frameMgr = frameMgr;
		loadCombos();
	}
	
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
	
    public void loadCombos() {
		saver = new AnnotationSaver();
		try {
			namesSets =  saver.getNameSets(frameMgr.getStrainTypeId());
			ArrayList<String> names = new ArrayList<String>();
			for (Map.Entry<String, AnnotationSet> entry : namesSets.entrySet()) {
				names.add(entry.getKey());
			}
			nameCombo.setItems(FXCollections.observableArrayList(names));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean getResult() {
    	return result;
    }

	
	

}
