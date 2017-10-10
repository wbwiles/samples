

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.camel.Exchange;

/**
 * The Class ExecutionBaseService.
 */
public abstract class BulkExecutionBaseService implements IExecutionBaseService {

    private static final LoggerUtil LOG = LoggerUtil.getLogger(BulkExecutionBaseService.class.getName());
    private JaxbOtaInterface jaxbOtaBinder;
    private ApplicationResults successMessage;
    private IValidator validator;
    private IPolicyAdminService pdeAdminService;
    private ITravelPolicyVersionUtil versionSupportUtil;
    private VersionUtil versionUtil;

    @Override
    public String processRequest(Exchange exchange) {

        String responseXml = null;
        String requestReleaseVersion = ApplicationConstants.DEFAULT_RELEASE_VERSION;

        IBulkReadCompositeCriteria bulkReadCompositeCriteria = null;
        try {
            StpThreadLocal.STPTHREADLOCAL.set(exchange.getIn().getHeader(ApplicationConstants.STP_MESSAGE_ID));

            Map<String, String> errorMap = new HashMap<String, String>();
            Object request = exchange.getIn().getBody();
            bulkReadCompositeCriteria = getDomainObjFromJaxbObj(request, errorMap);
            requestReleaseVersion = bulkReadCompositeCriteria.getVersion();

            validator.validateBulk(bulkReadCompositeCriteria, errorMap);

            if (errorMap.isEmpty()) {
                Object result = process(bulkReadCompositeCriteria);
                STLResponsePayload response = getJaxbObjFromDomainObj(result, versionUtil.getApplicationVersion());
                responseXml = jaxbOtaBinder.marshallAsXmlResponse(getJaxbElement(jaxbOtaBinder.getObjectFactory(), response));
            } else {
                responseXml = createErrorResponse(errorMap);
            }
        } catch (Exception ex) {
            responseXml = processException(ex, bulkReadCompositeCriteria);
        }

        responseXml = versionSupportUtil.getVersionConvertedOutput(responseXml, requestReleaseVersion);

        LOG.info("BulkReadResponse = " + responseXml);
        StpThreadLocal.STPTHREADLOCAL.remove();
        return responseXml;
    }

    /**
     * @return the successMessage
     */
    @Override
    public ApplicationResults getSuccessMessage() {
        return successMessage;
    }

    /**
     * @param aSuccessMessage the successMessage to set
     */
    @Override
    public void setSuccessMessage(ApplicationResults aSuccessMessage) {
        successMessage = aSuccessMessage;
        successMessage.getSuccess().add(new ProblemInformation());
    }

    @Override
    public void setJaxbOtaBinder(JaxbOtaInterface aBinder) {
        jaxbOtaBinder = aBinder;

    }

    @Override
    public JaxbOtaInterface getJaxbOtaBinder() {
        return jaxbOtaBinder;
    }

    /**
     * @return the validator
     */
    public IValidator getValidator() {
        return validator;
    }

    /**
     * @param aValidator the validator to set
     */
    public void setValidator(IValidator aValidator) {
        validator = aValidator;
    }

    /**
     * getDomainObjFromJaxbObj
     * @param request
     * @param aErrorMap
     * @return IBulkComposite
     */
    protected abstract IBulkReadCompositeCriteria getDomainObjFromJaxbObj(Object request, Map<String, String> aErrorMap);

    /**
     * processException
     * @param ex the exception
     * @param policyComposite
     * @return String exception
     */
    protected String processException(Exception ex, IBulkReadCompositeCriteria policyComposite) {
        String responseXml = null;
        Map<String, String> errorMap = new HashMap<String, String>();
        LOG.error("Error processing BulkRead " + getProcessName(), ex);
        LOG.errorToEIAPI("Error executing " + getProcessName(), ex);
        if (ex instanceof ServiceException) {
            ServiceException exception = (ServiceException) ex;
            errorMap.put(exception.getErrorType().getCode(), exception.getMessage());
            responseXml = createErrorResponse(errorMap);
        } else {
            errorMap.put(getProcessError().getCode(), getProcessError().getDescription());
            responseXml = createErrorResponse(errorMap);
        }
        return responseXml;
    }

    private String createErrorResponse(Map<String, String> aErrorMap) {
        String version = versionUtil.getApplicationVersion();
        STLResponsePayload errorResponse = getResponseObject(version);
        errorResponse.setApplicationResults(XmlUtilityFunctions.buildXmlResponseErrors(aErrorMap));
        String response = jaxbOtaBinder.marshallXML(getJaxbElement(jaxbOtaBinder.getObjectFactory(), errorResponse), true);
        LOG.debug("ERROR RESPONSE: " + response);
        return response;
    }

    /**
     * getProcessName
     * @return process name
     */
    protected abstract String getProcessName();

    /**
     * Process.
     * @param request the request
     * @return the object
     */
    protected abstract Object process(IBulkReadCompositeCriteria request);

    /**
     * getProcessErrorCode
     * @return errorCode
     */
    protected abstract ErrorType getProcessError();

    /**
     * Gets the jaxb element.
     * @param aObjectFactory the object factory
     * @param aErrorResponse the error response
     * @return the jaxb element
     */
    protected abstract JAXBElement<?> getJaxbElement(ObjectFactory aObjectFactory, STLResponsePayload aErrorResponse);

    /**
     * Gets the response object.
     * @param version the version
     * @return the response object
     */
    protected abstract STLResponsePayload getResponseObject(String version);

    /**
     * Gets the jaxb obj from domain obj.
     * @param request the request
     * @param version version
     * @return the jaxb obj from domain obj
     */
    protected abstract STLResponsePayload getJaxbObjFromDomainObj(Object request, String version);

    /**
     * @return the pdeAdminService
     */
    public IPolicyAdminService getPdeAdminService() {
        return pdeAdminService;
    }

    /**
     * @param aPdeAdminService the pdeAdminService to set
     */
    public void setPdeAdminService(IPolicyAdminService aPdeAdminService) {
        pdeAdminService = aPdeAdminService;
    }

    /**
     * setVersionSupportUtil
     * @param aVersionUtil versionUtil
     */
    public void setVersionSupportUtil(ITravelPolicyVersionUtil aVersionUtil) {
        versionSupportUtil = aVersionUtil;
    }

    /**
     * getVersionSupportUtil
     * @return ITravelPolicyVersionUtil
     */
    public ITravelPolicyVersionUtil getVersionSupportUtil() {
        return versionSupportUtil;
    }

    /**
     * @return the versionUtil
     */
    public VersionUtil getVersionUtil() {
        return versionUtil;
    }

    /**
     * @param aVersionUtil the versionUtil to set
     */
    public void setVersionUtil(VersionUtil aVersionUtil) {
        this.versionUtil = aVersionUtil;
    }
}
