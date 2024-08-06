package org.dharbar.telegabot.view;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.context.annotation.Configuration;

@Configuration
@Theme(variant = Lumo.DARK)
public class VaadinConfiguration implements AppShellConfigurator {
}
