/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.components.excel.common;

import de.rcenvironment.core.datamodel.api.TypedDatumSerializer;
import de.rcenvironment.core.datamodel.api.TypedDatumService;


/**
 * Holds any OSGi-Service for common classes.
 *
 * @author Markus Kunde
 */
public class ServiceHolder {

    
    private static TypedDatumSerializer serializer;

    protected void bindTypedDatumService(TypedDatumService newTypedDatumService) {
        serializer = newTypedDatumService.getSerializer();
    }
  
    protected void unbindTypedDatumService(TypedDatumService oldTypedDatumService) {}
    
    protected TypedDatumSerializer getSerializer() {
        return serializer;
    }
}
