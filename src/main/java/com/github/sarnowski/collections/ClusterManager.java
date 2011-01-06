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

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.logging.Log;
import org.jgroups.logging.LogFactory;
import org.jgroups.util.Util;

/**
 * The ClusterManager encapsulates the JGroups handling and provides easy
 * cluster handling through the {@link ClusterManaged} interface.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 * @param <A> serializable action identifier
 * @param <P> serializable payload
 * @param <S> serializable state
 * @see ClusterManaged
 */
final class ClusterManager<A,P,S> implements Receiver {
    private final Log LOG = LogFactory.getLog(ClusterManager.class);

    private final Channel channel;
    private final ClusterManaged<A,P,S> managed;

    ClusterManager(String clusterName, Channel channel, ClusterManaged<A,P,S> managed) throws ChannelException {
        this.channel = channel;
        this.managed = managed;

        channel.setReceiver(this);
        channel.setOpt(Channel.LOCAL, false);

        if (LOG.isTraceEnabled())
            LOG.trace("connect()");
        channel.connect(clusterName);
        channel.getState(null, 5000);
    }

    /**
     * Provides the underlying channel.
     *
     * @return the underlying channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Sends an update to the cluster members.
     *
     * @param action the update action
     * @param payload the payload
     */
    public void sendUpdate(A action, P payload) {
        if (LOG.isDebugEnabled())
            LOG.debug("sendUpdate(" + action + ", " + payload + ")");
        final ClusterUpdate<A,P> update = new ClusterUpdate<A,P>(action, payload);
        final byte[] buffer;
        try {
            buffer = Util.objectToByteBuffer(update);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        try {
            channel.send(new Message(null, null, buffer));
        } catch (ChannelNotConnectedException e) {
            throw new IllegalStateException(e);
        } catch (ChannelClosedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void viewAccepted(View new_view) {
        if (LOG.isTraceEnabled())
            LOG.trace("viewAccepted(" + new_view + ")");
    }

    @Override
    public void suspect(Address suspected_mbr) {
        if (LOG.isTraceEnabled())
            LOG.trace("suspect(" + suspected_mbr + ")");
    }

    @Override
    public void block() {
        if (LOG.isTraceEnabled())
            LOG.trace("block()");
    }

    @Override
    public void receive(Message msg) {
        if (LOG.isTraceEnabled())
            LOG.trace("receive(" + msg + ")");

        final ClusterUpdate<A,P> update;
        try {
            update = (ClusterUpdate) Util.objectFromByteBuffer(msg.getBuffer());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        managed.handleUpdate(update.getActionIdentifier(), update.getPayload());
    }

    @Override
    public byte[] getState() {
        if (LOG.isTraceEnabled())
            LOG.trace("getState()");
        try {
            return Util.objectToByteBuffer(managed.provideClusterState());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setState(byte[] state) {
        if (LOG.isTraceEnabled())
            LOG.trace("setState(...)");
        try {
            managed.updateClusterState((S)Util.objectFromByteBuffer(state));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "ClusterManager{" +
                "channel=" + channel +
                '}';
    }
}
