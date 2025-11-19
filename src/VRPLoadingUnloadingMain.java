/**
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class VRPLoadingUnloadingMain {

	/**
	 * @param args
	 */
	public static int n;
	//private static SourceDestination [] SrcDest = null;
	//private static Map<String, String> dest_src = new HashMap<String, String>();
	//private static List<String[]> validOrderings = new ArrayList<String[]>();
	public static ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()-1);
	private static String currentDirectory;// = System.getProperty("user.dir");
	private static Queue<Query> queries = new LinkedList<Query>();
        public static final int START_WORKING_HOUR = 540;
        public static final int END_WORKING_HOUR = 1140;
        public static final int SPLIT_THR = 2;
        private static final int MAX_CLUSTER_SIZE = 3;
        public static final double SPATIAL_THRESHOLD = 0.5;
        private static boolean useExactAlgorithm = false;
        private static boolean useFoodMatchAlgorithm = false;
        private static boolean useLifoStackHeuristic = false;
        private static boolean useOrToolsBaseline = false;
        private static boolean useInsertionHeuristic = false;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		n = Integer.parseInt(br.readLine());
//		SrcDest = new SourceDestination [2*n];
//        // Fill the array with pairs of elements
//
//		for (int i = 0; i < n; i++) {
//			String src = (char)('A' + i) + "" + i;
//			String dest = (char)('a' + i) + "" + i;
//
//			SourceDestination Src = new SourceDestination(1, src);
//			SourceDestination Dest = new SourceDestination(0, dest);
//			dest_src.put(dest, src);
//			
//            SrcDest[2 * i] = Src;
//            SrcDest[2 * i + 1] = Dest;
//        }
//		
//		SourceDestinationOrderings sdOrdering = new SourceDestinationOrderings();
//		sdOrdering.run();
//		System.out.println(validOrderings.size());
//		printOrderings();

    if (args.length == 0) {
                    throw new IllegalArgumentException("Working directory argument is required.");
                }

                currentDirectory = args[0];
                // Prepare the time-dependent graph once and then process every query sequentially
                for (int i = 1; i < args.length; i++) {
                        switch (args[i].toLowerCase()) {
                        case "--exact":
                                useExactAlgorithm = true;
                                System.out.println("Running exact VRP-LU solver as requested.");
                                break;
                        case "--foodmatch":
                                useFoodMatchAlgorithm = true;
                                System.out.println("Running FoodMatch-inspired VRP-LU solver as requested.");
                                break;
                        case "--lifostack":
                                useLifoStackHeuristic = true;
                                System.out.println("Running LIFO multi-stack heuristic solver as requested.");
                                break;
                        case "--insertion":
                                useInsertionHeuristic = true;
                                System.out.println("Running greedy insertion VRP-LU heuristic as requested.");
                                break;
                        case "--ortools":
                                useOrToolsBaseline = true;
                                System.out.println("Running OR-Tools VRPTW baseline as requested.");
                                break;
                        default:
                                System.out.println("Ignoring unrecognized flag: " + args[i]);
                        }
                }
                GenerateTDGraph.driver(currentDirectory);

                create_query_bucket();
		try {
			query_processing();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}

        private static void create_query_bucket() throws IOException{
                String query_file = currentDirectory + "/" + "Query_" + Graph.get_vertex_count() +".txt";
                File fin = new File(query_file);

                try (BufferedReader br = new BufferedReader(new FileReader(fin))) {
            String line;
            Query current_query = null;
            int i=1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("D")) {
                    if (current_query != null) {
                        queries.add(current_query); // save previous block
                    }
                    current_query = new Query(i++);
                    TimeWindow depot_timewindow = new TimeWindow(START_WORKING_HOUR, END_WORKING_HOUR);
                    Node depot_node = Graph.get_node(parseIntAfterSpace(line));

                    Point depot = new Point(depot_node, depot_timewindow, "Depot");
                    current_query.setDepot(depot);
                    current_query.setTimeWindow(depot_timewindow);

                }
                else if (line.startsWith("C") && current_query != null) {
                        current_query.setCapacity(parseIntAfterSpace(line));
                }
                else if (line.startsWith("S") && current_query != null) {
                    addServiceToQuery(current_query, line);
                }
            }

            if (current_query != null) {
                queries.add(current_query); // add the last block
            }

        }

        }

        private static void query_processing() throws IOException, InterruptedException, ExecutionException{
                String outputPrefix = "Output_";
                if(useExactAlgorithm) {
                        outputPrefix = "OutputExact_";
                } else if(useFoodMatchAlgorithm) {
                        outputPrefix = "OutputFoodMatch_";
                } else if (useInsertionHeuristic) {
                        outputPrefix = "OutputInsertion_";
                }
                String output_file = currentDirectory + "/" + outputPrefix + Graph.get_vertex_count() +".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(output_file))) {

                        while(!queries.isEmpty()){
                                long start = System.currentTimeMillis();
                                List<RoutePlan> output_order = new LinkedList<RoutePlan>();
                                Query query = queries.poll();
                                if(useExactAlgorithm) {
                                        ExactAlgorithmSolver solver = new ExactAlgorithmSolver(query);
                                        output_order.addAll(solver.solve());
                                }
                                else if(useFoodMatchAlgorithm) {
                                        FoodMatchSolver solver = new FoodMatchSolver(query);
                                        output_order.addAll(solver.solve());
                                }
                                else if(useLifoStackHeuristic) {
                                        LifoStackSolver solver = new LifoStackSolver(query);
                                        output_order.addAll(solver.solve());
                                }
                                else if(useInsertionHeuristic) {
                                        InsertionHeuristicSolver solver = new InsertionHeuristicSolver(query);
                                        output_order.addAll(solver.solve());
                                }
                                else if(useOrToolsBaseline) {
                                        OrToolsVRPTWBaseline solver = new OrToolsVRPTWBaseline(query);
                                        output_order.addAll(solver.solve());
                                }
                                else {
                                        Rider rider =  new Rider(query,MAX_CLUSTER_SIZE);
                                        output_order.addAll(rider.getFinalOrders());
                                }

                                long end = System.currentTimeMillis();
                                printOutput(output_order,writer, start, end);

                        }
                }
                System.out.println("All query processing is done.");
        }

        private static void addServiceToQuery(Query current_query, String line) {
                String[] parts = line.split(" ");
                int[] endpoints = parseEndpoints(parts[1]);

                TimeWindow start = parseTimeWindow(parts[2]);
                TimeWindow end = parseTimeWindow(parts[3]);

                Point start_point = new Point(Graph.get_node(endpoints[0]), start, "Source");
                Point end_point = new Point(Graph.get_node(endpoints[1]), end, "Destination");

                int capacity = Integer.parseInt(parts[parts.length - 1]);
                Service new_service = new Service(start_point, end_point, capacity);
                int service_id = current_query.addServices(new_service);

                start_point.setServiceObject(new_service);
                end_point.setServiceObject(new_service);

                start_point.setID(service_id);
                end_point.setID(service_id);
        }

        private static void printOutput(List<? extends RoutePlan> output_orders, BufferedWriter writer, long start, long end) {
                try {
                        for(RoutePlan output_order:output_orders) {
                                List<Point> order = output_order.getOrder();
                                StringBuilder routeBuilder = new StringBuilder();
                                routeBuilder.append('[');

                                for(int i=0;i<order.size()-1;i++) {
                                        routeBuilder.append(formatPoint(order.get(i))).append(',');
                                }
                                routeBuilder.append("Depot:")
                                            .append(order.get(order.size()-1).getNode().getNodeID())
                                            .append(']')
                                            .append("\tNumber of Successful Requests:")
                                            .append(output_order.getNumberofProcessedRequests())
                                            .append("\tL-U Cost:")
                                            .append(output_order.getLUCost())
                                            .append("\tDistance:")
                                            .append(output_order.getDistance());
                                writer.write(routeBuilder.toString());
                                writer.newLine();
                        }
                        writer.write((end-start)/1000F+"\n\n");

                } catch (IOException e) {

                        e.printStackTrace();
                }

        }

        private static int[] parseEndpoints(String endpointString) {
                String[] endpoints = endpointString.split(",");
                return new int[] {Integer.parseInt(endpoints[0]), Integer.parseInt(endpoints[1])};
        }

        private static TimeWindow parseTimeWindow(String rawWindow) {
                String[] bounds = rawWindow.split(",");
                return new TimeWindow(Double.parseDouble(bounds[0]), Double.parseDouble(bounds[1]));
        }

        private static int parseIntAfterSpace(String line) {
                return Integer.parseInt(line.split(" ")[1]);
        }

        private static String formatPoint(Point point) {
                String type = point.getType();
                if("Source".equals(type)) {
                        return "S"+point.getID()+":"+point.getNode().getNodeID();
                }
                else if("Destination".equals(type)) {
                        return "D"+point.getID()+":"+point.getNode().getNodeID();
                }
                return "Depot"+":"+point.getNode().getNodeID();
        }
//	private static void printOrderings() {
//		for(String[] ordering: validOrderings) {
//			for(int i=0;i<2*n;i++)
//				System.out.print(ordering[i] + " ");
//			System.out.println();
//		}
//	}
//
//	public static SourceDestination getSrcDestElement(int i) {
//		return SrcDest[i];
//	}
//
//	public static void updateValidOrdering(String[] newOrdering) {
//		validOrderings.add(newOrdering);
//	}
//
//	public static String getSource(SourceDestination newElement) {
//		return dest_src.get(newElement.getSrcOrDest());
//	}
}

//class SourceDestinationOrderings implements Runnable{
//	private List<String> considerList = null;
//	
//	public SourceDestinationOrderings() {
//		this.considerList = new ArrayList<String>();
//	}
//	
//	public void updateConsiderList(List<String> newList) {
//		this.considerList.clear();
//		this.considerList.addAll(newList);
//	}
//	
//	@Override
//	public void run() {
//		int n = VRPLoadingUnloadingMain.n;
//		for(int i=0;i<2*n;i++) {
//			if(terminationCondition()) {
//				VRPLoadingUnloadingMain.updateValidOrdering(getOrdering());
//				return;
//			}
//			else {
//				SourceDestination newElement = VRPLoadingUnloadingMain.getSrcDestElement(i);
//				if(checkValidity(newElement)) {
//					
//					List<String> newConsiderList = new ArrayList<String>();
//					newConsiderList.addAll(considerList);
//					newConsiderList.add(newElement.getSrcOrDest());
//					
//					SourceDestinationOrderings newOrdering = new SourceDestinationOrderings();
//					newOrdering.updateConsiderList(newConsiderList);
//					//VRPLoadingUnloadingMain.pool.execute(newOrdering);
//					newOrdering.run();
//				}
//			}
//			
//		}
//	}
//
//	private String[] getOrdering() {
//		String[] ordering = new String[2*VRPLoadingUnloadingMain.n];
//		
//		for(int i=0;i<2*VRPLoadingUnloadingMain.n;i++) {
//			ordering[i] = considerList.get(i);
//		}
//		return ordering;
//	}
//
//	private boolean terminationCondition() {
//		if(considerList.size()==2*VRPLoadingUnloadingMain.n)
//			return true;
//		return false;
//	}
//
//	private boolean checkValidity(SourceDestination newElement) {
//		if(!this.considerList.contains(newElement.getSrcOrDest())) {
//			if(newElement.isSource()){
//				return true;
//			}
//			else{
//				String src = VRPLoadingUnloadingMain.getSource(newElement);
//				if(this.considerList.contains(src)) 
//					 return true;
//			}
//		}
//		return false;
//	}
//}
//
//class SourceDestination{
//	private int flag;
//	private String SrcDest;
//	
//	public SourceDestination(int flag, String s) {
//		this.flag = flag;
//		this.SrcDest = s;
//	}
//	
//	public boolean isSource() {
//		if(this.flag==1)
//			return true;
//		return false;
//	}
//	
//	public String getSrcOrDest() {
//		return this.SrcDest;
//	}
//}
