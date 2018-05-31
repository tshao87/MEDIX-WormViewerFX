package annotationtoolfx.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import annotationtoolfx.object.AnnotationSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SelectSet extends AnchorPane implements Initializable {
	
	public class AnnotationSetDisplay implements UpdateObserver {
		
		private final SimpleStringProperty setId;
		private final SimpleStringProperty userId;
		private final SimpleStringProperty dateString;
			
		public String getSetId() {
			return setId.get();
		}
		
		public String getUserId() {
			return userId.get();
		}

		public String getDate() {
			return dateString.get();
		}

		public AnnotationSetDisplay(AnnotationSet as) {
			setId = new SimpleStringProperty(as.getSetId());
			userId = new SimpleStringProperty(as.getUserName());
			dateString = new SimpleStringProperty(as.getAnnotationStartDate().toString());
		}

		@Override
		public void update() {

			//Do nothing.  Nothing gets updated.
		}

	}
	
    @FXML
    private TableView<AnnotationSetDisplay> setTable;
    @FXML 
    private TableColumn<AnnotationSetDisplay, String> setIdCol;
    @FXML 
    private TableColumn<AnnotationSetDisplay, String> dateCol;
    @FXML 
    private TableColumn<AnnotationSetDisplay, String> userNameCol;
    @FXML
    private Label nameLabel;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    
    private ArrayList<AnnotationSetDisplay> asdList;
    private ObservableList<AnnotationSetDisplay> asDisplayList;
    private ArrayList<AnnotationSet> asList;
    private String name;
    private int index;
    private AnnotationSet annotationSet;
    private Stage stage;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	nameLabel.setText(name);
    	
    	asdList = new ArrayList<AnnotationSetDisplay>();
    	Iterator<AnnotationSet> it = asList.iterator();

    	while(it.hasNext()) {
    		AnnotationSet as = it.next();
    		AnnotationSetDisplay asd = new AnnotationSetDisplay(as);
    		asdList.add(asd);
    	}
    	
    	asDisplayList = FXCollections.observableArrayList(asdList);
    	setIdCol.setCellValueFactory(new PropertyValueFactory<AnnotationSetDisplay, String>("setId"));
    	dateCol.setCellValueFactory(new PropertyValueFactory<AnnotationSetDisplay, String>("dateCol"));
    	userNameCol.setCellValueFactory(new PropertyValueFactory<AnnotationSetDisplay, String>("userNameCol"));
    	
    	setTable.setItems(asDisplayList);

    	okButton.setOnAction((event)-> {
    		stage.hide();
		});

    	cancelButton.setOnAction((event)-> {
    		index = -1;
    		annotationSet = null;
    		stage.hide();
		});

    	setTable.setRowFactory( tv -> {
    	    TableRow<AnnotationSetDisplay> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
    	        	index = setTable.getSelectionModel().getSelectedIndex();
    	        	okButton.setDisable(false);
    	        	annotationSet = asList.get(index);
    	        }
    	    });
    	    return row ;
    	 });
    	okButton.setDisable(true);
	}
    
    public void setAnnotationSetList(ArrayList<AnnotationSet> asList) {
    	this.asList = asList;
    }

    public void setName(String name) {
    	this.name = name;
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    public AnnotationSet getAnnotationSet() {
    	return annotationSet;
    }
}
