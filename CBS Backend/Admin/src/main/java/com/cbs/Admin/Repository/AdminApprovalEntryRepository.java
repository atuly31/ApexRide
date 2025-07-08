package com.cbs.Admin.Repository;

import com.cbs.Admin.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface AdminApprovalEntryRepository extends JpaRepository<Admin,Long> {
}
