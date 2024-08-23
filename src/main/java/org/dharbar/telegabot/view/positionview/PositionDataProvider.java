package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class PositionDataProvider extends AbstractBackEndDataProvider<PositionViewModel, String> {

    private final PositionController positionController;
    private final PositionViewMapper positionViewMapper;

    private final Checkbox isOnlyOpenCheckbox;

    public PositionDataProvider(PositionController positionController, PositionViewMapper positionViewMapper, Checkbox isOnlyOpenCheckbox) {
        this.positionController = positionController;
        this.positionViewMapper = positionViewMapper;
        this.isOnlyOpenCheckbox = isOnlyOpenCheckbox;
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
        boolean isOnlyOpen = isOnlyOpenCheckbox.getValue();
        return positionController.getPositions(isOnlyOpen, VaadinSpringDataHelpers.toSpringPageRequest(query));
    }

    public void saveNewPosition(OrderViewModel order) {
        CreatePositionRequest createPositionRequest = positionViewMapper.toCreatePositionRequest(order.getTicker(), List.of(order));
        positionController.createPosition(createPositionRequest);

        refreshAll();
    }

    public void addOrderToPosition(OrderViewModel order) {
        CreateOrderRequest createOrderRequest = positionViewMapper.toCreateOrderRequest(order);
        PositionResponse positionResponse = positionController.addOrderToPosition(order.getPositionId(), createOrderRequest);
        PositionViewModel model = toModel(positionResponse);

        refreshItem(model);
    }

    @Override
    public UUID getId(PositionViewModel item) {
        return item.getId();
    }

    private PositionViewModel toModel(PositionResponse positionResponse) {
        List<OrderViewModel> orders = positionViewMapper.toModels(positionResponse.getOrders(), positionResponse.getId());
        return positionViewMapper.toModel(positionResponse, orders);
    }
}
