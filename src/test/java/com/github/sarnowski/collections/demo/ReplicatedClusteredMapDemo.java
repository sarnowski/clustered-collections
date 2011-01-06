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

import com.github.sarnowski.collections.ClusteredCollections;
import com.github.sarnowski.collections.ClusteredMap;
import org.jgroups.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tobias Sarnowski
 */
public final class ReplicatedClusteredMapDemo extends AbstractClusteredDemo implements ClusteredDemoCommandCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedClusteredMapDemo.class);

    private Map<String,String> map;

    public static void main(String[] arguments) {
        System.out.println("ReplicatedClusteredMap Demo");
        System.out.println();

        final ClusteredMap<String,String> clusteredMap;
        try {
            clusteredMap = ClusteredCollections.newReplicatedClusteredMap("ReplicatedClusteredMapDemo");
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }

        ReplicatedClusteredMapDemo demo = new ReplicatedClusteredMapDemo(clusteredMap);
        demo.run();
    }

    public ReplicatedClusteredMapDemo(ClusteredMap<String,String> clusteredMap) {
        super(clusteredMap);
        map = clusteredMap;
    }

    @Override
    public void printObjects() {
        int counter = 0;
        for (Map.Entry<String,String> entry: map.entrySet()) {
            System.out.println(" " + counter + ": " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Size: " + map.size());
    }

    @Override
    public List<ClusteredDemoCommand> getCommands() {
        List<ClusteredDemoCommand> commands = new ArrayList<ClusteredDemoCommand>();
        commands.add(new ClusteredDemoCommand.Def("put", "put <key> <value>", "adds an entry in the map", this));
        commands.add(new ClusteredDemoCommand.Def("remove", "remove <key>", "removes an entry from the map", this));
        commands.add(new ClusteredDemoCommand.Def("clear", "clear", "clears the map", this));
        return commands;
    }

    @Override
    public void callCommand(ClusteredDemoCommand cmd, String[] tokens) {
        if ("put".equals(cmd.getCommand())) {
            map.put(tokens[1], tokens[2]);
            return;
        }

        if ("remove".equals(cmd.getCommand())) {
            map.remove(tokens[1]);
            return;
        }

        if ("clear".equals(cmd.getCommand())) {
            map.clear();
            return;
        }
    }
}