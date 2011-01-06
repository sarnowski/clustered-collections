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
package com.github.sarnowski.collections.demo;

import com.github.sarnowski.collections.ClusteredCollection;
import com.github.sarnowski.collections.ClusteredCollections;
import com.github.sarnowski.collections.ClusteredSet;
import org.jgroups.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Sarnowski
 */
public final class ReplicatedClusteredSetDemo extends AbstractClusteredDemo {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedClusteredSetDemo.class);

    public ReplicatedClusteredSetDemo(ClusteredCollection<String> clusteredCollection) {
        super(clusteredCollection);
    }

    public static void main(String[] arguments) {
        System.out.println("ReplicatedClusteredSet Demo");
        System.out.println();

        final ClusteredSet<String> clusteredSet;
        try {
            clusteredSet = ClusteredCollections.newReplicatedClusteredSet("ReplicatedClusteredSetDemo");
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }

        ReplicatedClusteredSetDemo demo = new ReplicatedClusteredSetDemo(clusteredSet);
        demo.run();
    }
}