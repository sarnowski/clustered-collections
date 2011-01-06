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
import com.github.sarnowski.collections.Clustered;
import org.jgroups.Address;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Tobias Sarnowski
 */
public abstract class AbstractClusteredDemo implements ClusterUpdateCallback, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClusteredDemo.class);

    private final Clustered clusteredObject;

    public AbstractClusteredDemo(Clustered clusteredObject) {
        this.clusteredObject = clusteredObject;
        clusteredObject.setUpdateCallback(this);
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
                printObjects();
                continue;
            }

            if ("cluster".equals(tokens[0].toLowerCase())) {
                View view = clusteredObject.getChannel().getView();
                System.out.println("Cluster informations");
                System.out.println(" ID:  " + view.getViewId());
                System.out.println(" Local:  " + clusteredObject.getChannel().getAddress());
                System.out.println(" Creator:  " + view.getCreator());
                System.out.println(" Member:");
                for (Address member: view.getMembers()) {
                    System.out.println("   " + member);
                }
                continue;
            }

            boolean foundCommand = false;
            for (ClusteredDemoCommand cmd: getCommands()) {
                if (tokens[0].equals(cmd.getCommand())) {
                    try {
                        cmd.getCallback().callCommand(cmd, tokens);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    foundCommand = true;
                }
            }
            if (foundCommand) continue;

            if ("quit".equals(tokens[0].toLowerCase())) {
                System.exit(0);
            }

            if (!"help".equals(tokens[0].toLowerCase())) {
                System.out.println("Unknown command '" + tokens[0] + "'!");
            }
            System.out.println("Available Commands:");
            System.out.println();
            System.out.println("  show                 - prints an overview of the clustered object");
            System.out.println("  cluster              - print the members of the cluster");
            System.out.println();
            for (ClusteredDemoCommand cmd: getCommands()) {
                System.out.print("  " + cmd.getUsage());
                int n; for (n = cmd.getUsage().length(); n < 20; n++) { System.out.print(" "); }
                System.out.println(" - " + cmd.getHelp());
            }
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
        printObjects();
    }

    public abstract void printObjects();

    public abstract List<ClusteredDemoCommand> getCommands();
}