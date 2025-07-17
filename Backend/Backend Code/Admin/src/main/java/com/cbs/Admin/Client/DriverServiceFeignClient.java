package com.cbs.Admin.Client;

import com.cbs.Admin.AdminDTO.DriverApprovalUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Driver")
public interface DriverServiceFeignClient {
    @PutMapping("/api/v1/drivers/update-approval-status") // Assuming this is the endpoint path in Driver Service
    void updateDriverApprovalStatus(@RequestBody DriverApprovalUpdateDTO updateDTO);
}
