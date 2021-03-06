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

import org.vaadin.addons.sitekit.flow.AbstractFlowViewlet;
import org.vaadin.addons.sitekit.flow.Flowlet;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowlet;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomersFlowlet;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupFlowlet;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupUserMemberFlowlet;

/**
 * @author Tommi S.E. Laukkanen
 */
public final class AccountFlowViewlet extends AbstractFlowViewlet {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void addFlowlets() {
        final AccountFlowlet accountView = new AccountFlowlet();
        addFlowlet(accountView);
        final Flowlet userView = new UserAccountFlowlet();
        addFlowlet(userView);
        final Flowlet customerView = new CustomerFlowlet();
        addFlowlet(customerView);
        final Flowlet groupView = new GroupFlowlet(true);
        addFlowlet(groupView);
        final Flowlet groupUserMemberFlowletView = new GroupUserMemberFlowlet();
        addFlowlet(groupUserMemberFlowletView);
        setRootFlowlet(accountView);
    }

}
