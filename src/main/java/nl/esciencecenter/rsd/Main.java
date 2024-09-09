package nl.esciencecenter.rsd;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
	
	public static final int startYear = 2022;
	public static final int endYear = 2024;
	public static final int startMonth = 7;
	public static final int endMonth = 9;
	
	public static final String [][] RSDs = { { "nlesc", "https://research.software" }, { "helmholtz", "https://helmholtz.software" } };
	
	public static final String usersEndpoint = "/api/v1/rpc/new_accounts_count_since_timestamp?timestmp=";
	public static final String orgsEndpoint = "/api/v1/user_count_per_home_organisation";

	public static class OrgStats implements Comparable<OrgStats> { 
		public String home_organisation;
		public int count;
				
		@Override
		public int compareTo(OrgStats o) {
			
			if (home_organisation == null) { 
				return Integer.MAX_VALUE;
			}
			
			return o.count - count;
		}				
	}
	
	private static void writeFile(String filename, String content) { 
		
		try (FileWriter w = new FileWriter(filename)) { 
			w.append(content);
		} catch (Exception e) {
			System.err.println("Failed to write file: " + filename + " " + e);
			e.printStackTrace(System.err);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] readJsonArray(String data, Class<T> clazz) {		
		return (T[]) new Gson().fromJson(data, clazz.arrayType());
	}
	
	public static String get(String uri) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(uri))
				.build();
		HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
		HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (response.statusCode() >= 300) {
			throw new RuntimeException("Error fetching data from endpoint " + uri + " with response: " + response.body());
		}
		return response.body();
	}
	
	private static void getOrgStatistics(String name, String base, int overall) {
		
		System.out.println("Retrieving ORG stats for " + name);
		
		String result = get(base + orgsEndpoint);
		
		OrgStats [] stats = readJsonArray(result, OrgStats.class);
		
		// We need to adjust the name and count of the "null" org, as they may contain duplicate accounts
		
		int sum = 0;
		int nullIndex = -1;
		
		for (int i=0;i<stats.length;i++) { 
			
			OrgStats s = stats[i];
			
			if (s.home_organisation == null) { 
				nullIndex = i;
			} else { 
				sum += s.count;
			}
		}
		
		if (nullIndex >=0 && overall > sum) { 
			stats[nullIndex].count = overall-sum;
			stats[nullIndex].home_organisation = "ORCID";
		}
		
		Arrays.sort(stats);
		
		StringBuffer output = new StringBuffer();
		
		for (int i=0;i<stats.length;i++) {
			output.append(i + " " + stats[i].home_organisation + " " + stats[i].count + "\n");
		}
	
		writeFile("org-" + name + ".data", output.toString());
	}
	
	private static int fetchUserStatistics(String base, String date) { 
		String result = get(base + usersEndpoint + date);
		return Integer.parseInt(result);
	}
		
	private static int getUserStatistics(String name, String base) { 

		System.out.println("Retrieving USER stats for " + name);
		
		String date = Integer.toString(startYear) + "-" + Integer.toString(startMonth) + "-01";
		int overall = fetchUserStatistics(base, date);
		
		int index = 0;

		StringBuffer output = new StringBuffer();

		for (int y=startYear;y<=2024;y++) {
		
			for (int m=1;m<12;m++) { 
				
				if ((y == startYear && m < startMonth) || (y == endYear && m > endMonth)) { 
					// skip
				} else { 
					date = Integer.toString(y) + "-" + Integer.toString(m) + "-01";
					int count = fetchUserStatistics(base, date);
					output.append(index++ + " " + date + " " + (overall-count) + "\n");
				}
			}
		}
		
		writeFile("users-" + name + ".data", output.toString());
		return overall;
	}
	
	public static void getStatistics(String name, String base) { 
		
		int overall = getUserStatistics(name, base);
		getOrgStatistics(name, base, overall);
	}
		
	public static void main(String[] args) {
		
		if (args.length != 2) { 
			System.err.println("Invalid arguments");
			System.exit(1);
		}
		
		getStatistics(args[0], args[1]);
	}
}
