package com.cbs.Driver.Client;

import com.cbs.Driver.dto.DriverRegistrationRequestToAdminDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ADMIN")
public interface AdminServiceFeignClient {
    @PostMapping("/api/v1/admin/receive-registration")
    void receiveDriverRegistrationForApproval(@RequestBody DriverRegistrationRequestToAdminDTO requestDTO);
}
