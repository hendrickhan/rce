/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.doe.gui.properties;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import de.rcenvironment.components.doe.common.DOEConstants;
import de.rcenvironment.core.component.model.configuration.api.ConfigurationDescription;
import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.EndpointType;
import de.rcenvironment.core.gui.workflow.editor.commands.endpoint.AddDynamicEndpointCommand;
import de.rcenvironment.core.gui.workflow.editor.properties.EndpointSelectionPane;

/**
 * Add command for changing custom table if neccessary.
 * 
 * @author Sascha Zur
 */
public class DOEAddDynamicEndpointCommand extends AddDynamicEndpointCommand {

    protected static final Log LOGGER = LogFactory.getLog(DOEAddDynamicEndpointCommand.class);

    public DOEAddDynamicEndpointCommand(EndpointType direction, String id, String name, DataType type, Map<String, String> metaData,
        EndpointSelectionPane refresh) {
        super(direction, id, name, type, metaData, refresh);
    }

    @Override
    public void execute() {
        super.execute();
        ConfigurationDescription config =
            getWorkflowNode().getConfigurationDescription();
        if (DOEConstants.DOE_ALGORITHM_CUSTOM_TABLE.equals(config.getConfigurationValue(DOEConstants.KEY_METHOD))) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String[][] tableValuesDouble = null;
                if (config.getConfigurationValue(DOEConstants.KEY_TABLE) != null
                    && !config.getConfigurationValue(DOEConstants.KEY_TABLE).isEmpty()) {
                    tableValuesDouble = mapper.readValue(config.getConfigurationValue(DOEConstants.KEY_TABLE), String[][].class);
                }
                if (tableValuesDouble != null) {
                    for (int i = 0; i < tableValuesDouble.length; i++) {
                        String[] newArray = new String[tableValuesDouble[i].length + 1];
                        System.arraycopy(tableValuesDouble[i], 0, newArray, 0, tableValuesDouble[i].length);
                        newArray[newArray.length - 1] = "";
                        tableValuesDouble[i] = newArray;
                    }
                }
                config.setConfigurationValue(DOEConstants.KEY_TABLE, mapper.writeValueAsString(tableValuesDouble));
            } catch (IOException e) {
                LOGGER.error("Could not read custom table", e);
            }
        }
    }

    @Override
    public void undo() {
        super.undo();
        ConfigurationDescription config =
            getWorkflowNode().getConfigurationDescription();
        if (DOEConstants.DOE_ALGORITHM_CUSTOM_TABLE.equals(config.getConfigurationValue(DOEConstants.KEY_METHOD))) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String[][] tableValuesDouble = null;
                if (config.getConfigurationValue(DOEConstants.KEY_TABLE) != null
                    && !config.getConfigurationValue(DOEConstants.KEY_TABLE).isEmpty()) {
                    tableValuesDouble = mapper.readValue(config.getConfigurationValue(DOEConstants.KEY_TABLE), String[][].class);
                }
                if (tableValuesDouble != null) {
                    for (int i = 0; i < tableValuesDouble.length; i++) {
                        String[] newArray = new String[tableValuesDouble[i].length - 1];
                        System.arraycopy(tableValuesDouble[i], 0, newArray, 0, tableValuesDouble[i].length - 1);
                        tableValuesDouble[i] = newArray;
                    }
                }
                config.setConfigurationValue(DOEConstants.KEY_TABLE, mapper.writeValueAsString(tableValuesDouble));
            } catch (IOException e) {
                LOGGER.error("Could not read custom table", e);
            }
        }
    }
}
