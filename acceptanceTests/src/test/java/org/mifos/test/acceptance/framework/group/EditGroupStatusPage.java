/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;


public class EditGroupStatusPage extends MifosPage {
    public EditGroupStatusPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("CustomerChangeStatus");
    }

    public EditGroupStatusConfirmationPage submitAndNavigateToEditStatusConfirmationPage(EditGroupStatusParameters params) {
        selenium.check("name=newStatusId value=" + params.getStatus().getId());
        if(params.getStatus().equals(GroupStatus.CANCELLED) || params.getStatus().equals(GroupStatus.CLOSED)){
            selenium.select("customerchangeStatus.input.cancel_reason", "value="+params.getCancelReason().getId());
        }
        selenium.type("customerchangeStatus.input.notes", params.getNote());
        selenium.click("customerchangeStatus.button.preview");
        waitForPageToLoad();
        return new EditGroupStatusConfirmationPage(selenium);
    }
}