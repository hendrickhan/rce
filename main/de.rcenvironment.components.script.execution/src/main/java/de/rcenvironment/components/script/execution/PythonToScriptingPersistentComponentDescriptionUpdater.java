/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.script.execution;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import de.rcenvironment.components.script.common.ScriptComponentConstants;
import de.rcenvironment.core.component.update.api.PersistentComponentDescription;
import de.rcenvironment.core.component.update.api.PersistentDescriptionFormatVersion;
import de.rcenvironment.core.component.update.spi.PersistentComponentDescriptionUpdater;

/**
 * Implementation of {@link PersistentComponentDescriptionUpdater}.
 * 
 * @author Sascha Zur
 */
public class PythonToScriptingPersistentComponentDescriptionUpdater implements PersistentComponentDescriptionUpdater {

    private static final String V2_9 = "2.9";

    private final String currentVersion = "3.0";

    @Override
    public String[] getComponentIdentifiersAffectedByUpdate() {
        return new String[] { "de.rcenvironment.rce.components.python.PythonComponent_Python" };
    }

    @Override
    public int getFormatVersionsAffectedByUpdate(String persistentComponentDescriptionVersion, boolean silent) {
        int versionsToUpdate = PersistentDescriptionFormatVersion.NONE;
        
        if (!silent
            && (persistentComponentDescriptionVersion == null
            || persistentComponentDescriptionVersion.compareTo(V2_9) < 0)) {
            versionsToUpdate = versionsToUpdate | PersistentDescriptionFormatVersion.BEFORE_VERSON_THREE;
        }
        if (!silent && persistentComponentDescriptionVersion != null
            && persistentComponentDescriptionVersion.compareTo(currentVersion) < 0) {
            versionsToUpdate = versionsToUpdate | PersistentDescriptionFormatVersion.FOR_VERSION_THREE;
        }
        return versionsToUpdate;
    }

    @Override
    public PersistentComponentDescription performComponentDescriptionUpdate(int formatVersion,
        PersistentComponentDescription description, boolean silent)
        throws IOException {
        if (!silent) {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createJsonParser(description.getComponentDescriptionAsString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonParser);
            if (formatVersion == PersistentDescriptionFormatVersion.BEFORE_VERSON_THREE) {
                return firstUpdate(node, mapper, description);
            }
        }
        return description;
    }

    private PersistentComponentDescription firstUpdate(JsonNode node, ObjectMapper mapper, PersistentComponentDescription description)
        throws JsonParseException, JsonGenerationException, JsonMappingException, IOException {
        // update python placeholder (V 1.0)
        JsonNode configurationNode = node.get("configuration");
        Iterator<JsonNode> nodeIterator = configurationNode.getElements();
        int index = 0;
        while (nodeIterator.hasNext()) {
            JsonNode configurationValueNode = nodeIterator.next();
            if (configurationValueNode.getTextValue().startsWith("pythonInstallation:java.lang.String:")) {
                break;
            }
            index++;
        }
        ((ArrayNode) configurationNode).remove(index);
        ((ArrayNode) configurationNode).add("pythonExecutionPath:java.lang.String:${pythonExecutionPath}");
        // Add language for conversion to script component
        ((ArrayNode) configurationNode).add("scriptLanguage:java.lang.String:Python");
        // replace all _dm_[""] occurences in user script
        nodeIterator = configurationNode.getElements();
        index = 0;
        while (nodeIterator.hasNext()) {
            JsonNode configurationValueNode = nodeIterator.next();
            if (configurationValueNode.getTextValue().startsWith("script:java.lang.String:")) {
                break;
            }
            index++;
        }
        String script = configurationNode.get(index).getTextValue();
        Pattern p = Pattern.compile("(_dm_\\[\".*\"\\])");
        Matcher m = p.matcher(script);

        while (m.find()) {
            String group = m.group(1);
            String name = group.substring(group.indexOf("\"") + 1, group.lastIndexOf("\""));
            script = script.substring(0, script.indexOf(group)) + name + script.substring(script.indexOf(group) + group.length());
        }
        ((ArrayNode) configurationNode).remove(index);
        ((ArrayNode) configurationNode).add(script);

        // replace component identifier
        JsonNode componentNode = node.get("component");
        ((ObjectNode) componentNode).remove("identifier");
        ((ObjectNode) componentNode).put("identifier", ScriptComponentConstants.COMPONENT_ID);

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        description = new PersistentComponentDescription(writer.writeValueAsString(node));
        description.setComponentVersion(V2_9);
        return description;
    }
}
