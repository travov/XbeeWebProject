package org.xbee.project.repository;

import org.xbee.project.model.IOLineState;

import java.util.List;

public interface IOLineStateRepository {

    IOLineState get(Integer id);

    List<IOLineState> getByDeviceId(Integer deviceId);

    IOLineState save(IOLineState state);

    Integer getLineId(String atCommand);

    boolean delete(Integer id);
}
