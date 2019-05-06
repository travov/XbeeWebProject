package org.xbee.project.model;
import javax.persistence.*;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = MyRemoteXbeeDevice.GET_ALL, query = "SELECT d FROM MyRemoteXbeeDevice d"),
        @NamedQuery(name = MyRemoteXbeeDevice.DELETE, query = "DELETE FROM MyRemoteXbeeDevice d WHERE d.id=:id"),
        @NamedQuery(name = MyRemoteXbeeDevice.GET, query = "SELECT d FROM MyRemoteXbeeDevice d WHERE d.xBee64BitAddress=:xBee64BitAddress"),
        @NamedQuery(name = MyRemoteXbeeDevice.DELETE_ALL, query = "DELETE FROM MyRemoteXbeeDevice d"),
        @NamedQuery(name = MyRemoteXbeeDevice.GET_ALL_ACTIVE, query = "SELECT d FROM MyRemoteXbeeDevice d WHERE d.active=:active")

})
@Entity
@Table(name = "devices")
public class MyRemoteXbeeDevice extends AbstractEntity {

    public static final String GET_ALL = "MyRemoteXbeeDevice.getAll";
    public static final String DELETE = "MyRemoteXbeeDevice.delete";
    public static final String GET = "MyRemoteXbeeDevice.get";
    public static final String DELETE_ALL = "MyRemoteXbeeDevice.deleteAll";
    public static final String GET_ALL_ACTIVE = "MyRemoteXbeeDevice.getAllActive";


    @Column(name = "nodeId")
    private String nodeId;

    @Column(name = "adr64bit")
    private String xBee64BitAddress;

    @Column(name = "adr16bit")
    private String xBee16BitAddress;

    @Column(name = "role")
    private String role;

    @Column(name = "active")
    private boolean active;

    public MyRemoteXbeeDevice() {
    }

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress) {
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.active = true;
    }

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress, String firmwareVersion) {
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.active = true;
        setRole(firmwareVersion);
    }

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress, boolean active){
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRole(String firmwareVersion) {
        switch (firmwareVersion.substring(0, 2)){
            case "21":
                role = "ZigBee Coordinator API";
                break;
            case "29":
                role = "ZigBee End Device API";
                break;
            case "23":
                role = "ZigBee Router API";
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyRemoteXbeeDevice device = (MyRemoteXbeeDevice) o;
        return xBee64BitAddress.equals(device.xBee64BitAddress) &&
                xBee16BitAddress.equals(device.xBee16BitAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xBee64BitAddress, xBee16BitAddress);
    }

    @Override
    public String toString() {
        return "MyRemoteXbeeDevice{" +
                "nodeId='" + nodeId + '\'' +
                ", xBee64BitAddress='" + xBee64BitAddress + '\'' +
                ", xBee16BitAddress='" + xBee16BitAddress + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
