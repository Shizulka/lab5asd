import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

public class Lab5 extends JFrame {
    private static final int n1 = 4, n2 = 2, n3 = 1, n4 = 9;
    private static final int n = 11;
    private static final int seed = Integer.parseInt(n1 + "" + n2 + "" + n3 + "" + n4);
    private static final double k = 1.0 - 0.01 * n3 - 0.005 * n4 - 0.15;

    private final int[][] adjMatrix = new int[n][n];
    private final int[][] treeMatrix = new int[n][n];
    private final List<Integer> traversalOrder = new ArrayList<>();
    private final Point[] vertexPositions = new Point[n];
    private boolean[] visited = new boolean[n];
    private final List<Edge> treeEdges = new ArrayList<>();
    private final Stack<Integer> stack = new Stack<>();
    private final Queue<Integer> queue = new LinkedList<>();
    private int mode = 0;
    private final GraphPanel panel = new GraphPanel();

    private Integer currentVertex = null;

    public Lab5() {
        setTitle("Lab 5 - Graph Traversal (BFS / DFS)");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        generateGraph();
        printAdjacencyMatrix("Матриця сумiжностi напрямленого графа:", adjMatrix);
        calculateVertexPositions();

        JPanel buttonPanel = new JPanel();
        JButton dfsButton = new JButton("DFS");
        JButton bfsButton = new JButton("BFS");
        JButton stepButton = new JButton("Step");
        JButton resetButton = new JButton("Reset");

        dfsButton.addActionListener(e -> startTraversal(1));
        bfsButton.addActionListener(e -> startTraversal(2));
        stepButton.addActionListener(e -> performStep());
        resetButton.addActionListener(e -> resetTraversal());

        buttonPanel.add(dfsButton);
        buttonPanel.add(bfsButton);
        buttonPanel.add(stepButton);
        buttonPanel.add(resetButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateGraph() {
        Random rand = new Random(seed);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adjMatrix[i][j] = rand.nextDouble() * 2.0 * k >= 1.0 ? 1 : 0;
    }

    private void printAdjacencyMatrix(String title, int[][] matrix) {
        System.out.println(title);
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    private void calculateVertexPositions() {
        int centerX = 400;
        int centerY = 300;
        int width = 300;
        int height = 200;

        vertexPositions[0] = new Point(centerX - width / 2, centerY - height / 2);
        vertexPositions[1] = new Point(centerX - width / 6, centerY - height / 2);
        vertexPositions[2] = new Point(centerX + width / 6, centerY - height / 2);
        vertexPositions[3] = new Point(centerX + width / 2, centerY - height / 2);
        vertexPositions[4] = new Point(centerX + width / 2, centerY - height / 6);
        vertexPositions[5] = new Point(centerX + width / 2, centerY + height / 2);
        vertexPositions[6] = new Point(centerX + width / 6, centerY + height / 2);
        vertexPositions[7] = new Point(centerX - width / 6, centerY + height / 2);
        vertexPositions[8] = new Point(centerX - width / 2, centerY + height / 2);
        vertexPositions[9] = new Point(centerX - width / 2, centerY + height / 10);
        vertexPositions[10] = new Point(centerX, centerY);
    }

    private int findStartVertex() {
        for (int i = 0; i < n; i++)
            if (!visited[i])
                for (int j = 0; j < n; j++)
                    if (adjMatrix[i][j] == 1)
                        return i;
        return -1;
    }

    private void startTraversal(int newMode) {
        if (mode != newMode) {
            mode = newMode;
            Arrays.fill(visited, false);
            treeEdges.clear();
            stack.clear();
            queue.clear();
            traversalOrder.clear();
            currentVertex = null;
            for (int[] row : treeMatrix)
                Arrays.fill(row, 0);
        } else if (!stack.isEmpty() || !queue.isEmpty()) {
            return;
        }

        int start = findStartVertex();
        if (start == -1) {
            currentVertex = null;
            panel.repaint();
            return;
        }

        if (mode == 1) stack.push(start);
        if (mode == 2) queue.offer(start);

        visited[start] = true;
        currentVertex = start;
        traversalOrder.add(start);
        panel.repaint();
    }

    private void performStep() {
        if (mode == 1) {
            if (!stack.isEmpty()) {
                int v = stack.pop();
                currentVertex = v;
                boolean foundUnvisited = false;
                for (int i = 0; i < n; i++) {
                    if (adjMatrix[v][i] == 1 && !visited[i]) {
                        visited[i] = true;
                        stack.push(i);
                        treeEdges.add(new Edge(v, i));
                        treeMatrix[v][i] = 1;
                        traversalOrder.add(i);
                        foundUnvisited = true;
                        break;
                    }
                }
                if (!foundUnvisited && stack.isEmpty()) {
                    int start = findStartVertex();
                    if (start != -1) {
                        stack.push(start);
                        visited[start] = true;
                        currentVertex = start;
                        traversalOrder.add(start);
                    } else {
                        currentVertex = null;
                        printAdjacencyMatrix("Матриця сумiжностi дерева обходу:", treeMatrix);
                        printTraversalOrder();
                    }
                }
            }
        } else if (mode == 2) {
            if (!queue.isEmpty()) {
                int v = queue.poll();
                currentVertex = v;
                for (int i = 0; i < n; i++) {
                    if (adjMatrix[v][i] == 1 && !visited[i]) {
                        visited[i] = true;
                        queue.offer(i);
                        treeEdges.add(new Edge(v, i));
                        treeMatrix[v][i] = 1;
                        traversalOrder.add(i);
                    }
                }
                if (queue.isEmpty()) {
                    int start = findStartVertex();
                    if (start != -1) {
                        queue.offer(start);
                        visited[start] = true;
                        currentVertex = start;
                        traversalOrder.add(start);
                    } else {
                        currentVertex = null;
                        printAdjacencyMatrix("Матриця сумiжностi дерева обходу:", treeMatrix);
                        printTraversalOrder();
                    }
                }
            }
        }
        panel.repaint();
    }

    private void printTraversalOrder() {
        System.out.println("Список вiдповiдностi старих номерiв i нових у порядку обходу:");
        for (int i = 0; i < traversalOrder.size(); i++) {
            System.out.println("Нова " + (i + 1) + " -> Стара " + (traversalOrder.get(i) + 1));
        }
    }

    private void resetTraversal() {
        mode = 0;
        Arrays.fill(visited, false);
        treeEdges.clear();
        stack.clear();
        queue.clear();
        traversalOrder.clear();
        for (int[] row : treeMatrix)
            Arrays.fill(row, 0);
        currentVertex = null;
        panel.repaint();
    }

    class GraphPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.GRAY);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (adjMatrix[i][j] == 1)
                        drawArrow(g, vertexPositions[i], vertexPositions[j]);

            g.setColor(Color.RED);
            for (Edge e : treeEdges)
                drawArrow(g, vertexPositions[e.from], vertexPositions[e.to]);

            for (int i = 0; i < n; i++) {
                Point p = vertexPositions[i];
                int size = 40;
                int x = p.x - size / 2, y = p.y - size / 2;

                if (currentVertex != null && i == currentVertex) {
                    g.setColor(Color.ORANGE);
                } else if (visited[i]) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }

                g.fillRect(x, y, size, size);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, size, size);

                FontMetrics fm = g.getFontMetrics();
                String label = String.valueOf(i + 1);
                int labelWidth = fm.stringWidth(label);
                int labelHeight = fm.getAscent();
                g.drawString(label, x + (size - labelWidth) / 2, y + (size + labelHeight) / 2 - 2);
            }
        }
    }

    private void drawArrow(Graphics g1, Point from, Point to) {
        Graphics2D g = (Graphics2D) g1.create();
        double dx = to.x - from.x, dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy) - 25;
        AffineTransform at = AffineTransform.getTranslateInstance(from.x, from.y);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[]{len, len - 6, len - 6}, new int[]{0, -6, 6}, 3);
        g.dispose();
    }

    class Edge {
        int from, to;
        Edge(int f, int t) {
            from = f;
            to = t;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab5().setVisible(true));
    }
}
