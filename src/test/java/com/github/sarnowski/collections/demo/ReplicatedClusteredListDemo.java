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

import com.github.sarnowski.collections.ClusterUpdateCallback;
import com.github.sarnowski.collections.ClusteredCollections;
import com.github.sarnowski.collections.ClusteredList;
import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Tobias Sarnowski
 */
public final class ReplicatedClusteredListDemo implements ClusterUpdateCallback, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedClusteredListDemo.class);

    private List<String> stringList;

    public ReplicatedClusteredListDemo() {
        try {
            ClusteredList<String> clusteredList = ClusteredCollections.newReplicatedClusteredList("ReplicatedClusteredListDemo");
            clusteredList.setUpdateCallback(this);

            stringList = clusteredList;
        } catch (ChannelException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void run() {
        System.out.println();
        System.out.println("Cluster started, type 'help' for a list of available commands.");
        System.out.println();
        while (true) {
            final String command;
            try {
                command = Util.readStringFromStdin("$ ").trim();
            } catch (Exception e) {
                System.out.println();
                System.exit(1);
                throw new UnsupportedOperationException();
            }

            final String[] tokens = command.split(" ");
            if (tokens.length == 0 || tokens[0].length() == 0) {
                continue;
            }

            if ("list".equals(tokens[0].toLowerCase())) {
                printList();
                continue;
            }

            if ("cluster".equals(tokens[0].toLowerCase())) {
                ClusteredList<String> clusteredList = (ClusteredList<String>) stringList;
                View view = clusteredList.getChannel().getView();
                System.out.println("Cluster informations");
                System.out.println(" ID:  " + view.getViewId());
                System.out.println(" Local:  " + clusteredList.getChannel().getAddress());
                System.out.println(" Creator:  " + view.getCreator());
                System.out.println(" Member:");
                for (Address member: view.getMembers()) {
                    System.out.println("   " + member);
                }
                continue;
            }

            if ("clear".equals(tokens[0].toLowerCase())) {
                stringList.clear();
                continue;
            }

            if ("add".equals(tokens[0].toLowerCase())) {
                if (tokens.length == 2) {
                    stringList.add(tokens[1]);
                } else {
                    stringList.add(Integer.valueOf(tokens[1]), tokens[2]);
                }
                continue;
            }

            if ("set".equals(tokens[0].toLowerCase()) && tokens.length == 3) {
                stringList.set(Integer.valueOf(tokens[1]), tokens[2]);
                continue;
            }

            if ("remove".equals(tokens[0].toLowerCase()) && tokens.length == 2) {
                if (tokens[1].matches("^\\d+$")) {
                    stringList.remove((int)Integer.valueOf(tokens[1]));
                } else {
                    stringList.remove(tokens[1]);
                }
                continue;
            }

            if ("quit".equals(tokens[0].toLowerCase())) {
                System.exit(0);
            }

            if (!"help".equals(tokens[0].toLowerCase())) {
                System.out.println("Unknown command '" + tokens[0] + "'!");
            }
            System.out.println("Available Commands:");
            System.out.println();
            System.out.println("  list                 - print the content of the clustered list");
            System.out.println("  cluster              - print the members of the cluter");
            System.out.println();
            System.out.println("  add <word>           - add a word to the list");
            System.out.println("  add <index> <word>   - add a word to the list at the specified index");
            System.out.println("  set <index> <word>   - replace a word in the list at the specified index");
            System.out.println("  remove <word>        - remove a word from the list");
            System.out.println("  remove <index>       - remove the word at the specified index from the list");
            System.out.println("  clear                - remove all list entries");
            System.out.println();
            System.out.println("  help                 - display this command list");
            System.out.println("  quit                 - exit the program");
            System.out.println();
        }
    }

    @Override
    public void clusterUpdated() {
        System.out.println();
        System.out.println("Update received!");
        printList();
    }

    public void printList() {
        int counter = 0;
        for (String entry: stringList) {
            System.out.println(" " + counter + ": " + entry);
            counter++;
        }
        System.out.println("Size: " + counter);
    }

    public static void main(String[] arguments) {
        System.out.println("ReplicatedClusteredList Demo");
        System.out.println();
        ReplicatedClusteredListDemo demo = new ReplicatedClusteredListDemo();
        demo.run();
    }
}