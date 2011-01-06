package com.github.sarnowski.collections;

import org.jgroups.Channel;

/**
 * This interface provides basic informations about the underlying cluster.
 *
 * All implementations are based on this interface which in addition provides
 * a registration method to set up a change listener.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 */
public interface Clustered {

    /**
     * Returns the underlying JGroups channel to provide access to every cluster
     * aspect.
     * 
     * @return the underlying JGroups implementation
     */
    Channel getChannel();

    /**
     * Registers a change listener to the implementation. The implementation has to
     * call the callback everytime it receives an update from another cluster
     * member. Local changes will not trigger the callback.
     */
    void setUpdateCallback(ClusterUpdateCallback callback);

}
