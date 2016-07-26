/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ardufocuser.starfocusing.gui;

import ardufocuser.starfocusing.FitsImage;
import ardufocuser.starfocusing.Star;
import ardufocuser.starfocusing.StarFilterStatus;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author zerjillo josemlp
 */
public class StarFieldPanel extends javax.swing.JPanel {

    private FitsImage fits;
    private BufferedImage starFieldImage;
    private double zoom = 1.0;
    private int minimumHistogram = 0;
    private int maximumHistogram = 65535;
    private double gamma = 1.0;
    private boolean inverted = false;
    private StarFieldFrame sff = null;

    private int selectedPixelX = -1;
    private int selectedPixelY = -1;

    private ArrayList<Star> stars;

    /**
     * Creates new form StarFieldPanel
     */
    public StarFieldPanel(FitsImage fits, StarFieldFrame sff) {
        initComponents();

        this.fits = fits;
        this.sff = sff;

        zoom = 1.0;
        gamma = 1.0;
        minimumHistogram = 0;
        maximumHistogram = 65535;
        inverted = false;

        getImageAndRepaint();

        setPreferredSize(new Dimension(starFieldImage.getWidth(), starFieldImage.getHeight()));
    }

    public void setSelectedPixel(int x, int y) {
        selectedPixelX = x;
        selectedPixelY = y;

        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;

        setPreferredSize(new Dimension((int) (starFieldImage.getWidth() * zoom), (int) (starFieldImage.getHeight() * zoom)));

        revalidate();
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;

        getImageAndRepaint();
    }

    public int getMinimumHistogram() {
        return minimumHistogram;
    }

    public void setMinimumHistogram(int minimumHistogram) {
        this.minimumHistogram = minimumHistogram;

        getImageAndRepaint();
    }

    public int getMaximumHistogram() {
        return maximumHistogram;
    }

    public void setMaximumHistogram(int maximumHistogram) {
        this.maximumHistogram = maximumHistogram;

        getImageAndRepaint();
    }

    private void getImageAndRepaint() {
        starFieldImage = fits.asBufferedImage(gamma, minimumHistogram, maximumHistogram, inverted);

        repaint();
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;

        getImageAndRepaint();
    }

    public void paintComponent(Graphics g) {
        Color starOkColor = new Color(0, 192, 0, 255);
        Color starFilteredByMarginColor = new Color(0, 255, 255, 255);
        Color starFilteredByContrastColor = Color.ORANGE;
        Color starFilteredByDistance = Color.BLUE;
        Color starFilteredByFWHM = Color.RED;
        Color starFilteredByGauss = Color.YELLOW;
        Color starFilteredByNumber = Color.PINK;
        
        g.drawImage(starFieldImage, 0, 0, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight(), null);

        if ((selectedPixelX >= 0) && (selectedPixelY >= 0) && (selectedPixelX < fits.getWidth()) && selectedPixelY < fits.getHeight()) {
            drawCross(g, new Color(255, 0, 0, 255), selectedPixelX, selectedPixelY, 3);
        }

        if (stars != null) {
            for (int i = 0; i < stars.size(); i++) {
                Star star = stars.get(i);
                
                Color c = starOkColor;
                switch (star.getFilterStatus()){
                    case OK:
                        c = starOkColor;
                        break;
                    case FILTERED_BY_MARGIN:
                        c = starFilteredByMarginColor;
                        break;
                    case FILTERED_BY_CONTRAST:
                        c = starFilteredByContrastColor;
                        break;
                        
                     case FILTERED_BY_DISTANCE:
                        c = starFilteredByDistance;
                        break;
                    
                    case FILTERED_BY_FWHM:
                        c = starFilteredByFWHM;
                        break;
                        
                     case FILTERED_BY_GAUSS:
                        c = starFilteredByGauss;
                        break;
                         
                      case FILTERED_BY_NUMBER_STAR_MAX_BRIGHNESS:
                        c = starFilteredByNumber;
                        break;
                         
                         
                }

                drawCross(g, c, star.getxPos(), star.getyPos(), 2);
                
                if (star.getFilterStatus() == StarFilterStatus.OK) {
                    drawPlus(g, c, star.getxPos(), star.getyPos(), 2);
                }
            }
        }
    }

    private void drawCross(Graphics g, Color color, int x, int y, int size) {
        g.setColor(color);
        
        int rx = (int) (x * zoom + zoom / 2);
        int ry = (int) (y * zoom + zoom / 2);

        g.drawLine(rx - size, ry - size, rx + size, ry + size);
        g.drawLine(rx + size, ry - size, rx - size, ry + size);
    }
    
    private void drawPlus(Graphics g, Color color, int x, int y, int size) {
        g.setColor(color);
        
        int rx = (int) (x * zoom + zoom / 2);
        int ry = (int) (y * zoom + zoom / 2);

        g.drawLine(rx, ry - size, rx, ry + size);
        g.drawLine(rx - size, ry, rx + size, ry);
    }

    public void setStars(ArrayList<Star> stars) {
        this.stars = stars;

        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

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

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (sff != null) {
            int x = (int) (((double) evt.getX()) / zoom);
            int y = (int) (((double) evt.getY()) / zoom);

            if (evt.getClickCount() == 2) {
                sff.searchPeak(x, y);
            } else {
                sff.setClickCoordinates(x, y);
            }
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (sff != null) {
            int x = (int) (((double) evt.getX()) / zoom);
            int y = (int) (((double) evt.getY()) / zoom);

            sff.setClickCoordinates(x, y);
        }
    }//GEN-LAST:event_formMouseDragged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
