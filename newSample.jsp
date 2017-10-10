<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>


<sec:authorize access="hasRole('ROLE_TN_USER')">
    <fmt:setBundle basename="STP_StripesResources" var="BundleName" />
 </sec:authorize>
<sec:authorize access="!hasRole('ROLE_TN_USER')">
    <fmt:setBundle basename="StripesResources" var="BundleName" />
</sec:authorize>



<s:layout-render name="/WEB-INF/jsp/layout.jsp" title="New Rule">
    <s:layout-component name="js">
    <sec:authorize access="hasRole('ROLE_TN_USER')">
              <script defer="defer" src="${contextPath}/js/app/views/tn-condition-section-view.js"></script>
  </sec:authorize>
        <script type="text/javascript">
         var rule = {
            conditionSections: ${actionBean.conditionSectionJSON},
            conditions: ${actionBean.conditionJSON}
         };
      </script>
    </s:layout-component>
    <s:layout-component name="breadcrumb">
        <ul id="breadcrumb">
            <li><s:link beanclass="com.xyz.policy.ui.actions.PoliciesActionBean">
                    <fmt:message key="title.policies" />
                </s:link> &gt;</li>
            <li><s:link beanclass="com.xyz.policy.ui.actions.PolicyActionBean" event="edit">
                    <s:param name="policy.policyId" value="${actionBean.policy.policyId}" />
                    <c:out value="${actionBean.policy.name}" />
                </s:link> &gt;</li>
            <li><c:if test="${actionBean.rule.ruleId==0}">
                    <fmt:message key="title.rule.new" />
                </c:if> (<c:out value="${actionBean.template.name}" />)</li>
        </ul>

    </s:layout-component>
    <s:layout-component name="body">

        <div id="rule">
            <s:form id="rule-conditions" beanclass="com.xyz.policy.ui.actions.CustomRuleActionBean">
                <s:hidden name="policyVersion.id" />
                <s:hidden name="rule.ruleId" />
                <s:hidden name="rule.ruleOrderIndex" />
                <s:hidden name="template.id" />
                <s:hidden name="policy.policyId" />
                <p class="name-field-wrapper">
                    <span class="required">* </span>
                    <s:label for="rule-name" />
                    <input type="text" data-show-character-limit="true" data-first-focus="true" id="rule-name" class="name-field" name="rule.ruleName" value="${actionBean.rule.ruleName}"
                        maxlength="60" />
                </p>
                <div id="condition-sections" class="condition-sections form-block">
                    <h1>Rule</h1>
                    <c:choose>
                        <c:when test="${actionBean.template.type =='ACTION_RULE'}">
                            <h2>If:</h2>
                        </c:when>
                        <c:when test="${actionBean.template.type =='CALCULATING_RULE'}">
                            <c:choose>
                                <c:when test="${actionBean.template.id != 1259}">
                                    <h2>If:</h2>
                                </c:when>
                            </c:choose>
                        </c:when>
                    </c:choose>
                </div>
                <c:choose>
                    <c:when test="${actionBean.template.type =='ACTION_RULE'}">
                        <div id="system-behavior" class="form-block">
                            <h2>Then:</h2>
                                    <s:radio name="rule.addAViolationFlag" value="true" id="add-violation" checked=""/>
                                    <s:label for="add-violation"/>
                                    <p id="out-of-policy-violation">
                                        <s:label for="violation-violation"/>
                                        <s:select id="violation-violation" name="rule.violationId"
                                                  value="${actionBean.rule.violationId}">
                                            <s:option value="">Select a violation</s:option>
                                            <c:forEach var="violation" items="${actionBean.availableViolations}">
                                                <s:option value="${violation.id}">
                                                    <fmt:message bundle="${BundleName}"
                                                                 key="${violation.localizationKey}"/>
                                                </s:option>
                                            </c:forEach>
                                        </s:select>
                                    </p>
                                    <sec:authorize access="!hasRole('ROLE_TN_USER')">
                                        <p id="out-of-policy-behavior">
                                            <s:label for="violation-behavior"/>
                                            <s:select id="violation-behavior" name="rule.violationBehavior"
                                                      value="${actionBean.rule.violationBehavior}">
                                               <c:forEach var="behavior" items="${actionBean.availableBehaviors}">
                                                  <s:option value="${behavior.internalName}">
                                                     <fmt:message bundle="${BundleName}"
                                                                  key="${behavior.localizationKey}"/>
                                                  </s:option>
                                               </c:forEach>
                                            </s:select>
                                        </p>
                                    </sec:authorize>
                                    <p>
                                        <s:radio name="rule.addAViolationFlag" value="false" id="do-not-add-violation"
                                                 checked=""/>
                                        <s:label for="do-not-add-violation"/>
                                    </p>
                                    <c:if test="${actionBean.rule.assetType == 'AIR_CITY_PAIR_REQ' || actionBean.rule.assetType == 'SHOP_REQUEST'}">
                                       <h2>And/Or:</h2>
                                        <div class="notification-area form-block">
                                            <div class="notification-box">
                                                <s:checkbox id="notification" name="rule.addANotificationFlag" class="notification" checked="${actionBean.rule.addANotificationFlag}" />
                                                <s:label for="add-notification"/>
                                                <input type="text" name="rule.notificationName" value="${actionBean.rule.notificationName}"
                                                       id="add-notification"/>
                                            </div>
                                            <div class="notification-help">
                                                <fmt:message key="notification-help" />
                                            </div>
                                        </div>
                                    </c:if>
                        </div>
                    </c:when>
                    <c:when test="${actionBean.template.type =='CALCULATING_RULE'}">
                        <c:choose>
                            <c:when test="${actionBean.template.id != 1259}">
                                <div id="system-behavior" class="form-block">
                                    <h2>Then:</h2>
                                     <p>Itinerary is not to be considered when determining ideal itineraries.</p>
                                </div>
                            </c:when>
                        </c:choose>
                        <div class="hidden">
                            <input type="radio" name="rule.addAViolationFlag" value="false" id="do-not-add-violation" checked="true" />
                        </div>
                    </c:when>
                </c:choose>
                <div id="rule-applicability" class="form-block">
                    <c:choose>
                        <c:when test="${actionBean.template.type =='ACTION_RULE'}">
                                <h1>When to Apply this Rule</h1>
                                <p>
                                    <s:radio name="rule.alwaysApplicable" value="true" id="always-apply"/>
                                    <s:label for="always-apply"/>
                                </p>
                                <p>
                                    <s:radio name="rule.alwaysApplicable" value="false" id="apply-based-on-conditions"/>
                                    <s:label for="apply-based-on-conditions"/>
                                </p>
                                <div id="applicability-condition-sections" class="condition-sections"></div>
                        </c:when>
                    </c:choose>
                </div>
                <c:choose>
                    <c:when test="${actionBean.template.type =='CALCULATING_RULE'}">
                        <c:choose>
                            <c:when test="${actionBean.template.hasUnlessConditionSection}">
                                <div id="rule-unless" class="form-block">
                                    <h2>Unless:</h2>
                                    <div id="unless-condition-sections" class="condition-sections"></div>
                                </div>
                            </c:when>
                        </c:choose>
                    </c:when>
                </c:choose>
                <sec:authorize access="!hasRole('ROLE_TN_USER')">
                    <c:if test="${actionBean.template.type == 'ACTION_RULE'}">
                    <div class="form-block form-block-last">
                     <h1>Continue Evaluating ${rule.stopProcessingFlag}</h1>
                        <s:checkbox id="continue-evaluation" name="rule.stopProcessingFlag" checked="${rule.stopProcessingFlag}"/>
                        <s:label for="continue-evaluation"/>
                    </div>
                    </c:if>
                </sec:authorize>

                <div class="buttons">
                    
                    <sec:authorize access="!hasRole('ROLE_TN_USER')">
                        <s:submit name="update" class="button" />
                        <s:link class="button cancel" beanclass="com.xyz.policy.ui.actions.PolicyActionBean" event="edit">
                        <s:param name="policy.policyId" value="${actionBean.policy.policyId}" />Cancel</s:link>

                    </sec:authorize>
                    <sec:authorize access="hasRole('ROLE_TN_USER')">
                        <s:link class="button-secondary" beanclass="com.xyz.policy.ui.actions.PolicyActionBean" event="edit">
                        <s:param name="policy.policyId" value="${actionBean.policy.policyId}" />Cancel</s:link>
                        <s:submit name="update" class="ui-button-primary" />
                    </sec:authorize>
                    
                </div>
            </s:form>
        </div>
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/conditionSection.jsp" />
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/condition.jsp" />
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/variables.jsp" />
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/operators.jsp" />
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/valueWidgets.jsp" />
        <sec:authorize access="!hasRole('ROLE_TN_USER')">
            <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/optionsTemplate.jsp" />
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_TN_USER')">
            <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/optionsTemplate-TN.jsp" />
        </sec:authorize>
        <s:layout-render name="/WEB-INF/jsp/custom-rules/templates/units.jsp" />
         
 	</s:layout-component>
</s:layout-render>