package graphics;

import static graphics.DataVisualizationPanel.FPS;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import object.DVDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;

public class DynamicLinePlotPanel extends JPanel {

    private static final int COUNT = 2 * 1000;
    private static final Random random = new Random();
    private final DVDataset dvDataset;
    private final Timer timer;
    private final JTextArea textArea;
    private int currentFrame;
    private final DynamicTimeSeriesCollection dataset;
    private final int timerInterval = 1000 / FPS;

    public DynamicLinePlotPanel(Timer timer, DVDataset dvDataset, JTextArea textArea) {
        this.dvDataset = dvDataset;
        this.timer = timer;
        this.textArea = textArea;
        currentFrame = dvDataset.getFrameOffset();
        dataset = new DynamicTimeSeriesCollection(1, COUNT, new Millisecond());
        dataset.setTimeBase(new Millisecond(0, 0, 0, 0, 1, 1, 2011));
        dataset.addSeries(initData(), 0, dvDataset.getTitle());
        JFreeChart chart = createChart(dataset);
        ChartPanel cp = new ChartPanel(chart, 464, 188, 464, 188, 464, 188, true, false, false, false, false, false);
        this.add(cp);

        timer.addActionListener(new onTimerChangeActionListener());

    }

    private class onTimerChangeActionListener implements ActionListener {

        final LinkedList<Float> dvDatasetList = dvDataset.getDataList();
        float[] newData = new float[1];

        @Override
        public void actionPerformed(ActionEvent e) {
//            dataset.advanceTime();
            currentFrame++;
            newData[0] = dvDatasetList.pop();
            dataset.appendData(newData);
            textArea.setText("Current Frame: " + currentFrame + "\n" + dvDataset.getTitle() + ": " + newData[0] + "\nMIn = " + dvDataset.getMin() + "\nMax = " + dvDataset.getMax());

            if (dvDatasetList.peek() == null) {
                timer.stop();
                textArea.setText(textArea.getText() + "\nVideo playback finished!");
            } else {
                float nextData = dvDatasetList.peek();
                float dataStep = (nextData - newData[0]) / timerInterval;
                for (int i = 0; i < timerInterval; i++) {
                    dataset.advanceTime();
                    newData[0] += i * dataStep;
                    dataset.appendData(newData);
                }
            }
        }
    }

    private float[] initData() {
        float[] a = new float[dvDataset.getFrameOffset()];
        return a;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        String yLabel = dvDataset.getTitle();
        
        if (yLabel.contains("angle")) {
            yLabel += " ";
            yLabel += "deg";
        } else if (yLabel.contains("angluarvelocity")) {
            yLabel += " ";
            yLabel += "deg/s2";
        } else if (yLabel.contains("speed")) {
            yLabel += " ";
            yLabel += "um/s";
        } else if (yLabel.contains("acceleration")) {
            yLabel += " ";
            yLabel += "um/s2";
        }
        
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "", "", yLabel, dataset, false, false, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(dvDataset.getMin(), dvDataset.getMax());
        return result;
    }

    public void start() {
        timer.start();
    }
}
