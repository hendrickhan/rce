/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.database.common.jdbc;

import java.sql.DriverManager;

/**
 * Describes a JDBC driver registered via {@link DriverManager}.
 * 
 * @author Doreen Seider
 */
public interface JDBCDriverInformation {

    /**
     * @return the URL scheme of the driver
     */
    String getUrlScheme();
    
    /**
     * @return the display name of the driver
     */
    String getDisplayName();
}
