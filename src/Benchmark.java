import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int REPETITIONS = 5;
    private static final int ACCESS_OPERATIONS = 10_000;
    private static final int SEARCH_OPERATIONS = 1_000;
    private static final int CHANGE_OPERATIONS = 1_000;
    private static volatile long sink;

    private Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        String outputFile = args.length == 0
                ? "results/tables/results.csv" : args[0];
        List<ResultRow> rows = new ArrayList<ResultRow>();

        warmUp();
        runRandomAccess(rows);
        runSearch(rows);
        runInsertionAndRemoval(rows);
        runPriorityProcessing(rows);
        writeCsv(rows, Paths.get(outputFile));

        System.out.println("Assignment 2: Data Structure Analysis");
        System.out.println("All four workloads completed successfully.");
        System.out.println("Repetitions per experiment: " + REPETITIONS);
        System.out.println("Result rows written: " + rows.size());
        System.out.println("CSV file: " + outputFile);
        System.out.println("Correctness checks passed.");
    }

    private static void warmUp() {
        int[] values = randomValues(2_000, 42L);
        int[] indices = randomIndices(50_000, values.length, 42L);
        DynamicArray array = dynamicArrayOf(values);
        LinkedList list = linkedListOf(values);
        MinHeap heap = new MinHeap();
        long checksum = 0;
        for (int index : indices) {
            checksum += array.get(index);
            checksum += list.get(index);
        }
        for (int value : values) {
            array.contains(value);
            list.contains(value);
            heap.insert(value);
        }
        while (!heap.isEmpty()) {
            checksum += heap.extractMin();
        }
        sink ^= checksum;
    }

    private static void runRandomAccess(List<ResultRow> rows) {
        for (int n : SIZES) {
            int[] values = randomValues(n, 42L);
            int[] indices = randomIndices(ACCESS_OPERATIONS, n, 42L);
            long time = 0;
            long metric = 0;
            for (int repetition = 0; repetition < REPETITIONS; repetition++) {
                DynamicArray array = dynamicArrayOf(values);
                array.resetMetrics();
                long checksum = 0;
                long start = System.nanoTime();
                for (int index : indices) {
                    checksum += array.get(index);
                }
                time += System.nanoTime() - start;
                metric += array.getAccesses();
                sink ^= checksum;
            }
            rows.add(row("random_access", "DynamicArray", "get", n,
                    ACCESS_OPERATIONS, time, "element_accesses", metric, "Theta(1)"));

            time = 0;
            metric = 0;
            for (int repetition = 0; repetition < REPETITIONS; repetition++) {
                LinkedList list = linkedListOf(values);
                list.resetMetrics();
                long checksum = 0;
                long start = System.nanoTime();
                for (int index : indices) {
                    checksum += list.get(index);
                }
                time += System.nanoTime() - start;
                metric += list.getAccesses();
                sink ^= checksum;
            }
            rows.add(row("random_access", "LinkedList", "get", n,
                    ACCESS_OPERATIONS, time, "node_accesses", metric, "Theta(n) average"));
        }
    }

    private static void runSearch(List<ResultRow> rows) {
        for (int n : SIZES) {
            int[] values = randomValues(n, 42L);
            int[] queries = searchValues(values, SEARCH_OPERATIONS, 42L);
            long time = 0;
            long metric = 0;
            for (int repetition = 0; repetition < REPETITIONS; repetition++) {
                DynamicArray array = dynamicArrayOf(values);
                array.resetMetrics();
                int found = 0;
                long start = System.nanoTime();
                for (int query : queries) {
                    if (array.contains(query)) {
                        found++;
                    }
                }
                time += System.nanoTime() - start;
                metric += array.getComparisons();
                sink ^= found;
            }
            rows.add(row("search", "DynamicArray", "contains", n,
                    SEARCH_OPERATIONS, time, "comparisons", metric, "Theta(n) average"));

            time = 0;
            metric = 0;
            for (int repetition = 0; repetition < REPETITIONS; repetition++) {
                LinkedList list = linkedListOf(values);
                list.resetMetrics();
                int found = 0;
                long start = System.nanoTime();
                for (int query : queries) {
                    if (list.contains(query)) {
                        found++;
                    }
                }
                time += System.nanoTime() - start;
                metric += list.getComparisons();
                sink ^= found;
            }
            rows.add(row("search", "LinkedList", "contains", n,
                    SEARCH_OPERATIONS, time, "comparisons", metric, "Theta(n) average"));
        }
    }

    private static void runInsertionAndRemoval(List<ResultRow> rows) {
        for (int n : SIZES) {
            int[] values = randomValues(n, 42L);
            runDynamicChanges(rows, values, n, 0, "beginning", "Theta(n)");
            runLinkedChanges(rows, values, n, 0, "beginning", "Theta(1)");
            int middle = n / 2;
            runDynamicChanges(rows, values, n, middle, "middle", "Theta(n)");
            runLinkedChanges(rows, values, n, middle, "middle", "Theta(n)");
        }
    }

    private static void runDynamicChanges(List<ResultRow> rows, int[] values,
                                          int n, int index, String position,
                                          String complexity) {
        long insertTime = 0;
        long insertMetric = 0;
        long removeTime = 0;
        long removeMetric = 0;
        for (int repetition = 0; repetition < REPETITIONS; repetition++) {
            DynamicArray array = dynamicArrayOf(values);
            array.resetMetrics();
            long start = System.nanoTime();
            for (int operation = 0; operation < CHANGE_OPERATIONS; operation++) {
                array.add(index, -operation - 1);
            }
            insertTime += System.nanoTime() - start;
            insertMetric += array.getMovements();

            array.resetMetrics();
            start = System.nanoTime();
            for (int operation = 0; operation < CHANGE_OPERATIONS; operation++) {
                array.remove(index);
            }
            removeTime += System.nanoTime() - start;
            removeMetric += array.getMovements();
            verifyRestored(array, values);
        }
        rows.add(row("insertion_removal", "DynamicArray", "insert_" + position, n,
                CHANGE_OPERATIONS, insertTime, "element_movements", insertMetric, complexity));
        rows.add(row("insertion_removal", "DynamicArray", "remove_" + position, n,
                CHANGE_OPERATIONS, removeTime, "element_movements", removeMetric, complexity));
    }

    private static void runLinkedChanges(List<ResultRow> rows, int[] values,
                                         int n, int index, String position,
                                         String complexity) {
        long insertTime = 0;
        long insertMetric = 0;
        long removeTime = 0;
        long removeMetric = 0;
        for (int repetition = 0; repetition < REPETITIONS; repetition++) {
            LinkedList list = linkedListOf(values);
            list.resetMetrics();
            long start = System.nanoTime();
            for (int operation = 0; operation < CHANGE_OPERATIONS; operation++) {
                list.add(index, -operation - 1);
            }
            insertTime += System.nanoTime() - start;
            insertMetric += list.getAccesses();

            list.resetMetrics();
            start = System.nanoTime();
            for (int operation = 0; operation < CHANGE_OPERATIONS; operation++) {
                list.remove(index);
            }
            removeTime += System.nanoTime() - start;
            removeMetric += list.getAccesses();
            verifyRestored(list, values);
        }
        rows.add(row("insertion_removal", "LinkedList", "insert_" + position, n,
                CHANGE_OPERATIONS, insertTime, "node_accesses", insertMetric, complexity));
        rows.add(row("insertion_removal", "LinkedList", "remove_" + position, n,
                CHANGE_OPERATIONS, removeTime, "node_accesses", removeMetric, complexity));
    }

    private static void runPriorityProcessing(List<ResultRow> rows) {
        for (int n : SIZES) {
            int[] values = randomValues(n, 42L);
            long insertTime = 0;
            long insertComparisons = 0;
            long extractTime = 0;
            long extractComparisons = 0;
            for (int repetition = 0; repetition < REPETITIONS; repetition++) {
                MinHeap heap = new MinHeap();
                heap.resetMetrics();
                long start = System.nanoTime();
                for (int value : values) {
                    heap.insert(value);
                }
                insertTime += System.nanoTime() - start;
                insertComparisons += heap.getComparisons();
                if (!heap.isValidHeap()) {
                    throw new IllegalStateException("heap property failed after insertion");
                }

                int[] extracted = new int[n];
                heap.resetMetrics();
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    extracted[i] = heap.extractMin();
                }
                extractTime += System.nanoTime() - start;
                extractComparisons += heap.getComparisons();
                verifyNonDecreasing(extracted);
                sink ^= extracted[n - 1];
            }
            rows.add(row("priority_processing", "MinHeap", "insert", n, n,
                    insertTime, "comparisons", insertComparisons, "O(log n) each"));
            rows.add(row("priority_processing", "MinHeap", "extract_min", n, n,
                    extractTime, "comparisons", extractComparisons, "Theta(log n) each"));
        }
    }

    private static ResultRow row(String workload, String structure, String operation,
                                 int n, int m, long totalTime, String metricName,
                                 long totalMetric, String complexity) {
        return new ResultRow(workload, structure, operation, n, m,
                totalTime / REPETITIONS, totalMetric / REPETITIONS,
                metricName, complexity);
    }

    private static DynamicArray dynamicArrayOf(int[] values) {
        DynamicArray array = new DynamicArray();
        for (int value : values) {
            array.add(value);
        }
        return array;
    }

    private static LinkedList linkedListOf(int[] values) {
        LinkedList list = new LinkedList();
        for (int value : values) {
            list.add(value);
        }
        return list;
    }

    private static int[] randomValues(int size, long seed) {
        Random random = new Random(seed);
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt(Integer.MAX_VALUE);
        }
        return values;
    }

    private static int[] randomIndices(int count, int bound, long seed) {
        Random random = new Random(seed);
        int[] indices = new int[count];
        for (int i = 0; i < count; i++) {
            indices[i] = random.nextInt(bound);
        }
        return indices;
    }

    private static int[] searchValues(int[] values, int count, long seed) {
        Random random = new Random(seed);
        int[] queries = new int[count];
        for (int i = 0; i < count; i++) {
            queries[i] = i % 2 == 0
                    ? values[random.nextInt(values.length)] : -i - 1;
        }
        return queries;
    }

    private static void verifyRestored(DynamicArray array, int[] expected) {
        if (array.size() != expected.length) {
            throw new IllegalStateException("dynamic array size was not restored");
        }
        for (int i = 0; i < expected.length; i++) {
            if (array.get(i) != expected[i]) {
                throw new IllegalStateException("dynamic array content was not restored");
            }
        }
    }

    private static void verifyRestored(LinkedList list, int[] expected) {
        if (list.size() != expected.length) {
            throw new IllegalStateException("linked list size was not restored");
        }
        for (int value : expected) {
            if (list.remove(0) != value) {
                throw new IllegalStateException("linked list content was not restored");
            }
        }
    }

    private static void verifyNonDecreasing(int[] values) {
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[i - 1]) {
                throw new IllegalStateException("heap output is not sorted");
            }
        }
    }

    private static void writeCsv(List<ResultRow> rows, Path output) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("workload,structure,operation,n,m,average_time_ns,average_time_ms,metric_name,average_metric,theoretical_complexity");
            writer.newLine();
            for (ResultRow row : rows) {
                writer.write(row.toCsv());
                writer.newLine();
            }
        }
    }

    private static final class ResultRow {
        private final String workload;
        private final String structure;
        private final String operation;
        private final int n;
        private final int m;
        private final long averageTimeNs;
        private final long averageMetric;
        private final String metricName;
        private final String complexity;

        private ResultRow(String workload, String structure, String operation,
                          int n, int m, long averageTimeNs, long averageMetric,
                          String metricName, String complexity) {
            this.workload = workload;
            this.structure = structure;
            this.operation = operation;
            this.n = n;
            this.m = m;
            this.averageTimeNs = averageTimeNs;
            this.averageMetric = averageMetric;
            this.metricName = metricName;
            this.complexity = complexity;
        }

        private String toCsv() {
            return workload + "," + structure + "," + operation + "," + n + "," + m
                    + "," + averageTimeNs + ","
                    + String.format(Locale.US, "%.6f", averageTimeNs / 1_000_000.0)
                    + "," + metricName + "," + averageMetric + "," + complexity;
        }
    }
}
