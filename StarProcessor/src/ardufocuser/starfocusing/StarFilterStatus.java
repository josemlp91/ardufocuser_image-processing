
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

/**
 *
 * @author zerjillo josemlp
 */

// Posibles estados de las estrellas tras aplicar filtrados.
public enum StarFilterStatus {
    OK,
    FILTERED_BY_MARGIN,
    FILTERED_BY_CONTRAST,
    FILTERED_BY_DISTANCE,
    FILTERED_BY_NUMBER_STAR_MAX_BRIGHNESS,
    FILTERED_BY_FWHM,
    FILTERED_BY_GAUSS
    
}
