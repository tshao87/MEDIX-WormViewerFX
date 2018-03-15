package graphics;

import java.util.LinkedList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import object.DVDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import singleton.PostgresSQLDBManager;

/**
 *
 * @author MSHAO1
 */
public class NewFXMain extends Application {
        private static final int COUNT = 2 * 1000;
    private int FPS = 30;
    private int currentFrame;
    private final int timerInterval = 1000 / FPS;
    private final float[] newData = new float[1];
    private ChartCanvas cv;

    private JFreeChart createChart() {
        DVDataset dvDataset = PostgresSQLDBManager.getDVEntriesFromTable();
        currentFrame = dvDataset.getFrameOffset();
        DynamicTimeSeriesCollection dataset = new DynamicTimeSeriesCollection(1, COUNT, new Millisecond());
        dataset.setTimeBase(new Millisecond(0, 0, 0, 0, 1, 1, 2011));
        return null;
    }

    @Override
    public void start(Stage stage) throws Exception {
        JFreeChart chart = createChart();
        ChartViewer viewer = new ChartViewer(chart);
        stage.setScene(new Scene(viewer));
        stage.setTitle("JFreeChart: TimeSeriesFXDemo1.java");
        stage.setWidth(700);
        stage.setHeight(390);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
