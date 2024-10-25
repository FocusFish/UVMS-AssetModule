package fish.focus.uvms.mobileterminal.model.mapper;

import fish.focus.schema.mobileterminal.source.v1.GetMobileTerminalRequest;
import fish.focus.schema.mobileterminal.types.v1.MobileTerminalDataSourceMethod;
import fish.focus.schema.mobileterminal.types.v1.MobileTerminalId;
import fish.focus.uvms.asset.model.mapper.JAXBMarshaller;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JAXBMarshallerTest {

    private final String GUID = UUID.randomUUID().toString();
    private final String TEST_COMMENT = "TEST_COMMENT";
    private final String TEST_USER = "TEST_USER";

    @Test
    public void testMarshallJaxBObjectToString() throws Exception {
        GetMobileTerminalRequest mobileTerminalRequest = createMobileTerminalRequest();
        String response = JAXBMarshaller.marshallJaxBObjectToString(mobileTerminalRequest);

        assertNotNull(response);
        assertTrue(response.contains(TEST_COMMENT));
        assertTrue(response.contains(TEST_USER));
        assertTrue(response.contains(GUID));
    }

    @Test
    public void testUnmarshallTextMessage() {
        try {
            GetMobileTerminalRequest mobileTerminalRequest = createMobileTerminalRequest();
            String response = JAXBMarshaller.marshallJaxBObjectToString(mobileTerminalRequest);

            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(response);

            GetMobileTerminalRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, GetMobileTerminalRequest.class);

            assertNotNull(request);
            assertEquals(GUID, request.getId().getGuid());
            assertEquals(TEST_USER, request.getUsername());
            assertEquals(TEST_COMMENT, request.getComment());
        } catch (Exception e) {
            fail("FAILED: " + e);
        }
    }

    private GetMobileTerminalRequest createMobileTerminalRequest() {
        GetMobileTerminalRequest request = new GetMobileTerminalRequest();
        request.setMethod(MobileTerminalDataSourceMethod.GET);
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(GUID);
        request.setId(mobileTerminalId);
        request.setComment(TEST_COMMENT);
        request.setUsername(TEST_USER);
        return request;
    }
}
