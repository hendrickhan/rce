/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.workflow.update.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;

import de.rcenvironment.core.component.update.api.PersistentComponentDescription;
import de.rcenvironment.core.utils.common.JsonUtils;

/**
 * Encapsulates information about a persistent workflow description (content of workflow files). This class is only a thin wrapper around
 * the JSON content of the workflow file.
 *
 * @author Doreen Seider
 * @author Sascha Zur
 * 
 * Note: See note in {@link PersistentComponentDescription}. --seid_do
 */
public class PersistentWorkflowDescription implements Serializable {

    private static final long serialVersionUID = -5664277831346681518L;

    private static final String WORKFLOW_VERSION = "workflowVersion";

    private String workflowVersion = "";

    private List<PersistentComponentDescription> componentDescriptions;

    /** contains everything except the component's parts. */
    private String workflowDescriptionString;

    public PersistentWorkflowDescription(List<PersistentComponentDescription> componentDescriptions,
        String workflowDescriptionString) throws JsonParseException, IOException {

        this.componentDescriptions = componentDescriptions;
        this.workflowDescriptionString = workflowDescriptionString;

        // parse information for convenient access via getter
        try (JsonParser jsonParser = new JsonFactory().createJsonParser(workflowDescriptionString)) {
            JsonNode node = JsonUtils.getDefaultObjectMapper().readTree(jsonParser);

            if (node.get(WORKFLOW_VERSION) != null) {
                workflowVersion = node.get(WORKFLOW_VERSION).getTextValue();
            }
        }

    }

    public String getWorkflowVersion() {
        return workflowVersion;
    }

    public List<PersistentComponentDescription> getComponentDescriptions() {
        return componentDescriptions;
    }

    public String getWorkflowDescriptionAsString() {
        return workflowDescriptionString;
    }

    @Override
    public String toString() {
        return getWorkflowDescriptionAsString();
    }
}
