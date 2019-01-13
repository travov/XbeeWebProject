package org.xbee.project.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = MyRemoteXbeeDevice.GET_ALL, query = "SELECT d FROM MyRemoteXbeeDevice d"),
        @NamedQuery(name = MyRemoteXbeeDevice.DELETE, query = "DELETE FROM MyRemoteXbeeDevice d WHERE d.id=:id"),
        @NamedQuery(name = MyRemoteXbeeDevice.GET, query = "SELECT d FROM MyRemoteXbeeDevice d WHERE d.xBee16BitAddress=:xBee16BitAddress")
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
    @NotNull
    private String xBee64BitAddress;

    @Column(name = "adr16bit")
    @NotNull
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

    public MyRemoteXbeeDevice(String nodeId, String xBee64BitAddress, String xBee16BitAddress, String role) {
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.role = role;
    }

    public MyRemoteXbeeDevice(Integer id, String nodeId, String xBee64BitAddress, String xBee16BitAddress, String role) {
        this.id = id;
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.role = role;
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

    public void setRole(String role) {
        this.role = role;
    }
}
