package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;


public class ModifyNetwork {
	private static String inputNetwork = "C:/Users/jakob/Dropbox/Documents/Education-TUB/2019_SS/NahMob/simulation/input/berlin-v5-network.xml" ;
	private static String inputLinks = "C:/Users/jakob/Dropbox/Documents/Education-TUB/2019_SS/NahMob/simulation/input/linksToPedestrianize.txt" ;
	private static String outputFile = "C:/Users/jakob/Dropbox/Documents/Education-TUB/2019_SS/NahMob/simulation/input/network_modified.xml.gz";

	public static void main(String[] args) {

		// Read Network
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(inputNetwork);

		// Read Links to Pedestrianize (along Friedrichstr between Leipzigerstr and Kochstr, as well as side streets)
		ArrayList<String> linksToPedestrianize = readLinksFile(inputLinks);

		// Change Capacity of Specified Links to 0 (applies to cars)
		for (Link i : network.getLinks().values()) {
			if (linksToPedestrianize.contains(i.getId().toString())) {
				i.setCapacity(0.);
			}
		}

		// write new network to file
		new NetworkWriter(network).write(outputFile);
	}

	public static ArrayList<String> readLinksFile(String fileName){
		Scanner s ;
		ArrayList<String> list = new ArrayList<String>();
		try {
			s = new Scanner(new File(fileName));
			while (s.hasNext()){
				list.add(s.next());
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
}

