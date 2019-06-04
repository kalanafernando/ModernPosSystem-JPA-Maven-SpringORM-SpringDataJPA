package lk.ijse.pos.dao.custom;

import lk.ijse.pos.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    //Order getTopOrdersByOrderByIdDesc() throws Exception;
    @Query(value = "SELECT o.id FROM `Order` o ORDER BY o.id DESC LIMIT 1",nativeQuery = true)
    int getLastOrderId()throws Exception;
}
