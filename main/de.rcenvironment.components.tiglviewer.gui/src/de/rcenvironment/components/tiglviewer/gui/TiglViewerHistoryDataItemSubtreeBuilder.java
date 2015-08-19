/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.tiglviewer.gui;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import de.rcenvironment.core.gui.datamanagement.browser.spi.ComponentHistoryDataItemSubtreeBuilder;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DefaultHistoryDataItemSubtreeBuilder;

/**
 * Implementation of {@link ComponentHistoryDataItemSubtreeBuilder} for the TiGL viewer component.
 * 
 * @author Doreen Seider
 */
public class TiglViewerHistoryDataItemSubtreeBuilder extends DefaultHistoryDataItemSubtreeBuilder {

    private static final Image COMPONENT_ICON;

    static {
        String iconPath = "platform:/plugin/de.rcenvironment.components.tiglviewer.execution/resources/tigl_16.png";
        URL url = null;
        try {
            url = new URL(iconPath);
        } catch (MalformedURLException e) {
            LogFactory.getLog(TiglViewerHistoryDataItemSubtreeBuilder.class).error("Component icon not found: " + iconPath);
        }
        if (url != null) {
            COMPONENT_ICON = ImageDescriptor.createFromURL(url).createImage();
        } else {
            COMPONENT_ICON = null;
        }
    }

    @Override
    public String[] getSupportedHistoryDataItemIdentifier() {
        return new String[] { TiglViewerComponentConstants.COMPONENT_ID };
    }

    @Override
    public Image getComponentIcon(String identifier) {
        return COMPONENT_ICON;
    }

}
