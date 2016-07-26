/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_graph;

import ardufocuser.starfocusing.FitsImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ardufocuser.starfocusing.FitsImage;

/**
 *
 * @author josemlp
 */
public class MatrixDraw {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Archivo con la imagen.
                String filename = "/home/zerjillo/Escritorio/ardufocuser_imageProcessing/ardufocuser_image-processing/imgs/nucleo24890_123.fit";

                // Coordenadas del centro del objeto y dimsensiones.
                Integer coordx = 949;
                Integer coordy = 775;
                Integer dimen = 20;

                FitsImage fimg;
                fimg = new FitsImage(filename);

                // Extraemos la parte de la imagen que nos interesa.
                int framematrix[][] = new int[dimen][dimen];
                framematrix = fimg.getSubMatrixCenter(coordx, coordy, dimen);
                int dimmatrix = dimen;

                int maxvalue = 1500;
                int minvalue = 0;

                // Instanciamos el frame con la grafica.
                MatrixDrawFrame frame = new MatrixDrawFrame(framematrix, dimmatrix, maxvalue, minvalue);
                
                fimg.printImageMatrix(framematrix, dimmatrix);
                
                frame.showUI();
            }
        });
    }

}

class MatrixDrawFrame extends JFrame {

    MatrixDrawFramePanel panel;

    public MatrixDrawFrame(int frame[][], int dim, int max, int min) {
        //Creamos 
        panel = new MatrixDrawFramePanel(frame, dim, max, min);
        add(panel);

    }

    public void showUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Grafica Matrix");
        setSize(600, 600);
        setVisible(true);
    }
}

class MatrixDrawFramePanel extends JPanel {

    public static final int FIRST_LENGHT = 25;
    public static final int SECOND_LENGHT = 5;
    public static final int MAX_VALUE = 500;

    public int matrixdimension;
    public int matrix[][];
    public int maxvalue;
    public int minvalue;

    public MatrixDrawFramePanel(int frame[][], int dim, int max, int min) {
        this.matrixdimension = dim;
        this.matrix = frame;
        this.maxvalue = max;
        this.minvalue = min;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        BufferedImage bufferedImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.blue);

        int DESP_X = FIRST_LENGHT;
        int DESP_Y = FIRST_LENGHT;

        int nPoints = matrixdimension;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];

        for (int x = 0; x < matrixdimension; x++) {
            DESP_X = FIRST_LENGHT;
            DESP_Y = DESP_Y + FIRST_LENGHT;

            for (int y = 0; y < matrixdimension; y++) {
                DESP_X = DESP_X + FIRST_LENGHT;
                float value1 = ((matrix[x][y] * MAX_VALUE) / maxvalue);
                g2.drawRect(DESP_X, DESP_Y, FIRST_LENGHT, FIRST_LENGHT);

                value1 = (float) ((value1 * 0.120) / maxvalue);

                // Ajustamos el valor en relacion al valor de la estrella.
                float hue = 0.0080f / value1 - 0.25f; //hue   
                float saturation = 1.0f; //saturation
                float brightness = 1.0f; //brightness

                Color c = Color.getHSBColor(hue, saturation, brightness);
                g2.setColor(c);
                g2.fillRect(DESP_X, DESP_Y, FIRST_LENGHT, FIRST_LENGHT);
            }

        }

        g2.setColor(Color.blue);
        Stroke strok = new BasicStroke(5);
        g2.setStroke(strok);
        g2.drawPolygon(xPoints, yPoints, nPoints);
        g2.setColor(Color.orange);
        g2.fillPolygon(xPoints, yPoints, nPoints);

    

    }

}
