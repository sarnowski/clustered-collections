package com.github.sarnowski.collections;

import org.jgroups.Channel;

/**
 * @author Tobias Sarnowski
 */
public interface Clustered {

    Channel getChannel();

    void setUpdateCallback(ClusterUpdateCallback callback);

}
