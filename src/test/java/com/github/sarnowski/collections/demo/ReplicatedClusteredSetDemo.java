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
import com.github.sarnowski.collections.ClusteredSet;
import org.jgroups.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Tobias Sarnowski
 */
public final class ReplicatedClusteredSetDemo extends AbstractClusteredDemo implements ClusteredDemoCommandCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedClusteredSetDemo.class);

    private Set<String> words;

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

    public ReplicatedClusteredSetDemo(ClusteredSet<String> clusteredSet) {
        super(clusteredSet);
        words = clusteredSet;
    }

    @Override
    public void printObjects() {
        int counter = 0;
        for (String word: words) {
            System.out.println(" " + counter + ": " + word);
        }
        System.out.println("Size: " + words.size());
    }

    @Override
    public List<ClusteredDemoCommand> getCommands() {
        List<ClusteredDemoCommand> commands = new ArrayList<ClusteredDemoCommand>();
        commands.add(new ClusteredDemoCommand.Def("add", "add <word>", "adds a word to the set", this));
        commands.add(new ClusteredDemoCommand.Def("remove", "remove <word>", "removes a word from the list", this));
        commands.add(new ClusteredDemoCommand.Def("clear", "clear", "clears the map", this));
        return commands;
    }

    @Override
    public void callCommand(ClusteredDemoCommand cmd, String[] tokens) {
        if ("add".equals(cmd.getCommand())) {
            words.add(tokens[1]);
            return;
        }

        if ("remove".equals(cmd.getCommand())) {
            words.remove(tokens[1]);
            return;
        }

        if ("clear".equals(cmd.getCommand())) {
            words.clear();
            return;
        }
    }
}