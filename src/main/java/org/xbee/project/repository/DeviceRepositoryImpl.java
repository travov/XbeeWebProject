package org.xbee.project.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.xbee.project.model.MyRemoteXbeeDevice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class DeviceRepositoryImpl implements DeviceRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MyRemoteXbeeDevice> getAll() {
        return em.createNamedQuery(MyRemoteXbeeDevice.GET_ALL, MyRemoteXbeeDevice.class).getResultList();
    }

    @Override
    public MyRemoteXbeeDevice get(Integer id) {
        return em.find(MyRemoteXbeeDevice.class, id);
    }

    @Override
    public MyRemoteXbeeDevice get(String adr64bit) {
        return em.createNamedQuery(MyRemoteXbeeDevice.GET, MyRemoteXbeeDevice.class).setParameter("xBee64BitAddress", adr64bit).getSingleResult();
    }

    @Override
    @Transactional
    public MyRemoteXbeeDevice save(MyRemoteXbeeDevice device) {
        if (device.isNew()){
            em.persist(device);
            return device;
        }
        else return em.merge(device);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        return em.createNamedQuery(MyRemoteXbeeDevice.DELETE).setParameter("id", id).executeUpdate() != 0;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        return em.createNamedQuery(MyRemoteXbeeDevice.DELETE_ALL).executeUpdate() != 0;
    }
}
