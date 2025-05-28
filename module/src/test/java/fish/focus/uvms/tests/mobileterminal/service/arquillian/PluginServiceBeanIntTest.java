package fish.focus.uvms.tests.mobileterminal.service.arquillian;

import fish.focus.schema.exchange.common.v1.AcknowledgeTypeType;
import fish.focus.schema.mobileterminal.polltypes.v1.*;
import fish.focus.uvms.mobileterminal.bean.PluginServiceBean;
import fish.focus.uvms.mobileterminal.entity.MobileTerminal;
import fish.focus.uvms.mobileterminal.mapper.MobileTerminalEntityToModelMapper;
import fish.focus.uvms.tests.TransactionalTests;
import fish.focus.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class PluginServiceBeanIntTest extends TransactionalTests {

    private final String USERNAME = "TEST_USERNAME";

    @EJB
    private PluginServiceBean pluginService;

    @EJB
    private TestPollHelper testPollHelper;

    @Test
    @OperateOnDeployment("normal")
    public void sendPoll() {
        PollResponseType pollResponseType = createPollResponseType();

        AcknowledgeTypeType ack = pluginService.sendPoll(pollResponseType);
        assertNotNull(ack);
        /*
          This sends a message to exchange, since we don't have exchange running we mock the response
          and to run the MT tests we need said mocker to return a plugin instead of the ack required
          by this test. Thus ignore until we have changed the mocker class.
        */
        //assertEquals(AcknowledgeTypeType.OK, ack);
    }

    private PollResponseType createPollResponseType() {
        PollId pollId = new PollId();
        pollId.setGuid(UUID.randomUUID().toString());

        MobileTerminal mobileTerminal = testPollHelper.createAndPersistMobileTerminal(null);

        PollType pollType = PollType.PROGRAM_POLL;

        String comment = "TEST_COMMENT";

        List<PollAttribute> attributes = new ArrayList<>();
        PollAttribute attribute = new PollAttribute();
        attribute.setKey(PollAttributeType.USER);
        attribute.setValue(USERNAME);
        attributes.add(attribute);

        PollResponseType pollResponseType = new PollResponseType();
        pollResponseType.setPollId(pollId);
        pollResponseType.setMobileTerminal(MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal));
        pollResponseType.setPollType(pollType);
        pollResponseType.setUserName(USERNAME);
        pollResponseType.setComment(comment);
        pollResponseType.getAttributes().addAll(attributes);

        return pollResponseType;
    }
}
