/*******************************************************************************
 * Copyright (c) 2007-2009  Jonathan Alvarsson
 *                    2011  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.google.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.managers.business.IBioclipseManager;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;

public class GoogleManager implements IBioclipseManager {

	private String username = "";
	private String password = "";

    protected GoogleManagerFactory factory;

    protected GoogleManagerFactory getFactory() throws Exception {
        if (factory == null) {
            factory = new GoogleManagerFactory();
        }
        return factory;
    }

    public String getManagerName() {
        return "google";
    }

    public void setUserCredentials(String username, String password) {
    	this.username = username;
    	this.password = password;
    }

    private List<SpreadsheetEntry> getSpreadsheets() throws BioclipseException {
    	SpreadsheetFeed feed;
		try {
			URL metafeedUrl = new URL(
			    "http://spreadsheets.google.com/feeds/spreadsheets/private/full"
			);
	    	SpreadsheetService service = getService();
			feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
		} catch (Exception e1) {
			throw new BioclipseException(
				"Error while download list of spreadsheets: " + e1.getMessage(),
				e1
			);
		}
        return feed.getEntries();
    }

	private SpreadsheetService getService() throws BioclipseException {
		SpreadsheetService service = new SpreadsheetService("Bioclipse");
		try {
			service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			throw new BioclipseException(
				"Error while authenticating user: " + e.getMessage(),
				e
			);
		}
		return service;
	}

    public List<String> listSpreadsheets() throws BioclipseException {
    	List<SpreadsheetEntry> spreadsheets = getSpreadsheets();
        List<String> titles = new ArrayList<String>();
        for (int i = 0; i < spreadsheets.size(); i++) {
            SpreadsheetEntry entry = spreadsheets.get(i);
            titles.add(entry.getTitle().getPlainText());
        }
    	return titles;
    }
    
    public StringMatrix loadWorksheet(String spreadsheet, String worksheet)
    throws BioclipseException {
    	List<SpreadsheetEntry> spreadsheets = getSpreadsheets();

    	SpreadsheetEntry sheet = null;
    	for (int i = 0; i < spreadsheets.size(); i++) {
    		SpreadsheetEntry entry = spreadsheets.get(i);
    		if (spreadsheet.equals(entry.getTitle().getPlainText())) {
    			sheet = entry;
    		}
    	}

    	if (spreadsheet == null) {
    		throw new BioclipseException(
    			"No spreadsheets with the name: " + spreadsheet
    		);
    	}

    	WorksheetQuery worksheetQuery =
    	    new WorksheetQuery(sheet.getWorksheetFeedUrl());

    	worksheetQuery.setTitleQuery(worksheet);
    	SpreadsheetService service = getService();
    	WorksheetFeed worksheetFeed;
		try {
			worksheetFeed = service.query(
				worksheetQuery, WorksheetFeed.class
			);
		} catch (Exception exception) {
			throw new BioclipseException(
	    		"Error while loading worksheet:  " +
	    		sheet.getTitle().getPlainText(),
	    		exception
			);
		}
    	List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
    	if (worksheets.isEmpty()) {
    		throw new BioclipseException(
    			"No worksheets with that name in spreadsheet " +
    			sheet.getTitle().getPlainText()
    		);
    	}

    	WorksheetEntry worksheetEntry = worksheets.get(0);

    	CellFeed cellFeed;
		try {
			cellFeed = service.getFeed(
				worksheetEntry.getCellFeedUrl(), CellFeed.class
			);
		} catch (Exception exception) {
    		throw new BioclipseException(
        		"Error while downloading worksheet content for " +
        		sheet.getTitle().getPlainText(),
        		exception
    		);
		}

    	List<CellEntry> cells = cellFeed.getEntries();
    	StringMatrix matrix = new StringMatrix();
    	for (CellEntry cellEntry : cells) {
    		Cell cell = cellEntry.getCell();
    		matrix.set(cell.getRow(), cell.getCol(), cell.getValue());
    	}
    	return matrix;
    }
}
