package org.exampledriven.zuul;

import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ControllerHARequestHelper extends ProxyRequestHelper {
    private final ThreadLocal<InputStream> responseStream = new ThreadLocal<>();
    private final ThreadLocal<Map<String, List<String>>> headers = new ThreadLocal<>();
    private final SessionServerMapper sessionServerMapper;

    public ControllerHARequestHelper(SessionServerMapper sessionServerMapper) {
        this.sessionServerMapper = sessionServerMapper;
    }

    @Override
    public void setResponse(
            int status, InputStream responseStream, MultiValueMap<String, String> headers) throws IOException {
        this.responseStream.set(responseStream);
        sessionServerMapper.setResponseSession(parseSessionId(headers));
        headers.putAll(this.headers.get());
        super.setResponse(status, responseStream, headers);
    }

    public static void main(String[] args) {
        String cookies = "LEFT_PANEL_MAXIMIZEDAPP_CONFIGURATION=false; JSESSIONID=d378cdcde17a8df86f1b869a6b34; " +
                "X-CSRF-TOKEN=aba73181a367250d0f614e7e488f3d8453e531af; " +
                "visid_incap_910292=Wkn6GV7tTdW+AHkN4AYclBgXw1gAAAAAQUIPAAAAAADHZOrloXtAQoK8zlloK5Rw; " +
                "ajs_anonymous_id=%226078d07a-f168-461b-bf2d-3ff41e6f0976%22; visid_incap_960362=34qrjRcPQ1yrVRCsZS52Chq+71gAAAAAQUIPAAAAAABydxMIhzcU0kkDjuTMwIvO; colodin_id=389062977-4201438967-4273200709-appdynamics.com; colodin_orig_referrer=https%3A%2F%2Fwww.google.com%2F; visid_incap_980313=r770EArwQ7CnfokmgBVThaWrB1kAAAAAQUIPAAAAAACX1AbMtQKsSd3my2W0FPYc; visid_incap_989104=QFm/SELnT2+6lqWxK54RW05WQ1kAAAAAQUIPAAAAAABpwYMzG4Qe9UM/wRhJIt2f; optimizelySegments=%7B%22278501250%22%3A%22referral%22%2C%22278501251%22%3A%22gc%22%2C%22278506160%22%3A%22false%22%2C%227555162307%22%3A%22none%22%7D; optimizelyBuckets=%7B%7D; colodin_thank_you_page_referrer=; colodin_thank_you_page_url=https%3A%2F%2Fdocs.appdynamics.com%2Fdisplay%2FPRO43%2FGetting%2BStarted; visid_incap_1048744=KX6fDt3USfKSlox7RVk/mTBsyVkAAAAAQUIPAAAAAAD/8bFzsv+V6Dc1cuz0ZlBg; visid_incap_980327=zpZ6zgyuQOGAI+WTcu3Ffwk7zVkAAAAAQUIPAAAAAADRo17lDq0nUMQLup8gbwWM; adcustomer=3; _actmu=102226118.1337300303.1493329081856.1508349484273; _ga=GA1.2.1274958166.1489187603; uuid=7443a447-0548-483a-9fb0-08bf14c3fb2f; optimizelyEndUserId=oeu1489187602668r0.5961746963092582; ser_app=%7B%22original%22%3A%7B%22utm_campaign%22%3A%22website%22%2C%22utm_term%22%3A%22unknown%22%2C%22utm_content%22%3A%22unknown%22%2C%22utm_source%22%3A%22unknown%22%2C%22utm_medium%22%3A%22direct%22%2C%22utm_budget%22%3A%22none%22%2C%22landing_page%22%3A%22https%3A//login.appdynamics.com/sso/authenticate/%3Fsite%3Dselfservice%26target%3D/account/%22%7D%2C%22recent%22%3A%7B%22utm_campaign%22%3A%22https%3A//appdynamics.okta.com/app/appdynamicsincprod_portal_1/exk19slxd790IR5BS1d8/sso/saml%3Fsite%3Dzendesk%22%2C%22utm_term%22%3A%22unknown%22%2C%22utm_content%22%3A%22unknown%22%2C%22utm_source%22%3A%22appdynamics.okta.com%22%2C%22utm_medium%22%3A%22referral%22%2C%22utm_budget%22%3A%22none%22%2C%22landing_page%22%3A%22https%3A//login.appdynamics.com/sso/samlservice/loginProcess/zendesk/%22%7D%7D; hjd_ajax_Language2011=en; ajs_user_id=%227443a447-0548-483a-9fb0-08bf14c3fb2f%22; ajs_group_id=null; _mkto_trk=id:031-WIX-618&token:_mch-appdynamics.com-1489187602938-19164; db_annual_sales=49247000000; db_demandbase_sid=27707240; db_company_name=AppDynamics; db_employee_count=73700; db_industry=Communication%2C%20Media%20%26%20Services; db_country=US; db_city=San%20Francisco; db_state=CA; db_revenue_range=Over%20%245B; db_sub_industry=Services; db_primary_sic=7383; ei_client_id=5a0b8d479588790016a84aa4; mf_user=4e91277bd431fa25e45fdfbbe9cc46e8|; nlbi_910292=6BKDJHn9THaCFwveSNFkCgAAAACShYjhLOAVA+EqSMVYnGcE; incap_ses_442_910292=W6RscJErfSlAXyjGXk0iBk2XYVoAAAAA1fqEJbCbSsCpTsWcAUg1xg==; ADRUM=s=1516345327039&r=https%3A%2F%2Foa.saas.appdynamics.com%2Fcontroller%2F%3F-786357767";
        System.out.println(parseSessionId(cookies));
    }

    public static String parseSessionId(MultiValueMap<String, String> headers) {
        return parseSessionId(headers.getFirst("cookie"));
    }

    public static String parseSessionId(String cookies) {
        if (cookies == null) {
            return null;
        }
        System.out.println("----------------- " + cookies);
        String[] cookieArray = cookies.split(";");
        String prefix = "JSESSIONID=";
        for(String cookieString : cookieArray) {
            cookieString = cookieString.trim();
            if(cookieString.startsWith(prefix)) {
                return cookieString.substring(prefix.length());
            }
        }
        return null;
    }

    public void addHeader(String name, String... values) {
        if(headers.get() == null) headers.set(new HashMap<>());
        headers.get().compute(name, (key, valuesToAdd) -> {
            if (valuesToAdd == null) valuesToAdd = new ArrayList<>();
            for( String value : values)
                valuesToAdd.add(value);
            return valuesToAdd;
        });
    }

    public InputStream getResponseStream() {
        return responseStream.get();
    }
}
