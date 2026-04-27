package com.shopx.api_gateway.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Sameer Shaikh
 * @date 24-04-2026
 * @description
 */

@FeignClient(name = "user-service")
public interface UserFeignClient {



}
