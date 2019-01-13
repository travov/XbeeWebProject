package org.xbee.project.model;
import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = MyRemoteXbeeDevice.GET_ALL, query = "SELECT d FROM MyRemoteXbeeDevice d"),
        @NamedQuery(name = MyRemoteXbeeDevice.DELETE, query = "DELETE FROM MyRemoteXbeeDevice d WHERE d.id=:id"),
        @NamedQuery(name = MyRemoteXbeeDevice.GET, query = "SELECT d FROM MyRemoteXbeeDevice d WHERE d.xBee64BitAddress=:xBee64BitAddress")
})
@Entity
@Table(name = "devices")
public class MyRemoteXbeeDevice extends AbstractEntity {

    public static final String GET_ALL = "MyRemoteXbeeDevice.getAll";
    public static final String DELETE = "MyRemoteXbeeDevice.delete";
    public static final String GET = "MyRemoteXbeeDevice.get";


    @Column(name = "nodeId")
    private String nodeId;

    @Column(name = "adr64bit")
    private String xBee64BitAddress;

    @Column(name = "adr16bit")
    private String xBee16BitAddress;

    @Column(name = "role")
    private String role;

    public MyRemoteXbeeDevice() {
    }

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress) {
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
    }

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress, String firmwareVersion) {
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        setRole(firmwareVersion);
    }

    public MyRemoteXbeeDevice(Integer id, String nodeId, String xBee64BitAddress, String xBee16BitAddress, String firmwareVersion) {
        this(nodeId, xBee64BitAddress, xBee16BitAddress, firmwareVersion);
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getxBee64BitAddress() {
        return xBee64BitAddress;
    }

    public String getxBee16BitAddress() {
        return xBee16BitAddress;
    }

    public String getRole() {
        return role;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setxBee64BitAddress(String xBee64BitAddress) {
        this.xBee64BitAddress = xBee64BitAddress;
    }

    public void setxBee16BitAddress(String xBee16BitAddress) {
        this.xBee16BitAddress = xBee16BitAddress;
    }

    public void setRole(String firmwareVersion) {
        switch (firmwareVersion.substring(0, 2)){
            case "21":
                role = "ZigBee Coordinator API";
                break;
            case "20":
                role = "ZigBee Coordinator AT";
                break;
            case "29":
                role = "ZigBee End Device API";
                break;
            case "28":
                role = "ZigBee End Device AT";
                break;
            case "23":
                role = "ZigBee Router API";
                break;
            case "22":
                role = "ZigBee Router AT";
                break;
        }
    }
}
