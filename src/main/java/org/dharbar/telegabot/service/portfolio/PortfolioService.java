package org.dharbar.telegabot.service.portfolio;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.PortfolioRepository;
import org.dharbar.telegabot.repository.entity.PortfolioEntity;
import org.dharbar.telegabot.service.portfolio.dto.PortfolioDto;
import org.dharbar.telegabot.service.portfolio.mapper.PortfolioServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final PortfolioServiceMapper mapper;

    public List<PortfolioDto> getPortfolios() {
        return StreamSupport.stream(portfolioRepository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .toList();
    }

    public PortfolioDto get(UUID id) {
        return portfolioRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow();
    }

    public PortfolioDto create(PortfolioDto portfolioDto) {
        PortfolioEntity entity = mapper.toEntity(portfolioDto);
        PortfolioEntity savedPortfolio = portfolioRepository.save(entity);
        return mapper.toDto(savedPortfolio);
    }

    @Transactional
    public PortfolioDto update(UUID id, PortfolioDto dto) {
        PortfolioEntity portfolio = portfolioRepository.findById(id).orElseThrow();
        PortfolioEntity updatedPortfolio = mapper.update(portfolio, dto);
        PortfolioEntity savedPortfolio = portfolioRepository.save(updatedPortfolio);
        return mapper.toDto(savedPortfolio);
    }
}
