 

import java.math.BigDecimal;
import java.util.Map;

import javax.xml.bind.JAXBElement;


 

/**
 * The Class ExecutionService.
 */
public class ExecutionService extends ExecutionBaseService implements IExecutionBaseService {
    protected static final String POLICY_NOT_EXECUTED = "Policy NOT Executed";

    private static final LoggerUtil LOG = LoggerUtil.getLogger(ExecutionBaseService.class.getName());
    private static final String PREFENCEID_COLLECTOR = "PreferenceId";
    private IVendorPreferenceService vendorPreferenceService;
    private IControlDataService controlDataService;
    private PolicyExecutionTranslatorService policyExecutionTranslatorService;

    @Override
    protected IPolicyExecutionComposite getDomainObjFromJaxbObj(Object aRequest, Map<String, String> aErrorMap, Map<String, String> aWarningMap) {
        TravelPolicyExecutionRQ travelPolicyExecutionRQ = (TravelPolicyExecutionRQ) aRequest;
        IPolicyExecutionComposite policyExecutionComposite = new PolicyExecutionComposite();
        policyExecutionComposite.setWorkflow(com.xxx.tn.stp.auxl.domainModel.domainInterface.ExecutionWorkflowType.fromValue(travelPolicyExecutionRQ.getExecutionRequestSummary().getWorkflow().value()));
        policyExecutionComposite.setClientCode(travelPolicyExecutionRQ.getExecutionRequestSummary().getClientCode());
        policyExecutionComposite.setDomainId(travelPolicyExecutionRQ.getExecutionRequestSummary().getDomainID());
        policyExecutionComposite.setTransactionId(travelPolicyExecutionRQ.getExecutionRequestSummary().getTransactionID());
        policyExecutionComposite.setVersion(travelPolicyExecutionRQ.getVersion());

        if (travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler() != null) {
            ITravelerProfile traveler = new TravelerProfile();
            traveler.setCompany(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getCompany());
            traveler.setCostCenter(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getCostCenter());
            traveler.setDepartment(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getDepartment());
            traveler.setDivision(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getDivision());
            traveler.setEmployeeNbr(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getEmployeeNbr());
            traveler.setProjectCode(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getProjectCode());
            traveler.setCorporateID(travelPolicyExecutionRQ.getExecutionRequestSummary().getTraveler().getCorporateID());

            policyExecutionComposite.setTravelerProfile(traveler);
        }

        ExecutionServiceHelperFactory.getExecutionServiceHelper(policyExecutionComposite).mapAssets(travelPolicyExecutionRQ, policyExecutionComposite, controlDataService);
        PolicyMapper.mapPolicyToExecute(policyExecutionComposite, travelPolicyExecutionRQ.getExecutionRequestSummary().getExecutionProfile(), aErrorMap);
        PreferenceMapper.mapPreferenceToExecute(policyExecutionComposite, travelPolicyExecutionRQ.getExecutionRequestSummary().getExecutionProfile(), aWarningMap);
        LOG.debug("POLICY_EXECUTION_COMPOSITE: " + policyExecutionComposite.toString());
        return policyExecutionComposite;
    }

    @Override
    protected Object process(IPolicyExecutionComposite aPolicyExecutionComposite, Map<String, String> errorMap, Map<String, String> aWarningMap) {
        IPolicyExecutionComposite policyExecutionComposite = aPolicyExecutionComposite;
        if (policyExecutionComposite.getPreferenceAgency() != null) {
            Integer agencyId = policyExecutionComposite.getPreferenceAgency().getPreferenceId();
            processPreferences(policyExecutionComposite, agencyId, ProfileTypes.AGY, aWarningMap);
        }
        if (policyExecutionComposite.getPreferenceCorp() != null) {
            Integer corpId = policyExecutionComposite.getPreferenceCorp().getPreferenceId();
            processPreferences(policyExecutionComposite, corpId, ProfileTypes.CRP, aWarningMap);
        }

        if (errorMap.isEmpty()) {

            policyExecutionComposite = policyExecutionTranslatorService.translatePolicy(policyExecutionComposite);

            if (policyExecutionComposite.getCollector().getPolicyName() != null && "No valid policy id found".equalsIgnoreCase(policyExecutionComposite.getCollector().getPolicyName())) {
                LOG.info("No valid PolicyID found " + policyExecutionComposite.getPolicyId());
                String[] values = new String[1];
                values[0] = policyExecutionComposite.getPolicyId();
                errorMap.put(ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getCode(), ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getParamaterizedDescription(values));
            }
        } else {
            policyExecutionComposite.getCollector().getMap().put(DefaultPolicyEvaluationCollector.POLICYNAME_KEY, "Policy NOT Executed");
            LOG.info("There are validation error in Execution Request, request cannot be processed " + errorMap.toString());
        }
        return policyExecutionComposite;

    }

    private void processPreferences(IPolicyExecutionComposite aPolicyExecutionComposite, Integer aPrefId, ProfileTypes aProfileTypes, Map<String, String> aWarningMap) {
        if (aPrefId > 0) {
            IPreferenceComposite preferenceComposite = vendorPreferenceService.readPreference(BigDecimal.valueOf(aPrefId), null);
            if (preferenceComposite == null || preferenceComposite.getVendorPreference() == null) {
                aPolicyExecutionComposite.getCollector().getMap().put(PREFENCEID_COLLECTOR, aPrefId);
                String[] values = new String[1];
                values[0] = String.valueOf(aPrefId);
                if (aProfileTypes == ProfileTypes.AGY) {
                    aWarningMap.put(ServiceErrorType.POLICY_EXECUTION_AGY_PREFERENCEID_INVALID.getCode(), ServiceErrorType.POLICY_EXECUTION_AGY_PREFERENCEID_INVALID.getParamaterizedDescription(values));
                } else {
                    aWarningMap.put(ServiceErrorType.POLICY_EXECUTION_CRP_PREFERENCEID_INVALID.getCode(), ServiceErrorType.POLICY_EXECUTION_CRP_PREFERENCEID_INVALID.getParamaterizedDescription(values));
                }
                LOG.error("Error finding preference " + aPrefId);
            } else {
                ExecutionServiceHelperFactory.getExecutionServiceHelper(aPolicyExecutionComposite).mapPreferenceLevels(aPolicyExecutionComposite, preferenceComposite, aProfileTypes, controlDataService);

            }
        }
    }

    @Override
    protected STLResponsePayload getJaxbObjFromDomainObj(Object aRequest, Map<String, String> aErrorMap, Map<String, String> aWarningMap, String version) {
        IPolicyExecutionComposite policyExecutionComposite = (IPolicyExecutionComposite) aRequest;
        LOG.entering("getJaxbObjFromDomainObj");
        TravelPolicyExecutionRS response = null;
        try {

            response = (TravelPolicyExecutionRS) getResponseObject(version);
            response.setApplicationResults(getSuccessMessage());
            response.setExecutionResponseSummary(new ExecutionResponse());
            response.getExecutionResponseSummary().setTransactionID(policyExecutionComposite.getTransactionId());
            response.getExecutionResponseSummary().setPolicyName(policyExecutionComposite.getCollector().getPolicyName());
            response.getExecutionResponseSummary().setPolicyID(policyExecutionComposite.getPolicyId());
            ExecutionServiceHelperFactory.getExecutionServiceHelper(policyExecutionComposite).mapResponseAssets(response, policyExecutionComposite, aErrorMap, aWarningMap);
            if (!aWarningMap.isEmpty() || !aErrorMap.isEmpty()) {
                setErrorWarnResponse(response, aErrorMap, aWarningMap);
            }
        } catch (Exception e) {
            LOG.error("Error in getJaxbObjFromDomainObj ", e);
            throw new ServiceException(ServiceErrorType.POLICY_EXECUTION_ERROR, e);
        }

        LOG.exiting("getJaxbObjFromDomainObj");
        return response;

    }

    private void setErrorWarnResponse(TravelPolicyExecutionRS aResponse, Map<String, String> errors, Map<String, String> warnings) {
        ApplicationResults applicationResults = new ApplicationResults();
        if (!errors.isEmpty()) {
            applicationResults.getError().add(buildApplicationResults(errors));
        }
        if (!warnings.isEmpty()) {
            applicationResults.getWarning().add(buildApplicationResults(warnings));
        }
        applicationResults.setStatus(CompletionCodes.COMPLETE);
        aResponse.setApplicationResults(applicationResults);
    }

    private ProblemInformation buildApplicationResults(Map<String, String> aResultMap) {

        ProblemInformation problem = new ProblemInformation();
        SystemSpecificResults result = new SystemSpecificResults();
        for (Map.Entry<String, String> map : aResultMap.entrySet()) {
            MessageCondition message = new MessageCondition();
            message.setCode(map.getKey());
            message.setValue(map.getValue());
            result.getMessage().add(message);
        }
        problem.getSystemSpecificResults().add(result);
        return problem;
    }

    @Override
    protected String getProcessName() {
        return ApplicationConstants.POLICY_EXECUTION;
    }

    @Override
    protected ErrorType getProcessError() {
        return ServiceErrorType.POLICY_EXECUTION_ERROR;
    }

    @Override
    protected JAXBElement<?> getJaxbElement(ObjectFactory aObjectFactory, STLResponsePayload aErrorResponse) {
        return aObjectFactory.createTravelPolicyExecutionRS((TravelPolicyExecutionRS) aErrorResponse);
    }

    @Override
    protected STLResponsePayload getResponseObject(String aVersion) {
        TravelPolicyExecutionRS response = new TravelPolicyExecutionRS();
        response.setVersion(aVersion);
        return response;
    }

    /**
     * @param aVendorPreferenceService the vendorPreferenceService to set
     */
    public void setVendorPreferenceService(IVendorPreferenceService aVendorPreferenceService) {
        vendorPreferenceService = aVendorPreferenceService;
    }

    /**
     * setControlDataService
     * @param aCcontrolDataService aCcontrolDataService
     */
    public void setControlDataService(IControlDataService aCcontrolDataService) {
        controlDataService = aCcontrolDataService;
    }

    /**
     * @param aPolicyExecutionTranslatorService the policyExecutionTranslatorService to set
     */
    public void setPolicyExecutionTranslatorService(PolicyExecutionTranslatorService aPolicyExecutionTranslatorService) {
        policyExecutionTranslatorService = aPolicyExecutionTranslatorService;
    }

}
