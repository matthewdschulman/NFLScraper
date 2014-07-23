
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import javax.swing.text.html.HTMLDocument.Iterator;

public class NFLScraper {
	public static void main(String[] args) {		
		try {			
			String fileName = getFileName();			
			PrintStream out = new PrintStream(new FileOutputStream(fileName));
			System.setOut(out);
			HashMap<String, LinkedList<Integer>> standingsHash = new HashMap<String, LinkedList<Integer>>();
			String[] spacesArr = getArrOfLines("http://www.nfl.com/standings");
			String pattern = "/teams/profile?team=";			
			
			for (int i = 0; i < spacesArr.length; i++) {
				if (spacesArr[i].contains(pattern)) {
					String teamName = getTeamName(spacesArr[i]);
					LinkedList<Integer> results = new LinkedList<Integer>();
					for (int j = 6; j > 0; j--) {
						if (j != 4) {
							results.push(getAttribute(spacesArr[i+j]));
						}
					}
					standingsHash.put(teamName, results);
				}
			}			
			printResults(standingsHash);					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String[] getArrOfLines(String url) {
		Document nflStandingsUrl;
		try {
			nflStandingsUrl = Jsoup.connect(url).get();
			String pageHtml = nflStandingsUrl.html();
			String[] arrOfLines = pageHtml.split("\n");
			return arrOfLines;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ERROR! Contact Schulman to see what went wrong.");
		return null;
		
	}

	private static String getFileName() {
		Calendar calendar = new GregorianCalendar();
		int year       = calendar.get(Calendar.YEAR);
		int month      = calendar.get(Calendar.MONTH) + 1; 
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		int minute     = calendar.get(Calendar.MINUTE);	
		int second     = calendar.get(Calendar.SECOND);
		String dateForFileName = month+"-"+dayOfMonth+"-"+year+"--"+hourOfDay+":"+minute+":"+second+".txt";
		return dateForFileName;
	}

	private static void printResults(
			HashMap<String, LinkedList<Integer>> standingsHash) {
		java.util.Iterator<Entry<String, LinkedList<Integer>>> iter = standingsHash.entrySet().iterator();
		System.out.println("|TEAM|WINS|LOSSES|TIES|PF|PA|");
	    while (iter.hasNext()) {
	        Map.Entry pairs = (Map.Entry)iter.next();
	        System.out.print("|"+pairs.getKey());
	        LinkedList<Integer> curResults = (LinkedList<Integer>) pairs.getValue();
	        while (!curResults.isEmpty()) {
	        	System.out.print("|"+curResults.pop());
	        }
	        System.out.println("|");	        
	        iter.remove();
	    }	
	}

	private static Integer getAttribute(String line) {		
		String template = "<td>(\\d)</td>";
		Pattern p = Pattern.compile(template);
		Matcher m = p.matcher(line);
		int attribute = -1;
		if (m.find()) {
			attribute = Integer.parseInt(m.group(1));
		}		
		return attribute;		
	}

	private static String getTeamName(String line) {
	    line = line.substring(line.indexOf("team=") + 1);
		line = line.substring(4, line.indexOf("\""));		
		return line;
	}
	
}
