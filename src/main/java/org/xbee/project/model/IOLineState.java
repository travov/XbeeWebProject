package org.xbee.project.model;

import com.digi.xbee.api.io.IOLine;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedQueries({
        @NamedQuery(name = IOLineState.DELETE, query = "DELETE FROM IOLineState s WHERE s.id=:id"),
        @NamedQuery(name = IOLineState.GET_BY_DEVICE_ID, query = "SELECT s FROM IOLineState s WHERE s.deviceId=:deviceId")
})
@Entity
@Table(name = "line_states")
public class IOLineState extends AbstractEntity {

    public static final String GET_BY_DEVICE_ID = "IOLineState.getByDeviceId";
    public static final String DELETE = "IOLineState.delete";

    @Column(name = "devices_id")
    private Integer deviceId;

    @Transient
    private IOLine line;

    @Column(name = "lines_id")
    private Integer linesId;

    @Column(name = "value")
    private String value;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    public IOLineState(){
    }

    public IOLineState(Integer deviceId, Integer linesId, String value, LocalDateTime dateTime){
        this.deviceId = deviceId;
        this.linesId = linesId;
        this.value = value;
        this.dateTime = dateTime;
    }

    public IOLineState(Integer id, Integer deviceId, Integer linesId, String value, LocalDateTime dateTime){
        this(deviceId, linesId, value, dateTime);
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public IOLine getLine() {
        return line;
    }

    public void setLine(IOLine line) {
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getLinesId() {
        return linesId;
    }

    public void setLinesId(Integer linesId) {
        this.linesId = linesId;
    }
}
