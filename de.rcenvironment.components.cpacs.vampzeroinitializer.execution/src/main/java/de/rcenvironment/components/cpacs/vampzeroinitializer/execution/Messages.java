/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.cpacs.vampzeroinitializer.execution;

import org.eclipse.osgi.util.NLS;

/**
 * Supports language specific messages.
 *
 * @author Jan Flink
 */
public class Messages extends NLS {

    /** Validation message. */
    public static String noXmlContentGenerated;

    /** Validation message. */
    public static String noXmlContentGeneratedLong;

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
