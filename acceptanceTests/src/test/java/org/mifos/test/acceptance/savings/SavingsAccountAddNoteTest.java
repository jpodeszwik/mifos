/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.savings;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.AccountNotesPage;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"savings", "acceptance", "ui", "no_db_unit"})
public class SavingsAccountAddNoteTest extends UiTestCaseBase {

    private SavingsAccountHelper savingsAccountHelper;
    private static final String TEST_ACCOUNT = "000100000000002";
    private static final String TEST_ACCOUNT_NOTE = "Acceptance Test note for SavingsAccountAddNoteTest";
    
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation);
        DateTime targetTime = new DateTime(2009, 9, 9, 8, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTimeWithMifosLastLoginUpdate(targetTime);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void addNoteToSavingsAccountAndVerifyRecentNotesAndAllNotes() throws Exception {
        SavingsAccountDetailPage savingsAccountDetailPage = savingsAccountHelper.addNoteToSavingsAccount(TEST_ACCOUNT, TEST_ACCOUNT_NOTE);
        savingsAccountDetailPage.verifyPage();
        AccountNotesPage accountNotesPage = savingsAccountDetailPage.navigateToAccountNotesPage();
        accountNotesPage.verifyPage();
        assertTextFoundOnPage(TEST_ACCOUNT_NOTE);
    }
}
