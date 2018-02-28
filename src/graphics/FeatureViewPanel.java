package graphics;

import utils.Utils;
import utils.GraphicUtils;
import utils.StatisticsUtils;
import singleton.ConfigurationManager;
import singleton.PostgresSQLDBManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author MSHAO1
 */
public class FeatureViewPanel extends javax.swing.JPanel {

    private class DatasetComboBoxItemChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                ConfigurationManager.getConfigurationManager().getConfiguration().setStrainTypeId(item.toString());
            }
        }
    }

    private class TableComboBoxItemChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                ConfigurationManager.getConfigurationManager().getConfiguration().setTableName(item.toString());
                ArrayList<String> tableKeys = PostgresSQLDBManager.getAllKeysOfTable(item.toString());
                ConfigurationManager.getConfigurationManager().getConfiguration().setTableKeys(tableKeys);
                GraphicUtils.populateFeatureList(featureSelectorList);
            }
        }
    }

    private class FeatureSelectorListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                List selectionValues = list.getSelectedValuesList();
                Object[] selected = selectionValues.toArray();
                ArrayList<String> selectedFeatures = new ArrayList();
                for (Object o : selected) {
                    selectedFeatures.add(o.toString());
                }
                ConfigurationManager.getConfigurationManager().getConfiguration().setSelectedColumns(selectedFeatures);
            }
        }
    }

    /**
     * Creates new form FeatureViewJPanel
     */
    public FeatureViewPanel() {
        initComponents();
        initData();
    }

    private static DefaultTableModel buildSummaryDisplayTableModel(ArrayList<FiveNumberSummary> fnsList) {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        for (FiveNumberSummary fns : fnsList) {
            columnNames.add(fns.getName());
        }

        Vector<Vector<Object>> data = new Vector<>();
        data.add(Utils.generateDataVectorFromFiveNumberSummaryList("Min", fnsList));
        data.add(Utils.generateDataVectorFromFiveNumberSummaryList("1st Quartile", fnsList));
        data.add(Utils.generateDataVectorFromFiveNumberSummaryList("Median", fnsList));
        data.add(Utils.generateDataVectorFromFiveNumberSummaryList("3rd Quartile", fnsList));
        data.add(Utils.generateDataVectorFromFiveNumberSummaryList("Max", fnsList));

        return new DefaultTableModel(data, columnNames);
    }

    private static DefaultTableModel buildMainDisplayTableModel(CachedRowSet crs) {
        try {
            crs.beforeFirst();
            ResultSetMetaData metaData = crs.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            Vector<Vector<Object>> data = new Vector<>();
            if (!crs.next()) {
                JOptionPane.showMessageDialog(null, "SQL database query returned empty table.", "Warning", JOptionPane.WARNING_MESSAGE);
                return new DefaultTableModel(data, columnNames);
            }

            while (crs.next()) {
                Vector<Object> vector = new Vector<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(crs.getObject(columnIndex));
                }
                data.add(vector);
            }

            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(PostgresSQLDBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static HashMap<String, ArrayList<Double>> prepareDataForFiveNumberSummary(CachedRowSet crs) {
        HashMap<String, ArrayList<Double>> resultMap = new HashMap<>();
        try {
            crs.beforeFirst();
            ResultSetMetaData metaData = crs.getMetaData();
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();

            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
                if (!ConfigurationManager.getConfigurationManager().getConfiguration().getTableKeys().contains(metaData.getColumnName(column)) && !metaData.getColumnName(column).equalsIgnoreCase("IsMoving") && !metaData.getColumnName(column).equalsIgnoreCase("Resolution")) {
                    resultMap.put(metaData.getColumnName(column), new ArrayList<>());
                }
            }

            while (crs.next()) {
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    if (resultMap.get(columnNames.get(columnIndex - 1)) != null) {
                        try {
                            if (crs.getObject(columnIndex).toString().equalsIgnoreCase("Inf")) {
                                resultMap.get(columnNames.get(columnIndex - 1)).add(Double.POSITIVE_INFINITY);
                            } else {
                                resultMap.get(columnNames.get(columnIndex - 1)).add(Double.parseDouble(crs.getObject(columnIndex).toString()));
                            }
                        } catch (NullPointerException | NumberFormatException npe) {
                            resultMap.get(columnNames.get(columnIndex - 1)).add(Double.NaN);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserInterfaceJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultMap;
    }

    private void initData() {
        datasetComboBox.removeAllItems();
        ArrayList<String> resultList = PostgresSQLDBManager.getAllStrainTypeIDs();
        for (String s : resultList) {
            datasetComboBox.addItem(s);
        }
        datasetComboBox.addItemListener(new DatasetComboBoxItemChangeListener());
        datasetComboBox.setSelectedIndex(0);
        ConfigurationManager.getConfigurationManager().getConfiguration().setStrainTypeId(datasetComboBox.getSelectedItem().toString());

        tableComboBox.removeAllItems();
        resultList = PostgresSQLDBManager.getAllTableNames();
        for (String s : resultList) {
            tableComboBox.addItem(s);
        }
        tableComboBox.addItemListener(new TableComboBoxItemChangeListener());
        tableComboBox.setSelectedIndex(0);
        System.out.print(ConfigurationManager.getConfigurationManager().getConfiguration().getTableName());
        ConfigurationManager.getConfigurationManager().getConfiguration().setTableName(tableComboBox.getSelectedItem().toString());
        ArrayList<String> tableKeys = PostgresSQLDBManager.getAllKeysOfTable(tableComboBox.getSelectedItem().toString());
        ConfigurationManager.getConfigurationManager().getConfiguration().setTableKeys(tableKeys);
        featureSelectorList.addListSelectionListener(new FeatureSelectorListSelectionHandler());
        GraphicUtils.populateFeatureList(featureSelectorList);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        datasetComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        tableComboBox = new javax.swing.JComboBox<>();
        mainScrollPane = new javax.swing.JScrollPane();
        summaryScrollPane = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        featureSelectorList = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        viewFeaturesButton = new javax.swing.JButton();
        downloadDatasetButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(820, 540));
        setMinimumSize(new java.awt.Dimension(820, 540));
        setPreferredSize(new java.awt.Dimension(820, 540));

        jLabel1.setText("Select Dataset");

        jLabel2.setText("Select Table");

        jScrollPane3.setViewportView(featureSelectorList);

        jLabel3.setText("Select Features");

        viewFeaturesButton.setText("View Features");
        viewFeaturesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewFeaturesButtonActionPerformed(evt);
            }
        });

        downloadDatasetButton.setText("Download Dataset");
        downloadDatasetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadDatasetButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Summary Display");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(tableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(downloadDatasetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(datasetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(summaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(viewFeaturesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(mainScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(datasetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewFeaturesButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(downloadDatasetButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(summaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void viewFeaturesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewFeaturesButtonActionPerformed

        System.out.println(ConfigurationManager.getConfigurationManager().getConfiguration().getStrainTypeId());
        System.out.println(ConfigurationManager.getConfigurationManager().getConfiguration().getTableName());
        System.out.println(ConfigurationManager.getConfigurationManager().getConfiguration().getSelectedColumns());
        CachedRowSet crs = PostgresSQLDBManager.getEntriesFromTable();
        if (crs != null) {
            JTable mainTable = new JTable(buildMainDisplayTableModel(crs));
            mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//            GraphicUtils.adjustTableColumnWidth(mainTable
            mainScrollPane.getViewport().add(mainTable);

            HashMap<String, ArrayList<Double>> resultMap = prepareDataForFiveNumberSummary(crs);
            System.out.println(resultMap.size());
            ArrayList<FiveNumberSummary> fnsList = StatisticsUtils.getAllFiveNumberSummaries(resultMap);

            JTable summaryTable = new JTable(buildSummaryDisplayTableModel(fnsList));
            summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//            GraphicUtils.adjustTableColumnWidth(summaryTable);
            summaryScrollPane.getViewport().add(summaryTable);
        }
    }//GEN-LAST:event_viewFeaturesButtonActionPerformed

    private void downloadDatasetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadDatasetButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("output"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV file", "csv"));

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getPath();
            if (!filename .endsWith(".csv")){
                filename += ".csv";
            }
            PostgresSQLDBManager.saveOutputData(filename);
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + filename);
        } else {
            System.out.println("No Selection ");
        }
    }//GEN-LAST:event_downloadDatasetButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> datasetComboBox;
    private javax.swing.JButton downloadDatasetButton;
    private javax.swing.JList<String> featureSelectorList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JScrollPane summaryScrollPane;
    private javax.swing.JComboBox<String> tableComboBox;
    private javax.swing.JButton viewFeaturesButton;
    // End of variables declaration//GEN-END:variables
}
