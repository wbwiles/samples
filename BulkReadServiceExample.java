

import java.io.File;
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
 * The Class BulkReadService.
 */
public class BulkReadService extends StpDomainController {
    private static String libertyJunitFileName = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "TravelPolicyBulkReadRS.xml";
    private static final LoggerUtil METRICSLOG = LoggerUtil.getLogger(EnterpriseLogging.class.getName());
    private static final LoggerUtil LOG = LoggerUtil.getLogger(BulkReadService.class.getName());
    private static final String LIBERTY_FILE_NAME = "etc" + File.separator + "stp" + File.separator + "TravelPolicyBulkReadRS.xml";

    private ITravelPolicyVersionUtil versionSupportUtil;

    @Override
    public Object processRequest(@Body Object body, @Headers Map<Object, Object> headers, @Property(Exchange.CHARSET_NAME) String set) {
        Object result = null;
        String methodName = getMethodName();
        TransactionLogRecord transactionRec = null;
        long startT = System.currentTimeMillis();
        String inputRequest = null;
        String requestReleaseVersion = ApplicationConstants.DEFAULT_RELEASE_VERSION;
        try {
            inputRequest = (String) body;
            inputRequest = versionSupportUtil.getVersionConvertedInput(inputRequest);
            LOG.info("BulkRead output = " + inputRequest + " methodName = " + methodName);

            transactionRec = getMetricsLogger().serviceReceiveRequest(null, methodName, inputRequest.length());

            List<ValidationEvent> validationErrors = new ArrayList<ValidationEvent>();
            Object validatedObject = getJaxbOtaBinder().unMarshallXmlRequestWithValidation(inputRequest, validationErrors);
            requestReleaseVersion = ((TravelPolicyBulkReadRQ) validatedObject).getVersion();

            if (!hasRequestSchemaErrors(validationErrors)) {
                if (getDomainId(validatedObject).equals(LIBERTY)) {
                    return TravelPolicyUtilityFunctions.getFixedResponseForTest(LIBERTY_FILE_NAME, libertyJunitFileName);
                }
                result = process(validatedObject);
            } else {
                result = createXMLWithSchemaValidationErrors(validationErrors, validatedObject);
            }
        } catch (Exception e) {
            result = processException(e, body);
        }

        long endT = System.currentTimeMillis();

        if (transactionRec != null) {
            transactionRec.setKeyValuePair(SUCCESS_TIME, (int) (endT - startT));
            getMetricsLogger().updateServiceItem(null, methodName, SUCCESS_TIME, endT - startT, Level.FATAL);
            METRICSLOG.info((endT - startT) + " ms : DC-" + methodName);
        }

        if (result instanceof String) {
            result = versionSupportUtil.getVersionConvertedOutput((String) result, requestReleaseVersion);
        }

        getMetricsLogger().serviceTransmitReply(null, methodName, result.toString().length(), transactionRec);

        return result;

    }

    @Override
    protected String generateErrorResponse(String errorCode, List<String> errorMessageList, Object request) {
        STLResponsePayload errorResponse = createPolicyErrorResponse(errorCode, errorMessageList, request);
        return getJaxbOtaBinder().marshallXML(getJaxbElementError(errorResponse), true);
    }

    @Override
    protected String getClientCode(Object aRequest) {
        TravelPolicyBulkReadRQ request = (TravelPolicyBulkReadRQ) aRequest;
        return request.getBulkReadRequestSummary().getClientCode();
    }

    @Override
    protected String getDomainId(Object aInput) {
        TravelPolicyBulkReadRQ request = (TravelPolicyBulkReadRQ) aInput;
        return request.getBulkReadRequestSummary().getDomainID();
    }

    @Override
    protected String getErrorCode() {
        return ServiceErrorType.POLICY_EXECUTION_BULK_READ_ERROR.getCode();
    }

    @Override
    protected JAXBElement<?> getJaxbElementError(STLResponsePayload aErrorResponse) {
        return getJaxbOtaBinder().getObjectFactory().createTravelPolicyBulkReadRS((TravelPolicyBulkReadRS) aErrorResponse);
    }

    @Override
    public synchronized Object getErrorResponseObject() {
        TravelPolicyBulkReadRS response = new TravelPolicyBulkReadRS();
        response.setApplicationResults(new ApplicationResults());
        return response;
    }

    /**
     * @param aLibertyJunitFileName the libertyJunitFileName to set
     */
    public static void setLibertyJunitFileName(String aLibertyJunitFileName) {
        libertyJunitFileName = aLibertyJunitFileName;
    }

    /**
     * setVersionSupportUtil
     * @param versionUtil versionUtil
     */
    public void setVersionSupportUtil(ITravelPolicyVersionUtil versionUtil) {
        versionSupportUtil = versionUtil;
    }
}
