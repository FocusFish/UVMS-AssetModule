package fish.focus.uvms.tests.asset.service.arquillian.arquillian;

import fish.focus.uvms.asset.domain.dao.CustomCodeDao;
import fish.focus.uvms.asset.domain.entity.CustomCode;
import fish.focus.uvms.asset.domain.entity.CustomCodesPK;
import fish.focus.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class CustomCodesDaoTestIT extends TransactionalTests {

    private static final String CONSTANT = "TESTcarrieractiveTEST";

    private final Random rnd = new Random();

    @Inject
    private CustomCodeDao customCodesDao;

    @Test
    @OperateOnDeployment("normal")
    public void create() {
        CustomCode recordActive = createHelper(CONSTANT, true);
        CustomCode recordInactive = createHelper(CONSTANT, false);
        CustomCode createdRecordActive = customCodesDao.create(recordActive);
        String constantActive = createdRecordActive.getPrimaryKey().getConstant();
        String valueActive = createdRecordActive.getPrimaryKey().getCode();
        CustomCode fetchedRecord = customCodesDao.get(createdRecordActive.getPrimaryKey());
        assertEquals(constantActive, fetchedRecord.getPrimaryKey().getConstant());
        assertEquals(valueActive, fetchedRecord.getPrimaryKey().getCode());
        CustomCode createdRecordInactive = customCodesDao.create(recordInactive);
        String constantInactive = createdRecordInactive.getPrimaryKey().getConstant();
        String valueInactive = createdRecordInactive.getPrimaryKey().getCode();
        CustomCode fetchedInactiveRecord = customCodesDao.get(createdRecordInactive.getPrimaryKey());
        assertEquals(constantInactive, fetchedInactiveRecord.getPrimaryKey().getConstant());
        assertEquals(valueInactive, fetchedInactiveRecord.getPrimaryKey().getCode());
        List<CustomCode> rs = customCodesDao.getAllFor(recordActive.getPrimaryKey().getConstant());
        assertEquals(2, rs.size());
        customCodesDao.delete(recordActive.getPrimaryKey());
        customCodesDao.delete(recordInactive.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() {
        CustomCode customCode = createHelper(CONSTANT, true);
        CustomCode createdRecord = customCodesDao.create(customCode);
        CustomCode rec = customCodesDao.get(createdRecord.getPrimaryKey());
        assertNotNull(rec);
        customCodesDao.delete(customCode.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void exists() {
        CustomCode customCode = createHelper(CONSTANT, true);
        CustomCode created = customCodesDao.create(customCode);
        Boolean exists = customCodesDao.exists(created.getPrimaryKey());
        Assert.assertTrue(exists);
        customCodesDao.delete(customCode.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() {
        for (int i = 0; i < 10; i++) {
            CustomCode customCode = createHelper(CONSTANT, "kod" + i, "description" + i);
            customCodesDao.create(customCode);
        }
        em.flush();
        for (int i = 0; i < 10; i++) {
            CustomCode customCode = createHelper(CONSTANT + "2", "kod" + i, "description" + i);
            customCodesDao.create(customCode);
        }
        em.flush();
        List<CustomCode> rs1 = customCodesDao.getAllFor(CONSTANT);
        List<CustomCode> rs2 = customCodesDao.getAllFor(CONSTANT + "2");
        assertEquals(10, rs1.size());
        assertEquals(10, rs2.size());

        customCodesDao.deleteAllFor(CONSTANT);
        rs1 = customCodesDao.getAllFor(CONSTANT);
        assertEquals(0, rs1.size());

        customCodesDao.deleteAllFor(CONSTANT + "2");
        rs2 = customCodesDao.getAllFor(CONSTANT + "2");
        assertEquals(0, rs2.size());
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() {
        CustomCode customCode = createHelper(CONSTANT, "kod", "description");
        CustomCode created = customCodesDao.create(customCode);
        String createdDescription = created.getDescription();

        created.setDescription("CHANGED");
        customCodesDao.update(created.getPrimaryKey(), "CHANGED");
        em.flush();

        CustomCode fetched = customCodesDao.get(created.getPrimaryKey());

        assertNotEquals(createdDescription, fetched.getDescription());
        assertEquals("CHANGED", fetched.getDescription());

        customCodesDao.deleteAllFor(CONSTANT);
    }

    @Test
    @OperateOnDeployment("normal")
    public void storeLatest() {
        CustomCode customCode = createHelper(CONSTANT, "kod", "description");
        CustomCode created = customCodesDao.replace(customCode);

        CustomCode aNewCustomCode = new CustomCode();

        aNewCustomCode.setPrimaryKey(created.getPrimaryKey());
        aNewCustomCode.setDescription("STORE_LATEST");

        customCodesDao.replace(aNewCustomCode);
        em.flush();

        CustomCode fetched = customCodesDao.get(created.getPrimaryKey());
        assertNotNull(fetched);
        customCodesDao.deleteAllFor(CONSTANT);
    }

    private CustomCode createHelper(String constant, Boolean active) {
        CustomCode customCode = new CustomCode();
        if (active) {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "1");
            customCode.setPrimaryKey(primaryKey);
            customCode.setDescription("Active");
        } else {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "0");
            customCode.setPrimaryKey(primaryKey);
            customCode.setDescription("InActive");
        }
        return customCode;
    }

    private CustomCode createHelper(String constant, String code, String descr) {
        CustomCode customCode = new CustomCode();
        CustomCodesPK primaryKey = createPrimaryKey(constant, code);
        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription(descr);
        return customCode;
    }

    private CustomCodesPK createPrimaryKey(String constant, String code) {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        Instant fromDate = Instant.now(Clock.systemUTC());
        fromDate = fromDate.minus(n, ChronoUnit.DAYS);
        Instant toDate = Instant.now(Clock.systemUTC());
        toDate = toDate.plus(duration, ChronoUnit.DAYS);
        return new CustomCodesPK(constant, code, fromDate, toDate);
    }
}
