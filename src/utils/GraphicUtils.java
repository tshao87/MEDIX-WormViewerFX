package utils;

import singleton.ConfigurationManager;
import singleton.PostgresSQLDBManager;
import java.awt.Component;
import java.util.Vector;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author MSHAO1
 */
public class GraphicUtils {

    public static void adjustTableColumnWidth(JTable jTable) {
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int column = 0; column < jTable.getColumnCount(); column++) {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < jTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Component c = jTable.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + jTable.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    public static void populateFeatureList(ListView listView) {
        if (!ConfigurationManager.getConfigurationManager().getConfiguration().getStrainTypeId().isEmpty() && !ConfigurationManager.getConfigurationManager().getConfiguration().getTableName().isEmpty()) {
            ObservableList<String> columnNames = PostgresSQLDBManager.getAllTableColumnLabels(ConfigurationManager.getConfigurationManager().getConfiguration().getTableName());
            listView.setItems(columnNames);
            listView.getSelectionModel().select(0);
        }
    }

    public static void populateFeatureComboBox(JComboBox<String> jComboBox) {
        if (!ConfigurationManager.getConfigurationManager().getDVConfiguration().getDvStrainTypeId().isEmpty() && !ConfigurationManager.getConfigurationManager().getDVConfiguration().getDvTableName().isEmpty()) {
            ObservableList<String> columnNames = PostgresSQLDBManager.getAllTableColumnLabels(ConfigurationManager.getConfigurationManager().getDVConfiguration().getDvTableName());
            jComboBox.removeAllItems();
            for (String s : columnNames) {
                if (!s.equals("*")) {
                    jComboBox.addItem(s);
                }
            }
            jComboBox.setSelectedIndex(0);
        }
    }
}
