package com.github.sarnowski.collections.demo;

/**
 * @author Tobias Sarnowski
 */
public interface ClusteredDemoCommand {

    String getCommand();

    ClusteredDemoCommandCallback getCallback();

    String getUsage();
    String getHelp();


    public static final class Def implements ClusteredDemoCommand {

        private final String command;
        private final String usage;
        private final String help;
        private final ClusteredDemoCommandCallback callback;

        public Def(String command, String usage, String help, ClusteredDemoCommandCallback callback) {
            this.command = command;
            this.usage = usage;
            this.help = help;
            this.callback = callback;
        }

        @Override
        public String getCommand() {
            return command;
        }

        @Override
        public ClusteredDemoCommandCallback getCallback() {
            return callback;
        }

        @Override
        public String getUsage() {
            return usage;
        }

        @Override
        public String getHelp() {
            return usage;
        }
    }
}
