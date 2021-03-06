/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.xpathchooser;


/**
 * Represents all contens of a predicate, without further parsing it.
 *
 * @author Heinrich Wendel
 * @author Markus Kunde
 */
public class XPathPredicate extends XPathStep {


    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + xValue + "]";
    }   

}
