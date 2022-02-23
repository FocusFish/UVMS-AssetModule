package fish.focus.uvms.tests;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import fish.focus.uvms.rest.mobileterminal.rest.ExchangeModuleRestMock;
import fish.focus.uvms.rest.mobileterminal.rest.UserRestMock;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.UnionVMSMock;

@ArquillianSuiteDeployment
public abstract class BuildAssetServiceDeployment {

    @Deployment(name = "normal", order = 2)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "fish.focus.uvms.tests");
        testWar.addPackages(true, "fish.focus.uvms.asset");
        testWar.addPackages(true, "fish.focus.uvms.mobileterminal");
        testWar.addPackages(true, "fish.focus.uvms.rest.asset");
        testWar.addPackages(true, "fish.focus.uvms.rest.mobileterminal");

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");
        
        return testWar;
    }

    @Deployment(name = "uvms", order = 1)
    public static Archive<?> createExchangeMock(){

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "unionvms.war");
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("fish.focus.uvms.exchange:exchange-client",
                        "fish.focus.uvms.lib:usm4uvms",
                        "fish.focus.uvms.commons:uvms-commons-message")
                .withTransitivity().asFile();

        testWar.addAsLibraries(files);


        testWar.addClass(UnionVMSMock.class);
        testWar.addClass(ExchangeModuleRestMock.class);
        testWar.addClass(MovementMock.class);
        testWar.addClass(UserRestMock.class);

        return testWar;
    }
}
