/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.model.api;

import de.rcenvironment.core.communication.common.LogicalNodeId;

/**
 * Represents a runnable component installation on an RCE node that fulfills the semantic behaviour of an abstract {@link ComponentRevision}
 * .
 * 
 * In the user interface, {@link ComponentInstallation}s are selected when running a workflow to define where the individual workflow
 * component nodes (which define a {@link ComponentRevision}) should be executed.
 * 
 * @author Robert Mischke
 * 
 * Note: The concept of {@link ComponentInstallation}, {@link ComponentRevision}, and {@link ComponentInterface} are introduced
 * later on compared to {@link ComponentDescription}. The concept was not fully implemented yet. Implementation of
 * {@link ComponentRevision} is missing at all. Also, the concept was not applied in a way that the benefits of this approach really
 * come through. --seid_do
 */
public interface ComponentInstallation extends Comparable<ComponentInstallation>, Cloneable {

    /**
     * @return the string form of this installation's location, which is a {@link LogicalNodeId}
     * 
     * @see LogicalNodeId#getLogicalNodeIdString()
     */
    String getNodeId();

    /**
     * @return the node id that defines this installation's location
     */
    LogicalNodeId fetchNodeIdAsObject();

    /**
     * @return the {@link ComponentRevision} that this {@link ComponentInstallation} fulfills
     */
    ComponentRevision getComponentRevision();

    /**
     * @return a string identifying this installation; only required to be unique per node
     */
    String getInstallationId();

    /**
     * @return the number of maximum parallel instances, or <code>null</code> if unlimited
     */
    Integer getMaximumCountOfParallelInstances();

    /**
     * @return <code>true</code> if {@link ComponentInstallation} is published (is made available for remote nodes), otherwise
     *         <code>false</code>
     */
    boolean getIsPublished();

}
