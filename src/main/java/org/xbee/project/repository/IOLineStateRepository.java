package org.xbee.project.repository;

import org.xbee.project.model.IOLineState;

import java.time.LocalDateTime;
import java.util.List;

public interface IOLineStateRepository {

    IOLineState get(Integer id);

    List<IOLineState> getByDeviceId(Integer deviceId);

    List<IOLineState> getByDeviceIdAndTime(Integer deviceId, String atCommand, LocalDateTime startDateTime, LocalDateTime endDateTime);

    IOLineState save(IOLineState state);

    Integer getLineId(String atCommand);

    String getLineName(Integer id);

    boolean delete(Integer id);
}
