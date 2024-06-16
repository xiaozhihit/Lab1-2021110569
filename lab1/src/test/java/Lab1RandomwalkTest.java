package lab.one;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Lab1RandomwalkTest {
  private Lab1.Graph graph;

  @Before
  public void setUp() {
    graph = new Lab1.Graph();
  }

  @Test
  public void testRandomWalk_EmptyGraph() {
    List<String> result = graph.randomWalk();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRandomWalk_SingleNode() {
    graph.addEdge("A", "A");
    List<String> result = graph.randomWalk();
    assertEquals(1, result.size());
    assertEquals("A", result.get(0));
  }

  @Test
  public void testRandomWalk_MultipleNodesNoEdges() {
    graph.addEdge("A", "B"); // Add nodes A and B without creating a real edge between them
    graph.addEdge("B", "B");
    List<String> result = graph.randomWalk();
    assertEquals(1, result.size());
  }

  @Test
  public void testRandomWalk_WithCycle() {
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    List<String> result = graph.randomWalk();
    assertTrue(result.size() >= 3); // Should visit at least 3 nodes
  }

  @Test
  public void testRandomWalk_MultipleComponents() {
    graph.addEdge("A", "B");
    graph.addEdge("C", "D");

    List<String> result = graph.randomWalk();
    assertTrue(result.size() >= 1); // Should visit at least 1 node
  }
}
