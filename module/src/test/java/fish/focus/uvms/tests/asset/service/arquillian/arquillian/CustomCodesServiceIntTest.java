package fish.focus.uvms.tests.asset.service.arquillian.arquillian;

import fish.focus.uvms.asset.bean.CustomCodesServiceBean;
import fish.focus.uvms.asset.domain.entity.CustomCode;
import fish.focus.uvms.asset.domain.entity.CustomCodesPK;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.transaction.*;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class CustomCodesServiceIntTest extends TransactionalTests {

    private static final String CONSTANT = "testconstant";
    private static final String CODE = "testcode";
    private final Random rnd = new Random();

    @EJB
    private CustomCodesServiceBean service;

    @Test
    @OperateOnDeployment("normal")
    public void create() {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        CustomCode fetchedCustomCode = service.get(CONSTANT, CODE, fromDate, toDate);
        assertNotNull(fetchedCustomCode);
        service.delete(CONSTANT, CODE, fromDate, toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        assertThrows(Throwable.class, () -> service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void exists() {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        Boolean exist = service.exists(CONSTANT, CODE, fromDate, toDate);
        assertNotNull(exist);
        service.delete(CONSTANT, CODE, fromDate, toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT, CODE + iStr, fromDate, toDate, CODE + "Description");
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT + "2", CODE + iStr, fromDate, toDate, CODE + "Description");
        }

        List<CustomCode> rs1 = service.getAllFor(CONSTANT);
        List<CustomCode> rs2 = service.getAllFor(CONSTANT + "2");
        assertEquals(10, rs1.size());
        assertEquals(10, rs2.size());

        userTransaction.commit();
        userTransaction.begin();

        service.deleteAllFor(CONSTANT + "2");

        userTransaction.commit();
        userTransaction.begin();

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        assertEquals(10, rs1.size());
        assertEquals(0, rs2.size());

        service.deleteAllFor(CONSTANT);
        userTransaction.commit();
        userTransaction.begin();

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        assertEquals(0, rs1.size());
        assertEquals(0, rs2.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        CustomCode createdCustomCode = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        String createdDescription = createdCustomCode.getDescription();

        createdCustomCode.setDescription("CHANGED");
        service.update(createdCustomCode.getPrimaryKey().getConstant(), createdCustomCode.getPrimaryKey().getCode(), fromDate, toDate, "CHANGED");
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetchedCustomCode = service.get(createdCustomCode.getPrimaryKey().getConstant(), createdCustomCode.getPrimaryKey().getCode(), fromDate, toDate);

        assertNotEquals(createdDescription, fetchedCustomCode.getDescription());
        assertEquals("CHANGED", fetchedCustomCode.getDescription());

        service.deleteAllFor(CONSTANT);
        userTransaction.commit();
        userTransaction.begin();
    }

    private CustomCodesPK createPrimaryKey() {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);

        Instant fromDate = Instant.now(Clock.systemUTC()).minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC()).plus(duration, ChronoUnit.DAYS);

        CustomCodesPK primaryKey = new CustomCodesPK();
        primaryKey.setConstant("TEST_constant_TEST");
        primaryKey.setCode("TEST_code_TEST");
        primaryKey.setValidFromDate(fromDate);
        primaryKey.setValidToDate(toDate);

        return primaryKey;
    }

    @Test
    @OperateOnDeployment("normal")
    public void storeLatest() {
        CustomCode customCode = new CustomCode();
        CustomCodesPK primaryKey = createPrimaryKey();

        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription("TEST_DESCRIPTION_TEST");
        service.replace(customCode);

        CustomCode aSecondCustomCode = new CustomCode();
        aSecondCustomCode.setPrimaryKey(primaryKey);
        aSecondCustomCode.setDescription("TEST_DESCRIPTION_TEST_SECONF");
        CustomCode createdASecond = service.replace(aSecondCustomCode);

        CustomCode fetchedCustomCode = service.get(primaryKey);

        assertEquals(createdASecond, fetchedCustomCode);

        service.deleteAllFor("TEST_constant_TEST");
    }
}
