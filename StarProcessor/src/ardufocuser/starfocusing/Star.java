/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ardufocuser.starfocusing;

public class Star {

    private int xPos;
    private int yPos;
    private int luminosity;
    private StarFilterStatus filterStatus;

    public Star(FitsImage fits, int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        luminosity = fits.getValue(xPos, yPos);
        filterStatus = StarFilterStatus.OK;
    }

    public void setFilterStatus(StarFilterStatus filterStatus) {
        this.filterStatus = filterStatus;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getLuminosity() {
        return luminosity;
    }

    public StarFilterStatus getFilterStatus() {
        return filterStatus;
    }

    public Boolean isOKStatus() {

        return (filterStatus == filterStatus.OK);
    }

    /**
     * Escribe por pantalla la estructura estrella en un formato legible. USE:
     * Debug.
     */
    @Override
    public String toString() {
        return "{" + "coordx=" + this.getxPos() + ", coordy=" + this.yPos + ", maxlux=" + this.getLuminosity() + ", Filter Status=" + this.getFilterStatus() + "}";
    }

}
