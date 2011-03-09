/*******************************************************************************
 * Copyright (c) 2011  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.google.business;

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass ("Manager for accessing the Google online services.")
public interface IGoogleManager extends IBioclipseManager {

    @PublishedMethod(
        params="String username, String password",
        methodSummary="Sets the Google user name and password."
    )
    public void setUserCredentials(String username, String password);

    @PublishedMethod(
        methodSummary="Lists the spreadsheets."
    )
    public List<String> listSpreadsheets() throws BioclipseException;

    @PublishedMethod(
        methodSummary="Downloads a worksheet from a spreadsheet.",
        params="String spreadsheet, String worksheet"
    )
    public StringMatrix loadWorksheet(String spreadsheet, String worksheet);

    @PublishedMethod(
        methodSummary="Downloads a worksheet from a spreadsheet.",
        params="String spreadsheet, String worksheet, " +
        		"boolean firstRowHasHeaders"
    )
    public StringMatrix loadWorksheet(String spreadsheet, String worksheet,
    	boolean firstRowHasHeaders);
}
