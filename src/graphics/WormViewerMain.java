
package graphics;

/**
 *
 * @author MSHAO1
 */


public class WormViewerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserInterfaceJFrame njf = new UserInterfaceJFrame();
                njf.setTitle("WormViewer");
                njf.setVisible(true);
            }
        });
    }
}
