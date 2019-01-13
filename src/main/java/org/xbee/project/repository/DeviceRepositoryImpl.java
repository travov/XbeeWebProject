package org.xbee.project.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.xbee.project.model.MyRemoteXbeeDevice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MyRemoteXbeeDevice> getAll() {
        return em.createNamedQuery(MyRemoteXbeeDevice.GET_ALL, MyRemoteXbeeDevice.class).getResultList();
    }

    @Override
    public MyRemoteXbeeDevice get(int id) {
        return em.find(MyRemoteXbeeDevice.class, id);
    }

    @Override
    public MyRemoteXbeeDevice get(String adr64bit) {
        return em.createNamedQuery(MyRemoteXbeeDevice.GET, MyRemoteXbeeDevice.class).getSingleResult();
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
    public boolean delete(int id) {
        return em.createNamedQuery(MyRemoteXbeeDevice.DELETE).setParameter("id", id).executeUpdate() != 0;
    }
}
