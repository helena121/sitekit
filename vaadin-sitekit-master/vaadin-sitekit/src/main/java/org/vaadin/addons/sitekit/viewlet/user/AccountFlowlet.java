/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.viewlet.user;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.util.OpenIdUtil;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowlet;
import org.vaadin.addons.sitekit.site.SiteFields;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupFlowlet;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Customer list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class AccountFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<Customer> entityContainer;
    /** The customer grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "account";
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void initialize() {
        final List<FieldDescriptor> fieldDefinitions = SiteFields.getFieldDescriptors(Customer.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();
        filterDefinitions.add(new FilterDescriptor("companyName", "companyName", "Company Name", new TextField(), 101, "=", String.class, ""));
        filterDefinitions.add(new FilterDescriptor("lastName", "lastName", "Last Name", new TextField(), 101, "=", String.class, ""));

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        entityContainer = new EntityContainer<Customer>(entityManager, true, false, false, Customer.class, 1000, new String[] { "companyName",
                "lastName" }, new boolean[] { false, false }, "customerId");

        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            entityContainer.addContainerProperty(fieldDefinition.getId(), fieldDefinition.getValueType(), fieldDefinition.getDefaultValue(),
                    fieldDefinition.isReadOnly(), fieldDefinition.isSortable());
        }

        final GridLayout gridLayout = new GridLayout(1, 6);
        gridLayout.setRowExpandRatio(0, 0.0f);
        gridLayout.setRowExpandRatio(1, 0.0f);
        gridLayout.setRowExpandRatio(2, 0.0f);
        gridLayout.setRowExpandRatio(3, 0.0f);
        gridLayout.setRowExpandRatio(4, 0.0f);
        gridLayout.setRowExpandRatio(5, 1.0f);

        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(4, 1f);
        setViewContent(gridLayout);

        final HorizontalLayout userAccountTitle = new HorizontalLayout();
        userAccountTitle.setMargin(new MarginInfo(false, false, false, false));
        userAccountTitle.setSpacing(true);
        final Embedded userAccountTitleIcon = new Embedded(null, getSite().getIcon("view-icon-user"));
        userAccountTitleIcon.setWidth(32, UNITS_PIXELS);
        userAccountTitleIcon.setHeight(32, UNITS_PIXELS);
        userAccountTitle.addComponent(userAccountTitleIcon);
        final Label userAccountTitleLabel = new Label("<h2>User Account</h2>", Label.CONTENT_XHTML);
        userAccountTitle.addComponent(userAccountTitleLabel);
        gridLayout.addComponent(userAccountTitle, 0, 0);

        final HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(true, false, false, false));
        titleLayout.setSpacing(true);
        final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-customer"));
        titleIcon.setWidth(32, UNITS_PIXELS);
        titleIcon.setHeight(32, UNITS_PIXELS);
        titleLayout.addComponent(titleIcon);
        final Label titleLabel = new Label("<h2>Customer Accounts</h2>", Label.CONTENT_XHTML);
        titleLayout.addComponent(titleLabel);
        gridLayout.addComponent(titleLayout, 0, 3);

        final Table table = new Table();
        table.setPageLength(10);
        entityGrid = new Grid(table, entityContainer);
        entityGrid.setFields(fieldDefinitions);
        entityGrid.setFilters(filterDefinitions);
        //entityGrid.setFixedWhereCriteria("e.owner.companyId=:companyId");

        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        table.setColumnCollapsed("company", true);
        gridLayout.addComponent(entityGrid, 0, 5);

        final Button editUserButton = new Button("Edit User Account");
        editUserButton.setIcon(getSite().getIcon("button-icon-edit"));
        gridLayout.addComponent(editUserButton, 0, 2);
        editUserButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final User entity = ((SecurityProviderSessionImpl)
                        getSite().getSecurityProvider()).getUserFromSession();
                final UserAccountFlowlet customerView = getFlow().getFlowlet(UserAccountFlowlet.class);
                customerView.edit(entity, false);
                getFlow().forward(UserAccountFlowlet.class);
            }
        });

        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company.isOpenIdLogin()) {
            final Panel openIdPanel = new Panel();
            openIdPanel.setStyleName(Reindeer.PANEL_LIGHT);
            openIdPanel.setCaption("Choose OpenID Provider:");
            gridLayout.addComponent(openIdPanel, 0, 1);
            final HorizontalLayout openIdLayout = new HorizontalLayout();
            openIdPanel.setContent(openIdLayout);
            openIdLayout.setMargin(new MarginInfo(false, false, true, false));
            openIdLayout.setSpacing(true);
            final String returnViewName = "openidlink";
            final Map<String, String> urlIconMap = OpenIdUtil.getOpenIdProviderUrlIconMap();
            for (final String url : urlIconMap.keySet()) {
                openIdLayout.addComponent(OpenIdUtil.getLoginButton(url,urlIconMap.get(url), returnViewName));
            }
        }

        final HorizontalLayout customerButtonsLayout = new HorizontalLayout();
        gridLayout.addComponent(customerButtonsLayout, 0, 4);
        customerButtonsLayout.setMargin(false);
        customerButtonsLayout.setSpacing(true);

        final Button editCustomerDetailsButton = new Button("Edit Customer Details");
        customerButtonsLayout.addComponent(editCustomerDetailsButton);
        editCustomerDetailsButton.setEnabled(false);
        editCustomerDetailsButton.setIcon(getSite().getIcon("button-icon-edit"));
        editCustomerDetailsButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final CustomerFlowlet customerView = getFlow().forward(CustomerFlowlet.class);
                customerView.edit(entity, false);
            }
        });

        final Button editCustomerMembersButton = new Button("Edit Customer Members");
        customerButtonsLayout.addComponent(editCustomerMembersButton);
        editCustomerMembersButton.setEnabled(false);
        editCustomerMembersButton.setIcon(getSite().getIcon("button-icon-edit"));
        editCustomerMembersButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final GroupFlowlet view = getFlow().forward(GroupFlowlet.class);
                view.edit(entity.getMemberGroup(), false);
            }
        });

        final Button editCustomerAdminsButton = new Button("Edit Customer Admins");
        customerButtonsLayout.addComponent(editCustomerAdminsButton);
        editCustomerAdminsButton.setEnabled(false);
        editCustomerAdminsButton.setIcon(getSite().getIcon("button-icon-edit"));
        editCustomerAdminsButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final GroupFlowlet view = getFlow().forward(GroupFlowlet.class);
                view.edit(entity.getAdminGroup(), false);
            }
        });

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final Property.ValueChangeEvent event) {
                editCustomerDetailsButton.setEnabled(table.getValue() != null);
                editCustomerMembersButton.setEnabled(table.getValue() != null);
                editCustomerAdminsButton.setEnabled(table.getValue() != null);
            }
        });

    }

    @Override
    public void enter() {

        entityContainer.removeDefaultFilters();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        final User user = ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).getUserFromSession();

        if (user != null) {
            final List<Group> groups = UserDao.getUserGroups(entityManager, user.getOwner(), user);
            Container.Filter filter = null;
            for (final Group group : groups) {
                if (filter == null) {
                    filter = new Compare.Equal("adminGroup", group);
                } else {
                    filter = new Or(filter, new Compare.Equal("adminGroup", group));
                }
            }
            if (filter != null) {
                entityContainer.addDefaultFilter(filter);
            }
        }

        entityGrid.refresh();

    }

}
