package eu.europa.fisheries.uvms;

import java.io.File;

import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.CustomCodesService;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@ArquillianSuiteDeployment
public abstract class BuildAssetServiceDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.asset:deprecated-asset-message",
                         "eu.europa.ec.fisheries.uvms.asset:deprecated-asset-message-mock",
                         "eu.europa.ec.fisheries.uvms.audit:audit-model",
                         "eu.europa.ec.fisheries.uvms:uvms-config",
                         "eu.europa.ec.fisheries.uvms.config:config-model:4.0.0")
                .withoutTransitivity().asFile();
        testWar.addAsLibraries(files);
        
        testWar.addPackages(true, "eu.europa.fisheries.uvms.asset.service");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.bean");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.exception");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.dto");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.mapper");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.domain");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.arquillian");


        testWar.addPackages(true, "eu.europa.fisheries.uvms.mobileterminal.service");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.service.exception");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.service.entity");

        testWar.addClass(AssetService.class);
        testWar.addClass(AssetGroupService.class);
        testWar.addClass(CustomCodesService.class);
        testWar.addClass(TransactionalTests.class);


// MobileTerminalDeps
/*
        testWar.addPackages(true,"eu.europa.ec.fisheries.schema.mobileterminal");          // model
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.model.dto");  // model

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.exception");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.message.event");

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.bean");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.constant");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.exception");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.dto");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.search");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.message.event");

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.exchange.model");         // exchange
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.exchange.common");        // exchange
        testWar.addPackages(true,"eu.europa.ec.fisheries.schema.exchange.common.v1");   // exchange

*/


        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");


        return testWar;
    }

    /*
    @Deployment(name = "mt", order = 2)
    public static Archive<?> createDeploymentMobileTerminal() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test2.war");


        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.audit:audit-model",
                        "eu.europa.ec.fisheries.uvms:uvms-config",
                        "eu.europa.ec.fisheries.uvms.config:config-model:4.0.0")
                .withoutTransitivity().asFile();
        testWar.addAsLibraries(files);


        testWar.addPackages(true,"eu.europa.ec.fisheries.schema.mobileterminal");          // model
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.model.dto");  // model

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.bean");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.exception");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.dto");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.mapper");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.search");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.message.event");

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.exchange.model");         // exchange
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.exchange.common");        // exchange
        testWar.addPackages(true,"eu.europa.ec.fisheries.schema.exchange.common.v1");   // exchange

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");


        return testWar;
    }

    */
}
