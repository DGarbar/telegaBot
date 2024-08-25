package org.dharbar.telegabot.view.positionview.portfolio;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.controller.PortfolioController;
import org.dharbar.telegabot.controller.request.CreatePortfolioRequest;
import org.dharbar.telegabot.controller.response.PortfolioResponse;
import org.dharbar.telegabot.view.mapper.PortfolioViewMapper;
import org.dharbar.telegabot.view.model.PortfolioViewModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class PortfolioDataProvider extends AbstractBackEndDataProvider<PortfolioViewModel, String> {

    private final PortfolioController portfolioController;
    private final PortfolioViewMapper portfolioViewMapper;

    public PortfolioDataProvider(PortfolioController portfolioController,
                                 PortfolioViewMapper portfolioViewMapper) {
        this.portfolioController = portfolioController;
        this.portfolioViewMapper = portfolioViewMapper;
    }

    @Override
    protected Stream<PortfolioViewModel> fetchFromBackEnd(Query query) {
        log.info("Get portfolios");
        return getPortfoliosResponse(query).stream()
                .map(this::toModel);
    }

    // TODO fix double request
    @Override
    protected int sizeInBackEnd(Query<PortfolioViewModel, String> query) {
        log.info("Get portfolios size");
        return getPortfoliosResponse(query).size();
    }

    private List<PortfolioResponse> getPortfoliosResponse(Query query) {
        return portfolioController.getPortfolios();
    }

    public void saveNewPortfolio(String name, String description) {
        CreatePortfolioRequest createPortfolioRequest = portfolioViewMapper.toCreatePortfolioRequest(name, description);
        portfolioController.createPortfolio(createPortfolioRequest);

        refreshAll();
    }

    @Override
    public UUID getId(PortfolioViewModel item) {
        return item.getId();
    }

    private PortfolioViewModel toModel(PortfolioResponse portfolioResponse) {
        return portfolioViewMapper.toModel(portfolioResponse);
    }
}
