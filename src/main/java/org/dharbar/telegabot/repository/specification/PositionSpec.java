package org.dharbar.telegabot.repository.specification;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@And({
        @Spec(path = "ticker", params = "ticker", spec = Equal.class),
        @Spec(path = "portfolioId", params = "portfolioId", spec = Equal.class),
        @Spec(path = "isClosed", params = "isClosed", spec = Equal.class),
})
public interface PositionSpec extends Specification<PositionEntity> {

    static Specification<PositionEntity> toSpec(PositionFilter filter) {
        String ticker = filter.getTicker();
        UUID portfolioId = filter.getPortfolioId();
        Boolean isClosed = filter.getIsClosed();
        return SpecificationBuilder.specification(PositionSpec.class)
                .withParam("ticker", StringUtils.defaultIfEmpty(ticker, null))
                .withParam("portfolioId", portfolioId == null ? null : portfolioId.toString())
                .withParam("isClosed", isClosed == null ? null : isClosed.toString())
                .build();
    }
}
