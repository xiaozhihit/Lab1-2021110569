package Lab1;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.*;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class Lab1 {

    static class Graph {
        private final Map<String, Map<String, Integer>> adjList = new HashMap<>();

        public void addEdge(String source, String dest) {
            adjList.putIfAbsent(source, new HashMap<>());
            adjList.putIfAbsent(dest, new HashMap<>()); // 添加这行
            adjList.get(source).put(dest, adjList.get(source).getOrDefault(dest, 0) + 1);
        }

        public void display() {
            for (String node : adjList.keySet()) {
                System.out.print(node + " -> ");
                for (String neighbor : adjList.get(node).keySet()) {
                    System.out.print(neighbor + "(" + adjList.get(node).get(neighbor) + ") ");
                }
                System.out.println();
            }
        }

        public List<String> randomWalk() {
            List<String> nodesVisited = new ArrayList<>();
            List<String> nodes = new ArrayList<>(adjList.keySet());
            if (nodes.isEmpty()) {
                return nodesVisited;
            }

            String current = nodes.get(new Random().nextInt(nodes.size()));
            nodesVisited.add(current);

            while (true) {
                Map<String, Integer> neighbors = adjList.get(current);
                if (neighbors == null || neighbors.isEmpty()) {
                    break;
                }

                List<String> nextNodes = new ArrayList<>(neighbors.keySet());
                String next = nextNodes.get(new Random().nextInt(nextNodes.size()));
                nodesVisited.add(next);
                current = next;
            }

            return nodesVisited;
        }

        public void exportToPngFile(String filePath) {
            MutableGraph g = mutGraph("graph").setDirected(true);
            for (String node : adjList.keySet()) {
                for (Map.Entry<String, Integer> entry : adjList.get(node).entrySet()) {
                    String neighbor = entry.getKey();
                    g.add(mutNode(node).addLink(to(mutNode(neighbor)).with(Label.of(entry.getValue().toString()))));
                }
            }
            try {
                Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File(filePath));
            } catch (IOException e) {
                System.out.println("Error writing PNG file: " + e.getMessage());
            }
        }


        public Map<String, Map<String, Integer>> getAdjList() {
            return adjList;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph();

        while (true) {
            System.out.println("请选择一个选项：");
            System.out.println("1. 从文本文件读取数据并生成有向图");
            System.out.println("2. 展示生成的有向图");
            System.out.println("3. 查询桥接词（bridge words）");
            System.out.println("4. 根据桥接词生成新文本");
            System.out.println("5. 计算两个单词之间的最短路径");
            System.out.println("6. 随机游走（Random Walk）");
            System.out.println("0. 退出");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 读取换行符

            switch (choice) {
                case 1:
                    System.out.print("请输入文件路径：");
                    String inputFilePath = scanner.nextLine();
                    System.out.print("请输入PNG文件导出路径：");
                    String pngFilePath = scanner.nextLine();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                        String line;
                        String prevWord = null;

                        while ((line = reader.readLine()) != null) {
                            String[] words = line.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
                            for (String word : words) {
                                if (!word.isEmpty()) {
                                    if (prevWord != null) {
                                        graph.addEdge(prevWord, word);
                                    }
                                    prevWord = word;
                                }
                            }
                        }
                        reader.close();
                        graph.exportToPngFile(pngFilePath);
                        System.out.println("有向图生成并导出为PNG文件完毕！");
                    } catch (IOException e) {
                        System.out.println("文件读取失败：" + e.getMessage());
                    }
                    break;


                case 2:
                    graph.display();
                    break;

                case 3:
                    System.out.print("请输入第一个单词：");
                    String word1 = scanner.nextLine();
                    System.out.print("请输入第二个单词：");
                    String word2 = scanner.nextLine();
                    String bridgeWords = queryBridgeWords(graph, word1, word2);
                    System.out.println(bridgeWords);
                    break;

                case 4:
                    System.out.print("请输入文本：");
                    String inputText = scanner.nextLine();
                    String newText = generateNewText(graph, inputText);
                    System.out.println("生成的新文本： " + newText);
                    break;

                case 5:
                    System.out.print("请输入起始单词：");
                    String startWord = scanner.nextLine();
                    System.out.print("请输入目标单词：");
                    String endWord = scanner.nextLine();
                    String shortestPath = calcShortestPath(graph, startWord, endWord);
                    System.out.println(shortestPath);
                    break;

                case 6:
                    String randomWalk = randomWalk(graph);
//                    System.out.println(randomWalk);
                    List<String> nodesVisited = graph.randomWalk();
                    writeNodesToFile(nodesVisited, "D:\\IJ IDEA\\hit\\lab1\\src\\main\\resources\\random_walk.txt");
                    System.out.println("Random walk nodes written to random_walk.txt");
                    break;

                case 0:
                    System.out.println("退出程序");
                    scanner.close();
                    return;

                default:
                    System.out.println("无效选项，请重试。");
                    break;
            }
        }
    }

    static void writeNodesToFile(List<String> nodesVisited, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String node : nodesVisited) {
                writer.write(node);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    static String queryBridgeWords(Graph graph, String word1, String word2) {
        Map<String, Integer> word1Neighbors = graph.getAdjList().get(word1);
        if (word1Neighbors == null) {
            return "No \"" + word1 + "\" in the graph!";
        }
        Set<String> bridges = new HashSet<>();
        for (String neighbor : word1Neighbors.keySet()) {
            if (graph.getAdjList().get(neighbor) != null && graph.getAdjList().get(neighbor).containsKey(word2)) {
                bridges.add(neighbor);
            }
        }
        if (bridges.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridges);
    }

    static String generateNewText(Graph graph, String inputText) {
        String[] words = inputText.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String bridgeWord = getRandomBridgeWord(graph, words[i], words[i + 1]);
            if (bridgeWord != null) {
                newText.append(bridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    static String getRandomBridgeWord(Graph graph, String word1, String word2) {
        Map<String, Integer> word1Neighbors = graph.getAdjList().get(word1);
        if (word1Neighbors == null) {
            return null;
        }
        List<String> bridges = new ArrayList<>();
        for (String neighbor : word1Neighbors.keySet()) {
            if (graph.getAdjList().get(neighbor) != null && graph.getAdjList().get(neighbor).containsKey(word2)) {
                bridges.add(neighbor);
            }
        }
        if (bridges.isEmpty()) {
            return null;
        }
        return bridges.get(new Random().nextInt(bridges.size()));
    }

    static String calcShortestPath(Graph graph, String word1, String word2) {
        Map<String, Map<String, Integer>> adjList = graph.getAdjList();
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : adjList.keySet()) {
            if (node.equals(word1)) {
                distances.put(node, 0);
            } else {
                distances.put(node, Integer.MAX_VALUE);
            }
            pq.add(node);
        }

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) {
                break;
            }
            if (distances.get(current) == Integer.MAX_VALUE) {
                continue;
            }
            Map<String, Integer> neighbors = adjList.get(current);
            if (neighbors == null) {
                continue;
            }
            for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                String neighbor = neighborEntry.getKey();
                int newDist = distances.get(current) + neighborEntry.getValue();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        if (!previousNodes.containsKey(word2)) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = previousNodes.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return "Shortest path from \"" + word1 + "\" to \"" + word2 + "\": " + String.join(" -> ", path);
    }

    static String randomWalk(Graph graph) {
        List<String> nodes = new ArrayList<>(graph.getAdjList().keySet());
        if (nodes.isEmpty()) {
            return "Graph is empty!";
        }

        String current = nodes.get(new Random().nextInt(nodes.size()));
        StringBuilder walk = new StringBuilder(current);
        Set<String> visitedEdges = new HashSet<>();

        while (true) {
            Map<String, Integer> neighbors = graph.getAdjList().get(current);
            if (neighbors == null || neighbors.isEmpty()) {
                break;
            }

            List<String> nextNodes = new ArrayList<>(neighbors.keySet());
            String next = nextNodes.get(new Random().nextInt(nextNodes.size()));
            String edge = current + " -> " + next;
            if (visitedEdges.contains(edge)) {
                break;
            }

            walk.append(" -> ").append(next);
            visitedEdges.add(edge);
            current = next;
        }

        return walk.toString();
    }
}
