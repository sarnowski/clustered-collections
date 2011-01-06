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
import com.github.sarnowski.collections.ClusteredList;
import org.jgroups.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Sarnowski
 */
public final class ReplicatedClusteredListDemo extends AbstractClusteredDemo implements ClusteredDemoCommandCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedClusteredListDemo.class);

    private List<String> words;

    public static void main(String[] arguments) {
        System.out.println("ReplicatedClusteredList Demo");
        System.out.println();

        final ClusteredList<String> clusteredList;
        try {
            clusteredList = ClusteredCollections.newReplicatedClusteredList("ReplicatedClusteredListDemo");
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }

        ReplicatedClusteredListDemo demo = new ReplicatedClusteredListDemo(clusteredList);
        demo.run();
    }

    public ReplicatedClusteredListDemo(ClusteredList<String> clusteredList) {
        super(clusteredList);
        words = clusteredList;
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
        commands.add(new ClusteredDemoCommand.Def("add", "add [index] <word>", "adds a word to the list", this));
        commands.add(new ClusteredDemoCommand.Def("set", "set <index> <word>", "set a word in the list", this));
        commands.add(new ClusteredDemoCommand.Def("remove", "remove <index|word>", "removes a word from the list", this));
        commands.add(new ClusteredDemoCommand.Def("clear", "clear", "clears the list", this));
        return commands;
    }

    @Override
    public void callCommand(ClusteredDemoCommand cmd, String[] tokens) {
        if ("add".equals(cmd.getCommand())) {
            if (tokens[1].matches("\\d+")) {
                words.add(Integer.valueOf(tokens[1]), tokens[2]);
            } else {
                words.add(tokens[1]);
            }
            return;
        }

        if ("set".equals(cmd.getCommand())) {
            words.set(Integer.valueOf(tokens[1]), tokens[2]);
            return;
        }

        if ("remove".equals(cmd.getCommand())) {
            if (tokens[1].matches("\\d+")) {
                words.remove((int) Integer.valueOf(tokens[1]));
            } else {
                words.remove(tokens[1]);
            }
            return;
        }

        if ("clear".equals(cmd.getCommand())) {
            words.clear();
            return;
        }
    }

}