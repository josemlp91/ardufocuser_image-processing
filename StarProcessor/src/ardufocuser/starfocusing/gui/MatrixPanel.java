/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ardufocuser.starfocusing.gui;

import ardufocuser.starfocusing.Utils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author zerjillo josemlp
 */
public class MatrixPanel extends javax.swing.JPanel {

    int[][] pixels;
    int min;
    int max;

    /**
     * Creates new form MatrixPanel
     */
    public MatrixPanel() {
        initComponents();

        pixels = null;
    }

    public void setPixels(int[][] pixels) {
        this.pixels = pixels;

        computeMinMax();

        repaint();
    }

    private void computeMinMax() {
        int[] res = Utils.computeMinMax(pixels);
        min = res[0];
        max = res[1];
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (pixels != null) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            double pixelWidth = (double) width / pixels.length;
            double pixelHeight = (double) height / pixels[0].length;

            for (int x = 0; x < pixels.length; x++) {
                for (int y = 0; y < pixels[0].length; y++) {
                    double val = (double) (pixels[x][y] - min) / (double) (max - min);  // Valor entre 0 y 1
                    
                    //System.out.print(val + "  ");

                    if (val < 0.0) {
                        val = 0.0;
                    }

                    if (val > 1.0) {
                        val = 1.0;
                    }

                    float hue = (float) val;
                    float saturation = 1.0f;
                    float brightness = 1.0f;

                    Color c = Color.getHSBColor(hue, saturation, brightness);
                    g2.setColor(c);
                    g2.drawRect((int) (x * pixelWidth), (int) (y * pixelHeight), (int) pixelWidth, (int) pixelHeight);
                    g2.fillRect((int) (x * pixelWidth), (int) (y * pixelHeight), (int) pixelWidth, (int) pixelHeight);
                }
                //System.out.println("");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(125, 125));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
