package lk.ijse.pos.dao.custom;

import lk.ijse.pos.entity.CustomEntity;

import java.util.List;

public interface QueryDAO {

    List<CustomEntity> getOrdersTotal() throws Exception;

}
