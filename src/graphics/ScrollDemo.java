package graphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;


public class ScrollDemo extends JPanel {

    private Axis columnView;
    private Axis rowView;
    private JScrollPane pictureScrollPane;

    public ScrollDemo() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        java.util.List<Integer> scores = new ArrayList<Integer>();
        Random random = new Random();
        int maxDataPoints = 16;
        int maxScore = 20;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add(random.nextInt(maxScore));
        }
        DrawGraph mainPanel = new DrawGraph(scores);
        BufferedImage awtImage = mainPanel.getPaintImage();
        mainPanel.revalidate();

        //Create the row and column headers.
        columnView = new Axis(Axis.HORIZONTAL, mainPanel.getxScale(), mainPanel.getyScale());
        rowView = new Axis(Axis.VERTICAL, mainPanel.getxScale(), mainPanel.getyScale());

//        if (bee != null) {
//            columnView.setPreferredWidth(bee.getIconWidth());
//            rowView.setPreferredHeight(bee.getIconHeight());
//        } else {
//            columnView.setPreferredWidth(320);
//            rowView.setPreferredHeight(480);
//        }
        if (awtImage != null) {
            columnView.setPreferredWidth(awtImage.getWidth());
            rowView.setPreferredHeight(awtImage.getHeight());
        } else {
            columnView.setPreferredWidth(464);
            rowView.setPreferredHeight(188);
        }
        

        //Set up the scroll pane.
//        picture = new ScrollablePicture(bee, columnView.getIncrement());
//        picture = new ScrollablePicture(new ImageIcon(awtImage), columnView.getIncrement());
        pictureScrollPane = new JScrollPane(mainPanel);
        pictureScrollPane.setPreferredSize(new Dimension(464, 188));
        pictureScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));

        pictureScrollPane.setColumnHeaderView(columnView);
        pictureScrollPane.setRowHeaderView(rowView);

        //Set the corners.
        //In theory, to support internationalization you would change
        //UPPER_LEFT_CORNER to UPPER_LEADING_CORNER,
        //LOWER_LEFT_CORNER to LOWER_LEADING_CORNER, and
        //UPPER_RIGHT_CORNER to UPPER_TRAILING_CORNER.  In practice,
        //bug #4467063 makes that impossible (in 1.4, at least).
        pictureScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,
                new Corner());
        pictureScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                new Corner());

        //Put it in this panel.
        add(pictureScrollPane);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public JScrollPane getPictureScrollPane() {
        return pictureScrollPane;
    }
    
}
