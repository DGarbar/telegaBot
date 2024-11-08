package org.dharbar.telegabot.view.view.alarm;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.dharbar.telegabot.view.model.AlarmViewModel;
import org.dharbar.telegabot.view.view.position.PositionDataProvider;

import java.util.Set;

public class AlarmListCustomField extends CustomField<Set<AlarmViewModel>> {

    private final VerticalLayout content = new VerticalLayout();

    private final PositionDataProvider positionDataProvider;

    public AlarmListCustomField(PositionDataProvider positionDataProvider) {
        this.positionDataProvider = positionDataProvider;
        setLabel("Alarms");
        setSizeFull();
        add(content);
    }

    @Override
    protected Set<AlarmViewModel> generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(Set<AlarmViewModel> alarms) {
        content.removeAll();
        if (alarms == null || alarms.isEmpty()) {
            setVisible(false);
            return;
        }

        setVisible(true);
        alarms.forEach(alarm -> content.add(setupAlarm(alarm)));
    }

    private HorizontalLayout setupAlarm(AlarmViewModel alarmViewModel) {
        HorizontalLayout alarmLayout = new HorizontalLayout();

        Text alarmText = new Text(alarmViewModel.getType().name());

        Button resolveButton = new Button("Resolve", VaadinIcon.MINUS.create());
        resolveButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resolveButton.addClickListener(click -> {
            positionDataProvider.deleteAlarm(alarmViewModel.getPositionId(), alarmViewModel.getId());
            getValue().removeIf(alarm -> alarm.getId().equals(alarmViewModel.getId()));
            content.remove(alarmLayout);
        });

        alarmLayout.setWidth("100%");
        alarmLayout.add(alarmText);
        alarmLayout.add(resolveButton);

        return alarmLayout;
    }
}
