package run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehiclesFactory;
import java.io.File;



public class RunBerlin {

    public static void main(String[] args) {
        String version = "2019-08-16/C-First_Full_Attempt";
        String rootPath = "C:/Users/jakob/Dropbox/Documents/Education-TUB/2019_SS/NahMob/simulation/";

        // -- C O N F I G --
        String configFileName = rootPath + "input/berlin-v5.4-1pct.config.xml";
//        String configFileName = rootPath + "Input_global/berlin-v5.4-10pct.config.xml";
        Config config = ConfigUtils.loadConfig( configFileName);

        
        // Input Files -- local
//        config.network().setInputFile("berlin-v5-network.xml.gz");
        config.network().setInputFile("network_modified.xml.gz");
        config.plans().setInputFile("plans/berlin-plans-1pct-original.xml.gz");
        config.plans().setInputPersonAttributeFile("berlin-v5-person-attributes.xml.gz");
        config.vehicles().setVehiclesFile("berlin-v5-mode-vehicle-types.xml");
        config.transit().setTransitScheduleFile("berlin-v5-transit-schedule.xml.gz");
        config.transit().setVehiclesFile("berlin-v5.4-transit-vehicles.xml.gz");

        String outputDirectory = rootPath + "/output/" + version;
        new File(outputDirectory).mkdirs();

//        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.failIfDirectoryExists);

        config.controler().setLastIteration(500);
        config.controler().setOutputDirectory(outputDirectory);
        config.controler().setWritePlansInterval(10);
        config.controler().setWriteEventsInterval(10);


        // Scoring
        config = SetupActivityParams(config);

        // Routing
        config.plansCalcRoute().setInsertingAccessEgressWalk( true );
        config.plansCalcRoute().setRoutingRandomness( 3. );
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.ride);
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.pt);
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.bike);
        config.plansCalcRoute().removeModeRoutingParams("undefined");

        // -- S C E N A R I O --
        Scenario scenario = ScenarioUtils.loadScenario( config );



        VehiclesFactory vf = scenario.getVehicles().getFactory();
        VehicleType vehType = vf.createVehicleType(Id.create(TransportMode.ride, VehicleType.class));
        vehType.setMaximumVelocity(25. / 3.6);
        scenario.getVehicles().addVehicleType(vehType);

        // -- C O N T R O L E R --
        Controler controler = new Controler( scenario );
//        controler.addOverridingModule(new SwissRailRaptorModule());

        // use the (congested) car travel time for the teleported ride mode
        controler.addOverridingModule( new AbstractModule() {
            @Override
            public void install() {
                addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
                addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
            }
        } );

        controler.run();
    }


    static Config SetupActivityParams(Config config) {
        // activities:
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            final ActivityParams params = new ActivityParams( "home_" + ii + ".0" ) ;
            params.setTypicalDuration( ii );
            config.planCalcScore().addActivityParams( params );
        }
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            final ActivityParams params = new ActivityParams( "work_" + ii + ".0" ) ;
            params.setTypicalDuration( ii );
            params.setOpeningTime(6. * 3600.);
            params.setClosingTime(20. * 3600.);
            config.planCalcScore().addActivityParams( params );
        }
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            final ActivityParams params = new ActivityParams( "leisure_" + ii + ".0" ) ;
            params.setTypicalDuration( ii );
            params.setOpeningTime(9. * 3600.);
            params.setClosingTime(27. * 3600.);
            config.planCalcScore().addActivityParams( params );
        }
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            final ActivityParams params = new ActivityParams( "shopping_" + ii + ".0" ) ;
            params.setTypicalDuration( ii );
            params.setOpeningTime(8. * 3600.);
            params.setClosingTime(20. * 3600.);
            config.planCalcScore().addActivityParams( params );
        }
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            final ActivityParams params = new ActivityParams( "other_" + ii + ".0" ) ;
            params.setTypicalDuration( ii );
            config.planCalcScore().addActivityParams( params );
        }
        {
            final ActivityParams params = new ActivityParams( "freight" ) ;
            params.setTypicalDuration( 12.*3600. );
            config.planCalcScore().addActivityParams( params );
        }

        return config ;
    }
}
