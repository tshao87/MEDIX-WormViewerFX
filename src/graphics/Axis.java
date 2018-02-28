package graphics;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mingfeishao
 */
public class Axis extends JComponent {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int SIZE = 20;
    private static final int GRAPH_POINT_WIDTH = 12;
    
    public int orientation;
    private int xScale;
    private int yScale;
    private int units;
    
    public Axis(int o, int xScale, int yScale) {
        orientation = o;
        this.xScale = xScale;
        this.yScale = yScale;
    }
    
    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, SIZE));
    }
    
    protected void paintComponent(Graphics g) {
        Rectangle drawHere = g.getClipBounds();

        // Fill clipping area with dirty brown/orange.
        g.setColor(new Color(230, 163, 4));
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Do the ruler labels in a small font that's black.
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(Color.black);

        // Some vars we need.
        int end = 0;
        int start = 0;
        int tickLength = 0;
        String text = null;

        // Use clipping bounds to calculate first and last tick locations.
        if (orientation == HORIZONTAL) {
            start = (drawHere.x / xScale) * xScale;
            end = (((drawHere.x + drawHere.width) / xScale) + 1)
                  * xScale;
        } else {
            start = (drawHere.y / yScale) * yScale;
            end = (((drawHere.y + drawHere.height) / yScale) + 1)
                  * yScale;
        }

        // Make a special case of 0 to display the number
        // within the rule and draw a units label.
        if (start == 0) {
            tickLength = 10;
            if (orientation == HORIZONTAL) {
                g.drawLine(0, SIZE-1, 0, SIZE-tickLength-1);
                start = xScale;
            } else {
                g.drawLine(SIZE-1, 0, SIZE-tickLength-1, 0);
                start = yScale;
            }
        }

        // ticks and labels
        for (int i = start; i < end; i += xScale) {

                tickLength = 10;
 
           

            if (tickLength != 0) {
                    g.drawLine(i, SIZE-1, i, SIZE-tickLength-1);
                    if (text != null)
                        g.drawString(text, i-3, 21);
            }
        }
        
        for (int i = start; i < end; i += yScale) {
                tickLength = 10;
                
            if (tickLength != 0) {
                    g.drawLine(SIZE-1, i, SIZE-tickLength-1, i);
                    if (text != null)
                        g.drawString(text, 9, i+3);
            }
        }
    }
}
