/**
 * Copyright 2011 Tobias Sarnowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sarnowski.collections;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.conf.ProtocolStackConfigurator;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;

/**
 * This factory is the entry point for working with a clustered collection
 * instance.
 * 
 * All methods are overloaded with every possible type of configuration of
 * {@link org.jgroups.JChannel}. Fore more informations about configuring
 * the cluster look at the
 * <a href="http://jgroups.org/manual/html/">JGroups Documentation</a>. 
 * 
 * <p>
 * <strong>Example:</strong>
 * <pre>
 * List&lt;String&gt; myList = ClusteredCollections.newReplicatedClusteredList("MyClusterList");
 * </pre>
 * </p>
 *
 * @author Tobias Sarnowski
 * @since 1.0
 * @see org.jgroups.JChannel
 */
public final class ClusteredCollections {
    private ClusteredCollections() {
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, Channel channel) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, channel);
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel());
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, File properties) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, URL properties) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, Element properties) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, String properties) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredList<T> newReplicatedClusteredList(String clusterName, ProtocolStackConfigurator configurator) throws ChannelException {
        return new ReplicatedClusteredList<T>(clusterName, new JChannel(configurator));
    }


    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, Channel channel) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, channel);
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel());
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, File properties) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, URL properties) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, Element properties) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, String properties) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel(properties));
    }

    public static <T> ClusteredSet<T> newReplicatedClusteredSet(String clusterName, ProtocolStackConfigurator configurator) throws ChannelException {
        return new ReplicatedClusteredSet<T>(clusterName, new JChannel(configurator));
    }


    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, Channel channel) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, channel);
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel());
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, File properties) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel(properties));
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, URL properties) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel(properties));
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, Element properties) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel(properties));
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, String properties) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel(properties));
    }

    public static <K,V> ClusteredMap<K,V> newReplicatedClusteredMap(String clusterName, ProtocolStackConfigurator configurator) throws ChannelException {
        return new ReplicatedClusteredMap<K,V>(clusterName, new JChannel(configurator));
    }
}
