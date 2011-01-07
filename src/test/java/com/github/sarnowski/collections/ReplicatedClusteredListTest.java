package com.github.sarnowski.collections;

import java.util.List;

import junit.framework.Test;

import org.jgroups.ChannelException;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;

/**
 * Tests the {@link List} compatability of {@link ReplicatedClusteredList}.
 *
 * @since 1.1
 * @author Willi Schoenborn
 */
public final class ReplicatedClusteredListTest implements TestListGenerator<String> {
    
    private ClusteredList<String> list;

    /**
     * Creates and returns the test for this test. 
     *
     * @since 1.1
     * @return the test
     */
    public static Test suite() {
        return ListTestSuiteBuilder.using(new ReplicatedClusteredListTest()).
            named(ReplicatedClusteredListTest.class.getSimpleName()).
            withFeatures(
                CollectionSize.ANY,
                CollectionFeature.ALLOWS_NULL_VALUES,
                CollectionFeature.KNOWN_ORDER,
                ListFeature.GENERAL_PURPOSE,
                ListFeature.REMOVE_OPERATIONS
            ).createTestSuite();
    }
    
    @Override
    public List<String> create(Object... elements) {
        if (list != null) {
            list.getChannel().close();
            list = null;
        }
        
        try {
            list = ClusteredCollections.newReplicatedClusteredList("clusterName");
        } catch (ChannelException e) {
            throw new AssertionError(e);
        }
        
        for (Object element : elements) {
            final String string = String.class.cast(element);
            list.add(string);
        }
        
        return list;
    }
    
    @Override
    public String[] createArray(int length) {
        return new String[length];
    }
    
    @Override
    public Iterable<String> order(List<String> insertionOrder) {
        return insertionOrder;
    }
    
    @Override
    public SampleElements<String> samples() {
        return new SampleElements<String>("a", "b", "c", "d", "e");
    }
    
}
