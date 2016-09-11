/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ardufocuser.starfocusing.gui;

import ardufocuser.starfocusing.FitsImage;
import ardufocuser.starfocusing.Processing;
import ardufocuser.starfocusing.Star;
import ardufocuser.starfocusing.StarFilterStatus;
import ardufocuser.starfocusing.Utils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author zerjillo josemlp
 */
public class StarFieldFrame extends javax.swing.JInternalFrame {

    private StarFieldPanel sfp;
    private MatrixPanel mp;
    private CartesianPanel cp;
    private int xClick;
    private int yClick;
    private FitsImage fits;

    private ArrayList<Star> stars;
    private boolean detectStars;
    private int radiusSurroundingPeak;
    private boolean filterByMargin;
    private int marginSize;
    private boolean contrastFilter;
    private int contrastRadius;
    private double minimumQuotient;

    private boolean distanceFilter;
    private int distanceSize;

    private boolean FWHMFilter;
    private int FWHMMin;
    private int FWHMRadius;

    private boolean GaussianFilter;
    private int gausianRadius;
    private double gausianNormalMin;
    private double gausianMeanMin;
    private double gausianSigmaMin;
    
     private boolean numberFilter;
     private int numberSize;

    /**
     * Creates new form StarFieldFrame
     */
    public StarFieldFrame(FitsImage fits) {
        initComponents();

        detectStars = false;
        radiusSurroundingPeak = 1;

        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/ardufocuser/starfocusing/gui/img/esquimoIcon20.png"));

        this.setFrameIcon(icon);

        this.fits = fits;

        sfp = new StarFieldPanel(fits, this);
        scroll.setViewportView(sfp);

        String fn = fits.getFilename();
        setTitle(fn.substring(fn.lastIndexOf("/") + 1));

        width.setText(fits.getWidth() + "");
        height.setText(fits.getHeight() + "");
        max.setText(fits.getMax() + "");
        min.setText(fits.getMin() + "");
        mean.setText(String.format("%.2f", fits.getMean()));

        setBounds((int) (40 * Math.random()), (int) (40 * Math.random()), 640, 480);
        minHText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                minHTextCambiado();
            }

            public void removeUpdate(DocumentEvent e) {
                minHTextCambiado();
            }

            public void insertUpdate(DocumentEvent e) {
                minHTextCambiado();
            }
        });

        maxHText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                maxHTextCambiado();
            }

            public void removeUpdate(DocumentEvent e) {
                maxHTextCambiado();
            }

            public void insertUpdate(DocumentEvent e) {
                maxHTextCambiado();
            }
        });

        mp = new MatrixPanel();
        matrixContainer.add(mp);

        cp = new CartesianPanel();
        cartesianContainer.add(cp);
    }

    public void setClickCoordinates(int xClick, int yClick) {
        this.xClick = xClick;
        this.yClick = yClick;

        setZooms(xClick, yClick);
        setFwhm(xClick, yClick);
    }

    public void searchPeak(int x, int y) {
        int[] searched = fits.getMaximumCoordinatesAround(x, y, 15);

        this.xClick = searched[0];
        this.yClick = searched[1];

        setZooms(xClick, yClick);
    }

    private void setZooms(int x, int y) {
        zoomInfo.setText("[" + x + ", " + y + "] -> " + fits.getValue(x, y) + "");
        sfp.setSelectedPixel(x, y);
        setZooms();
    }
    
    private void setFwhm(int x, int y){
        int [][] frame;
        double fwhm;
       
        //frame=fits.getSubMatrixCenter(x, y, 100);
       
        fwhm=Utils.computeFWHM(fits.getImageMatrix(), x, y, 10);
        //System.out.println(fwhm);
        
        DecimalFormat decimales = new DecimalFormat("0.0000");          
        fwhmInfo.setText("[FWHM] -> " + decimales.format(fwhm) + "");
    
    }

    private void setZooms() {
        int[][] subm = fits.getSubMatrixCenter(xClick, yClick, zoomsSize.getValue());
        mp.setPixels(subm);

        int[][] subm2 = fits.getSubMatrixCenter(xClick, yClick, zoomsSize.getValue());
        cp.setPixels(subm2);
    }

    public void setDetectStars(
            boolean detectStars, int radiusSurroundingPeak,
            boolean filterByMargin, int marginSize,
            boolean contrastFilter, int contrastRadius, double minimumQuotient,
            boolean distanceFilter, int distanceSize,
            boolean FWHMFilter, int FWHMMin, int FWHMRadius,
            boolean GaussianFilter, int gausianRadius, double gausianNormalMin, double gausianMeanMin, double gausianSigmaMin,
            boolean numberFilter, int numberSize
            ) {

        this.detectStars = detectStars;
        this.radiusSurroundingPeak = radiusSurroundingPeak;
        this.filterByMargin = filterByMargin;
        this.marginSize = marginSize;
        this.contrastFilter = contrastFilter;
        this.contrastRadius = contrastRadius;
        this.minimumQuotient = minimumQuotient;

        this.distanceSize = distanceSize;
        this.distanceFilter = distanceFilter;

        this.FWHMFilter = FWHMFilter;
        this.FWHMMin = FWHMMin;
        this.FWHMRadius = FWHMRadius;

        this.GaussianFilter = GaussianFilter;
        this.gausianRadius = gausianRadius;
        this.gausianNormalMin = gausianNormalMin;
        this.gausianMeanMin = gausianMeanMin;
        this.gausianSigmaMin = gausianSigmaMin;
        
         this.numberFilter=numberFilter;
         this.numberSize=numberSize;

        computeStars();
    }

    private void computeStars() {

        if (!detectStars) {
            stars = new ArrayList<Star>();
        } else {
            stars = Processing.detectStars(fits, radiusSurroundingPeak);
        }

        if (filterByMargin) {
            Processing.filterStarByMargin(stars, marginSize, fits.getWidth(), fits.getHeight());
        }

        if (contrastFilter) {
            Processing.filterByContrast(fits, stars, contrastRadius, minimumQuotient);
        }

        if (distanceFilter) {
            Processing.filterByMinDistance(fits, stars, distanceSize);

        }

        if (FWHMFilter) {
            Processing.filterByFWHM(fits, stars, FWHMRadius, FWHMMin);

        }

        if (GaussianFilter) {
            Processing.filterByGaussianParam(fits, stars, gausianRadius, gausianNormalMin, gausianMeanMin, gausianSigmaMin);

        }
        
         if (numberFilter) {
             Processing.filterByMaximunBrighness(fits, stars,numberSize);
           
        }

        int starvalid = 0;
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).getFilterStatus() == StarFilterStatus.OK) {
                starvalid++;
            }
        }

        validstars.setText(starvalid + "");

        sfp.setStars(stars);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        zooms = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        matrixContainer = new javax.swing.JPanel();
        cartesianContainer = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        zoomsSize = new javax.swing.JSlider();
        jPanel8 = new javax.swing.JPanel();
        zoomInfo = new javax.swing.JTextField();
        fwhmInfo = new javax.swing.JTextField();
        scroll = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        width = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        height = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        min = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        max = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        mean = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        validstars = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        zoom = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        gamma = new javax.swing.JSlider();
        gammaText = new javax.swing.JTextField();
        invert = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        minH = new javax.swing.JSlider();
        minHText = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        maxH = new javax.swing.JSlider();
        maxHText = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jSplitPane1.setDividerLocation(460);
        jSplitPane1.setResizeWeight(1.0);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new java.awt.BorderLayout());

        zooms.setMinimumSize(new java.awt.Dimension(100, 100));
        zooms.setLayout(new java.awt.BorderLayout());

        jPanel9.setLayout(new java.awt.GridLayout(2, 1));

        matrixContainer.setLayout(new java.awt.BorderLayout(1, 0));
        jPanel9.add(matrixContainer);

        cartesianContainer.setLayout(new java.awt.BorderLayout(1, 0));
        jPanel9.add(cartesianContainer);

        zooms.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.BorderLayout());

        zoomsSize.setMaximum(20);
        zoomsSize.setMinimum(5);
        zoomsSize.setOrientation(javax.swing.JSlider.VERTICAL);
        zoomsSize.setValue(10);
        zoomsSize.setPreferredSize(new java.awt.Dimension(16, 100));
        zoomsSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomsSizeStateChanged(evt);
            }
        });
        jPanel6.add(zoomsSize, java.awt.BorderLayout.CENTER);

        zooms.add(jPanel6, java.awt.BorderLayout.EAST);

        jPanel5.add(zooms, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.BorderLayout());

        zoomInfo.setEditable(false);
        zoomInfo.setColumns(16);
        zoomInfo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        zoomInfo.setText("-");
        zoomInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInfoActionPerformed(evt);
            }
        });
        jPanel8.add(zoomInfo, java.awt.BorderLayout.CENTER);

        fwhmInfo.setEditable(false);
        fwhmInfo.setColumns(16);
        fwhmInfo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fwhmInfo.setText("-");
        jPanel8.add(fwhmInfo, java.awt.BorderLayout.PAGE_START);

        jPanel5.add(jPanel8, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(jPanel5);

        scroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollMouseClicked(evt);
            }
        });
        jSplitPane1.setLeftComponent(scroll);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(3, 1));

        jLabel1.setText("Width:");
        jPanel2.add(jLabel1);

        width.setEditable(false);
        width.setColumns(5);
        width.setText("-");
        jPanel2.add(width);

        jLabel2.setText("Height:");
        jPanel2.add(jLabel2);

        height.setEditable(false);
        height.setColumns(5);
        height.setText("-");
        jPanel2.add(height);

        jLabel3.setText("Min:");
        jPanel2.add(jLabel3);

        min.setEditable(false);
        min.setColumns(5);
        min.setText("-");
        jPanel2.add(min);

        jLabel4.setText("Max:");
        jPanel2.add(jLabel4);

        max.setEditable(false);
        max.setColumns(5);
        max.setText("-");
        jPanel2.add(max);

        jLabel5.setText("Mean:");
        jPanel2.add(jLabel5);

        mean.setEditable(false);
        mean.setColumns(5);
        mean.setText("-");
        mean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meanActionPerformed(evt);
            }
        });
        jPanel2.add(mean);

        jLabel10.setText("Valid Stars");
        jPanel2.add(jLabel10);

        validstars.setEditable(false);
        validstars.setColumns(5);
        validstars.setText("-");
        jPanel2.add(validstars);

        jPanel1.add(jPanel2);

        jLabel6.setText("Zoom:");
        jPanel3.add(jLabel6);

        zoom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25%", "33%", "50%", "100%", "200%", "300%", "400%" }));
        zoom.setSelectedIndex(3);
        zoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomActionPerformed(evt);
            }
        });
        jPanel3.add(zoom);

        jLabel7.setText("    Gamma:");
        jPanel3.add(jLabel7);

        gamma.setMaximum(90);
        gamma.setMinimum(-95);
        gamma.setValue(0);
        gamma.setPreferredSize(new java.awt.Dimension(100, 16));
        gamma.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gammaStateChanged(evt);
            }
        });
        jPanel3.add(gamma);

        gammaText.setEditable(false);
        gammaText.setColumns(5);
        gammaText.setText("1.0");
        jPanel3.add(gammaText);

        invert.setText("Invert");
        invert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertActionPerformed(evt);
            }
        });
        jPanel3.add(invert);

        jPanel1.add(jPanel3);

        jLabel8.setText("Min:");
        jPanel4.add(jLabel8);

        minH.setMaximum(65535);
        minH.setValue(0);
        minH.setPreferredSize(new java.awt.Dimension(100, 16));
        minH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                minHStateChanged(evt);
            }
        });
        jPanel4.add(minH);

        minHText.setColumns(4);
        minHText.setText("0");
        jPanel4.add(minHText);

        jLabel9.setText("    Max:");
        jPanel4.add(jLabel9);

        maxH.setMaximum(65535);
        maxH.setValue(65535);
        maxH.setPreferredSize(new java.awt.Dimension(100, 16));
        maxH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxHStateChanged(evt);
            }
        });
        jPanel4.add(maxH);

        maxHText.setColumns(4);
        maxHText.setText("65535");
        jPanel4.add(maxHText);

        jPanel1.add(jPanel4);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void zoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomActionPerformed
        switch (zoom.getSelectedIndex()) {
            case 0:
                sfp.setZoom(0.25);
                break;
            case 1:
                sfp.setZoom(0.33);
                break;
            case 2:
                sfp.setZoom(0.5);
                break;
            case 3:
                sfp.setZoom(1);
                break;
            case 4:
                sfp.setZoom(2);
                break;
            case 5:
                sfp.setZoom(3);
                break;
            case 6:
                sfp.setZoom(4);
                break;
        }
    }//GEN-LAST:event_zoomActionPerformed

    private void gammaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gammaStateChanged
        double v = gamma.getValue();

        double g = 1.0;

        if (v >= 0) {
            g = 1.0 + v / 10;
        }

        if (v <= 0) {
            g = 1.0 - 0.01 * (-v);
        }

        gammaText.setText(String.format("%.2f", g));

        sfp.setGamma(g);
    }//GEN-LAST:event_gammaStateChanged

    private void minHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_minHStateChanged
        minHText.setText(minH.getValue() + "");
    }//GEN-LAST:event_minHStateChanged

    private void maxHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxHStateChanged
        maxHText.setText(maxH.getValue() + "");
    }//GEN-LAST:event_maxHStateChanged

    private void invertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertActionPerformed
        sfp.setInverted(invert.isSelected());
    }//GEN-LAST:event_invertActionPerformed

    private void zoomsSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomsSizeStateChanged
        setZooms();
    }//GEN-LAST:event_zoomsSizeStateChanged

    private void meanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meanActionPerformed

    private void scrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scrollMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_scrollMouseClicked

    private void zoomInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInfoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_zoomInfoActionPerformed

    private void minHTextCambiado() {
        try {
            int v = Integer.parseInt(minHText.getText());
            if ((v >= 0) && (v <= 65535)) {
                sfp.setMinimumHistogram(v);
            }
        } catch (Exception e) {
        }
    }

    private void maxHTextCambiado() {
        try {
            int v = Integer.parseInt(maxHText.getText());
            if ((v >= 0) && (v <= 65535)) {
                sfp.setMaximumHistogram(v);
            }
        } catch (Exception e) {
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cartesianContainer;
    private javax.swing.JTextField fwhmInfo;
    private javax.swing.JSlider gamma;
    private javax.swing.JTextField gammaText;
    private javax.swing.JTextField height;
    private javax.swing.JCheckBox invert;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel matrixContainer;
    private javax.swing.JTextField max;
    private javax.swing.JSlider maxH;
    private javax.swing.JTextField maxHText;
    private javax.swing.JTextField mean;
    private javax.swing.JTextField min;
    private javax.swing.JSlider minH;
    private javax.swing.JTextField minHText;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTextField validstars;
    private javax.swing.JTextField width;
    private javax.swing.JComboBox zoom;
    private javax.swing.JTextField zoomInfo;
    private javax.swing.JPanel zooms;
    private javax.swing.JSlider zoomsSize;
    // End of variables declaration//GEN-END:variables
}
