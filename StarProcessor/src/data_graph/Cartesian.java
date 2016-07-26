package data_graph;

import ardufocuser.starfocusing.FitsImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Cartesian {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Archivo con la imagen.
                String filename = "/home/josemlp/workspace/pruebasEnfoque/nucleo24880_042.fit";

                // Coordenadas del centro del objeto y dimsensiones.
                Integer coordx = 949;
                Integer coordy = 776;
                Integer dimen = 20;

                FitsImage fimg;
                fimg = new FitsImage(filename);

                // Extraemos la parte de la imagen que nos interesa.
                int framematrix[][] = new int[dimen][dimen];
                framematrix = fimg.getSubMatrixCenter(coordx, coordy, dimen);
                int dimmatrix = dimen;

                int maxvalue = 1500;
                int minvalue = 0;

                CartesianFrame frame = new CartesianFrame(framematrix, dimmatrix, maxvalue, minvalue);
                frame.showUI();
            }
        });
    }

}

class CartesianFrame extends JFrame {

    CartesianPanel panel;

    public CartesianFrame(int frame[][], int dim, int max, int min) {
        panel = new CartesianPanel(frame, dim, max, min);
        add(panel);
    }

    public void showUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Grafica Montaña");
        setSize(900, 600);
        setVisible(true);
    }
}

class CartesianPanel extends JPanel {

    // x-axis coord constants
    public int X_AXIS_FIRST_X_COORD = 50;
    public int X_AXIS_SECOND_X_COORD = 1000 * getSize().width;
    public int X_AXIS_Y_COORD = 700;

    // y-axis coord constants
    public static final int Y_AXIS_FIRST_Y_COORD = 50;
    public int Y_AXIS_SECOND_Y_COORD = 600 * getSize().width;
    public static final int Y_AXIS_X_COORD = 50;

    public static final int FIRST_LENGHT = 20;
    public static final int SECOND_LENGHT = 5;

    // size of start coordinate lenght
    public static final int ORIGIN_COORDINATE_LENGHT = 6;
    public static final int MAX_VALUE = 500;

    public int matrixdimension;
    public int matrix[][];
    public int maxvalue;
    public int minvalue;

    public CartesianPanel(int frame[][], int dim, int max, int min) {
        this.matrixdimension = dim;
        this.matrix = frame;
        this.maxvalue = max;
        this.minvalue = min;
    }

    // Top
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //////////Representamos nuestros datos //////////////////////
        g2.setColor(Color.blue);

        int nPoints = matrixdimension;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        int DESP_X;
        int DESP_Y;

        for (int x = 0; x < matrixdimension; x++) {

            // En cada iteración aplicamos un desplazamiento adicional (efecto 3D)
            DESP_X = X_AXIS_FIRST_X_COORD + FIRST_LENGHT * x;
            DESP_Y = (int) (Y_AXIS_FIRST_Y_COORD - FIRST_LENGHT * (x * 0.10));
            for (int y = matrixdimension - 1; y >= 0; y--) {

                //Normalizamos los datos entre 0 y 500
                int value1 = ((matrix[x][y] * MAX_VALUE) / maxvalue);
                xPoints[y] = DESP_X - FIRST_LENGHT;
                yPoints[y] = X_AXIS_Y_COORD - value1 - DESP_Y;
                DESP_X += FIRST_LENGHT;

            }

            g2.setColor(Color.blue);
            Stroke strok = new BasicStroke(5);
            g2.setStroke(strok);
            g2.drawPolygon(xPoints, yPoints, nPoints);
            g2.setColor(Color.orange);
            g2.fillPolygon(xPoints, yPoints, nPoints);
        }

    }
}
