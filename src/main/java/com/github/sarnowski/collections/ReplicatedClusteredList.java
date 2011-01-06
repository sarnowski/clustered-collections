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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a clustered {@link List}.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 */
final class ReplicatedClusteredList<T> extends AbstractList<T> implements
        ClusteredList<T>,
        ClusterManaged<ReplicatedClusteredList.ListActions,ReplicatedClusteredList.ListPayload<T>,List<T>> {

    private final Log LOG = LogFactory.getLog(ReplicatedClusteredList.class);

    private List<T> localList = new ArrayList<T>();

    private final ClusterManager<ListActions,ReplicatedClusteredList.ListPayload<T>,List<T>> clusterManager;
    private ClusterUpdateCallback updateCallback = null;

    public ReplicatedClusteredList(String clusterName, Channel channel) throws ChannelException {
        clusterManager = new ClusterManager<ListActions,ReplicatedClusteredList.ListPayload<T>,List<T>>(clusterName, channel, this);
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
    public void handleUpdate(ListActions action, ListPayload<T> payload) {
        if (LOG.isTraceEnabled())
            LOG.trace("handleUpdate(" + action + ", " + payload + ")");
        switch (action) {
            case SET:
                localList.set(payload.getIndex(),payload.getElement());
                break;
            case ADD:
                localList.add(payload.getIndex(), payload.getElement());
                break;
            case REMOVE:
                localList.remove(payload.getIndex());
                break;
            case CLEAR:
                localList.clear();
                break;
        }
        if (updateCallback != null) {
            updateCallback.clusterUpdated();
        }
    }

    @Override
    public void updateClusterState(List<T> state) {
        localList = state;
    }

    @Override
    public List<T> provideClusterState() {
        return localList;
    }

    @Override
    public int size() {
        return localList.size();
    }

    @Override
    public T get(int index) {
        return localList.get(index);
    }

    @Override
    public T set(int index, T element) {
        clusterManager.sendUpdate(ListActions.SET, new ListPayload<T>(index, element));
        return localList.set(index, element);
    }


    @Override
    public void add(int index, T element) {
        clusterManager.sendUpdate(ListActions.ADD, new ListPayload<T>(index, element));
        localList.add(index, element);
    }

    @Override
    public T remove(int index) {
        clusterManager.sendUpdate(ListActions.REMOVE, new ListPayload<T>(index, null));
        return localList.remove(index);
    }

    @Override
    public void clear() {
        clusterManager.sendUpdate(ListActions.CLEAR, null);
        localList.clear();
    }

    @Override
    public String toString() {
        return "ReplicatedClusteredList{" +
                "listSize=" + localList.size() +
                ", cluster=" + clusterManager +
                '}';
    }

    public static enum ListActions {
        SET,
        ADD,
        REMOVE,
        CLEAR
    }

    public static class ListPayload<T> implements Serializable {
        private final int index;
        private final T element;

        public ListPayload(int index, T element) {
            this.index = index;
            this.element = element;
        }

        public int getIndex() {
            return index;
        }

        public T getElement() {
            return element;
        }

        @Override
        public String toString() {
            return "ListPayload{" +
                    "index=" + index +
                    ", element=" + element +
                    '}';
        }
    }
}
