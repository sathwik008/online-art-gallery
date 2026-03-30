package com.artgallery.onlineartgallery.repository;

import com.artgallery.onlineartgallery.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}
