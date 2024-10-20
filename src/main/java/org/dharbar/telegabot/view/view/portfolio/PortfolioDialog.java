package org.dharbar.telegabot.view.view.portfolio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.dharbar.telegabot.view.model.PortfolioViewModel;

public class PortfolioDialog extends Dialog {

    private final TextField nameField = new TextField("Name");
    private final TextArea descriptionText = new TextArea("Description");
    private final Button saveButton = new Button("Save");
    private Binder<PortfolioViewModel> portfolioBinder;

    public PortfolioDialog(PortfolioDataProvider portfolioDataProvider,
                           Binder<PortfolioViewModel> portfolioBinder) {
        VerticalLayout addPortfolioLayout = createAddPortfolioLayout();
        setupBinder(portfolioBinder);

        setHeaderTitle("Portfolio");
        add(addPortfolioLayout);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            PortfolioViewModel bean = portfolioBinder.getBean();
            if (bean == null) {
                portfolioDataProvider.savePortfolio(nameField.getValue(), descriptionText.getValue());
            } else {
                portfolioDataProvider.updatePortfolio(bean);
            }
            close();
        });

        Button cancelButton = new Button("Cancel", e -> close());
        getFooter().add(saveButton);
        getFooter().add(cancelButton);
    }

    private VerticalLayout createAddPortfolioLayout() {
        VerticalLayout fieldLayout = new VerticalLayout(nameField, descriptionText);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "300px").set("max-width", "100%");
        return fieldLayout;
    }

    private void setupBinder(Binder<PortfolioViewModel> portfolioBinder) {
        this.portfolioBinder = portfolioBinder;
        portfolioBinder.forField(nameField)
                .asRequired("Name is required")
                .bind(PortfolioViewModel::getName, PortfolioViewModel::setName);
        portfolioBinder.forField(descriptionText)
                .bind(PortfolioViewModel::getDescription, PortfolioViewModel::setDescription);
    }

    @Override
    public void open() {
        PortfolioViewModel bean = portfolioBinder.getBean();
        if (bean != null) {
            setHeaderTitle("Edit Portfolio " + bean.getName());
            saveButton.setText("Update");
        } else {
            setHeaderTitle("New Portfolio");
            saveButton.setText("Save");
        }
        super.open();
    }
}
