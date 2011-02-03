/* Copyright (C) 2008  Egon Willighagen <egon.willighagen@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ons.solubility.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.google.business.GoogleManager;

public class SolubilityData {

    GoogleManager google = new GoogleManager();
    
    private Map<Integer,Measurement> measurements;
    
    public SolubilityData(String username, String password) throws Exception {
        if(username==null) throw new NullPointerException("undefined username");
    	if(password==null) throw new NullPointerException("undefined password");
    	google.setUserCredentials(username, password);
    	this.measurements = new HashMap<Integer,Measurement>();
    }
    
    public void download() throws Exception {
    	StringMatrix matrix = google.loadWorksheet("SolubilitiesSum", "Sheet1");
        Measurement measurement = null;
        // the first row has columns headers
        for (int row=2; row<matrix.getRowCount(); row++) {
        	measurement = new Measurement();
            measurement.setExperiment(matrix.get(row, 1));
            measurement.setSample(matrix.get(row, 2));
            measurement.setReference(matrix.get(row, 3));
            measurement.setSolute(matrix.get(row, 4));
            measurement.setSoluteSMILES(matrix.get(row, 5));
            measurement.setSolvent(matrix.get(row, 6));
            measurement.setSolventSMILES(matrix.get(row, 7));
            measurement.setConcentration(matrix.get(row, 8));
            measurements.put(row-1, measurement);
        }
    }
    
    public Collection<Measurement> getData() {
        return measurements.values();
    }

}
