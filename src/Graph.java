import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight, process-wide graph registry used by all solver implementations.
 * Nodes are stored in an adjacency list keyed by node identifier. The class also
 * exposes a time series describing allowed departure times for use in
 * time-dependent cost calculations.
 */
class Graph {
        private static int n_vertexes;
        private static final Map<Integer, Node> adjacency_list = new HashMap<Integer, Node>();
        private static double[] timeSeries;

        /**
         * @return number of vertices declared for the current graph
         */
        public static int get_vertex_count(){
                return n_vertexes;
        }

        /**
         * Update the time-series array, parsing each string value to a double.
         * The series is assumed to be sorted; consumers can leverage this to
         * avoid scanning the entire structure when filtering intervals.
         */
        public static void updateTimeSeries(String[] time_series) {
                timeSeries = new double[time_series.length];
                for(int i=0;i<time_series.length;i++) {
                        timeSeries[i] = Double.parseDouble(time_series[i]);
                }

                System.out.println("Updated time series with " + time_series.length + " entries.");

        }

        public static double[] getTimeSeries() {
                return timeSeries;
        }

        /**
         * Return all time points strictly between the provided bounds. Because the
         * underlying array is sorted, the scan can terminate once the upper bound
         * is crossed.
         */
        public static List<Double> getTimeSeries(double start_departure_time, double end_departure_time) {
                List<Double> time_series = new ArrayList<Double>();

                for (double time_point : timeSeries) {

                        if(time_point==start_departure_time || time_point== end_departure_time)
                                continue;

            if (time_point > start_departure_time && time_point< end_departure_time) {
                time_series.add(time_point);
            } else if (time_point > end_departure_time) {
                break; // No need to continue as the list is sorted
            }
        }

                return time_series;
        }

        public static void set_vertex_count(int n){
                n_vertexes = n;
                System.out.println("Vertex count set to " + n_vertexes);
        }

        public static void add_node(int node_id, Node node){
                adjacency_list.put(node_id, node);
        }

        public static int getNodeCount() {
                return adjacency_list.size();
        }

        public static Node get_node(int node_id){
                return adjacency_list.get(node_id);
        }
}
