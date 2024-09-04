package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreatePortfolioRequest;
import org.dharbar.telegabot.controller.response.PortfolioResponse;
import org.dharbar.telegabot.facade.mapper.PortfolioFacadeMapper;
import org.dharbar.telegabot.service.portfolio.PortfolioService;
import org.dharbar.telegabot.service.portfolio.dto.PortfolioDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioFacade {
    private final PortfolioService portfolioService;

    private final PortfolioFacadeMapper portfolioFacadeMapper;

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolios() {
        return portfolioService.getPortfolios().stream()
                .map(portfolioFacadeMapper::toResponse)
                .toList();
    }

    @Transactional
    public PortfolioResponse createPortfolio(CreatePortfolioRequest request) {
        PortfolioDto dto = portfolioFacadeMapper.toDto(request);
        PortfolioDto savedPortfolio = portfolioService.create(dto);
        return portfolioFacadeMapper.toResponse(savedPortfolio);
    }

}
