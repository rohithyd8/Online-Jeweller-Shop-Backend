package com.jewelleryshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jewelleryshop.modal.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
