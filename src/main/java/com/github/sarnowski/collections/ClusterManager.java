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
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Sarnowski
 */
final class ClusterManager<A,P,S> implements Receiver {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterManager.class);

    private final Channel channel;
    private final ClusterManaged<A,P,S> managed;

    ClusterManager(String clusterName, Channel channel, ClusterManaged<A,P,S> managed) throws ChannelException {
        this.channel = channel;
        this.managed = managed;

        channel.setReceiver(this);
        channel.setOpt(Channel.LOCAL, false);

        LOG.trace("connect()");
        channel.connect(clusterName);
        channel.getState(null, 5000);
    }

    public Channel getChannel() {
        return channel;
    }

    public void sendUpdate(A actionIdentifier, P payload) {
        LOG.debug("sendUpdate({}, {})", actionIdentifier, payload);
        final ClusterUpdate<A,P> update = new ClusterUpdate<A,P>(actionIdentifier, payload);
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
        LOG.trace("viewAccepted({})", new_view);
    }

    @Override
    public void suspect(Address suspected_mbr) {
        LOG.trace("suspect({})", suspected_mbr);
    }

    @Override
    public void block() {
        LOG.trace("block()");
    }

    @Override
    public void receive(Message msg) {
        LOG.trace("receive({})", msg);

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
        LOG.trace("getState()");
        try {
            return Util.objectToByteBuffer(managed.provideClusterState());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setState(byte[] state) {
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