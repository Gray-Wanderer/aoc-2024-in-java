package andrei.mishunin.aoc2024.day16;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class ReindeerMaze {
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static Result getBestScoreAndBestSpots(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] maze = MatrixUtils.toMatrix(input);
        int n = maze.length;
        int m = maze.length;
        Node start = new Node(n - 2, 1, 0, 1, null);

        Node[][][] visited = new Node[n][m][4];
        visited[start.j][start.j][start.direction] = start;

        PriorityQueue<Node> nextMovements = new PriorityQueue<>(Comparator.comparingInt(a -> a.score));
        nextMovements.add(start);
        int bestScore = 100_000;
        while (!nextMovements.isEmpty()) {
            Node current = nextMovements.poll();
            if (maze[current.i][current.j] == 'E') {
                bestScore = current.score;
            }

            int nextI = current.i + DIRECTIONS[current.direction][0];
            int nextJ = current.j + DIRECTIONS[current.direction][1];
            if (maze[nextI][nextJ] != '#') {
                Node next = new Node(nextI, nextJ, current.score + 1, current.direction, current);
                if (bestScore >= next.score) {
                    if (visited[next.i][next.j][next.direction] == null) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    } else if (visited[next.i][next.j][next.direction].score == next.score) {
                        visited[next.i][next.j][next.direction].prev.add(current);
                    } else if (visited[next.i][next.j][next.direction].score > next.score) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    }
                }
            }

            int nextDirection = current.direction + 1;
            if (nextDirection == 4) {
                nextDirection = 0;
            }
            nextI = current.i + DIRECTIONS[nextDirection][0];
            nextJ = current.j + DIRECTIONS[nextDirection][1];
            if (maze[nextI][nextJ] != '#') {
                Node next = new Node(current.i, current.j, current.score + 1000, nextDirection, current);
                if (bestScore >= next.score) {
                    if (visited[next.i][next.j][next.direction] == null) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    } else if (visited[next.i][next.j][next.direction].score == next.score) {
                        visited[next.i][next.j][next.direction].prev.add(current);
                    } else if (visited[next.i][next.j][next.direction].score > next.score) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    }
                }
            }

            nextDirection = (current.direction - 1);
            if (nextDirection == -1) {
                nextDirection = 3;
            }
            nextI = current.i + DIRECTIONS[nextDirection][0];
            nextJ = current.j + DIRECTIONS[nextDirection][1];
            if (maze[nextI][nextJ] != '#') {
                Node next = new Node(current.i, current.j, current.score + 1000, nextDirection, current);
                if (bestScore >= next.score) {
                    if (visited[next.i][next.j][next.direction] == null) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    } else if (visited[next.i][next.j][next.direction].score == next.score) {
                        visited[next.i][next.j][next.direction].prev.add(current);
                    } else if (visited[next.i][next.j][next.direction].score > next.score) {
                        visited[next.i][next.j][next.direction] = next;
                        nextMovements.add(next);
                    }
                }
            }
        }

        Deque<Node> backtrackQueue = new LinkedList<>();
        for (Node finalNode : visited[1][m - 2]) {
            if (finalNode != null) {
                backtrackQueue.add(finalNode);
            }
        }

        boolean[][] bestSpots = new boolean[n][m];
        bestSpots[1][m - 2] = true;
        bestSpots[n - 2][1] = true;
        while (!backtrackQueue.isEmpty()) {
            Node current = backtrackQueue.poll();
            bestSpots[current.i][current.j] = true;
            backtrackQueue.addAll(current.prev);
        }

        int bestCount = 0;
        for (boolean[] bestRow : bestSpots) {
            for (boolean bestSpot : bestRow) {
                if (bestSpot) {
                    bestCount++;
                }
            }
        }

        return new Result(bestScore, bestCount);
    }

    public static void main(String[] args) {
        Result testResult = getBestScoreAndBestSpots("day16/test.txt");
        Result inputResult = getBestScoreAndBestSpots("day16/input.txt");

        System.out.println("== TEST 1 ==");
        System.out.println(testResult.bestScore);
        System.out.println("== SOLUTION 1 ==");
        System.out.println(inputResult.bestScore);
        System.out.println("== TEST 2 ==");
        System.out.println(testResult.bestSpotCount);
        System.out.println("== SOLUTION 2 ==");
        System.out.println(inputResult.bestSpotCount);
    }

    private static class Node {
        int i;
        int j;
        int score;
        int direction;
        List<Node> prev = new LinkedList<>();

        public Node(int i, int j, int score, int direction, Node prev) {
            this.i = i;
            this.j = j;
            this.score = score;
            this.direction = direction;
            if (prev != null) {
                this.prev.add(prev);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return i == node.i && j == node.j && direction == node.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j, direction);
        }
    }

    public record Result(int bestScore, int bestSpotCount) {
    }
}
