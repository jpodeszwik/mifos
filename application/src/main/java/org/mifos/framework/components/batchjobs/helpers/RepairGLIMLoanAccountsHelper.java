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

package org.mifos.framework.components.batchjobs.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

@SuppressWarnings("deprecation")
public class RepairGLIMLoanAccountsHelper extends TaskHelper {

    public RepairGLIMLoanAccountsHelper() {
        super();
    }


    
    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        List<String> errorList = new ArrayList<String>();

        Session session = StaticHibernateUtil.getSessionTL();
        List<LoanBO> parentLoans;
        Query query = session.getNamedQuery("accounts.findAllParentLoans");
        parentLoans = query.list();
        int counter = 0;
        
        System.out.println("Found " + parentLoans.size() + " parent loans");
        
        for(LoanBO parent : parentLoans) {
            if(parent.isAccountActive()) {
                parent.applyMifos5692fix();
            }
            counter++;
            if((counter%1000)==0)
                System.out.println("Processed "+ counter + " accounts...");
        }
        System.out.println("Processed "+ counter + " accounts...");
        System.out.println("Finished");
        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }
    
    private void applyPaymentToChildAccount(AccountPaymentEntity parentPayment, LoanBO child, BigDecimal factor)
    {
        Money memberPaymentAmount = parentPayment.getAmount().divide(factor);
        memberPaymentAmount = MoneyUtils.currencyRound(memberPaymentAmount);
        PaymentData memberPayment;
        try {
            memberPayment = new PaymentData(memberPaymentAmount, ApplicationContextProvider.getBean(LegacyPersonnelDao.class).getPersonnel(((short)1)), parentPayment.getPaymentType().getId(), parentPayment.getPaymentDate());
            if(child.getGlobalAccountNum().equals("000100000175658"))
            {
                int x = 0;
            }
            child.applyPayment(memberPayment);
            if(child.getGlobalAccountNum().equals("000100000175658"))
            {
                int y = 0;
            }
        } catch (PersistenceException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
        } catch (AccountException e) {
        //e.printStackTrace();
        int x = 0;
    }
            
        
    }
    
    private void disburseLoanOnChildAccount(AccountPaymentEntity disbursalPayment, LoanBO child, BigDecimal factor)
    {
        Money memberPaymentAmount = disbursalPayment.getAmount().divide(factor);
        memberPaymentAmount = MoneyUtils.currencyRound(memberPaymentAmount);
        PaymentData memberPayment = new PaymentData(memberPaymentAmount, disbursalPayment.getCreatedByUser(), disbursalPayment.getPaymentType().getId(), disbursalPayment.getPaymentDate());
        try {
            
            child.disburseLoan(disbursalPayment);

        } catch (PersistenceException e) {
            //e.printStackTrace();
        } catch (AccountException e) {
            //e.printStackTrace();
        }
    }

}