package com.izzisoft.orders.repo;

import com.izzisoft.orders.model.MarketOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketOrderRepo extends JpaRepository<MarketOrder, Long> {
    List<MarketOrder> findAllMarketOrdersByUserEmail(String email);
}
