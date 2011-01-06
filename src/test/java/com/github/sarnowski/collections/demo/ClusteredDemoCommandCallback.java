package com.github.sarnowski.collections.demo;

/**
 * @author Tobias Sarnowski
 */
public interface ClusteredDemoCommandCallback {

    void callCommand(ClusteredDemoCommand cmd, String[] tokens);

}
