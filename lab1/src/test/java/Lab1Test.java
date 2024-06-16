package lab.one;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lab1Test {

  private String sortBridgeWords(String result) {
    String prefix = "The bridge words from ";
    if (result.startsWith(prefix)) {
      String[] parts = result.split(": ");
      String words = parts[1].replace("\"", "").replace(".", "");
      List<String> sortedWords = Arrays.stream(words.split(", "))
          .sorted()
          .collect(Collectors.toList());
      return parts[0] + ": " + String.join(", ", sortedWords);
    }
    return result;
  }

  @Test
  public void testQueryBridgeWords_E1() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("word1", "bridge1");
    graph.addEdge("bridge1", "word2");
    graph.addEdge("word1", "bridge2");
    graph.addEdge("bridge2", "word2");

    String result = Lab1.queryBridgeWords(graph, "word1", "word2");
    String sortedResult = sortBridgeWords(result);
    String expected = "The bridge words from \"word1\" to \"word2\" are: bridge1, bridge2";
    String sortedExpected = sortBridgeWords(expected);

    assertEquals(sortedExpected, sortedResult);
  }

  @Test
  public void testQueryBridgeWords_E2() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("word3", "word4");

    String result = Lab1.queryBridgeWords(graph, "word3", "word4");
    assertEquals("No bridge words from \"word3\" to \"word4\"!", result);
  }

  @Test
  public void testQueryBridgeWords_E3() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("word5", "other");

    String result = Lab1.queryBridgeWords(graph, "word5", "nonexistent");
    assertEquals("No \"nonexistent\" in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_E4() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("other", "word6");

    String result = Lab1.queryBridgeWords(graph, "nonexistent", "word6");
    assertEquals("No \"nonexistent\" in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_E5() {
    Lab1.Graph graph = new Lab1.Graph();

    String result = Lab1.queryBridgeWords(graph, "nonexistent1", "nonexistent2");
    assertEquals("No \"nonexistent1\" in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_I1() {
    Lab1.Graph graph = new Lab1.Graph();

    String result = Lab1.queryBridgeWords(graph, "", "");
    assertEquals("No \"\" in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_I2() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("word7", "other");

    String result = Lab1.queryBridgeWords(graph, "", "word7");
    assertEquals("No \"\" in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_I3() {
    Lab1.Graph graph = new Lab1.Graph();
    graph.addEdge("word8", "other");

    String result = Lab1.queryBridgeWords(graph, "word8", "");
    assertEquals("No \"\" in the graph!", result);
  }
}
