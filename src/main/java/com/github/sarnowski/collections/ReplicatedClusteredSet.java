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
import org.jgroups.logging.Log;
import org.jgroups.logging.LogFactory;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements a clustered {@link Set}.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 */
final class ReplicatedClusteredSet<T> extends AbstractSet<T> implements
        ClusteredSet<T>,
        ClusterManaged<ReplicatedClusteredSet.SetActions,T,Set<T>> {

    private final Log LOG = LogFactory.getLog(ReplicatedClusteredMap.class);

    private Set<T> localSet = new HashSet<T>();

    private final ClusterManager<ReplicatedClusteredSet.SetActions,T,Set<T>> clusterManager;
    private ClusterUpdateCallback updateCallback = null;

    public ReplicatedClusteredSet(String clusterName, Channel channel) throws ChannelException {
        clusterManager = new ClusterManager<SetActions,T,Set<T>>(clusterName, channel, this);
    }

    @Override
    public Channel getChannel() {
        return clusterManager.getChannel();
    }

    @Override
    public void setUpdateCallback(ClusterUpdateCallback callback) {
        updateCallback = callback;
    }

    @Override
    public void handleUpdate(ReplicatedClusteredSet.SetActions action, T payload) {
        if (LOG.isTraceEnabled())
            LOG.trace("handleUpdate(" + action + ", " + payload + ")");
        switch (action) {
            case ADD:
                localSet.add(payload);
                break;
            case REMOVE:
                localSet.remove(payload);
                break;
        }
        if (updateCallback != null) {
            updateCallback.clusterUpdated();
        }
    }

    @Override
    public void updateClusterState(Set<T> state) {
        localSet = state;
    }

    @Override
    public Set<T> provideClusterState() {
        return localSet;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iterator = localSet.iterator();
        return new Iterator<T>() {
            private T next;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return next = iterator.next();
            }

            @Override
            public void remove() {
                clusterManager.sendUpdate(SetActions.REMOVE, next);
                iterator.remove();
            }
        };
    }

    @Override
    public int size() {
        return localSet.size();
    }

    @Override
    public boolean add(T t) {
        boolean inserted = localSet.add(t);
        if (inserted) {
            clusterManager.sendUpdate(SetActions.ADD, t);
        }
        return inserted;
    }

    @Override
    public String toString() {
        return "ReplicatedClusteredSet{" +
                "setSize=" + localSet.size() +
                ", cluster=" + clusterManager +
                '}';
    }

    public static enum SetActions {
        ADD,
        REMOVE
    }
}
