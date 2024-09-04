package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdateOrderRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PortfolioViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.springframework.data.domain.Page;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class PositionDataProvider extends AbstractBackEndDataProvider<PositionViewModel, String> {

    private final PositionController positionController;
    private final PositionViewMapper positionViewMapper;

    // TODO make as supplier for filter fields ?
    private final Select<PortfolioViewModel> portfolioSelect;
    private final Checkbox isShowClosedCheckbox;

    public PositionDataProvider(PositionController positionController,
                                PositionViewMapper positionViewMapper,
                                Select<PortfolioViewModel> portfolioSelect,
                                Checkbox isShowClosedCheckbox) {
        this.positionController = positionController;
        this.positionViewMapper = positionViewMapper;
        this.portfolioSelect = portfolioSelect;
        this.isShowClosedCheckbox = isShowClosedCheckbox;
    }

    @Override
    protected Stream<PositionViewModel> fetchFromBackEnd(Query query) {
        return getPositionsResponse(query).stream()
                .map(this::toModel);
    }

    // TODO fix double request
    @Override
    protected int sizeInBackEnd(Query<PositionViewModel, String> query) {
        return (int) getPositionsResponse(query).getTotalElements();
    }

    private Page<PositionResponse> getPositionsResponse(Query query) {
        Boolean isClosedFilter = isShowClosedCheckbox.getValue() ? null : false;
        PortfolioViewModel portfolioViewModel = portfolioSelect.getValue();
        UUID portfolioId = portfolioViewModel != null ? portfolioViewModel.getId() : null;

        return positionController.getPositions(
                null,
                portfolioId,
                isClosedFilter,
                VaadinSpringDataHelpers.toSpringPageRequest(query));
    }

    public void saveNewPosition(PositionViewModel position) {
        Set<CreateOrderRequest> createOrderRequests = positionViewMapper.toCreateOrderRequests(position.getOrders());
        CreatePositionRequest createPositionRequest = positionViewMapper.toCreatePositionRequest(position, createOrderRequests);
        positionController.createPosition(createPositionRequest);

        refreshAll();
    }

    public void updatePosition(PositionViewModel position) {
        Set<UpdateOrderRequest> updateOrderRequests = positionViewMapper.toUpdateOrderRequests(position.getOrders());
        UpdatePositionRequest updatePositionRequest = positionViewMapper.toUpdatePositionRequest(position, updateOrderRequests);
        PositionResponse positionResponse = positionController.updatePosition(position.getId(), updatePositionRequest);
        PositionViewModel model = toModel(positionResponse);

        refreshItem(model);
    }

    public void addOrderToPosition(UUID positionId, OrderViewModel order) {
        CreateOrderRequest createOrderRequest = positionViewMapper.toCreateOrderRequest(order);
        PositionResponse positionResponse = positionController.addOrderToPosition(positionId, createOrderRequest);
        PositionViewModel model = toModel(positionResponse);

        refreshItem(model);
    }

    public void updateOrderPosition(UUID positionId, OrderViewModel order) {
        CreateOrderRequest createOrderRequest = positionViewMapper.toCreateOrderRequest(order);
        PositionResponse positionResponse = positionController.updatePositionOrder(positionId, order.getId(), createOrderRequest);
        PositionViewModel model = toModel(positionResponse);

        refreshItem(model);
    }

    @Override
    public UUID getId(PositionViewModel item) {
        return item.getId();
    }

    private PositionViewModel toModel(PositionResponse positionResponse) {
        Set<OrderViewModel> orders = positionViewMapper.toModels(positionResponse.getOrders(), positionResponse.getId());
        return positionViewMapper.toModel(positionResponse, orders);
    }
}
