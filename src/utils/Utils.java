/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import graphics.FiveNumberSummary;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author mingfeishao
 */
public class Utils {
    static final String[] EXTENSIONS = new String[]{
        "jpeg", "jpg"
    };

    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    public static Vector generateDataVectorFromFiveNumberSummaryList (String label, ArrayList<FiveNumberSummary> fnsList) {
        DecimalFormat df = new DecimalFormat("#.##");
        Vector<Object> vector = new Vector<>();
        vector.add(label);
        for (FiveNumberSummary fns : fnsList) {
            switch (label) {
                case "Min":
                    vector.add(df.format(fns.getMin()));
                    break;
                case "1st Quartile":
                    vector.add(df.format(fns.getFirstQuartile()));
                    break;
                case "Median":
                    vector.add(df.format(fns.getMedian()));
                    break;
                case "3rd Quartile":
                    vector.add(df.format(fns.getThirdQuartile()));
                    break;
                case "Max":
                    vector.add(df.format(fns.getMax()));
                    break;
                default:
                    break;
            }
            
        }
        return vector;
    }
    
    public static String convertStarinTypeIdToDatasetName(String StrainTypeId) {
        String[] decompositedSTId = StrainTypeId.split("_");
        boolean isHR = false;
        
        if (decompositedSTId[1].equals("HR")) {
            isHR = true;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(decompositedSTId[0]);
        if(isHR){
            sb.append("_");
            sb.append(decompositedSTId[1]);
        }
        sb.append("_");
        sb.append(decompositedSTId[2].toLowerCase());
        sb.append(decompositedSTId[3]);
        return sb.toString();
    }
    
    public static LinkedList<String> loadImagePathByStrainTypeId (String StrainTypeId, int offset){
        LinkedList<String> imagePathList = new LinkedList<>();
        String dataSetName = convertStarinTypeIdToDatasetName(StrainTypeId);
        String inputPath = "\\\\CDM-MEDIXSRV\\Nematodes\\data\\*****\\input\\";
        inputPath = inputPath.replace("*****", dataSetName);
        System.out.println(inputPath);
        File inputDir = new File(inputPath);
        int counter = 0;
        
        for (final File f : inputDir.listFiles(IMAGE_FILTER)) {
            if (counter < offset) {
                counter++;
                continue;
            }
            
            imagePathList.add(f.getAbsolutePath());
        }
        
        return imagePathList;
    }
    
    public static void displayErrorMessage(JPanel jpanel, String message){
        JOptionPane.showMessageDialog(jpanel,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
    }
    
    public static void displayWarningMessage(JPanel jpanel, String message){
        JOptionPane.showMessageDialog(jpanel,
                        message,
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
    }
}
