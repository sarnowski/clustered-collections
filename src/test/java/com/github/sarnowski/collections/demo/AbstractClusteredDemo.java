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
import com.github.sarnowski.collections.ClusteredCollection;
import org.jgroups.Address;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Sarnowski
 */
public abstract class AbstractClusteredDemo implements ClusterUpdateCallback, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClusteredDemo.class);

    private final ClusteredCollection<String> clusteredCollection;

    public AbstractClusteredDemo(ClusteredCollection<String> clusteredCollection) {
        this.clusteredCollection = clusteredCollection;
        clusteredCollection.setUpdateCallback(this);
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

            if ("show".equals(tokens[0].toLowerCase())) {
                printCollection();
                continue;
            }

            if ("cluster".equals(tokens[0].toLowerCase())) {
                View view = clusteredCollection.getChannel().getView();
                System.out.println("Cluster informations");
                System.out.println(" ID:  " + view.getViewId());
                System.out.println(" Local:  " + clusteredCollection.getChannel().getAddress());
                System.out.println(" Creator:  " + view.getCreator());
                System.out.println(" Member:");
                for (Address member: view.getMembers()) {
                    System.out.println("   " + member);
                }
                continue;
            }

            if ("clear".equals(tokens[0].toLowerCase())) {
                clusteredCollection.clear();
                continue;
            }

            if ("add".equals(tokens[0].toLowerCase())) {
                clusteredCollection.add(tokens[1]);
                continue;
            }

            if ("remove".equals(tokens[0].toLowerCase()) && tokens.length == 2) {
                clusteredCollection.remove(tokens[1]);
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
            System.out.println("  remove <word>        - remove a word from the list");
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
        System.out.println("Cluster update received:");
        printCollection();
    }

    public void printCollection() {
        int index = 0;
        for (String string: clusteredCollection) {
            System.out.println(" " + index + ": " + string);
            index++;
        }
        System.out.println("Collection size: " + clusteredCollection.size());
    }
}