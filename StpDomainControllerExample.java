 

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.ValidationEvent;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.Property;

 

/**
 * DomainController
 */
public abstract class StpDomainController implements IStpDomainController {

    protected static final String SUCCESS_TIME = "STP Time";
    protected static final String LIBERTY = "LIB1";
    protected static final String NEWLINE = System.getProperty("line.separator");

    private static final LoggerUtil LOG = LoggerUtil.getLogger(StpDomainController.class.getName());
    private static final LoggerUtil METRICSLOG = LoggerUtil.getLogger(EnterpriseLogging.class.getName());
    private static final String VERSION_XML_NAME = "version=";
    private Object errorResponseObject;

    private JaxbOtaInterface jaxbOtaBinder;

    private Metrics metricsLogger;
    private IAuthorizationAuthentication securityManager;
    private VersionUtil versionUtil;

    /**
     * @param value value
     */
    @Override
    public void setErrorResponseObject(Object value) {
        this.errorResponseObject = value;
    }

    /**
     * getErrorResponseObject
     * @return Object o
     */
    public synchronized Object getErrorResponseObject() {
        return errorResponseObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJaxbOtaBinder(JaxbOtaInterface aJaxbBinder) {
        this.jaxbOtaBinder = aJaxbBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxbOtaInterface getJaxbOtaBinder() {
        return this.jaxbOtaBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetricsLogger(Metrics metrics) {
        this.metricsLogger = metrics;
    }

    /**
     * getMetricsLogger
     * @return metricsLogger
     */
    protected Metrics getMetricsLogger() {
        return metricsLogger;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSecurityManager(IAuthorizationAuthentication aSecurityManager) {
        this.securityManager = aSecurityManager;
    }

    /**
     * isBranchAccessEnabled
     * @return boolean
     */
    public boolean isBranchAccessEnabled() {
        return true;
    }

    @Override
    public Object processRequest(@Body Object body, @Headers Map<Object, Object> headers, @Property(Exchange.CHARSET_NAME) String set) {
        Object result = null;
        String chSet = set == null ? "UTF-8" : set;
        String methodName = null;
        ISecurityResult securityResult = null;
        TransactionLogRecord transactionRec = null;
        long startT = System.currentTimeMillis();

        try {
            StpThreadLocal.STPTHREADLOCAL.set(headers.get(ApplicationConstants.STP_MESSAGE_ID));
            LOG.info(" Incoming MESSAGE = " + (String) body);
            methodName = getMethodName();
            transactionRec = metricsLogger.serviceReceiveRequest(null, methodName, body.toString().length());

            LOG.debug("HEADERS = " + headers.toString() + " CharacterSet = " + chSet);
            securityResult = securityManager.authorizeAuthenticate(headers);
            headers.put(ISecurityResult.SECURITY_RESULT_KEY, securityResult);

            List<ValidationEvent> validationErrors = new ArrayList<ValidationEvent>();
            Object validatedObject = jaxbOtaBinder.unMarshallXmlRequestWithValidation((String) body, validationErrors);
            if (hasRequestSchemaErrors(validationErrors)) {
                result = createXMLWithSchemaValidationErrors(validationErrors, validatedObject);
            } else {
                LOG.debug("Checking domainID for " + getDomainId(validatedObject));

                if (isBranchAccessEnabled() && !"GT".equals(getClientCode(validatedObject))) {
                    securityManager.branchAccess(getDomainId(validatedObject), securityResult);
                }
                result = process(validatedObject);
            }
        } catch (Exception e) {
            LOG.error("Error in DC " + e.getMessage(), e);
            result = processException(e, body);
        }

        long endT = System.currentTimeMillis();

        if (transactionRec != null) {
            transactionRec.setKeyValuePair(SUCCESS_TIME, (int) (endT - startT));
            metricsLogger.updateServiceItem(null, methodName, SUCCESS_TIME, endT - startT, Level.INFO);
            metricsLogger.serviceTransmitReply(null, methodName, result.toString().length(), transactionRec);
            METRICSLOG.info("processRequest DC " + (endT - startT) + " ms : " + methodName);
        }

        StpThreadLocal.STPTHREADLOCAL.remove();
        return result;
    }

    protected Object processException(Exception ex, Object request) {
        Object result = request;

        try {
            if (ex instanceof ServiceException) {
                ServiceException serviceException = (ServiceException) ex;
                if (serviceException.getErrorType() == ServiceErrorType.AUTHORIZATION_FAILED_ERROR) {
                    result = createNoAuthorizationResponse(request);
                } else if (serviceException.getErrorType() == ServiceErrorType.AUTHENTICATION_FAILED_ERROR || serviceException.getErrorType() == ServiceErrorType.AUTHENTICATION_ROLES_ERROR) {
                    result = createNoAuthenticationResponse(request);
                } else if (serviceException.getErrorType() == ServiceErrorType.JAXB_UNMARSHALLING_ERROR) {
                    result = createInvalidSchemaResponse(request, ServiceErrorType.JAXB_UNMARSHALLING_ERROR);
                } else if (serviceException.getErrorType() == ServiceErrorType.JAXB_MARSHALLING_ERROR) {
                    result = createInvalidSchemaResponse(request, ServiceErrorType.JAXB_MARSHALLING_ERROR);
                } else if (serviceException.getErrorType() == ServiceErrorType.BRANCH_ACCESS_FAILED_ERROR) {
                    result = createNoBranchAccessResponse(request);
                } else {
                    result = createServiceError(getErrorCode(), request);
                }
            } else if (ex instanceof Exception) {
                result = createServiceError(getErrorCode(), request);
            }
        } catch (Exception exp) {
            try {
                result = createServiceError(getErrorCode(), request);
            } catch (Exception e) {
                LOG.error("Error generating error response ", e);
                result = "Application Error, error generating response";
            }
        }

        return result;
    }

    protected boolean hasRequestSchemaErrors(List<ValidationEvent> validationErrors) {
        if (validationErrors.size() > 0) {
            LOG.debug("REquest schema has validation errors = " + validationErrors.size());

            return true;
        } else {
            return false;
        }
    }

    protected Object createNoAuthorizationResponse(Object request) {
        List<String> errorMessageList = new ArrayList<String>();
        errorMessageList.add(ServiceErrorType.AUTHORIZATION_FAILED_ERROR.getDescription());
        return generateErrorResponse(ServiceErrorType.AUTHORIZATION_FAILED_ERROR.getCode(), errorMessageList, request);
    }

    protected Object createNoBranchAccessResponse(Object request) {
        List<String> errorMessageList = new ArrayList<String>();
        errorMessageList.add(ServiceErrorType.BRANCH_ACCESS_FAILED_ERROR.getDescription());
        return generateErrorResponse(ServiceErrorType.BRANCH_ACCESS_FAILED_ERROR.getCode(), errorMessageList, request);
    }

    protected Object createNoAuthenticationResponse(Object request) {
        List<String> errorMessageList = new ArrayList<String>();
        errorMessageList.add(ServiceErrorType.AUTHENTICATION_FAILED_ERROR.getDescription());
        return generateErrorResponse(ServiceErrorType.AUTHENTICATION_FAILED_ERROR.getCode(), errorMessageList, request);
    }

    protected Object createServiceError(String aErrorCode, Object request) {
        List<String> errorMessageList = new ArrayList<String>();
        ServiceErrorType error = ServiceErrorType.getByErrorCode(aErrorCode);
        errorMessageList.add(error.getDescription());
        return generateErrorResponse(error.getCode(), errorMessageList, request);
    }

    protected Object createInvalidSchemaResponse(Object request, ErrorType errorType) {
        List<String> errorMessageList = new ArrayList<String>();
        errorMessageList.add(errorType.getDescription());
        return generateErrorResponse(errorType.getCode(), errorMessageList, request);
    }

    protected String createXMLWithSchemaValidationErrors(List<ValidationEvent> validationErrors, Object request) {
        String responseXML = null;
        List<String> errorMessageList = new ArrayList<String>();

        for (ValidationEvent validationEvent : validationErrors) {
            LOG.debug("Adding schema validation error = " + validationEvent.getMessage());
            errorMessageList.add(validationEvent.getMessage());
        }

        responseXML = generateErrorResponse(ServiceErrorType.SCHEMA_VALIDATION_ERROR.getCode(), errorMessageList, request);
        LOG.errorToEIAPI("Schema Validation Error " + errorMessageList.toString());

        return responseXML;
    }

    protected abstract String getDomainId(Object request);

    protected Object process(Object request) {
        LOG.debug("Processing for request = " + request.toString());
        return request;
    }

    // TODO This should be the common method to create errors for Pref and Policy. Have them separate for now, until Preference is moved to STP
    protected STLResponsePayload createPolicyErrorResponse(String errorCode, List<String> errorMessageList, Object aRequest) {
        STLResponsePayload errorResponse = (STLResponsePayload) getErrorResponseObject();

        ApplicationResults respMessage = errorResponse.getApplicationResults();
        respMessage.setStatus(CompletionCodes.COMPLETE);
        respMessage.getError().clear();
        for (String errorDesc : errorMessageList) {
            ProblemInformation problem = new ProblemInformation();
            SystemSpecificResults result = new SystemSpecificResults();
            MessageCondition message = new MessageCondition();
            message.setCode(errorCode);
            message.setValue(errorDesc);
            result.getMessage().add(message);
            problem.getSystemSpecificResults().add(result);
            respMessage.getError().add(problem);
        }

        // always return latest application version no matter what
        errorResponse.setVersion(versionUtil.getApplicationVersion());
        return errorResponse;
    }

    protected String generateErrorResponse(String errorCode, List<String> errorMessageList, Object request) {
        STLResponsePayload errorResponse = createPolicyErrorResponse(errorCode, errorMessageList, request);
        return getJaxbOtaBinder().marshallXML(getJaxbElementError(errorResponse), true);
    }

    protected abstract JAXBElement<?> getJaxbElementError(STLResponsePayload errorResponse);

    protected abstract String getErrorCode();

    protected abstract String getClientCode(Object request);

    /*
     * http://techteams2.xyz.com/EnterpriseServices/Infrastructure/Security_Systems/ESSM/ESSM%20API/Wiki/Wiki%20Pages/Mertrics%20Logging.aspx
     */
    protected String getMethodName() {
        StringBuffer methodName = new StringBuffer();
        int inx = this.getClass().getName().lastIndexOf('.');
        methodName.append("STP:");
        methodName.append(EnterpriseLoggingGrouping.getModuleNameForMetrics(this.getClass().getName()));
        methodName.append(":");
        methodName.append(this.getClass().getName().substring(inx + 1));
        methodName.append(":process");
        return methodName.toString();
    }

}
