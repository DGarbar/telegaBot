package org.dharbar.telegabot.view.positionview.portfolio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class PortfolioCreationDialog extends Dialog {

    private TextField nameField;
    private TextArea descriptionText;

    public PortfolioCreationDialog(PortfolioDataProvider portfolioDataProvider) {
        VerticalLayout addPortfolioLayout = createAddPortfolioLayout();
        setHeaderTitle("Add Portfolio");
        add(addPortfolioLayout);

        Button saveButton = new Button("Add", e -> {
            portfolioDataProvider.saveNewPortfolio(nameField.getValue(), descriptionText.getValue());

            nameField.clear();
            descriptionText.clear();
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

    private VerticalLayout createAddPortfolioLayout() {
        nameField = new TextField("Name");
        descriptionText = new TextArea("Description");

        VerticalLayout fieldLayout = new VerticalLayout(nameField, descriptionText);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "300px").set("max-width", "100%");
        return fieldLayout;
    }

}
