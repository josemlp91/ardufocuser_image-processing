
/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
