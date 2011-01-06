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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Tobias Sarnowski
 */
final class ReplicatedClusteredMap<K,V> extends AbstractMap<K,V> implements
        ClusteredMap<K,V>,
        ClusterManaged<ReplicatedClusteredMap.MapActions,ReplicatedClusteredMap.MapPayload<K,V>,Map<K,V>> {

    private final Log LOG = LogFactory.getLog(ReplicatedClusteredMap.class);

    private Map<K,V> localMap = new HashMap<K,V>();

    private final ClusterManager<ReplicatedClusteredMap.MapActions,MapPayload<K,V>,Map<K,V>> clusterManager;
    private ClusterUpdateCallback updateCallback = null;

    public ReplicatedClusteredMap(String clusterName, Channel channel) throws ChannelException {
        clusterManager = new ClusterManager<ReplicatedClusteredMap.MapActions,MapPayload<K,V>,Map<K,V>>(clusterName, channel, this);
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
    public void handleUpdate(ReplicatedClusteredMap.MapActions action, MapPayload<K,V> payload) {
        if (LOG.isTraceEnabled())
            LOG.trace("handleUpdate(" + action + ", " + payload + ")");
        switch (action) {
            case PUT:
                localMap.put(payload.getKey(), payload.getValue());
                break;
            case REMOVE:
                localMap.remove(payload.getKey());
                break;
        }
        if (updateCallback != null) {
            updateCallback.clusterUpdated();
        }
    }

    @Override
    public void updateClusterState(Map<K,V> state) {
        localMap = state;
    }

    @Override
    public Map<K,V> provideClusterState() {
        return localMap;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> localEntrySet = localMap.entrySet();
        return new AbstractSet<Entry<K,V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Iterator<Entry<K, V>> iterator = localEntrySet.iterator();
                return new Iterator<Entry<K, V>>() {
                    private Entry<K, V> next;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        return next = iterator.next();
                    }

                    @Override
                    public void remove() {
                        clusterManager.sendUpdate(
                                ReplicatedClusteredMap.MapActions.REMOVE,
                                new MapPayload<K, V>(next.getKey(), next.getValue()));
                        iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return localEntrySet.size();
            }

            @Override
            public boolean add(Entry<K, V> t) {
                boolean inserted = localEntrySet.add(t);
                if (inserted) {
                    clusterManager.sendUpdate(
                            ReplicatedClusteredMap.MapActions.PUT,
                            new MapPayload<K, V>(t.getKey(), t.getValue()));
                }
                return inserted;
            }
        };
    }

    @Override
    public int size() {
        return localMap.size();
    }

    @Override
    public V put(K key, V value) {
        clusterManager.sendUpdate(ReplicatedClusteredMap.MapActions.PUT, new MapPayload<K, V>(key, value));
        return localMap.put(key, value);
    }

    @Override
    public String toString() {
        return "ReplicatedClusteredSet{" +
                "mapSize=" + localMap.size() +
                ", cluster=" + clusterManager +
                '}';
    }

    public static enum MapActions {
        PUT,
        REMOVE
    }

    public static class MapPayload<K,V> implements Serializable {
        private final K key;
        private final V value;

        public MapPayload(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "SetPayload{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }
}