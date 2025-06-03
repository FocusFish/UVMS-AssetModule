/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.mobileterminal.polltypes.v1.*;
import fish.focus.schema.mobileterminal.types.v1.ListPagination;
import fish.focus.uvms.asset.domain.dao.AssetDao;
import fish.focus.uvms.asset.domain.entity.Asset;
import fish.focus.uvms.mobileterminal.bean.PollServiceBean;
import fish.focus.uvms.mobileterminal.dao.PollProgramDaoBean;
import fish.focus.uvms.mobileterminal.entity.ProgramPoll;
import fish.focus.uvms.mobileterminal.timer.PollTimerTask;
import fish.focus.uvms.tests.TransactionalTests;
import fish.focus.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class PollTimerTaskTest extends TransactionalTests {

    @Inject
    private TestPollHelper pollHelper;

    @Inject
    private AssetDao assetDao;

    @Inject
    private PollServiceBean pollService;

    @Inject
    private PollProgramDaoBean pollDao;

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskPollShouldBeCreated() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), null);
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();

        List<PollResponseType> polls = getAllPolls(asset.getId());
        long autoPollCount = polls.stream().filter(p -> p.getPollType().equals(PollType.AUTOMATIC_POLL)).count();

        assertEquals(1, autoPollCount);
    }

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskRecentLastRunNoPollCreated() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now().plus(1, ChronoUnit.MINUTES));
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();

        List<PollResponseType> polls = getAllPolls(asset.getId());
        long autoPollCount = polls.stream().filter(p -> p.getPollType().equals(PollType.AUTOMATIC_POLL)).count();

        assertEquals(0, autoPollCount);
    }

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskTwoPollsShouldBeCreated() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), null);
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();
        pollProgram.setLatestRun(Instant.now().minus(1, ChronoUnit.MINUTES)); // Frequency is 1s
        pollDao.updateProgramPoll(pollProgram);
        new PollTimerTask(pollService).run();

        List<PollResponseType> polls = getAllPolls(asset.getId());
        long autoPollCount = polls.stream().filter(p -> p.getPollType().equals(PollType.AUTOMATIC_POLL)).count();
        assertEquals(2, autoPollCount);
    }

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskFutureDateShouldNotCreatePoll() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().plus(1, ChronoUnit.HOURS), Instant.now().plus(2, ChronoUnit.HOURS), null);
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();

        List<PollResponseType> polls = getAllPolls(asset.getId());
        long autoPollCount = polls.stream().filter(p -> p.getPollType().equals(PollType.AUTOMATIC_POLL)).count();

        assertEquals(0, autoPollCount);
    }

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskLastRunShouldBeSet() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), null);
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();

        ProgramPoll fetchedPollProgram = pollDao.getProgramPollByGuid(pollProgram.getId().toString());
        assertThat(fetchedPollProgram.getLatestRun(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskOldProgramShouldBeArchived() {
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        ProgramPoll pollProgram = pollHelper.createProgramPoll(asset.getId().toString(),
                Instant.now().minus(2, ChronoUnit.HOURS), Instant.now().minus(1, ChronoUnit.HOURS), null);
        pollDao.createProgramPoll(pollProgram);

        new PollTimerTask(pollService).run();

        ProgramPoll fetchedPollProgram = pollDao.getProgramPollByGuid(pollProgram.getId().toString());
        assertThat(fetchedPollProgram.getPollState(), CoreMatchers.is(ProgramPollStatus.ARCHIVED));
    }

    private List<PollResponseType> getAllPolls(UUID connectId) {
        PollListQuery query = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(1000);
        pagination.setPage(1);
        query.setPagination(pagination);

        PollSearchCriteria criteria = new PollSearchCriteria();
        criteria.setIsDynamic(true);

        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.CONNECT_ID);
        listCriteria.setValue(connectId.toString());
        criteria.getCriterias().add(listCriteria);

        query.setPollSearchCriteria(criteria);
        PollListResponse pollList = pollService.getPollList(query);

        return pollList.getPollList();
    }
}
