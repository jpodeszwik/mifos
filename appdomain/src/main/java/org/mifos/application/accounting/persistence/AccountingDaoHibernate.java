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

package org.mifos.application.accounting.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.AccountingDto;
import org.mifos.dto.domain.MisProcessingTransactionsDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.RowCount;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.DateUtils;

public class AccountingDaoHibernate extends LegacyGenericDao implements
		AccountingDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<AccountingDto> findMainAccountCashGlCodes() {

		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<AccountingDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.Cash", emptyqueryparameters,
				AccountingDto.class);
		return cashGlCodes;
	}

	public List<GlBalancesBO> findExistedGlBalacesBOs(Integer officeLevelId,
			String officeId, String glCodeValue) {
		List<GlBalancesBO> balancesBOs = null;
		Query query = createdNamedQuery("getExistedGlBalancesBO");
		query.setInteger("OFFICE_LEVEL", officeLevelId);
		query.setString("OFFICE_ID", officeId);
		query.setString("GLCODEVALUE", glCodeValue);
		balancesBOs = query.list();
		return balancesBOs;
	}

	@Override
	public List<AccountingDto> findDebitAccounts() {

		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<AccountingDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.DebitAccountGlCodes",
				emptyqueryparameters, AccountingDto.class);
		return cashGlCodes;
	}

	@Override
	public List<AccountingDto> findCreditAccounts(String glCode) {

		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("Gl_Code", glCode);
		final List<AccountingDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.CreditAccountGlCodes",
				queryparameters, AccountingDto.class);
		return cashGlCodes;
	}

	@Override
	public List<AccountingDto> findMainAccountBankGlCodes() {
		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<AccountingDto> bankGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.Bank", emptyqueryparameters,
				AccountingDto.class);
		return bankGlCodes;
	}

	@Override
	public List<AccountingDto> findAccountHeadGlCodes(String glCode) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("Gl_Code", glCode);
		final List<AccountingDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.AccountHeadGlCodes", queryparameters,
				AccountingDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public boolean savingGeneralLedgerTransaction(GlMasterBO glMasterBO) {
		boolean result = false;
		try {
			GlMasterBO o = (GlMasterBO) save(glMasterBO);
			if (o.getTransactionMasterId() > 0)
				result = true;

		} catch (Exception e) {
			throw new MifosRuntimeException(e);
		}
		return result;
	}

	@Override
	public boolean savingOpenBalancesTransaction(GlBalancesBO balancesBO) {
		boolean result = false;
		try {
			GlBalancesBO o = (GlBalancesBO) save(balancesBO);
			if (o.getGlBalancesId() > 0)
				result = true;
		} catch (Exception e) {
			throw new MifosRuntimeException(e);
		}
		return result;
	}

	@Override
	public <T extends Object> T getPersistedObject(final Class<T> clazz,
			final Serializable persistentObjectId) throws PersistenceException {
		return getPersistentObject(clazz, persistentObjectId);
	}

	@Override
	public List<GlBalancesBO> getResultantGlBalancesBO(GlBalancesBO glBalancesBO) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICEID", glBalancesBO.getOfficeId());
		queryparameters.put("OFFICELEVEL", glBalancesBO.getOfficeLevel());
		queryparameters.put("COAID", glBalancesBO.getGlCodeValue());
		final List<GlBalancesBO> glBalancesBOs = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.GetExistedOpenBalance",
				queryparameters, GlBalancesBO.class);
		return glBalancesBOs;
	}

	@Override
	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short levelId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICE_LEVEL_ID", levelId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.office", queryparameters,
				OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<OfficeGlobalDto> findCustomersWithGlobalNum(Short levelId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("CUSTOMER_LEVEL_ID", levelId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.customer", queryparameters,
				OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<FinancialYearBO> findFinancialYear() {
		Query query = createdNamedQuery("getStartDateByStatus");
		List<FinancialYearBO> list = query.list();
		return list;
	}

	@Override
	public List<AccountingDto> findGlCodes() {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		final List<AccountingDto> glCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalGlCodes", queryparameters,
				AccountingDto.class);
		return glCodes;
	}

	@Override
	public List<ViewTransactionsDto> findAccountingTransactions(Date trxnDate,
			int startRecord, int numberOfRecords) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TRANSACTION_DATE", trxnDate);
		queryparameters.put("START_RECORD", startRecord);
		queryparameters.put("NUMBER_OF_RECORDS", numberOfRecords);
		final List<ViewTransactionsDto> viewAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewTransactions", queryparameters,
				ViewTransactionsDto.class);
		return viewAccountingTransactions;
	}

	public List<RowCount> findTotalNumberOfRecords(Date trxnDate) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TRANSACTION_DATE", trxnDate);
		final List<RowCount> rowCountList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalNumberOfRecords",
				queryparameters, RowCount.class);
		return rowCountList;
	}

	@Override
	public String findLastProcessingDate(String namedQueryString) {
		String lastProcessingDate = null;
		Query query = createdNamedQuery(namedQueryString);
		query.setString("KEY", "MisProcessing");
		List<ConfigurationKeyValue> list = query.list();
		if (list.size() > 0 && !"".equals(list.get(0).getValue())) {
			ConfigurationKeyValue configurationKeyValue = list.get(0);
			lastProcessingDate = configurationKeyValue.getValue();
		}
		return lastProcessingDate;
	}

	@Override
	public String findLastProcessingDateFirstTime(String namedQueryString) {
		String lastProcessingDate = null;
		Query query = createdNamedQuery(namedQueryString);
		// query.setString("STATUS", "A");
		List<FinancialYearBO> list = query.list();
		if (list.size() > 0) {
			FinancialYearBO bo = list.get(0);
			lastProcessingDate = DateUtils.format(bo
					.getFinancialYearStartDate());
		}
		return lastProcessingDate;
	}

	@Override
	public List<MisProcessingTransactionsDto> processMisPostings(
			Date lastProcessDate) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("LAST_PROCESSING_DATE", lastProcessDate);
		final List<MisProcessingTransactionsDto> misProcessingTransactionDtosList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ProcessAccountingList",
				queryparameters, MisProcessingTransactionsDto.class);
		//

		//

		return misProcessingTransactionDtosList;
	}

	public void updateLastProcessDate(Date lastProcessDate) {
		Query q = createdNamedQuery("getConfigurationKeyValueByKey");
		q.setString("KEY", "MisProcessing");
		List<ConfigurationKeyValue> list = q.list();
		if (list.size() > 0) {
			ConfigurationKeyValue configurationKeyValue = list.get(0);
			configurationKeyValue.setValue(DateUtils.format(lastProcessDate));
			try {
				createOrUpdate(configurationKeyValue);
			} catch (PersistenceException e) {
				throw new MifosRuntimeException(e);
			}
		}
	}

}
