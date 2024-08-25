package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.UI;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PositionViewLogic {

    private final PositionView view;
    private final PositionDataProvider positionDataProvider;

    public PositionViewLogic(PositionView view, PositionDataProvider positionDataProvider) {
        this.view = view;
        this.positionDataProvider = positionDataProvider;
    }

    public void viewPositionBuyOrder(PositionViewModel position) {
        view.clearSelection();

        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.BUY)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        view.showOrderForm(order);
    }

    public void viewNewBuyOrder() {
        OrderViewModel order = OrderViewModel.builder()
                .type(OrderType.BUY)
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        view.showOrderForm(order);
    }

    public void viewSellAllOrder(PositionViewModel position) {
        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.SELL)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .quantity(position.getBuyQuantity().subtract(position.getSellQuantity()))
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        view.showOrderForm(order);
    }

    public void saveOrder(OrderFormEvent.SaveOrderEvent event) {
        view.clearSelection();

        OrderViewModel order = event.getOrder();
        UUID positionId = order.getPositionId();
        boolean isNewPosition = positionId == null;
        if (isNewPosition) {
            UUID selectedPortfolioId = view.getSelectedPortfolioId();
            positionDataProvider.saveNewPosition(selectedPortfolioId, order);
        } else {
            positionDataProvider.addOrderToPosition(positionId, order);
        }

        view.showForm(false);
        view.showNotification(order.getTicker() + (isNewPosition ? " created" : " updated"));
    }

    public void closeEditor() {
        view.showForm(false);
        setFragmentParameter("");
        view.clearSelection();
    }

    /**
     * Opens the product form and clears its fields to make it ready for entering a new product if productId is null, otherwise loads the product with
     * the given productId and shows its data in the form fields so the user can edit them.
     */
    // public void enter(String productId) {
    //     if (StringUtils.isNotBlank(productId)) {
    //         if (productId.equals("new")) {
    //             newProduct();
    //         } else {
    //             // Ensure this is selected even if coming directly here from
    //             // login
    //             try {
    //                  UUID id = UUID.fromString(productId);
    //                 final Product product = findProduct(id);
    //                 view.selectRow(product);
    //             } catch (final NumberFormatException e) {
    //             }
    //         }
    //     } else {
    //         view.showForm(false);
    //     }
    // }

    /**
     * Updates the fragment without causing InventoryViewLogic navigator to change view. It actually appends the productId as a parameter to the URL.
     * The parameter is set to keep the view state the same during e.g. a refresh and to enable bookmarking of individual product selections.
     */
    private static void setFragmentParameter(String positionId) {
        String fragment = StringUtils.defaultIfEmpty(positionId, "");
        UI.getCurrent().navigate(PositionView.class, fragment);
    }
}
