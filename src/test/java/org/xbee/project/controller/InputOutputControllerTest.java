package org.xbee.project.controller;

import com.digi.xbee.api.exceptions.XBeeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xbee.project.model.MyRemoteXbeeDevice;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.xbee.project.XbeeDeviceTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-mvc.xml",
        "classpath:spring/spring-db.xml"
})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class InputOutputControllerTest {

    private static final Integer TIMEOUT = 5000;

    @Autowired
    private InputOutputController controller;

    static {
        System.setProperty("prop.location", "C:/apache-tomcat-9.0.30/XbeeWebProject-master/app.properties");
    }

    public void startDiscoveryProcess() throws XBeeException, InterruptedException {
        controller.startDiscoveryProcess(TIMEOUT);
        Thread.sleep(TIMEOUT + 500);
    }

    @Test
    public void getDiscoveredDevices() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        List<MyRemoteXbeeDevice> expected = Arrays.asList(DEVICE1);
        assertMatch(controller.getDiscoveredDevices(null), expected);
    }

    @Test
    public void setSampling() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        controller.setSampling(DEVICE1.getxBee64BitAddress(), DEVICE1_SAMPLING);
        String rateValue = controller.getParameter(DEVICE1.getxBee64BitAddress(), AT_SAMPLING).getParameters().get(AT_SAMPLING);
        assertThat(String.valueOf(Integer.parseInt(rateValue, 16))).isEqualTo(DEVICE1_SAMPLING);
    }

    @Test
    public void setChangeDetection() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        controller.setChangeDetection(DEVICE1.getxBee64BitAddress(), DEVICE1_LINES);
        Set<Integer> actualChangeDetectionLines = controller.getChangeDetection(DEVICE1.getxBee64BitAddress()).getParameters().get(AT_CHANGE_DETECTION);
        assertThat(actualChangeDetectionLines).isEqualTo(DEVICE1_LINES);
    }

    @Test
    public void getChangeDetection() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        Set<Integer> actualChangeDetectionLines = controller.getChangeDetection(DEVICE1.getxBee64BitAddress()).getParameters().get(AT_CHANGE_DETECTION);
        assertThat(actualChangeDetectionLines).isEqualTo(DEVICE1_LINES);
    }

    @Test
    public void getDioValue() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        String dioValue = controller.getDioValue(DEVICE1.getxBee64BitAddress(), 0).getParameters().get(D0);
        assertThat(dioValue).isEqualTo(LOW);

    }

    @Test
    public void setNodeIdentifier() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        controller.setNodeIdentifier(DEVICE1.getxBee64BitAddress(), DEVICE1_NEW_NI);
        String newNi = controller.getParameter(DEVICE1.getxBee64BitAddress(), NI).getParameters().get(NI);
        assertThat(newNi).isEqualTo(DEVICE1_NEW_NI);
    }

    @Test
    public void getParameter() throws XBeeException, InterruptedException {
        startDiscoveryProcess();
        String d0Value = controller.getParameter(DEVICE1.getxBee64BitAddress(), D0).getParameters().get(D0);
        assertThat(d0Value).isEqualTo("03");
    }

    @Test
    public void setParameter() throws InterruptedException, XBeeException {
        startDiscoveryProcess();
        controller.setParameter(DEVICE1.getxBee64BitAddress(), D1, "03");
        String d1Value = controller.getParameter(DEVICE1.getxBee64BitAddress(), D1).getParameters().get(D1);
        assertThat(d1Value).isEqualTo("03");
    }
}
