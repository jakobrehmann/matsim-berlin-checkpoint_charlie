package analysis;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import utils.MyUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DefineSubpopulation {
    public static void main(String[] args) {

        String rootPath = "C:/Users/jakob/Dropbox/Documents/Education-TUB/2019_SS/NahMob/simulation/";
        String percent = "1";

        String inputPopFilename = rootPath + "input/plans/berlin-plans-" + percent + "pct-original.xml.gz";
        String agentsSubpopFilename = rootPath + "input/agents-" + percent + "pct-Subpop.txt";
        String agentsNotSubpopFilename = rootPath + "input/agents-" + percent + "pct-NotSubpop.txt";
        String inputLinksFilename = rootPath + "input/linksToPedestrianize.txt";

        // Initialize Variables
        ArrayList<String> agentsSubpop = new ArrayList<>();
        ArrayList<String> agentsNotSubpop = new ArrayList<>();
        int agentsTotal = 0;
        int freight = 0 ;

        // Read Population
        Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new PopulationReader(sc).readFile(inputPopFilename);
        final Population pop = sc.getPopulation();

        // Read Links File
        ArrayList<String> linksToPedestrianize = utils.MyUtils.readLinksFile(inputLinksFilename);


        // Find Agents who drive on specified links
        for (Person person : pop.getPersons().values()) {
            agentsTotal++;
            if (person.getId().toString().contains("freight")) {
                freight ++ ;
                continue;
            }

            agentsNotSubpop.add(person.getId().toString());

            if (checkWhetherSubpop(linksToPedestrianize, person)) {
                agentsSubpop.add(person.getId().toString());
            } else {
                agentsNotSubpop.add(person.getId().toString());
            }
        }

        System.out.println("subpop: " + agentsSubpop.size());
        System.out.println("not subpop: " + agentsNotSubpop.size());
        System.out.println("freight : " + freight);
        System.out.println("total agents: " + agentsTotal);

        MyUtils.writeIdsToFile(agentsSubpop, agentsSubpopFilename);
        MyUtils.writeIdsToFile(agentsNotSubpop, agentsNotSubpopFilename);
    }

    private static boolean checkWhetherSubpop(ArrayList<String> linksToPedestrianize, Person person) {
        Plan plan = person.getSelectedPlan();
        for (Leg leg : TripStructureUtils.getLegs(plan)) {
            String linkListString;
            if (leg.getMode().equals(TransportMode.car)) {
                linkListString = leg.getRoute().getRouteDescription();
                ArrayList<String> linkList = new ArrayList<>(Arrays.asList(linkListString.split(" ")));
                for (String i : linkList) {
                    if (linksToPedestrianize.contains(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}


