 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

 

/**
 * The Class ExecutionServiceTest.
 */
public class ExecutionServiceTest {
    private ExecutionService executionService;
    private final JaxbOtaInterface jaxbOtaBinder = JaxbOtaMarshallerUnMarshaller.createInstance();
    private IPolicyExecutionComposite policyExecutionComposite;
    private TravelPolicyExecutionRQ travelPolicyExecutionRQ;
    private final ObjectFactory objectFactory = jaxbOtaBinder.getObjectFactory();
    @Mock
    private IPreferenceSort mockPreferenceSort;
    @Mock
    private IControlDataService controlDataService;
    @Mock
    private IVendorPreferenceService mockVendorPreferenceService;
    private IPreferenceComposite preferenceComposite;
    @Mock
    private PolicyExecutionTranslatorService mockPolicyExecutionTranslatorService;
    private Map<String, String> emptyErrorMap;
    private Map<String, String> emptyWarningMap;
    private GeographicData airportOriginInfo;
    private GeographicData airportDestinationInfo;

    /**
     * Inits the test
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        executionService = new ExecutionService();
        executionService.setControlDataService(controlDataService);
        executionService.setVendorPreferenceService(mockVendorPreferenceService);
        travelPolicyExecutionRQ = new TravelPolicyExecutionRQ();
        travelPolicyExecutionRQ = createNewRequest();
        policyExecutionComposite = createComposite();
        executionService.setPolicyExecutionTranslatorService(mockPolicyExecutionTranslatorService);
        emptyErrorMap = Collections.emptyMap();
        emptyWarningMap = Collections.emptyMap();

        airportOriginInfo = new GeographicData();
        airportOriginInfo.setCityCode("DFW");
        airportOriginInfo.setCountryCode("US");
        airportOriginInfo.setGeoCode("DFW");
        airportOriginInfo.setGeoTypeCode("APT");
        airportOriginInfo.setRegionCode("50");
        // airportOriginInfo.setREGIONCREATEPROFILEID(BigInteger.valueOf(Integer.valueOf("50")));

        airportDestinationInfo = new GeographicData();
        airportDestinationInfo.setCityCode("NYC");
        airportDestinationInfo.setCountryCode("US");
        airportDestinationInfo.setGeoCode("JFK");
        airportDestinationInfo.setGeoTypeCode("APT");
        airportDestinationInfo.setRegionCode("60");
        // airportDestinationInfo.setREGIONCREATEPROFILEID(BigInteger.valueOf(Integer.valueOf("60")));

        when(controlDataService.getAirportDetails("DFW")).thenReturn(airportOriginInfo);
        when(controlDataService.getAirportDetails("JFK")).thenReturn(airportDestinationInfo);
        AirExecutionServiceHelper instance = (AirExecutionServiceHelper) AirExecutionServiceHelper.getInstance();
        instance.setPreferenceSort(mockPreferenceSort);

    }

    /**
     * Gets the domain obj from jaxb obj test.
     */
    @Test
    public void getDomainObjFromJaxbObjTest() {
        Map<String, String> errorMap = new HashMap<String, String>();
        Map<String, String> aWarningMap = new HashMap<String, String>();
        IPolicyExecutionComposite policyExecutionCompositeResult = executionService.getDomainObjFromJaxbObj(travelPolicyExecutionRQ, errorMap, aWarningMap);
        assertNotNull(policyExecutionCompositeResult);
        assertSame(ServiceTestHelper.CLIENT_CODE, policyExecutionCompositeResult.getClientCode());
        assertSame(ServiceTestHelper.DOMAIN_ID, policyExecutionCompositeResult.getDomainId());
        assertSame(ServiceTestHelper.TRANSACTION_ID, policyExecutionCompositeResult.getTransactionId());
        assertSame("AVAIL", policyExecutionCompositeResult.getWorkflow().value());
        assertSame("WilesCO", policyExecutionCompositeResult.getTravelerProfile().getCompany());
        assertSame("COSTCENTER", policyExecutionCompositeResult.getTravelerProfile().getCostCenter());
        assertSame("DEPT", policyExecutionCompositeResult.getTravelerProfile().getDepartment());
        assertSame("DIVISION", policyExecutionCompositeResult.getTravelerProfile().getDivision());
        assertSame("EMPLOYEENBR", policyExecutionCompositeResult.getTravelerProfile().getEmployeeNbr());
        assertSame("PRJCODE", policyExecutionCompositeResult.getTravelerProfile().getProjectCode());
        assertSame("CORPORATEID", policyExecutionCompositeResult.getTravelerProfile().getCorporateID());
        assertSame(BigInteger.ONE, policyExecutionCompositeResult.getAirAssets().get(0).getSegments().get(0).getSegmentSeqNumber());

        assertSame(ServiceTestHelper.VERSION, policyExecutionCompositeResult.getVersion());

    }

    /**
     * Gets the domain obj from jaxb obj test.
     */
    @Test
    public void getDomainObjFromJaxbObjRelativeDaysTest() {
        Map<String, String> errorMap = new HashMap<String, String>();
        Map<String, String> aWarningMap = new HashMap<String, String>();
        travelPolicyExecutionRQ.getExecutionRequestSummary().getAirAssetRequest().get(0).getAirSegment().get(0).setArrivalDateRelativeArrival(1);
        travelPolicyExecutionRQ.getExecutionRequestSummary().getAirAssetRequest().get(0).getAirSegment().get(0).setDepartureDateRelativeDeparture(2);

        IPolicyExecutionComposite policyExecutionCompositeResult = executionService.getDomainObjFromJaxbObj(travelPolicyExecutionRQ, errorMap, aWarningMap);
        assertNotNull(policyExecutionCompositeResult);
        assertSame(ServiceTestHelper.CLIENT_CODE, policyExecutionCompositeResult.getClientCode());
        assertSame(ServiceTestHelper.DOMAIN_ID, policyExecutionCompositeResult.getDomainId());
        assertSame(ServiceTestHelper.TRANSACTION_ID, policyExecutionCompositeResult.getTransactionId());
        assertSame("AVAIL", policyExecutionCompositeResult.getWorkflow().value());
        assertSame("WilesCO", policyExecutionCompositeResult.getTravelerProfile().getCompany());
        assertSame("COSTCENTER", policyExecutionCompositeResult.getTravelerProfile().getCostCenter());
        assertSame("DEPT", policyExecutionCompositeResult.getTravelerProfile().getDepartment());
        assertSame("DIVISION", policyExecutionCompositeResult.getTravelerProfile().getDivision());
        assertSame("EMPLOYEENBR", policyExecutionCompositeResult.getTravelerProfile().getEmployeeNbr());
        assertSame("PRJCODE", policyExecutionCompositeResult.getTravelerProfile().getProjectCode());
        assertSame("CORPORATEID", policyExecutionCompositeResult.getTravelerProfile().getCorporateID());
        assertSame(BigInteger.ONE, policyExecutionCompositeResult.getAirAssets().get(0).getSegments().get(0).getSegmentSeqNumber());
        assertEquals(1, policyExecutionCompositeResult.getAirAssets().get(0).getSegments().get(0).getArrivalDayRelativeOrigin());
        assertEquals(2, policyExecutionCompositeResult.getAirAssets().get(0).getSegments().get(0).getDepartureDayRelativeOrigin());
        assertSame(ServiceTestHelper.VERSION, policyExecutionCompositeResult.getVersion());

    }

    /**
     * Gets the domain obj from jaxb obj no traveler test.
     */
    @Test
    public void getDomainObjFromJaxbObjNoTravelerTest() {
        Map<String, String> errorMap = new HashMap<String, String>();
        Map<String, String> aWarningMap = new HashMap<String, String>();
        travelPolicyExecutionRQ.getExecutionRequestSummary().setTraveler(null);
        IPolicyExecutionComposite policyExecutionCompositeResult = executionService.getDomainObjFromJaxbObj(travelPolicyExecutionRQ, errorMap, aWarningMap);
        assertNotNull(policyExecutionCompositeResult);
        assertSame(ServiceTestHelper.CLIENT_CODE, policyExecutionCompositeResult.getClientCode());
        assertSame(ServiceTestHelper.DOMAIN_ID, policyExecutionCompositeResult.getDomainId());
        assertSame(ServiceTestHelper.TRANSACTION_ID, policyExecutionCompositeResult.getTransactionId());
        assertSame("AVAIL", policyExecutionCompositeResult.getWorkflow().value());
        assertNull(policyExecutionCompositeResult.getTravelerProfile());
        assertSame(BigInteger.ONE, policyExecutionCompositeResult.getAirAssets().get(0).getSegments().get(0).getSegmentSeqNumber());

        assertSame(ServiceTestHelper.VERSION, policyExecutionCompositeResult.getVersion());

    }

    /**
     * Test get jaxb obj from domain obj error.
     */
    @Test
    public void testGetJaxbObjFromDomainObjError() {
        try {
            executionService.getJaxbObjFromDomainObj(null, null, null, null);
        } catch (ServiceException e) {
            assertSame(ServiceErrorType.POLICY_EXECUTION_ERROR, e.getErrorType());
        }

    }

    /**
     * testGetJaxbObjFromDomainPolicyInd
     */
    @Test
    public void testGetJaxbObjFromDomainPolicyPreferenceInd() {
        policyExecutionComposite.getAirAssets().get(0).setCorporatePolicyViolations(new HashMap<String, String>());
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, emptyErrorMap, emptyErrorMap, ServiceTestHelper.VERSION);
        assertNotNull(response);
        assertEquals(com.xyz.stl.travelpolicy.v2.PreferenceExecutionLevel.FIFTH_MOST_PREFERRED, response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelAgency());
        assertEquals(com.xyz.stl.travelpolicy.v2.PreferenceExecutionLevel.NEUTRAL, response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelCorporate());
        assertTrue(response.getExecutionResponseSummary().getAirAssetResponse().get(0).isInPolicyInd());

    }

    /**
     * testGetJaxbObjFromDomainPolicyPreferenceIndWithViolations
     */
    @Test
    public void testGetJaxbObjFromDomainPolicyPreferenceIndWithViolations() {
        policyExecutionComposite.getAirAssets().get(0).getSegments().get(0).setPreferenceLevelAgency(null);
        policyExecutionComposite.getAirAssets().get(0).getSegments().get(0).setPreferenceLevelCorp(null);
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, emptyErrorMap, emptyErrorMap, ServiceTestHelper.VERSION);
        assertNotNull(response);
        assertNull(response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelAgency());
        assertNull(response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelCorporate());
        assertFalse(response.getExecutionResponseSummary().getAirAssetResponse().get(0).isInPolicyInd());
        assertEquals("123", response.getExecutionResponseSummary().getAirAssetResponse().get(0).getViolations().get(0).getViolationCode());
        assertEquals("Error", response.getExecutionResponseSummary().getAirAssetResponse().get(0).getViolations().get(0).getViolationMsg());

    }

    /**
     * testGetJaxbObjFromDomainPolicyPreferenceIndEndTranSact
     */
    @Test
    public void testGetJaxbObjFromDomainPolicyPreferenceIndEndTranSact() {
        policyExecutionComposite.setWorkflow(ExecutionWorkflowType.ENDTRANSACT);
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, emptyErrorMap, emptyErrorMap, ServiceTestHelper.VERSION);
        assertNotNull(response);
        assertTrue(response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().isEmpty());
        assertFalse(response.getExecutionResponseSummary().getAirAssetResponse().get(0).isInPolicyInd());
        assertEquals("123", response.getExecutionResponseSummary().getAirAssetResponse().get(0).getViolations().get(0).getViolationCode());
        assertEquals("Error", response.getExecutionResponseSummary().getAirAssetResponse().get(0).getViolations().get(0).getViolationMsg());

    }

    /**
     * testGetJaxbObjFromDomainPolicyPreferenceIndForSell
     */
    @Test
    public void testGetJaxbObjFromDomainPolicyPreferenceIndForSell() {
        policyExecutionComposite.getAirAssets().get(0).setCorporatePolicyViolations(new HashMap<String, String>());
        policyExecutionComposite.setWorkflow(ExecutionWorkflowType.SELL);
        policyExecutionComposite.getAirAssets().get(0).setCorporatePolicyViolations(new HashMap<String, String>());
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, emptyErrorMap, emptyErrorMap, ServiceTestHelper.VERSION);
        assertNotNull(response);
        assertEquals(com.xyz.stl.travelpolicy.v2.PreferenceExecutionLevel.FIFTH_MOST_PREFERRED, response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelAgency());
        assertEquals(com.xyz.stl.travelpolicy.v2.PreferenceExecutionLevel.NEUTRAL, response.getExecutionResponseSummary().getAirAssetResponse().get(0).getAirSegmentResponse().get(0).getPreferenceLevelCorporate());
        assertTrue(response.getExecutionResponseSummary().getAirAssetResponse().get(0).isInPolicyInd());

    }

    /**
     * Test get jaxb obj from domain.
     */
    @Test
    public void testGetJaxbObjFromDomainWarningsNoErrors() {
        Map<String, String> warningMap = new HashMap<String, String>();
        warningMap.put("1", "2");
        Map<String, String> errorMap = new HashMap<String, String>();
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, errorMap, warningMap, ServiceTestHelper.VERSION);
        ProblemInformation problemInformation = response.getApplicationResults().getWarning().get(0);
        assertSame("1", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());
        assertSame(0, response.getApplicationResults().getError().size());
    }

    /**
     * Test get jaxb obj from domain.
     */
    @Test
    public void testGetJaxbObjFromDomainWarningsNoWarnings() {
        Map<String, String> warningMap = new HashMap<String, String>();
        warningMap.put("1", "2");
        Map<String, String> errorMap = new HashMap<String, String>();
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, warningMap, errorMap, ServiceTestHelper.VERSION);
        ProblemInformation problemInformation = response.getApplicationResults().getError().get(0);
        assertSame("1", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());
        assertSame(0, response.getApplicationResults().getWarning().size());
    }

    /**
     * Test get jaxb obj from domain.
     */
    @Test
    public void testGetJaxbObjFromDomainWarningsNoErrorsForSell() {
        Map<String, String> warningMap = new HashMap<String, String>();
        warningMap.put("1", "2");
        Map<String, String> errorMap = new HashMap<String, String>();
        policyExecutionComposite.setWorkflow(ExecutionWorkflowType.SELL);
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, errorMap, warningMap, ServiceTestHelper.VERSION);
        ProblemInformation problemInformation = response.getApplicationResults().getWarning().get(0);
        assertSame("1", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());
        assertSame(0, response.getApplicationResults().getError().size());
    }

    /**
     * Test get jaxb obj from domain.
     */
    @Test
    public void testGetJaxbObjFromDomainWarningsAndErrors() {
        Map<String, String> warningMap = new HashMap<String, String>();
        warningMap.put("1", "2");
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("3", "4");
        Map<String, ArrayList<String>> warningMapForPreference = new HashMap<String, ArrayList<String>>();
        warningMapForPreference.put("1", new ArrayList<String>());
        warningMapForPreference.get("1").add("2");
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, errorMap, warningMap, ServiceTestHelper.VERSION);
        ProblemInformation problemInformation = response.getApplicationResults().getWarning().get(0);
        assertSame("1", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());

        problemInformation = response.getApplicationResults().getError().get(0);
        assertSame("3", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());
    }

    /**
     * Test get jaxb obj from domain.
     */
    @Test
    public void testGetJaxbObjFromDomainWarningsAndErrorsForSell() {
        Map<String, String> warningMap = new HashMap<String, String>();
        warningMap.put("1", "2");
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("3", "4");
        policyExecutionComposite.setWorkflow(ExecutionWorkflowType.SELL);
        Map<String, ArrayList<String>> warningMapForPreference = new HashMap<String, ArrayList<String>>();
        warningMapForPreference.put("1", new ArrayList<String>());
        warningMapForPreference.get("1").add("2");
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getJaxbObjFromDomainObj(policyExecutionComposite, errorMap, warningMap, ServiceTestHelper.VERSION);
        ProblemInformation problemInformation = response.getApplicationResults().getWarning().get(0);
        assertSame("1", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());

        problemInformation = response.getApplicationResults().getError().get(0);
        assertSame("3", problemInformation.getSystemSpecificResults().get(0).getMessage().get(0).getCode());
    }

    /**
     * Test process.
     */
    @Test
    public void testProcess() {
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionComposite);
        IPreferenceComposite preferenceCompositeEmpty = new PreferenceCompositeVO();
        IPreference vendorPref = new PolicyPreferenceModel();
        preferenceCompositeEmpty.setVendorPreference(vendorPref);
        Mockito.when(mockVendorPreferenceService.readPreference(Mockito.any(BigDecimal.class), Mockito.any(MarketPrefType.class))).thenReturn(preferenceCompositeEmpty);
        assertNotNull(executionService.process(policyExecutionComposite, emptyErrorMap, emptyWarningMap));

    }

    /**
     * Test testProcessWithValidationError.
     */
    @Test
    public void testProcessWithValidationError() {
        // Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionComposite);
        IPreferenceComposite preferenceCompositeEmpty = new PreferenceCompositeVO();
        IPreference vendorPref = new PolicyPreferenceModel();
        preferenceCompositeEmpty.setVendorPreference(vendorPref);
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put(ServiceErrorType.INVALID_CLIENT_CODE.getCode(), ServiceErrorType.INVALID_CLIENT_CODE.getDescription());
        Mockito.when(mockVendorPreferenceService.readPreference(Mockito.any(BigDecimal.class), Mockito.any(MarketPrefType.class))).thenReturn(preferenceCompositeEmpty);
        IPolicyExecutionComposite result = (IPolicyExecutionComposite) executionService.process(policyExecutionComposite, errorMap, emptyWarningMap);
        assertNotNull(result);
        assertEquals(ExecutionService.POLICY_NOT_EXECUTED, result.getCollector().getPolicyName());
        Mockito.verifyZeroInteractions(mockPolicyExecutionTranslatorService);

    }

    /**
     * Test process.
     */
    @Test
    public void testProcessNoPrefs() {
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionComposite);
        IPreferenceComposite preferenceCompositeEmpty = new PreferenceCompositeVO();
        IPreference vendorPref = new PolicyPreferenceModel();
        preferenceCompositeEmpty.setVendorPreference(vendorPref);
        policyExecutionComposite.setPreferenceAgency(null);
        policyExecutionComposite.setPreferenceCorp(null);
        Mockito.when(mockVendorPreferenceService.readPreference(Mockito.any(BigDecimal.class), Mockito.any(MarketPrefType.class))).thenReturn(preferenceCompositeEmpty);
        assertNotNull(executionService.process(policyExecutionComposite, emptyErrorMap, emptyWarningMap));

    }

    /**
     * Test process.
     */
    @Test
    public void testProcessNoPolicy() {
        IPolicyExecutionComposite policyExecutionCompositeNoPolicy = policyExecutionComposite;

        DefaultPolicyEvaluationCollector collector = new DefaultPolicyEvaluationCollector();
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("PolicyName", "No valid policy id found");
        collector.addAll(items);
        policyExecutionCompositeNoPolicy.setCollector(collector);
        Map<String, String> localMap = new HashMap<String, String>();
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionCompositeNoPolicy);
        executionService.process(policyExecutionCompositeNoPolicy, localMap, new HashMap<String, String>());
        assertTrue(localMap.containsKey(ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getCode()));
    }

    /**
     * Test process.
     */
    @Test
    public void testProcessNoPolicyInvalidError() {
        IPolicyExecutionComposite policyExecutionCompositeNoPolicy = policyExecutionComposite;

        DefaultPolicyEvaluationCollector collector = new DefaultPolicyEvaluationCollector();
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("PolicyName", "Duplicate name");
        collector.addAll(items);
        policyExecutionCompositeNoPolicy.setCollector(collector);
        Map<String, String> localMap = new HashMap<String, String>();
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionCompositeNoPolicy);
        executionService.process(policyExecutionCompositeNoPolicy, localMap, new HashMap<String, String>());
        assertFalse(localMap.containsKey(ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getCode()));
    }

    /**
     * Test process no agy pref found.
     */
    @Test
    public void testProcessNoPrefFound() {
        IPolicyExecutionComposite policyExecutionCompositeNoPolicy = policyExecutionComposite;

        DefaultPolicyEvaluationCollector collector = new DefaultPolicyEvaluationCollector();
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("PolicyName", "No valid policy id found");
        collector.addAll(items);
        policyExecutionCompositeNoPolicy.setCollector(collector);
        Map<String, String> localErrorMap = new HashMap<String, String>();
        Map<String, String> localWarningMap = new HashMap<String, String>();
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionCompositeNoPolicy);
        executionService.process(policyExecutionCompositeNoPolicy, localErrorMap, localWarningMap);
        assertTrue(localErrorMap.containsKey(ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getCode()));
        assertTrue(localWarningMap.containsKey(ServiceErrorType.POLICY_EXECUTION_AGY_PREFERENCEID_INVALID.getCode()));
        assertTrue(localWarningMap.containsKey(ServiceErrorType.POLICY_EXECUTION_CRP_PREFERENCEID_INVALID.getCode()));
    }

    /**
     * Test process.
     */
    @Test
    public void testProcessNoPolicyNoPref() {
        IPolicyExecutionComposite policyExecutionCompositeNoPolicy = policyExecutionComposite;

        DefaultPolicyEvaluationCollector collector = new DefaultPolicyEvaluationCollector();
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("PolicyName", "No valid policy id found");
        collector.addAll(items);
        policyExecutionCompositeNoPolicy.setCollector(collector);
        Map<String, String> localMap = new HashMap<String, String>();
        Mockito.when(mockPolicyExecutionTranslatorService.translatePolicy(Mockito.any(IPolicyExecutionComposite.class))).thenReturn(policyExecutionCompositeNoPolicy);

        policyExecutionCompositeNoPolicy.getPreferenceAgency().setPreferenceId(0);
        policyExecutionCompositeNoPolicy.getPreferenceCorp().setPreferenceId(0);

        executionService.process(policyExecutionCompositeNoPolicy, localMap, emptyWarningMap);
        assertTrue(localMap.containsKey(ServiceErrorType.POLICY_EXECUTION_POLICYID_INVALID.getCode()));
    }

    /**
     * Test get process name.
     */
    @Test
    public void testGetProcessName() {
        assertEquals(ApplicationConstants.POLICY_EXECUTION, executionService.getProcessName());
    }

    /**
     * Test get process error.
     */
    @Test
    public void testGetProcessError() {
        assertEquals(ServiceErrorType.POLICY_EXECUTION_ERROR, executionService.getProcessError());
    }

    /**
     * Test get response object.
     */
    @Test
    public void testGetResponseObject() {
        TravelPolicyExecutionRS response = (TravelPolicyExecutionRS) executionService.getResponseObject("1.0");
        assertSame("1.0", response.getVersion());
    }

    /**
     * Gets the jaxb element test.
     */
    @Test
    public void getJaxbElementTest() {
        TravelPolicyExecutionRS travelPolicyExecutionRS = new TravelPolicyExecutionRS();
        assertEquals(objectFactory.createTravelPolicyExecutionRS(travelPolicyExecutionRS).getValue().getClass(), executionService.getJaxbElement(objectFactory, travelPolicyExecutionRS).getValue().getClass());

    }

    /**
     * Gets the control data lookup util test.
     */
    @Test
    public void getControlDataLookupUtilTest() {
        executionService.setControlDataService(controlDataService);
    }

    /**
     * Map preference levels test.
     */
    @Test
    public void mapPreferenceLevelsTest() {
        IPreferenceComposite prefComposite = createPreferenceComposite();
        List<Integer> result[] = new ArrayList[1];
        List<Integer> list = new ArrayList<Integer>();
        list.add(0);
        result[0] = list;
        when(mockPreferenceSort.mapPreferencePriority(prefComposite)).thenReturn(result);
        // executionService.setPreferenceSort(mockPreferenceSort);
        IPolicyExecutionComposite policyExecutionComp = createComposite("DFW", "JFK", "AA");
        // executionService.mapPreferenceLevels(policyExecutionComp, prefComposite, ProfileTypes.AGY);
        AirExecutionServiceHelper.getInstance().mapPreferenceLevels(policyExecutionComp, prefComposite, ProfileTypes.AGY, controlDataService);

        assertEquals(PreferenceLevelType.THIRD_MOST_PREFERRED, policyExecutionComp.getAirAssets().get(0).getSegments().get(0).getPreferenceLevelAgency());

    }

    /**
     * Map preference levels test.
     */
    @Test
    public void mapPreferenceLevelsNoAssetsTest() {

        IPolicyExecutionComposite policyExecutionComp = createComposite("DFW", "JFK", "AA");
        policyExecutionComp.getAirAssets().remove(0);
        // executionService.mapPreferenceLevels(policyExecutionComp, createPreferenceComposite(), ProfileTypes.AGY);
        AirExecutionServiceHelper.getInstance().mapPreferenceLevels(policyExecutionComp, createPreferenceComposite(), ProfileTypes.AGY, controlDataService);

    }

    /**
     * Map preference levels test.
     */
    @Test
    public void mapPreferenceLevelsNoSegmentsTest() {

        IPolicyExecutionComposite policyExecutionComp = createComposite("DFW", "JFK", "AA");
        policyExecutionComp.getAirAssets().get(0).getSegments().remove(0);
        // executionService.mapPreferenceLevels(policyExecutionComp, createPreferenceComposite(), ProfileTypes.AGY);
        AirExecutionServiceHelper.getInstance().mapPreferenceLevels(policyExecutionComp, createPreferenceComposite(), ProfileTypes.AGY, controlDataService);

    }

    /**
     * Map preference levels test with exception.
     */
    @Test
    public void mapPreferenceLevelsTestWithException() {
        // executionService.mapPreferenceLevels(policyExecutionComposite, preferenceComposite, ProfileTypes.AGY);
        AirExecutionServiceHelper.getInstance().mapPreferenceLevels(policyExecutionComposite, preferenceComposite, ProfileTypes.AGY, controlDataService);

    }

    /**
     * Sets the policy execution translator service test.
     */
    @Test
    public void setPolicyExecutionTranslatorServiceTest() {
        executionService.setPolicyExecutionTranslatorService(mockPolicyExecutionTranslatorService);
    }

    private TravelPolicyExecutionRQ createNewRequest() {
        TravelPolicyExecutionRQ request = new TravelPolicyExecutionRQ();
        request.setVersion(ServiceTestHelper.VERSION);
        ExecutionRequest executionRequest = new ExecutionRequest();
        executionRequest.setClientCode(ServiceTestHelper.CLIENT_CODE);
        executionRequest.setDomainID(ServiceTestHelper.DOMAIN_ID);
        executionRequest.setPolicyRequestID(ServiceTestHelper.POLICY_ID);
        executionRequest.setTransactionID(ServiceTestHelper.TRANSACTION_ID);

        Traveler traveler = new Traveler();
        traveler.setCompany("WilesCO");
        traveler.setCorporateID("123");
        traveler.setCostCenter("COSTCENTER");
        traveler.setDepartment("DEPT");
        traveler.setDivision("DIVISION");
        traveler.setEmployeeNbr("EMPLOYEENBR");
        traveler.setProjectCode("PRJCODE");
        traveler.setCorporateID("CORPORATEID");

        executionRequest.setTraveler(traveler);

        AirAssetRequest airAssetRequest = new AirAssetRequest();
        airAssetRequest.setAssetID("321");
        airAssetRequest.setDepartureDate(null);

        com.xyz.stl.travelpolicy.v2.AirSegment airSegment = new com.xyz.stl.travelpolicy.v2.AirSegment();
        airSegment.setArrival("arrival");
        airSegment.setBookingClass("class1");
        airSegment.setDeparture("depart");
        airSegment.setMarketingCarrier("AA");
        airSegment.setSegmentSequenceNumber(BigInteger.ONE);
        airAssetRequest.getAirSegment().add(airSegment);
        executionRequest.getAirAssetRequest().add(airAssetRequest);

        executionRequest.setWorkflow(com.xyz.stl.travelpolicy.v2.ExecutionWorkflowType.AVAIL);
        request.setExecutionRequestSummary(executionRequest);

        return request;
    }

    private IPolicyExecutionComposite createComposite(String origin, String destination, String carrier) {
        IPolicyExecutionComposite policyExecutionComp = new PolicyExecutionComposite();
        policyExecutionComp.setClientCode(ServiceTestHelper.CLIENT_CODE);
        policyExecutionComp.setDomainId(ServiceTestHelper.DOMAIN_ID);
        policyExecutionComp.setPolicyId(ServiceTestHelper.CLIENT_CODE);
        IPreferenceExecution agency = new PreferenceExecution();
        agency.setPreferenceId(Integer.valueOf("123"));
        policyExecutionComp.setPreferenceAgency(agency);

        IPreferenceExecution corp = new PreferenceExecution();
        corp.setPreferenceId(Integer.valueOf("666"));
        policyExecutionComp.setPreferenceCorp(corp);
        PolicyEvaluationCollector policyEvalCollector = new DefaultPolicyEvaluationCollector();
        policyExecutionComp.setCollector(policyEvalCollector);

        IAsset airAsset = new AirAsset(airportOriginInfo, airportDestinationInfo);
        ISegment segment = new AirSegment();
        segment.setOriginAirport(origin);
        segment.setDestinationAirport(destination);
        segment.setCarrier(carrier);
        airAsset.getSegments().add(segment);

        policyExecutionComp.setWorkflow(ExecutionWorkflowType.AVAIL);
        policyExecutionComp.getAirAssets().add(airAsset);
        return policyExecutionComp;
    }

    private IPolicyExecutionComposite createComposite() {
        IPolicyExecutionComposite policyExecutionComp = new PolicyExecutionComposite();
        policyExecutionComp.setClientCode(ServiceTestHelper.CLIENT_CODE);
        policyExecutionComp.setDomainId(ServiceTestHelper.DOMAIN_ID);
        policyExecutionComp.setPolicyId(ServiceTestHelper.CLIENT_CODE);
        IPreferenceExecution agency = new PreferenceExecution();
        agency.setPreferenceId(Integer.valueOf("123"));
        policyExecutionComp.setPreferenceAgency(agency);

        IPreferenceExecution corp = new PreferenceExecution();
        corp.setPreferenceId(Integer.valueOf("666"));
        policyExecutionComp.setPreferenceCorp(corp);
        PolicyEvaluationCollector policyEvalCollector = new DefaultPolicyEvaluationCollector();
        policyExecutionComp.setCollector(policyEvalCollector);

        IAsset airAsset = new AirAsset(airportOriginInfo, airportDestinationInfo);
        ISegment segment = new AirSegment();
        segment.setOriginAirport("DFW");
        segment.setDestinationAirport("AUS");
        airAsset.getCorporatePolicyViolations().put("123", "Error");
        airAsset.getSegments().add(segment);

        segment.setPreferenceLevelAgency(PreferenceLevelType.FIFTH_MOST_PREFERRED);
        segment.setPreferenceLevelCorp(PreferenceLevelType.NEUTRAL);
        policyExecutionComp.setWorkflow(ExecutionWorkflowType.AVAIL);
        policyExecutionComp.getAirAssets().add(airAsset);
        return policyExecutionComp;
    }

    private IPreferenceComposite createPreferenceComposite() {
        IPreferenceComposite prefComposite = new PreferenceCompositeVO();
        prefComposite.setVersion(ServiceTestHelper.VERSION);
        IPreference preference = new PolicyPreferenceModel();
        List<IPolicyPrefMarketing> policyPrefMarketingList = new ArrayList<IPolicyPrefMarketing>();
        IPolicyPrefMarketing policyPreferenceMarketingModel = new PolicyPreferenceMarketingModel();
        policyPreferenceMarketingModel.setOriginValue("DFW");
        policyPreferenceMarketingModel.setDestinationValue("JFK");
        policyPreferenceMarketingModel.setBiDirectionalInd("N");
        policyPreferenceMarketingModel.setDestinationMktCategoryCd(AirServiceUtil.AIRPORT_MKT_CAT_CD);
        policyPreferenceMarketingModel.setOriginMktCategoryCd(AirServiceUtil.AIRPORT_MKT_CAT_CD);

        List<IPolicyPrefSpl> policyPrfList = new ArrayList<IPolicyPrefSpl>();
        IPolicyPrefSpl policyPrefSplModel = new PolicyPreferenceSupplierModel();
        policyPrefSplModel.setLevelNumber(3);
        policyPrefSplModel.setOrderSequenceNumber(0);
        policyPrefSplModel.setPolicyPrefMarketingId(BigDecimal.ONE);
        policyPrefSplModel.setSupplierContextId(BigDecimal.TEN);
        policyPrefSplModel.setVendorcode("AA");
        policyPrfList.add(policyPrefSplModel);

        policyPrefSplModel = new PolicyPreferenceSupplierModel();

        policyPrefSplModel.setLevelNumber(2);
        policyPrefSplModel.setVendorcode("CO");
        policyPrefSplModel.setOrderSequenceNumber(1);
        policyPrfList.add(policyPrefSplModel);

        policyPreferenceMarketingModel.setPolicyPreferences(policyPrfList);
        policyPrefMarketingList.add(policyPreferenceMarketingModel);

        prefComposite.setAirlinePolicyPrefMarketing(policyPrefMarketingList);
        prefComposite.setVendorPreference(preference);

        return prefComposite;
    }
}
