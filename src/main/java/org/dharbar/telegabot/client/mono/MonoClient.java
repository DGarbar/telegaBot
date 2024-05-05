package org.dharbar.telegabot.client.mono;

import org.dharbar.telegabot.client.mono.config.MonoFeignConfig;
import org.dharbar.telegabot.client.mono.response.TransactionMonoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "mono-client", url = "${api.mono.url}", configuration = MonoFeignConfig.class)
public interface MonoClient {

    @GetMapping(value = "/personal/statement/0/{from}/{to}")
    List<TransactionMonoResponse> getTransactions(@PathVariable long from,
                                                  @PathVariable long to);
}
